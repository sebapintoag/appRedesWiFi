package redescomputacionales.cl.appredeswifi;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class RegistrosActivity extends AppCompatActivity {

    Button bEnviar;
    String[][] arregloREST;
    JSONObject jsonObject = new JSONObject();
    private static RegistrosActivity mInstance;
    private  RequestQueue requestQueue;
    int i;
    int cantidad;

    private View mRunningBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mInstance=this;
        setContentView(R.layout.activity_registros);

        //Cambia el título del toolbar
        getSupportActionBar().setTitle("Registros previos");
        //Añade el botón "hacia atrás" por defecto de Android
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mRunningBar = findViewById(R.id.runningBarReg);
        mRunningBar.setVisibility(View.GONE);

        bEnviar = (Button)findViewById(R.id.enviar);
        bEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enviarDatos();
            }
        });
        cargar();
    }

    public static synchronized RegistrosActivity getInstance()
    {
        return mInstance;
    }

    public RequestQueue getRequestQueue()
    {
        if (requestQueue==null)
            requestQueue= Volley.newRequestQueue(getApplicationContext());

        return requestQueue;
    }

    public void addToRequestQueue(Request request,String tag)
    {
        request.setTag(tag);
        getRequestQueue().add(request);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            //Maneja la acción del botón "hacia atrás" por defecto de Android
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void enviarDatos()
    {


        final Context context = getApplicationContext();
        final CharSequence postCorrect = "Datos ingresados correctamente";
        final CharSequence postError= "Ha ocurrido un error con la subida de datos";
        final int shortDuration = Toast.LENGTH_SHORT;
        final int longDuration = Toast.LENGTH_LONG;

        // Instantiate the RequestQueue.
        final RequestQueue[] queue = {Volley.newRequestQueue(RegistrosActivity.this)};
        //this is the url where you want to send the request
        String url = "http://206.189.184.79:8091/redes/signals";


        // Request a string response from the provided URL.
        for(i=0;i<cantidad;i++)
        {
            String _id = arregloREST[i][0];
            String _latitud = arregloREST[i][1];
            String _longitud = arregloREST[i][2];
            String _fecha = arregloREST[i][3];
            String estado = arregloREST[i][4];
            String _velocidad = arregloREST[i][5];
            String _intensidad = arregloREST[i][6];
            JSONObject postparams=new JSONObject();
            try {
                postparams.put("id", _id);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                postparams.put("latitud", _latitud);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                postparams.put("longitud", _longitud);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                postparams.put("fecha", _fecha);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                postparams.put("estado", estado);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                postparams.put("velocidad", _velocidad);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                postparams.put("intensidad", _intensidad);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            bEnviar.setVisibility(View.GONE);
            mRunningBar.setVisibility(View.VISIBLE);

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                    (Request.Method.POST, url, postparams, new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            mRunningBar.setVisibility(View.GONE);
                            bEnviar.setVisibility(View.VISIBLE);
                            Toast toastPlain = Toast.makeText(context, postCorrect, shortDuration);
                            toastPlain.show();
                            //Toast toast = Toast.makeText(context, response.toString() , longDuration);
                            //toast.show();

                            ConexionSQLiteHelper bdConn = new ConexionSQLiteHelper(RegistrosActivity.this);
                            SQLiteDatabase db = bdConn.getWritableDatabase();
                            db.delete("registros",null, null);
                            db.close();
                            cargar();
                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            mRunningBar.setVisibility(View.GONE);
                            bEnviar.setVisibility(View.VISIBLE);
                            Toast toastPlain = Toast.makeText(context, postError, shortDuration);
                            toastPlain.show();
                            //Toast toast = Toast.makeText(context, error.toString(), longDuration);
                            //toast.show();
                        }
                    });
            // Access the RequestQueue through your singleton class.
            RegistrosActivity.getInstance().addToRequestQueue(jsonObjectRequest,"postRequest");
        }
    }

    public void cargar (){

        ConexionSQLiteHelper bdConn = new ConexionSQLiteHelper(RegistrosActivity.this);
        SQLiteDatabase db = bdConn.getWritableDatabase();
        if (db != null) {
            Cursor c = db.rawQuery("select * from registros", null);
            cantidad = c.getCount();
            i = 0;

            String[] arreglo = new String[cantidad];
            arregloREST = new String[cantidad][7];
            if (c.moveToFirst()) {
                do {
                    String linea = c.getInt(0) + " | " + c.getFloat(1) + " | " + c.getFloat(2) + " | " + c.getString(3) + " | " + c.getFloat(4) + " | " + c.getFloat(5) + " | " + c.getString(6);
                    arregloREST[i][0] = c.getString(0)+"";
                    arregloREST[i][1] = c.getFloat(1)+"";
                    arregloREST[i][2] = c.getFloat(2)+"";
                    arregloREST[i][3] = c.getString(3);
                    arregloREST[i][4] = c.getFloat(4)+"";
                    arregloREST[i][5] = c.getFloat(5)+"";
                    arregloREST[i][6] = c.getString(6)+"";
                    arreglo[i] = linea;
                    i++;
                } while (c.moveToNext());
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arreglo);
            ListView lista = (ListView) findViewById(R.id.listaRegistros);
            lista.setAdapter(adapter);

        }
    }
}
