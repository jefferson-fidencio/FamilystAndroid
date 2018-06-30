package voluta.familyst.Model;

import java.io.Serializable;
import java.util.Date;

public class Comentario implements Serializable {

    private int idComentario;
    private String descricao;
    private Date dataCriacao;

    public Comentario(int idComentario, String descricao, Date dataCriacao) {
        this.idComentario = idComentario;
        this.descricao = descricao;
        this.dataCriacao = dataCriacao;
    }

    public Date getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(Date dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public int getIdComentario() {
        return idComentario;
    }

    public void setIdComentario(int idComentario) {
        this.idComentario = idComentario;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}
