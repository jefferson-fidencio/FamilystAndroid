package voluta.familyst.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import voluta.familyst.Adapters.GridViewAdapter;
import voluta.familyst.FamilystApplication;
import voluta.familyst.Interfaces.RestCallback;
import voluta.familyst.Model.Album;
import voluta.familyst.Model.Familia;
import voluta.familyst.Model.Foto;
import voluta.familyst.R;
import voluta.familyst.Services.RestService;

import java.util.ArrayList;

public class AlbumActivity extends BaseActivity{

    private GridView gridView;
    private GridViewAdapter gridAdapter;
    private Album album;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);
        gridView = (GridView) findViewById(R.id.gridView);

        album = carregarAlbum(getIntent().getExtras().getInt("idAlbum"));

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_album);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),CadastroFotoActivity.class);
                intent.putExtra("idAlbum", album.getIdAlbum());
                startActivity(intent);
            }
        });

        carregarListaFotos();
    }

    private void carregarListaFotos() {
        gridAdapter = new GridViewAdapter(this, R.layout.item_gridview_album, carregarFotos());
        gridView.setAdapter(gridAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Foto foto = (Foto) parent.getItemAtPosition(position);

                //Criar Intent
                Intent intent = new Intent(getApplicationContext(), DetalheFotoActivity.class);
                intent.putExtra("idFoto", foto.getIdImagem());
                startActivity(intent);
            }
        });
    }

    @Override
    public void onStart(){
        super.onStart();

        // chamar progressdialog
        final ProgressDialog dialogProgresso = ProgressDialog.show(this, "Aguarde", "Atualizando Fotos.");
        dialogProgresso.setCancelable(false);
        RestService.getInstance(this).CarregarFotosAlbunsFamiliasAsync(new RestCallback(){
            @Override
            public void onRestResult(boolean success) {
                if (success){
                    Toast.makeText(AlbumActivity.this,getResources().getText(R.string.sucesso_atualizar_fotos), Toast.LENGTH_SHORT).show();
                    carregarListaFotos();
                }
                else
                {
                    Toast.makeText(AlbumActivity.this,getResources().getText(R.string.falha_atualizar_fotos), Toast.LENGTH_SHORT).show();
                }
                // dismiss progressdialog
                dialogProgresso.dismiss();
            }
        });
    }

    //preparando dados escrotos para a gridView
    private ArrayList<Foto> carregarFotos(){
        ArrayList<Foto> fotos = album.getFotos();
        if (fotos == null)
            return new ArrayList<>();
        return fotos;
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
