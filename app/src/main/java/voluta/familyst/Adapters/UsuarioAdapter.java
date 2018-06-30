package voluta.familyst.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import voluta.familyst.Model.Usuario;
import voluta.familyst.R;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class UsuarioAdapter extends ArrayAdapter<Usuario> {

    private Context context;
    int layoutResourceId;
    ArrayList<Usuario> dados = null;

    public UsuarioAdapter(Context context, int layoutResourceId, ArrayList<Usuario> dados){
        super(context, layoutResourceId, dados);
        this.context = context;
        this.layoutResourceId = layoutResourceId;
        this.dados = dados;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        UsuarioHolder holder = null;

        if(row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new UsuarioHolder();
            holder.txtNome  = (TextView) row.findViewById(R.id.nome_usuario_item);
            holder.txtEmail = (TextView) row.findViewById(R.id.email_usuario_item);

            row.setTag(holder);
        }
        else
        {
            holder = (UsuarioHolder)row.getTag();
        }

        Usuario usuario = dados.get(position);
        holder.txtNome.setText(usuario.getNome());
        holder.txtEmail.setText(usuario.getEmail());

        return row;
    }


    static class UsuarioHolder
    {
        TextView txtNome;
        TextView txtEmail;
    }

}
