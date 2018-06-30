package voluta.familyst.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import voluta.familyst.Adapters.FamiliaAdapter;
import voluta.familyst.FamilystApplication;
import voluta.familyst.Fragments.AlbumsFragment;
import voluta.familyst.Fragments.EventosFragment;
import voluta.familyst.Fragments.GaleriaFragment;
import voluta.familyst.Fragments.MembrosFragment;
import voluta.familyst.Fragments.NewsFragment;
import voluta.familyst.Model.Familia;
import voluta.familyst.Model.Usuario;
import voluta.familyst.R;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    NavigationView navigationView = null;
    Usuario _usuarioLogado;
    String _accessToken;
    Toolbar toolbar = null;
    Menu _menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        _usuarioLogado = ((FamilystApplication)getApplication()).get_usuarioLogado();
        _accessToken = ((FamilystApplication)getApplication()).get_accessToken();

        NewsFragment fragment = new NewsFragment();
        FragmentTransaction fragmentTransaction =
                getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);

        RelativeLayout header = (RelativeLayout) headerView.findViewById(R.id.relative_header);
        header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), PerfilActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();

        if (((FamilystApplication)getApplication()).getLogout()) {
            ((FamilystApplication)getApplication()).setLogout(false);
            sair();
        }
        else{
            CarregarPerfilUser();
            AtualizarMenuFamilias();
        }
    }

    private void CarregarPerfilUser() {
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) drawerLayout.findViewById(R.id.nav_view);
        TextView txtFamilia = (TextView) navigationView.getHeaderView(0).findViewById(R.id.familyname);
        TextView txtUser = (TextView) navigationView.getHeaderView(0).findViewById(R.id.username);

        FamilystApplication familystApplication = (FamilystApplication)getApplication();
        String user = familystApplication.get_usuarioLogado().getNome();
        String familia = familystApplication.getFamiliaAtual().getNome();

        txtFamilia.setText(familia);
        txtUser.setText(user);

    }

    private void AtualizarMenuFamilias() {
        try {
            ArrayList<Familia> familias = ((FamilystApplication) getApplication()).get_usuarioLogado().getFamilias();
            if (familias.isEmpty()) {
                Familia familiaNenhuma = new Familia(-1, "Nenhuma Familia", -1, "", "");
                ArrayList<Familia> familiasVazio = new ArrayList<>();
                familiasVazio.add(familiaNenhuma);
                FamiliaAdapter adapter = new FamiliaAdapter(this,
                        R.layout.item_lista_familias, familiasVazio);
                MenuItem item = _menu.findItem(R.id.spinner);
                Spinner spinner = (Spinner) MenuItemCompat.getActionView(item);
                spinner.setAdapter(adapter); // set the adapter to provide layout of rows and content
            } else {
                FamiliaAdapter adapter = new FamiliaAdapter(this,
                        R.layout.item_lista_familias, familias);

                MenuItem item = _menu.findItem(R.id.spinner);
                Spinner spinner = (Spinner) MenuItemCompat.getActionView(item);
                spinner.setAdapter(adapter); // set the adapter to provide layout of rows and content
                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        Familia familiaSelecionada = (Familia) parent.getItemAtPosition(position);
                        ((FamilystApplication) getApplication()).setIdFamiliaSelecionada(familiaSelecionada.getIdFamilia());
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });
            }
        }
        catch(Exception ex)
        {
            //ignore
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        _menu = menu;

        AtualizarMenuFamilias();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_events) {
            EventosFragment fragment = new EventosFragment();
            FragmentTransaction fragmentTransaction =
                    getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, fragment);
            fragmentTransaction.commit();
        }
        else if (id == R.id.nav_members) {
            MembrosFragment fragment = new MembrosFragment();
            FragmentTransaction fragmentTransaction =
                    getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, fragment);
            fragmentTransaction.commit();
        }else if (id == R.id.nav_albums) {
            AlbumsFragment fragment = new AlbumsFragment();
            FragmentTransaction fragmentTransaction =
                    getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, fragment);
            fragmentTransaction.commit();
        } else if (id == R.id.nav_gallery) {
            GaleriaFragment fragment = new GaleriaFragment();
            FragmentTransaction fragmentTransaction =
                    getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, fragment);
            fragmentTransaction.commit();

        } else if (id == R.id.nav_news) {
            NewsFragment fragment = new NewsFragment();
            FragmentTransaction fragmentTransaction =
                    getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, fragment);
            fragmentTransaction.commit();

        } else if (id == R.id.nav_logout) {
            sair();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void sair() {
        ((FamilystApplication)getApplication()).setLoginAutomatico(false);
        ((FamilystApplication)getApplication()).clearData();
        Intent intent = new Intent();
        intent.setClass(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
