package com.example.oitzh.myapplication;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.amazonaws.mobileconnectors.iot.AWSIotMqttNewMessageCallback;
import com.amazonaws.mobileconnectors.iot.AWSIotMqttQos;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlacePicker;

import org.json.JSONException;
import org.json.JSONObject;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;
import java.util.Timer;
import java.util.TimerTask;

import static com.example.oitzh.myapplication.Aws.LOG_TAG;


@Root
public class MainActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 0;
    final int MY_CHILD_ACTIVITY = 1;
    final int PLACE_PICKER_REQUEST = 2;
    final int CURRENT_PLACE_PICKER_REQUEST = 3;
    Aws aws;
    ListView listView;
    EditText editTextView;
    Stack<AlertDialog> stack;
    @ElementList
    ArrayList<Scenario> itemScenarioList;
    CustomAdapter customAdapter;
    TextView tvStatus;
    ArrayList<Scenario> itemScenarioListXml;
    HashMap<Object, List<String>> sensorsInfo = new HashMap<>();
    private FusedLocationProviderClient mFusedLocationClient;
    Timer timer;
    TimerTask clearSensorsInfoTask;
    TimerTask publishAlert;
    HashMap<String,Long> scenariosWithAlerts = new HashMap<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        listView = (ListView) findViewById(R.id.listview);
        editTextView = (EditText) findViewById(R.id.editTextView);
        stack = new Stack<>();

        itemScenarioList = new ArrayList<Scenario>();

        //read from XML file
        Serializer serializer = new Persister();
        File source = new File(this.getFilesDir(), "scenario.xml");
        try {
            this.itemScenarioList = serializer.read(MainActivity.class, source).itemScenarioList;
        } catch (Exception e) {
            e.printStackTrace();
        }

        customAdapter = new CustomAdapter(getApplicationContext(), itemScenarioList, this);
        listView.setEmptyView(findViewById(android.R.id.empty));
        listView.setAdapter(customAdapter);


        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // The id of the channel.
        String id = "my_channel_01";
        // The user-visible name of the channel.
        CharSequence name = getString(R.string.channel_name);
        // The user-visible description of the channel.
        String description = getString(R.string.channel_description);
        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel mChannel = new NotificationChannel(id, name, importance);
        // Configure the notification channel.
        mChannel.setDescription(description);
        mChannel.enableLights(true);
        // Sets the notification light color for notifications posted to this
        // channel, if the device supports this feature.
        mChannel.setLightColor(Color.RED);
        mChannel.enableVibration(true);
        mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
        mNotificationManager.createNotificationChannel(mChannel);

        //Periodically clear sensors info to verify updated data
        timer = new Timer();
        clearSensorsInfoTask = new TimerTask() {
            @Override
            public void run() {
                sensorsInfo.clear();
            }
        };
        timer.schedule(clearSensorsInfoTask,30*1000);

        AWSIotMqttNewMessageCallback awsIotMqttNewMessageCallback = new AWSIotMqttNewMessageCallback() {
            @Override
            public void onMessageArrived(final String topic, final byte[] data) {
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String message = new String(data, "UTF-8");
                            Log.d(LOG_TAG, "Message arrived:");
                            Log.d(LOG_TAG, "   Topic: " + topic);
                            Log.d(LOG_TAG, " Message: " + message);

                            try {
                                JSONObject jsonObject = new JSONObject(message);
                                Iterator<String> keys = jsonObject.keys();
                                while (keys.hasNext()) {
                                    String stringKey = keys.next();
                                    Object key = ScenarioInput.Name.stringToInputName(stringKey) != null ? (ScenarioInput.Name) ScenarioInput.Name.stringToInputName(stringKey) : (ScenarioAction.Name) ScenarioAction.Name.stringToActionName(stringKey);
                                    String value = (String) jsonObject.get(stringKey);
                                    if (!MainActivity.this.sensorsInfo.containsKey(key)) {
                                        MainActivity.this.sensorsInfo.put(key, new ArrayList<>());
                                    }
                                    if (!MainActivity.this.sensorsInfo.get(key).contains(value)) {
                                        MainActivity.this.sensorsInfo.get(key).add(value);
                                        Log.d(LOG_TAG, key.toString());
                                        Log.d(LOG_TAG, value.toString());
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            /*
                            // 1. Instantiate an AlertDialog.Builder with its constructor
                            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

                            // 2. Chain together various setter methods to set the dialog characteristics
                            builder.setMessage("Topic: " + topic + "\n" + "Message: " + message)
                                    .setTitle("Message arrived:");

                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // User clicked OK button
                                }
                            });

                            builder.setIcon(R.drawable.ic_warning_black_24px);

                            // 3. Get the AlertDialog from create()
                            AlertDialog dialog = builder.create();
                            dialog.show();
                            */
                        } catch (UnsupportedEncodingException e) {
                            Log.e(LOG_TAG, "Message encoding error.", e);
                        }
                    }
                });
            }
        };

        AWSIotMqttNewMessageCallback alertsSensorCallback = new AWSIotMqttNewMessageCallback() {
            @Override
            public void onMessageArrived(final String topic, final byte[] data) {
                try {
                    String message = new String(data, "UTF-8");
                    Log.d(LOG_TAG, "Message arrived:");
                    Log.d(LOG_TAG, "   Topic: " + topic);
                    Log.d(LOG_TAG, " Message: " + message);


                    JSONObject jsonObject = new JSONObject(message);
                    Iterator<String> keys = jsonObject.keys();
                    while (keys.hasNext()) {
                        String stringKey = keys.next();
                        if (stringKey.equals("alert")) {
                            String value = (String) jsonObject.get(stringKey);
                            String scenarioName = (String) jsonObject.get(keys.next());
                            String scenarioCondition = (String) jsonObject.get(keys.next());
                            if (!scenariosWithAlerts.containsKey(scenarioName) || (scenariosWithAlerts.containsKey(scenarioName) && (System.currentTimeMillis() - scenariosWithAlerts.get(scenarioName))> 60*1000) ) {

                                // The id of the channel.
                                String CHANNEL_ID = "my_channel_01";
                                NotificationCompat.Builder mBuilder =
                                        new NotificationCompat.Builder(MainActivity.this, CHANNEL_ID)
                                                .setSmallIcon(R.drawable.ic_warning_black_24px)
                                                .setContentTitle("Alert")
                                                .setContentText(scenarioCondition);
                                // Creates an explicit intent for an Activity in your app
                                Intent resultIntent = new Intent(MainActivity.this, MainActivity.class);

                                // The stack builder object will contain an artificial back stack for the
                                // started Activity.
                                // This ensures that navigating backward from the Activity leads out of
                                // your app to the Home screen.
                                TaskStackBuilder stackBuilder = TaskStackBuilder.create(MainActivity.this);
                                // Adds the back stack for the Intent (but not the Intent itself)
                                stackBuilder.addParentStack(MainActivity.class);
                                // Adds the Intent that starts the Activity to the top of the stack
                                stackBuilder.addNextIntent(resultIntent);
                                PendingIntent resultPendingIntent =
                                        stackBuilder.getPendingIntent(
                                                0,
                                                PendingIntent.FLAG_UPDATE_CURRENT
                                        );
                                mBuilder.setContentIntent(resultPendingIntent);
                                NotificationManager mNotificationManager =
                                        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                                // mNotificationId is a unique integer your app uses to identify the
                                // notification. For example, to cancel the notification, you can pass its ID
                                // number to NotificationManager.cancel().
                                mNotificationManager.notify(1, mBuilder.build());

                                scenariosWithAlerts.put(scenarioName, System.currentTimeMillis());

                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    Log.e(LOG_TAG, "Message encoding error.", e);
                }
            }
        };

        // tvStatus = (TextView) findViewById(R.id.tvStatus);

        aws = new Aws(MainActivity.this);
        aws.initialize();
        aws.connect();
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        aws.subscribe("sensors/info", AWSIotMqttQos.QOS0, awsIotMqttNewMessageCallback);
        aws.subscribe("actions", AWSIotMqttQos.QOS0, alertsSensorCallback);

        //publish alert device info
        JSONObject obj = new JSONObject();
        try {
            obj.put("alert", "alert");
            String payLoad = obj.toString();
            aws.publish(payLoad, "sensors/info");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new Thread() {
            @Override
            public void run() {
                try {
                    LocationMock locationMock = new LocationMock(mFusedLocationClient, MainActivity.this);
                    while (true) {
                        locationMock.getLastLocation(MainActivity.this);
                        try {
                            Thread.sleep(5 * 1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                }
            }

        }.start();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

                    builder.setMessage("Application must have location permission enabled to work properly. Please enable location permission.")
                            .setTitle("Location Permission Issue:");

                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            finish();
                            System.exit(0);
                        }
                    });

                    builder.setIcon(R.drawable.ic_warning_black_24px);

                    AlertDialog dialog = builder.create();
                    dialog.show();

                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    //Call an activity for creating a scenario
    public void createActivity(View v) {
        Intent i = new Intent(this, NameActivity.class);
        i.putExtra("sensorsInfo", sensorsInfo);
        startActivityForResult(i, MY_CHILD_ACTIVITY);
    }

    //Call an activity for editing existing a scenario
    public void editActivity(View v, Scenario editedScenario) {
        Intent i = new Intent(this, NameActivity.class);
        //Scenario editedScenario = itemScenarioList.get(itemScenarioList.size()-1);
        i.putExtra("edit", editedScenario);
        i.putExtra("sensorsInfo", sensorsInfo);
        startActivityForResult(i, MY_CHILD_ACTIVITY);
    }

    public void createPlacePickerActivity(View v) throws GooglePlayServicesNotAvailableException, GooglePlayServicesRepairableException {
        try {
            Intent intent =
                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                            .build(this);
            startActivityForResult(intent, PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException e) {
            // TODO: Handle the error.
        } catch (GooglePlayServicesNotAvailableException e) {
            // TODO: Handle the error.
        }
    }

    public void createPlacePickerCurrentActivity(View v) throws GooglePlayServicesNotAvailableException, GooglePlayServicesRepairableException {
        try {
            Intent intent =
                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                            .build(this);
            startActivityForResult(intent, CURRENT_PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException e) {
            // TODO: Handle the error.
        } catch (GooglePlayServicesNotAvailableException e) {
            // TODO: Handle the error.
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case (MY_CHILD_ACTIVITY): {
                if (resultCode == Activity.RESULT_OK) {
                    Scenario scenarioCreated = (Scenario) data.getSerializableExtra("result");
                    process(scenarioCreated);
                    //publishScenario(scenarioCreated); //publish to aws server!
                    itemScenarioList.add(new Scenario(scenarioCreated));
                    customAdapter.notifyDataSetChanged();

                    Serializer serializer = new Persister();
                    File result = new File(this.getFilesDir(), "scenario.xml");
                    try {
                        serializer.write(this, result);
                        //FileInputStream fileInputStream = openFileInput("scenario.xml");
                        //byte[] fileByte = new byte[(int) result.length()];
                        //fileInputStream.read(fileByte);
                        //String str = new String(fileByte, StandardCharsets.UTF_8);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            }
            case (PLACE_PICKER_REQUEST): {
                if (resultCode == RESULT_OK) {
                    Place place = PlacePicker.getPlace(data, this);
                    LocationMock.home = place;
                    Button button = (Button) findViewById(R.id.setHomeButton);
                    button.setText("Change Home");
                }
                break;
            }
/*
            case (CURRENT_PLACE_PICKER_REQUEST): {
                if (resultCode == RESULT_OK) {
                    Place place = PlacePicker.getPlace(data, this);
                    LocationMock.currentPlace = place;
                    Button button = (Button) findViewById(R.id.setCurrentPlaceButton);
                    button.setText("Change Current Place");
                }
                float[] distance = new float[3];
                Location.distanceBetween(LocationMock.currentPlace.getLatLng().latitude, LocationMock.currentPlace.getLatLng().longitude, LocationMock.home.getLatLng().latitude, LocationMock.home.getLatLng().longitude, distance);
                float distanceCurrentFromHome = distance[0];
                if (Math.round(distanceCurrentFromHome) < 50) {
                    this.publishLocation(ScenarioInput.Trigger.Location.When_Arriving);
                } else {
                    this.publishLocation(ScenarioInput.Trigger.Location.When_Leaving);
                }

                break;
            }
*/
        }
    }

    private void process(Scenario scenarioCreated) {
        ScenarioInput scenarioInput = scenarioCreated.scenarioInput;
        ScenarioAction scenarioAction = scenarioCreated.scenarioAction;
        JSONObject obj = new JSONObject();
        JSONObject scenarioObj = new JSONObject();
        JSONObject conditionsObj = new JSONObject();
        JSONObject actionsObj = new JSONObject();

        try {
            for (ScenarioInput.Name name : scenarioInput.names) {
                switch (name) {
                    case Location:
                        conditionsObj.put(scenarioInput.sensorsInfo.get(name), "=" + "'" + scenarioInput.trigger.location.toString().toLowerCase() + "'");
                        break;
                    case Time:
                        conditionsObj.put(scenarioInput.sensorsInfo.get(name), ScenarioInput.Trigger.Time.toSqlCondition(scenarioInput.trigger.time, scenarioInput.sensorsInfo.get(name)));
                        break;
                    case Climate:
                        String value = scenarioInput.trigger.climate.toString().replace("_", "").replaceFirst("Above", ">").replace("Below", "<").replace("Degrees", String.valueOf(scenarioInput.trigger.climate.first)).toLowerCase();
                        conditionsObj.put(scenarioInput.sensorsInfo.get(name), value);
                        break;
                    case Motion:
                        conditionsObj.put(scenarioInput.sensorsInfo.get(name), "=" + "'" + scenarioInput.trigger.motion.toString().toLowerCase() + "'");
                        break;
                }
            }
            for (ScenarioAction.Name name : scenarioAction.names) {
                switch (name) {
                    case Ac:
                        actionsObj.put(scenarioAction.sensorsInfo.get(name), scenarioAction.action.ac.name().toLowerCase());
                        break;
                    case Tv:
                        actionsObj.put(scenarioAction.sensorsInfo.get(name), scenarioAction.action.tv.name().toLowerCase());
                        break;
                    case Light:
                        actionsObj.put(scenarioAction.sensorsInfo.get(name), scenarioAction.action.light.name().toLowerCase());
                        break;
                    case Alert:
                        actionsObj.put(scenarioAction.sensorsInfo.get(name), scenarioAction.action.alert.name().toLowerCase());
                        break;
                    case Boiler:
                        actionsObj.put(scenarioAction.sensorsInfo.get(name), scenarioAction.action.boiler.name().toLowerCase());
                        break;
                }
            }
            obj.put(scenarioCreated.getScenarioName(), scenarioObj.put("conditions", conditionsObj).put("actions", actionsObj));
            String payLoad = obj.toString();

            final String topic = "scenario/create";
            aws.publish(payLoad, topic);

        } catch (JSONException e) {
            System.out.println(e.getMessage());
        }

        //Validating  there is no duplicate scenario names
        int appearingCounter = 0;
        for (Scenario scenario : itemScenarioList) {
            if (scenario.getScenarioName().split("[(]")[0].equals(scenarioCreated.getScenarioName())) {
                appearingCounter++;
            }
        }
        if (appearingCounter > 0) {
            scenarioCreated.setScenarioName(scenarioCreated.getScenarioName() + "(" + String.valueOf(appearingCounter) + ")");
        }
    }

    public void publishScenario(Scenario publishedScenario) {
        final String topic = "AWS/Scenario";
        final String msg = publishedScenario.getScenarioName();

        aws.publish(msg, topic);
    }

    public void publishRemovedScenario(Scenario removedScenario) {
        final String topic = "scenario/remove";
        final String name = removedScenario.getScenarioName();

        JSONObject obj = new JSONObject();
        try {
            obj.put("scenario_name", name);
            String payLoad = obj.toString();
            aws.publish(payLoad, topic);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    //endregion
}

