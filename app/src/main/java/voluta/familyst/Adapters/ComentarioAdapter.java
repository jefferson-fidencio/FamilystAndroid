package voluta.familyst.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import voluta.familyst.Model.Comentario;
import voluta.familyst.R;

import java.util.ArrayList;

public class ComentarioAdapter extends ArrayAdapter<Comentario> {

    private Context context;
    int layoutResourceId;
    ArrayList<Comentario> dados = null;

    public ComentarioAdapter(Context context, int layoutResourceId, ArrayList<Comentario> dados){
        super(context, layoutResourceId, dados);
        this.context = context;
        this.layoutResourceId = layoutResourceId;
        this.dados = dados;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ComentarioHolder holder = null;

        if (row == null){
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new ComentarioHolder();
            holder.txtNome = (TextView) row.findViewById(R.id.nome_comentario_item);
            holder.txtDescricao = (TextView) row.findViewById(R.id.descricao_comentario_item);
            holder.txtData = (TextView) row.findViewById(R.id.data_comentario_item);

            row.setTag(holder);
        }
        else
        {
            holder = (ComentarioHolder)row.getTag();
        }
        android.text.format.DateFormat df = new android.text.format.DateFormat();
        Comentario comentario = dados.get(position);

        String dataFormatada =  df.format("dd/MM", comentario.getDataCriacao()).toString();
        holder.txtDescricao.setText(comentario.getDescricao());
        holder.txtData.setText(dataFormatada);

        return row;
    }

    static class ComentarioHolder
    {
        TextView txtNome;
        TextView txtDescricao;
        TextView txtData;
    }
}
