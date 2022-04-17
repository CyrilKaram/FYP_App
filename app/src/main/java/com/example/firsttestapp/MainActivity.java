package com.example.firsttestapp;
// Au Revoir
import android.net.wifi.WifiManager;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.text.format.Formatter;
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
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    private ItemViewModel viewModel;
    private TextView textres;
    ExecutorService executorService = Executors.newFixedThreadPool(4);


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
            Toast.makeText(getApplicationContext(),item.toString(), Toast.LENGTH_LONG).show();
            System.out.println("Outside " +Thread.currentThread());
            new Ping().execute();
            new SpeedTestTask().execute();
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
    protected void onResume()
    {
        super.onResume();
        Toast.makeText(getApplicationContext(),"Now onResume() calls", Toast.LENGTH_LONG).show(); //onStart Called
//        FirstFragment.getView().findViewByID(R.id.textview_first).setText("Now onStart() calls");
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                getCurrentIP();
//            }
//        }).start();
        getCurrentIP(); /////////////////////////////////////////////////
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

    //Related to iPerf
    //This function is used to copy the iperf executable to a directory which execute permissions for this application, and then gives it execute permissions.
    //It runs on every initiation of an iperf test, but copies the file only if it's needed.
//    public void initIperf() {
////        final TextView tv = (TextView) findViewById(R.id.OutputText);
//        InputStream in;
//
//        try {
//            //The asset "iperf" (from assets folder) inside the activity is opened for reading.
//            in = getResources().getAssets().open("iperf");
//        } catch (IOException e2) {
//            Toast.makeText(getApplicationContext(),"3\nError occurred while accessing system resources, please reboot and try again.2", Toast.LENGTH_LONG).show();
//            return;
//        }
//        try {
//            //Checks if the file already exists, if not copies it.
//            new FileInputStream("/data/data/com.example.firsttestapp/iperf");
//        } catch (FileNotFoundException e1) {
//            try {
//                //The file named "iperf" is created in a system designated folder for this application.
//                OutputStream out = new FileOutputStream("/data/data/com.example.firsttestapp/iperf", false);
//                Toast.makeText(getApplicationContext(),"Output Worked", Toast.LENGTH_LONG).show();
//                // Transfer bytes from "in" to "out"
//                byte[] buf = new byte[1024];
//                int len;
//                while ((len = in.read(buf)) > 0) {
//                    out.write(buf, 0, len);
//                }
//                in.close();
//                out.close();
//                //After the copy operation is finished, we give execute permissions to the "iperf" executable using shell commands.
//                Process processChmod = Runtime.getRuntime().exec("/system/bin/chmod 744 /data/data/iperf.project/iperf");
//                // Executes the command and waits untill it finishes.
//                processChmod.waitFor();
//            } catch (IOException e) {
//                Toast.makeText(getApplicationContext(),"4 Error occurred while accessing system resources, please reboot and try again.3", Toast.LENGTH_LONG).show();
//                return;
//            } catch (InterruptedException e) {
//                Toast.makeText(getApplicationContext(),"5\nError occurred while accessing system resources, please reboot and try again.4", Toast.LENGTH_LONG).show();
//                return;
//            }
//
//            //Creates an instance of the class IperfTask for running an iperf test, then executes.
//            IperfTask iperfTask = new IperfTask();
//            iperfTask.execute();
//            return;
//        }
//        //Creates an instance of the class IperfTask for running an iperf test, then executes.
//        IperfTask iperfTask = new IperfTask();
//        iperfTask.execute();
//        return;
//    }
//    @Override
//    public void onBackPressed() {
//        tellFragments();
//        super.onBackPressed();
//        getFloatingActionButton().show();
//    }
//
//    private void tellFragments(){
//        List<Fragment> fragments = getSupportFragmentManager().getFragments();
//        for(Fragment f : fragments){
//            if(f != null && f instanceof default_fragment)
//                ((default_fragment)f).onBackPressed();
//        }
//    }
}
