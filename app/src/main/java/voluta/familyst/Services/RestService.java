package voluta.familyst.Services;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.util.ArrayMap;
import android.util.Base64;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import voluta.familyst.FamilystApplication;
import voluta.familyst.Interfaces.RestCallback;
import voluta.familyst.Interfaces.RestObjectReceiveCallback;
import voluta.familyst.Model.Album;
import voluta.familyst.Model.Comentario;
import voluta.familyst.Model.Evento;
import voluta.familyst.Model.Familia;
import voluta.familyst.Model.Foto;
import voluta.familyst.Model.Item;
import voluta.familyst.Model.Noticia;
import voluta.familyst.Model.TipoEvento;
import voluta.familyst.Model.TipoItem;
import voluta.familyst.Model.Usuario;
import voluta.familyst.Model.Video;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

public class RestService {

    private static RestService mInstance;
    private static Context mCtx;
    private RequestQueue mRequestQueue;
    private int contadorSincronizacao = 0;
    private RestCallback _restCallback;

    private RestService(Context context) {
            mCtx = context;
            mRequestQueue = getRequestQueue();
    }

    public static synchronized RestService getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new RestService(context);
        }
        return mInstance;
    }

    private RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }

    private ArrayList<Familia> getFamilias() {
        return ((FamilystApplication)((Activity)mCtx).getApplication()).get_usuarioLogado().getFamilias();
    }

    private Familia getFamiliaAtual() {
        return ((FamilystApplication)((Activity)mCtx).getApplication()).getFamiliaAtual();
    }

    private Usuario getUsuarioAtual() {
        return ((FamilystApplication)((Activity)mCtx).getApplication()).get_usuarioLogado();
    }

    public  void CarregarFamiliasAsync(RestCallback restCallback) {
        try {

            //seta retorno
            _restCallback = restCallback;

            //monta url requisicao
            String url = "usuarios/" + ((FamilystApplication)((Activity)mCtx).getApplication()).get_usuarioLogado().getIdUsuario() + "/familias";

            //monta headers adicionais
            Map headers = new ArrayMap();

            //monta body
            JSONObject postBody = new JSONObject();

            //monta requisicao
            JsonRestRequest jsonRequest = new JsonRestRequest((((Activity)mCtx).getApplication()), Request.Method.GET, true, url, headers, postBody,
                    new Response.Listener<JsonRestRequest.JsonRestResponse>() {
                        @Override
                        public void onResponse(JsonRestRequest.JsonRestResponse jsonRestResponse) {
                            if (jsonRestResponse.get_httpStatusCode() == 200) //ok
                            {
                                JSONObject bodyRetorno = jsonRestResponse.get_bodyResponse();
                                onSucessoFamilias(bodyRetorno);
                            }
                            else //erros
                            {
                                onFalhaFamilias("Retorno HTTP não esperado.");
                            }
                        }
                    },
                    error -> onFalhaFamilias(error.getMessage())
            );

            //envia requisicao
            addToRequestQueue(jsonRequest);
        }
        catch (Exception ex){
            Log.d("Error", "Erro ao requisitar Familias: " + ex.getLocalizedMessage());
        }
    }

    private void onFalhaFamilias(String message) {
        Log.d("Error", "Erro ao requisitar Familias: " + message);
        _restCallback.onRestResult(false);
    }

    private void onSucessoFamilias(JSONObject bodyRetorno) {
        try {
            ArrayList<Familia> _familias = new ArrayList<>();
            if (bodyRetorno != null)
            {
                JSONArray familias = bodyRetorno.getJSONArray("familia");
                for (int i = 0 ; i < familias.length() ; i++) {
                    JSONObject familiaJson = familias.getJSONObject(i);
                    Familia familia = new Familia(familiaJson.getInt("idFamilia"), familiaJson.getString("nome"), familiaJson.getInt("idGaleria"), familiaJson.getString("descricao"), familiaJson.getString("local"));
                    _familias.add(familia);
                }
            }
            else
            {
                _familias = new ArrayList<>();
            }

            ((FamilystApplication)((Activity)mCtx).getApplication()).get_usuarioLogado().setFamilias(_familias);
            _restCallback.onRestResult(true);

        } catch (JSONException e) {
            Log.d("Error", "Erro ao requisitar Familias: " + e.getMessage());
        }
    }

    public void CarregarEventosFamiliasAsync(RestCallback restCallback) {
        try{
            //seta retorno
            _restCallback = restCallback;

            contadorSincronizacao = getFamilias().size();
            for (int i = 0 ; i < getFamilias().size() ; i++)
            {
                CarregarEventosFamiliaAsync(getFamilias().get(i));
            }
        }
        catch (Exception ex)
        {
            //ignore
        }
    }

    private void CarregarEventosFamiliaAsync(Familia familia) {
        try {
            //monta url requisicao
            String url = "familias/" + familia.getIdFamilia() + "/eventos";

            //monta headers adicionais
            Map headers = new ArrayMap();

            //monta body
            JSONObject postBody = new JSONObject();

            //monta requisicao
            JsonRestRequest jsonRequest = new JsonRestRequest(((Activity)mCtx).getApplication(), Request.Method.GET, true, url, headers, postBody,
                    new Response.Listener<JsonRestRequest.JsonRestResponse>() {
                        @Override
                        public void onResponse(JsonRestRequest.JsonRestResponse jsonRestResponse) {
                            if (jsonRestResponse.get_httpStatusCode() == 200) //ok
                            {
                                JSONObject bodyRetorno = jsonRestResponse.get_bodyResponse();
                                onSucessoEventosFamilia(familia, bodyRetorno);
                            }
                            else //erros
                            {
                                onFalhaEventosFamilia("Retorno HTTP não esperado.");
                            }
                        }
                    },
                    error -> onFalhaEventosFamilia(error.getMessage())
            );

            //envia requisicao
            addToRequestQueue(jsonRequest);
        }
        catch (Exception ex){
            Log.d("Error", "Erro ao requisitar Eventos de familia: " + ex.getLocalizedMessage());
        }
    }

    private void onFalhaEventosFamilia(String message) {
        Log.d("Error", "Erro ao requisitar Eventos de familia: " + message);
        _restCallback.onRestResult(false);
    }

    private void onSucessoEventosFamilia(Familia familia, JSONObject bodyRetorno) {
        try {
            contadorSincronizacao--;

            if (bodyRetorno != null)
            {
                ArrayList<Evento> eventos = new ArrayList<>();

                Object jsonBody = bodyRetorno.get("evento");
                if (jsonBody instanceof JSONArray) {
                    // se for um vetor de elementos
                    JSONArray eventosJson = (JSONArray)jsonBody;

                    for (int i = 0 ; i < eventosJson.length() ; i++) {
                        JSONObject eventoJson = eventosJson.getJSONObject(i);

                        //recuperando data do evento
                        String dateStr = eventoJson.getString("dataCriacao");
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                        Date dataEvento = sdf.parse(dateStr);

                        //criando evento
                        Evento evento = new Evento(eventoJson.getInt("idEvento"), eventoJson.getString("nome"), eventoJson.getString("descricao"), dataEvento, eventoJson.getString("local"), eventoJson.getInt("idUsuario"), eventoJson.getInt("idTipoEvento"));
                        eventos.add(evento);
                    }
                }
                else if (jsonBody instanceof JSONObject) {
                    //se for so um elemento
                    JSONObject eventoJson = (JSONObject)jsonBody;

                    //recuperando data do evento
                    String dateStr = eventoJson.getString("dataCriacao");
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                    Date dataEvento = sdf.parse(dateStr);

                    //criando evento
                    Evento evento = new Evento(eventoJson.getInt("idEvento"), eventoJson.getString("nome"), eventoJson.getString("descricao"), dataEvento, eventoJson.getString("local"), eventoJson.getInt("idUsuario"), eventoJson.getInt("idTipoEvento"));
                    eventos.add(evento);
                }

                familia.setEventos(eventos);
            }
            else
            {
                ArrayList<Evento> eventos = new ArrayList<>();
                familia.setEventos(eventos);
            }

            //se todos os requests foram executados
            if (contadorSincronizacao == 0)
            {
                _restCallback.onRestResult(true);
            }
        } catch (JSONException | ParseException e) {
            Log.d("Error", "Erro ao requisitar Eventos de familia: " + e.getMessage());
        }
    }

    public void CarregarTiposEventosFamiliasAsync(RestCallback restCallback) {
        try{
            //seta retorno
            _restCallback = restCallback;

            for (int i = 0 ; i < getFamilias().size() ; i++)
            {
                Familia familia = getFamilias().get(i);
                contadorSincronizacao = contadorSincronizacao + familia.getEventos().size();
                for (int j = 0 ; j < familia.getEventos().size() ; j++) {
                    Evento evento = familia.getEventos().get(j);
                    CarregarTipoEventoAsync(evento);
                }
            }
        }
        catch (Exception ex)
        {
            //ignore
        }
    }

    private void CarregarTipoEventoAsync(Evento evento) {
        try {
            //monta url requisicao
            String url = "tiposEvento/" + evento.getIdTipoEvento();

            //monta headers adicionais
            Map headers = new ArrayMap();

            //monta body
            JSONObject postBody = new JSONObject();

            //monta requisicao
            JsonRestRequest jsonRequest = new JsonRestRequest(((Activity)mCtx).getApplication(), Request.Method.GET, true, url, headers, postBody,
                    new Response.Listener<JsonRestRequest.JsonRestResponse>() {
                        @Override
                        public void onResponse(JsonRestRequest.JsonRestResponse jsonRestResponse) {
                            if (jsonRestResponse.get_httpStatusCode() == 200) //ok
                            {
                                JSONObject bodyRetorno = jsonRestResponse.get_bodyResponse();
                                onSucessoTipoEventoFamilia(evento, bodyRetorno);
                            }
                            else //erros
                            {
                                onFalhaTipoEventoFamilia("Retorno HTTP não esperado.");
                            }
                        }
                    },
                    error -> onFalhaTipoEventoFamilia(error.getMessage())
            );

            //envia requisicao
            addToRequestQueue(jsonRequest);
        }
        catch (Exception ex){
            Log.d("Error", "Erro ao requisitar Tipo Evento de familia: " + ex.getLocalizedMessage());
        }
    }

    private void onFalhaTipoEventoFamilia(String message) {
        Log.d("Error", "Erro ao requisitar Tipo Evento de familia: " + message);
        _restCallback.onRestResult(false);
    }

    private void onSucessoTipoEventoFamilia(Evento evento, JSONObject tipoEventoJson) {
        try {
            contadorSincronizacao--;

            if (tipoEventoJson != null)
            {
                //criando tipo evento
                TipoEvento tipoEvento = new TipoEvento(tipoEventoJson.getInt("idTipoEvento"), tipoEventoJson.getString("nome"));
                evento.setTipoEvento(tipoEvento);
            }

            //se todos os requests foram executados
            if (contadorSincronizacao == 0)
            {
                _restCallback.onRestResult(true);
            }
        } catch (JSONException e) {
            Log.d("Error", "Erro ao requisitar Tipo Evento de familia: " + e.getMessage());
        }
    }

    public void CarregarUsuarioEventosFamiliasAsync(RestCallback restCallback) {
        try{
            //seta retorno
            _restCallback = restCallback;

            for (int i = 0 ; i < getFamilias().size() ; i++)
            {
                Familia familia = getFamilias().get(i);
                contadorSincronizacao = contadorSincronizacao + familia.getEventos().size();
                for (int j = 0 ; j < familia.getEventos().size() ; j++) {
                    Evento evento = familia.getEventos().get(j);
                    CarregarUsuarioEventoAsync(evento);
                }
            }
        }
        catch (Exception ex)
        {
            //ignore
        }
    }

    private void CarregarUsuarioEventoAsync(Evento evento) {
        try {
            //monta url requisicao
            String url = "usuarios/" + evento.getIdUsuario();

            //monta headers adicionais
            Map headers = new ArrayMap();

            //monta body
            JSONObject postBody = new JSONObject();

            //monta requisicao
            JsonRestRequest jsonRequest = new JsonRestRequest(((Activity)mCtx).getApplication(), Request.Method.GET, true, url, headers, postBody,
                    new Response.Listener<JsonRestRequest.JsonRestResponse>() {
                        @Override
                        public void onResponse(JsonRestRequest.JsonRestResponse jsonRestResponse) {
                            if (jsonRestResponse.get_httpStatusCode() == 200) //ok
                            {
                                JSONObject bodyRetorno = jsonRestResponse.get_bodyResponse();
                                onSucessoUsuarioEventoFamilia(evento, bodyRetorno);
                            }
                            else //erros
                            {
                                onFalhaUsuarioEventoFamilia("Retorno HTTP não esperado.");
                            }
                        }
                    },
                    error -> onFalhaUsuarioEventoFamilia(error.getMessage())
            );

            //envia requisicao
            addToRequestQueue(jsonRequest);
        }
        catch (Exception ex){
            Log.d("Error", "Erro ao requisitar Usuario de Evento de familia: " + ex.getLocalizedMessage());
        }
    }

    private void onFalhaUsuarioEventoFamilia(String message) {
        Log.d("Error", "Erro ao requisitar Usuario de Evento de familia: " + message);
        _restCallback.onRestResult(false);
    }

    private void onSucessoUsuarioEventoFamilia(Evento evento, JSONObject usuarioJson) {
        try {
            contadorSincronizacao--;

            if (usuarioJson != null)
            {
                //criando usuario
                Usuario usuario = new Usuario(usuarioJson.getInt("idUsuario"), usuarioJson.getString("nome"), usuarioJson.getString("email"));
                evento.setUsuarioEvento(usuario);
            }

            //se todos os requests foram executados
            if (contadorSincronizacao == 0)
            {
                _restCallback.onRestResult(true);
            }
        } catch (JSONException e) {
            Log.d("Error", "Erro ao requisitar Usuario de Evento de familia: " + e.getMessage());
        }
    }

    public void CarregarItensEventosFamiliasAsync(RestCallback restCallback) {
        try{
            //seta retorno
            _restCallback = restCallback;

            for (int i = 0 ; i < getFamilias().size() ; i++)
            {
                Familia familia = getFamilias().get(i);
                contadorSincronizacao = contadorSincronizacao + familia.getEventos().size();
                for (int j = 0 ; j < familia.getEventos().size() ; j++) {
                    Evento evento = familia.getEventos().get(j);
                    CarregarItensEventoAsync(evento);
                }
            }
        }
        catch (Exception ex)
        {
            //ignore
        }
    }

    private void CarregarItensEventoAsync(Evento evento) {
        try {
            //monta url requisicao
            String url = "eventos/" + evento.getIdEvento() + "/itens";

            //monta headers adicionais
            Map headers = new ArrayMap();

            //monta body
            JSONObject postBody = new JSONObject();

            //monta requisicao
            JsonRestRequest jsonRequest = new JsonRestRequest(((Activity)mCtx).getApplication(), Request.Method.GET, true, url, headers, postBody,
                    new Response.Listener<JsonRestRequest.JsonRestResponse>() {
                        @Override
                        public void onResponse(JsonRestRequest.JsonRestResponse jsonRestResponse) {
                            if (jsonRestResponse.get_httpStatusCode() == 200) //ok
                            {
                                JSONObject bodyRetorno = jsonRestResponse.get_bodyResponse();
                                onSucessoItensEventoFamilia(evento, bodyRetorno);
                            }
                            else //erros
                            {
                                onFalhaItensEventoFamilia("Retorno HTTP não esperado.");
                            }
                        }
                    },
                    error -> onFalhaItensEventoFamilia(error.getMessage())
            );

            //envia requisicao
            addToRequestQueue(jsonRequest);
        }
        catch (Exception ex){
            Log.d("Error", "Erro ao requisitar Itens de Eventos de familia: " + ex.getLocalizedMessage());
        }
    }

    private void onFalhaItensEventoFamilia(String message) {
        Log.d("Error", "Erro ao requisitar Itens de Eventos de familia: " + message);
        _restCallback.onRestResult(false);
    }

    private void onSucessoItensEventoFamilia(Evento evento, JSONObject bodyRetorno) {
        try {
            contadorSincronizacao--;

            if (bodyRetorno != null)
            {
                ArrayList<Item> itens = new ArrayList<>();

                Object jsonBody = bodyRetorno.get("item");
                if (jsonBody instanceof JSONArray) {
                    // se for um vetor de elementos
                    JSONArray itensJson = (JSONArray)jsonBody;

                    for (int i = 0 ; i < itensJson.length() ; i++) {
                        JSONObject itemJson = itensJson.getJSONObject(i);

                        //criando item
                        Item item = new Item(itemJson.getInt("idItem"), itemJson.getInt("quantidade"), itemJson.getInt("idTipoItem"));
                        itens.add(item);
                    }
                }
                else if (jsonBody instanceof JSONObject) {
                    //se for so um elemento
                    JSONObject itemJson = (JSONObject)jsonBody;

                    //criando item
                    Item item = new Item(itemJson.getInt("idItem"), itemJson.getInt("quantidade"), itemJson.getInt("idTipoItem"));
                    itens.add(item);
                }

                evento.setItensEvento(itens);
            }
            else
            {
                ArrayList<Item> itens = new ArrayList<>();
                evento.setItensEvento(itens);
            }

            //se todos os requests foram executados
            if (contadorSincronizacao == 0)
            {
                _restCallback.onRestResult(true);
            }
        } catch (JSONException e) {
            Log.d("Error", "Erro ao requisitar Itens de Eventos de familia: " + e.getMessage());
        }
    }

    public void CarregarComentariosEventosFamiliasAsync(RestCallback restCallback) {
        try{
            //seta retorno
            _restCallback = restCallback;

            for (int i = 0 ; i < getFamilias().size() ; i++)
            {
                Familia familia = getFamilias().get(i);
                contadorSincronizacao = contadorSincronizacao + familia.getEventos().size();
                for (int j = 0 ; j < familia.getEventos().size() ; j++) {
                    Evento evento = familia.getEventos().get(j);
                    CarregarComentariosEventoAsync(evento);
                }
            }
        }
        catch (Exception ex)
        {
            //ignore
        }
    }

    private void CarregarComentariosEventoAsync(Evento evento) {
        try {
            //monta url requisicao
            String url = "eventos/" + evento.getIdEvento() + "/comentarios";

            //monta headers adicionais
            Map headers = new ArrayMap();

            //monta body
            JSONObject postBody = new JSONObject();

            //monta requisicao
            JsonRestRequest jsonRequest = new JsonRestRequest(((Activity)mCtx).getApplication(), Request.Method.GET, true, url, headers, postBody,
                    new Response.Listener<JsonRestRequest.JsonRestResponse>() {
                        @Override
                        public void onResponse(JsonRestRequest.JsonRestResponse jsonRestResponse) {
                            if (jsonRestResponse.get_httpStatusCode() == 200) //ok
                            {
                                JSONObject bodyRetorno = jsonRestResponse.get_bodyResponse();
                                onSucessoComentariosEventoFamilia(evento, bodyRetorno);
                            }
                            else //erros
                            {
                                onFalhaComentariosEventoFamilia("Retorno HTTP não esperado.");
                            }
                        }
                    },
                    error -> onFalhaComentariosEventoFamilia(error.getMessage())
            );

            //envia requisicao
            addToRequestQueue(jsonRequest);
        }
        catch (Exception ex){
            Log.d("Error", "Erro ao requisitar Comentarios de Eventos de familia: " + ex.getLocalizedMessage());
        }
    }

    private void onFalhaComentariosEventoFamilia(String message) {
        Log.d("Error", "Erro ao requisitar Comentarios de Eventos de familia: " + message);
        _restCallback.onRestResult(false);
    }

    private void onSucessoComentariosEventoFamilia(Evento evento, JSONObject bodyRetorno) {
        try {
            contadorSincronizacao--;

            if (bodyRetorno != null)
            {
                ArrayList<Comentario> comentarios = new ArrayList<>();

                Object jsonBody = bodyRetorno.get("comentario");
                if (jsonBody instanceof JSONArray) {
                    // se for um vetor de elementos
                    JSONArray itensJson = (JSONArray)jsonBody;

                    for (int i = 0 ; i < itensJson.length() ; i++) {
                        JSONObject comentarioJson = itensJson.getJSONObject(i);

                        //recuperando data do comentario
                        String dateStr = comentarioJson.getString("dataCriacao");
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                        Date dataCriacao = sdf.parse(dateStr);

                        //criando comentario
                        Comentario comentario = new Comentario(comentarioJson.getInt("idComentario"), comentarioJson.getString("descricao"), dataCriacao);
                        comentarios.add(comentario);
                    }
                }
                else if (jsonBody instanceof JSONObject) {
                    //se for so um elemento
                    JSONObject comentarioJson = (JSONObject)jsonBody;

                    //recuperando data do comentario
                    String dateStr = comentarioJson.getString("dataCriacao");
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                    Date dataCriacao = sdf.parse(dateStr);

                    //criando comentario
                    Comentario comentario = new Comentario(comentarioJson.getInt("idComentario"), comentarioJson.getString("descricao"), dataCriacao);
                    comentarios.add(comentario);
                }

                evento.setComentariosEvento(comentarios);
            }
            else
            {
                ArrayList<Comentario> comentarios = new ArrayList<>();
                evento.setComentariosEvento(comentarios);
            }

            //se todos os requests foram executados
            if (contadorSincronizacao == 0)
            {
                _restCallback.onRestResult(true);
            }
        } catch (JSONException | ParseException e) {
            Log.d("Error", "Erro ao requisitar Comentarios de Eventos de familia: " + e.getMessage());
        }
    }

    public void CarregarTiposItensAsync(RestCallback restCallback) {
        try{
            //seta retorno
            _restCallback = restCallback;

            for (int i = 0 ; i < getFamilias().size() ; i++)
            {
                Familia familia = getFamilias().get(i);
                for (int j = 0 ; j < familia.getEventos().size() ; j++)
                {
                    Evento evento = familia.getEventos().get(j);
                    contadorSincronizacao = contadorSincronizacao + evento.getItensEvento().size();
                    for (int o = 0 ; o < evento.getItensEvento().size() ; o++)
                    {
                        Item item = evento.getItensEvento().get(o);
                        CarregarTipoItemAsync(item);
                    }
                }
            }

        }
        catch (Exception ex)
        {
            //ignore
        }
    }

    private void CarregarTipoItemAsync(Item item) {
        try {
            //monta url requisicao
            String url = "tiposItem/" + item.getIdTipoItem();

            //monta headers adicionais
            Map headers = new ArrayMap();

            //monta body
            JSONObject postBody = new JSONObject();

            //monta requisicao
            JsonRestRequest jsonRequest = new JsonRestRequest(((Activity)mCtx).getApplication(), Request.Method.GET, true, url, headers, postBody,
                    new Response.Listener<JsonRestRequest.JsonRestResponse>() {
                        @Override
                        public void onResponse(JsonRestRequest.JsonRestResponse jsonRestResponse) {
                            if (jsonRestResponse.get_httpStatusCode() == 200) //ok
                            {
                                JSONObject bodyRetorno = jsonRestResponse.get_bodyResponse();
                                onSucessoTipoItem(item, bodyRetorno);
                            }
                            else //erros
                            {
                                onFalhaTipoItem("Retorno HTTP não esperado.");
                            }
                        }
                    },
                    error -> onFalhaTipoItem(error.getMessage())
            );

            //envia requisicao
            addToRequestQueue(jsonRequest);
        }
        catch (Exception ex){
            Log.d("Error", "Erro ao requisitar Tipo Item de evento: " + ex.getLocalizedMessage());
        }
    }

    private void onFalhaTipoItem(String message) {
        Log.d("Error", "Erro ao requisitar Tipo Item de evento: " + message);
        _restCallback.onRestResult(false);
    }

    private void onSucessoTipoItem(Item item, JSONObject tipoItemJson) {
        try {
            contadorSincronizacao--;

            if (tipoItemJson != null)
            {
                //criando tipo item
                TipoItem tipoItem = new TipoItem(tipoItemJson.getInt("idTipoItem"), tipoItemJson.getString("nome"));
                item.setTipoItem(tipoItem);
            }

            //se todos os requests foram executados
            if (contadorSincronizacao == 0)
            {
                _restCallback.onRestResult(true);
            }
        } catch (JSONException e) {
            Log.d("Error", "Erro ao requisitar Tipo Item de evento: " + e.getMessage());
        }
    }

    public void CarregarUsuariosFamiliasAsync(RestCallback restCallback) {
        try{
            //seta retorno
            _restCallback = restCallback;

            contadorSincronizacao = getFamilias().size();
            for (int i = 0 ; i < getFamilias().size() ; i++)
            {
                CarregarUsuariosFamiliaAsync(getFamilias().get(i));
            }

        }
        catch (Exception ex)
        {
            //ignore
        }
    }

    private void CarregarUsuariosFamiliaAsync(Familia familia) {
        try {
            //monta url requisicao
            String url = "familias/" + familia.getIdFamilia() + "/usuarios";

            //monta headers adicionais
            Map headers = new ArrayMap();

            //monta body
            JSONObject postBody = new JSONObject();

            //monta requisicao
            JsonRestRequest jsonRequest = new JsonRestRequest(((Activity)mCtx).getApplication(), Request.Method.GET, true, url, headers, postBody,
                    new Response.Listener<JsonRestRequest.JsonRestResponse>() {
                        @Override
                        public void onResponse(JsonRestRequest.JsonRestResponse jsonRestResponse) {
                            if (jsonRestResponse.get_httpStatusCode() == 200) //ok
                            {
                                JSONObject bodyRetorno = jsonRestResponse.get_bodyResponse();
                                onSucessoUsuariosFamilia(familia, bodyRetorno);
                            }
                            else //erros
                            {
                                onFalhaUsuariosFamilia("Retorno HTTP não esperado.");
                            }
                        }
                    },
                    error -> onFalhaUsuariosFamilia(error.getMessage())
            );

            //envia requisicao
            addToRequestQueue(jsonRequest);
        }
        catch (Exception ex){
            Log.d("Error", "Erro ao requisitar Usuarios de familia: " + ex.getLocalizedMessage());
        }
    }

    private void onFalhaUsuariosFamilia(String message) {
        Log.d("Error", "Erro ao requisitar Usuarios de familia: " + message);
        _restCallback.onRestResult(false);
    }

    private void onSucessoUsuariosFamilia(Familia familia, JSONObject bodyRetorno) {
        try {
            contadorSincronizacao--;

            if (bodyRetorno != null)
            {
                ArrayList<Usuario> usuarios = new ArrayList<>();

                Object jsonBody = bodyRetorno.get("usuario");
                if (jsonBody instanceof JSONArray) {
                    // se for um vetor de elementos
                    JSONArray eventosJson = (JSONArray)jsonBody;

                    for (int i = 0 ; i < eventosJson.length() ; i++) {
                        JSONObject usuarioJson = eventosJson.getJSONObject(i);
                        Usuario usuario = new Usuario(usuarioJson.getInt("idUsuario"), usuarioJson.getString("nome"), usuarioJson.getString("email"));
                        usuarios.add(usuario);
                    }
                }
                else if (jsonBody instanceof JSONObject) {
                    //se for so um elemento
                    JSONObject usuarioJson = (JSONObject)jsonBody;
                    Usuario usuario = new Usuario(usuarioJson.getInt("idUsuario"), usuarioJson.getString("nome"), usuarioJson.getString("email"));
                    usuarios.add(usuario);
                }

                familia.setUsuarios(usuarios);
            }
            else
            {
                ArrayList<Usuario> usuarios = new ArrayList<>();
                familia.setUsuarios(usuarios);
            }

            //se todos os requests foram executados
            if (contadorSincronizacao == 0)
            {
                _restCallback.onRestResult(true);
            }
        } catch (JSONException e) {
            Log.d("Error", "Erro ao requisitar Usuarios de familia: " + e.getMessage());
        }
    }

    public void CarregarNoticiasFamiliasAsync(RestCallback restCallback) {
        try{
            //seta retorno
            _restCallback = restCallback;

            contadorSincronizacao = getFamilias().size();
            for (int i = 0 ; i < getFamilias().size() ; i++)
            {
                CarregarNoticiasFamiliaAsync(getFamilias().get(i));
            }

        }
        catch (Exception ex)
        {
            //ignore
        }
    }

    private void CarregarNoticiasFamiliaAsync(Familia familia) {
        try {
            //monta url requisicao
            String url = "familias/" + familia.getIdFamilia() + "/noticias";

            //monta headers adicionais
            Map headers = new ArrayMap();

            //monta body
            JSONObject postBody = new JSONObject();

            //monta requisicao
            JsonRestRequest jsonRequest = new JsonRestRequest(((Activity)mCtx).getApplication(), Request.Method.GET, true, url, headers, postBody,
                    new Response.Listener<JsonRestRequest.JsonRestResponse>() {
                        @Override
                        public void onResponse(JsonRestRequest.JsonRestResponse jsonRestResponse) {
                            if (jsonRestResponse.get_httpStatusCode() == 200) //ok
                            {
                                JSONObject bodyRetorno = jsonRestResponse.get_bodyResponse();
                                onSucessoNoticiasFamilia(familia, bodyRetorno);
                            }
                            else //erros
                            {
                                onFalhaNoticiasFamilia("Retorno HTTP não esperado.");
                            }
                        }
                    },
                    error -> onFalhaNoticiasFamilia(error.getMessage())
            );

            //envia requisicao
            addToRequestQueue(jsonRequest);
        }
        catch (Exception ex){
            Log.d("Error", "Erro ao requisitar Noticias de familia: " + ex.getLocalizedMessage());
        }
    }

    private void onFalhaNoticiasFamilia(String message) {
        Log.d("Error", "Erro ao requisitar Noticias de familia: " + message);
        _restCallback.onRestResult(false);
    }

    private void onSucessoNoticiasFamilia(Familia familia, JSONObject bodyRetorno) {
        try {
            contadorSincronizacao--;

            if (bodyRetorno != null)
            {
                ArrayList<Noticia> noticias = new ArrayList<>();

                Object jsonBody = bodyRetorno.get("noticia");
                if (jsonBody instanceof JSONArray) {
                    // se for um vetor de elementos
                    JSONArray noticiasJson = (JSONArray)jsonBody;

                    for (int i = 0 ; i < noticiasJson.length() ; i++) {
                        JSONObject noticiaJson = noticiasJson.getJSONObject(i);
                        Noticia noticia = new Noticia(noticiaJson.getInt("idNoticia"), noticiaJson.getString("descricao"), noticiaJson.getInt("idUsuario"));
                        noticias.add(noticia);
                    }
                }
                else if (jsonBody instanceof JSONObject) {
                    //se for so um elemento
                    JSONObject noticiaJson = (JSONObject)jsonBody;
                    Noticia noticia = new Noticia(noticiaJson.getInt("idNoticia"), noticiaJson.getString("descricao"), noticiaJson.getInt("idUsuario"));
                    noticias.add(noticia);
                }

                familia.setNoticias(noticias);
            }
            else
            {
                ArrayList<Noticia> noticias = new ArrayList<>();
                familia.setNoticias(noticias);
            }

            //se todos os requests foram executados
            if (contadorSincronizacao == 0)
            {
                _restCallback.onRestResult(true);
            }
        } catch (JSONException e) {
            Log.d("Error", "Erro ao requisitar Noticias de familia: " + e.getMessage());
        }
    }

    public void CarregarComentariosNoticiasFamiliasAsync(RestCallback restCallback) {
        try {
            //seta retorno
            _restCallback = restCallback;

            for (int i = 0; i < getFamilias().size(); i++) {
                Familia familia = getFamilias().get(i);
                contadorSincronizacao = contadorSincronizacao + familia.getNoticias().size();
                for (int j = 0; j < familia.getNoticias().size(); j++) {
                    Noticia noticia = familia.getNoticias().get(j);
                    CarregarComentariosNoticiaAsync(noticia);
                }
            }
        }
        catch (Exception ex)
        {
            //ignore
        }
    }

    private void CarregarComentariosNoticiaAsync(Noticia noticia) {
        try {
            //monta url requisicao
            String url = "noticias/" + noticia.getIdNoticia() + "/comentarios";

            //monta headers adicionais
            Map headers = new ArrayMap();

            //monta body
            JSONObject postBody = new JSONObject();

            //monta requisicao
            JsonRestRequest jsonRequest = new JsonRestRequest(((Activity)mCtx).getApplication(), Request.Method.GET, true, url, headers, postBody,
                    new Response.Listener<JsonRestRequest.JsonRestResponse>() {
                        @Override
                        public void onResponse(JsonRestRequest.JsonRestResponse jsonRestResponse) {
                            if (jsonRestResponse.get_httpStatusCode() == 200) //ok
                            {
                                JSONObject bodyRetorno = jsonRestResponse.get_bodyResponse();
                                onSucessoComentariosNoticiaFamilia(noticia, bodyRetorno);
                            }
                            else //erros
                            {
                                onFalhaComentariosNoticiaFamilia("Retorno HTTP não esperado.");
                            }
                        }
                    },
                    error -> onFalhaComentariosEventoFamilia(error.getMessage())
            );

            //envia requisicao
            addToRequestQueue(jsonRequest);
        }
        catch (Exception ex){
            Log.d("Error", "Erro ao requisitar Comentarios de Notica de familia: " + ex.getLocalizedMessage());
        }
    }

    private void onFalhaComentariosNoticiaFamilia(String message) {
        Log.d("Error", "Erro ao requisitar Comentarios de Notica de familia: " + message);
        _restCallback.onRestResult(false);
    }

    private void onSucessoComentariosNoticiaFamilia(Noticia noticia, JSONObject bodyRetorno) {
        try {
            contadorSincronizacao--;

            if (bodyRetorno != null)
            {
                ArrayList<Comentario> comentarios = new ArrayList<>();

                Object jsonBody = bodyRetorno.get("comentario");
                if (jsonBody instanceof JSONArray) {
                    // se for um vetor de elementos
                    JSONArray itensJson = (JSONArray)jsonBody;

                    for (int i = 0 ; i < itensJson.length() ; i++) {
                        JSONObject comentarioJson = itensJson.getJSONObject(i);

                        //recuperando data do comentario
                        String dateStr = comentarioJson.getString("dataCriacao");
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                        Date dataCriacao = sdf.parse(dateStr);

                        //criando comentario
                        Comentario comentario = new Comentario(comentarioJson.getInt("idComentario"), comentarioJson.getString("descricao"), dataCriacao);
                        comentarios.add(comentario);
                    }
                }
                else if (jsonBody instanceof JSONObject) {
                    //se for so um elemento
                    JSONObject comentarioJson = (JSONObject)jsonBody;

                    //recuperando data do comentario
                    String dateStr = comentarioJson.getString("dataCriacao");
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                    Date dataCriacao = sdf.parse(dateStr);

                    //criando comentario
                    Comentario comentario = new Comentario(comentarioJson.getInt("idComentario"), comentarioJson.getString("descricao"), dataCriacao);
                    comentarios.add(comentario);
                }

                noticia.setComentarios(comentarios);
            }
            else
            {
                ArrayList<Comentario> comentarios = new ArrayList<>();
                noticia.setComentarios(comentarios);
            }

            //se todos os requests foram executados
            if (contadorSincronizacao == 0)
            {
                _restCallback.onRestResult(true);
            }
        } catch (JSONException | ParseException e) {
            Log.d("Error", "Erro ao requisitar Comentarios de Notica de familia: " + e.getMessage());
        }
    }

    public void CarregarUsuariosNoticiasFamiliasAsync(RestCallback restCallback) {
        try{
            //seta retorno
            _restCallback = restCallback;

            for (int i = 0 ; i < getFamilias().size() ; i++)
            {
                Familia familia = getFamilias().get(i);
                contadorSincronizacao = contadorSincronizacao + familia.getNoticias().size();
                for (int j = 0 ; j < familia.getNoticias().size() ; j++) {
                    Noticia noticia = familia.getNoticias().get(j);
                    CarregarUsuariosNoticiaAsync(noticia);
                }
            }
        }
        catch (Exception ex)
        {
            //ignore
        }
    }

    private void CarregarUsuariosNoticiaAsync(Noticia noticia) {
        try {
            //monta url requisicao
            String url = "usuarios/" + noticia.getIdUsuario();

            //monta headers adicionais
            Map headers = new ArrayMap();

            //monta body
            JSONObject postBody = new JSONObject();

            //monta requisicao
            JsonRestRequest jsonRequest = new JsonRestRequest(((Activity)mCtx).getApplication(), Request.Method.GET, true, url, headers, postBody,
                    new Response.Listener<JsonRestRequest.JsonRestResponse>() {
                        @Override
                        public void onResponse(JsonRestRequest.JsonRestResponse jsonRestResponse) {
                            if (jsonRestResponse.get_httpStatusCode() == 200) //ok
                            {
                                JSONObject bodyRetorno = jsonRestResponse.get_bodyResponse();
                                onSucessoUsuariossNoticiaFamilia(noticia, bodyRetorno);
                            }
                            else //erros
                            {
                                onFalhaUsuariossNoticiaFamilia("Retorno HTTP não esperado.");
                            }
                        }
                    },
                    error -> onFalhaUsuariossNoticiaFamilia(error.getMessage())
            );

            //envia requisicao
            addToRequestQueue(jsonRequest);
        }
        catch (Exception ex){
            Log.d("Error", "Erro ao requisitar usuarios de Notica de familia: " + ex.getLocalizedMessage());
        }
    }

    private void onFalhaUsuariossNoticiaFamilia(String message) {
        Log.d("Error", "Erro ao requisitar usuarios de Notica de familia: " + message);
        _restCallback.onRestResult(false);
    }

    private void onSucessoUsuariossNoticiaFamilia(Noticia noticia, JSONObject bodyRetorno) {
        try {
            contadorSincronizacao--;

            if (bodyRetorno != null)
            {
                JSONObject usuarioJson = (JSONObject)bodyRetorno;
                Usuario usuario = new Usuario(usuarioJson.getInt("idUsuario"), usuarioJson.getString("nome"), usuarioJson.getString("email"));
                noticia.setUsuarioCriador(usuario);
            }

            //se todos os requests foram executados
            if (contadorSincronizacao == 0)
            {
                _restCallback.onRestResult(true);
            }
        } catch (JSONException e) {
            Log.d("Error", "Erro ao requisitar usuarios de Notica de familia: " + e.getMessage());
        }
    }

    public void CarregarVideosFamiliasAsync(RestCallback restCallback) {
        try
        {
            //seta retorno
            _restCallback = restCallback;

            contadorSincronizacao = getFamilias().size();
            for (int i = 0 ; i < getFamilias().size() ; i++)
            {
                CarregarVideosFamiliaAsync(getFamilias().get(i));
            }
        }
        catch (Exception ex)
        {
            //ignore
        }
    }

    private void CarregarVideosFamiliaAsync(Familia familia) {
        try {
            //monta url requisicao
            String url = "galerias/" + familia.getIdGaleria() + "/videos";

            //monta headers adicionais
            Map headers = new ArrayMap();

            //monta body
            JSONObject postBody = new JSONObject();

            //monta requisicao
            JsonRestRequest jsonRequest = new JsonRestRequest(((Activity)mCtx).getApplication(), Request.Method.GET, true, url, headers, postBody,
                    new Response.Listener<JsonRestRequest.JsonRestResponse>() {
                        @Override
                        public void onResponse(JsonRestRequest.JsonRestResponse jsonRestResponse) {
                            if (jsonRestResponse.get_httpStatusCode() == 200) //ok
                            {
                                JSONObject bodyRetorno = jsonRestResponse.get_bodyResponse();
                                onSucessoVideosFamilia(familia, bodyRetorno);
                            }
                            else //erros
                            {
                                onFalhaVideosFamilia("Retorno HTTP não esperado.");
                            }
                        }
                    },
                    error -> onFalhaVideosFamilia(error.getMessage())
            );

            //envia requisicao
            addToRequestQueue(jsonRequest);
        }
        catch (Exception ex){
            Log.d("Error", "Erro ao requisitar Videos de familia: " + ex.getLocalizedMessage());
        }
    }

    private void onFalhaVideosFamilia(String message) {
        Log.d("Error", "Erro ao requisitar Videos de familia: " + message);
        _restCallback.onRestResult(false);
    }

    private void onSucessoVideosFamilia(Familia familia, JSONObject bodyRetorno) {
        try {
            contadorSincronizacao--;

            if (bodyRetorno != null)
            {
                ArrayList<Video> videos = new ArrayList<>();

                Object jsonBody = bodyRetorno.get("video");
                if (jsonBody instanceof JSONArray) {
                    // se for um vetor de elementos
                    JSONArray videosJson = (JSONArray)jsonBody;

                    for (int i = 0 ; i < videosJson.length() ; i++) {
                        JSONObject videoJson = videosJson.getJSONObject(i);

                        //recuperando data do video
                        String dateStr = videoJson.getString("dataCriacao");
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                        Date dataVideo = sdf.parse(dateStr);

                        Video video = new Video(videoJson.getInt("idVideo"), videoJson.getString("descricao"), dataVideo, videoJson.getString("link"));
                        videos.add(video);
                    }
                }
                else if (jsonBody instanceof JSONObject) {
                    //se for so um elemento
                    JSONObject videoJson = (JSONObject)jsonBody;

                    //recuperando data do video
                    String dateStr = videoJson.getString("dataCriacao");
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                    Date dataVideo = sdf.parse(dateStr);

                    Video video = new Video(videoJson.getInt("idVideo"), videoJson.getString("descricao"), dataVideo, videoJson.getString("link"));
                    videos.add(video);
                }

                familia.setVideos(videos);
            }
            else
            {
                ArrayList<Video> videos = new ArrayList<>();
                familia.setVideos(videos);
            }

            //se todos os requests foram executados
            if (contadorSincronizacao == 0)
            {
                _restCallback.onRestResult(true);
            }
        } catch (JSONException | ParseException e) {
            Log.d("Error", "Erro ao requisitar Videos de familia: " + e.getMessage());
        }
    }

    public void CarregarAlbunsFamiliasAsync(RestCallback restCallback) {

        try {
            //seta retorno
            _restCallback = restCallback;

            contadorSincronizacao = getFamilias().size();
            for (int i = 0 ; i < getFamilias().size() ; i++)
            {
                CarregarAlbunsFamiliaAsync(getFamilias().get(i));
            }
        }
        catch (Exception ex)
        {
            //ignore
        }
    }

    private void CarregarAlbunsFamiliaAsync(Familia familia) {
        try {
            //monta url requisicao
            String url = "familias/" + familia.getIdFamilia() + "/albuns";

            //monta headers adicionais
            Map headers = new ArrayMap();

            //monta body
            JSONObject postBody = new JSONObject();

            //monta requisicao
            JsonRestRequest jsonRequest = new JsonRestRequest(((Activity)mCtx).getApplication(), Request.Method.GET, true, url, headers, postBody,
                    new Response.Listener<JsonRestRequest.JsonRestResponse>() {
                        @Override
                        public void onResponse(JsonRestRequest.JsonRestResponse jsonRestResponse) {
                            if (jsonRestResponse.get_httpStatusCode() == 200) //ok
                            {
                                JSONObject bodyRetorno = jsonRestResponse.get_bodyResponse();
                                onSucessoAlbunsFamilia(familia, bodyRetorno);
                            }
                            else //erros
                            {
                                onFalhaAlbunsFamilia("Retorno HTTP não esperado.");
                            }
                        }
                    },
                    error -> onFalhaAlbunsFamilia(error.getMessage())
            );

            //envia requisicao
            addToRequestQueue(jsonRequest);
        }
        catch (Exception ex){
            Log.d("Error", "Erro ao requisitar Albuns de familia: " + ex.getLocalizedMessage());
        }
    }

    private void onFalhaAlbunsFamilia(String message) {
        Log.d("Error", "Erro ao requisitar Albuns de familia: " + message);
        _restCallback.onRestResult(false);
    }

    private void onSucessoAlbunsFamilia(Familia familia, JSONObject bodyRetorno) {
        try {
            contadorSincronizacao--;

            if (bodyRetorno != null)
            {
                ArrayList<Album> albuns = new ArrayList<>();

                Object jsonBody = bodyRetorno.get("album");
                if (jsonBody instanceof JSONArray) {
                    // se for um vetor de elementos
                    JSONArray albunsJson = (JSONArray)jsonBody;

                    for (int i = 0 ; i < albunsJson.length() ; i++) {
                        JSONObject albumJson = albunsJson.getJSONObject(i);

                        //recuperando data do album
                        String dateStr = albumJson.getString("dataCriacao");
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                        Date dataAlbum = sdf.parse(dateStr);

                        Album album = new Album(albumJson.getInt("idAlbum"), albumJson.getString("nome"), dataAlbum, albumJson.getString("descricao"));
                        albuns.add(album);
                    }
                }
                else if (jsonBody instanceof JSONObject) {
                    //se for so um elemento
                    JSONObject albumJson = (JSONObject)jsonBody;

                    //recuperando data do album
                    String dateStr = albumJson.getString("dataCriacao");
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                    Date dataAlbum = sdf.parse(dateStr);

                    Album album = new Album(albumJson.getInt("idAlbum"), albumJson.getString("nome"), dataAlbum, albumJson.getString("descricao"));
                    albuns.add(album);
                }

                familia.setAlbuns(albuns);
            }
            else
            {
                ArrayList<Album> albuns = new ArrayList<>();
                familia.setAlbuns(albuns);
            }

            //se todos os requests foram executados
            if (contadorSincronizacao == 0)
            {
                _restCallback.onRestResult(true);
            }
        } catch (JSONException | ParseException e) {
            Log.d("Error", "Erro ao requisitar Noticias de familia: " + e.getMessage());
        }
    }

    public void CarregarFotosAlbunsFamiliasAsync(RestCallback restCallback) {
        try{
            //seta retorno
            _restCallback = restCallback;

            for (int i = 0 ; i < getFamilias().size() ; i++)
            {
                Familia familia = getFamilias().get(i);
                contadorSincronizacao = contadorSincronizacao + familia.getAlbuns().size();
                for (int j = 0 ; j < familia.getAlbuns().size() ; j++) {
                    Album album = familia.getAlbuns().get(j);
                    CarregarFotosAlbumAsync(album);
                }
            }
        }
        catch (Exception ex)
        {
            //ignore
        }
    }

    private void CarregarFotosAlbumAsync(Album album) {
        try {
            //monta url requisicao
            String url = "albuns/" + album.getIdAlbum() + "/fotos";

            //monta headers adicionais
            Map headers = new ArrayMap();

            //monta body
            JSONObject postBody = new JSONObject();

            //monta requisicao
            JsonRestRequest jsonRequest = new JsonRestRequest(((Activity)mCtx).getApplication(), Request.Method.GET, true, url, headers, postBody,
                    new Response.Listener<JsonRestRequest.JsonRestResponse>() {
                        @Override
                        public void onResponse(JsonRestRequest.JsonRestResponse jsonRestResponse) {
                            if (jsonRestResponse.get_httpStatusCode() == 200) //ok
                            {
                                JSONObject bodyRetorno = jsonRestResponse.get_bodyResponse();
                                onSucessoFotosAlbumFamilia(album, bodyRetorno);
                            }
                            else //erros
                            {
                                onFalhaFotosAlbumFamilia("Retorno HTTP não esperado.");
                            }
                        }
                    },
                    error -> onFalhaFotosAlbumFamilia(error.getMessage())
            );

            //envia requisicao
            addToRequestQueue(jsonRequest);
        }
        catch (Exception ex){
            Log.d("Error", "Erro ao requisitar Fotos de Album de familia: " + ex.getLocalizedMessage());
        }
    }

    private void onFalhaFotosAlbumFamilia(String message) {
        Log.d("Error", "Erro ao requisitar Fotos de Album de familia: " + message);
        _restCallback.onRestResult(false);
    }

    private void onSucessoFotosAlbumFamilia(Album album, JSONObject bodyRetorno) {
        try {
            contadorSincronizacao--;

            if (bodyRetorno != null)
            {
                ArrayList<Foto> fotos = new ArrayList<>();

                Object jsonBody = bodyRetorno.get("foto");
                if (jsonBody instanceof JSONArray) {
                    // se for um vetor de elementos
                    JSONArray fotosJson = (JSONArray)jsonBody;

                    for (int i = 0 ; i < fotosJson.length() ; i++) {
                        JSONObject fotoJson = fotosJson.getJSONObject(i);

                        //recuperando data da foto
                        String dateStr = fotoJson.getString("dataCriacao");
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                        Date dataCriacao = sdf.parse(dateStr);

                        //criando foto
                        Foto foto = new Foto(fotoJson.getInt("idFoto"), fotoJson.getString("dados"), fotoJson.getString("descricao"), dataCriacao);
                        fotos.add(foto);
                    }
                }
                else if (jsonBody instanceof JSONObject) {
                    //se for so um elemento
                    JSONObject fotoJson = (JSONObject)jsonBody;

                    //recuperando data da foto
                    String dateStr = fotoJson.getString("dataCriacao");
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                    Date dataCriacao = sdf.parse(dateStr);

                    //criando foto
                    Foto foto = new Foto(fotoJson.getInt("idFoto"), fotoJson.getString("dados"), fotoJson.getString("descricao"), dataCriacao);
                    fotos.add(foto);
                }

                album.setFotos(fotos);
            }
            else
            {
                ArrayList<Foto> fotos = new ArrayList<>();
                album.setFotos(fotos);
            }

            //se todos os requests foram executados
            if (contadorSincronizacao == 0)
            {
                _restCallback.onRestResult(true);
            }
        } catch (JSONException | ParseException e) {
            Log.d("Error", "Erro ao requisitar Fotos de Album de familia: " + e.getMessage());
        }
    }

    public  void CarregarTiposEventosAsync(RestCallback restCallback) {
        try {

            //seta retorno
            _restCallback = restCallback;

            //monta url requisicao
            String url = "tiposEvento";

            //monta headers adicionais
            Map headers = new ArrayMap();

            //monta body
            JSONObject postBody = new JSONObject();

            //monta requisicao
            JsonRestRequest jsonRequest = new JsonRestRequest((((Activity)mCtx).getApplication()), Request.Method.GET, true, url, headers, postBody,
                    new Response.Listener<JsonRestRequest.JsonRestResponse>() {
                        @Override
                        public void onResponse(JsonRestRequest.JsonRestResponse jsonRestResponse) {
                            if (jsonRestResponse.get_httpStatusCode() == 200) //ok
                            {
                                JSONObject bodyRetorno = jsonRestResponse.get_bodyResponse();
                                onSucessoTiposEventos(bodyRetorno);
                            }
                            else //erros
                            {
                                onFalhaTiposEventos("Retorno HTTP não esperado.");
                            }
                        }
                    },
                    error -> onFalhaTiposEventos(error.getMessage())
            );

            //envia requisicao
            addToRequestQueue(jsonRequest);
        }
        catch (Exception ex){
            Log.d("Error", "Erro ao requisitar Tipos Eventos: " + ex.getLocalizedMessage());
        }
    }

    private void onFalhaTiposEventos(String message) {
        Log.d("Error", "Erro ao requisitar Tipos Eventos: " + message);
        _restCallback.onRestResult(false);
    }

    private void onSucessoTiposEventos(JSONObject bodyRetorno) {
        try {
            ArrayList<TipoEvento> tipoEventos = new ArrayList<>();

            if (bodyRetorno != null)
            {

                Object jsonBody = bodyRetorno.get("tipoEvento");
                if (jsonBody instanceof JSONArray) {
                    // se for um vetor de elementos
                    JSONArray tipoEventosJson = (JSONArray)jsonBody;

                    for (int i = 0 ; i < tipoEventosJson.length() ; i++) {
                        JSONObject tipoEventoJson = tipoEventosJson.getJSONObject(i);

                        TipoEvento tipoEvento = new TipoEvento(tipoEventoJson.getInt("idTipoEvento"), tipoEventoJson.getString("nome"));
                        tipoEventos.add(tipoEvento);
                    }
                }
                else if (jsonBody instanceof JSONObject) {
                    //se for so um elemento
                    JSONObject tipoEventoJson = (JSONObject)jsonBody;

                    TipoEvento tipoEvento = new TipoEvento(tipoEventoJson.getInt("idTipoEvento"), tipoEventoJson.getString("nome"));
                    tipoEventos.add(tipoEvento);
                }
            }
            else
            {
               tipoEventos = new ArrayList<>();
            }

            ((FamilystApplication)((Activity)mCtx).getApplication()).setTiposEventos(tipoEventos);
            _restCallback.onRestResult(true);

        } catch (JSONException e) {
            Log.d("Error", "Erro ao requisitar Tipos Eventos: " + e.getMessage());
        }
    }

    public  void CarregarTiposItemAsync(RestCallback restCallback) {
        try {

            //seta retorno
            _restCallback = restCallback;

            //monta url requisicao
            String url = "tiposItem";

            //monta headers adicionais
            Map headers = new ArrayMap();

            //monta body
            JSONObject postBody = new JSONObject();

            //monta requisicao
            JsonRestRequest jsonRequest = new JsonRestRequest((((Activity)mCtx).getApplication()), Request.Method.GET, true, url, headers, postBody,
                    new Response.Listener<JsonRestRequest.JsonRestResponse>() {
                        @Override
                        public void onResponse(JsonRestRequest.JsonRestResponse jsonRestResponse) {
                            if (jsonRestResponse.get_httpStatusCode() == 200) //ok
                            {
                                JSONObject bodyRetorno = jsonRestResponse.get_bodyResponse();
                                onSucessoTiposItem(bodyRetorno);
                            }
                            else //erros
                            {
                                onFalhaTiposItem("Retorno HTTP não esperado.");
                            }
                        }
                    },
                    error -> onFalhaTiposItem(error.getMessage())
            );

            //envia requisicao
            addToRequestQueue(jsonRequest);
        }
        catch (Exception ex){
            Log.d("Error", "Erro ao requisitar Tipos Itens: " + ex.getLocalizedMessage());
        }
    }

    private void onFalhaTiposItem(String message) {
        Log.d("Error", "Erro ao requisitar Tipos Itens: " + message);
        _restCallback.onRestResult(false);
    }

    private void onSucessoTiposItem(JSONObject bodyRetorno) {
        try {
            ArrayList<TipoItem> tipoItens = new ArrayList<>();

            if (bodyRetorno != null)
            {

                Object jsonBody = bodyRetorno.get("tipoItem");
                if (jsonBody instanceof JSONArray) {
                    // se for um vetor de elementos
                    JSONArray tipoItensJson = (JSONArray)jsonBody;

                    for (int i = 0 ; i < tipoItensJson.length() ; i++) {
                        JSONObject tipoItemJson = tipoItensJson.getJSONObject(i);

                        TipoItem tipoItem = new TipoItem(tipoItemJson.getInt("idTipoItem"), tipoItemJson.getString("nome"));
                        tipoItens.add(tipoItem);
                    }
                }
                else if (jsonBody instanceof JSONObject) {
                    //se for so um elemento
                    JSONObject tipoItemJson = (JSONObject)jsonBody;

                    TipoItem tipoItem = new TipoItem(tipoItemJson.getInt("idTipoItem"), tipoItemJson.getString("nome"));
                    tipoItens.add(tipoItem);
                }
            }
            else
            {
                tipoItens = new ArrayList<>();
            }

            ((FamilystApplication)((Activity)mCtx).getApplication()).setTiposItens(tipoItens);
            _restCallback.onRestResult(true);

        } catch (JSONException e) {
            Log.d("Error", "Erro ao requisitar Tipos Itens: " + e.getMessage());
        }
    }

    public void EnviarNoticia(String descricao, RestCallback restCallback)    {
        try {
            //seta retorno
            _restCallback = restCallback;

            //monta url requisicao
            String url = "noticias";

            //monta headers adicionais
            Map headers = new ArrayMap();

            //monta body
            JSONObject postBody = new JSONObject();
            postBody.put("descricao", descricao);
            postBody.put("idFamilia", getFamiliaAtual().getIdFamilia());
            postBody.put("idUsuario", getUsuarioAtual().getIdUsuario());

            //monta requisicao
            JsonRestRequest jsonRequest = new JsonRestRequest(((Activity)mCtx).getApplication(), Request.Method.POST, true, url, headers, postBody,
                    new Response.Listener<JsonRestRequest.JsonRestResponse>() {
                        @Override
                        public void onResponse(JsonRestRequest.JsonRestResponse jsonRestResponse) {
                            if (jsonRestResponse.get_httpStatusCode() == 201) //created
                            {
                                _restCallback.onRestResult(true);
                            }
                            else //erros
                            {
                                _restCallback.onRestResult(false);
                            }
                        }
                    },
                    error ->  _restCallback.onRestResult(false)
            );

            //envia requisicao
            addToRequestQueue(jsonRequest);
        }
        catch (Exception ex){
            Log.d("Error", "Erro ao enviar noticia: " + ex.getLocalizedMessage());
        }
    }

    public void EnviarVideo(String descricao, String link, RestCallback restCallback) {
        try {
            //seta retorno
            _restCallback = restCallback;

            //monta url requisicao
            String url = "videos";

            //monta headers adicionais
            Map headers = new ArrayMap();

            //monta body
            JSONObject postBody = new JSONObject();
            postBody.put("descricao", descricao);
            postBody.put("link", link);
            postBody.put("idGaleria", getFamiliaAtual().getIdGaleria());

            //monta requisicao
            JsonRestRequest jsonRequest = new JsonRestRequest(((Activity)mCtx).getApplication(), Request.Method.POST, true, url, headers, postBody,
                    new Response.Listener<JsonRestRequest.JsonRestResponse>() {
                        @Override
                        public void onResponse(JsonRestRequest.JsonRestResponse jsonRestResponse) {
                            if (jsonRestResponse.get_httpStatusCode() == 201) //created
                            {
                                _restCallback.onRestResult(true);
                            }
                            else //erros
                            {
                                _restCallback.onRestResult(false);
                            }
                        }
                    },
                    error ->  _restCallback.onRestResult(false)
            );

            //envia requisicao
            addToRequestQueue(jsonRequest);
        }
        catch (Exception ex){
            Log.d("Error", "Erro ao enviar video: " + ex.getLocalizedMessage());
        }
    }

    public void EnviarAlbum(String nome, String descricao, RestCallback restCallback) {
        try {
            //seta retorno
            _restCallback = restCallback;

            //monta url requisicao
            String url = "albuns";

            //monta headers adicionais
            Map headers = new ArrayMap();

            //monta body
            JSONObject postBody = new JSONObject();
            postBody.put("nome", nome);
            postBody.put("descricao", descricao);
            postBody.put("idFamilia", getFamiliaAtual().getIdFamilia());

            //monta requisicao
            JsonRestRequest jsonRequest = new JsonRestRequest(((Activity)mCtx).getApplication(), Request.Method.POST, true, url, headers, postBody,
                    new Response.Listener<JsonRestRequest.JsonRestResponse>() {
                        @Override
                        public void onResponse(JsonRestRequest.JsonRestResponse jsonRestResponse) {
                            if (jsonRestResponse.get_httpStatusCode() == 201) //created
                            {
                                _restCallback.onRestResult(true);
                            }
                            else //erros
                            {
                                _restCallback.onRestResult(false);
                            }
                        }
                    },
                    error ->  _restCallback.onRestResult(false)
            );

            //envia requisicao
            addToRequestQueue(jsonRequest);
        }
        catch (Exception ex){
            Log.d("Error", "Erro ao enviar album: " + ex.getLocalizedMessage());
        }
    }

    public void EnviarEvento(String nomeEvento, String descricaoEvento, String localEvento, int idTipoEvento, ArrayList<Item> itensAdicionados, RestCallback restCallback) {
        try {
            //seta retorno
            _restCallback = restCallback;

            //monta url requisicao
            String url = "eventos";

            //monta headers adicionais
            Map headers = new ArrayMap();

            //monta body
            JSONObject postBody = new JSONObject();
            postBody.put("nome", nomeEvento);
            postBody.put("descricao", descricaoEvento);
            postBody.put("local", localEvento);
            postBody.put("idFamilia", getFamiliaAtual().getIdFamilia());
            postBody.put("idTipoEvento", idTipoEvento);
            postBody.put("idUsuario", getUsuarioAtual().getIdUsuario());

            //monta requisicao
            JsonRestRequest jsonRequest = new JsonRestRequest(((Activity)mCtx).getApplication(), Request.Method.POST, true, url, headers, postBody,
                    new Response.Listener<JsonRestRequest.JsonRestResponse>() {
                        @Override
                        public void onResponse(JsonRestRequest.JsonRestResponse jsonRestResponse) {
                            if (jsonRestResponse.get_httpStatusCode() == 201) //created
                            {
                                //pegando id do evento criado
                                String localizacaoRecurso = jsonRestResponse.get_headers().get("Location").toString();
                                int idRecursoCriado = Integer.parseInt(localizacaoRecurso.substring(localizacaoRecurso.lastIndexOf('/') + 1));

                                contadorSincronizacao = itensAdicionados.size();
                                for (int j = 0 ; j < itensAdicionados.size() ; j++) {
                                    Item item = itensAdicionados.get(j);
                                    EnviarItem(item.getQuantidade(), item.getIdTipoItem(),idRecursoCriado);
                                }
                            }
                            else //erros
                            {
                                _restCallback.onRestResult(false);
                            }
                        }
                    },
                    error ->  _restCallback.onRestResult(false)
            );

            //envia requisicao
            addToRequestQueue(jsonRequest);
        }
        catch (Exception ex){
            Log.d("Error", "Erro ao enviar evento: " + ex.getLocalizedMessage());
        }
    }

    public void EnviarItem(int quantidade, int idTipoItem, int idEvento) {
        try {

            //monta url requisicao
            String url = "itens";

            //monta headers adicionais
            Map headers = new ArrayMap();

            //monta body
            JSONObject postBody = new JSONObject();
            postBody.put("quantidade", quantidade);
            postBody.put("idTipoItem", idTipoItem);
            postBody.put("idEvento", idEvento);
            postBody.put("idUsuario", getUsuarioAtual().getIdUsuario());

            //monta requisicao
            JsonRestRequest jsonRequest = new JsonRestRequest(((Activity)mCtx).getApplication(), Request.Method.POST, true, url, headers, postBody,
                    new Response.Listener<JsonRestRequest.JsonRestResponse>() {
                        @Override
                        public void onResponse(JsonRestRequest.JsonRestResponse jsonRestResponse) {
                            if (jsonRestResponse.get_httpStatusCode() == 201) //created
                            {
                                contadorSincronizacao--;

                                //se todos os requests foram executados
                                if (contadorSincronizacao == 0)
                                {
                                    _restCallback.onRestResult(true);
                                }
                            }
                            else //erros
                            {
                                _restCallback.onRestResult(false);
                            }
                        }
                    },
                    error ->  _restCallback.onRestResult(false)
            );

            //envia requisicao
            addToRequestQueue(jsonRequest);
        }
        catch (Exception ex){
            Log.d("Error", "Erro ao enviar item: " + ex.getLocalizedMessage());
        }
    }

    public void ReceberUsuario(String emailUsuario, RestObjectReceiveCallback restObjectReceiveCallback) {
        try {

            //monta url requisicao
            String url = "usuariosporemail/" + emailUsuario;

            //monta headers adicionais
            Map headers = new ArrayMap();

            //monta body
            JSONObject postBody = new JSONObject();

            //monta requisicao
            JsonRestRequest jsonRequest = new JsonRestRequest((((Activity)mCtx).getApplication()), Request.Method.GET, true, url, headers, postBody,
                    new Response.Listener<JsonRestRequest.JsonRestResponse>() {
                        @Override
                        public void onResponse(JsonRestRequest.JsonRestResponse jsonRestResponse) {
                            if (jsonRestResponse.get_httpStatusCode() == 200) //ok
                            {
                                JSONObject usuarioJson = jsonRestResponse.get_bodyResponse();
                                if (usuarioJson != null)
                                {
                                    //criando usuario
                                    Usuario usuario = null;
                                    try {

                                        usuario = new Usuario(usuarioJson.getInt("idUsuario"), usuarioJson.getString("nome"), usuarioJson.getString("email"));
                                        restObjectReceiveCallback.onRestResult(usuario);

                                    } catch (JSONException e) {
                                        restObjectReceiveCallback.onRestResult(null);
                                    }
                                }
                                else
                                {
                                    restObjectReceiveCallback.onRestResult(null);
                                }
                            }
                            else //erros
                            {
                                restObjectReceiveCallback.onRestResult(null);
                            }
                        }
                    },
                    error -> restObjectReceiveCallback.onRestResult(null)
            );

            //envia requisicao
            addToRequestQueue(jsonRequest);
        }
        catch (Exception ex){
            Log.d("Error", "Erro ao requisitar usuario por email: " + ex.getLocalizedMessage());
        }
    }

    public void RelacionarUsuarioFamilia(Usuario usuario, Familia familiaAtual, RestCallback restCallback) {
        try {

            //seta retorno
            _restCallback = restCallback;

            //monta url requisicao
            String url = "familias/" + familiaAtual.getIdFamilia() + "/usuarios";

            //monta headers adicionais
            Map headers = new ArrayMap();

            //monta body
            JSONObject postBody = new JSONObject();
            postBody.put("idUsuario", usuario.getIdUsuario());

            //monta requisicao
            JsonRestRequest jsonRequest = new JsonRestRequest(((Activity)mCtx).getApplication(), Request.Method.POST, true, url, headers, postBody,
                    new Response.Listener<JsonRestRequest.JsonRestResponse>() {
                        @Override
                        public void onResponse(JsonRestRequest.JsonRestResponse jsonRestResponse) {
                            if (jsonRestResponse.get_httpStatusCode() == 201) //created
                            {
                                _restCallback.onRestResult(true);
                            }
                            else //erros
                            {
                                _restCallback.onRestResult(false);
                            }
                        }
                    },
                    error ->  _restCallback.onRestResult(false)
            );

            //envia requisicao
            addToRequestQueue(jsonRequest);
        }
        catch (Exception ex){
            Log.d("Error", "Erro ao relacionar usuario com familia: " + ex.getLocalizedMessage());
        }
    }

    public void EnviarEmailSenha(String emailUsuario, RestCallback restCallback) {
        try {
            //seta retorno
            _restCallback = restCallback;

            //monta url requisicao
            String url = "usuariosporemail";

            //monta headers adicionais
            Map headers = new ArrayMap();

            //monta body
            JSONObject postBody = new JSONObject();
            postBody.put("email", emailUsuario);

            //monta requisicao
            JsonRestRequest jsonRequest = new JsonRestRequest(((Activity)mCtx).getApplication(), Request.Method.POST, true, url, headers, postBody,
                    new Response.Listener<JsonRestRequest.JsonRestResponse>() {
                        @Override
                        public void onResponse(JsonRestRequest.JsonRestResponse jsonRestResponse) {
                            if (jsonRestResponse.get_httpStatusCode() == 201) //created
                            {
                                _restCallback.onRestResult(true);
                            }
                            else //erros
                            {
                                _restCallback.onRestResult(false);
                            }
                        }
                    },
                    error ->  _restCallback.onRestResult(false)
            );
            //requisicao de email demora mais
            jsonRequest.setRetryPolicy(new DefaultRetryPolicy(
                    60000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            //envia requisicao
            addToRequestQueue(jsonRequest);
        }
        catch (Exception ex){
            Log.d("Error", "Erro ao enviar email de senha: " + ex.getLocalizedMessage());
        }
    }

    public void EnviarComentario(String descricao, Noticia _noticia, RestCallback restCallback) {
        try {
            //seta retorno
            _restCallback = restCallback;

            //monta url requisicao
            String url = "comentarios";

            //monta headers adicionais
            Map headers = new ArrayMap();

            //monta body
            JSONObject postBody = new JSONObject();
            postBody.put("descricao", descricao);
            postBody.put("idNoticia", _noticia.getIdNoticia());
            postBody.put("idUsuario", getUsuarioAtual().getIdUsuario());

            //monta requisicao
            JsonRestRequest jsonRequest = new JsonRestRequest(((Activity)mCtx).getApplication(), Request.Method.POST, true, url, headers, postBody,
                    new Response.Listener<JsonRestRequest.JsonRestResponse>() {
                        @Override
                        public void onResponse(JsonRestRequest.JsonRestResponse jsonRestResponse) {
                            if (jsonRestResponse.get_httpStatusCode() == 201) //created
                            {
                                _restCallback.onRestResult(true);
                            }
                            else //erros
                            {
                                _restCallback.onRestResult(false);
                            }
                        }
                    },
                    error ->  _restCallback.onRestResult(false)
            );

            //envia requisicao
            addToRequestQueue(jsonRequest);
        }
        catch (Exception ex){
            Log.d("Error", "Erro ao enviar comentario: " + ex.getLocalizedMessage());
        }

    }

    public void EnviarComentarioEvento(String descricao, Evento evento, RestCallback restCallback) {
        try {
            //seta retorno
            _restCallback = restCallback;

            //monta url requisicao
            String url = "comentarios";

            //monta headers adicionais
            Map headers = new ArrayMap();

            //monta body
            JSONObject postBody = new JSONObject();
            postBody.put("descricao", descricao);
            postBody.put("idEvento", evento.getIdEvento());
            postBody.put("idUsuario", getUsuarioAtual().getIdUsuario());

            //monta requisicao
            JsonRestRequest jsonRequest = new JsonRestRequest(((Activity)mCtx).getApplication(), Request.Method.POST, true, url, headers, postBody,
                    new Response.Listener<JsonRestRequest.JsonRestResponse>() {
                        @Override
                        public void onResponse(JsonRestRequest.JsonRestResponse jsonRestResponse) {
                            if (jsonRestResponse.get_httpStatusCode() == 201) //created
                            {
                                _restCallback.onRestResult(true);
                            }
                            else //erros
                            {
                                _restCallback.onRestResult(false);
                            }
                        }
                    },
                    error ->  _restCallback.onRestResult(false)
            );

            //envia requisicao
            addToRequestQueue(jsonRequest);
        }
        catch (Exception ex){
            Log.d("Error", "Erro ao enviar comentario: " + ex.getLocalizedMessage());
        }
    }

    public void EnviarFoto(String descricao, Bitmap bitmapImage, Album album, RestCallback restCallback) {
        try {
            //seta retorno
            _restCallback = restCallback;

            //monta url requisicao
            String url = "fotos";

            //monta headers adicionais
            Map headers = new ArrayMap();

            //monta body
            JSONObject postBody = new JSONObject();
            postBody.put("descricao", descricao);
            postBody.put("dados", encodeBitmap(bitmapImage));
            postBody.put("idAlbum", album.getIdAlbum());

            //monta requisicao
            JsonRestRequest jsonRequest = new JsonRestRequest(((Activity)mCtx).getApplication(), Request.Method.POST, true, url, headers, postBody,
                    new Response.Listener<JsonRestRequest.JsonRestResponse>() {
                        @Override
                        public void onResponse(JsonRestRequest.JsonRestResponse jsonRestResponse) {
                            if (jsonRestResponse.get_httpStatusCode() == 201) //created
                            {
                                _restCallback.onRestResult(true);
                            }
                            else //erros
                            {
                                _restCallback.onRestResult(false);
                            }
                        }
                    },
                    error ->  _restCallback.onRestResult(false)
            );

            //envia requisicao
            addToRequestQueue(jsonRequest);
        }
        catch (Exception ex){
            Log.d("Error", "Erro ao enviar foto: " + ex.getLocalizedMessage());
        }
    }

    private String encodeBitmap(Bitmap bitmap){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream .toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    public void EditarAlbum(String nome, String descricao, int idAlbum, RestCallback restCallback) {
        try {
            //seta retorno
            _restCallback = restCallback;

            //monta url requisicao
            String url = "albuns/" + idAlbum;

            //monta headers adicionais
            Map headers = new ArrayMap();

            //monta body
            JSONObject postBody = new JSONObject();
            postBody.put("nome", nome);
            postBody.put("descricao", descricao);

            //monta requisicao
            JsonRestRequest jsonRequest = new JsonRestRequest(((Activity)mCtx).getApplication(), Request.Method.PUT, true, url, headers, postBody,
                    new Response.Listener<JsonRestRequest.JsonRestResponse>() {
                        @Override
                        public void onResponse(JsonRestRequest.JsonRestResponse jsonRestResponse) {
                            if (jsonRestResponse.get_httpStatusCode() == 200) //OK
                            {
                                _restCallback.onRestResult(true);
                            }
                            else //erros
                            {
                                _restCallback.onRestResult(false);
                            }
                        }
                    },
                    error ->  _restCallback.onRestResult(false)
            );

            //envia requisicao
            addToRequestQueue(jsonRequest);
        }
        catch (Exception ex){
            Log.d("Error", "Erro ao editar album: " + ex.getLocalizedMessage());
        }
    }

    public void RemoverAlbum(int idAlbum, RestCallback restCallback) {
        try {
            //seta retorno
            _restCallback = restCallback;

            //monta url requisicao
            String url = "albuns/" + idAlbum;

            //monta headers adicionais
            Map headers = new ArrayMap();

            //monta body
            JSONObject postBody = new JSONObject();

            //monta requisicao
            JsonRestRequest jsonRequest = new JsonRestRequest(((Activity)mCtx).getApplication(), Request.Method.DELETE, true, url, headers, postBody,
                    new Response.Listener<JsonRestRequest.JsonRestResponse>() {
                        @Override
                        public void onResponse(JsonRestRequest.JsonRestResponse jsonRestResponse) {
                            if (jsonRestResponse.get_httpStatusCode() == 200) //OK
                            {
                                _restCallback.onRestResult(true);
                            }
                            else //erros
                            {
                                _restCallback.onRestResult(false);
                            }
                        }
                    },
                    error ->  _restCallback.onRestResult(false)
            );

            //envia requisicao
            addToRequestQueue(jsonRequest);
        }
        catch (Exception ex){
            Log.d("Error", "Erro ao excluir album: " + ex.getLocalizedMessage());
        }
    }

    public void EditarNoticia(String descricao, int idNoticia, RestCallback restCallback) {
        try {
            //seta retorno
            _restCallback = restCallback;

            //monta url requisicao
            String url = "noticias/" + idNoticia;

            //monta headers adicionais
            Map headers = new ArrayMap();

            //monta body
            JSONObject postBody = new JSONObject();
            postBody.put("descricao", descricao);

            //monta requisicao
            JsonRestRequest jsonRequest = new JsonRestRequest(((Activity)mCtx).getApplication(), Request.Method.PUT, true, url, headers, postBody,
                    new Response.Listener<JsonRestRequest.JsonRestResponse>() {
                        @Override
                        public void onResponse(JsonRestRequest.JsonRestResponse jsonRestResponse) {
                            if (jsonRestResponse.get_httpStatusCode() == 200) //OK
                            {
                                _restCallback.onRestResult(true);
                            }
                            else //erros
                            {
                                _restCallback.onRestResult(false);
                            }
                        }
                    },
                    error ->  _restCallback.onRestResult(false)
            );

            //envia requisicao
            addToRequestQueue(jsonRequest);
        }
        catch (Exception ex){
            Log.d("Error", "Erro ao editar noticia: " + ex.getLocalizedMessage());
        }
    }

    public void RemoverNoticia(int idNoticia, RestCallback restCallback) {
        try {
            //seta retorno
            _restCallback = restCallback;

            //monta url requisicao
            String url = "noticias/" + idNoticia;

            //monta headers adicionais
            Map headers = new ArrayMap();

            //monta body
            JSONObject postBody = new JSONObject();

            //monta requisicao
            JsonRestRequest jsonRequest = new JsonRestRequest(((Activity)mCtx).getApplication(), Request.Method.DELETE, true, url, headers, postBody,
                    new Response.Listener<JsonRestRequest.JsonRestResponse>() {
                        @Override
                        public void onResponse(JsonRestRequest.JsonRestResponse jsonRestResponse) {
                            if (jsonRestResponse.get_httpStatusCode() == 200) //OK
                            {
                                _restCallback.onRestResult(true);
                            }
                            else //erros
                            {
                                _restCallback.onRestResult(false);
                            }
                        }
                    },
                    error ->  _restCallback.onRestResult(false)
            );

            //envia requisicao
            addToRequestQueue(jsonRequest);
        }
        catch (Exception ex){
            Log.d("Error", "Erro ao excluir noticia: " + ex.getLocalizedMessage());
        }
    }

    public void EditarVideo(String descricao, String link, int idVideo, RestCallback restCallback) {
        try {
            //seta retorno
            _restCallback = restCallback;

            //monta url requisicao
            String url = "videos/" + idVideo;

            //monta headers adicionais
            Map headers = new ArrayMap();

            //monta body
            JSONObject postBody = new JSONObject();
            postBody.put("descricao", descricao);
            postBody.put("link", link);

            //monta requisicao
            JsonRestRequest jsonRequest = new JsonRestRequest(((Activity)mCtx).getApplication(), Request.Method.PUT, true, url, headers, postBody,
                    new Response.Listener<JsonRestRequest.JsonRestResponse>() {
                        @Override
                        public void onResponse(JsonRestRequest.JsonRestResponse jsonRestResponse) {
                            if (jsonRestResponse.get_httpStatusCode() == 200) //OK
                            {
                                _restCallback.onRestResult(true);
                            }
                            else //erros
                            {
                                _restCallback.onRestResult(false);
                            }
                        }
                    },
                    error ->  _restCallback.onRestResult(false)
            );

            //envia requisicao
            addToRequestQueue(jsonRequest);
        }
        catch (Exception ex){
            Log.d("Error", "Erro ao editar video: " + ex.getLocalizedMessage());
        }
    }

    public void RemoverVideo(int idVideo, RestCallback restCallback) {
        try {
            //seta retorno
            _restCallback = restCallback;

            //monta url requisicao
            String url = "videos/" + idVideo;

            //monta headers adicionais
            Map headers = new ArrayMap();

            //monta body
            JSONObject postBody = new JSONObject();

            //monta requisicao
            JsonRestRequest jsonRequest = new JsonRestRequest(((Activity)mCtx).getApplication(), Request.Method.DELETE, true, url, headers, postBody,
                    new Response.Listener<JsonRestRequest.JsonRestResponse>() {
                        @Override
                        public void onResponse(JsonRestRequest.JsonRestResponse jsonRestResponse) {
                            if (jsonRestResponse.get_httpStatusCode() == 200) //OK
                            {
                                _restCallback.onRestResult(true);
                            }
                            else //erros
                            {
                                _restCallback.onRestResult(false);
                            }
                        }
                    },
                    error ->  _restCallback.onRestResult(false)
            );

            //envia requisicao
            addToRequestQueue(jsonRequest);
        }
        catch (Exception ex){
            Log.d("Error", "Erro ao excluir video: " + ex.getLocalizedMessage());
        }
    }

    public void RemoverMembro(int idUsuario, RestCallback restCallback) {
        try {
            //seta retorno
            _restCallback = restCallback;

            //monta url requisicao
            String url = "familias/" + getFamiliaAtual().getIdFamilia() + "/usuarios/" + idUsuario;

            //monta headers adicionais
            Map headers = new ArrayMap();

            //monta body
            JSONObject postBody = new JSONObject();

            //monta requisicao
            JsonRestRequest jsonRequest = new JsonRestRequest(((Activity)mCtx).getApplication(), Request.Method.DELETE, true, url, headers, postBody,
                    new Response.Listener<JsonRestRequest.JsonRestResponse>() {
                        @Override
                        public void onResponse(JsonRestRequest.JsonRestResponse jsonRestResponse) {
                            if (jsonRestResponse.get_httpStatusCode() == 200) //OK
                            {
                                _restCallback.onRestResult(true);
                            }
                            else //erros
                            {
                                _restCallback.onRestResult(false);
                            }
                        }
                    },
                    error ->  _restCallback.onRestResult(false)
            );

            //envia requisicao
            addToRequestQueue(jsonRequest);
        }
        catch (Exception ex){
            Log.d("Error", "Erro ao excluir membro da familia: " + ex.getLocalizedMessage());
        }
    }

    public void RemoverEvento(int idEvento, RestCallback restCallback) {
        try {
            //seta retorno
            _restCallback = restCallback;

            //monta url requisicao
            String url = "eventos/" + idEvento;

            //monta headers adicionais
            Map headers = new ArrayMap();

            //monta body
            JSONObject postBody = new JSONObject();

            //monta requisicao
            JsonRestRequest jsonRequest = new JsonRestRequest(((Activity)mCtx).getApplication(), Request.Method.DELETE, true, url, headers, postBody,
                    new Response.Listener<JsonRestRequest.JsonRestResponse>() {
                        @Override
                        public void onResponse(JsonRestRequest.JsonRestResponse jsonRestResponse) {
                            if (jsonRestResponse.get_httpStatusCode() == 200) //OK
                            {
                                _restCallback.onRestResult(true);
                            }
                            else //erros
                            {
                                _restCallback.onRestResult(false);
                            }
                        }
                    },
                    error ->  _restCallback.onRestResult(false)
            );

            //envia requisicao
            addToRequestQueue(jsonRequest);
        }
        catch (Exception ex){
            Log.d("Error", "Erro ao excluir evento: " + ex.getLocalizedMessage());
        }
    }

    public void EditarEvento(String nome, String descricao, String local, int idTipoEvento, int idEvento, RestCallback restCallback) {
        try {
            //seta retorno
            _restCallback = restCallback;

            //monta url requisicao
            String url = "eventos/" + idEvento;

            //monta headers adicionais
            Map headers = new ArrayMap();

            //monta body
            JSONObject postBody = new JSONObject();
            postBody.put("descricao", descricao);
            postBody.put("nome", nome);
            postBody.put("descricao", descricao);
            postBody.put("local", local);
            postBody.put("idTipoEvento", idTipoEvento);

            //monta requisicao
            JsonRestRequest jsonRequest = new JsonRestRequest(((Activity)mCtx).getApplication(), Request.Method.PUT, true, url, headers, postBody,
                    new Response.Listener<JsonRestRequest.JsonRestResponse>() {
                        @Override
                        public void onResponse(JsonRestRequest.JsonRestResponse jsonRestResponse) {
                            if (jsonRestResponse.get_httpStatusCode() == 200) //OK
                            {
                                _restCallback.onRestResult(true);
                            }
                            else //erros
                            {
                                _restCallback.onRestResult(false);
                            }
                        }
                    },
                    error ->  _restCallback.onRestResult(false)
            );

            //envia requisicao
            addToRequestQueue(jsonRequest);
        }
        catch (Exception ex){
            Log.d("Error", "Erro ao editar evento: " + ex.getLocalizedMessage());
        }
    }

    public void RemoverUsuario(int idUsuario, RestCallback restCallback) {
        try {
            //seta retorno
            _restCallback = restCallback;

            //monta url requisicao
            String url = "usuarios/" + idUsuario;

            //monta headers adicionais
            Map headers = new ArrayMap();

            //monta body
            JSONObject postBody = new JSONObject();

            //monta requisicao
            JsonRestRequest jsonRequest = new JsonRestRequest(((Activity)mCtx).getApplication(), Request.Method.DELETE, true, url, headers, postBody,
                    new Response.Listener<JsonRestRequest.JsonRestResponse>() {
                        @Override
                        public void onResponse(JsonRestRequest.JsonRestResponse jsonRestResponse) {
                            if (jsonRestResponse.get_httpStatusCode() == 200) //OK
                            {
                                _restCallback.onRestResult(true);
                            }
                            else //erros
                            {
                                _restCallback.onRestResult(false);
                            }
                        }
                    },
                    error ->  _restCallback.onRestResult(false)
            );

            //envia requisicao
            addToRequestQueue(jsonRequest);
        }
        catch (Exception ex){
            Log.d("Error", "Erro ao excluir usuario: " + ex.getLocalizedMessage());
        }
    }

    public void EditarUsuario(String nome, int idUsuario, RestCallback restCallback) {
        try {
            //seta retorno
            _restCallback = restCallback;

            //monta url requisicao
            String url = "usuarios/" + idUsuario;

            //monta headers adicionais
            Map headers = new ArrayMap();

            //monta body
            JSONObject postBody = new JSONObject();
            postBody.put("nome", nome);

            //monta requisicao
            JsonRestRequest jsonRequest = new JsonRestRequest(((Activity)mCtx).getApplication(), Request.Method.PUT, true, url, headers, postBody,
                    new Response.Listener<JsonRestRequest.JsonRestResponse>() {
                        @Override
                        public void onResponse(JsonRestRequest.JsonRestResponse jsonRestResponse) {
                            if (jsonRestResponse.get_httpStatusCode() == 200) //OK
                            {
                                _restCallback.onRestResult(true);
                            }
                            else //erros
                            {
                                _restCallback.onRestResult(false);
                            }
                        }
                    },
                    error ->  _restCallback.onRestResult(false)
            );

            //envia requisicao
            addToRequestQueue(jsonRequest);
        }
        catch (Exception ex){
            Log.d("Error", "Erro ao editar usuario: " + ex.getLocalizedMessage());
        }
    }

    public void EnviarFamilia(String nome, String descricao, String local, RestCallback restCallback) {
        try {
            //seta retorno
            _restCallback = restCallback;

            //monta url requisicao
            String url = "familias";

            //monta headers adicionais
            Map headers = new ArrayMap();

            //monta body
            JSONObject postBody = new JSONObject();
            postBody.put("nome", nome);
            postBody.put("descricao", descricao);
            postBody.put("local", local);

            //monta requisicao
            JsonRestRequest jsonRequest = new JsonRestRequest(((Activity)mCtx).getApplication(), Request.Method.POST, true, url, headers, postBody,
                    new Response.Listener<JsonRestRequest.JsonRestResponse>() {
                        @Override
                        public void onResponse(JsonRestRequest.JsonRestResponse jsonRestResponse) {
                            if (jsonRestResponse.get_httpStatusCode() == 201) //created
                            {
                                _restCallback.onRestResult(true);
                            }
                            else //erros
                            {
                                _restCallback.onRestResult(false);
                            }
                        }
                    },
                    error ->  _restCallback.onRestResult(false)
            );

            //envia requisicao
            addToRequestQueue(jsonRequest);
        }
        catch (Exception ex){
            Log.d("Error", "Erro ao enviar familia: " + ex.getLocalizedMessage());
        }
    }

    public void EditarFamilia(String nome, String descricao, String local, int idFamilia, RestCallback restCallback) {
        try {
            //seta retorno
            _restCallback = restCallback;

            //monta url requisicao
            String url = "familias/" + idFamilia;

            //monta headers adicionais
            Map headers = new ArrayMap();

            //monta body
            JSONObject postBody = new JSONObject();
            postBody.put("nome", nome);
            postBody.put("descricao", descricao);
            postBody.put("local", local);

            //monta requisicao
            JsonRestRequest jsonRequest = new JsonRestRequest(((Activity)mCtx).getApplication(), Request.Method.PUT, true, url, headers, postBody,
                    new Response.Listener<JsonRestRequest.JsonRestResponse>() {
                        @Override
                        public void onResponse(JsonRestRequest.JsonRestResponse jsonRestResponse) {
                            if (jsonRestResponse.get_httpStatusCode() == 200) //OK
                            {
                                _restCallback.onRestResult(true);
                            }
                            else //erros
                            {
                                _restCallback.onRestResult(false);
                            }
                        }
                    },
                    error ->  _restCallback.onRestResult(false)
            );

            //envia requisicao
            addToRequestQueue(jsonRequest);
        }
        catch (Exception ex){
            Log.d("Error", "Erro ao editar familia: " + ex.getLocalizedMessage());
        }
    }

    public void RemoverFamilia(int idFamilia, RestCallback restCallback) {
        try {
            //seta retorno
            _restCallback = restCallback;

            //monta url requisicao
            String url = "familias/" + idFamilia;

            //monta headers adicionais
            Map headers = new ArrayMap();

            //monta body
            JSONObject postBody = new JSONObject();

            //monta requisicao
            JsonRestRequest jsonRequest = new JsonRestRequest(((Activity)mCtx).getApplication(), Request.Method.DELETE, true, url, headers, postBody,
                    new Response.Listener<JsonRestRequest.JsonRestResponse>() {
                        @Override
                        public void onResponse(JsonRestRequest.JsonRestResponse jsonRestResponse) {
                            if (jsonRestResponse.get_httpStatusCode() == 200) //OK
                            {
                                _restCallback.onRestResult(true);
                            }
                            else //erros
                            {
                                _restCallback.onRestResult(false);
                            }
                        }
                    },
                    error ->  _restCallback.onRestResult(false)
            );

            //envia requisicao
            addToRequestQueue(jsonRequest);
        }
        catch (Exception ex){
            Log.d("Error", "Erro ao excluir familia: " + ex.getLocalizedMessage());
        }
    }

}

