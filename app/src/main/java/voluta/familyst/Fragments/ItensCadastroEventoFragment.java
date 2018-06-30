package voluta.familyst.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import voluta.familyst.Adapters.ItemEventoAdapter;
import voluta.familyst.Adapters.TipoItemAdapter;
import voluta.familyst.FamilystApplication;
import voluta.familyst.Model.Evento;
import voluta.familyst.Model.Item;
import voluta.familyst.Model.TipoItem;
import voluta.familyst.R;

import java.util.ArrayList;

public class ItensCadastroEventoFragment extends Fragment {


    private Spinner spnTipoItem;
    private EditText txtQuantidadeItem;
    private Button addItem;
    private ListView listItens;
    public ArrayList<Item> _itensAdicionados;
    private boolean isEdicao;
    private Evento _evento;

    public ItensCadastroEventoFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_itens_cadastro_evento, container, false);

        spnTipoItem = (Spinner) rootView.findViewById(R.id.spn_tipo_item);
        txtQuantidadeItem = (EditText) rootView.findViewById(R.id.txt_quantidade_item);
        listItens = (ListView) rootView.findViewById(R.id.list_itens_adicionados);
        addItem = (Button) rootView.findViewById(R.id.btn_add_item);
        addItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TipoItem tipoItem = (TipoItem) spnTipoItem.getSelectedItem();
                int idTipoItem = tipoItem.getIdTipoItem();
                int quantidade = Integer.valueOf(txtQuantidadeItem.getText().toString());

                Item item = new Item(-1, quantidade, idTipoItem);
                item.setTipoItem(tipoItem);
                _itensAdicionados.add(item);

                ItemEventoAdapter adapter = new ItemEventoAdapter(getActivity(),
                        R.layout.item_lista_itensevento, _itensAdicionados);
                listItens.setAdapter(adapter);
            }
        });

        ArrayList<TipoItem> tiposItens = ((FamilystApplication)getActivity().getApplication()).getTiposItens();
        TipoItemAdapter adapter = new TipoItemAdapter(getActivity(),
                R.layout.item_lista_tipoitem, tiposItens);
        spnTipoItem.setAdapter(adapter);

        _itensAdicionados = new ArrayList<>();

        listItens.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getContext(), "Long click!", Toast.LENGTH_LONG).show();
                //TODO abrir tela de Cadastro com extras: idEvento e bool indicando edicao
                return true;

            }
        });

        isEdicao = getArguments().getBoolean("isEdicao", false);
        if(isEdicao) {
            int idEvento = getArguments().getInt("idEvento");
            _evento = carregarEvento(idEvento);

            for ( int i = 0 ; i < _evento.getItensEvento().size() ; i++) {
                Item item = _evento.getItensEvento().get(i);
                _itensAdicionados.add(item);
            }
            ItemEventoAdapter adapter2 = new ItemEventoAdapter(getActivity(),
                    R.layout.item_lista_itensevento, _itensAdicionados);
            listItens.setAdapter(adapter2);
        }

        return  rootView;
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
