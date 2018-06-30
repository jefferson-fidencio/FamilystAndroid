package voluta.familyst.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import voluta.familyst.Model.Item;
import voluta.familyst.R;

import java.util.ArrayList;

public class ItemEventoAdapter extends ArrayAdapter<Item> {

    private Context context;
    int layoutResourceId;
    ArrayList<Item> dados = null;

    public ItemEventoAdapter(Context context, int layoutResourceId, ArrayList<Item> dados){
        super(context,layoutResourceId, dados);
        this.context = context;
        this.layoutResourceId = layoutResourceId;
        this.dados = dados;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ItemEventoHolder holder = null;

        if (row == null)
        {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new ItemEventoHolder();
            holder.txtNome = (TextView)row.findViewById(R.id.nome_item);
            holder.txtQuantidade = (TextView)row.findViewById(R.id.quantidade_item);

            row.setTag(holder);
        }
        else{
            holder = (ItemEventoHolder)row.getTag();
        }

        Item itemEvento = dados.get(position);
        holder.txtNome.setText(itemEvento.getTipoItem().getNome());
        holder.txtQuantidade.setText(Integer.toString(itemEvento.getQuantidade()));

        return row;
    }

    static class ItemEventoHolder
    {
        TextView txtNome;
        TextView txtQuantidade;
    }
}
