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
import com.google.gson.JsonParser;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.async.parser.JSONObjectParser;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;
import java.util.HashMap;

public class SubmitData1 extends AppCompatActivity {
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Button btNext;
    Spinner spProduk,spKartu,spBranch;
    EditText etMotherName;
    ProgressDialog progressd;
    HashMap<String,String> productList,cardList,branchList;

    void init_data_session(){
        String codeProduct = spProduk.getSelectedItem().toString();
        final String codeCard = spKartu.getSelectedItem().toString();
        String namaIbu = etMotherName.getText().toString();
        String codeBranch = spBranch.getSelectedItem().toString();
        JsonObject json = new JsonObject();
        json.addProperty("productType",productList.get(codeProduct));
        json.addProperty("cardType",cardList.get(codeCard));
        json.addProperty("motherName",namaIbu);
        json.addProperty("branchCode",branchList.get(codeBranch));
        progressd.show();
        Ion.with(this)
                .load("POST","https://oob.mandiriwhatthehack.com/api/submitData")
                .addHeader("Content-Type","application/json")
                .addHeader("Authorization","Bearer " + pref.getString("BEARER_JWT","0"))
                .setJsonObjectBody(json)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        // do stuff with the result or error
                        progressd.dismiss();
                        if (result == null)  Toast.makeText(SubmitData1.this, "Mohon Dicoba Lagi", Toast.LENGTH_SHORT).show();
                        if (result.get("message").getAsString().equals("Submit Success")){
                            Toast.makeText(SubmitData1.this, "Submit Sukses!", Toast.LENGTH_SHORT).show();
                            JsonObject jsonarr = result.getAsJsonObject("data");
                            editor = pref.edit();
                            editor.putString("BEARER_JWT",jsonarr.get("token").getAsString());
                            editor.commit();
                            Intent intent = new Intent(getApplicationContext(), FotoKTPScreen.class);
                            startActivity(intent);
                        }
                        else {
                            Toast.makeText(SubmitData1.this, "Mohon Dicoba Lagi", Toast.LENGTH_SHORT).show();
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
                        else Toast.makeText(SubmitData1.this,"Error! Silahkan Coba Kembali", Toast.LENGTH_SHORT).show();
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
    }

    void init_dob(){
        String temp = getIntent().getExtras().get("jsonlist").toString();
        JsonParser jsonparser = new JsonParser();
        JsonObject json = (JsonObject) jsonparser.parse(temp);
        ArrayList<String> list_card = new ArrayList<String>();
        JsonArray jsonarray = json.get("cardList").getAsJsonArray();
        for(int i = 0 ; i < jsonarray.size(); i++){
            JsonObject jsontemp = jsonarray.get(i).getAsJsonObject();
            list_card.add(jsontemp.get("cardName").getAsString());
            cardList.put(jsontemp.get("cardName").getAsString(),jsontemp.get("cardCode").getAsString());
        }
        jsonarray = json.get("productList").getAsJsonArray();
        ArrayList<String> list_product = new ArrayList<String>();
        for(int i = 0 ; i < jsonarray.size(); i++){
            JsonObject jsontemp = jsonarray.get(i).getAsJsonObject();
            list_product.add(jsontemp.get("productName").getAsString());
            productList.put(jsontemp.get("productName").getAsString(),jsontemp.get("productCode").getAsString());
        }
        jsonarray = json.get("branchList").getAsJsonArray();
        ArrayList<String> list_branch = new ArrayList<String>();
        for(int i = 0 ; i < jsonarray.size(); i++){
            JsonObject jsontemp = jsonarray.get(i).getAsJsonObject();
            list_branch.add(jsontemp.get("branchCode").getAsString()+" - " + jsontemp.get("branchName").getAsString());
            branchList.put(jsontemp.get("branchCode").getAsString()+" - " + jsontemp.get("branchName").getAsString(),jsontemp.get("branchCode").getAsString());
        }
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, list_product);
        spProduk.setAdapter(spinnerArrayAdapter);
        spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, list_card);
        spKartu.setAdapter(spinnerArrayAdapter);
        spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, list_branch);
        spBranch.setAdapter(spinnerArrayAdapter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        progressd = new ProgressDialog(this);
        progressd.setMessage("Loading");
        pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
        editor = pref.edit();
        setContentView(R.layout.submit_data);
        btNext = findViewById(R.id.SbtDta_btn_next);
        spProduk = findViewById(R.id.SbtDta_sp_tabungan);
        spKartu = findViewById(R.id.SbtDta_sp_kartu);
        etMotherName = findViewById(R.id.SbtDta_et_nama_ibu);
        spBranch = findViewById(R.id.SbtDta_sp_cabang_bank);
        productList = new HashMap<>();
        cardList = new HashMap<>();
        branchList = new HashMap<>();
        init_dob();
        set_btnext_click();

    }
}
