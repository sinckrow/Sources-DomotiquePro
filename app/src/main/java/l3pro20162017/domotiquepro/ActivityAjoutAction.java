package l3pro20162017.domotiquepro;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class ActivityAjoutAction extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actions_ajout);

        Button btn_valider = (Button)findViewById(R.id.activity_actions_ajout_btn_valider);
        btn_valider.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void finish(){
        EditText edit_libelle = (EditText)findViewById(R.id.activity_actions_ajout_input_libelle);
        EditText edit_code = (EditText)findViewById(R.id.activity_actions_ajout_input_code_action);
        CheckBox edit_captcha = (CheckBox)findViewById(R.id.activity_actions_ajout_input_check_verif);

        String libelle = edit_libelle.getText().toString();
        String code = edit_code.getText().toString();
        boolean captcha = edit_captcha.isChecked();

        if (libelle.trim().isEmpty() || code.trim().isEmpty()){
            Intent data = new Intent();
            setResult(RESULT_CANCELED, data);
            super.finish();
        }
        else {
            Intent data = new Intent();
            data.putExtra("libelle",libelle);
            data.putExtra("code",code);
            data.putExtra("captcha",captcha);
            setResult(RESULT_OK, data);
            super.finish();
        }
    }
}
