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
import voluta.familyst.R;
import voluta.familyst.Services.RestService;

import java.util.ArrayList;

public class CadastroFamiliaActivity extends BaseActivity {

    private EditText nomeFamilia;
    private EditText localFamilia;
    private EditText descricaoFamilia;
    private Button btnCadastrarFamilia;
    private ImageButton btnRemoverFamilia;
    private boolean isEdicao = false;
    private Familia _familia;
    private TextView cabecalhoFamilia;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_familia);

        nomeFamilia = (EditText) findViewById(R.id.txt_nome_familia_cadastro);
        localFamilia = (EditText) findViewById(R.id.txt_local_familia_cadastro);
        descricaoFamilia = (EditText) findViewById(R.id.txt_descricao_familia_cadastro);
        btnCadastrarFamilia = (Button) findViewById(R.id.btn_cadastrar_familia);
        btnRemoverFamilia = (ImageButton) findViewById(R.id.btn_remover_familia);
        cabecalhoFamilia = (TextView) findViewById(R.id.txt_cadastro_cabecalho);
        btnRemoverFamilia.setVisibility(View.GONE);

        isEdicao = getIntent().getBooleanExtra("isEdicao", false);
        if(isEdicao)
        {
            _familia = carregarFamilia(getIntent().getExtras().getInt("idFamilia"));
            nomeFamilia.setText(_familia.getNome());
            localFamilia.setText(_familia.getLocal());
            descricaoFamilia.setText(_familia.getDescricao());

            btnCadastrarFamilia.setText("Salvar");
            btnRemoverFamilia.setVisibility(View.VISIBLE);
            cabecalhoFamilia.setText("Edite a familia selecionada:");
        }

        btnRemoverFamilia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(CadastroFamiliaActivity.this)
                        .setTitle("Alerta!")
                        .setMessage("Deseja remover a familia selecionada?")
                        .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                final ProgressDialog dialogProgresso = ProgressDialog.show(CadastroFamiliaActivity.this, "Aguarde", "Excluindo familia.");
                                dialogProgresso.setCancelable(false);
                                RestService.getInstance(CadastroFamiliaActivity.this).RemoverFamilia( _familia.getIdFamilia(), new RestCallback(){
                                    @Override
                                    public void onRestResult(boolean success) {
                                        if (success){
                                            finish();
                                        }
                                        else
                                        {
                                            Toast.makeText(getApplicationContext(),getResources().getText(R.string.falha_remover_familia), Toast.LENGTH_SHORT).show();
                                        }
                                        dialogProgresso.dismiss();
                                    }
                                });

                                dialog.dismiss();
                            }
                        }).show();
            }
        });

        btnCadastrarFamilia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isEdicao)
                {
                    final ProgressDialog dialogProgresso = ProgressDialog.show(CadastroFamiliaActivity.this, "Aguarde", "Cadastrando familia.");
                    dialogProgresso.setCancelable(false);
                    RestService.getInstance(CadastroFamiliaActivity.this).EnviarFamilia( nomeFamilia.getText().toString(), descricaoFamilia.getText().toString(), localFamilia.getText().toString(), new RestCallback(){
                        @Override
                        public void onRestResult(boolean success) {
                            if (success){
                                dialogProgresso.dismiss();
                                finish();
                            }
                            else
                            {
                                Toast.makeText(getApplicationContext(),getResources().getText(R.string.falha_cadastro_familia), Toast.LENGTH_SHORT).show();
                            }
                            dialogProgresso.dismiss();
                        }
                    });
                }
                else {
                    final ProgressDialog dialogProgresso = ProgressDialog.show(CadastroFamiliaActivity.this, "Aguarde", "Editando familia.");
                    dialogProgresso.setCancelable(false);
                    RestService.getInstance(CadastroFamiliaActivity.this).EditarFamilia( nomeFamilia.getText().toString(), descricaoFamilia.getText().toString(),  localFamilia.getText().toString(), _familia.getIdFamilia(), new RestCallback(){
                        @Override
                        public void onRestResult(boolean success) {
                            if (success){
                                dialogProgresso.dismiss();
                                finish();
                            }
                            else
                            {
                                Toast.makeText(getApplicationContext(),getResources().getText(R.string.falha_editar_familia), Toast.LENGTH_SHORT).show();
                            }
                            dialogProgresso.dismiss();
                        }
                    });
                }
            }
        });

    }

    private Familia carregarFamilia(int idFamilia) {
        FamilystApplication familystApplication = ((FamilystApplication)getApplication());
        ArrayList<Familia> familias = familystApplication.get_usuarioLogado().getFamilias();
        for (int i = 0 ; i < familias.size() ; i++) {
            Familia familia = familias.get(i);
            if (familia.getIdFamilia() == idFamilia){
                return familia;
            }
        }
        return null;
    }
}
