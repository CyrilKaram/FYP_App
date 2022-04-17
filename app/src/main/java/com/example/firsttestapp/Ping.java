package com.example.firsttestapp;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;

import androidx.annotation.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.util.concurrent.Executor;


public class Ping extends AsyncTask<Void, Void, String> {

    private StringBuffer res = new StringBuffer();

    @Override
    protected String doInBackground(Void... voids) {
        System.out.println("Inside " +Thread.currentThread());
        Runtime runtime = Runtime.getRuntime();
//                    String inputLine = "";
//                    String res = "";

        try {
            System.out.println("Start");
//                        Process ipProcess = runtime.exec("/system/bin/iperf -c iperf.par2.as49434.net");
            Process ipProcess = runtime.exec("/system/bin/ping -c 3 8.8.8.8");
//                        Process ipProcess = runtime.exec(" ls /system/bin");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(ipProcess.getInputStream()));

//                        inputLine = bufferedReader.readLine();
//                        while ((inputLine != null)) {
//                            if (inputLine.length() > 0 && inputLine.contains("avg")) {  // when we get to the last line of executed ping command
//                                break;
//                            }
//                            res = res + inputLine + "\n";
//                            inputLine = bufferedReader.readLine();
//                        }
            String line = "";
            while ((line = bufferedReader.readLine())!= null) {
                res.append(line + "\n");
            }
            System.out.println("Res: "+res);
            int exitValue = ipProcess.waitFor();
            ipProcess.destroy();
//                    Toast.makeText(getActivity().getApplicationContext(),exitValue, Toast.LENGTH_LONG).show();
            System.out.println("ExitValue: "+exitValue);
            if(exitValue == 0){
                // Success
                System.out.println("Reachable");
//                            Toast.makeText(getApplicationContext(),"Reachable", Toast.LENGTH_LONG).show();
            } else {
                // Failure
                System.out.println("Unreachable");
//                            Toast.makeText(getApplicationContext(),"Unreachable", Toast.LENGTH_LONG).show();
//                            text.setText("Unreachable");
                FirstFragment conv = new FirstFragment();
//                            conv.setText("Unreachable");
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            System.out.println("Error");
        }

        return res.toString();
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
    }
}


