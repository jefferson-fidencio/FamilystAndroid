package voluta.familyst.Model;

import java.util.Date;

public class Video {

    private int idVideo;
    private String descricao;
    private Date dataCriacao;
    private String link;

    public Video(int idVideo, String descricao, Date dataCriacao, String link) {
        this.idVideo = idVideo;
        this.descricao = descricao;
        this.dataCriacao = dataCriacao;
        this.link = link;
    }

    public int getIdVideo() {
        return idVideo;
    }

    public void setIdVideo(int idVideo) {
        this.idVideo = idVideo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Date getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(Date dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
