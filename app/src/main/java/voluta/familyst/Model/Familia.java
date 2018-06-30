package voluta.familyst.Model;

import java.util.ArrayList;
import java.util.Date;

public class Familia {

    private int idFamilia;
    private String nome;
    private String descricao;
    private Date dataCriacao;
    private String local;
    int idGaleria;

    //propriedades exclusivas do client
    private ArrayList<Evento> eventos;
    private ArrayList<Usuario> usuarios;
    private ArrayList<Noticia> noticias;
    private ArrayList<Album> albuns;
    private ArrayList<Video> videos;

    public Familia (int idFamilia, String nome, int idGaleria, String descricao, String local)
    {
        this.idFamilia = idFamilia;
        this.nome = nome;
        this.idGaleria = idGaleria;
        this.descricao = descricao;
        this.local = local;
    }

    public int getIdGaleria() {
        return idGaleria;
    }

    public void setIdGaleria(int idGaleria) {
        this.idGaleria = idGaleria;
    }

    public int getIdFamilia() {
        return idFamilia;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
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

    public String getLocal() {
        return local;
    }

    public void setLocal(String local) {
        this.local = local;
    }

    @Override
    public String toString() {
        return getNome();
    }

    public ArrayList<Evento> getEventos() {
        return eventos;
    }

    public void setEventos(ArrayList<Evento> eventos) {
        this.eventos = eventos;
    }

    public ArrayList<Usuario> getUsuarios() {
        return usuarios;
    }

    public void setUsuarios(ArrayList<Usuario> usuarios) {
        this.usuarios = usuarios;
    }

    public ArrayList<Noticia> getNoticias() {
        return noticias;
    }

    public void setNoticias(ArrayList<Noticia> noticias) {
        this.noticias = noticias;
    }

    public ArrayList<Album> getAlbuns() {
        return albuns;
    }

    public void setAlbuns(ArrayList<Album> albuns) {
        this.albuns = albuns;
    }

    public ArrayList<Video> getVideos() {
        return videos;
    }

    public void setVideos(ArrayList<Video> videos) {
        this.videos = videos;
    }
}
