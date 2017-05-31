package com.example.android_mqtt_ssl;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.android_mqtt_ssl.model.Device;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.security.KeyStore;

import javax.net.SocketFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "log_tag";
    private static final String IOT_ORGANIZATION_TCP = ".messaging.internetofthings.ibmcloud.com:1883";
    private static final String IOT_ORGANIZATION_SSL = ".messaging.internetofthings.ibmcloud.com:8883";
    private static final String IOT_DEVICE_USERNAME = "use-token-auth";
    private static Device dev = new Device();
    private MqttAndroidClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dev.setOrganization("p9p0l7");
        dev.setDeviceType("android3");
        dev.setDeviceID("333");
        dev.setAuthorizationToken("glNpI(@qHs2VlVS&ac");

        try {
            connectDevice();
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public IMqttToken connectDevice() throws MqttException {
        SocketFactory factory = handleActivate();

        String clientID = "d:" + dev.getOrganization() + ":" + dev.getDeviceType() + ":" + dev.getDeviceID();
        String connectionURI;
        if (factory == null) {
            connectionURI = "tcp://" + dev.getOrganization() + IOT_ORGANIZATION_TCP;
        } else {
            connectionURI = "ssl://" + dev.getOrganization() + IOT_ORGANIZATION_SSL;
        }

        if (!isMqttConnected()) {
            if (client != null) {
                client.unregisterResources();
                client = null;
            }
            client = new MqttAndroidClient(this, connectionURI, clientID);
            client.setCallback(mqttCallback);

            String username = IOT_DEVICE_USERNAME;
            char[] password = dev.getAuthorizationToken().toCharArray();

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
        }
        return null;
    }

    IMqttActionListener mqttActionListener = new IMqttActionListener() {
        @Override
        public void onSuccess(IMqttToken asyncActionToken) {
            Log.i(TAG, "Connected success: ");
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
}
