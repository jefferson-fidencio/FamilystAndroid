package voluta.familyst.Activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import voluta.familyst.FamilystApplication;
import voluta.familyst.Interfaces.RestCallback;
import voluta.familyst.Model.Album;
import voluta.familyst.Model.Familia;
import voluta.familyst.R;
import voluta.familyst.Services.JsonRestRequest;
import voluta.familyst.Services.RestService;

import java.io.IOException;

public class CadastroFotoActivity extends AppCompatActivity {

    Button btnTirarFoto;
    Button btnEscolherFoto;
    Button btnSalvarFoto;
    ImageView imgSelecionada;
    EditText txtDescricaoFoto;
    Bitmap bitmapImage;
    Album album;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0) {
            Bitmap imagem = (Bitmap) data.getExtras().get("data");
            imgSelecionada.setImageBitmap(imagem);
        }
        else if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK)
            {
                if (data != null)
                {
                    try
                    {
                        bitmapImage = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                        imgSelecionada.setImageBitmap(bitmapImage);
                    } catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED)
            {
                Toast.makeText(getApplicationContext(), "Operação Cancelada", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_foto);

        album = carregarAlbum(getIntent().getExtras().getInt("idAlbum"));

        btnTirarFoto = (Button) findViewById(R.id.btn_tirar_foto);
        btnEscolherFoto = (Button) findViewById(R.id.btn_foto_galeria);
        btnSalvarFoto = (Button) findViewById(R.id.btn_cadastrar_foto);
        txtDescricaoFoto = (EditText) findViewById(R.id.txt_descricao_foto_cadastro);
        imgSelecionada = (ImageView) findViewById(R.id.img_cadastro_foto);
        btnTirarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Abrir intent de camera
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 0);
            }
        });
        btnEscolherFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentGaleria = new Intent();
                intentGaleria.setType("image/*");
                intentGaleria.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intentGaleria, "Selecione a foto"), 1);
            }
        });
        btnSalvarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog dialogProgresso = ProgressDialog.show(CadastroFotoActivity.this, "Aguarde", "Cadastrando Foto.");
                dialogProgresso.setCancelable(false);
                RestService.getInstance(CadastroFotoActivity.this).EnviarFoto( txtDescricaoFoto.getText().toString(), bitmapImage, album, new RestCallback(){
                    @Override
                    public void onRestResult(boolean success) {
                        if (success){
                            Toast.makeText(getApplicationContext(),getResources().getText(R.string.sucesso_cadastro_foto), Toast.LENGTH_SHORT).show();
                            dialogProgresso.dismiss();
                            finish();
                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(),getResources().getText(R.string.falha_cadastro_foto), Toast.LENGTH_SHORT).show();
                            dialogProgresso.dismiss();
                        }
                    }
                });
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
