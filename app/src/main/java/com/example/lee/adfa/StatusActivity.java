package com.example.lee.adfa;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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

public class StatusActivity extends AppCompatActivity {
    String serverurl;
    MqttAndroidClient client;

    private TextView subText;
    private Switch ledStatus;
    String topic = "nodemcu";

    private TextView tvsubText;
    private Switch tvStatus;
    String tvtopic = "TVMQTT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

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

        tvStatus = (Switch) findViewById(R.id.tvStatus);
        tvStatus.setOnClickListener(tvClickListener);
        tvsubText = (TextView) findViewById(R.id.tvsubText);

        String clientId = MqttClient.generateClientId();
        client = new MqttAndroidClient(this.getApplicationContext(), serverurl,
                clientId);
        Toast.makeText(StatusActivity.this,serverurl,Toast.LENGTH_SHORT).show();

        try {
            final IMqttToken token = client.connect();
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // We are connected
                    Toast.makeText(StatusActivity.this, "connected", Toast.LENGTH_SHORT).show();
                    mqttSub();
                    TVmqttSub();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // Something went wrong e.g. connection timeout or firewall problems
                    Toast.makeText(StatusActivity.this, "not connected", Toast.LENGTH_SHORT).show();

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
                String massage = new String(message.getPayload());
                if(massage.contains("LED")) {
                    subText.setText(massage);
                }else if(massage.contains("TV")){
                    tvsubText.setText(massage);
                }

                if (subText.getText().equals("LED ON")) {
                    ledStatus.setChecked(true);
                } else if (subText.getText().equals("LED OFF")) {
                    ledStatus.setChecked(false);
                }

                if(tvsubText.getText().equals("TV ON")) {
                    tvStatus.setChecked(true);
                } else if (tvsubText.getText().equals("TV OFF")) {
                    tvStatus.setChecked(false);
                }

            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });
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

    public View.OnClickListener tvClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(tvStatus.isChecked()) {
                String message = "1";
                try {
                    client.publish(tvtopic, message.getBytes(),0,false);
                } catch (MqttException e) {
                    e.printStackTrace();
                }
            }
            else {
                String message = "0";
                try {
                    client.publish(tvtopic, message.getBytes(),0,false);
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
    public void TVmqttSub(){
        String topic = "tvstate";
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
                    Toast.makeText(StatusActivity.this, "connected", Toast.LENGTH_SHORT).show();
                    mqttSub();
                    TVmqttSub();
                }
                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Toast.makeText(StatusActivity.this, "not connected", Toast.LENGTH_SHORT).show();

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
                    Toast.makeText(StatusActivity.this, "disconnected", Toast.LENGTH_SHORT).show();

                }
                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // Something went wrong e.g. connection timeout or firewall problems
                    Toast.makeText(StatusActivity.this, "could not disconnect..", Toast.LENGTH_SHORT).show();

                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}


