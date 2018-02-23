package l3pro20162017.domotiquepro;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.Date;

public class DBHelper extends SQLiteOpenHelper{
    SQLiteDatabase db;

    public DBHelper(Context context) {
        super(context, KeyWords.DATABADE_NAME, null, KeyWords.DATABASE_VERSION);
        db  = super.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS "+KeyWords.DATABASE_TABLE_ACTIONS);
        db.execSQL("DROP TABLE IF EXISTS "+KeyWords.DATABASE_TABLE_LOGS);
        db.execSQL(KeyWords.CREATE_DATABASE_ACTIONS);
        db.execSQL("INSERT INTO actions (libelle, code, confirm) VALUES ('Tout ouvrir','ALL UP', 1);");
        db.execSQL("INSERT INTO actions (libelle, code, confirm) VALUES ('Tout fermer','ALL DOWN', 1);");
        db.execSQL("INSERT INTO actions (libelle, code, confirm) VALUES ('Ouvrir Sud','SUD UP', 0);");
        db.execSQL("INSERT INTO actions (libelle, code, confirm) VALUES ('Fermer Sud','SUD DOWN', 0);");
        db.execSQL("INSERT INTO actions (libelle, code, confirm) VALUES ('Ouvrir Est','EST UP', 0);");
        db.execSQL("INSERT INTO actions (libelle, code, confirm) VALUES ('Fermer Est','EST DOWN', 0)");
        db.execSQL("INSERT INTO actions (libelle, code, confirm) VALUES ('Ouvrir Ouest','OUEST UP', 0);");
        db.execSQL("INSERT INTO actions (libelle, code, confirm) VALUES ('Fermer Ouest','OUEST DOWN', 0);");
        db.execSQL(KeyWords.CREATE_DATABASE_LOGS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion > oldVersion){
            db.execSQL("DROP TABLE IF EXISTS "+KeyWords.DATABASE_TABLE_ACTIONS);
            db.execSQL("DROP TABLE IF EXISTS "+KeyWords.DATABASE_TABLE_LOGS);
            db.execSQL(KeyWords.CREATE_DATABASE_ACTIONS);
            db.execSQL("INSERT INTO actions (libelle, code, confirm) VALUES ('Tout ouvrir','ALL UP', 1);");
            db.execSQL("INSERT INTO actions (libelle, code, confirm) VALUES ('Tout fermer','ALL DOWN', 1);");
            db.execSQL("INSERT INTO actions (libelle, code, confirm) VALUES ('Ouvrir Sud','SUD UP', 0);");
            db.execSQL("INSERT INTO actions (libelle, code, confirm) VALUES ('Fermer Sud','SUD DOWN', 0);");
            db.execSQL("INSERT INTO actions (libelle, code, confirm) VALUES ('Ouvrir Est','EST UP', 0);");
            db.execSQL("INSERT INTO actions (libelle, code, confirm) VALUES ('Fermer Est','EST DOWN', 0)");
            db.execSQL("INSERT INTO actions (libelle, code, confirm) VALUES ('Ouvrir Ouest','OUEST UP', 0);");
            db.execSQL("INSERT INTO actions (libelle, code, confirm) VALUES ('Fermer Ouest','OUEST DOWN', 0);");
            db.execSQL(KeyWords.CREATE_DATABASE_LOGS);
        }
    }

    //GESTION DES LOGS
    public void insertLog(String dateStr, String message){
        Log log = new Log(dateStr, message);
        insertLog(log);
    }

    public void insertLog(Date date, String message){
        Log log = new Log(Log.getDateString(date), message);
        insertLog(log);
    }

    public void insertLog(Log log){
        ContentValues values = new ContentValues();
        values.put("dateStr",log.getDateStr());
        values.put("message",log.getMessage());
        db.insert(KeyWords.DATABASE_TABLE_LOGS, null, values);
    }

    public void viderLogs(){
        db.execSQL("delete from domo_logs;");
    }


    //GESTION DES ACTIONS
    public void insertAction(String libelle, String code, boolean confirm){
        ContentValues values = new ContentValues();
        values.put("libelle", libelle);
        values.put("code", code);
        values.put("confirm", confirm);
        db.insert(KeyWords.DATABASE_TABLE_ACTIONS, null, values);
    }

    public void insertAction(Action action){
        ContentValues values = new ContentValues();
        values.put("libelle", action.getLibelle());
        values.put("code", action.getCode());
        values.put("confirm", action.getConfirm());
        db.insert(KeyWords.DATABASE_TABLE_ACTIONS, null, values);
    }

    public void updateAction(Action action){
        ContentValues values = new ContentValues();
        int id = action.getId();
        values.put("libelle", action.getLibelle());
        values.put("code", action.getCode());
        values.put("confirm", action.getConfirm());
        db.update(KeyWords.DATABASE_TABLE_ACTIONS, values,"_id=?", new String[]{String.valueOf(id)});
    }

    public void updateAction(int id, String libelle, String code, boolean confirm){
        ContentValues values = new ContentValues();
        values.put("libelle", libelle);
        values.put("code", code);
        values.put("confirm", confirm);
        db.update(KeyWords.DATABASE_TABLE_ACTIONS, values,"_id=?", new String[]{String.valueOf(id)});
    }

    public void deleteAction(int id){
        db.delete(KeyWords.DATABASE_TABLE_ACTIONS, "_id=?", new String[]{String.valueOf(id)});
    }
}
