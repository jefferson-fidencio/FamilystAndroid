package voluta.familyst.Fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import voluta.familyst.Activities.AlbumActivity;
import voluta.familyst.Activities.CadastroAlbumActivity;
import voluta.familyst.Adapters.AlbumAdapter;
import voluta.familyst.FamilystApplication;
import voluta.familyst.Interfaces.RestCallback;
import voluta.familyst.Model.Album;
import voluta.familyst.R;
import voluta.familyst.Services.RestService;

import java.util.ArrayList;

public class AlbumsFragment extends Fragment {

    ListView listViewAlbums;

    public AlbumsFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_albums, container, false);
        listViewAlbums = (ListView) rootView.findViewById(R.id.listview_albuns);


        FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getContext(), CadastroAlbumActivity.class);
                startActivity(intent);
            }
        });

        return rootView;
    }

    @Override
    public void onStart(){
        super.onStart();

        final ProgressDialog dialogProgresso = ProgressDialog.show(getContext(), "Aguarde", "Carregando Albuns...");
        dialogProgresso.setCancelable(false);

        // chamar progressdialog
        RestService.getInstance(getActivity()).CarregarAlbunsFamiliasAsync(new RestCallback(){
            @Override
            public void onRestResult(boolean success) {
                if (success){
                    RestService.getInstance(getActivity()).CarregarFotosAlbunsFamiliasAsync(new RestCallback(){
                        @Override
                        public void onRestResult(boolean success) {
                            if (success){
                                carregarListaAlbuns();
                            }
                            else
                            {
                                Toast.makeText(getActivity(),getResources().getText(R.string.falha_atualizar_albums), Toast.LENGTH_SHORT).show();
                            }
                            dialogProgresso.dismiss();
                        }
                    });
                }
                else
                {
                    Toast.makeText(getActivity(),getResources().getText(R.string.falha_atualizar_albums), Toast.LENGTH_SHORT).show();
                }
                //
                // dismiss progressdialog
                dialogProgresso.dismiss();
            }
        });
    }

    private void carregarListaAlbuns() {

        AlbumAdapter adapter = new AlbumAdapter(getContext(),
                R.layout.item_lista_albuns, carregarAlbuns());
        listViewAlbums.setAdapter(adapter);
        listViewAlbums.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //TODO Mudar para fragment de galeria quando este for criado
//                EventoFragment fragment = new EventoFragment();
//                getFragmentManager().beginTransaction()
//                        .replace(R.id.fragment_container, fragment)
//                        .addToBackStack(null)
//                        .commit();

                Album album = (Album)parent.getAdapter().getItem(position);

                Intent intent = new Intent(getActivity(), AlbumActivity.class);
                intent.putExtra("idAlbum", album.getIdAlbum());
                startActivity(intent);
            }
        });

        listViewAlbums.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                Album album = (Album) parent.getItemAtPosition(position);

                //TODO abrir tela de Cadastro com extras: idEvento e bool indicando edicao
                Intent intent = new Intent(getContext(), CadastroAlbumActivity.class);
                intent.putExtra("idAlbum", album.getIdAlbum());
                intent.putExtra("isEdicao", true);
                startActivity(intent);

                return true;

            }
        });

    }

    private ArrayList<Album> carregarAlbuns() {
        FamilystApplication familystApplication = ((FamilystApplication)getActivity().getApplication());
        return familystApplication.getFamiliaAtual().getAlbuns();
    }
}
