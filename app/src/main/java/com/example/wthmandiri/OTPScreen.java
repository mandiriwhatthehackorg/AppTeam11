package com.example.wthmandiri;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

public class OTPScreen extends AppCompatActivity {
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    TextView tvOnboarding;
    Button btResend,btVerif;
    EditText etOtp1,etOtp2,etOtp3,etOtp4,etOtp5,etOtp6;
    ProgressDialog progressd;
    void resend_otp(){
        JsonObject json = new JsonObject();
        String txtNIK = pref.getString("nik_temp","0");
        json.addProperty("nik",txtNIK);
        progressd.show();
        Ion.with(this)
                .load("POST","https://oob.mandiriwhatthehack.com/api/initiate/resendOTP")
                .addHeader("Content-Type","application/json")
                .setJsonObjectBody(json)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        // do stuff with the result or error
                        progressd.dismiss();
                        if (result == null)  Toast.makeText(OTPScreen.this, "Mohon Dicoba kembali", Toast.LENGTH_SHORT).show();
                        else if (result.get("message").getAsString().equals("OTP Send successfully")){
                            Toast.makeText(OTPScreen.this, "Resend OTP sukses!", Toast.LENGTH_SHORT).show();
                            JsonObject jsonarr = result.getAsJsonObject("data");
                            editor = pref.edit();
                            editor.putString("BEARER_JWT",jsonarr.get("token").getAsString());
                            editor.commit();
                        }
                        else {
                            Toast.makeText(OTPScreen.this, "Mohon dicoba kembali", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    void verify_otp(){
        JsonObject json = new JsonObject();
        String otptxt = etOtp1.getText().toString()+etOtp2.getText().toString()+etOtp3.getText().toString()+etOtp4.getText().toString()+etOtp5.getText().toString()+etOtp6.getText().toString();
        json.addProperty("otp",otptxt);
        progressd.show();
        Ion.with(this)
                .load("POST","https://oob.mandiriwhatthehack.com/api/initiate/validateOTP")
                .addHeader("Content-Type","application/json")
                .addHeader("Authorization","Bearer "+pref.getString("BEARER_JWT","0"))
                .setJsonObjectBody(json)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        // do stuff with the result or error
                        progressd.dismiss();
                        if (result == null)  Toast.makeText(OTPScreen.this, "Mohon Dicoba kembali", Toast.LENGTH_SHORT).show();
                        else if (!result.has("message")){
                            Toast.makeText(OTPScreen.this, "Mohon dicoba kembali! error : " + result.get("message").getAsString(), Toast.LENGTH_SHORT).show();
                        }
                        else if (result.get("message").getAsString().equals("OTP confirmed")){
                            Toast.makeText(OTPScreen.this, "Verifikasi OTP Sukses!", Toast.LENGTH_SHORT).show();
                            JsonObject jsonarr = result.getAsJsonObject("data");
                            editor = pref.edit();
                            editor.putString("BEARER_JWT",jsonarr.get("token").getAsString());
                            editor.commit();
                            Intent intent = new Intent(getApplicationContext(), SubmitData1.class);
                            intent.putExtra("jsonlist",jsonarr.getAsJsonObject("data").toString());
                            startActivity(intent);
                        }
                        else {
                            Toast.makeText(OTPScreen.this, "Mohon dicoba kembali! error : " + result.get("message").getAsString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    void set_btn_listener(){
        btResend.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub
                resend_otp();
            }
        });
        btVerif.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub
                verify_otp();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        progressd = new ProgressDialog(this);
        progressd.setMessage("Loading");
        pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
        editor = pref.edit();
        setContentView(R.layout.otp_screen);
        etOtp1 = findViewById(R.id.etOtp1);
        etOtp2 = findViewById(R.id.etOtp2);
        etOtp3 = findViewById(R.id.etOtp3);
        etOtp4 = findViewById(R.id.etOtp4);
        etOtp5 = findViewById(R.id.etOtp5);
        etOtp6 = findViewById(R.id.etOtp6);
        btResend = findViewById(R.id.otpScr_resend);
        btVerif = findViewById(R.id.otpScr_continue);
        etOtp1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String text = editable.toString();
                if (text.isEmpty()) etOtp1.requestFocus();
                else etOtp2.requestFocus();
            }
        });
        etOtp2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String text = editable.toString();
                if (text.isEmpty()) etOtp1.requestFocus();
                else etOtp3.requestFocus();
            }
        });
        etOtp3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String text = editable.toString();
                if (text.isEmpty()) etOtp2.requestFocus();
                else etOtp4.requestFocus();
            }
        });
        etOtp4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String text = editable.toString();
                if (text.isEmpty()) etOtp3.requestFocus();
                else etOtp5.requestFocus();
            }
        });
        etOtp5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String text = editable.toString();
                if (text.isEmpty()) etOtp4.requestFocus();
                else etOtp6.requestFocus();
            }
        });
        etOtp6.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String text = editable.toString();
                if (text.isEmpty()) etOtp5.requestFocus();
                else etOtp6.requestFocus();
            }
        });
        set_btn_listener();
    }
}
