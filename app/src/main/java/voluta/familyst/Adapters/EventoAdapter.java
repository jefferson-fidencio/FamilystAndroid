package voluta.familyst.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import voluta.familyst.Model.Evento;
import voluta.familyst.R;

import java.util.ArrayList;

public class EventoAdapter extends ArrayAdapter<Evento> {

    private Context context;
    int layoutResourceId;
    ArrayList<Evento> dados = null;

    public EventoAdapter(Context context, int layoutResourceId, ArrayList<Evento> dados) {
        super(context, layoutResourceId, dados);
        this.context = context;
        this.layoutResourceId = layoutResourceId;
        this.dados = dados;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        EventoHolder holder = null;

        if (row == null)
        {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new EventoHolder();
            holder.txtData = (TextView)row.findViewById(R.id.data_evento);
            holder.txtNome = (TextView) row.findViewById(R.id.nome_evento);

            row.setTag(holder);
        }
        else {
            holder = (EventoHolder)row.getTag();
        }

        android.text.format.DateFormat df = new android.text.format.DateFormat();
        Evento evento = dados.get(position);

        String dataFormatada =  df.format("dd/MM/yy", evento.getDataCriacao()).toString();
        holder.txtNome.setText(evento.getNome());
        holder.txtData.setText(dataFormatada);

        return row;
    }

    static class EventoHolder
    {
        TextView txtNome;
        TextView txtDescricao;
        TextView txtData;
    }
}
