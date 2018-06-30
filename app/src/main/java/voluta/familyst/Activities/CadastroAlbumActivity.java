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
import voluta.familyst.Model.Album;
import voluta.familyst.Model.Familia;
import voluta.familyst.R;
import voluta.familyst.Services.RestService;

public class CadastroAlbumActivity extends BaseActivity {

    private EditText nomeAlbum;
    private EditText descricaoAlbum;
    private TextView cabecalhoAlbum;
    private Button btnCadastrarAlbum;
    private ImageButton btnRemoverAlbum;
    private Album album;
    private boolean isEdicao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_album);

        nomeAlbum = (EditText) findViewById(R.id.txt_nome_album_cadastro);
        descricaoAlbum = (EditText) findViewById(R.id.txt_descricao_album_cadastro);
        cabecalhoAlbum = (TextView) findViewById(R.id.txt_cadastro_album_cabecalho);
        btnCadastrarAlbum = (Button) findViewById(R.id.btn_cadastrar_album);
        btnRemoverAlbum = (ImageButton) findViewById(R.id.btn_remover_album);
        btnRemoverAlbum.setVisibility(View.GONE);

        isEdicao = getIntent().getBooleanExtra("isEdicao", false);
        if(isEdicao)
        {
            album = carregarAlbum(getIntent().getExtras().getInt("idAlbum"));
            nomeAlbum.setText(album.getNome());
            descricaoAlbum.setText(album.getDescricao());

            btnCadastrarAlbum.setText("Salvar");
            btnRemoverAlbum.setVisibility(View.VISIBLE);
            cabecalhoAlbum.setText("Edite o Album selecionado:");
        }
        btnRemoverAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(CadastroAlbumActivity.this)
                        .setTitle("Alerta!")
                        .setMessage("Deseja remover o album selecionado?")
                        .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                final ProgressDialog dialogProgresso = ProgressDialog.show(CadastroAlbumActivity.this, "Aguarde", "Excluindo Album.");
                                dialogProgresso.setCancelable(false);
                                RestService.getInstance(CadastroAlbumActivity.this).RemoverAlbum( album.getIdAlbum(), new RestCallback(){
                                    @Override
                                    public void onRestResult(boolean success) {
                                        if (success){
                                            finish();
                                        }
                                        else
                                        {
                                            Toast.makeText(getApplicationContext(),getResources().getText(R.string.falha_remover_album), Toast.LENGTH_SHORT).show();
                                        }
                                        dialogProgresso.dismiss();
                                    }
                                });

                                dialog.dismiss();
                            }
                        }).show();
            }
        });

        btnCadastrarAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!isEdicao)
                {
                    final ProgressDialog dialogProgresso = ProgressDialog.show(CadastroAlbumActivity.this, "Aguarde", "Cadastrando Album.");
                    dialogProgresso.setCancelable(false);
                    RestService.getInstance(CadastroAlbumActivity.this).EnviarAlbum( nomeAlbum.getText().toString(), descricaoAlbum.getText().toString(), new RestCallback(){
                        @Override
                        public void onRestResult(boolean success) {
                            if (success){
                                dialogProgresso.dismiss();
                                finish();
                            }
                            else
                            {
                                Toast.makeText(getApplicationContext(),getResources().getText(R.string.falha_cadastro_album), Toast.LENGTH_SHORT).show();
                            }
                            dialogProgresso.dismiss();
                        }
                    });
                }
                else {
                    final ProgressDialog dialogProgresso = ProgressDialog.show(CadastroAlbumActivity.this, "Aguarde", "Editando Album.");
                    dialogProgresso.setCancelable(false);
                    RestService.getInstance(CadastroAlbumActivity.this).EditarAlbum( nomeAlbum.getText().toString(), descricaoAlbum.getText().toString(), album.getIdAlbum(), new RestCallback(){
                        @Override
                        public void onRestResult(boolean success) {
                            if (success){
                                dialogProgresso.dismiss();
                                finish();
                            }
                            else
                            {
                                Toast.makeText(getApplicationContext(),getResources().getText(R.string.falha_editar_album), Toast.LENGTH_SHORT).show();
                            }
                            dialogProgresso.dismiss();
                        }
                    });
                }
            }
        });
    }

    private Album carregarAlbum(int idAlbum) {
        FamilystApplication familystApplication = ((FamilystApplication)getApplication());
        Familia familiaSelecionada = familystApplication.getFamiliaAtual();

        //acha album por id (Melhor implementacao seria Map<Int,Album> ao inves de Arraylist... para todas as nossas listas)
        for (int i = 0 ; i < familiaSelecionada.getAlbuns().size() ; i++) {
            Album albumFor = familiaSelecionada.getAlbuns().get(i);
            if (albumFor.getIdAlbum() == idAlbum){
                return albumFor;
            }
        }
        return null;
    }
}
