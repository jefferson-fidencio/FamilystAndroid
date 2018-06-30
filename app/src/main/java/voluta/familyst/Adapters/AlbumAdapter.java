package voluta.familyst.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import voluta.familyst.Model.Album;
import voluta.familyst.R;

import java.util.ArrayList;

public class AlbumAdapter extends ArrayAdapter<Album> {

    private Context context;
    int layoutResourceId;
    ArrayList<Album> dados = null;

  public AlbumAdapter(Context context, int layoutResourceId, ArrayList<Album> dados) {
      super(context, layoutResourceId, dados);
      this.context = context;
      this.layoutResourceId = layoutResourceId;
      this.dados = dados;
  }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        AlbumHolder holder = null;


        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new AlbumHolder();
            holder.txtData = (TextView) row.findViewById(R.id.data_album);
            holder.txtNome = (TextView) row.findViewById(R.id.nome_album);

            row.setTag(holder);
        }
        else {
            holder = (AlbumHolder)row.getTag();
        }

        android.text.format.DateFormat df = new android.text.format.DateFormat();
        Album album = dados.get(position);

        String dataFormatada =  df.format("dd/MM", album.getDataCriacao()).toString();
        holder.txtNome.setText(album.getNome());
        holder.txtData.setText(dataFormatada);

        return row;
    }

    static class AlbumHolder
    {
        TextView txtNome;
        TextView txtData;
    }
}
