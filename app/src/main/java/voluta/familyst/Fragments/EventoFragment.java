package voluta.familyst.Fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import voluta.familyst.Adapters.ComentarioAdapter;
import voluta.familyst.Adapters.ItemEventoAdapter;
import voluta.familyst.FamilystApplication;
import voluta.familyst.Interfaces.RestCallback;
import voluta.familyst.Model.Evento;
import voluta.familyst.R;
import voluta.familyst.Services.RestService;

import java.util.ArrayList;

public class EventoFragment extends Fragment {

    ListView listViewItens;
    ListView listViewComentarios;
    TextView textViewCriadorEvento;
    TextView textViewDescricaoEvento;
    TextView textViewLocalEvento;
    Button btnConfirmarPresenca;
    TextView txtConfirmarPresenca;
    ImageButton btnEnviarComentario;
    EditText edtComentarioEnviar;
    Evento evento;

    public EventoFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_event, container, false);
        listViewItens= (ListView) rootView.findViewById(R.id.list_itens);
        listViewComentarios = (ListView) rootView.findViewById(R.id.list_comentarios);
        textViewCriadorEvento = (TextView) rootView.findViewById(R.id.criadorEvento);
        textViewDescricaoEvento = (TextView) rootView.findViewById(R.id.descricaoEvento);
        textViewLocalEvento = (TextView) rootView.findViewById(R.id.localEvento);
        btnConfirmarPresenca = (Button) rootView.findViewById(R.id.btn_confirmar_presenca);
        txtConfirmarPresenca = (TextView) rootView.findViewById(R.id.txt_presenca_confirmada);
        txtConfirmarPresenca.setVisibility(View.GONE);
        btnConfirmarPresenca.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //enviar confirmacao de presenca!
                btnConfirmarPresenca.setVisibility(View.GONE);
                txtConfirmarPresenca.setVisibility(View.VISIBLE);
            }
        });
        edtComentarioEnviar = (EditText) rootView.findViewById(R.id.txt_comentario_enviar);
        btnEnviarComentario = (ImageButton) rootView.findViewById(R.id.btn_enviar_comentario);
        btnEnviarComentario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RestService.getInstance(getActivity()).EnviarComentarioEvento( edtComentarioEnviar.getText().toString(), evento, new RestCallback(){
                    @Override
                    public void onRestResult(boolean success) {
                        if (success){
                            Toast.makeText(getActivity(),getResources().getText(R.string.sucesso_cadastro_comentario), Toast.LENGTH_SHORT).show();

                            // chamar progressdialog
                            final ProgressDialog dialogProgresso = ProgressDialog.show(getActivity(), "Aguarde", "Atualizando Comentários");
                            dialogProgresso.setCancelable(false);

                            RestService.getInstance(getActivity()).CarregarComentariosEventosFamiliasAsync(new RestCallback(){
                                @Override
                                public void onRestResult(boolean success) {
                                    if (success){
                                        Toast.makeText(getActivity(),getResources().getText(R.string.sucesso_atualizar_comentarios), Toast.LENGTH_SHORT).show();
                                        CarregarListaComentarios();
                                        edtComentarioEnviar.setText("");

                                    }
                                    else
                                    {
                                        Toast.makeText(getActivity(),getResources().getText(R.string.falha_atualizar_comentarios), Toast.LENGTH_SHORT).show();
                                    }
                                    // dismiss progressdialog
                                    dialogProgresso.dismiss();
                                }
                            });
                        }
                        else
                        {
                            Toast.makeText(getActivity(),getResources().getText(R.string.falha_cadastro_comentario), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        int idEvento = getArguments().getInt("idEvento");
        evento = carregarEvento(idEvento);

        textViewCriadorEvento.setText(evento.getUsuarioEvento().getNome());
        textViewDescricaoEvento.setText(evento.getDescricao());
        textViewLocalEvento.setText(evento.getLocal());

        FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        fab.hide();

        CarregarListaComentarios();

        return rootView;
    }

    private Evento carregarEvento(int idEvento) {
        FamilystApplication familystApplication = ((FamilystApplication)getActivity().getApplication());
        ArrayList<Evento> eventos = familystApplication.getFamiliaAtual().getEventos();
        for (int i = 0 ; i < eventos.size() ; i++)
        {
            Evento evento = eventos.get(i);
            if (evento.getIdEvento() == idEvento)
                return evento;
        }
        return null;
    }

    private void CarregarListaComentarios() {

        ItemEventoAdapter adapter = new ItemEventoAdapter(getContext(),
                R.layout.item_lista_itensevento, evento.getItensEvento());
        ComentarioAdapter adapter2 = new ComentarioAdapter(getContext(),
                R.layout.item_lista_comentarios, evento.getComentariosEvento());

        listViewItens.setAdapter(adapter);
        listViewComentarios.setAdapter(adapter2);
        listViewItens.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //TODO Implementar surgimento do botão "levar"
            }
        });

    }

    @Override
    public void onStart(){
        super.onStart();

        // chamar progressdialog
        final ProgressDialog dialogProgresso = ProgressDialog.show(getContext(), "Aguarde", "Atualizando comentários.");
        dialogProgresso.setCancelable(false);
        RestService.getInstance(getActivity()).CarregarComentariosEventosFamiliasAsync(new RestCallback(){
            @Override
            public void onRestResult(boolean success) {
                if (success){
                    Toast.makeText(getActivity(),getResources().getText(R.string.sucesso_atualizar_comentarios), Toast.LENGTH_SHORT).show();
                    CarregarListaComentarios();
                }
                else
                {
                    Toast.makeText(getActivity(),getResources().getText(R.string.falha_atualizar_comentarios), Toast.LENGTH_SHORT).show();
                }
                // dismiss progressdialog
                dialogProgresso.dismiss();
            }
        });
    }
}
