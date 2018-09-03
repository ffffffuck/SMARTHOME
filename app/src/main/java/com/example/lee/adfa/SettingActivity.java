package com.example.lee.adfa;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.lee.adfa.preference.AddressPreference;

public class SettingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        AddressPreference ap = new AddressPreference(getBaseContext());

        String ip = ap.getIP();
        int port = ap.getPort();

        EditText ipEditText = findViewById(R.id.idEditText);
        ipEditText.setText(ip);

        EditText portEditText = findViewById(R.id.portEditText);
        String porttext = Integer.toString(port);
        portEditText.setText(porttext);


        Button saveButton = findViewById(R.id.saveButton);
        saveButton.setOnClickListener(onsaveClickListener);

    }

    private View.OnClickListener onsaveClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            EditText ipEditText = findViewById(R.id.idEditText);
            String ip = ipEditText.getText().toString();

            EditText portEditText = findViewById(R.id.portEditText);
            String porttext = portEditText.getText().toString();
            int port = Integer.valueOf(porttext);

            AddressPreference ap = new AddressPreference(getBaseContext());
            ap.putIp(ip);
            ap.putPort(port);

            Toast.makeText(getBaseContext(), ip+" : "+port + "이(가) 저장되었습니다.", Toast.LENGTH_SHORT).show();

            finish();
        }
   };
}