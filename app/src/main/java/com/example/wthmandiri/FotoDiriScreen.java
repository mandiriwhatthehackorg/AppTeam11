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
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class FotoDiriScreen extends AppCompatActivity {
    static final int REQUEST_IMAGE_CAPTURE = 1;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Button btNext,btTakePhoto;
    ImageView imKtp;
    ProgressDialog progressd;
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }
    void init_data_session(){
        saveImage();
        File root = new File(Environment.getExternalStorageDirectory()
                + File.separator + "WTHMandiri" + File.separator);
        root.mkdirs();
        File sdImageMainDirectory = new File(root, "savedPhoto.jpg");
        progressd.show();
        Ion.with(this)
                .load("POST","https://oob.mandiriwhatthehack.com/api/submitImageSelfie")
             //   .addHeader("Content-Type","multipart/form-data")
                .addHeader("Authorization","Bearer " + pref.getString("BEARER_JWT","0"))
                .setMultipartFile("file","application/json",sdImageMainDirectory)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        // do stuff with the result or error
                        progressd.dismiss();
                        if (result == null)  Toast.makeText(FotoDiriScreen.this, "Mohon Dicoba Lagi", Toast.LENGTH_SHORT).show();
                        if (result.get("message").getAsString().equals("Selfie Image Stored")){
                            Toast.makeText(FotoDiriScreen.this, "Submit Sukses!", Toast.LENGTH_SHORT).show();
                            JsonObject jsonarr = result.getAsJsonObject("data");
                            editor = pref.edit();
                            editor.putString("BEARER_JWT",jsonarr.get("token").getAsString());
                            editor.commit();
                            Intent intent = new Intent(getApplicationContext(), FotoSignatureScreen.class);
                            startActivity(intent);
                        }
                        else {
                            Toast.makeText(FotoDiriScreen.this, "Mohon Dicoba Lagi, error : " + result.get("message").getAsString(), Toast.LENGTH_SHORT).show();
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
                        else Toast.makeText(FotoDiriScreen.this,"Error! Silahkan Coba Kembali", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    void set_btnext_click(){
        btNext.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub
               // callJwtTokenResult();
                init_data_session();
            }
        });
        btTakePhoto.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub
                // callJwtTokenResult();
                dispatchTakePictureIntent();
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
        setContentView(R.layout.fotodiri_layout);
        btNext = findViewById(R.id.fotodiri_btn_next);
        btTakePhoto = findViewById(R.id.fotodiri_btn_opencam);
        imKtp = findViewById(R.id.fotodiri_iv_ktp);
        set_btnext_click();

    }
    void saveImage(){
        imKtp.buildDrawingCache();
        Bitmap bm= imKtp.getDrawingCache();
        OutputStream fOut = null;
        Uri outputFileUri;
        try {
            File root = new File(Environment.getExternalStorageDirectory()
                    + File.separator + "WTHMandiri" + File.separator);
            root.mkdirs();
            File sdImageMainDirectory = new File(root, "savedPhoto.jpg");
            fOut = new FileOutputStream(sdImageMainDirectory);
        } catch (Exception e) {
            Toast.makeText(this, "Error occured. Please try again later.",
                    Toast.LENGTH_SHORT).show();
        }
        try {
            bm.compress(Bitmap.CompressFormat.JPEG, 2, fOut);
            fOut.flush();
            fOut.close();
        } catch (Exception e) {
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            imKtp.setImageBitmap(imageBitmap);
        }
    }
}

