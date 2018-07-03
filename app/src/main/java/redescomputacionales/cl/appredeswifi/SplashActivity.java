package redescomputacionales.cl.appredeswifi;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;

public class SplashActivity extends AppCompatActivity {

    static public final int REQUEST_LOCATION = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.putExtra("ACTION", "signin");
            startActivity(intent);
            finish();
        }
        else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    Intent intent = new Intent(this, LoginActivity.class);
                    intent.putExtra("ACTION", "signin");
                    startActivity(intent);
                    finish();

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                    dialog.setCancelable(false);
                    dialog.setTitle("Información");
                    dialog.setMessage("Esta aplicación necesita acceder a la ubicación de este dispositivo para continuar");
                    dialog.setPositiveButton("Continuar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            //Action for positive.
                            ActivityCompat.requestPermissions(SplashActivity.this,
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    REQUEST_LOCATION);
                        }
                    })
                            .setNegativeButton("Salir ", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //Action for negative.
                                    SplashActivity.this.finish();
                                    //System.exit(0);
                                }
                            });
                    final AlertDialog alert = dialog.create();
                    alert.show();
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
}