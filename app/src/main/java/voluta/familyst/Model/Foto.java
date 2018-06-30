package voluta.familyst.Model;

import android.graphics.Bitmap;

import java.io.Serializable;
import java.util.Date;

public class Foto implements Serializable {

    private int idImagem;
    private String dados;
    private String descricao;
    private Date dataCriacao;

    public Foto(int idImagem, String dados, String descricao, Date dataCriacao) {
        this.idImagem = idImagem;
        this.dados = dados;
        this.descricao = descricao;
        this.dataCriacao = dataCriacao;
    }

    public int getIdImagem() {
        return idImagem;
    }

    public void setIdImagem(int idImagem) {
        this.idImagem = idImagem;
    }

    public String getDados() {
        return dados;
    }

    public void setDados(String dados) {
        this.dados = dados;
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
}
