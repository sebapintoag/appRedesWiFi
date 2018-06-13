package redescomputacionales.cl.appredeswifi;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

public class RegistrosActivity extends AppCompatActivity {

    Button bEnviar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registros);

        //Cambia el título del toolbar
        getSupportActionBar().setTitle("Registros previos");
        //Añade el botón "hacia atrás" por defecto de Android
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        bEnviar = (Button)findViewById(R.id.enviar);
        bEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ConexionSQLiteHelper bdConn = new ConexionSQLiteHelper(RegistrosActivity.this);
                SQLiteDatabase db = bdConn.getWritableDatabase();
                db.delete("registros",null, null);
                db.close();

                cargar();

            }
        });


        cargar();
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

    private void cargar (){

        ConexionSQLiteHelper bdConn = new ConexionSQLiteHelper(RegistrosActivity.this);
        SQLiteDatabase db = bdConn.getWritableDatabase();

        if (db != null) {
            Cursor c = db.rawQuery("select * from registros", null);
            int cantidad = c.getCount();
            int i = 0;
            String[] arreglo = new String[cantidad];
            if (c.moveToFirst()) {
                do {
                    String linea = c.getInt(0) + " | " + c.getFloat(1) + " | " + c.getFloat(2) + " | " + c.getString(3) + " | " + c.getFloat(4) + " | " + c.getFloat(5);
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
