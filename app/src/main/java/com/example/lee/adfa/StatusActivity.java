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
import org.json.JSONArray;
import org.json.JSONObject;

public class StatusActivity extends AppCompatActivity {
    String serverurl;
    MqttAndroidClient client;

    String topic = "nodemcu";
    String arduinotopic = "arduino";

    String TV;
    String AIRCON ;
    String WINDOW;
    String TEMPERTURE;
    String HUMIDITY ;

    private TextView subText;
    private Switch ledStatus;


    private TextView tvsubText;
    private Switch tvStatus;

    private TextView airconsubText;
    private Switch airconStatus;


    private TextView windowsubText;
    private Switch windowStatus;

    private TextView temperturesubText;

    private TextView humiditysubText;

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

        airconStatus = (Switch) findViewById(R.id.airconStatus);
        airconStatus.setOnClickListener(airconClickListener);
        airconsubText = (TextView) findViewById(R.id.airconsubText);

        windowStatus = (Switch) findViewById(R.id.windowStatus);
        windowStatus.setOnClickListener(windowClickListener);
        windowsubText = (TextView) findViewById(R.id.windowsubText);

        temperturesubText = (TextView) findViewById(R.id.temperturesubText);

        humiditysubText = (TextView) findViewById(R.id.humiditysubText);

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
                    arduinoSub();
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
                }else if(massage.contains("AIRCON")) {
                    airconsubText.setText(massage);
                }else if(massage.contains("WINDOW")) {
                    windowsubText.setText(massage);
                }else if(massage.contains("TEMPERTURE")) {
                    temperturesubText.setText(massage);
                }else if(massage.contains("HUMIDITY")) {
                    humiditysubText.setText(massage);
                }else {
                    JSONArray jarray = new JSONArray(message);

                    for (int i = 0; i < jarray.length(); i++) {
                        JSONObject jObject = jarray.getJSONObject(i);  // JSONObject 추출
                        TV = jObject.getString("tv");
                        AIRCON = jObject.getString("aircon");
                        WINDOW = jObject.getString("window");
                        TEMPERTURE = jObject.getString("temperture");
                        HUMIDITY = jObject.getString("humidity");


                        if(TV.contains("TV")){
                            tvsubText.setText(TV);
                        }else if(AIRCON.contains("AIRCON")) {
                            airconsubText.setText(AIRCON);
                        }else if(WINDOW.contains("WINDOW")) {
                            windowsubText.setText(WINDOW);
                        }else if(TEMPERTURE.contains("TEMPERTURE")) {
                            temperturesubText.setText(TEMPERTURE);
                        }else if(HUMIDITY.contains("HUMIDITY")) {
                            humiditysubText.setText(HUMIDITY);
                        }

                    }


                    if (subText.getText().equals("LED ON")) {
                        ledStatus.setChecked(true);
                    } else if (subText.getText().equals("LED OFF")) {
                        ledStatus.setChecked(false);
                    }

                    if (tvsubText.getText().equals("TV ON")) {
                        tvStatus.setChecked(true);
                    } else if (tvsubText.getText().equals("TV OFF")) {
                        tvStatus.setChecked(false);
                    }

                    if (airconsubText.getText().equals("AIRCON ON")) {
                        airconStatus.setChecked(true);
                    } else if (airconsubText.getText().equals("AIRCON OFF")) {
                        airconStatus.setChecked(false);
                    }

                    if (windowsubText.getText().equals("WINDOW OPEN")) {
                        windowStatus.setChecked(true);
                    } else if (windowsubText.getText().equals("WINDOW CLOSE")) {
                        windowStatus.setChecked(false);
                    }
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
                String message = "tv1";
                try {
                    client.publish(arduinotopic, message.getBytes(),0,false);
                } catch (MqttException e) {
                    e.printStackTrace();
                }
            }
            else {
                String message = "tv0";
                try {
                    client.publish(arduinotopic, message.getBytes(),0,false);
                } catch (MqttException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    public View.OnClickListener airconClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(windowStatus.isChecked()) {
                String message = "aircon1";
                try {
                    client.publish(arduinotopic, message.getBytes(),0,false);
                } catch (MqttException e) {
                    e.printStackTrace();
                }
            }
            else {
                String message = "aircon0";
                try {
                    client.publish(arduinotopic, message.getBytes(),0,false);
                } catch (MqttException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    public View.OnClickListener windowClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(windowStatus.isChecked()) {
                String message = "window1";
                try {
                    client.publish(arduinotopic, message.getBytes(),0,false);
                } catch (MqttException e) {
                    e.printStackTrace();
                }
            }
            else {
                String message = "window0";
                try {
                    client.publish(arduinotopic, message.getBytes(),0,false);
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

    public void arduinoSub() {
        String topic = "arduinostate";
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
                    arduinoSub();

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


