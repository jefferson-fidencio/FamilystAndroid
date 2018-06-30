package voluta.familyst.Model;

public class TipoItem {

    private final int idTipoItem;
    private final String nome;

    public TipoItem(int idTipoItem, String nome) {
        this.idTipoItem = idTipoItem;
        this.nome = nome;
    }

    public int getIdTipoItem() {
        return idTipoItem;
    }

    public String getNome() {
        return nome;
    }

    @Override
    public String toString()
    {
        return this.nome;
    }
}
