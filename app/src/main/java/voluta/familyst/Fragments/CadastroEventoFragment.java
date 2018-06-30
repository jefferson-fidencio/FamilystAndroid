package voluta.familyst.Fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import voluta.familyst.Adapters.TipoEventoAdapter;
import voluta.familyst.FamilystApplication;
import voluta.familyst.Interfaces.RestCallback;
import voluta.familyst.Model.Evento;
import voluta.familyst.Model.Item;
import voluta.familyst.Model.TipoEvento;
import voluta.familyst.R;
import voluta.familyst.Services.RestService;

import java.util.ArrayList;

public class CadastroEventoFragment extends Fragment {

    private Spinner spnTipoEvento;
    private EditText nomeEvento;
    private EditText localEvento;
    private EditText descricaoEvento;
    private Button btnCadastrarEvento;
    private ImageButton btnRemover;
    private TextView cabecalho;
    private boolean isEdicao;
    private Evento _evento;

    public CadastroEventoFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_cadastro_evento, container, false);

        nomeEvento = (EditText) rootView.findViewById(R.id.txt_nome_evento_cadastro);
        localEvento = (EditText) rootView.findViewById(R.id.txt_local_evento_cadastro);
        descricaoEvento = (EditText) rootView.findViewById(R.id.txt_descricao_evento_cadastro);
        spnTipoEvento = (Spinner) rootView.findViewById(R.id.spn_tipo_evento);
        btnCadastrarEvento = (Button) rootView.findViewById(R.id.btn_cadastrar_evento);
        cabecalho = (TextView) rootView.findViewById(R.id.txt_cadastro_cabecalho);
        btnRemover = (ImageButton) rootView.findViewById(R.id.btn_remover);

        ArrayList<TipoEvento> tiposEventos = ((FamilystApplication)getActivity().getApplication()).getTiposEventos();
        TipoEventoAdapter adapter = new TipoEventoAdapter(getActivity(),
                R.layout.item_lista_tipoevento, tiposEventos);
        spnTipoEvento.setAdapter(adapter);

        isEdicao = getArguments().getBoolean("isEdicao", false);
        if(isEdicao)
        {
            int idEvento = getArguments().getInt("idEvento");
            _evento = carregarEvento(idEvento);

            nomeEvento.setText(_evento.getNome());
            descricaoEvento.setText(_evento.getDescricao());
            localEvento.setText(_evento.getLocal());

            for (int i = 0 ; i< spnTipoEvento.getAdapter().getCount() ; i++)
            {
                TipoEvento tipoEvento = (TipoEvento) spnTipoEvento.getItemAtPosition(i);
                if (tipoEvento.getIdTipoEvento() == _evento.getIdTipoEvento())
                {
                    spnTipoEvento.setSelection(i);
                    break;
                }
            }

            btnCadastrarEvento.setText("Salvar");
            btnRemover.setVisibility(View.VISIBLE);
            cabecalho.setText("Edite o item selecionado:");
        }

        btnRemover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog dialogProgresso = ProgressDialog.show(getActivity(), "Aguarde", "Excluindo item.");
                dialogProgresso.setCancelable(false);
                RestService.getInstance(getActivity()).RemoverEvento( _evento.getIdEvento(), new RestCallback(){
                    @Override
                    public void onRestResult(boolean success) {
                        if (success){
                            getActivity().finish();
                        }
                        else
                        {
                            Toast.makeText(getActivity(),getResources().getText(R.string.falha_remover_evento), Toast.LENGTH_SHORT).show();
                        }
                        dialogProgresso.dismiss();
                    }
                });
            }
        });
        btnCadastrarEvento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //HACK
                String tag = "android:switcher:" + R.id.viewPager + ":" + 1;
                ItensCadastroEventoFragment f = (ItensCadastroEventoFragment)getFragmentManager().findFragmentByTag(tag);
                ArrayList<Item> itensAdicionados = f._itensAdicionados;

                if (!isEdicao) {
                    final ProgressDialog dialogProgresso = ProgressDialog.show(getContext(), "Aguarde", "Cadastrando evento.");
                    dialogProgresso.setCancelable(false);

                    RestService.getInstance(getActivity()).EnviarEvento(
                            nomeEvento.getText().toString(),
                            descricaoEvento.getText().toString(),
                            localEvento.getText().toString(),
                            ((TipoEvento) spnTipoEvento.getSelectedItem()).getIdTipoEvento(),
                            itensAdicionados,
                            new RestCallback() {
                                @Override
                                public void onRestResult(boolean success) {
                                    if (success) {
                                        getActivity().finish();
                                    } else {
                                        Toast.makeText(getContext(), getResources().getText(R.string.falha_cadastro_evento), Toast.LENGTH_SHORT).show();
                                    }
                                    dialogProgresso.dismiss();
                                }
                            });
                }
                else {
                    final ProgressDialog dialogProgresso = ProgressDialog.show(getContext(), "Aguarde", "Editando item.");
                    dialogProgresso.setCancelable(false);
                    RestService.getInstance(getContext()).EditarEvento( nomeEvento.getText().toString(), descricaoEvento.getText().toString(),localEvento.getText().toString(), ((TipoEvento) spnTipoEvento.getSelectedItem()).getIdTipoEvento(), _evento.getIdEvento(), new RestCallback(){
                        @Override
                        public void onRestResult(boolean success) {
                            if (success){
                                getActivity().finish();
                            }
                            else
                            {
                                Toast.makeText(getContext(),getResources().getText(R.string.falha_editar_evento), Toast.LENGTH_SHORT).show();
                            }
                            dialogProgresso.dismiss();
                        }
                    });
                }
            }
        });

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

}
