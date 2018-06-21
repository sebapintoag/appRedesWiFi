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

        db.execSQL("CREATE TABLE registros(id_registro INTEGER PRIMARY KEY AUTOINCREMENT, latitud INTEGER, longitud INTEGER, fecha TEXT, velocidad INTEGER, intensidad INTEGER, estado TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int versionAntigua, int versionNueva) {    // se refrescan los scripts

        db.execSQL("DROP TABLE IF EXISTS registros");
        onCreate(db);

    }




}

