package com.example.android_mqtt;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.UnsupportedEncodingException;
import java.security.KeyStore;

import javax.net.SocketFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

public class MainActivity extends AppCompatActivity {
    private EditText  pass, orgId, deviceType, deviceId;
    private TextView info;
    private Button connect, publish;
    private CheckBox boxSubscribe;
    private static final String TAG = "log_tag";
    private static final String IOT_ORGANIZATION_TCP = ".messaging.internetofthings.ibmcloud.com:1883";
    private static final String IOT_ORGANIZATION_SSL = ".messaging.internetofthings.ibmcloud.com:8883";
    private static final String IOT_DEVICE_USERNAME = "use-token-auth";

    private MqttAndroidClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        info = (TextView) findViewById(R.id.textInfo);
        connect = (Button) findViewById(R.id.btnConnect);
        publish = (Button) findViewById(R.id.btnPublish);
        publish.setOnClickListener(buttonPublish);
        connect.setOnClickListener(buttonConnect);

        pass = (EditText) findViewById(R.id.etPass);

        orgId = (EditText) findViewById(R.id.etOrgId);
        deviceType = (EditText) findViewById(R.id.etDevType);
        deviceId = (EditText) findViewById(R.id.etDevId);
        info = (TextView) findViewById(R.id.textInfo);



        orgId.setText("p9p0l7");
        deviceType.setText("android3");
        deviceId.setText("333");
        pass.setText("glNpI(@qHs2VlVS&ac");
        boxSubscribe = (CheckBox) findViewById(R.id.checkboxSubscribe);

    }

    public IMqttToken connectDevice() throws MqttException {
        SocketFactory factory = handleActivate();

        String organization = orgId.getText().toString();
        String deviceT = deviceType.getText().toString();
        String device_Id = deviceId.getText().toString();
        String authorizationToken = pass.getText().toString();
        String clientID = String.format("d:%s:%s:%s", organization, deviceT, device_Id);
      //  String clientID = "d:" + dev.getOrganization() + ":" + dev.getDeviceType() + ":" + dev.getDeviceID();
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
                // connect
                client.connect(options, this, mqttActionListener);
            } catch (MqttException e) {
                Log.e(TAG, "Exception caught while attempting to connect to server", e.getCause());
                throw e;
            }


        return null;
    }

    IMqttActionListener mqttActionListener = new IMqttActionListener() {
        @Override
        public void onSuccess(IMqttToken asyncActionToken) {
            Log.i(TAG, "Connected success: ");
            String topic = "iot-2/cmd/+/fmt/json";
            if (boxSubscribe.isChecked()) {
                try {
                    client.subscribe(topic,0);

                    Log.i(TAG, "Subscribed");
                } catch (MqttException e) {
                    Log.i(TAG, "Subscribe failed");
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
            Log.i(TAG, "onFailure: ");
        }
    };

    MqttCallback mqttCallback = new MqttCallback() {
        @Override
        public void connectionLost(Throwable cause) {
            Log.i(TAG, "connectionLost: ");
        }

        @Override
        public void messageArrived(String topic, MqttMessage message) throws Exception {
            Log.i(TAG, "messageArrived: ");
        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken token) {
            Log.i(TAG, "deliveryComplete: ");
        }
    };

    private SocketFactory handleActivate() {
        SocketFactory factory = null;
        try {

            SSLContext sslContext;
            KeyStore ks = KeyStore.getInstance("bks");
            ks.load(this.getResources().openRawResource(R.raw.iot), "password".toCharArray());
            TrustManagerFactory tmf = TrustManagerFactory.getInstance("X509");
            tmf.init(ks);
            TrustManager[] tm = tmf.getTrustManagers();
            sslContext = SSLContext.getInstance("TLSv1.2");
            sslContext.init(null, tm, null);
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

    View.OnClickListener buttonConnect = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            try {
                connectDevice();
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
    };


    View.OnClickListener buttonPublish = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            String topic = "iot-2/evt/audio/fmt/json";
            try {
                client.publish(
                        topic,
                        "payload".getBytes("UTF-8"),
                        1,
                        false);
                Log.i(TAG, "publish ");
            } catch (MqttException | UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    };
}
