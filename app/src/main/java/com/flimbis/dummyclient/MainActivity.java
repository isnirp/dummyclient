package com.flimbis.dummyclient;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private int MY_PERMISSIONS = 33;
    private FusedLocationProviderClient fusedLocationClient;
    private double lat, lon = 0;
    private EditText edtHost;
    private EditText edtPort;
    private TextView txtStatus;
    private TextView txtLog;
    private TextView txtLongitude;
    private TextView txtLatitude;
    private Button bttnConnect;
    private Button bttnGps;
    private Button bttnSend;
    private Toolbar toolbar;
    private RelativeLayout relativeLayout;
    private SockClient client;
    private int conn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        //setSupportActionBar(toolbar);
        //toolbar.setTitle("GPS Client");
        bttnConnect.setOnClickListener(this);
        bttnGps.setOnClickListener(this);
        bttnSend.setOnClickListener(this);


        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
    }

    private void initViews() {
        //toolbar = findViewById(R.id.toolbar);
        edtHost = findViewById(R.id.edt_host);
        edtPort = findViewById(R.id.edt_port);
        txtStatus = findViewById(R.id.txt_status);
        txtLog = findViewById(R.id.txt_log);
        txtLongitude = findViewById(R.id.txt_lon);
        txtLatitude = findViewById(R.id.txt_lat);
        bttnConnect = findViewById(R.id.bttn_connect);
        bttnGps = findViewById(R.id.bttn_get_gps);
        bttnSend = findViewById(R.id.bttn_send_gps);
        relativeLayout = findViewById(R.id.rel_lay);
    }

    private void requirePermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS);
    }

    private void getMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED) {
            //ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS);
            requirePermission();
        } else {
            // permission granted
            fusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        lat = location.getLatitude();
                        lon = location.getLongitude();
                    }
                }
            });
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS) {
            // check if permission granted for location or not
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // granted
                fusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            lat = location.getLatitude();
                            lon = location.getLongitude();
                        }
                    }
                });
            } else {
                // show snack bar
            }
        }
    }

    private void connectServer() {
        String host = "";
        int port = 3306;
        if (TextUtils.isEmpty(edtHost.getText().toString()) || TextUtils.isEmpty(edtPort.getText().toString())) {
            Toast.makeText(this, "all fields are required", Toast.LENGTH_LONG).show();
        } else {
            host = edtHost.getText().toString().trim();
            port = Integer.parseInt(edtPort.getText().toString().trim());

            Toast.makeText(this, "host: " + host + " port: " + port, Toast.LENGTH_LONG).show();
            client = new SockClient(host, port);
            txtLog.setText("connecting to " + SockClient.server + "...");
        }

    }

    private void showMsg(String msg) {
        Snackbar snackbar = Snackbar
                .make(relativeLayout, msg, Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bttn_connect:
                connectServer();
                conn = client.openConnection();
                if (conn == 1) {
                    txtStatus.setText("Connected");
                    showMsg("connected");
                    txtLog.setText("");
                } else {
                    txtStatus.setText("Connection failed");
                    txtLog.setText("ensure machines are on the same network...");
                }
                break;
            case R.id.bttn_get_gps:
                getMyLocation();
                txtLatitude.setText("LAT: " + lat);
                txtLongitude.setText("LON: " + lon);
                break;
            case R.id.bttn_send_gps:
                if (conn == 1) {
                    int send = client.sendData("lat: " + lat + ", lon: " + lon);
                    if (send == 1) {
                        showMsg("gps sent success");
                    } else
                        showMsg("failed to send gps");
                } else {
                    showMsg("no connection");
                    txtLog.setText("socket connection failed");
                }
            default:
                showMsg("default");
                break;
        }
    }
}