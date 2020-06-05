package com.khanhlh.firewarning.mqtt;

import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.UUID;

public class MqttCommon {
    private static String userName = "";
    private static String passWord = "";
    private static final String host = "tcp://192.168.2.13:1234";
    private static final String TAG = "MqttClient";

    final private String PRODUCTKEY = "a11xsrW****";
    final private String DEVICENAME = "paho_android";
    final private String DEVICESECRET = "tLMT9QWD36U2SArglGqcHCDK9rK9****";

    private ReceiveMessage receiveMessage;
    private Activity activity;
    private String clientId;
    private MqttConnectOptions mqttConnectOptions;

    public void setReceiveMessage(ReceiveMessage receiveMessage) {
        this.receiveMessage = receiveMessage;
    }

    public MqttCommon(Activity activity) {
        this.activity = activity;
        clientId = UUID.randomUUID().toString();
        /* Obtain the MQTT connection information clientId, username, and password. */
        AiotMqttOption aiotMqttOption = new AiotMqttOption().getMqttOption(PRODUCTKEY, DEVICENAME, DEVICESECRET);
        if (aiotMqttOption == null) {
            Log.e("AiotMqttOption", "device info error");
        } else {
            clientId = aiotMqttOption.getClientId();
            userName = aiotMqttOption.getUsername();
            passWord = aiotMqttOption.getPassword();
        }

        /* Create an MqttConnectOptions object and configure the username and password. */
        mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setUserName(userName);
        mqttConnectOptions.setPassword(passWord.toCharArray());

        /* Create an MqttAndroidClient object and set a callback interface. */

//        MqttAndroidClient mqttAndroidClient = new MqttAndroidClient(activity, host, clientId);
//        mqttAndroidClient.setCallback(mqttCallBack);
    }

    public void publishMessage(String pubTopic, String payload) {
        try {
            MqttAndroidClient mqttAndroidClient = new MqttAndroidClient(activity, host, clientId);
            mqttAndroidClient.connect(mqttConnectOptions, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.i(TAG, "connect succeed");
                    try {
                        MqttMessage message = new MqttMessage();
                        message.setPayload(payload.getBytes());
                        message.setQos(0);
                        mqttAndroidClient.publish(pubTopic, message, null, publishListener);
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.i(TAG, "connect failed");
                }
            });
        } catch (MqttException e) {
            Log.e(TAG, e.toString());
            e.printStackTrace();
        }
    }

    public void subscribeTopic(String topic) {
        /* Establish an MQTT connection */
        try {
            MqttAndroidClient mqttAndroidClient = new MqttAndroidClient(activity, host, clientId);

            mqttAndroidClient.connect(mqttConnectOptions, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.i(TAG, "connect succeed");

                    try {
                        mqttAndroidClient.subscribe(topic, 0, iMqttMessageListener);
//                        mqttAndroidClient.subscribe(topic, 0, null, subscribeListener);
                    } catch (MqttException e) {
                        e.printStackTrace();
                        receiveMessage.onSubError(e.toString());
                    }
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.i(TAG, "connect failed");
                    receiveMessage.onSubError(exception.toString());
                }
            });


        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void unSubscribe(String topic) {
        try {
            MqttAndroidClient mqttAndroidClient = new MqttAndroidClient(activity, host, clientId);

            mqttAndroidClient.unsubscribe(topic);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    private IMqttActionListener publishListener = new IMqttActionListener() {
        @Override
        public void onSuccess(IMqttToken asyncActionToken) {
            Log.i(TAG, "publish succeed! ");
            receiveMessage.onPubSuccess(asyncActionToken.toString());
        }

        @Override
        public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
            Log.i(TAG, "publish failed!");
            receiveMessage.onPubError(exception.toString());
        }
    };
    private IMqttActionListener subscribeListener = new IMqttActionListener() {
        @Override
        public void onSuccess(IMqttToken asyncActionToken) {
            Log.i(TAG, "subscribed succeed");
        }

        @Override
        public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
            Log.i(TAG, "subscribed failed");
        }
    };

    private IMqttMessageListener iMqttMessageListener = (topic, message) -> {
        if (message == null) {
            receiveMessage.onSubError(null);
            return;
        }
        String mess = new String(message.getPayload());
        receiveMessage.onLoading(true);
        if (!TextUtils.isEmpty(mess)) {
            receiveMessage.onSubSuccess(mess);
        }
        Log.d(TAG, new String(message.getPayload()));
    };

    public interface ReceiveMessage {
        void onSubSuccess(String message);

        void onSubError(String message);

        void onPubSuccess(String message);

        void onPubError(String message);

        void onLoading(boolean loading);
    }
}
