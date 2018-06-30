package voluta.familyst.Activities;

import android.content.Intent;
import android.os.Bundle;

import voluta.familyst.Interfaces.RestCallback;
import voluta.familyst.R;
import voluta.familyst.Services.RestService;

public class LoadingDataActivity extends BaseActivity {

    boolean _familiasCarregadas = false;
    boolean _eventosCarregados = false;
    boolean _tiposEventosCarregados = false;
    boolean _usuarioEventosCarregados = false;
    boolean _itensEventosCarregados = false;
    boolean _comentariosEventosCarregados = false;
    boolean _tiposItemCarregados = false;
    boolean _usuariosCarregados = false;
    boolean _noticiasCarregadas = false;
    boolean _comentariosNoticiasCarregados = false;
    boolean _usuariosNoticiasCarregados = false;
    boolean _videosCarregados = false;
    boolean _albunsCarregados = false;
    boolean _fotosAlbunsCarregadas = false;;
    boolean _tiposEventoCarregados = false;;
    boolean _tiposItensCarregadas = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading_data_activity);
        getSupportActionBar().hide();

        CarregarFamiliasUsuarioAsync();
    }

    private void CarregarFamiliasUsuarioAsync() {

        if (!_familiasCarregadas)
            RestService.getInstance(this).CarregarFamiliasAsync(new RestCallback(){
                @Override
                public void onRestResult(boolean success) {
                    if (success){
                        _familiasCarregadas = true;
                        CarregarFamiliasUsuarioAsync();
                    }
                }
            });
        else if (!_eventosCarregados)
            RestService.getInstance(this).CarregarEventosFamiliasAsync(new RestCallback(){
                @Override
                public void onRestResult(boolean success) {
                    if (success){
                        _eventosCarregados = true;
                        CarregarFamiliasUsuarioAsync();
                    }
                }
            });
        else if (!_tiposEventosCarregados)
            RestService.getInstance(this).CarregarTiposEventosFamiliasAsync(new RestCallback(){
                @Override
                public void onRestResult(boolean success) {
                    if (success){
                        _tiposEventosCarregados = true;
                        CarregarFamiliasUsuarioAsync();
                    }
                }
            });
        else if (!_usuarioEventosCarregados)
            RestService.getInstance(this).CarregarUsuarioEventosFamiliasAsync(new RestCallback(){
                @Override
                public void onRestResult(boolean success) {
                    if (success){
                        _usuarioEventosCarregados = true;
                        CarregarFamiliasUsuarioAsync();
                    }
                }
            });
        else if (!_itensEventosCarregados)
            RestService.getInstance(this).CarregarItensEventosFamiliasAsync(new RestCallback(){
                @Override
                public void onRestResult(boolean success) {
                    if (success){
                        _itensEventosCarregados = true;
                        CarregarFamiliasUsuarioAsync();
                    }
                }
            });
        else if (!_comentariosEventosCarregados)
            RestService.getInstance(this).CarregarComentariosEventosFamiliasAsync(new RestCallback(){
                @Override
                public void onRestResult(boolean success) {
                    if (success){
                        _comentariosEventosCarregados = true;
                        CarregarFamiliasUsuarioAsync();
                    }
                }
            });
        else if (!_tiposItemCarregados)
            RestService.getInstance(this).CarregarTiposItensAsync(new RestCallback(){
                @Override
                public void onRestResult(boolean success) {
                    if (success){
                        _tiposItemCarregados = true;
                        CarregarFamiliasUsuarioAsync();
                    }
                }
            });
        else if (!_usuariosCarregados)
            RestService.getInstance(this).CarregarUsuariosFamiliasAsync(new RestCallback(){
                @Override
                public void onRestResult(boolean success) {
                    if (success){
                        _usuariosCarregados = true;
                        CarregarFamiliasUsuarioAsync();
                    }
                }
            });
        else if (!_noticiasCarregadas)
            RestService.getInstance(this).CarregarNoticiasFamiliasAsync(new RestCallback(){
                @Override
                public void onRestResult(boolean success) {
                    if (success){
                        _noticiasCarregadas = true;
                        CarregarFamiliasUsuarioAsync();
                    }
                }
            });
        else if (!_albunsCarregados)
            RestService.getInstance(this).CarregarAlbunsFamiliasAsync(new RestCallback(){
                @Override
                public void onRestResult(boolean success) {
                    if (success){
                        _albunsCarregados = true;
                        CarregarFamiliasUsuarioAsync();
                    }
                }
            });
        else if (!_comentariosNoticiasCarregados)
            RestService.getInstance(this).CarregarComentariosNoticiasFamiliasAsync(new RestCallback(){
                @Override
                public void onRestResult(boolean success) {
                    if (success){
                        _comentariosNoticiasCarregados = true;
                        CarregarFamiliasUsuarioAsync();
                    }
                }
            });
        else if (!_usuariosNoticiasCarregados)
            RestService.getInstance(this).CarregarUsuariosNoticiasFamiliasAsync(new RestCallback(){
                @Override
                public void onRestResult(boolean success) {
                    if (success){
                        _usuariosNoticiasCarregados = true;
                        CarregarFamiliasUsuarioAsync();
                    }
                }
            });
        else if (!_videosCarregados)
            RestService.getInstance(this).CarregarVideosFamiliasAsync(new RestCallback(){
                @Override
                public void onRestResult(boolean success) {
                    if (success){
                        _videosCarregados = true;
                        CarregarFamiliasUsuarioAsync();
                    }
                }
            });
        else if (!_fotosAlbunsCarregadas)
            RestService.getInstance(this).CarregarFotosAlbunsFamiliasAsync(new RestCallback(){
                @Override
                public void onRestResult(boolean success) {
                    if (success){
                        _fotosAlbunsCarregadas = true;
                        CarregarFamiliasUsuarioAsync();
                    }
                }
            });
        else if (!_tiposEventoCarregados)
            RestService.getInstance(this).CarregarTiposEventosAsync(new RestCallback(){
                @Override
                public void onRestResult(boolean success) {
                    if (success){
                        _tiposEventoCarregados = true;
                        CarregarFamiliasUsuarioAsync();
                    }
                }
            });
        else if (!_tiposItensCarregadas)
            RestService.getInstance(this).CarregarTiposItemAsync(new RestCallback(){
                @Override
                public void onRestResult(boolean success) {
                    if (success){
                        _tiposItensCarregadas = true;
                        CarregarFamiliasUsuarioAsync();
                    }
                }
            });
        else AbrirTelaPrincipal();
    }

    private void AbrirTelaPrincipal() {

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);

        finish();
    }
}
