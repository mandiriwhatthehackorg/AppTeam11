package com.example.wthmandiri;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.webkit.CookieManager;
import android.webkit.PermissionRequest;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

public class WVVidCall extends AppCompatActivity {
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wvvidcal);
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        WebView webView = (WebView) findViewById(R.id.webView1);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setMediaPlaybackRequiresUserGesture(false);
        webView.getSettings().setPluginState(WebSettings.PluginState.ON);
        webView.loadUrl("https://appr.tc/r/"+pref.getString("callReff","WTH2019"));
        webView.setWebChromeClient(new WebChromeClient() { @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                                   public void onPermissionRequest(final PermissionRequest request) { request.grant(request.getResources()); }});
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptThirdPartyCookies(webView, true);


    }
}
