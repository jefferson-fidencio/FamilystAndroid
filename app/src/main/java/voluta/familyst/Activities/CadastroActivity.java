package voluta.familyst.Activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.util.ArrayMap;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import voluta.familyst.FamilystApplication;
import voluta.familyst.Interfaces.RestCallback;
import voluta.familyst.Model.Usuario;
import voluta.familyst.R;
import voluta.familyst.Services.JsonRestRequest;
import voluta.familyst.Services.RestService;

import org.json.JSONObject;

import java.util.Map;

public class CadastroActivity extends BaseActivity {

    EditText txtNome;
    EditText txtEmail;
    EditText txtSenha;
    Button btnConfirmar;
    TextView cabecalho;
    Usuario _usuario;
    boolean isEdicao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        txtNome = (EditText) findViewById(R.id.txt_nome_cadastro);
        txtEmail = (EditText) findViewById(R.id.txt_email_cadastro);
        txtSenha = (EditText) findViewById(R.id.txt_senha_cadastro);
        cabecalho = (TextView) findViewById(R.id.txt_cadastro_cabecalho);
        btnConfirmar = (Button) findViewById(R.id.btn_cadastrar);

        isEdicao = getIntent().getBooleanExtra("isEdicao", false);
        if(isEdicao) {
            _usuario = carregarUsuario();
            txtNome.setText(_usuario.getNome());
            txtEmail.setText(_usuario.getEmail());
            txtEmail.setEnabled(false);
            txtSenha.setText("*******");
            txtSenha.setEnabled(false);
            btnConfirmar.setText("Salvar");
            cabecalho.setText("Edite seus dados:");
            btnConfirmar.setOnClickListener((v) -> EditarUsuario());
        }
        else
        {
            btnConfirmar.setOnClickListener((v) -> CadastrarUsuario());
        }

    }

    private Usuario carregarUsuario() {
        FamilystApplication familystApplication = (FamilystApplication) getApplication();
        return familystApplication.get_usuarioLogado();
    }

    private void EditarUsuario() {
        try {
            final ProgressDialog dialogProgresso = ProgressDialog.show(CadastroActivity.this, "Aguarde", "Editando usuario.");
            dialogProgresso.setCancelable(false);
            RestService.getInstance(CadastroActivity.this).EditarUsuario(txtNome.getText().toString(), _usuario.getIdUsuario(), new RestCallback(){
                @Override
                public void onRestResult(boolean success) {
                    if (success){
                        _usuario.setNome(txtNome.getText().toString());
                        SharedPreferences prefs = ((FamilystApplication)getApplication()).getSharedPreferences();
                        prefs.edit().putString("nomeUsuario", txtNome.getText().toString()).apply();
                        finish();
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(),getResources().getText(R.string.falha_editar_usuario), Toast.LENGTH_SHORT).show();
                    }
                    dialogProgresso.dismiss();
                }
            });
        }
        catch (Exception ex){
            Log.d("Error", "Erro ao editar usuario: " + ex.getLocalizedMessage());
        }
    }

    private void CadastrarUsuario() {
        try {
            //monta url requisicao
            String url = "usuarios";

            //monta headers adicionais
            Map headers = new ArrayMap();

            //monta body
            JSONObject postBody = new JSONObject();
            postBody.put("nome", txtNome.getText());
            postBody.put("email", txtEmail.getText());
            postBody.put("senha", txtSenha.getText());

            //abre dialog
            final ProgressDialog dialogProgresso = ProgressDialog.show(CadastroActivity.this, "Aguarde", "Cadastrando Usuario.");
            dialogProgresso.setCancelable(false);

            //monta requisicao
            JsonRestRequest jsonRequest = new JsonRestRequest(getApplication(), Request.Method.POST, false, url, headers, postBody,
                    new Response.Listener<JsonRestRequest.JsonRestResponse>() {
                        @Override
                        public void onResponse(JsonRestRequest.JsonRestResponse jsonRestResponse) {
                            if (jsonRestResponse.get_httpStatusCode() == 201) //created
                            {
                                String localizacaoRecurso = jsonRestResponse.get_headers().get("Location").toString();
                                int idRecursoCriado = Integer.parseInt(localizacaoRecurso.substring(localizacaoRecurso.lastIndexOf('/') + 1));
                                onSucessoCadastro(idRecursoCriado);
                                dialogProgresso.dismiss();
                            }
                            else //erros
                            {
                                onFalhaCadastro("Retorno HTTP não esperado.");
                            }
                        }
                    },
                    error -> onFalhaCadastro(error.getMessage())
            );

            //envia requisicao
            RestService.getInstance(this).addToRequestQueue(jsonRequest);
        }
        catch (Exception ex){
            Log.d("Error", "Erro ao cadastrar usuario: " + ex.getLocalizedMessage());
        }
    }

    private void onFalhaCadastro(String msgError) {
        runOnUiThread(()->{
            new AlertDialog.Builder(this)
                    .setTitle("Falha")
                    .setMessage("Falha ao cadastrar usuário. Tente novamente.")
                    .setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            //Falha no cadastro
            setResult(RESULT_CANCELED);
            finish();
        });
    }

    private void onSucessoCadastro(int idRecursoCriado) {
        runOnUiThread(()->{
            //Cadastrado com sucesso
            Bundle bundle = new Bundle();
            bundle.putString("nome", txtNome.getText().toString());
            bundle.putString("email", txtEmail.getText().toString());
            bundle.putString("senha", txtSenha.getText().toString());
            Intent it = new Intent();
            it.putExtras(bundle);
            setResult(RESULT_OK, it);
            finish();
        });
    }
}
