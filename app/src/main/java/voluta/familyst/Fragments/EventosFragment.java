package voluta.familyst.Fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import voluta.familyst.Activities.TabHostEventosActivity;
import voluta.familyst.Adapters.EventoAdapter;
import voluta.familyst.FamilystApplication;
import voluta.familyst.Interfaces.RestCallback;
import voluta.familyst.Model.Evento;
import voluta.familyst.R;
import voluta.familyst.Services.RestService;

import java.util.ArrayList;

public class EventosFragment extends Fragment {

    ListView listViewProximosEventos;

    public EventosFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_events, container, false);
        listViewProximosEventos = (ListView) rootView.findViewById(R.id.list_proximos_eventos);

        FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        fab.show();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Chama cadastro de Eventos", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                Intent intent = new Intent(getContext(), TabHostEventosActivity.class);
                startActivity(intent);
            }
        });

        return rootView;
    }

    @Override
    public void onStart(){
        super.onStart();

        final ProgressDialog dialogProgresso = ProgressDialog.show(getContext(), "Aguarde", "Atualizando Eventos");
        dialogProgresso.setCancelable(false);

        RestService.getInstance(getActivity()).CarregarEventosFamiliasAsync(new RestCallback(){
            @Override
            public void onRestResult(boolean success) {
                if (success){
                    RestService.getInstance(getActivity()).CarregarTiposEventosFamiliasAsync(new RestCallback(){
                        @Override
                        public void onRestResult(boolean success) {
                            if (success){
                                RestService.getInstance(getActivity()).CarregarUsuarioEventosFamiliasAsync(new RestCallback(){
                                    @Override
                                    public void onRestResult(boolean success) {
                                        if (success){
                                            RestService.getInstance(getActivity()).CarregarItensEventosFamiliasAsync(new RestCallback(){
                                                @Override
                                                public void onRestResult(boolean success) {
                                                    if (success){
                                                        RestService.getInstance(getActivity()).CarregarComentariosEventosFamiliasAsync(new RestCallback(){
                                                            @Override
                                                            public void onRestResult(boolean success) {
                                                                if (success){
                                                                    RestService.getInstance(getActivity()).CarregarTiposItensAsync(new RestCallback(){
                                                                        @Override
                                                                        public void onRestResult(boolean success) {
                                                                            if (success){
                                                                                Toast.makeText(getActivity(),getResources().getText(R.string.sucesso_atualizar_eventos), Toast.LENGTH_SHORT).show();
                                                                                CarregarListaEventos();
                                                                                dialogProgresso.dismiss();
                                                                            }
                                                                            else
                                                                            {
                                                                                Toast.makeText(getActivity(),getResources().getText(R.string.falha_atualizar_eventos), Toast.LENGTH_SHORT).show();
                                                                                dialogProgresso.dismiss();
                                                                            }
                                                                        }
                                                                    });
                                                                }
                                                                else
                                                                {
                                                                    Toast.makeText(getActivity(),getResources().getText(R.string.falha_atualizar_eventos), Toast.LENGTH_SHORT).show();
                                                                    dialogProgresso.dismiss();
                                                                }
                                                            }
                                                        });
                                                    }
                                                    else
                                                    {
                                                        Toast.makeText(getActivity(),getResources().getText(R.string.falha_atualizar_eventos), Toast.LENGTH_SHORT).show();
                                                        dialogProgresso.dismiss();
                                                    }
                                                }
                                            });
                                        }
                                        else
                                        {
                                            Toast.makeText(getActivity(),getResources().getText(R.string.falha_atualizar_eventos), Toast.LENGTH_SHORT).show();
                                            dialogProgresso.dismiss();
                                        }
                                    }
                                });
                            }
                            else
                            {
                                Toast.makeText(getActivity(),getResources().getText(R.string.falha_atualizar_eventos), Toast.LENGTH_SHORT).show();
                                dialogProgresso.dismiss();
                            }
                        }
                    });
                }
                else
                {
                    Toast.makeText(getActivity(),getResources().getText(R.string.falha_atualizar_eventos), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void CarregarListaEventos() {

        EventoAdapter adapter = new EventoAdapter(getContext(),
                R.layout.item_lista_eventos, carregarEventos());
        listViewProximosEventos.setAdapter(adapter);
        listViewProximosEventos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //TODO abre fragment de Evento, passando o objeto selecionado na lista

                int idEvento = ((Evento)parent.getItemAtPosition(position)).getIdEvento();

                EventoFragment fragment = new EventoFragment();
                Bundle data = new Bundle();
                data.putInt("idEvento", idEvento);

                fragment.setArguments(data);

                getFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, fragment)
                        .addToBackStack(null)
                        .commit();
            }
        });
        listViewProximosEventos.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                int idEvento = ((Evento)parent.getItemAtPosition(position)).getIdEvento();

                Intent intent = new Intent(getContext(), TabHostEventosActivity.class);
                intent.putExtra("idEvento", idEvento);
                intent.putExtra("isEdicao", true);
                startActivity(intent);

                return true;

            }
        });
    }

    private ArrayList<Evento> carregarEventos() {
        FamilystApplication familystApplication = ((FamilystApplication)getActivity().getApplication());
        return familystApplication.getFamiliaAtual().getEventos();
    }

}
