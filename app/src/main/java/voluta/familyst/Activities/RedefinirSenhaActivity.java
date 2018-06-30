package voluta.familyst.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import voluta.familyst.Interfaces.RestCallback;
import voluta.familyst.R;
import voluta.familyst.Services.RestService;

public class RedefinirSenhaActivity extends AppCompatActivity {


    private EditText emailRedefinir;
    private Button btnRedefinirSenha;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_redefinir_senha);

        emailRedefinir = (EditText) findViewById(R.id.txt_email_redefinir_senha);
        btnRedefinirSenha = (Button) findViewById(R.id.btn_redefinir_senha);

        btnRedefinirSenha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RestService.getInstance(RedefinirSenhaActivity.this).EnviarEmailSenha( emailRedefinir.getText().toString(), new RestCallback(){
                    @Override
                    public void onRestResult(boolean success) {
                        if (success){
                            Toast.makeText(getApplicationContext(),getResources().getText(R.string.sucesso_envio_email), Toast.LENGTH_LONG).show();
                            finish();
                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(),getResources().getText(R.string.falha_envio_email), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }
}
