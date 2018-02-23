package l3pro20162017.domotiquepro;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.icu.text.UnicodeSetSpanner;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.SmsManager;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Random;

public class ActivityListActions extends Activity {

    ListView vueActions;
    Cursor cursor;
    DBHelper dbHelper;
    SimpleCursorAdapter adapter;
    private static final int CODE_RESULT_CREATE = 1;
    private static final int CODE_RESULT_EDIT = 2;

    private String m_Text = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actions_home);

        try {
            //Demande et ajout des permissions (ici pour le sms)
            if (ContextCompat.checkSelfPermission(ActivityListActions.this,
                    Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(ActivityListActions.this,
                        Manifest.permission.SEND_SMS)) {
                    ActivityCompat.requestPermissions(ActivityListActions.this,
                            new String[]{Manifest.permission.SEND_SMS}, 1);
                } else {
                    ActivityCompat.requestPermissions(ActivityListActions.this,
                            new String[]{Manifest.permission.SEND_SMS}, 1);
                }
            } else {
                //ne rien faire
            }

            //Affichage du contenu de la bdd dans la List
            vueActions = (ListView) findViewById(R.id.activity_actions_list_view);
            dbHelper = new DBHelper(this);
            cursor = dbHelper.getReadableDatabase().rawQuery("SELECT * FROM actions;", null);
            startManagingCursor(cursor);
            adapter = new SimpleCursorAdapter(this,
                    android.R.layout.simple_list_item_1,
                    cursor,
                    new String[]{"libelle"},
                    new int[]{android.R.id.text1});

            vueActions.setAdapter(adapter);

            //ajout des actions sur la liste et le boutton
            vueActions.setOnItemClickListener(new EcouteurClickListeAction());
            vueActions.setOnItemLongClickListener(new EcouteurLongClickListeAction());
            Button btn_ajout = (Button) findViewById(R.id.activity_actions_home_button_add_action);
            btn_ajout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ActivityListActions.this, ActivityAjoutAction.class);
                    startActivityForResult(intent, CODE_RESULT_CREATE);
                }
            });
        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.out.println(e);
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        switch (requestCode){

            case CODE_RESULT_CREATE :
                switch (resultCode){
                    case RESULT_OK:
                        String libelle = data.getStringExtra("libelle");
                        String code = data.getStringExtra("code");
                        boolean captcha = data.getBooleanExtra("captcha",false);
                        dbHelper.insertAction(libelle, code, captcha);
                        break;
                    case  RESULT_CANCELED:
                        Toast.makeText(this, "Ajout annulé", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        break;
                }
            break;

            case CODE_RESULT_EDIT:
                switch (resultCode){
                    case RESULT_OK:
                        String action = data.getStringExtra("action");
                        if (action.equals("update")){
                            int id = data.getIntExtra("id", 0);
                            String libelle = data.getStringExtra("libelle");
                            String code = data.getStringExtra("code");
                            boolean captcha = data.getBooleanExtra("captcha",false);
                            dbHelper.updateAction(id, libelle, code, captcha);
                            Toast.makeText(this,"mis à jour", Toast.LENGTH_SHORT).show();
                        }
                        else if (action.equals("delete")){
                            int id = data.getIntExtra("id",0);
                            dbHelper.deleteAction(id);
                            Toast.makeText(this,"delete", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(this, "erreur", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    default:
                        break;
                }
                break;

            default:
                break;
        }
}

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
        switch (requestCode){
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    if (ContextCompat.checkSelfPermission(ActivityListActions.this,
                            Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED){
                        Toast.makeText(this, "Permissions ok", Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    Toast.makeText(this, "Pas de permissions", Toast.LENGTH_SHORT).show();
                }
                return;
        }
    }

    class EcouteurClickListeAction implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick (AdapterView<?> parent, final View v, int position, long id) {

            cursor.moveToPosition(position);
            int _id = cursor.getInt(0);
            final String libelle = cursor.getString(1);
            final String code = cursor.getString(2);
            int i_captcha = cursor.getInt(3);

            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(v.getContext());
            final String numeroValue = preferences.getString(KeyWords.NUMERO_TELEPHONE, "");
            final int compteurValue = preferences.getInt(KeyWords.COMPTEUR, -1);

            boolean captcha;
            if (i_captcha==1)
                captcha = true;
            else
                captcha = false;

            if (captcha){
                Random random = new Random();
                int nb1 = random.nextInt(50-1)+1;
                int nb2 = random.nextInt(50-1)+1;
                final int result = nb1 + nb2;

                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setTitle(nb1+" + "+nb2+" = ");

                final EditText input = new EditText(v.getContext());
                input.setInputType(InputType.TYPE_CLASS_NUMBER);
                builder.setView(input);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        m_Text = input.getText().toString();
                        int user_i = Integer.parseInt(m_Text);
                        if (user_i == result){
                            if ((numeroValue.length()>0) && (compteurValue != -1)){
                                sendSms(numeroValue, code, compteurValue);
                            }
                            else{
                                Toast.makeText(ActivityListActions.this, "Erreur tel ou compteur not init" ,Toast.LENGTH_SHORT).show();
                            }
                        }
                        else{
                            Toast.makeText(ActivityListActions.this, "Erreur captcha est faux" ,Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            }
            else{

                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setTitle("Envoyer cette action ?");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    if ((numeroValue.length()>0) && (compteurValue != -1)){
                        sendSms(numeroValue, code, compteurValue);
                    }
                    else{
                        Toast.makeText(ActivityListActions.this, "Erreur tel ou compteur not init" ,Toast.LENGTH_SHORT).show();
                    }
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            }
        }
    }

    private void sendSms(String phoneNo, String actionCode, int counter){
        long timestamp = System.currentTimeMillis()/1000;
        String msg = actionCode+";"+timestamp+";"+counter;
        try {
            SmsManager smsManager= SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNo, null, msg, null, null);
            Toast.makeText(ActivityListActions.this, "Sms send "+msg ,Toast.LENGTH_SHORT).show();
            incrementationCompteur();
        }
        catch (Exception e){
            Toast.makeText(ActivityListActions.this, "Sms fail "+msg ,Toast.LENGTH_SHORT).show();
        }
    }

    private void incrementationCompteur(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        int compteur = preferences.getInt(KeyWords.COMPTEUR,-1);
        if(compteur==-1){
            Toast.makeText(ActivityListActions.this, "Erreur compteur", Toast.LENGTH_SHORT).show();
            return;
        }
        compteur++;
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(KeyWords.COMPTEUR, compteur);
        editor.commit();
    }

    class EcouteurLongClickListeAction implements  AdapterView.OnItemLongClickListener{

        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            cursor.moveToPosition(position);
            //Toast.makeText(ActivityListActions.this, "Long "+ cursor.getString(1) + " " + cursor.getString(2) +" "+cursor.getInt(3) ,Toast.LENGTH_LONG).show();
            Intent intent = new Intent(ActivityListActions.this, ActivityEditActions.class);
            intent.putExtra("position", position);
            intent.putExtra("id", cursor.getInt(0));
            intent.putExtra("libelle", cursor.getString(1));
            intent.putExtra("code", cursor.getString(2));
            intent.putExtra("captcha", cursor.getInt(3));
            startActivityForResult(intent, CODE_RESULT_EDIT);
            return  true;
        }
    }
}




