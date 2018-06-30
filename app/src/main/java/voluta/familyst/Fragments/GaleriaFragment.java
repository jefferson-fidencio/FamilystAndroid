package voluta.familyst.Fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import voluta.familyst.Activities.CadastroVideoActivity;
import voluta.familyst.Adapters.VideoAdapter;
import voluta.familyst.FamilystApplication;
import voluta.familyst.Interfaces.RestCallback;
import voluta.familyst.Model.Video;
import voluta.familyst.R;
import voluta.familyst.Services.RestService;

import java.util.ArrayList;

public class GaleriaFragment extends Fragment {

    ListView listViewVideos;

    public GaleriaFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_galeria, container, false);
        listViewVideos = (ListView) rootView.findViewById(R.id.listview_galeria_videos);

        FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), CadastroVideoActivity.class);
                startActivity(intent);
            }
        });

        return rootView;
    }

    @Override
    public void onStart(){
        super.onStart();

        // chamar progressdialog
        final ProgressDialog dialogProgresso = ProgressDialog.show(getContext(), "Aguarde", "Atualizando Vídeos.");
        dialogProgresso.setCancelable(false);
        RestService.getInstance(getActivity()).CarregarVideosFamiliasAsync(new RestCallback(){
            @Override
            public void onRestResult(boolean success) {
                if (success){
                    Toast.makeText(getActivity(),getResources().getText(R.string.sucesso_atualizar_videos), Toast.LENGTH_SHORT).show();
                    CarregarListaVideos();
                }
                else
                {
                    Toast.makeText(getActivity(),getResources().getText(R.string.falha_atualizar_videos), Toast.LENGTH_SHORT).show();
                }
                dialogProgresso.dismiss();
            }
        });
    }

    private void CarregarListaVideos() {
        VideoAdapter adapter = new VideoAdapter(getContext(),
                R.layout.item_lista_video, carregarVideos());
        listViewVideos.setAdapter(adapter);
        listViewVideos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Video video = (Video) parent.getItemAtPosition(position);
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(video.getLink())));
            }
        });
        listViewVideos.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                Video video = (Video) parent.getItemAtPosition(position);
                Intent intent = new Intent(getContext(), CadastroVideoActivity.class);
                intent.putExtra("idVideo", video.getIdVideo());
                intent.putExtra("isEdicao", true);
                startActivity(intent);

                return true;

            }
        });
    }

    private ArrayList<Video> carregarVideos() {
        FamilystApplication familystApplication = ((FamilystApplication)getActivity().getApplication());
        return familystApplication.getFamiliaAtual().getVideos();
    }

}
