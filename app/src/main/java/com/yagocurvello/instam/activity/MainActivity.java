package com.yagocurvello.instam.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.yagocurvello.instam.R;
import com.yagocurvello.instam.fragment.FeedFragment;
import com.yagocurvello.instam.fragment.PerfilFragment;
import com.yagocurvello.instam.fragment.PesquisaFragment;
import com.yagocurvello.instam.fragment.PostarFragment;
import com.yagocurvello.instam.helper.ConfigFirebase;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private BottomNavigationViewEx bottomNavigation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle("Insta");
        setSupportActionBar(toolbar);

        configIniciais ();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.menuConfig :
                startActivity(new Intent(MainActivity.this, ConfigActivity.class));
                break;

            case R.id.menuSair :
                firebaseAuth.signOut();
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                finish();
                break;

        }

        return super.onOptionsItemSelected(item);
    }

    public void configIniciais (){
        firebaseAuth = ConfigFirebase.getFirebaseAutenticacao();

        ConfigBottomNavigation ();

        //inicia com o Fragment de Feed
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.viewPage, new FeedFragment()).commit();
    }

    public void ConfigBottomNavigation (){
        bottomNavigation = findViewById(R.id.bottomNavigation);

        //configurações de interface
        bottomNavigation.enableAnimation(false);
        bottomNavigation.enableShiftingMode(false);
        bottomNavigation.enableItemShiftingMode(false);
        bottomNavigation.setTextVisibility(false);

        //Habilitar navegação
        habilitarNavegacao (bottomNavigation);

        //Configura o item selecionado inicialmente
        Menu menu = bottomNavigation.getMenu();
        MenuItem menuItem = menu.getItem(0);
        menuItem.setChecked(true);
    }

    //Metodo para tratar eventos de click no bottom navigation
    private void habilitarNavegacao (BottomNavigationViewEx viewEx){
        viewEx.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                switch (menuItem.getItemId()){

                    case R.id.menuHome:
                        fragmentTransaction.replace(R.id.viewPage, new FeedFragment()).commit();
                        return true;

                    case R.id.menuPesquisa:
                        fragmentTransaction.replace(R.id.viewPage, new PesquisaFragment()).commit();
                        return true;

                    case R.id.menuPostar:
                        fragmentTransaction.replace(R.id.viewPage, new PostarFragment()).commit();
                        return true;
                    case R.id.menuPerfil:

                        fragmentTransaction.replace(R.id.viewPage, new PerfilFragment()).commit();
                        return true;
                }
                return false;
            }
        });
    }
}