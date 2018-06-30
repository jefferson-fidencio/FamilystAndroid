package voluta.familyst.Adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import voluta.familyst.Model.Foto;
import voluta.familyst.R;

import java.util.ArrayList;

public class GridViewAdapter extends ArrayAdapter<Foto> {

    private Context context;
    private int layoutResourceId;
    ArrayList<Foto> dados = new ArrayList<>();

    public GridViewAdapter(Context context, int layoutResourceId, ArrayList<Foto> dados)
    {
        super(context, layoutResourceId, dados);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.dados = dados;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder = null;

        if(row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new ViewHolder();
            holder.imagem = (ImageView) row.findViewById(R.id.imagem_grid);
            row.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) row.getTag();
        }

        Foto foto = dados.get(position);

        //recuperando foto
        Bitmap fotoBytes = null;
        try {
            String fotoEncoded = foto.getDados();
            byte[] decodedString = Base64.decode(fotoEncoded, Base64.DEFAULT);
            fotoBytes = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        }
        catch (Exception ex){}

        holder.imagem.setImageBitmap(fotoBytes);

        return row;
    }

    static class ViewHolder
    {
        ImageView imagem;
    }
}
