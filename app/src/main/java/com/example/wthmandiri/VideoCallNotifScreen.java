package com.example.wthmandiri;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class VideoCallNotifScreen extends AppCompatActivity {
    static final int REQUEST_IMAGE_CAPTURE = 1;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Button btNext,btVideoCall;
    TextView callReff;
    ProgressDialog progressd;
    void init_data_session(){
        progressd.show();
        Ion.with(this)
                .load("POST","https://oob.mandiriwhatthehack.com/api/createAccount")
                .addHeader("Content-Type","application/json")
                .addHeader("Authorization","Bearer " + pref.getString("BEARER_JWT","0"))
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        // do stuff with the result or error
                        progressd.dismiss();
                        if (result == null)  Toast.makeText(VideoCallNotifScreen.this, "Mohon Dicoba Lagi", Toast.LENGTH_SHORT).show();
                        else if (!result.has("message") || result.get("message").isJsonNull()){
                            Toast.makeText(VideoCallNotifScreen.this, "Error : Response : " + result.toString(), Toast.LENGTH_SHORT).show();
                        }
                        else if (result.get("message").getAsString().equals("Success")){
                            Toast.makeText(VideoCallNotifScreen.this, "Submit Sukses!", Toast.LENGTH_SHORT).show();
                            editor.putString("no_account",result.get("data").getAsJsonObject().get("data").getAsJsonObject().get("accountNumber").getAsString());
                            editor.commit();
                            Intent intent = new Intent(getApplicationContext(), SuccessScreen.class);
                            startActivity(intent);
                        }
                        else {
                            Toast.makeText(VideoCallNotifScreen.this, "Mohon Dicoba Lagi, Belum Diverifikasi!", Toast.LENGTH_SHORT).show();
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
                        }
                        else Toast.makeText(VideoCallNotifScreen.this,"Error! Silahkan Coba Kembali", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    void set_btnext_click(){
        btVideoCall.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub
               // callJwtTokenResult();
                init_data_session();
            }
        });
        btNext.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(getApplicationContext(), WVVidCall.class);
                startActivity(intent);
                // callJwtTokenResult();\
            }
        });
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        progressd = new ProgressDialog(this);
        progressd.setMessage("Loading");
        pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
        editor = pref.edit();
        setContentView(R.layout.videocall_layout);
        btNext = findViewById(R.id.videocallguide_btn_next);
        btVideoCall = findViewById(R.id.videocallguide_btn_opencam);
        callReff = findViewById(R.id.videocallguide_tv_callReff);
        callReff.setText(pref.getString("callReff","ABCD3"));
        set_btnext_click();

    }
}

