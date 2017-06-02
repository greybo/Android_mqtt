package com.example.android_mqtt;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.example.android_mqtt.model.Sound;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.UnsupportedEncodingException;
import javax.net.SocketFactory;
import javax.net.ssl.SSLContext;

public class MainActivity extends AppCompatActivity {
    private EditText token, orgId, deviceType, deviceId;
    private TextView info;
    private Button connectBtn, publishBtn;
    private CheckBox boxSslOn;

    private static final String TAG = "mqtt_android";
    private static final String IOT_ORGANIZATION_TCP = ".messaging.internetofthings.ibmcloud.com:1883";
    private static final String IOT_ORGANIZATION_SSL = ".messaging.internetofthings.ibmcloud.com:8883";
    private static final String IOT_DEVICE_USERNAME = "use-token-auth";

    private MqttAndroidClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        info = (TextView) findViewById(R.id.textInfo);
        connectBtn = (Button) findViewById(R.id.btnConnect);
        publishBtn = (Button) findViewById(R.id.btnPublish);
        publishBtn.setOnClickListener(publishListener);
        connectBtn.setOnClickListener(connectListener);

        token = (EditText) findViewById(R.id.etPass);
        orgId = (EditText) findViewById(R.id.etOrgId);
        deviceType = (EditText) findViewById(R.id.etDevType);
        deviceId = (EditText) findViewById(R.id.etDevId);
        info = (TextView) findViewById(R.id.textInfo);

        orgId.setText("p9p0l7");
        deviceType.setText("android3");
        deviceId.setText("333");
        token.setText("glNpI(@qHs2VlVS&ac");


        boxSslOn = (CheckBox) findViewById(R.id.checkboxSsl);
    }

    public IMqttToken connectDevice() throws MqttException {
        SocketFactory factory = null;

        if (boxSslOn.isChecked()) {
            factory = getSocketFactory();
        }

        String organization = orgId.getText().toString();
        String device_type = this.deviceType.getText().toString();
        String device_Id = deviceId.getText().toString();
        String authorizationToken = token.getText().toString();
        String clientID = String.format("d:%s:%s:%s", organization, device_type, device_Id);

        String connectionURI;
        if (factory == null) {
            connectionURI = "tcp://" + organization + IOT_ORGANIZATION_TCP;
        } else {
            connectionURI = "ssl://" + organization + IOT_ORGANIZATION_SSL;
        }

        if (!isMqttConnected()) {
            if (client != null) {
                client.unregisterResources();
                client = null;
            }

            client = new MqttAndroidClient(this, connectionURI, clientID);
            client.setCallback(mqttCallback);

            String username = IOT_DEVICE_USERNAME;
            char[] password = authorizationToken.toCharArray();

            MqttConnectOptions options = new MqttConnectOptions();
            options.setCleanSession(true);
            options.setUserName(username);
            options.setPassword(password);

            if (factory != null) {
                options.setSocketFactory(factory);
            }

            Log.i(TAG, "Connecting to server: " + connectionURI);
            Log.i(TAG, "ClientId: " + clientID);

            try {
                // connectBtn
                client.connect(options, this, mqttActionListener);
            } catch (MqttException e) {
                Log.e(TAG, "Exception caught while attempting to connectBtn to server", e.getCause());
                throw e;
            }
        } else {
            IMqttToken token = client.disconnect();
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    info.setText("Disconnect:");
                    Log.i(TAG, "Disconnect: ");
                    try {
                        connectDevice();
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                }
            });
        }

        return null;
    }

    IMqttActionListener mqttActionListener = new IMqttActionListener() {
        @Override
        public void onSuccess(IMqttToken asyncActionToken) {
            Log.i(TAG, "Connected success: ");
            info.setText("Connected success: ");

        }

        @Override
        public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
            info.setText("onFailure:");
            Log.i(TAG, "onFailure: ");
        }
    };

    MqttCallback mqttCallback = new MqttCallback() {
        @Override
        public void connectionLost(Throwable cause) {
            info.setText("Connection lost:");
            Log.i(TAG, "connectionLost: ");
        }

        @Override
        public void messageArrived(String topic, MqttMessage message) throws Exception {
            Log.i(TAG, "messageArrived: " + topic + " message: " + message);
        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken token) {
            try {
                Log.i(TAG, "deliveryComplete: " + token.getMessage().toString());
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
    };

    private SocketFactory getSocketFactory() {
        SocketFactory factory = null;
        try {

//            SSLContext sslContext;
//            KeyStore ks = KeyStore.getInstance("bks");
//            ks.load(this.getResources().openRawResource(R.raw.iot), "password".toCharArray());
//            TrustManagerFactory tmf = TrustManagerFactory.getInstance("X509");
//            tmf.init(ks);
//            TrustManager[] tm = tmf.getTrustManagers();
//            sslContext = SSLContext.getInstance("TLSv1.2");
//            sslContext.init(null, tm, null);
//            factory = sslContext.getSocketFactory();

            SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
           
            sslContext.init(null, null, null);
            factory = sslContext.getSocketFactory();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return factory;
    }

    private boolean isMqttConnected() {

        boolean connected = false;
        try {
            if ((client != null) && (client.isConnected())) {
                connected = true;
            }
        } catch (Exception e) {
            // swallowing the exception as it means the client is not connected
        }

        return connected;
    }

    View.OnClickListener connectListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            try {
                connectDevice();
            } catch (MqttException e) {
                e.printStackTrace();
            }

        }
    };

    private String prepareJson() {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();

        Sound sound = new Sound();
        // Log.i(TAG, "GSON: " + gson.toJson(sound));

        return gson.toJson(sound);
    }

    View.OnClickListener publishListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String topic = "iot-2/evt/audio/fmt/json";
            String json = prepareJson();
            try {
                client.publish(
                        topic,
                        json.getBytes("UTF-8"),
                        1,
                        false);
                Log.i(TAG, "publish ");
            } catch (MqttException | UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    };

}
