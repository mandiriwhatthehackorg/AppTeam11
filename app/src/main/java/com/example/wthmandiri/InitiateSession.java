package com.example.wthmandiri;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;

public class InitiateSession extends AppCompatActivity {
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Spinner spTgl,spBln,spThn;
    Button btNext;
    EditText etEmail,etNik,etPhoneNumber,etDob;
    ProgressDialog progressd;
    void resend_otp(){
        JsonObject json = new JsonObject();
        final String txtNIK = etNik.getText().toString();
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
                        if (result == null)  Toast.makeText(InitiateSession.this, "Mohon Dicoba kembali", Toast.LENGTH_SHORT).show();
                        else if (result.get("message").getAsString().equals("OTP Send successfully")){
                            Toast.makeText(InitiateSession.this, "Resend OTP sukses!", Toast.LENGTH_SHORT).show();
                            JsonObject jsonarr = result.getAsJsonObject("data");
                            editor = pref.edit();
                            editor.putString("BEARER_JWT",jsonarr.get("token").getAsString());
                            editor.putString("nik_temp",txtNIK);
                            editor.commit();
                            Intent intent = new Intent(getApplicationContext(), OTPScreen.class);
                            startActivity(intent);
                        }
                        else {
                            Toast.makeText(InitiateSession.this, "Mohon dicoba kembali", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    void init_data_session(){
        String txtEmail = etEmail.getText().toString();
        final String txtNIK = etNik.getText().toString();
        String txtHP = etPhoneNumber.getText().toString();
        String txtDoB = spThn.getSelectedItem().toString() + "-" + spBln.getSelectedItem().toString() + "-" + spTgl.getSelectedItem().toString();
        JsonObject json = new JsonObject();
        json.addProperty("email",txtEmail);
        json.addProperty("nik",txtNIK);
        json.addProperty("phone",txtHP);
        json.addProperty("ttl",txtDoB);
        progressd.show();
        Ion.with(this)
                .load("POST","https://oob.mandiriwhatthehack.com/api/initiate/createSession")
                .addHeader("Content-Type","application/json")
                .setJsonObjectBody(json)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        // do stuff with the result or error
                        progressd.dismiss();
                        if (result == null)  Toast.makeText(InitiateSession.this, "Mohon Dicoba Lagi", Toast.LENGTH_SHORT).show();
                        else if (result.get("message").getAsString().equals("initiate success")){
                            Toast.makeText(InitiateSession.this, "Initiate Sukses!", Toast.LENGTH_SHORT).show();
                            JsonObject jsonarr = result.getAsJsonObject("data");
                            editor = pref.edit();
                            editor.putString("BEARER_JWT",jsonarr.get("token").getAsString());
                            editor.putString("nik_temp",txtNIK);
                            editor.commit();
                            Intent intent = new Intent(getApplicationContext(), OTPScreen.class);
                            startActivity(intent);
                        }
                        else if (result.get("message").getAsString().equals("There is an Active Process")){
                            resend_otp();
                        }
                        else {
                            Toast.makeText(InitiateSession.this, "Mohon Dicoba Lagi", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    public void callJwtTokenResult(){
        progressd.show();
        Ion.with(this)
                .load("GET","http://apigateway.mandiriwhatthehack.com/rest/pub/apigateway/jwt/getJsonWebToken?app_id=765a5229-625a-467e-bcc9-b9fc27a90992")
                .basicAuthentication("4fd0686c-122b-4a8d-b507-bf85c066e115","857b9c50-58d3-4398-a8b3-4d5bece8cc86")
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        progressd.dismiss();
                        // do stuff with the result or error
                        if (!(result == null)){
                            String htmlresult = result.substring(result.indexOf("eyJraWQ"),result.lastIndexOf("</TD>"));
                            //Toast.makeText(InitiateSession.this, htmlresult, Toast.LENGTH_SHORT).show();
                            editor.putString("BEARER_JWT",htmlresult);
                            editor.commit();
                            init_data_session();
                        }
                        else Toast.makeText(InitiateSession.this,"Error! Silahkan Coba Kembali", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    void set_btnext_click(){
        btNext.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub
                callJwtTokenResult();
            }
        });
    }

    void init_dob(){
        ArrayList<String> list_tgl = new ArrayList<String>();
        for(int i = 1 ; i <= 31; i++){
            list_tgl.add(String.valueOf(i));
        }
        ArrayList<String> list_bulan = new ArrayList<String>();
        for(int i = 1 ; i <= 12; i++){
            list_bulan.add(String.valueOf(i));
        }
        ArrayList<String> list_tahun = new ArrayList<String>();
        for(int i = 2000 ; i >= 1950; i--){
            list_tahun.add(String.valueOf(i));
        }
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, list_tgl);
        spTgl.setAdapter(spinnerArrayAdapter);
        spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, list_bulan);
        spBln.setAdapter(spinnerArrayAdapter);
        spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, list_tahun);
        spThn.setAdapter(spinnerArrayAdapter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progressd = new ProgressDialog(this);
        progressd.setMessage("Loading");
        pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
        editor = pref.edit();
        setContentView(R.layout.initiate_session);
        spTgl = findViewById(R.id.spinner_tgl_lahir);
        spBln = findViewById(R.id.spinner_bln_lahir);
        spThn = findViewById(R.id.spinner_thn_lahir);
        btNext = findViewById(R.id.initSess_btn_next);
        etEmail = findViewById(R.id.initSess_et_email);
        etNik = findViewById(R.id.initSess_et_nik);
        etPhoneNumber = findViewById(R.id.initSess_et_nomor_hp);
        init_dob();
        set_btnext_click();

    }
}
