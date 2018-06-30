package voluta.familyst.Model;

import java.util.Date;

public class Galeria {

    private int idGaleria;
    private String nome;
    private Date dataCriacao;
    //private List<Video> videos;


    public Galeria(int idGaleria, String nome, Date dataCriacao) {
        this.idGaleria = idGaleria;
        this.nome = nome;
        this.dataCriacao = dataCriacao;
    }

    public int getIdGaleria() {
        return idGaleria;
    }

    public void setIdGaleria(int idGaleria) {
        this.idGaleria = idGaleria;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Date getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(Date dataCriacao) {
        this.dataCriacao = dataCriacao;
    }
}
