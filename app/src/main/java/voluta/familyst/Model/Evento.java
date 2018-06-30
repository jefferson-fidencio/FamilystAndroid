package voluta.familyst.Model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class Evento implements Serializable {

    private int idEvento;
    private String nome;
    private String descricao;
    private Date dataCriacao;
    private String local;
    private int idUsuario;
    private int idTipoEvento;

    //propriedades exclusivas do client
    private TipoEvento tipoEvento;
    private ArrayList<Item> itensEvento;
    private Usuario usuarioEvento;
    private ArrayList<Comentario> comentariosEvento;

    public Evento(int idEvento, String nome, String descricao, Date dataCriacao, String local, int idUsuario, int idTipoEvento) {
        this.idEvento = idEvento;
        this.nome = nome;
        this.descricao = descricao;
        this.dataCriacao = dataCriacao;
        this.local = local;
        this.idUsuario = idUsuario;
        this.idTipoEvento = idTipoEvento;
    }

    public int getIdEvento() {
        return idEvento;
    }

    public void setIdEvento(int idEvento) {
        this.idEvento = idEvento;
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

    public void setDataCriacao(Date dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public String getLocal() {
        return local;
    }

    public void setLocal(String local) {
        this.local = local;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public TipoEvento getTipoEvento() {
        return tipoEvento;
    }

    public void setTipoEvento(TipoEvento tipoEvento) {
        this.tipoEvento = tipoEvento;
    }

    public ArrayList<Item> getItensEvento() {
        return itensEvento;
    }

    public void setItensEvento(ArrayList<Item> itensEvento) {
        this.itensEvento = itensEvento;
    }

    public int getIdTipoEvento() {
        return idTipoEvento;
    }

    public void setIdTipoEvento(int idTipoEvento) {
        this.idTipoEvento = idTipoEvento;
    }

    public Usuario getUsuarioEvento() {
        return usuarioEvento;
    }

    public void setUsuarioEvento(Usuario usuarioEvento) {
        this.usuarioEvento = usuarioEvento;
    }

    public ArrayList<Comentario> getComentariosEvento() {
        return comentariosEvento;
    }

    public void setComentariosEvento(ArrayList<Comentario> comentariosEvento) {
        this.comentariosEvento = comentariosEvento;
    }
}
