package redescomputacionales.cl.appredeswifi;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;


public class datos extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_datos);
        cargar();
    }

    private void  cargar (){

        ConexionSQLiteHelper bdConn = new ConexionSQLiteHelper(datos.this);
        SQLiteDatabase db = bdConn.getWritableDatabase();

        if (db != null) {
            Cursor c = db.rawQuery("select * from registros", null);
            int cantidad = c.getCount();
            int i = 0;
            String[] arreglo = new String[cantidad];
            if (c.moveToFirst()) {
                do {
                    String linea = c.getInt(0) + " " + c.getString(1) + " " + c.getString(2);
                    ;
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