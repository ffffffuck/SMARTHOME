package com.example.lee.adfa;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lee.adfa.preference.AddressPreference;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;


public class MainActivity extends AppCompatActivity {

    String serverurl;
    private TextView subText;
    private Switch ledStatus;
    MqttAndroidClient client;
    String topic = "nodemcu";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        AddressPreference ap = new AddressPreference(getBaseContext());
        String ip = ap.getIP();
        String port = String.valueOf(ap.getPort());

        serverurl = "tcp://";
        serverurl+=ip;
        serverurl+=":";
        serverurl+=port;

        ledStatus = (Switch) findViewById(R.id.ledStatus);
        ledStatus.setOnClickListener(ledClickListener);

        subText = (TextView) findViewById(R.id.subText);

        String clientId = MqttClient.generateClientId();
        client = new MqttAndroidClient(this.getApplicationContext(), serverurl,
                clientId);
        Toast.makeText(MainActivity.this,serverurl,Toast.LENGTH_SHORT).show();

        try {
            final IMqttToken token = client.connect();
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // We are connected
                    Toast.makeText(MainActivity.this, "connected", Toast.LENGTH_SHORT).show();
                    mqttSub();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // Something went wrong e.g. connection timeout or firewall problems
                    Toast.makeText(MainActivity.this, "not connected", Toast.LENGTH_SHORT).show();

                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }

        client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
            }
            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                subText.setText(new String(message.getPayload()));

                if (subText.getText().equals("LED ON")) {
                    ledStatus.setChecked(true);
                } else if (subText.getText().equals("LED OFF")) {
                    ledStatus.setChecked(false);
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        if (id == R.id.action_setting) {
            Intent intent = new Intent(getBaseContext(),SettingActivity.class);

            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public View.OnClickListener ledClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(ledStatus.isChecked()) {
                String message = "1";
                try {
                    client.publish(topic, message.getBytes(),0,false);
                } catch (MqttException e) {
                    e.printStackTrace();
                }
            }
            else {
                String message = "0";
                try {
                    client.publish(topic, message.getBytes(),0,false);
                } catch (MqttException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    public void mqttSub(){
        String topic = "ledstate";
        try{
            client.subscribe(topic,0);
        }catch (MqttException e){
            e.printStackTrace();
        }
    }

    public  void conn(View view){
        try {
            final IMqttToken token = client.connect();
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Toast.makeText(MainActivity.this, "connected", Toast.LENGTH_SHORT).show();
                    mqttSub();
                }
                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Toast.makeText(MainActivity.this, "not connected", Toast.LENGTH_SHORT).show();

                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
    public void disconn(View view){
        try {
            final IMqttToken token = client.disconnect();
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // We are connected
                    Toast.makeText(MainActivity.this, "disconnected", Toast.LENGTH_SHORT).show();

                }
                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // Something went wrong e.g. connection timeout or firewall problems
                    Toast.makeText(MainActivity.this, "could not disconnect..", Toast.LENGTH_SHORT).show();

                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}

