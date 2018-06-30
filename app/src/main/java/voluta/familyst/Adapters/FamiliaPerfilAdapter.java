package voluta.familyst.Adapters;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import voluta.familyst.Model.Familia;
import voluta.familyst.R;

import java.util.ArrayList;

public class FamiliaPerfilAdapter extends ArrayAdapter<Familia> {

    private Context context;
    int layoutResourceId;
    ArrayList<Familia> dados = null;

    public FamiliaPerfilAdapter(Context context, int layoutResourceId, ArrayList<Familia> dados) {
        super(context, layoutResourceId, dados);
        this.context = context;
        this.layoutResourceId = layoutResourceId;
        this.dados = dados;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        FamiliaPerfilHolder holder = null;

        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new FamiliaPerfilHolder();
            holder.txtNomeFamilia = (TextView) row.findViewById(R.id.txt_familia_perfil);
            holder.txtLocalFamilia = (TextView) row.findViewById(R.id.txt_local_familia_perfil);

            row.setTag(holder);
        } else {
            holder = (FamiliaPerfilHolder) row.getTag();
        }

        Familia familia = dados.get(position);
        holder.txtNomeFamilia.setText(familia.getNome());
        holder.txtLocalFamilia.setText(familia.getLocal());

        return row;

    }

    static class FamiliaPerfilHolder {
        TextView txtNomeFamilia;
        TextView txtLocalFamilia;
    }
}