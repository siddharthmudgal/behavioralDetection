package com.ezmcom.smudgal.walkpattern;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.DetectedActivity;

import static com.ezmcom.smudgal.walkpattern.Constants.walking;


public class MainActivity extends AppCompatActivity implements SensorEventListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, ResultCallback<Status> {
    public int count_accel;
    public int count_magneto;
    public float[] acc_x,acc_y,acc_z;
    public float[] mag_x,mag_y,mag_z;
    public int size;
    EditText seconds ;
    EditText host ;
    SensorManager sensorManager ;
    Sensor accel ;
    Sensor magneto ;
    FloatingActionButton start ;
    FloatingActionButton stop ;
    TextView acc ;
    TextView mag ;
    TextView turns ;
    TextView recording ;
    TextView not_recording ;
    EditText username ;
    Util util;
    protected  ActivityDetectionBroadcastReceiver activityDetectionBroadcastReceiver;
    protected  GoogleApiClient googleApiClient;
    private void init(){
        count_accel = 0;
        count_magneto = 0;
        seconds = (EditText)findViewById(R.id.windowSecond);
        host = (EditText)findViewById(R.id.host);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magneto = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        start = (FloatingActionButton) findViewById(R.id.start);
        stop = (FloatingActionButton) findViewById(R.id.stop);
        turns = (TextView) findViewById(R.id.turns);
        acc = (TextView) findViewById(R.id.accel);
        mag = (TextView) findViewById(R.id.mag);
        recording = (TextView) findViewById(R.id.recording);
        not_recording = (TextView) findViewById(R.id.not_recording);
        username = (EditText) findViewById(R.id.userName);
        util = new Util();
        turns.setText("3");
        start.setEnabled(false);
        stop.setEnabled(false);
        username.setEnabled(true);
        host.setEnabled(true);
        seconds.setEnabled(true);
        host.setText("192.168.0.101");
        buildGoogleApiClient();
    }

    private void startInit(){
        acc_x = new float[size+1]; acc_y = new float[size+1]; acc_z = new float[size+1];
        mag_x = new float[size+1]; mag_y = new float[size+1]; mag_z = new float[size+1];
        recording.setVisibility(View.VISIBLE);
        not_recording.setVisibility(View.INVISIBLE);
        start.setEnabled(false);
        stop.setEnabled(true);
        seconds.setEnabled(false);
        host.setEnabled(false);
        username.setEnabled(false);
        username.setEnabled(false);
        if (!googleApiClient.isConnected()) {
            Toast.makeText(this, getString(R.string.not_connected),
                    Toast.LENGTH_SHORT).show();
            return;
        }
        ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates(
                googleApiClient,
                Constants.detection_time,
                getActivityDetectionPendingIntent()
        ).setResultCallback(this);
    }

    protected synchronized void buildGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(ActivityRecognition.API)
                .build();
    }

    private void stopInit(){
        recording.setVisibility(View.INVISIBLE);
        not_recording.setVisibility(View.VISIBLE);
        start.setEnabled(true);
        stop.setEnabled(false);
        seconds.setEnabled(true);
        host.setEnabled(true);
        username.setEnabled(true);

        LocalBroadcastManager.getInstance(this).registerReceiver(activityDetectionBroadcastReceiver, new IntentFilter(Constants.movement));
        if (!googleApiClient.isConnected()) {
            Toast.makeText(this, getString(R.string.not_connected), Toast.LENGTH_SHORT).show();
            return;
        }
        ActivityRecognition.ActivityRecognitionApi.removeActivityUpdates(
                googleApiClient,
                getActivityDetectionPendingIntent()
        ).setResultCallback(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        googleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        googleApiClient.disconnect();
    }
    @Override
    protected void onResume() {
        super.onResume();
        // Register the broadcast receiver that informs this activity of the DetectedActivity
        // object broadcast sent by the intent service.
        LocalBroadcastManager.getInstance(this).registerReceiver(activityDetectionBroadcastReceiver,
                new IntentFilter(Constants.movement));
    }
    @Override
    protected void onPause() {
        // Unregister the broadcast receiver that was registered during onResume().
        LocalBroadcastManager.getInstance(this).unregisterReceiver(activityDetectionBroadcastReceiver);
        super.onPause();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        init();
        activityDetectionBroadcastReceiver = new ActivityDetectionBroadcastReceiver();
        seconds.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                size = Integer.parseInt(String.valueOf(s)) * 48;
                acc_x = acc_y = acc_z = new float[size];
                mag_x = mag_y = mag_z = new float[size];
            }
        });
        username.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int startc, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int startc, int beforec, int count) {
                if (s.length() == 0 || String.valueOf(seconds.getText()).length() == 0 || String.valueOf(host.getText()).length() == 0)
                {
                    start.setEnabled(false);
                    stop.setEnabled(false);
                }
                else{
                    start.setEnabled(true);
                    stop.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                int turn =util.checkUser(String.valueOf(username.getText()),getApplicationContext());
                if (turn >= 0){
                    turns.setText(String.valueOf(turn));
                }
                else{
                    turns.setText("User taken");
                }
            }
        });

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int turn = util.checkUserName(String.valueOf(username.getText()),getApplicationContext());
                if (turn >= 0){
                    startInit();
                    sensorManager.registerListener(MainActivity.this, accel, SensorManager.SENSOR_DELAY_UI);
                    sensorManager.registerListener(MainActivity.this, magneto, SensorManager.SENSOR_DELAY_UI);
                    turns.setText(String.valueOf(turn));
                }
            }
        });
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopInit();
                sensorManager.unregisterListener(MainActivity.this, accel);
                sensorManager.unregisterListener(MainActivity.this, magneto);
                if (Integer.valueOf((String) turns.getText()) == 0) {
                    turns.setText("User taken");
                    init();
                }
            }
        });


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
    public void onSensorChanged(SensorEvent event) {
        if (count_accel >= (size - 1))
            count_accel = count_accel % size;
        if (count_magneto >= (size - 1))
            count_magneto = count_magneto % size;

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float val[] = event.values;
            acc_x[(++count_accel)] = val[0];
            acc_y[(count_accel)] = val[1];
            acc_z[(count_accel)] = val[2];
            acc.setText(String.valueOf(acc_x[count_accel])+"\t"+String.valueOf(acc_y[count_accel])+"\t"+String.valueOf(acc_z[count_accel])+"\t");
        }
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            float val[] = event.values;
            mag_x[(++count_magneto)] = val[0];
            mag_y[(count_magneto)] = val[1];
            mag_z[(count_magneto)] = val[2];
            mag.setText(String.valueOf(mag_x[count_magneto]) + "\t"+String.valueOf(mag_y[count_magneto]) + "\t"+String.valueOf(mag_z[count_magneto]) + "\t");
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Toast.makeText(this,"connected",Toast.LENGTH_SHORT);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(this,"onConnectionSuspended",Toast.LENGTH_SHORT);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this,"onConnectionFailed",Toast.LENGTH_SHORT);
    }

    @Override
    public void onResult(@NonNull Status status) {

    }
    private PendingIntent getActivityDetectionPendingIntent() {
        Intent intent = new Intent(this, DetectedActivityService.class);
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    class ActivityDetectionBroadcastReceiver extends BroadcastReceiver{

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getStringExtra("style");
            if (Constants.walking.equals(action)){
                util.convertToJson(action,String.valueOf(username.getText()), acc_x, acc_y, acc_z, mag_x, mag_y, mag_z, size, String.valueOf(host.getText()),getApplicationContext());
            }
            else if (Constants.running.equals(action)){
                util.convertToJson(action,String.valueOf(username.getText()), acc_x, acc_y, acc_z, mag_x, mag_y, mag_z, size, String.valueOf(host.getText()),getApplicationContext());
            }
        }
    }

}
