package voluta.familyst.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import voluta.familyst.Model.Galeria;
import voluta.familyst.R;

public class GaleriaAdapter extends ArrayAdapter<Galeria> {

    Context context;
    int layoutResourceId;
    Galeria dados [] = null;

    public GaleriaAdapter(Context context, int layoutResourceId, Galeria[] dados) {
        super(context, layoutResourceId, dados);
        this.context = context;
        this.layoutResourceId = layoutResourceId;
        this.dados = dados;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        GaleriaHolder holder = null;

        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new GaleriaHolder();
            holder.txtData = (TextView) row.findViewById(R.id.data_galeria);
            holder.txtNome = (TextView) row.findViewById(R.id.nome_galeria);

            row.setTag(holder);
        }
        else {
            holder = (GaleriaHolder)row.getTag();
        }
        android.text.format.DateFormat df = new android.text.format.DateFormat();
        Galeria galeria = dados[position];

        String dataFormatada =  df.format("dd/MM/yy", galeria.getDataCriacao()).toString();
        holder.txtNome.setText(galeria.getNome());
        holder.txtData.setText(dataFormatada);

        return row;
    }

    static class GaleriaHolder
    {
        TextView txtNome;
        TextView txtData;
    }

}
