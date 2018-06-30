package voluta.familyst.Activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import voluta.familyst.FamilystApplication;
import voluta.familyst.Interfaces.RestCallback;
import voluta.familyst.Model.Familia;
import voluta.familyst.Model.Noticia;
import voluta.familyst.R;
import voluta.familyst.Services.RestService;

import java.util.ArrayList;

public class CadastroNoticiaActivity extends BaseActivity {

    private EditText descricao;
    private Button btnEnviar;
    private TextView cabecalho;
    private ImageButton btnRemover;
    private boolean isEdicao;
    private Noticia noticia;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_noticia);

        descricao = (EditText) findViewById(R.id.txt_descricao_noticia_cadastro);
        btnEnviar = (Button) findViewById(R.id.btn_enviar_noticia_cadastro);
        btnRemover = (ImageButton) findViewById(R.id.btn_remover);
        btnRemover.setVisibility(View.GONE);
        cabecalho = (TextView) findViewById(R.id.txt_cadastro_cabecalho);

        isEdicao = getIntent().getBooleanExtra("isEdicao", false);
        if(isEdicao)
        {
            noticia = carregarNoticia(getIntent().getExtras().getInt("idNoticia"));
            descricao.setText(noticia.getDescricao());

            btnEnviar.setText("Salvar");
            btnRemover.setVisibility(View.VISIBLE);
            cabecalho.setText("Edite o item selecionado:");
        }
        btnRemover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new AlertDialog.Builder(CadastroNoticiaActivity.this)
                        .setTitle("Alerta")
                        .setMessage("Deseja remover a noticia selecionada?")
                        .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                final ProgressDialog dialogProgresso = ProgressDialog.show(CadastroNoticiaActivity.this, "Aguarde", "Excluindo item.");
                                dialogProgresso.setCancelable(false);
                                RestService.getInstance(CadastroNoticiaActivity.this).RemoverNoticia( noticia.getIdNoticia(), new RestCallback(){
                                    @Override
                                    public void onRestResult(boolean success) {
                                        if (success){
                                            dialogProgresso.dismiss();
                                            finish();
                                        }
                                        else
                                        {
                                            Toast.makeText(getApplicationContext(),getResources().getText(R.string.falha_remover_noticia), Toast.LENGTH_SHORT).show();
                                        }
                                        dialogProgresso.dismiss();
                                    }
                                });
                                dialog.dismiss();
                            }
                        }).show();
            }
        });

        btnEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isEdicao)
                {
                    final ProgressDialog dialogProgresso = ProgressDialog.show(CadastroNoticiaActivity.this, "Aguarde", "Cadastrando item.");
                    dialogProgresso.setCancelable(false);
                    RestService.getInstance(CadastroNoticiaActivity.this).EnviarNoticia( descricao.getText().toString(), new RestCallback(){
                        @Override
                        public void onRestResult(boolean success) {
                            if (success){
                                Toast.makeText(getApplicationContext(),getResources().getText(R.string.sucesso_cadastro_noticia), Toast.LENGTH_SHORT).show();
                                dialogProgresso.dismiss();
                                finish();
                            }
                            else
                            {
                                Toast.makeText(getApplicationContext(),getResources().getText(R.string.falha_cadastro_noticia), Toast.LENGTH_SHORT).show();
                                dialogProgresso.dismiss();
                            }
                        }
                    });
                }
                else
                {
                    final ProgressDialog dialogProgresso = ProgressDialog.show(CadastroNoticiaActivity.this, "Aguarde", "Editando item.");
                    dialogProgresso.setCancelable(false);
                    RestService.getInstance(CadastroNoticiaActivity.this).EditarNoticia( descricao.getText().toString(), noticia.getIdNoticia(), new RestCallback(){
                        @Override
                        public void onRestResult(boolean success) {
                            if (success){
                                dialogProgresso.dismiss();
                                finish();
                            }
                            else
                            {
                                Toast.makeText(getApplicationContext(),getResources().getText(R.string.falha_editar_noticia), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    dialogProgresso.dismiss();
                }
            }
        });
    }

    private Noticia carregarNoticia(int idNoticia) {
        FamilystApplication familystApplication = ((FamilystApplication)getApplication());
        Familia familiaAtual = familystApplication.getFamiliaAtual();
        ArrayList<Noticia> noticias = familiaAtual.getNoticias();
        for (int i = 0; i < noticias.size() ; i++)
        {
            Noticia noticia = noticias.get(i);
            if (noticia.getIdNoticia() == idNoticia)
            {
                return noticia;
            }
        }

        return null;
    }
}
