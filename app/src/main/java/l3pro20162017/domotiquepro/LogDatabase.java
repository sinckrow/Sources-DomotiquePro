package l3pro20162017.domotiquepro;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.Date;

public class LogDatabase extends SQLiteOpenHelper{
    SQLiteDatabase db;

    public LogDatabase(Context context){
        super(context, KeyWords.DATABADE_NAME, null, KeyWords.DATABASE_VERSION);
        db  = super.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        this.db = db;
        //db.execSQL("DROP TABLE IF EXISTS logs");
        db.execSQL(KeyWords.CREATE_DATABASE_LOGS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

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
}
