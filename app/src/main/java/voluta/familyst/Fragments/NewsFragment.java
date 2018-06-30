package voluta.familyst.Fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import voluta.familyst.Activities.CadastroNoticiaActivity;
import voluta.familyst.Adapters.NewsAdapter;
import voluta.familyst.FamilystApplication;
import voluta.familyst.Interfaces.RestCallback;
import voluta.familyst.Model.Noticia;
import voluta.familyst.R;
import voluta.familyst.Services.RestService;

import java.util.ArrayList;

public class NewsFragment extends Fragment {

    private NewsAdapter noticiasAdapter;
    private ArrayList<Noticia> listaNoticiasCardView = new ArrayList<>();
    private RecyclerView recList;

    public NewsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_news, container, false);
        recList = (RecyclerView) rootView.findViewById(R.id.cardlist_news);
        recList.setHasFixedSize(true);
        recList.setLayoutManager(new LinearLayoutManager(getActivity()));

        FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Chama cadastro de notícias", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                Intent intent = new Intent(getContext(), CadastroNoticiaActivity.class);
                startActivity(intent);
            }
        });

        return rootView;
    }

    private void carregarListaNoticias() {

        noticiasAdapter = new NewsAdapter(getActivity());
        recList.setAdapter(noticiasAdapter);
        listaNoticiasCardView = carregarNoticias();
        noticiasAdapter.setListaNoticia(listaNoticiasCardView);

    }

    @Override
    public void onStart(){
        super.onStart();

        try {

            //TODO chamar progressdialog
            final ProgressDialog dialogProgresso = ProgressDialog.show(getContext(), "Aguarde", "Atualizando Noticias");
            dialogProgresso.setCancelable(false);

            RestService.getInstance(getActivity()).CarregarNoticiasFamiliasAsync(new RestCallback() {
                @Override
                public void onRestResult(boolean success) {
                    if (success) {
                        RestService.getInstance(getActivity()).CarregarComentariosNoticiasFamiliasAsync(new RestCallback() {
                            @Override
                            public void onRestResult(boolean success) {
                                if (success) {
                                    RestService.getInstance(getActivity()).CarregarUsuariosNoticiasFamiliasAsync(new RestCallback() {
                                        @Override
                                        public void onRestResult(boolean success) {
                                            if (success) {
                                                carregarListaNoticias();
                                            } else {
                                                Toast.makeText(getActivity(), getResources().getText(R.string.falha_atualizar_noticias), Toast.LENGTH_SHORT).show();
                                            }
                                            dialogProgresso.dismiss();
                                        }
                                    });
                                } else {
                                    Toast.makeText(getActivity(), getResources().getText(R.string.falha_atualizar_noticias), Toast.LENGTH_SHORT).show();
                                    dialogProgresso.dismiss();
                                }
                            }
                        });
                    } else {
                        Toast.makeText(getActivity(), getResources().getText(R.string.falha_atualizar_noticias), Toast.LENGTH_SHORT).show();
                        dialogProgresso.dismiss();
                    }
                }
            });
        }
        catch (Exception ex)
        {
            //ignore
        }
    }

    private ArrayList<Noticia> carregarNoticias() {
        FamilystApplication familystApplication = ((FamilystApplication)getActivity().getApplication());
        return familystApplication.getFamiliaAtual().getNoticias();
    }

}
