package voluta.familyst.Activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import voluta.familyst.R;

public class CadastroGaleriaActivity extends BaseActivity {

    private EditText nomeGaleria;
    private EditText descricaoGaleria;
    private Button btnCadastrarGaleria;
    private ImageButton btnRemoverGaleria;
    private boolean isEdicao;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_galeria);


        nomeGaleria = (EditText) findViewById(R.id.txt_nome_galeria_cadastro);
        descricaoGaleria = (EditText) findViewById(R.id.txt_descricao_galeria_cadastro);
        btnCadastrarGaleria = (Button) findViewById(R.id.btn_cadastrar_galeria);
        btnRemoverGaleria = (ImageButton) findViewById(R.id.btn_remover_galeria);
        btnRemoverGaleria.setVisibility(View.GONE);

        if(isEdicao)
        {
            btnRemoverGaleria.setVisibility(View.VISIBLE);
            //TODO procedimentos de edicao de galeria
        }
        btnRemoverGaleria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO procedimentos de remoção de galeria
            }
        });

        btnCadastrarGaleria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Enviar galeria cadastrada
                //fechar essa activity
            }
        });
    }
}
