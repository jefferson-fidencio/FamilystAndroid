package voluta.familyst.Model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class Album implements Serializable{

    private int idAlbum;
    private String nome;
    private Date dataCriacao;
    private String descricao;

    //propriedades exclusivas do client
    private ArrayList<Foto> fotos;

    public Album(int idAlbum, String nome, Date dataCriacao, String descricao) {
        this.idAlbum = idAlbum;
        this.nome = nome;
        this.dataCriacao = dataCriacao;
        this.descricao = descricao;
    }

    public String getNome() {
        return nome;
    }

    public Date getDataCriacao() {
        return dataCriacao;
    }

    public int getIdAlbum() {
        return idAlbum;
    }

    public void setIdAlbum(int idAlbum) {
        this.idAlbum = idAlbum;
    }

    public ArrayList<Foto> getFotos() {
        return fotos;
    }

    public void setFotos(ArrayList<Foto> fotos) {
        this.fotos = fotos;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}
