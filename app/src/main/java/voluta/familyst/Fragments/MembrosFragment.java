package voluta.familyst.Fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import voluta.familyst.Activities.CadastroMembroActivity;
import voluta.familyst.Adapters.UsuarioAdapter;
import voluta.familyst.FamilystApplication;
import voluta.familyst.Interfaces.RestCallback;
import voluta.familyst.Model.Usuario;
import voluta.familyst.R;
import voluta.familyst.Services.RestService;

import java.util.ArrayList;

public class MembrosFragment extends Fragment {

    ListView listViewMembros;

    public MembrosFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_membros, container, false);
        listViewMembros= (ListView) rootView.findViewById(R.id.listview_membros);

        FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        fab.show();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Chama cadastro de Membros", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                Intent intent = new Intent(getContext(), CadastroMembroActivity.class);
                startActivity(intent);
            }
        });

        return rootView;
    }

    private void carregarListaMembros(){
        UsuarioAdapter adapter = new UsuarioAdapter(getContext(),
                R.layout.item_lista_membros, carregarMembros());

        listViewMembros.setAdapter(adapter);
        listViewMembros.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Abrir tela de profile com os dados do membro
            }
        });
        listViewMembros.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Usuario usuario = (Usuario) parent.getItemAtPosition(position);

                new AlertDialog.Builder(getContext())
                        .setTitle("Alerta!")
                        .setMessage("Deseja Remover o Membro selecionado?")
                        .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                final ProgressDialog dialogProgresso = ProgressDialog.show(getActivity(), "Aguarde", "Excluindo membro.");
                                dialogProgresso.setCancelable(false);
                                RestService.getInstance(getActivity()).RemoverMembro( usuario.getIdUsuario(), new RestCallback(){
                                    @Override
                                    public void onRestResult(boolean success) {
                                        if (success){
                                            onStart();
                                        }
                                        else
                                        {
                                            Toast.makeText(getActivity(),getResources().getText(R.string.falha_remover_membro), Toast.LENGTH_SHORT).show();
                                        }
                                        dialogProgresso.dismiss();
                                    }
                                });

                                dialog.dismiss();
                            }
                        }).show();



                return true;
            }
        });
    }

    private ArrayList<Usuario> carregarMembros()
    {
        FamilystApplication familystApplication = ((FamilystApplication)getActivity().getApplication());
        return familystApplication.getFamiliaAtual().getUsuarios();
    }


    @Override
    public void onStart(){
        super.onStart();

        final ProgressDialog dialogProgresso = ProgressDialog.show(getContext(), "Aguarde", "Atualizando Membros");
        dialogProgresso.setCancelable(false);

        RestService.getInstance(getActivity()).CarregarUsuariosFamiliasAsync(new RestCallback(){
            @Override
            public void onRestResult(boolean success) {
                if (success){
                    Toast.makeText(getActivity(),getResources().getText(R.string.sucesso_atualizar_membros), Toast.LENGTH_SHORT).show();
                    carregarListaMembros();
                }
                else
                {
                    Toast.makeText(getActivity(),getResources().getText(R.string.falha_atualizar_membros), Toast.LENGTH_SHORT).show();
                }
                dialogProgresso.dismiss();
            }
        });
    }

}
