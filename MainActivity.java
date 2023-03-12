package com.eggbot.app;

import static com.eggbot.app.MainActivity.lat;
import static com.eggbot.app.MainActivity.lon;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.documentfile.provider.DocumentFile;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.util.Log;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.Granularity;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

// set up storage for lat and long values
public class MainActivity extends AppCompatActivity {
    private static final int PERMISSIONS_FINE_LOCATION = 99;

    //vars to setup file and read file
    private static final int CREATE_FILE = 1;

    private static final int READ_FILE = 2;


    // set up storage for lat and long values

    public static double lat = 0.00;
    public static double lon = 0.00;

    public static Location lastLocation;
    private static final String FILE_NAME = "data.txt";


    public static final String EXTRA_INITIAL_URI = "data.txt";

    // references to the UI elements

    TextView tv_lat, tv_lon, tv_altitude, tv_accuracy, tv_speed, tv_sensor, tv_updates,tv_address;
    Switch sw_locationupdates, sw_gps;

    // variable to remmeber if we are tracking location or not
    boolean updateOn = false;

    // Location request is a config file for all settings releated to FusedLocationProviderClient
    LocationRequest locationRequest;

    LocationCallback locationCallBack;

    // Google's API for location services. THe majority of the app functions using this class
    FusedLocationProviderClient fusedLocationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // give each UI variable a value

        tv_lat = findViewById(R.id.tv_lat);
        tv_lon = findViewById(R.id.tv_lon);
        tv_altitude = findViewById(R.id.tv_altitude);
        tv_accuracy = findViewById(R.id.tv_accuracy);
        tv_speed = findViewById(R.id.tv_speed);
        tv_sensor = findViewById(R.id.tv_sensor);
        tv_updates = findViewById(R.id.tv_updates);
        tv_address = findViewById(R.id.tv_address);
        sw_gps = findViewById(R.id.sw_gps);
        sw_locationupdates = findViewById(R.id.sw_locationsupdates);

        // initialise the file
        readLocationData();
        //createFile(Uri.parse(EXTRA_INITIAL_URI));

        // set all properties of LocationRequest
        int locationInterval = 0;

        locationRequest= new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 500).setMinUpdateDistanceMeters(0).setGranularity(Granularity.GRANULARITY_PERMISSION_LEVEL).setWaitForAccurateLocation(false).build();

        // event that is triggered when the update interval is set
        locationCallBack = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);

                // save the location if changed
                Location thisLocation = locationResult.getLastLocation();
                if (thisLocation!=null) {
                    if (lastLocation==null ||
                            diffLocation(thisLocation,lastLocation)) {
                        updateUIValues(thisLocation);
                        lastLocation = thisLocation;
                        save();
                    }
                }
            }
        };

        sw_gps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(sw_gps.isChecked()){
                    // most accurate - use gps
                    locationRequest.setPriority(Priority.PRIORITY_HIGH_ACCURACY);
                    tv_sensor.setText("Using GPS sensors");
                } else{
                    locationRequest.setPriority(Priority.PRIORITY_BALANCED_POWER_ACCURACY);
                    tv_sensor.setText("Using Towers + WIFI");
                }
            }
        });

        sw_locationupdates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sw_locationupdates.isChecked()){
                    // turn on location tracking
                    startLocationUpdates();
                }
                else{
                    // turn off tracking
                    stopLocationUpdates();
                }
            }
        });

        updateGPS();
        // initialise the file
        readLocationData();
        //createFile(Uri.parse(EXTRA_INITIAL_URI));

        SocketThread t = new SocketThread();
        t.start();
    } // end onCreate method

    private boolean diffLocation(Location thisLocation, Location lastLocation) {
        return !(thisLocation.getLatitude()==lastLocation.getLatitude() &&
        thisLocation.getLongitude()==lastLocation.getLongitude());
    }

    private List<LocationSample> locationSamples = new ArrayList<>();
    private void readLocationData() {
        InputStream is = getResources().openRawResource(R.raw.data);
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(is, StandardCharsets.UTF_8)
        );

        String line = "";
        try {
            while ((line = reader.readLine()) != null) {
                //split by ','
                Log.d("ReadLine", "Just read: " + line);
                String[] tokens = line.split(",");

                //read the data
                LocationSample sample = new LocationSample();
                if (!line.contains("Latitude")) {
                    sample.setLatitude(Double.parseDouble(tokens[0]));
                    sample.setLongitude(Double.parseDouble(tokens[1]));
                    locationSamples.add(sample);
                    Log.d("MyActivity", "Just created: " + sample);
                }
            }

        } catch (IOException e) {
            Log.wtf("MyActivity", "error reading data file on line " + line, e);
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }



        private void requestPermission(){
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                Toast.makeText(MainActivity.this, "Storage permission is required, please allow from settings", Toast.LENGTH_SHORT).show();
            }else {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 111);
            }
            ActivityCompat.requestPermissions(MainActivity.this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},112);
        }

        private boolean checkPermission(){
            int result = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (result == PackageManager.PERMISSION_GRANTED){
                return true;
            }else{
                return false;
            }
        }

        public void save(){
            String latString = Double.toString(lat);
            String lonString = Double.toString(lon);
            File path = getApplicationContext().getFilesDir();
            //File path = Environment.getExternalStorageDirectory();
            try {
                FileOutputStream writer = new FileOutputStream(new File(path,FILE_NAME));
                writer.write(latString.getBytes("UTF-8"));
                writer.write(",".getBytes("UTF-8"));
                writer.write(lonString.getBytes("UTF-8"));
                writer.write("\n".getBytes("UTF-8"));
                Toast.makeText(this, "Saved to " + getFilesDir() + "/" + FILE_NAME, Toast.LENGTH_SHORT).show();
                writer.close();
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        public void load(){
            FileInputStream fis = null;

            try {
                fis = openFileInput(FILE_NAME);
                InputStreamReader isr = new InputStreamReader(fis);
                BufferedReader br = new BufferedReader(isr);
                StringBuilder sb = new StringBuilder();
                String text;

                while ((text = br.readLine())!= null){
                    sb.append(text).append("\n");
                }


            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                if (fis !=null){
                    try {
                        fis.close();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }


        }

        private void createFile(Uri pickerInitialUri) {
            Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("application/txt");
            intent.putExtra(Intent.EXTRA_TITLE, "data.txt");

            // Optionally, specify a URI for the directory that should be opened in
            // the system file picker when your app creates the document.
            intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri);

            startActivityForResult(intent, CREATE_FILE);
        }


    private void stopLocationUpdates() {
        tv_updates.setText("Location is NOT being tracked");
        tv_lat.setText("Not tracking location");
        tv_lon.setText("Not tracking location");
        tv_speed.setText("Not tracking location");
        tv_address.setText("Not tracking location");
        tv_accuracy.setText("Not tracking location");
        tv_altitude.setText("Not tracking location");
        tv_sensor.setText("Not tracking location");

        fusedLocationProviderClient.removeLocationUpdates(locationCallBack);
    }

    @SuppressLint("MissingPermission")
    private void startLocationUpdates() {
        tv_updates.setText("Location is being tracked");
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallBack, null);
        updateGPS();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch(requestCode){
            case PERMISSIONS_FINE_LOCATION:;
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                updateGPS();
            }
            else{
                Toast.makeText(this, "This app requires permission to be granted in order to work properly", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private void updateGPS(){
        // get permissions from the user to track GPS
        // get the current location from the fused client
        // update the UI - i.e. set all properties in their associated text view items

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MainActivity.this);

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            // user provided the permission
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    // we got permissions Put the values of location. XXX into the UI components
                    if (location!=null) updateUIValues(location);
                }


            });
        }
        else{
            // permissions not granted yet

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_FINE_LOCATION);
            }
        }
    }
    private void updateUIValues(Location location) {

        // update all of the text view objects with a new location
        lat = location.getLatitude();
        lon = location.getLongitude();

        tv_lat.setText(String.valueOf(lat));
        tv_lon.setText(String.valueOf(lon));
        tv_accuracy.setText(String.valueOf(location.getAccuracy()));

        if (location.hasAltitude()){
            tv_altitude.setText(String.valueOf(location.getAltitude()));
        }
        else{
            tv_altitude.setText("Not Available");
        }
        if (location.hasSpeed()){
            tv_speed.setText(String.valueOf(location.getSpeed()));
        }
        else{
            tv_speed.setText("Not Available");
        }

        // handling using google build in funcitons to find street address of corresponding lat and longitude

        Geocoder geocoder = new Geocoder(MainActivity.this);

        try{
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(), 1);
            tv_address.setText(addresses.get(0).getAddressLine(0));
        }
        catch(Exception e){
            tv_address.setText("Unable to get street address");

        }
    }


}

class SocketThread extends Thread {

    SocketThread() {
    }

    public void run() {
        // open listener
        try {
            ServerSocket ss = new ServerSocket(50000);
            Socket my_socket = ss.accept();
            Log.d("Main", String.format("client connected from: %s", my_socket.getRemoteSocketAddress().toString()));
            while (true) {
                // if there's a connection
                if (!my_socket.isOutputShutdown()) {
                    if (my_socket.isConnected()) {
                        Log.d("Main", String.format("Socket is connected."));
                        OutputStream out = my_socket.getOutputStream();
                        PrintWriter output = new PrintWriter(out);
                        String latString = Double.toString(lat);
                        String lonString = Double.toString(lon);
                        output.print("[" + latString + "," + lonString + "]");
                        output.flush();
                    }
                }
                // wait 1 second
                Log.d("Main", String.format("Sleeping."));
                Thread.sleep(1000);
            }
        } catch (IOException e) {
            //
        } catch (InterruptedException e) {
            //throw new RuntimeException(e);
        }
    }
}