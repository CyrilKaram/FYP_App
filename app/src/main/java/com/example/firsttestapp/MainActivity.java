package com.example.firsttestapp;
// Au Revoir
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.telephony.TelephonyManager;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.firsttestapp.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import androidx.annotation.Nullable;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.ListenableWorker;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import fr.bmartel.speedtest.SpeedTestReport;
import fr.bmartel.speedtest.SpeedTestSocket;
import fr.bmartel.speedtest.SpeedTestTask;
import fr.bmartel.speedtest.inter.ISpeedTestListener;
import fr.bmartel.speedtest.model.SpeedTestError;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    private ItemViewModel viewModel;
    private TextView textres;
    ExecutorService executorService = Executors.newFixedThreadPool(4);
    public static final String ACTION_DATA_ROAMING_SETTINGS = "android.settings.DATA_ROAMING_SETTINGS";
    Constraints constraints = new Constraints.Builder()
            .setRequiresBatteryNotLow(true)
            .build();
    final PeriodicWorkRequest workRequest = new PeriodicWorkRequest.
            Builder(MyWorker.class,15, TimeUnit.MINUTES).build();

    // CRITERIA
    private double latency = 0;
    private double throughput =0;
    private double jitter = 0;
    private double loss = 0;
    private double[] battery = {0.9,0.8,0.7};

    // RL PARAMETERS
    ArrayList<State> state_list = new ArrayList<State>();
    ArrayList<QLearning> learner_list = new ArrayList<QLearning>();
    private int ID = 1;
    Integer[] action_list = {1,2,3};
    String[] action_names = {"2G","3G","4G"};
    private int current_time;
    private int current_location;
    private int current_scenario;
    State current_state;
    int chosen_action;
    double[][] weights = {
            {0.412426357, 0.179835921, 0.089147185, 0.225188033, 0.093402504},
            {0.168448384, 0.555575253, 0.04742412, 0.163219626, 0.065332617},
            {0.373626374, 0.040934066, 0.19514652, 0.19514652, 0.19514652},
            {0.263947285, 0.052846664, 0.12804264, 0.438119634, 0.117043777}
    };
    Boolean settings_check = false;

    CellIDwithLocation cellIDwithLocation;
    String CellID;

    //Bonjour tout le monde
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        textres = (TextView) findViewById(R.id.textview_first);

//        text = (TextView) getFragmentManager().findFragmentById(R.id.FirstFragment).find;
        ////////////////////////////////////////
        viewModel = new ViewModelProvider(this).get(ItemViewModel.class);
        viewModel.getSelectedItem().observe(this, item -> {
            // Perform an action with the latest item data
            current_scenario=item;
            Toast.makeText(getApplicationContext(),item.toString(), Toast.LENGTH_SHORT).show();
            System.out.println("Outside " +Thread.currentThread());
            RL_Decision();
        });
        /////////////////////////////////////////

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);


        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Welcome! Please choose a scenario.", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

//        WorkManager.getInstance().enqueue(workRequest);
////        WorkManager.getInstance().enqueueUniquePeriodicWork("Weather Worker", ExistingPeriodicWorkPolicy.REPLACE, workRequest);
//        WorkManager.getInstance().getWorkInfoByIdLiveData(workRequest.getId())
//                .observe(this, new Observer<WorkInfo>() {
//                    @Override
//                    public void onChanged(@Nullable WorkInfo workInfo) {
//
//                        //receiving back the data
//                        if(workInfo != null && workInfo.getState().isFinished()){
//                            System.out.println("We received "+workInfo.getOutputData().getString(MyWorker.TASK_DESC));
//                        }
//                    }
//                });
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        if (settings_check){
            settings_check = false;
            // Start Updating Q
            Toast.makeText(getApplicationContext(),"Welcome back from Settings", Toast.LENGTH_LONG).show();
            criteria_eval();
        } else {
        Toast.makeText(getApplicationContext(),"Now onStart() calls", Toast.LENGTH_LONG).show(); //onStart Called
//        getCurrentIP(); //REMOVE
        System.out.println("Hi");
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public String getLocation(){
        cellIDwithLocation = new CellIDwithLocation(this);
        CellID = cellIDwithLocation.getCellID();
        System.out.println("cell ID: "+CellID);
        return CellID;
        //Toast.makeText(getApplicationContext(),CellID, Toast.LENGTH_LONG).show();
    }

    public void go_to_settings(){
        settings_check = true;
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(android.provider.Settings.ACTION_DATA_ROAMING_SETTINGS);
        startActivity(intent);
    }

    public void getCurrentIP() { //REMOVE
        // An instance of WifiManger is used to retrieve connection info.
        WifiManager wim = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);

        if (wim.getConnectionInfo() != null) {
            if ((wim.getConnectionInfo().getIpAddress()) != 0) {
                //IP is parsed into readable format
                Toast.makeText(getApplicationContext(),"Your IP address is: "
                        + Formatter.formatIpAddress(wim.getConnectionInfo()
                        .getIpAddress()), Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getApplicationContext(),"1Error: a WIFI connection cannot be detected.", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(getApplicationContext(),"2Error: a WIFI connection cannot be detected.", Toast.LENGTH_LONG).show();
        }

    }

    public FloatingActionButton getFloatingActionButton() {
        return binding.fab;
    }

    public void criteria_eval(){
        new Ping().execute();
//        new SpeedTestTask().execute();
//        for (State s : state_list){
//            if (current_time==s.gettime() && current_location==s.getlocation() && current_scenario==s.getscenario() ){
//                s.getlearner().update_Q(chosen_action, weights[current_scenario-1],
//                        throughput,
//                        battery[chosen_action],
//                        jitter,
//                        loss,
//                        latency );
//            }
//        }
    }

    public void RL_Decision(){
        Date currentTime = Calendar.getInstance().getTime();

        current_time = currentTime.getHours(); //Integer between 1 and 24
        current_location= Integer.parseInt(getLocation());
        // current_scenario = item;

        current_state=find_state(current_time,current_location,current_scenario);
        for (State s : state_list){
            if (current_time==s.gettime() && current_location==s.getlocation() && current_scenario==s.getscenario() ){
                chosen_action =s.getlearner().take_decision();
            }
        }
        String res = "Please Choose: "+action_names[chosen_action];
        Toast.makeText(getApplicationContext(),res, Toast.LENGTH_LONG).show();
        // Take User to settings and change variable check settings
        go_to_settings();
        // Then go to onStart and call the RL_Study
    }

    public State find_state(int t, int l, int sc){
        for (State s : state_list){
            if (t==s.gettime() && l==s.getlocation() && sc==s.getscenario() ){
                System.out.println("The State Exists: "+s.getscenario()+" "+s.gettime()+" "+s.getlocation());
                return s;
            }
        }
        QLearning temp_agent = new QLearning();
        learner_list.add(temp_agent);
        State temp_state = new State(this.ID,temp_agent,t,l,sc);
        state_list.add(temp_state);
        this.ID++;
        System.out.println("New State: "+temp_state.getscenario()+" "+temp_state.gettime()+" "+temp_state.getlocation());
        return temp_state; //Then take decision(QL), then compute criteria (main act), then update Q (QL)
    }


    private class Ping extends AsyncTask<Void, Void, String> {

        private StringBuffer res = new StringBuffer();

        @Override
        protected String doInBackground(Void... voids) {
            System.out.println("Inside " +Thread.currentThread());
            Runtime runtime = Runtime.getRuntime();

            try {
                System.out.println("Start");
                Process ipProcess = runtime.exec("/system/bin/ping -c 10 8.8.8.8");
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(ipProcess.getInputStream()));

                String line = "";
                double[] ttls= new double[10];
                int ind =0;
                while ((line = bufferedReader.readLine())!= null) {
                    res.append(line + "\n");
                    if(line.contains("packet loss")){
                        int i= line.indexOf("received");
                        int j= line.indexOf("%");
                        loss = Double.parseDouble(line.substring(i+10, j));
                        System.out.println("Loss "+loss);
                    }
                    if(line.contains("avg")){
                        int i=line.indexOf("/", 30);
                        int j=line.indexOf("/", i+1);
                        latency = Double.parseDouble(line.substring(i+1, j))/2;
                        System.out.println("Latency "+latency);
                    }
                    if(line.contains("ttl")) {
                        int i=line.indexOf("ms");
                        int j=line.indexOf("time");
                        double ttl1= Double.parseDouble(line.substring(j+5, i-1));
                        ttls[ind]=ttl1;
                        ind++;
                    }
                }
                double ji=0;
                for (int i=1;i<ttls.length;i++) {
                    if (ttls[i]==0){
                        ttls[i]=ttls[i-1];
                    }
                    ji=ji+Math.abs(ttls[i]-ttls[i-1]);
                }
                jitter=ji/(ttls.length-1);
                System.out.println("Jitter " +jitter);
                System.out.println(res);

                int exitValue = ipProcess.waitFor();
                ipProcess.destroy();
                System.out.println("ExitValue: "+exitValue);
                if(exitValue == 0){
                    // Success
                    System.out.println("Reachable");
                } else {
                    // Failure
                    System.out.println("Unreachable");
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
                System.out.println("Error");
            }

            return res.toString();
        }

        @Override
        protected void onPostExecute(String s) {
            new SpeedTestTask().execute();
        }
    }

    public class SpeedTestTask extends AsyncTask<Void, Void, String> {

        private int x =0;
        private String res="";

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
                    res = report.getTransferRateBit().toString();
                    throughput= Double.parseDouble(res);
//                    System.out.println("DD: "+throughput);
                    for (State s : state_list){
                        if (current_time==s.gettime() && current_location==s.getlocation() && current_scenario==s.getscenario() ){
                            s.getlearner().update_Q(chosen_action, weights[current_scenario-1],
                                    throughput,
                                    battery[chosen_action],
                                    jitter,
                                    loss,
                                    latency );
                        }
                    }
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

//        @Override
//        protected void onPostExecute(String s) {
//            dd=this.res;
//            System.out.println("DD: "+dd);
//        }

    }



}
