package redescomputacionales.cl.appredeswifi;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;
import com.facebook.network.connectionclass.ConnectionClassManager;
import com.facebook.network.connectionclass.ConnectionQuality;
import com.facebook.network.connectionclass.DeviceBandwidthSampler;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener, GoogleMap.OnMarkerDragListener {

    //Se agrega un MapFragment y un GoogleMap
    private SupportMapFragment mapFragment;
    private GoogleMap mMap;

    //Obtiene la posición actual
    private FusedLocationProviderClient mFusedLocationClient;

    //Posición GPS del dispositivo
    private LatLng gpsLocation;

    //Clase WifiInfo con los metodos para obtener datos de la coneccion
    private WifiManager wifiManager;
    private WifiInfo connectionInfo;

    String fechaHora;

    //Datos wifi solicitados
    private double speed;
    private double intensidad;

    String info;

    private static final String TAG = "ConnectionClass-Sample";

    private ConnectionClassManager mConnectionClassManager;
    private DeviceBandwidthSampler mDeviceBandwidthSampler;
    private ConnectionChangedListener mListener;
    private View mRunningBar;

    //private String mURL = "https://apod.nasa.gov/apod/fap/image/1505/LakeMyvatn_Brady_3840.jpg";
    private String mURL = "https://www.usach.cl/sites/default/files/logo_usach.jpg";
    private int mTries = 0;
    private ConnectionQuality mConnectionClass = ConnectionQuality.UNKNOWN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DownloadImage().execute(mURL);
                //saveData(view);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Soporte para el MapFragment
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map); //R.id.map es un fragmento que está en content_main.xml
        mapFragment.getMapAsync((OnMapReadyCallback) this);

        //Para obtener la última (o actual) ubicacion conocida
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        getSupportActionBar().setTitle("Registrar estado de red");

        mConnectionClassManager = ConnectionClassManager.getInstance();
        mDeviceBandwidthSampler = DeviceBandwidthSampler.getInstance();
        mRunningBar = findViewById(R.id.runningBar);
        mRunningBar.setVisibility(View.GONE);
        mListener = new ConnectionChangedListener();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mConnectionClassManager.remove(mListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mConnectionClassManager.register(mListener);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        /*
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        */

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        item.setCheckable(false);
        if (id == R.id.registros) {
            Intent registros = new Intent(MainActivity.this, RegistrosActivity.class);
            if (registros != null) {
                startActivity(registros);
            } else {
                Toast.makeText(this, "Ha ocurrido un error", Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.nada) {
            Toast.makeText(this, "Esto es opcional", Toast.LENGTH_SHORT).show();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.setMyLocationEnabled(true);
        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMyLocationClickListener(this);
        mMap.setOnMarkerDragListener(this);
        getCurrentLocation();
    }

    @Override
    public boolean onMyLocationButtonClick() {
        mMap.clear();
        getCurrentLocation();
        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {

    }

    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        gpsLocation = marker.getPosition();
    }


    public void updateGpsLocation(Location location) {
        //Asigna la LatLng a partir la ubicación GPS
        gpsLocation = new LatLng(location.getLatitude(), location.getLongitude());
    }

    public void addMarkerToLocation(Location location, String tittle) {
        //Añade un marcador en el mapa
        mMap.addMarker(new MarkerOptions()
                .position(gpsLocation)
                .title(tittle)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE))
                .draggable(true)
        );
    }

    public void moveCameraToLocation(Location location) {
        //Mueve el mapa con movimiento suave
        CameraUpdate camara = CameraUpdateFactory.newLatLngZoom(
                gpsLocation, 18);
        mMap.animateCamera(camara);
    }

    @SuppressLint("MissingPermission")
    public void getCurrentLocation()
    //Obtiene la posición actual y añade un marcador en el mapa
    {
        //Obtiene la posicion actual
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            // Logic to handle location object
                            updateGpsLocation(location);
                            addMarkerToLocation(location, "Posición Actual");
                            moveCameraToLocation(location);
                        }
                    }
                });
    }

    public void saveData()
    {
        //Verifica si el wifi esta activado
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (!wifiManager.isWifiEnabled()) //Si está apagado
        {
            Toast.makeText(MainActivity.this, "Conexión Wi-Fi apagada\nPor favor, conéctese a una red WiFi", Toast.LENGTH_SHORT).show();
            return;
        } else { //Si está predindo
            connectionInfo = wifiManager.getConnectionInfo();
            if (connectionInfo.getNetworkId() == -1) { //Si está prendido pero no conectado
                Toast.makeText(MainActivity.this, "No hay conexión Wi-Fi\nPor favor, conéctese a una red WiFi", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        Log.e("> Wifi", "Conectado a Wi-Fi");

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        fechaHora = sdf.format(new Date());

        intensidad = (double) connectionInfo.getRssi();

        ConexionSQLiteHelper bdConn = new ConexionSQLiteHelper(MainActivity.this);
        SQLiteDatabase db = bdConn.getWritableDatabase();
        if (db != null) {
            ContentValues registronuevo = new ContentValues();
            registronuevo.put("latitud", gpsLocation.latitude);
            registronuevo.put("longitud", gpsLocation.longitude);
            registronuevo.put("fecha", fechaHora);
            registronuevo.put("velocidad", speed);
            registronuevo.put("intensidad", intensidad);

            db.insert("registros", null, registronuevo);
        }

        info = "Datos almacenados\n> Latitud: " + gpsLocation.latitude + "\n> Longitud: " + gpsLocation.longitude + "\n> Fecha: " + fechaHora + "\n> Intensidad Wi-Fi: " + String.valueOf(intensidad) + " dBm" + "\n> Velocidad Wi-Fi: " + String.valueOf(speed) + " Kbps";
        /*
        Snackbar snackbar = Snackbar.make(view, info, Snackbar.LENGTH_LONG).setDuration(Snackbar.LENGTH_LONG);
        View snackbarView = snackbar.getView();
        TextView tv = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
        tv.setMaxLines(6);
        snackbar.show();
        */
        Toast.makeText(this, info, Toast.LENGTH_SHORT).show();
    }

    /**
     * Listener to update the UI upon connectionclass change.
     */
    private class ConnectionChangedListener
            implements ConnectionClassManager.ConnectionClassStateChangeListener {

        @Override
        public void onBandwidthStateChange(ConnectionQuality bandwidthState) {
            mConnectionClass = bandwidthState;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                }
            });
        }
    }

    /**
     * AsyncTask for handling downloading and making calls to the timer.
     */
    private class DownloadImage extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            mDeviceBandwidthSampler.startSampling();
            mRunningBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(String... urlArray) {
            String imageURL = urlArray[0];
            try {
                // Open a stream to download the image from our URL.
                URLConnection connection = new URL(imageURL).openConnection();
                connection.setUseCaches(false);
                connection.connect();
                InputStream input = connection.getInputStream();
                try {
                    byte[] buffer = new byte[1024];
                    // Do some busy waiting while the stream is open.
                    while (input.read(buffer) != -1) {
                    }
                } finally {
                    input.close();
                }
            } catch (IOException e) {
                Log.e(TAG, "Error while downloading image.");
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            mDeviceBandwidthSampler.stopSampling();
            // Retry for up to 10 times until we find a ConnectionClass.
            if (mConnectionClass == ConnectionQuality.UNKNOWN && mTries < 10) {
                mTries++;
                new DownloadImage().execute(mURL);
            }
            if (!mDeviceBandwidthSampler.isSampling()) {
                mRunningBar.setVisibility(View.GONE);
                speed = mConnectionClassManager.getDownloadKBitsPerSecond();
                saveData();
                mConnectionClass = ConnectionQuality.UNKNOWN;
                mTries = 0;
            }

        }
    }
}


