package com.soft.transport;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.soft.transport.constant.Common;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    // https://icon-icons.com/es/icono/vuelo-aeroplano-avion/30822
    WebView webView;
    String webViewURL = "";
    private ProgressDialog progressBar;

    String imei = "NONE";
    String strPhoneType;
    String info;
    static final int PERMISSION_READ_STATE = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //by
        getIMEI();
        if(null == this.imei){
           this.imei =  getSerialNumber()+Common.SERIAL_NUMBER;
        }
        webView = (WebView) findViewById(R.id.webview);
        webViewURL = Common.URL_MAIN;
        Map<String, String> extraHeaders = new HashMap<String, String>();//header
        extraHeaders.put(Common.AUTHORIZATION_HEADER,Common.BEARER);//header
        extraHeaders.put(Common.TOKEN_ID_HEADER,imei+Common.CODE_TRANSOFT);//header

        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        webView.setWebChromeClient(new WebChromeClient());// car for enable javascript on the html
        webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);

        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();

        progressBar = ProgressDialog.show(MainActivity.this, getString(R.string.progress_please_wait), getString(R.string.progress_loading));

        webView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            public void onPageFinished(WebView view, String url) {
                if (progressBar.isShowing()) {
                    progressBar.dismiss();
                }
            }

            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Toast.makeText(getApplicationContext(), "Oh no! " + description, Toast.LENGTH_SHORT).show();
                alertDialog.setTitle("Error");
                alertDialog.setMessage(description);
                alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        return;
                    }
                });
                alertDialog.show();
            }
        });
        webView.loadUrl(webViewURL, extraHeaders);//header
    }

    public void getIMEI() {
        int permission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);
        if (permission == PackageManager.PERMISSION_GRANTED) {
            MyTelephonyManager();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_PHONE_STATE},
                    PERMISSION_READ_STATE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permission, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permission, grantResults);
        switch (requestCode) {
            case PERMISSION_READ_STATE: {
                if (grantResults.length >= 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    MyTelephonyManager();
                } else {
                    Toast.makeText(this,
                            "You don't have required permission to make the Action",
                            Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void MyTelephonyManager() {
        TelephonyManager manager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        int phoneType = manager.getPhoneType();
        switch (phoneType) {
            case (TelephonyManager.PHONE_TYPE_CDMA):
                strPhoneType = "CDMA";
                break;
            case (TelephonyManager.PHONE_TYPE_GSM):
                strPhoneType = "GSM";
                break;
            case (TelephonyManager.PHONE_TYPE_NONE):
                strPhoneType = "NONE";
                break;
        }
        boolean isRoaming = manager.isNetworkRoaming();

        String phoneType_ =   strPhoneType;
        String imeiNumber = manager.getDeviceId();
        String subscriberId = manager.getDeviceId();
        String simSerialNumber = manager.getSimSerialNumber();
        String networkCountryISO = manager.getNetworkCountryIso();
        String simCountryISO = manager.getSimCountryIso();
        String softwareVersion = manager.getDeviceSoftwareVersion();
        String voiceMailNumber = manager.getVoiceMailNumber();

        info = "\nPhone Details: \n";
        info += "\n Phone Network Type: "+phoneType_;
        info += "\n IMEI Number: "+imeiNumber;
        info += "\n SubscriberID: "+subscriberId;
        info += "\n Sim serial number: "+simSerialNumber;
        info += "\n Network country iso: "+networkCountryISO;
        info += "\n sim country iso: "+simCountryISO;
        info += "\n software version: "+softwareVersion;
        info += "\n voice mail number: "+voiceMailNumber;
        info += "\n in roaming: "+isRoaming ;
        imei = imeiNumber;
        if(null == imei){
            imei = simSerialNumber;
        }
        System.out.println("\n-----------log--------- imei >> "+ imei);
        System.out.println("\n-----------inf--------- info >> "+ info);
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            finish();
        }
    }

    public static String getSerialNumber() {
        String serialNumber;

        try {
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method get = c.getMethod("get", String.class);

            serialNumber = (String) get.invoke(c, "gsm.sn1");
            if (serialNumber.equals(""))
                serialNumber = (String) get.invoke(c, "ril.serialnumber");
            if (serialNumber.equals(""))
                serialNumber = (String) get.invoke(c, "ro.serialno");
            if (serialNumber.equals(""))
                serialNumber = (String) get.invoke(c, "sys.serialnumber");
            if (serialNumber.equals(""))
                serialNumber = Build.SERIAL;

            // If none of the methods above worked
            if (serialNumber.equals(""))
                serialNumber = null;
        } catch (Exception e) {
            e.printStackTrace();
            serialNumber = null;
        }

        return serialNumber;
    }
}
