package com.example.firsttestapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.CellIdentityGsm;
import android.telephony.CellIdentityLte;
import android.telephony.CellIdentityWcdma;
import android.telephony.CellInfo;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoWcdma;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class CellIDwithLocation {


    //Initialize variable

    public String cellID;
    private MainActivity act;

   // public String get_cellID() { return cellID; }

    private static final String TAG ="CellIDwithLocation";
    //Button btLocation;

    FusedLocationProviderClient fusedLocationProviderClient;

    TelephonyManager telephonyManager;
/*
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
*/
    CellIDwithLocation(MainActivity main){
        //Assign variable
        this.act = main;
        /*btLocation = findViewById(R.id.bt_Location);
        textView1 = findViewById(R.id.text_view1);
        textView2 = findViewById(R.id.text_view2);
        textView3 = findViewById(R.id.text_view3);
        textView4 = findViewById(R.id.text_view4);
        textView5 = findViewById(R.id.text_view5);
        textView6 = findViewById(R.id.text_view6);
        textView7 = findViewById(R.id.text_view7);
        textView8 = findViewById(R.id.text_view8);
        textView9 = findViewById(R.id.text_view9);
        textView10 = findViewById(R.id.text_view10);
        textView11 = findViewById(R.id.text_view11);
        textView12 = findViewById(R.id.text_view12);
        textView13 = findViewById(R.id.text_view13);
        textView14 = findViewById(R.id.text_view14);
        textView15 = findViewById(R.id.text_view15);
        textView16 = findViewById(R.id.text_view16);
        textView17 = findViewById(R.id.text_view17);
*/
        //Initialize fusedLocationProviderClient
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(act);

        //btLocation.setOnClickListener(new View.OnClickListener() {
           // @RequiresApi(api = Build.VERSION_CODES.Q)
          //  @Override
          //  public void permission() {
        System.out.println("before permission");
                //Check permission
                if (ActivityCompat.checkSelfPermission(act,
                        Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(act,
                                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(act,
                                Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED){
                    //When permission granted
                    getLocation();
                    //this.cellID = this.getCellID();
                    System.out.println("permission");
                    System.out.println(getCellID());
                }else {
                    //When permission denied
                    ActivityCompat.requestPermissions(act,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},44);
                    ActivityCompat.requestPermissions(act,
                            new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},44);
                    ActivityCompat.requestPermissions(act,
                            new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION},44);
                }
            }
       // });

 //   }

    @SuppressLint({"SetTextI18n", "MissingPermission"})
    private String getCellID() {
        telephonyManager = (TelephonyManager) act.getSystemService(Context.TELEPHONY_SERVICE);
        LocationManager locationManager = (LocationManager) act.getSystemService(Context.LOCATION_SERVICE);
        String provider = LocationManager.GPS_PROVIDER;
        //locationManager.requestLocationUpdates(provider, 400, 1, (android.location.LocationListener) this);
        List<CellInfo> cellInfoList = null;
        cellInfoList = telephonyManager.getAllCellInfo();
        if (cellInfoList == null) {
            return "getAllCellInfo() is null";
        } else if (cellInfoList.size() == 0) {
            return "no info";
        } else {
            int cellNumber = cellInfoList.size();
            BaseStation main_BS = bindData(cellInfoList.get(0));
            for (CellInfo cellInfo : cellInfoList) {
                BaseStation bs = bindData(cellInfo);
                Log.i(TAG, bs.toString());
            }
            //return "test1";
            return cellNumber + "\n" + main_BS.toString();

        }

    }

    private BaseStation bindData(CellInfo cellInfo) {
        BaseStation baseStation = null;
        // 2G，3G，4G
        if (cellInfo instanceof CellInfoWcdma) {
            //3G
            CellInfoWcdma cellInfoWcdma = (CellInfoWcdma) cellInfo;
            CellIdentityWcdma cellIdentityWcdma = cellInfoWcdma.getCellIdentity();
            baseStation = new BaseStation();
            baseStation.setType("WCDMA");
            baseStation.setCid(cellIdentityWcdma.getCid());
            baseStation.setLac(cellIdentityWcdma.getLac());
            baseStation.setMcc(cellIdentityWcdma.getMcc());
            baseStation.setMnc(cellIdentityWcdma.getMnc());
            baseStation.setBsic_psc_pci(cellIdentityWcdma.getPsc());
            if (cellInfoWcdma.getCellSignalStrength() != null) {
                baseStation.setAsuLevel(cellInfoWcdma.getCellSignalStrength().getAsuLevel()); //Get the signal level as an asu value between 0..31, 99 is unknown Asu is calculated based on 3GPP RSRP.
                baseStation.setSignalLevel(cellInfoWcdma.getCellSignalStrength().getLevel()); //Get signal level as an int from 0..4
                baseStation.setDbm(cellInfoWcdma.getCellSignalStrength().getDbm()); //Get the signal strength as dBm
            }
        } else if (cellInfo instanceof CellInfoLte) {
            //4G
            CellInfoLte cellInfoLte = (CellInfoLte) cellInfo;
            CellIdentityLte cellIdentityLte = cellInfoLte.getCellIdentity();
            baseStation = new BaseStation();
            baseStation.setType("LTE");
            baseStation.setCid(cellIdentityLte.getCi());
            baseStation.setMnc(cellIdentityLte.getMnc());
            baseStation.setMcc(cellIdentityLte.getMcc());
            baseStation.setLac(cellIdentityLte.getTac());
            baseStation.setBsic_psc_pci(cellIdentityLte.getPci());
            if (cellInfoLte.getCellSignalStrength() != null) {
                baseStation.setAsuLevel(cellInfoLte.getCellSignalStrength().getAsuLevel());
                baseStation.setSignalLevel(cellInfoLte.getCellSignalStrength().getLevel());
                baseStation.setDbm(cellInfoLte.getCellSignalStrength().getDbm());
            }
        } else if (cellInfo instanceof CellInfoGsm) {
            //2G
            CellInfoGsm cellInfoGsm = (CellInfoGsm) cellInfo;
            CellIdentityGsm cellIdentityGsm = cellInfoGsm.getCellIdentity();
            baseStation = new BaseStation();
            baseStation.setType("GSM");
            baseStation.setCid(cellIdentityGsm.getCid());
            baseStation.setLac(cellIdentityGsm.getLac());
            baseStation.setMcc(cellIdentityGsm.getMcc());
            baseStation.setMnc(cellIdentityGsm.getMnc());
            baseStation.setBsic_psc_pci(cellIdentityGsm.getPsc());
            if (cellInfoGsm.getCellSignalStrength() != null) {
                baseStation.setAsuLevel(cellInfoGsm.getCellSignalStrength().getAsuLevel());
                baseStation.setSignalLevel(cellInfoGsm.getCellSignalStrength().getLevel());
                baseStation.setDbm(cellInfoGsm.getCellSignalStrength().getDbm());
            }
        } else {
            //2/3G
            Log.e(TAG, "CDMA CellInfo................................................");
        }
        return baseStation;
    }

    @SuppressLint("MissingPermission")
    private void getLocation() {
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                //Initialize location
                Location location = task.getResult();
                if (location != null){
                    try {
                        //Initialize geoCoder
                        Geocoder geocoder = new Geocoder(act,
                                Locale.getDefault());
                        //Initialize address list
                        List<Address> addresses = geocoder.getFromLocation(
                                location.getLatitude(), location.getLongitude(), 1
                        );


                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}
