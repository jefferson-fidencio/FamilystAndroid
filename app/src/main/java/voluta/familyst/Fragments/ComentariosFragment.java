package voluta.familyst.Fragments;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import voluta.familyst.R;

public class ComentariosFragment extends Fragment {

    private EditText txtComentario;
    private ImageButton btnEnviarComentario;

    public ComentariosFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       View rootView = inflater.inflate(R.layout.fragment_comentarios, container, false);

        txtComentario = (EditText) getActivity().findViewById(R.id.txt_comentario_enviar);
        btnEnviarComentario = (ImageButton) getActivity().findViewById(R.id.btn_enviar_comentario);


        btnEnviarComentario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO Recebe a informação adicionada no EditText
                //Verifica se o EditText está nulo
                //Monta objeto Comentario
                //Adiciona na listView
                //Envia comentário p/ web
            }
        });

        FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });

        /*Comentario dados_comentario[] = new Comentario[]
        {
            new Comentario(1, "Eu vou levar a bolar pra jogarmos um fut", "Jean Carlo", new Date(1, 1, 2016)),
            new Comentario(2, "Não sei se vou poder ir :(", "Mariazinha", new Date(1, 1, 2016)),
            new Comentario(2, "Vai ser muito divertido!", "Carlitos", new Date(1, 1, 2016)),

        };*/

        /*ComentarioAdapter adapter = new ComentarioAdapter(getContext(),
                R.layout.item_lista_comentarios, dados_comentario);*/

        final ListView listViewComentarios = (ListView) rootView.findViewById(R.id.listview_comentarios);

        //listViewComentarios.setAdapter(adapter);


        return rootView;
    }

}
