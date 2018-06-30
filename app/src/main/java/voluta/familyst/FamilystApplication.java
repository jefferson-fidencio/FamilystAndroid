package voluta.familyst;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import voluta.familyst.Model.Familia;
import voluta.familyst.Model.TipoEvento;
import voluta.familyst.Model.TipoItem;
import voluta.familyst.Model.Usuario;

import java.util.ArrayList;

public class FamilystApplication extends Application {

    //quando usuario esta logado, estas variaveis ficam gravadas em sharedpreferences
    private String _accessToken;
    private Usuario _usuarioLogado;

    //variaveis de controle de fluxo
    private int idFamiliaAtual = 1;
    private ArrayList<TipoItem> tiposItens;
    private ArrayList<TipoEvento> tiposEventos;
    private boolean logout;

    //metodos
    public String get_accessToken() {
        return _accessToken;
    }

    public void clearData(){
        _accessToken = "";
        _usuarioLogado = null;
        idFamiliaAtual = 1;
    }

    public void set_accessToken(String _accessToken) {
        this._accessToken = _accessToken;
    }

    public Usuario get_usuarioLogado() {
        return _usuarioLogado;
    }

    public void set_usuarioLogado(Usuario _usuarioLogado) {
        this._usuarioLogado = _usuarioLogado;
    }

    public Familia getFamiliaAtual() {
        ArrayList<Familia> familias = _usuarioLogado.getFamilias();
        for (int i = 0 ; i < familias.size() ; i++)
        {
            Familia familia = familias.get(i);
            if (familia.getIdFamilia() == idFamiliaAtual)
                return familia;
        }

        return null;
    }

    public void setIdFamiliaSelecionada(int idFamiliaSelecionada) {
        this.idFamiliaAtual = idFamiliaSelecionada;
    }

    public void setLoginAutomatico(boolean loginAutomatico) {
        SharedPreferences prefs = getSharedPreferences();
        if (loginAutomatico)
        {
            prefs.edit().putString("accessToken", _accessToken).apply();
            prefs.edit().putInt("idUsuario", _usuarioLogado.getIdUsuario()).apply();
            prefs.edit().putString("nomeUsuario", _usuarioLogado.getNome()).apply();
            prefs.edit().putString("emailUsuario", _usuarioLogado.getEmail()).apply();
        }
        else
        {
            prefs.edit().clear().apply();
        }

    }

    public boolean getLoginAutomatico()
    {
        SharedPreferences prefs = getSharedPreferences();
        if (prefs.contains("accessToken")&&prefs.contains("idUsuario")&&prefs.contains("nomeUsuario")&&prefs.contains("emailUsuario")) {
            int idUsuario = prefs.getInt("idUsuario",0);
            String nomeUsuario = prefs.getString("nomeUsuario","");
            String emailUsuario = prefs.getString("emailUsuario","");
            Usuario user = new Usuario(idUsuario, nomeUsuario, emailUsuario);
            String accessToken = prefs.getString("accessToken","");

            _usuarioLogado = user;
            _accessToken = accessToken;

            return true;
        }
        return false;
    }

    public SharedPreferences getSharedPreferences()
    {
        return getSharedPreferences("ufpr.tcc.familyst", Context.MODE_PRIVATE);
    }

    public void setTiposItens(ArrayList<TipoItem> tiposItens) {
        this.tiposItens = tiposItens;
    }

    public void setTiposEventos(ArrayList<TipoEvento> tiposEventos) {
        this.tiposEventos = tiposEventos;
    }

    public ArrayList<TipoItem> getTiposItens() {
        return tiposItens;
    }

    public ArrayList<TipoEvento> getTiposEventos() {
        return tiposEventos;
    }

    public void setLogout(boolean logout) {
        this.logout = logout;
    }

    public boolean getLogout() {
        return logout;
    }
}
