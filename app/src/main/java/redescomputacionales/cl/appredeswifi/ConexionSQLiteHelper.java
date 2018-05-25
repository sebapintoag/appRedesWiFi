package redescomputacionales.cl.appredeswifi;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ConexionSQLiteHelper extends SQLiteOpenHelper {

    Context ctx;

    public ConexionSQLiteHelper(Context context) {
        super(context, "db_registros", null, 1);

        // ctx= context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {   // se crea los script

        db.execSQL("CREATE TABLE registros(id_registro INTEGER, latitud INTEGER, longitud INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int versionAntigua, int versionNueva) {    // se refrescan los scripts

        db.execSQL("DROP TABLE IF EXISTS registros");
        onCreate(db);

    }

    /*/ variables globales, sirven para manipular la bd

    ConexionSQLiteHelper ayuda;
    SQLiteDatabase db;


    // metodos para manejar la bd

    public void abrir(){

        ayuda = new ConexionSQLiteHelper(ctx);
        db = ayuda.getWritableDatabase();
    }

    public void cerrar(){

        db.close();
    }

    //Metodos para manipular datos

    public long registrar(String latitud) throws Exception{

        ContentValues valores = new ContentValues();
        valores.put("latitud", latitud);
       // valores.put("longitud", longitud);

        return  db.insert("registros",null, valores);

    }

    public String consultar( ) throws Exception{

        String datos="";
        String[] columnas=new String[]{"latitud"};

        Cursor c= db.query("registros",columnas,null,null,null,null,null);


        for(c.moveToFirst();!c.isAfterLast();c.moveToNext()){

            datos+=c.getString(c.getColumnIndex("latitud"))+"\n";

        }

        return datos;
    }*/
}

