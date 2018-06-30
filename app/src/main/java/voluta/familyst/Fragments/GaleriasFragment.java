package voluta.familyst.Fragments;

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

import voluta.familyst.Activities.CadastroGaleriaActivity;
import voluta.familyst.Adapters.GaleriaAdapter;
import voluta.familyst.Model.Galeria;
import voluta.familyst.R;

import java.sql.Date;

public class GaleriasFragment extends Fragment {


    public GaleriasFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_gallery, container, false);

        FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Chama cadastro de notícias", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                Intent intent = new Intent(getContext(), CadastroGaleriaActivity.class);
                startActivity(intent);
            }
        });

        Galeria dados_galeria[] = new Galeria[]
        {
                new Galeria(1, "Churrasco de Domingo", new Date(29, 10, 2016)),
                new Galeria(1, "Aniversário da Vó", new Date(30, 10, 2016)),
                new Galeria(1, "Reunião dos primos", new Date(30, 11, 2016)),
                new Galeria(1, "Chá de panela da Joana", new Date(30, 11, 2016)),
                new Galeria(1, "Show de Talentos", new Date(30, 11, 2016)),
        };

        GaleriaAdapter adapter = new GaleriaAdapter(getContext(),
                R.layout.item_lista_galerias, dados_galeria);

        final ListView listViewGalerias = (ListView)rootView.findViewById(R.id.listview_galeria);

        listViewGalerias.setAdapter(adapter);

        listViewGalerias.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //TODO abrir o fragment de galeria quando este existir
                GaleriaFragment fragment = new GaleriaFragment();
                getFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, fragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        listViewGalerias.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getContext(), "Long click!", Toast.LENGTH_LONG).show();
                //TODO abrir tela de Cadastro com extras: idEvento e bool indicando edicao
                return true;

            }
        });

        return rootView;
    }

}
