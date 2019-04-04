package com.soft.demoimei;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {

    Button button;
    TextView text;
    String strPhoneType;
    String info;
    static final int PERMISSION_READ_STATE = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void Start(View view) {
        System.out.println("--------------car- > ");
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

        info = "Phone Details: \n";
        info += "\n Phone Network Type: "+phoneType_;
        info += "\n IMEI Number: "+imeiNumber;
        info += "\n SubscriberID: "+subscriberId;
        info += "\n Sim serial number: "+simSerialNumber;
        info += "\n Network country iso: "+networkCountryISO;
        info += "\n sim country iso: "+simCountryISO;
        info += "\n software version: "+softwareVersion;
        info += "\n voice mail number: "+voiceMailNumber;
        info += "\n in roaming: "+isRoaming ;

        System.out.println("-------------- :::: >>> "+info);
        button = (Button) findViewById(R.id.idBtnStart);
        text = (TextView) findViewById(R.id.idTxtView);
        text.setText(info);
    }
}
