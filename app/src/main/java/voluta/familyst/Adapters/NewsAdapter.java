package voluta.familyst.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import voluta.familyst.Activities.CadastroAlbumActivity;
import voluta.familyst.Activities.CadastroNoticiaActivity;
import voluta.familyst.Activities.NoticiaActivity;
import voluta.familyst.Model.Album;
import voluta.familyst.Model.Noticia;
import voluta.familyst.R;

import java.util.ArrayList;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {

    private ArrayList<Noticia> listaNoticias = new ArrayList<>();
    private LayoutInflater layoutInflater;
    private Context contexto;

    public NewsAdapter (Context context) {
        layoutInflater = LayoutInflater.from(context);
        contexto = context;
    }

    @Override
    public int getItemCount() { return listaNoticias.size();}

    @Override
    public void onBindViewHolder(NewsViewHolder newsViewHolder, int i) {
        Noticia noticia = listaNoticias.get(i);

        //TODO Adicionar restante das informações de noticia
        String descricao = noticia.getDescricao();
        String comentarios = noticia.getComentarios() == null ? "0 Comentarios" : noticia.getComentarios().size() + " Comentarios";
        newsViewHolder.vDescricao.setText(descricao);
        newsViewHolder.vNumComentarios.setText(comentarios);
        newsViewHolder.setNoticia(noticia);
    }

    public void setListaNoticia(ArrayList<Noticia> listaNoticias) {

        this.listaNoticias = listaNoticias;
        notifyItemChanged(0, listaNoticias.size());
    }

    @Override
    public NewsViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.news_cardview, viewGroup, false);

        return new NewsViewHolder(itemView, contexto);
    }

    public static class NewsViewHolder extends RecyclerView.ViewHolder{

        protected TextView vDescricao;
        protected TextView vNumComentarios;
        protected View view;
        private Context contexto;
        private Noticia noticia;


        public NewsViewHolder(View itemView, final Context context) {
            super(itemView);
            view = itemView;
            contexto = context;

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context.getApplicationContext(), NoticiaActivity.class);
                    intent.putExtra("idNoticia", getNoticia().getIdNoticia());
                    context.startActivity(intent);
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    Noticia noticia = (Noticia) getNoticia();

                    //TODO abrir tela de Cadastro com extras: idEvento e bool indicando edicao
                    Intent intent = new Intent(context.getApplicationContext(), CadastroNoticiaActivity.class);
                    intent.putExtra("idNoticia", noticia.getIdNoticia());
                    intent.putExtra("isEdicao", true);
                    context.startActivity(intent);

                    return false;
                }
            });

            vDescricao = (TextView) itemView.findViewById(R.id.txt_descricao_noticia);
            vNumComentarios = (TextView) itemView.findViewById(R.id.txt_comentarios);
        }

        public void setNoticia(Noticia noticia) {
            this.noticia = noticia;
        }

        public Noticia getNoticia() {
            return noticia;
        }
    }
}
