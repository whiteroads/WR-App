package com.whiteroads.application;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.whiteroads.application.data.UserDataWrapper;
import com.whiteroads.library.StartService;

import java.util.Arrays;

public class HomeActivity extends AppCompatActivity{

    private TextView accel, speed, gyroscope,linaccel,location,rotation,light,proxim,steps,orientation ;
    private BroadcastReceiver broadcastReceiver;
    private Button stopCapture;
    private Intent service,sensors;
    private PendingIntent pIntent;
    private AlarmManager alarm;
    private BottomNavigationView navigation;
    public StartService startService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_home);
        try {
            startService = new StartService(this);
            startService.initializeSDK("whiteroads","6ca383af9aa7d7c6538e746927ddc829",
                    UserDataWrapper.getInstance().getUserEmail(),
                    UserDataWrapper.getInstance().getVehicleNumber());
//            if(Arrays.asList("rahulgoyal670@gmail.com","parasiitbbs@gmail.com","parulsharmma@gmail.com","nikhilsharma010@gmail.com").contains(UserDataWrapper.getInstance().getUserEmail())){
//                HandleBottomNavigation();
//            }else{
                HandleBottomNavigation();
                loadFragment(new AnalysisFragment());
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void HandleBottomNavigation(){
        try{

            navigation = (BottomNavigationView) findViewById(R.id.navigationView);
            navigation.setVisibility(View.VISIBLE);
            navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
            navigation.setSelectedItemId(R.id.home);
            loadFragment(new AnalysisFragment());
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment;
            switch (item.getItemId()) {
                case R.id.home:
                    fragment = new AnalysisFragment();
                    loadFragment(fragment);
                    return true;
                case R.id.sensors:
                    fragment = new SensorsFragment();
                    loadFragment(fragment);
                    return true;
            }
            return false;
        }
    };

    private void loadFragment(Fragment fragment) {
        // load fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
