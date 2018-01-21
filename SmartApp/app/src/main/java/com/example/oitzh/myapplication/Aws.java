package com.example.oitzh.myapplication;

import android.content.Context;
import android.util.Log;
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

import java.security.KeyStore;
import java.util.UUID;

/**
 * Created by oitzh on 12/01/2018.
 */

public class Aws {

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
    Context mainActivity;
    AWSIotClient mIotAndroidClient;
    AWSIotMqttManager mqttManager;
    String clientId;
    String keystorePath;
    String keystoreName;
    String keystorePassword;
    KeyStore clientKeyStore = null;
    String certificateId;
    CognitoCachingCredentialsProvider credentialsProvider;


    public Aws(Context context) {
        this.mainActivity = context;
    }

    public void initialize() {

        //region AWS CONNECT Listener Code
//        View.OnClickListener connectClick = new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                Log.d(LOG_TAG, "clientId = " + clientId);
//
//                try {
//                    mqttManager.connect(clientKeyStore, new AWSIotMqttClientStatusCallback() {
//                        @Override
//                        public void onStatusChanged(final AWSIotMqttClientStatus status,
//                                                    final Throwable throwable) {
//                            Log.d(LOG_TAG, "Status = " + String.valueOf(status));
//
//                            ((MainActivity)mainActivity).runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    if (status == AWSIotMqttClientStatus.Connecting) {
//                                        ((TextView) ((MainActivity)mainActivity).findViewById(R.id.tvStatus)).setText("Connecting...");
//
//                                    } else if (status == AWSIotMqttClientStatus.Connected) {
//                                        ((TextView) ((MainActivity)mainActivity).findViewById(R.id.tvStatus)).setText("Connected");
//
//                                    } else if (status == AWSIotMqttClientStatus.Reconnecting) {
//                                        if (throwable != null) {
//                                            Log.e(LOG_TAG, "Connection error.", throwable);
//                                        }
//                                        ((TextView) ((MainActivity)mainActivity).findViewById(R.id.tvStatus)).setText("Reconnecting");
//                                    } else if (status == AWSIotMqttClientStatus.ConnectionLost) {
//                                        if (throwable != null) {
//                                            Log.e(LOG_TAG, "Connection error.", throwable);
//                                        }
//                                        ((TextView) ((MainActivity)mainActivity).findViewById(R.id.tvStatus)).setText("Disconnected");
//                                    } else {
//                                        ((TextView) ((MainActivity)mainActivity).findViewById(R.id.tvStatus)).setText("Disconnected");
//
//                                    }
//                                }
//                            });
//                        }
//                    });
//                } catch (final Exception e) {
//                    Log.e(LOG_TAG, "Connection error.", e);
//                    ((TextView) ((MainActivity)mainActivity).findViewById(R.id.tvStatus)).setText("Error! " + e.getMessage());
//                }
//            }
//        };
        //endregion

        // MQTT client IDs are required to be unique per AWS IoT account.
        // This UUID is "practically unique" but does not _guarantee_
        // uniqueness.
        clientId = UUID.randomUUID().toString();
        //tvClientId.setText(clientId);

        // Initialize the AWS Cognito credentials provider
        credentialsProvider = new CognitoCachingCredentialsProvider(
                mainActivity.getApplicationContext(), // context
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

        keystorePath = mainActivity.getFilesDir().getPath();
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

                        ((MainActivity) mainActivity).runOnUiThread(new Runnable() {
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
    }

    public void connect() {
        try {
            mqttManager.connect(clientKeyStore, new AWSIotMqttClientStatusCallback() {
                @Override
                public void onStatusChanged(final AWSIotMqttClientStatus status,
                                            final Throwable throwable) {
                    Log.d(LOG_TAG, "Status = " + String.valueOf(status));

                    ((MainActivity) mainActivity).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (status == AWSIotMqttClientStatus.Connecting) {
                                ((TextView) ((MainActivity) mainActivity).findViewById(R.id.tvStatus)).setText("Connecting...");

                            } else if (status == AWSIotMqttClientStatus.Connected) {
                                ((TextView) ((MainActivity) mainActivity).findViewById(R.id.tvStatus)).setText("Connected");

                            } else if (status == AWSIotMqttClientStatus.Reconnecting) {
                                if (throwable != null) {
                                    Log.e(LOG_TAG, "Connection error.", throwable);
                                }
                                ((TextView) ((MainActivity) mainActivity).findViewById(R.id.tvStatus)).setText("Reconnecting");
                            } else if (status == AWSIotMqttClientStatus.ConnectionLost) {
                                if (throwable != null) {
                                    Log.e(LOG_TAG, "Connection error.", throwable);
                                }
                                ((TextView) ((MainActivity) mainActivity).findViewById(R.id.tvStatus)).setText("Disconnected");
                            } else {
                                ((TextView) ((MainActivity) mainActivity).findViewById(R.id.tvStatus)).setText("Disconnected");

                            }
                        }
                    });
                }
            });
        } catch (final Exception e) {
            Log.e(LOG_TAG, "Connection error.", e);
            ((TextView) ((MainActivity) mainActivity).findViewById(R.id.tvStatus)).setText("Error! " + e.getMessage());
        }
    }

    public void subscribe(String topic, AWSIotMqttQos awsIotMqttQos, AWSIotMqttNewMessageCallback awsIotMqttNewMessageCallback) {
        try {
            Thread.sleep(2000);
            mqttManager.subscribeToTopic(topic, awsIotMqttQos, awsIotMqttNewMessageCallback);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void publish(String msg, String topic) {
        try {
            mqttManager.publishString(msg, topic, AWSIotMqttQos.QOS0);
        } catch (Exception e) {
            Log.e(LOG_TAG, "Publish error.", e);
        }
    }

}
