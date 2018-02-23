package l3pro20162017.domotiquepro;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ActivityEditActions extends Activity {
    EditText editLibelle;
    EditText editCode;
    CheckBox editCaptcha;

    int id;
    String libelle;
    String code;
    boolean captcha;
    int position;


    String action;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actions_edition);

        editLibelle = (EditText)findViewById(R.id.activity_actions_edition_input_libelle);
        editCode = (EditText)findViewById(R.id.activity_actions_edition_input_code_action);
        editCaptcha = (CheckBox)findViewById(R.id.activity_actions_edition_input_check_verif);

        id = getIntent().getIntExtra("id",0);
        libelle = getIntent().getStringExtra("libelle");
        code = getIntent().getStringExtra("code");
        position = getIntent().getIntExtra("position",1);
        int intcCaptcha = getIntent().getIntExtra("captcha",0);
        if (intcCaptcha==1)
            captcha = true;
        else
            captcha = false;

        editLibelle.setText(libelle, TextView.BufferType.EDITABLE);
        editCode.setText(code, TextView.BufferType.EDITABLE);
        editCaptcha.setChecked(captcha);


        Button btn_update = (Button)findViewById(R.id.activity_actions_edition_btn_valider);
        Button btn_delete = (Button)findViewById(R.id.activity_actions_edition_btn_delete);

        btn_update.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                action = "update";
                finish();
            }
        });

        btn_delete.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setTitle("Supprimer ?");
                builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        action = "delete";
                        finish();
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();

            }
        });
    }

    @Override
    public void finish(){
        Intent data = new Intent();
        if (action.equals("update")){
            libelle = editLibelle.getText().toString();
            code = editCode.getText().toString();
            captcha = editCaptcha.isChecked();
            data.putExtra("id",id);
            data.putExtra("libelle", libelle);
            data.putExtra("code", code);
            data.putExtra("captcha", captcha);
        }
        else {
            data.putExtra("id",id);
            data.putExtra("libelle", libelle);
            data.putExtra("code", code);
            data.putExtra("captcha", captcha);
        }
        data.putExtra("position",position);
        data.putExtra("action", action);
        setResult(RESULT_OK, data);
        super.finish();
    }

}
