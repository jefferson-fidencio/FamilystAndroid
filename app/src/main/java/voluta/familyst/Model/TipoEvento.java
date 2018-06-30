package voluta.familyst.Model;

import java.io.Serializable;

public class TipoEvento implements Serializable {

    private int idTipoEvento;
    private String nome;

    public TipoEvento(int idTipoEvento, String nome) {
        this.idTipoEvento = idTipoEvento;
        this.nome = nome;
    }

    public int getIdTipoEvento() {
        return idTipoEvento;
    }

    public void setIdTipoEvento(int idTipoEvento) {
        this.idTipoEvento = idTipoEvento;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    @Override
    public String toString()
    {
        return this.nome;
    }
}
