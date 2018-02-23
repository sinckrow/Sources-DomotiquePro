package l3pro20162017.domotiquepro;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import java.security.Key;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutionException;

public class ActivityListLog extends Activity {
    ListView vueLogs;
    Cursor cursor;
    DBHelper dbHelper;
    SimpleCursorAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_home);
    try {
        //Demande et ajout des permissions (ici pour lecture sms)
        if (ContextCompat.checkSelfPermission(ActivityListLog.this,
                Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(ActivityListLog.this,
                    Manifest.permission.READ_SMS)) {
                ActivityCompat.requestPermissions(ActivityListLog.this,
                        new String[]{Manifest.permission.READ_SMS}, 1);
            } else {
                ActivityCompat.requestPermissions(ActivityListLog.this,
                        new String[]{Manifest.permission.READ_SMS}, 1);
            }
        } else {
            //ne rien faire
        }

        //Affichage du contenu de la bdd dans la liste
        vueLogs = (ListView) findViewById(R.id.activity_logs_home_list);
        dbHelper = new DBHelper(this);
        cursor = dbHelper.getReadableDatabase().rawQuery("SELECT * FROM domo_logs;", null);
        startManagingCursor(cursor);
        adapter = new SimpleCursorAdapter(this,
                android.R.layout.simple_list_item_2,
                cursor,
                new String[]{"dateStr", "message"},
                new int[]{android.R.id.text1,
                        android.R.id.text2
                });
        vueLogs.setAdapter(adapter);

        Button btn_refresh = (Button) findViewById(R.id.activity_logs_home_btn_refresh);
        Button btn_delete = (Button) findViewById(R.id.activity_logs_home_btn_delete);

        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setTitle("Vider la liste des logs ?");
                builder.setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dbHelper.viderLogs();
                        cursor = dbHelper.getReadableDatabase().rawQuery("SELECT * FROM domo_logs;", null);
                        startManagingCursor(cursor);
                        vueLogs.setAdapter(adapter);
                        adapter = new SimpleCursorAdapter(ActivityListLog.this,
                                android.R.layout.simple_list_item_2,
                                cursor,
                                new String[]{"dateStr", "message"},
                                new int[]{android.R.id.text1, android.R.id.text2});
                        vueLogs.setAdapter(adapter);
                    }
                });
                builder.setNegativeButton("Non", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            }
        });

        btn_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(v.getContext(), "click", Toast.LENGTH_SHORT).show();
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(v.getContext());
                String numero = preferences.getString(KeyWords.NUMERO_TELEPHONE, "").trim();
                int compteur = preferences.getInt(KeyWords.COMPTEUR, -1);
                if (!(numero.length() > 0) || compteur == -1) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                    builder.setTitle("Application non configurée, veuillez la configurer");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(ActivityListLog.this, ActivityMenuOptions.class);
                            startActivity(intent);
                        }
                    });
                    builder.setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    builder.show();
                } else {
                    Toast.makeText(v.getContext(), "Début rafraichissement !", Toast.LENGTH_SHORT).show();
                    Uri uri = Uri.parse(KeyWords.SMS_URI_INBOX);

                    Cursor uriCursor = v.getContext().getContentResolver().query(uri, null, null, null, null);
                    startManagingCursor(uriCursor);
                    String numeroPlus = "+33" + numero.substring(1);
                    int indexId = uriCursor.getColumnIndex("_id");
                    int indexSender = uriCursor.getColumnIndex("address");
                    int indexDate = uriCursor.getColumnIndex("date");
                    int indexBody = uriCursor.getColumnIndex("body");
                    int indexRead = uriCursor.getColumnIndex("read");
                    // Read the sms data and store it in the list

                    while (uriCursor.moveToNext()) {
                        String sender = uriCursor.getString(indexSender);
                        int read = uriCursor.getInt(indexRead);
                        if ((sender.equals(numero) || sender.equals(numeroPlus)) && read != 1) {
                                SimpleDateFormat ft = Log.getSimpleDateFormat();
                                Date date = new Date(uriCursor.getLong(indexDate));
                                String dateStr = ft.format(date);
                                String body = uriCursor.getString(indexBody);
                                dbHelper.insertLog(dateStr, body);
                                //passer message en lu
                                String SmsMessageId = uriCursor.getString(uriCursor.getColumnIndex("_id"));
                                ContentValues values = new ContentValues();
                                values.put("read", true);
                                int res = v.getContext().getContentResolver().update(Uri.parse("content://sms/inbox/"), values, "_id=?" , new String[]{SmsMessageId});
                                System.out.println(String.valueOf(res));
                        }
                    }
                    uriCursor.close();

                    cursor = dbHelper.getReadableDatabase().rawQuery("SELECT * FROM domo_logs;", null);
                    startManagingCursor(cursor);
                    vueLogs.setAdapter(adapter);
                    adapter = new SimpleCursorAdapter(v.getContext(),
                            android.R.layout.simple_list_item_2,
                            cursor,
                            new String[]{"dateStr", "message"},
                            new int[]{android.R.id.text1, android.R.id.text2});
                    vueLogs.setAdapter(adapter);
                    Toast.makeText(v.getContext(), "Fin rafraichissement !", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }
    catch (Exception e){
        e.printStackTrace();
        System.out.println(e);
    }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
        switch (requestCode){
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    if (ContextCompat.checkSelfPermission(ActivityListLog.this,
                            Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED){
                        Toast.makeText(this, "Permissions ok", Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    Toast.makeText(this, "Pas de permissions", Toast.LENGTH_SHORT).show();
                }
                return;
        }
    }

}

