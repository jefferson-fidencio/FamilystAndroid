package voluta.familyst.Activities;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import voluta.familyst.Adapters.ViewPagerAdapter;
import voluta.familyst.Fragments.CadastroEventoFragment;
import voluta.familyst.Fragments.ItensCadastroEventoFragment;
import voluta.familyst.R;

public class TabHostEventosActivity extends AppCompatActivity {

    TabLayout tabLayout;
    ViewPager viewPager;
    ViewPagerAdapter viewPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab_host_eventos);

        int idEvento = getIntent().getIntExtra("idEvento", 0);

        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        viewPager = (ViewPager) findViewById(R.id.viewPager);

        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        CadastroEventoFragment cadastroEventoFragment = new CadastroEventoFragment();
        Bundle data = new Bundle();
        data.putInt("idEvento", idEvento);
        data.putBoolean("isEdicao", true);
        cadastroEventoFragment.setArguments(data);

        viewPagerAdapter.addFragments(cadastroEventoFragment, "Evento");

        ItensCadastroEventoFragment itensCadastroEventoFragment = new ItensCadastroEventoFragment();
        data = new Bundle();
        data.putInt("idEvento", idEvento);
        data.putBoolean("isEdicao", true);
        itensCadastroEventoFragment.setArguments(data);

        viewPagerAdapter.addFragments(itensCadastroEventoFragment, "Itens");

        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }
}
