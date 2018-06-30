package voluta.familyst.Activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import voluta.familyst.Adapters.FamiliaPerfilAdapter;
import voluta.familyst.FamilystApplication;
import voluta.familyst.Interfaces.RestCallback;
import voluta.familyst.Model.Familia;
import voluta.familyst.Model.Usuario;
import voluta.familyst.R;
import voluta.familyst.Services.RestService;

import java.util.ArrayList;

public class PerfilActivity extends BaseActivity {

    Button btnEditarPerfil;
    Button btnRemoverPerfil;
    TextView txtEmailPerfil;
    ListView listViewFamiliasPerfil;
    TextView txtNomeUsuarioPerfil;
    Usuario _usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        btnEditarPerfil = (Button) findViewById(R.id.btn_editar_perfil);
        btnRemoverPerfil = (Button) findViewById(R.id.btn_remover_conta);
        txtEmailPerfil = (TextView) findViewById(R.id.txt_email_perfil);
        txtNomeUsuarioPerfil = (TextView) findViewById(R.id.txt_nome_perfil);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_perfil);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CadastroFamiliaActivity.class);
                startActivity(intent);
            }
        });

        btnEditarPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CadastroActivity.class);
                intent.putExtra("idUsuario", _usuario.getIdUsuario());
                intent.putExtra("isEdicao", true);
                startActivity(intent);
            }
        });

        btnRemoverPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(PerfilActivity.this)
                        .setTitle("Alerta!")
                        .setMessage("Deseja excluir sua conta do Familyst?")
                        .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                final ProgressDialog dialogProgresso = ProgressDialog.show(PerfilActivity.this, "Aguarde", "Excluindo conta.");
                                dialogProgresso.setCancelable(false);

                                RestService.getInstance(PerfilActivity.this).RemoverUsuario( _usuario.getIdUsuario(), new RestCallback(){
                                    @Override
                                    public void onRestResult(boolean success) {
                                        if (success){
                                            ((FamilystApplication)getApplication()).setLogout(true);
                                            dialog.dismiss();
                                            dialogProgresso.dismiss();
                                            PerfilActivity.this.finish();
                                        }
                                        else
                                        {
                                            dialogProgresso.dismiss();
                                            Toast.makeText(getApplicationContext(),getResources().getText(R.string.falha_remover_usuario), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        }).show();
            }
        });
    }

    private Usuario carregarUsuario() {
        FamilystApplication familystApplication = (FamilystApplication) getApplication();
        return familystApplication.get_usuarioLogado();
    }

    @Override
    public void onStart(){
        super.onStart();

        _usuario = carregarUsuario();
        txtEmailPerfil.setText(_usuario.getEmail());
        txtNomeUsuarioPerfil.setText(_usuario.getNome());

        // chamar progressdialog
        final ProgressDialog dialogProgresso = ProgressDialog.show(this, "Aguarde", "Atualizando Familias.");
        dialogProgresso.setCancelable(false);
        RestService.getInstance(this).CarregarFamiliasAsync(new RestCallback(){
            @Override
            public void onRestResult(boolean success) {
                if (success){
                    carregarListaFamilias();
                }
                else
                {
                    Toast.makeText(PerfilActivity.this,getResources().getText(R.string.falha_atualizar_familias), Toast.LENGTH_SHORT).show();
                }
                dialogProgresso.dismiss();
            }
        });
    }

    private void carregarListaFamilias() {
        listViewFamiliasPerfil = (ListView) findViewById(R.id.listview_familias_perfil);
        FamiliaPerfilAdapter adapter = new FamiliaPerfilAdapter(this,
                R.layout.item_familia_perfil, carregarFamilias());
        listViewFamiliasPerfil.setAdapter(adapter);
        listViewFamiliasPerfil.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Familia familia = (Familia) parent.getItemAtPosition(position);
                Intent intent = new Intent(getApplicationContext(), CadastroFamiliaActivity.class);
                intent.putExtra("idFamilia", familia.getIdFamilia());
                intent.putExtra("isEdicao", true);
                startActivity(intent);

                return true;
            }
        });
        if (((FamilystApplication)getApplication()).getFamiliaAtual() == null) {
            ArrayList<Familia> familias = ((FamilystApplication) getApplication()).get_usuarioLogado().getFamilias();
            ((FamilystApplication) getApplication()).setIdFamiliaSelecionada(familias.get(0).getIdFamilia());
        }
    }

    private ArrayList<Familia> carregarFamilias() {
        ArrayList<Familia> familias = ((FamilystApplication)getApplication()).get_usuarioLogado().getFamilias();
        if (familias.isEmpty())
        {
            Familia familiaNenhuma = new Familia(-1,"Nenhuma Familia", -1,"","");
            ArrayList<Familia> familiasVazio = new ArrayList<>();
            familiasVazio.add(familiaNenhuma);
            return familiasVazio;
        }
        return familias;
    }

}
