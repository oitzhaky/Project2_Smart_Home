package com.example.oitzh.myapplication;


import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.iot.AWSIotKeystoreHelper;
import com.amazonaws.mobileconnectors.iot.AWSIotMqttClientStatusCallback;
import com.amazonaws.mobileconnectors.iot.AWSIotMqttLastWillAndTestament;
import com.amazonaws.mobileconnectors.iot.AWSIotMqttManager;
import com.amazonaws.mobileconnectors.iot.AWSIotMqttNewMessageCallback;
import com.amazonaws.mobileconnectors.iot.AWSIotMqttQos;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.iot.AWSIotClient;
import com.amazonaws.services.iot.model.AttachPrincipalPolicyRequest;
import com.amazonaws.services.iot.model.CreateKeysAndCertificateRequest;
import com.amazonaws.services.iot.model.CreateKeysAndCertificateResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlacePicker;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    static final String LOG_TAG = MainActivity.class.getCanonicalName();
    //region AWS Variables
    // IoT endpoint
    // AWS Iot CLI describe-endpoint call returns: XXXXXXXXXX.iot.<region>.amazonaws.com
    private static final String CUSTOMER_SPECIFIC_ENDPOINT = "a3n7np6iiz4al5.iot.us-east-1.amazonaws.com";
    // Cognito pool ID. For this app, pool needs to be unauthenticated pool with
    // AWS IoT permissions.
    private static final String COGNITO_POOL_ID = "us-east-1:8890e6b3-58c6-4ea5-929d-e0566f7f20c9";
    // Name of the AWS IoT policy to attach to a newly created certificate
    private static final String AWS_IOT_POLICY_NAME = "espJava";
    // Region of AWS IoT
    private static final Regions MY_REGION = Regions.US_EAST_1;
    // Filename of KeyStore file on the filesystem
    private static final String KEYSTORE_NAME = "iot_keystore";
    // Password for the private key in the KeyStore
    private static final String KEYSTORE_PASSWORD = "password";
    // --- Constants to modify per your configuration ---
    // Certificate and key aliases in the KeyStore
    private static final String CERTIFICATE_ID = "default";
    final String AC_TOPIC = "AWS/AC";
    final String TV_TOPIC = "AWS/TV";
    final String LIGHTS_TOPIC = "AWS/LIGHTS";
    final String BOILER_TOPIC = "AWS/BOILER";
    final String SECURITY_TOPIC = "AWS/SECURITY";
    final int MY_CHILD_ACTIVITY = 1;
    final int PLACE_PICKER_REQUEST = 2;
    AWSIotClient mIotAndroidClient;
    AWSIotMqttManager mqttManager;
    String clientId;
    String keystorePath;
    String keystoreName;
    String keystorePassword;
    KeyStore clientKeyStore = null;
    String certificateId;
    CognitoCachingCredentialsProvider credentialsProvider;
    ListView listView;
    EditText editTextView;
    ArrayList<Scenario> itemScenarioList;
    CustomAdapter customAdapter;
    EditText txtSubcribe;
    EditText txtTopic;
    EditText txtMessage;
    TextView tvLastMessage;
    TextView tvClientId;
    TextView tvStatus;
    Button btnConnect;
    Button btnSubscribe;
    Button btnPublish;
    Button btnDisconnect;
    //region AWS CONNECT Listener Code
    View.OnClickListener connectClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            Log.d(LOG_TAG, "clientId = " + clientId);

            try {
                mqttManager.connect(clientKeyStore, new AWSIotMqttClientStatusCallback() {
                    @Override
                    public void onStatusChanged(final AWSIotMqttClientStatus status,
                                                final Throwable throwable) {
                        Log.d(LOG_TAG, "Status = " + String.valueOf(status));

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (status == AWSIotMqttClientStatus.Connecting) {
                                    tvStatus.setText("Connecting...");

                                } else if (status == AWSIotMqttClientStatus.Connected) {
                                    tvStatus.setText("Connected");

                                } else if (status == AWSIotMqttClientStatus.Reconnecting) {
                                    if (throwable != null) {
                                        Log.e(LOG_TAG, "Connection error.", throwable);
                                    }
                                    tvStatus.setText("Reconnecting");
                                } else if (status == AWSIotMqttClientStatus.ConnectionLost) {
                                    if (throwable != null) {
                                        Log.e(LOG_TAG, "Connection error.", throwable);
                                    }
                                    tvStatus.setText("Disconnected");
                                } else {
                                    tvStatus.setText("Disconnected");

                                }
                            }
                        });
                    }
                });
            } catch (final Exception e) {
                Log.e(LOG_TAG, "Connection error.", e);
                tvStatus.setText("Error! " + e.getMessage());
            }
        }
    };


    //endregion
    private FusedLocationProviderClient mFusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        listView = (ListView) findViewById(R.id.listview);
        editTextView = (EditText) findViewById(R.id.editTextView);
        itemScenarioList = new ArrayList<Scenario>();
        customAdapter = new CustomAdapter(getApplicationContext(), itemScenarioList, this);
        listView.setEmptyView(findViewById(android.R.id.empty));
        listView.setAdapter(customAdapter);


        LocationMock locationMock = null;
        try {
            locationMock = new LocationMock(mFusedLocationClient, this);
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        }
        locationMock.getLastLocation(this);

        //region AWS Code

        //txtSubcribe = (EditText) findViewById(R.id.editTextSubscribe);
        //txtTopic = (EditText) findViewById(R.id.editTextPublish);
        //txtMessage = (EditText) findViewById(R.id.editTextMessage);

        //tvLastMessage = (TextView) findViewById(R.id.tvLastMessage);
        //tvClientId = (TextView) findViewById(R.id.tvClientId);
        tvStatus = (TextView) findViewById(R.id.tvStatus);

        // btnConnect = (Button) findViewById(R.id.btnConnect);
        //btnConnect.setOnClickListener(connectClick);
        //btnConnect.setEnabled(false);

        // btnSubscribe = (Button) findViewById(R.id.btnSubscribe);
        //btnSubscribe.setOnClickListener(subscribeClick);

        //btnPublish = (Button) findViewById(R.id.btnPublish);
        //btnPublish.setOnClickListener(publishClick);

        //btnDisconnect = (Button) findViewById(R.id.btnDisconnect);
        //btnDisconnect.setOnClickListener(disconnectClick);

        // MQTT client IDs are required to be unique per AWS IoT account.
        // This UUID is "practically unique" but does not _guarantee_
        // uniqueness.
        clientId = UUID.randomUUID().toString();
        //tvClientId.setText(clientId);

        // Initialize the AWS Cognito credentials provider
        credentialsProvider = new CognitoCachingCredentialsProvider(
                getApplicationContext(), // context
                COGNITO_POOL_ID, // Identity Pool ID
                MY_REGION // Region
        );

        Region region = Region.getRegion(MY_REGION);

        // MQTT Client
        mqttManager = new AWSIotMqttManager(clientId, CUSTOMER_SPECIFIC_ENDPOINT);

        // Set keepalive to 10 seconds.  Will recognize disconnects more quickly but will also send
        // MQTT pings every 10 seconds.
        mqttManager.setKeepAlive(10);

        // Set Last Will and Testament for MQTT.  On an unclean disconnect (loss of connection)
        // AWS IoT will publish this message to alert other clients.
        AWSIotMqttLastWillAndTestament lwt = new AWSIotMqttLastWillAndTestament("my/lwt/topic",
                "Android client lost connection", AWSIotMqttQos.QOS0);
        mqttManager.setMqttLastWillAndTestament(lwt);

        // IoT Client (for creation of certificate if needed)
        mIotAndroidClient = new AWSIotClient(credentialsProvider);
        mIotAndroidClient.setRegion(region);

        keystorePath = getFilesDir().getPath();
        keystoreName = KEYSTORE_NAME;
        keystorePassword = KEYSTORE_PASSWORD;
        certificateId = CERTIFICATE_ID;

        // To load cert/key from keystore on filesystem
        try {
            if (AWSIotKeystoreHelper.isKeystorePresent(keystorePath, keystoreName)) {
                if (AWSIotKeystoreHelper.keystoreContainsAlias(certificateId, keystorePath,
                        keystoreName, keystorePassword)) {
                    Log.i(LOG_TAG, "Certificate " + certificateId
                            + " found in keystore - using for MQTT.");
                    // load keystore from file into memory to pass on connection
                    clientKeyStore = AWSIotKeystoreHelper.getIotKeystore(certificateId,
                            keystorePath, keystoreName, keystorePassword);
                    //btnConnect.setEnabled(true);
                } else {
                    Log.i(LOG_TAG, "Key/cert " + certificateId + " not found in keystore.");
                }
            } else {
                Log.i(LOG_TAG, "Keystore " + keystorePath + "/" + keystoreName + " not found.");
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, "An error occurred retrieving cert/key from keystore.", e);
        }

        if (clientKeyStore == null) {
            Log.i(LOG_TAG, "Cert/key was not found in keystore - creating new key and certificate.");

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        // Create a new private key and certificate. This call
                        // creates both on the server and returns them to the
                        // device.
                        CreateKeysAndCertificateRequest createKeysAndCertificateRequest =
                                new CreateKeysAndCertificateRequest();
                        createKeysAndCertificateRequest.setSetAsActive(true);
                        final CreateKeysAndCertificateResult createKeysAndCertificateResult;
                        createKeysAndCertificateResult =
                                mIotAndroidClient.createKeysAndCertificate(createKeysAndCertificateRequest);
                        Log.i(LOG_TAG,
                                "Cert ID: " +
                                        createKeysAndCertificateResult.getCertificateId() +
                                        " created.");

                        // store in keystore for use in MQTT client
                        // saved as alias "default" so a new certificate isn't
                        // generated each run of this application
                        AWSIotKeystoreHelper.saveCertificateAndPrivateKey(certificateId,
                                createKeysAndCertificateResult.getCertificatePem(),
                                createKeysAndCertificateResult.getKeyPair().getPrivateKey(),
                                keystorePath, keystoreName, keystorePassword);

                        // load keystore from file into memory to pass on
                        // connection
                        clientKeyStore = AWSIotKeystoreHelper.getIotKeystore(certificateId,
                                keystorePath, keystoreName, keystorePassword);

                        // Attach a policy to the newly created certificate.
                        // This flow assumes the policy was already created in
                        // AWS IoT and we are now just attaching it to the
                        // certificate.
                        AttachPrincipalPolicyRequest policyAttachRequest =
                                new AttachPrincipalPolicyRequest();
                        policyAttachRequest.setPolicyName(AWS_IOT_POLICY_NAME);
                        policyAttachRequest.setPrincipal(createKeysAndCertificateResult
                                .getCertificateArn());
                        mIotAndroidClient.attachPrincipalPolicy(policyAttachRequest);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //btnConnect.setEnabled(true);
                            }
                        });
                    } catch (Exception e) {
                        Log.e(LOG_TAG,
                                "Exception occurred when generating new private key and certificate.",
                                e);
                    }
                }
            }).start();
        }
        //endregion

        try {
            mqttManager.connect(clientKeyStore, new AWSIotMqttClientStatusCallback() {
                @Override
                public void onStatusChanged(final AWSIotMqttClientStatus status,
                                            final Throwable throwable) {
                    Log.d(LOG_TAG, "Status = " + String.valueOf(status));

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (status == AWSIotMqttClientStatus.Connecting) {
                                tvStatus.setText("Connecting...");

                            } else if (status == AWSIotMqttClientStatus.Connected) {
                                tvStatus.setText("Connected");

                            } else if (status == AWSIotMqttClientStatus.Reconnecting) {
                                if (throwable != null) {
                                    Log.e(LOG_TAG, "Connection error.", throwable);
                                }
                                tvStatus.setText("Reconnecting");
                            } else if (status == AWSIotMqttClientStatus.ConnectionLost) {
                                if (throwable != null) {
                                    Log.e(LOG_TAG, "Connection error.", throwable);
                                }
                                tvStatus.setText("Disconnected");
                            } else {
                                tvStatus.setText("Disconnected");

                            }
                        }
                    });
                }
            });
        } catch (final Exception e) {
            Log.e(LOG_TAG, "Connection error.", e);
            tvStatus.setText("Error! " + e.getMessage());
        }

        AWSIotMqttNewMessageCallback awsIotMqttNewMessageCallback = new AWSIotMqttNewMessageCallback() {
            @Override
            public void onMessageArrived(final String topic, final byte[] data) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String message = new String(data, "UTF-8");
                            Log.d(LOG_TAG, "Message arrived:");
                            Log.d(LOG_TAG, "   Topic: " + topic);
                            Log.d(LOG_TAG, " Message: " + message);

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
                        } catch (UnsupportedEncodingException e) {
                            Log.e(LOG_TAG, "Message encoding error.", e);
                        }
                    }
                });
            }
        };

        //new Thread() {
        //    @Override
        //   public void run() {
        try {
            Thread.sleep(1000);
            //mqttManager.subscribeToTopic(AC_TOPIC, AWSIotMqttQos.QOS0, awsIotMqttNewMessageCallback);

        } catch (Exception e) {
            Log.e(LOG_TAG, "Subscription error.", e);
        }
        // }
        //};

    }


    //Call an activity for creating a scenario
    public void createActivity(View v) {
        Intent i = new Intent(this, InputActionsActivity.class);
        startActivityForResult(i, MY_CHILD_ACTIVITY);
    }

    //Call an activity for editing existing a scenario
    public void editActivity(View v, Scenario editedScenario) {
        Intent i = new Intent(this, InputActionsActivity.class);
        //Scenario editedScenario = itemScenarioList.get(itemScenarioList.size()-1);
        i.putExtra("edit", editedScenario);
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

        // PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
        // startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case (MY_CHILD_ACTIVITY): {
                if (resultCode == Activity.RESULT_OK) {
                    Scenario scenarioCreated = (Scenario) data.getSerializableExtra("result");
                    process(scenarioCreated);
                    publishScenario(scenarioCreated); //publish to aws server!
                    itemScenarioList.add(new Scenario(scenarioCreated));
                    customAdapter.notifyDataSetChanged();

                    // Toast.makeText(getApplicationContext(), returnValue, Toast.LENGTH_SHORT).show();
                }
                break;
            }
            case (PLACE_PICKER_REQUEST): {
                if (resultCode == RESULT_OK) {
                    Place place = PlacePicker.getPlace(data, this);
                    LocationMock.home = place;
                }
            }
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
                        conditionsObj.put("location", "=" + "'" + scenarioInput.trigger.location.toString().toLowerCase() + "'");
                        break;
                    case Time:
                        conditionsObj.put("time","=" + scenarioInput.trigger.time.toString().replaceFirst("HHMM", String.valueOf(scenarioInput.trigger.time.first)).replaceFirst("HHMM", String.valueOf(scenarioInput.trigger.time.second)).toLowerCase());
                        break;
                    case Climate:
                        String value = scenarioInput.trigger.climate.toString().replace("_", "").replaceFirst("Above", ">").replace("Below", "<").replace("Degrees", String.valueOf(scenarioInput.trigger.climate.first)).toLowerCase();
                        conditionsObj.put("climate", value);
                        break;
                    case Motion:
                        conditionsObj.put("motion", "=" +  "'" + scenarioInput.trigger.motion.toString().toLowerCase() + "'" );
                        break;
                }
            }
            for (ScenarioAction.Name name : scenarioAction.names) {
                switch (name) {
                    case AC:
                        actionsObj.put(name.name().toLowerCase(), scenarioAction.action.ac.name().toLowerCase());
                        break;
                    case TV:
                        actionsObj.put(name.name().toLowerCase(), scenarioAction.action.tv.name().toLowerCase());
                        break;
                    case Light:
                        actionsObj.put(name.name().toLowerCase(), scenarioAction.action.light.name().toLowerCase());
                        break;
                    case Security:
                        actionsObj.put(name.name().toLowerCase(), scenarioAction.action.security.name().toLowerCase());
                        break;
                    case Boiler:
                        actionsObj.put(name.name().toLowerCase(), scenarioAction.action.boiler.name().toLowerCase());
                        break;
                }
            }
            obj.put(scenarioCreated.getScenarioName(),scenarioObj.put("conditions",conditionsObj).put("actions",actionsObj));
            String payLoad = obj.toString();

            final String topic = "scenario/create";
            try {
                mqttManager.publishString(payLoad, topic, AWSIotMqttQos.QOS0);
            } catch (Exception e) {
                Log.e(LOG_TAG, "Publish error.", e);
            }

        } catch (JSONException e) {
            System.out.println(e.getMessage());
        }
    }
    //endregion

    //region Publish AWS Func
    public void publishScenario(Scenario publishedScenario) {

        //prepare new AWS rule
//        RepublishAction republishAction = new RepublishAction();
//        republishAction.setTopic("AWS/Scenario5"); //topic to which we republish
//        republishAction.setRoleArn("arn:aws:iam::896223013790:role/service-role/IoTRole"); //Role that has iot-publish permissions
//
//        com.amazonaws.services.iot.model.Action action = new Action();
//        action.setRepublish(republishAction);
//
//        TopicRulePayload topicRulePayload = new TopicRulePayload();
//        topicRulePayload.setRuleDisabled(false);
//        topicRulePayload.setActions(Arrays.asList(action));
//        topicRulePayload.setSql("SELECT * FROM 'AWS/Scenario'");
//        topicRulePayload.setDescription("A test rule");
//
//        CreateTopicRuleRequest createTopicRuleRequest = new CreateTopicRuleRequest();
//        createTopicRuleRequest.setRuleName("republish5");
//        createTopicRuleRequest.setTopicRulePayload(topicRulePayload);
//
//
//        mIotAndroidClient.createTopicRule(createTopicRuleRequest);


        final String topic = "AWS/Scenario";
        final String msg = publishedScenario.getScenarioName();

        try {
            mqttManager.publishString(msg, topic, AWSIotMqttQos.QOS0);
        } catch (Exception e) {
            Log.e(LOG_TAG, "Publish error.", e);
        }

    }

    public void publishRemovedScenario(Scenario removedScenario) {
        final String topic = "AWS/Scenario";
        final String msg = "REMOVED!" + removedScenario.getScenarioName();

        try {
            mqttManager.publishString(msg, topic, AWSIotMqttQos.QOS0);
        } catch (Exception e) {
            Log.e(LOG_TAG, "Publish error.", e);
        }
    }
    //endregion
}

