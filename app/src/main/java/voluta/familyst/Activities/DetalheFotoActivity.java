package voluta.familyst.Activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.widget.ImageView;
import android.widget.TextView;

import voluta.familyst.FamilystApplication;
import voluta.familyst.Model.Album;
import voluta.familyst.Model.Familia;
import voluta.familyst.Model.Foto;
import voluta.familyst.R;

public class DetalheFotoActivity extends BaseActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhe_foto);

        Foto foto = carregarFoto(getIntent().getExtras().getInt("idFoto"));

        byte[] decodedString = Base64.decode(foto.getDados(), Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

        TextView txtDescricao = (TextView) findViewById(R.id.descricao_foto);
        txtDescricao.setText(foto.getDescricao());

        ImageView imageView = (ImageView) findViewById(R.id.foto);
        imageView.setImageBitmap(bitmap);
    }

    private Foto carregarFoto(int idFoto) {
        FamilystApplication familystApplication = ((FamilystApplication)getApplication());
        Familia familiaSelecionada = familystApplication.getFamiliaAtual();

        //acha album por id (Melhor implementacao seria Map<Int,Album> ao inves de Arraylist... para todas as nossas listas)
        for (int i = 0 ; i < familiaSelecionada.getAlbuns().size() ; i++) {
            Album albumFor = familiaSelecionada.getAlbuns().get(i);
            for (int j = 0 ; j < albumFor.getFotos().size() ; j++)
            {
                Foto fotoFor = albumFor.getFotos().get(j);
                if (fotoFor.getIdImagem() == idFoto){
                    return fotoFor;
                }
            }
        }
        return null;
    }
}
