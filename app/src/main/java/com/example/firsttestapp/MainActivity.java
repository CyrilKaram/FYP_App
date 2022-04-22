package com.example.firsttestapp;
// Au Revoir
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.telephony.TelephonyManager;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;

import androidx.fragment.app.Fragment;
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
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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

    // CRITERIA
    private int latency = 0;
    private String throughput ="";

    // RL PARAMETERS
    ArrayList<State> state_list = new ArrayList<State>();
    ArrayList<QLearning> learner_list = new ArrayList<QLearning>();
    private int ID = 1;
    Integer[] action_list = {1,2,3};
    private int current_time;
    private int current_location;
    private int current_scenario;
    double[][] weights = {
            {0.412426357, 0.179835921, 0.089147185, 0.225188033, 0.093402504},
            {0.168448384, 0.555575253, 0.04742412, 0.163219626, 0.065332617},
            {0.373626374, 0.040934066, 0.19514652, 0.19514652, 0.19514652},
            {0.263947285, 0.052846664, 0.12804264, 0.438119634, 0.117043777}
    };

    CellIDwithLocation cellIDwithLocation;
    String CellID;

    //Bonjour tout le monde
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        textres = (TextView) findViewById(R.id.textview_first);

        getLocation();



//        text = (TextView) getFragmentManager().findFragmentById(R.id.FirstFragment).find;
        ////////////////////////////////////////
        viewModel = new ViewModelProvider(this).get(ItemViewModel.class);
        viewModel.getSelectedItem().observe(this, item -> {
            // Perform an action with the latest item data
            current_scenario=item;
            Toast.makeText(getApplicationContext(),item.toString(), Toast.LENGTH_LONG).show();
            System.out.println("Outside " +Thread.currentThread());
//            new Ping().execute();
//            new SpeedTestTask().execute();
            getLocation();
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
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        Toast.makeText(getApplicationContext(),"Now onStart() calls", Toast.LENGTH_LONG).show(); //onStart Called
        getCurrentIP(); /////////////////////////////////////////////////
        System.out.println("Hi");
        /*cellIDwithLocation = new CellIDwithLocation(this);
        CellID = cellIDwithLocation.cellID;*/
        //System.out.println(CellID);
        //Toast.makeText(getApplicationContext(),CellID, Toast.LENGTH_LONG).show();
//        initIperf();
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

    public void getLocation(){
        cellIDwithLocation = new CellIDwithLocation(this);
        CellID = cellIDwithLocation.getCellID();
        System.out.println(CellID);
        Toast.makeText(getApplicationContext(),CellID, Toast.LENGTH_LONG).show();
    }

    public void getCurrentIP() {
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

    public State find_state(int t, int l, int sc){
        for (State s : state_list){
            if (t==s.gettime() && l==s.getlocation() && sc==s.getscenario() ){
                return s;
            }
        }
        QLearning temp_agent = new QLearning();
        learner_list.add(temp_agent);
        State temp_state = new State(this.ID,temp_agent,t,l,sc);
        state_list.add(temp_state);
        this.ID++;
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
                Process ipProcess = runtime.exec("/system/bin/ping -c 3 8.8.8.8");
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(ipProcess.getInputStream()));

                String line = "";
                while ((line = bufferedReader.readLine())!= null) {
                    res.append(line + "\n");
                }
                System.out.println("Res: "+res);

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
            latency++;
            System.out.println("SS: "+latency);
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
                    throughput=res;
                    System.out.println("DD: "+throughput);
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
