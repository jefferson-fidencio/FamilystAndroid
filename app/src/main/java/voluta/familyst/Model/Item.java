package voluta.familyst.Model;

import java.io.Serializable;

public class Item implements Serializable {

    private int idItem;
    private int quantidade;
    private int idTipoItem;

    //propriedades exclusivas do client
    private TipoItem tipoItem;

    public Item(int idItem, int quantidade, int idTipoItem) {
        this.idItem = idItem;
        this.quantidade = quantidade;
        this.idTipoItem = idTipoItem;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

    public TipoItem getTipoItem() {
        return tipoItem;
    }

    public void setTipoItem(TipoItem tipoItem) {
        this.tipoItem = tipoItem;
    }

    public int getIdItem() {
        return idItem;
    }

    public void setIdItem(int idItem) {
        this.idItem = idItem;
    }

    public int getIdTipoItem() {
        return idTipoItem;
    }

    public void setIdTipoItem(int idTipoItem) {
        this.idTipoItem = idTipoItem;
    }
}
