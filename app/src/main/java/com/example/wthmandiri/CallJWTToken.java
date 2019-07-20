package com.example.wthmandiri;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

public class CallJWTToken extends AppCompatActivity {
    public void callJwtTokenResult(){
        Ion.with(this)
                .load("https://apigateway.mandiriwhatthehack.com/rest/pub/apigateway/jwt/getJsonWebToken?app_id=765a5229-625a-467e-bcc9-b9fc27a90992")
                .basicAuthentication("4fd0686c-122b-4a8d-b507-bf85c066e115","857b9c50-58d3-4398-a8b3-4d5bece8cc86")
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        // do stuff with the result or error
                        if (!result.isEmpty()){
                            String htmlresult = result.substring(result.indexOf("eyJraWQ"),result.lastIndexOf("</TD>"));
                        }
                    }
                });
    }
}
