package l3pro20162017.domotiquepro;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ActivityMenuOptions extends Activity {
    String numeroValue;
    int compteurValue;
    EditText editNumero;
    EditText editCompteur;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);

        editNumero = (EditText)findViewById(R.id.activity_options_input_numero);
        editCompteur = (EditText)findViewById(R.id.activity_options_input_compteur);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        numeroValue = preferences.getString(KeyWords.NUMERO_TELEPHONE, "");
        compteurValue = preferences.getInt(KeyWords.COMPTEUR, -1);

        if (!numeroValue.isEmpty()){
            editNumero.setText(numeroValue, TextView.BufferType.EDITABLE);
        }

        if (compteurValue != -1){
            editCompteur.setText(String.valueOf(compteurValue), TextView.BufferType.EDITABLE);
        }

        Button btnValider = (Button)findViewById(R.id.activity_options_btn_valider);
        btnValider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                numeroValue = editNumero.getText().toString();
                compteurValue = Integer.parseInt(editCompteur.getText().toString());
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(v.getContext());
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString(KeyWords.NUMERO_TELEPHONE, numeroValue);
                editor.putInt(KeyWords.COMPTEUR, compteurValue);
                boolean isok = editor.commit();
                if(isok)
                    Toast.makeText(v.getContext(), "Modifications enregistrés !", Toast.LENGTH_LONG).show();
                else
                    Toast.makeText(v.getContext(), "erreur !", Toast.LENGTH_LONG).show();
            }
        });
    }
}