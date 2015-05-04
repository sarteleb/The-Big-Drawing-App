package edu.msu.sarteleb.bigdrawing;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.OrientationEventListener;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.View;
import android.view.WindowManager;


public class MainActivity extends ActionBarActivity {

    //region Private Members

    DrawingView drawingView;
    private static final int color = 2;
    private static final float accelerationY = 9.0f;
    private static final float penMax = 20.0f;
    private SensorManager sensorManager = null;
    private Sensor sensor = null;
    private AccelerometerListener accelListener = null;
    private OrientationEventListener orientationListener = null;
    private float x;
    private float y;
    private float z;
    private float aX;
    private float aY;
    private float aZ;
    private float filter;
    private float penSize;
    private LocationManager locationManager;
    private ActiveListener activeListener;
    private int surfaceRotation;

    //endregion

    //region Nested Classes

    private class AccelerometerListener implements SensorEventListener {
        @Override
        public void onAccuracyChanged(Sensor arg0, int arg1) {
        }

        @Override
        public void onSensorChanged(SensorEvent event) {

            aY = event.values[1];
            y = (1 - filter) * aY + filter * y;

            if (y < accelerationY && y > -accelerationY) {
                penSize = (1 - Math.abs(y / accelerationY)) * penMax;
            }
            else {
                penSize = 0f;
            }

            drawingView.setLineWidth(penSize);
        }
    }

    private class ActiveListener implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {
            onLocation(location);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onProviderDisabled(String provider) {
            registerListeners();
        }
    };

    //endregion

    //region Overrides

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sensorManager = null;
        sensor = null;

        accelListener = null;
        orientationListener = null;

        x=0f;
        y=0f;
        z=0f;
        aX = 0f;
        aY = 0f;
        aZ = 0f;

        filter = 0.8f;
        surfaceRotation = 0;
        penSize = 0f;

        locationManager = null;

        activeListener = new ActiveListener();

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_main);
        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        drawingView = (DrawingView)findViewById(R.id.drawingView);

        orientationListener = new OrientationEventListener(this, SensorManager.SENSOR_DELAY_UI) {
            @Override
            public void onOrientationChanged(int orientation) {
                GetOrientation();
            }
        };
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.itemSend:
                sendToServer();
                break;
            case R.id.colorSelect:
                onColorSelect();
                break;
            case R.id.clearCanvas:
                clearCanvas();
                break;
            case R.id.showLocation:
                showLocation();
                break;

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == color && resultCode == RESULT_OK) {

            int color = data.getIntExtra(ColorSelectActivity.COLOR, Color.BLACK);
            drawingView.setLineColor(color);

        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        registerListeners();

        InstallSensor();
        GetOrientation();
        orientationListener.enable();
    }

    @Override
    protected void onPause() {
        unregisterListeners();
        super.onPause();

        UninstallSensor();
        orientationListener.disable();
    }

    //endregion

    //region Public Methods

    public void onColorSelect() {
        Intent intent = new Intent(this, ColorSelectActivity.class);
        startActivityForResult(intent, color);
    }

    public void sendToServer() {

        View what = findViewById(R.id.drawingView);

        ViewSender sender = new ViewSender();
        sender.sendView(this, what, "Titian");

    }

    //endregion

    //region Private Methods

    private void unregisterListeners(){
        locationManager.removeUpdates(activeListener);
    }

    private void registerListeners() {
        unregisterListeners();

        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setPowerRequirement(Criteria.POWER_HIGH);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setSpeedRequired(false);
        criteria.setCostAllowed(false);

        String bestAvailable = locationManager.getBestProvider(criteria, true);

        if(bestAvailable != null) {
            locationManager.requestLocationUpdates(bestAvailable, 500, 1, activeListener);
            Location location = locationManager.getLastKnownLocation(bestAvailable);
            onLocation(location);
        }

    }

    private void onLocation(Location location) {
        if(location != null) {
            drawingView.update(location.getLatitude(), location.getLongitude());
        }
    }

    private void clearCanvas() {
        drawingView.clearCanvas();
    }

    private void showLocation() {
        drawingView.showLocation();
    }

    private void InstallSensor() {
        sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);

        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        if(sensor != null) {
            accelListener = new AccelerometerListener();

            sensorManager.registerListener(accelListener, sensor, SensorManager.SENSOR_DELAY_FASTEST);
        }
    }

    private void UninstallSensor() {
        if(sensor != null) {
            sensorManager.unregisterListener(accelListener);
            accelListener = null;
            sensor = null;
        }
    }

    private void GetOrientation() {
        WindowManager wm = (WindowManager)getSystemService(Context.WINDOW_SERVICE);

        surfaceRotation = wm.getDefaultDisplay().getRotation();
    }

    //endregion

}
