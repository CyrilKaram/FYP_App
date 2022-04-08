package com.example.firsttestapp;
// Au Revoir
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;

import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.firsttestapp.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import androidx.annotation.Nullable;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    private ItemViewModel viewModel;
    ExecutorService executorService = Executors.newFixedThreadPool(4);
    //Bonjour tout le monde
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ////////////////////////////////////////
        viewModel = new ViewModelProvider(this).get(ItemViewModel.class);
        viewModel.getSelectedItem().observe(this, item -> {
            // Perform an action with the latest item data
            Toast.makeText(getApplicationContext(),item.toString(), Toast.LENGTH_LONG).show();
//            Ping p = new Ping(new Executor() {
//                @Override
//                public void execute(Runnable runnable) {
//                System.out.print("We are in the Thread: ");
//                System.out.println(Thread.currentThread());
//                }
//            });
            System.out.println("Outside " +Thread.currentThread());
            new Thread(new Runnable() {
                @Override
                public void run() {
                    System.out.println("Inside " +Thread.currentThread());
                    Runtime runtime = Runtime.getRuntime();
                    try {
                        System.out.println("Start");
                        Process ipProcess = runtime.exec("/system/bin/ping -c 3 8.8.8.8");
                        int exitValue = ipProcess.waitFor();
                        ipProcess.destroy();
//                    Toast.makeText(getActivity().getApplicationContext(),exitValue, Toast.LENGTH_LONG).show();
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
                }
            }).start();
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
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
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


}
