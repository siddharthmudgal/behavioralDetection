package com.ezmcom.smudgal.walkpattern;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity implements SensorEventListener {
    public int count_accel;
    public int count_magneto;
    public float[][] acc_x,acc_y,acc_z;
    public float[][] mag_x,mag_y,mag_z;
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
    }

    private void startInit(){
        recording.setVisibility(View.VISIBLE);
        not_recording.setVisibility(View.INVISIBLE);
        start.setEnabled(false);
        stop.setEnabled(true);
        seconds.setEnabled(false);
        host.setEnabled(false);
        username.setEnabled(false);
    }

    private void stopInit(){
        recording.setVisibility(View.INVISIBLE);
        not_recording.setVisibility(View.VISIBLE);
        start.setEnabled(true);
        stop.setEnabled(false);
        seconds.setEnabled(true);
        host.setEnabled(true);
        username.setEnabled(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        init();

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
                acc_x = acc_y = acc_z = new float[3][size];
                mag_x = mag_y = mag_z = new float[3][size];
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
                    util.convertToJson(String.valueOf(username.getText()), acc_x, acc_y, acc_z, mag_x, mag_y, mag_z, size, String.valueOf(host.getText()),getApplicationContext());
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
        int turn = Integer.valueOf(turns.getText().toString());
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float val[] = event.values;
            acc_x[turn][(++count_accel)%size] = val[0];
            acc_y[turn][(count_accel)%size] = val[1];
            acc_z[turn][(count_accel)%size] = val[2];
            acc.setText(String.valueOf(acc_x[turn][count_accel%size])+"\t"+String.valueOf(acc_y[turn][count_accel%size])+"\t"+String.valueOf(acc_z[turn][count_accel%size])+"\t");
        }
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            float val[] = event.values;
            mag_x[turn][(++count_magneto)%size] = val[0];
            mag_y[turn][(count_magneto)%size] = val[1];
            mag_z[turn][(count_magneto) % size] = val[2];
            mag.setText(String.valueOf(mag_x[turn][count_magneto%size]) + "\t"+String.valueOf(mag_y[turn][count_magneto%size]) + "\t"+String.valueOf(mag_z[turn][count_magneto%size]) + "\t");
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

}
