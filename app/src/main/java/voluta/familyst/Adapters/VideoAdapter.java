package voluta.familyst.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import voluta.familyst.Model.Video;
import voluta.familyst.R;

import java.util.ArrayList;

public class VideoAdapter extends ArrayAdapter<Video> {

    private Context context;
    int layoutResourceId;
    ArrayList<Video> dados = null;

    public VideoAdapter(Context context, int layoutResourceId, ArrayList<Video> dados ) {
        super(context, layoutResourceId, dados);
        this.context = context;
        this.layoutResourceId = layoutResourceId;
        this.dados = dados;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        VideoHolder holder = null;

        if(row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new VideoHolder();
            holder.txtDescricao = (TextView) row.findViewById(R.id.nome_video);
            holder.txtData = (TextView) row.findViewById(R.id.data_video);

            row.setTag(holder);
        }
        else
        {
            holder = (VideoHolder) row.getTag();
        }
        android.text.format.DateFormat df = new android.text.format.DateFormat();
        Video video = dados.get(position);

        String dataFormatada =  df.format("dd/MM/yy", video.getDataCriacao()).toString();
        holder.txtDescricao.setText(video.getDescricao());
        holder.txtData.setText(dataFormatada);

        return row;

    }

    static class VideoHolder
    {
        TextView txtDescricao;
        TextView txtData;
    }
}
