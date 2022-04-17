package com.example.firsttestapp;

import android.os.AsyncTask;
import android.util.Log;

import fr.bmartel.speedtest.SpeedTestReport;
import fr.bmartel.speedtest.SpeedTestSocket;
import fr.bmartel.speedtest.inter.ISpeedTestListener;
import fr.bmartel.speedtest.model.SpeedTestError;

public class SpeedTestTask extends AsyncTask<Void, Void, String> {

    private int x =0;
    @Override
    protected String doInBackground(Void... params) {

        SpeedTestSocket speedTestSocket = new SpeedTestSocket();


        // add a listener to wait for speedtest completion and progress
        speedTestSocket.addSpeedTestListener(new ISpeedTestListener() {

            @Override
            public void onCompletion(SpeedTestReport report) {
                // called when download/upload is finished
                Log.v("speedtest", "[COMPLETED] rate in octet/s : " + report.getTransferRateOctet());
                Log.v("speedtest", "[COMPLETED] rate in bit/s   : " + report.getTransferRateBit());
                System.out.println("X= "+x);
            }

            @Override
            public void onError(SpeedTestError speedTestError, String errorMessage) {
                // called when a download/upload error occur
            }

            @Override
            public void onProgress(float percent, SpeedTestReport report) {
                // called to notify download/upload progress
                x++;
//                Log.v("speedtest", "[PROGRESS] progress : " + percent + "%");
//                Log.v("speedtest", "[PROGRESS] rate in octet/s : " + report.getTransferRateOctet());
//                Log.v("speedtest", "[PROGRESS] rate in bit/s   : " + report.getTransferRateBit());
            }
        });

        speedTestSocket.startDownload("http://ipv4.ikoula.testdebit.info/1M.iso");
//        speedTestSocket.startFixedDownload("http://ipv4.ikoula.testdebit.info/10M.iso", 1000);

        return null;
    }
}
