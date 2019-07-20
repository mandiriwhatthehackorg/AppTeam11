package com.example.wthmandiri;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SuccessScreen extends AppCompatActivity {
    TextView tvOnboarding;
    Button btTest;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        setContentView(R.layout.createsuccess);
        btTest = findViewById(R.id.crtsuccess_btn_next);
        tvOnboarding = findViewById(R.id.crtsuccess_desc_title);
        tvOnboarding.setText(tvOnboarding.getText().toString()+"\n"+"Nomor Rekening Anda : " + pref.getString("no_account","123"));
        btTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });
    }
}
