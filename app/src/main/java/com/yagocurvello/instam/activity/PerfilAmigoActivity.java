    package com.yagocurvello.instam.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.yagocurvello.instam.R;
import com.yagocurvello.instam.adapter.GridAdapter;
import com.yagocurvello.instam.helper.ConfigFirebase;
import com.yagocurvello.instam.helper.UsuarioFirebase;
import com.yagocurvello.instam.model.Postagem;
import com.yagocurvello.instam.model.Usuario;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PerfilAmigoActivity extends AppCompatActivity {

    private CircleImageView circleFotoPerfilAmigo;
    private TextView numSeguidores, numSeguidos, numPublicacoes;
    private Button buttonSeguir;
    private GridView gridPerfilAmigo;
    private Usuario amigo;
    private Usuario usuarioLogado;
    private String idUsuarioLogado;
    private ValueEventListener eventListenerAmigo;
    private GridAdapter gridAdapter;
    private List<Postagem> postagemList;

    private DatabaseReference databaseReferenceSeguidor;
    private DatabaseReference databaseReferenceSeguidores;
    private DatabaseReference databaseReferenceUsuarios;
    private DatabaseReference databaseReferenceUsuarioLogado;
    private DatabaseReference databaseReferenceAmigo;
    private DatabaseReference databaseReferencePostagem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_amigo);

        configIniciais();

        //Setar dados do Usuario
        if (amigo.getCaminhoFoto() != null){
            Uri uri = Uri.parse(amigo.getCaminhoFoto());
            Glide.with(PerfilAmigoActivity.this).load(uri).into(circleFotoPerfilAmigo);
        }else {
            circleFotoPerfilAmigo.setImageResource(R.drawable.padrao);
        }

        iniciarImageLoader();
        carregarFotosPostagem();

        gridPerfilAmigo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView,View view,int i,long l) {

                Postagem postagem = postagemList.get(i);
                Intent intent = new Intent(getApplicationContext(), VisualizarPostagemActivity.class);
                intent.putExtra("postagem", postagem);
                intent.putExtra("usuario", amigo);
                startActivity(intent);

            }
        });

    }

    private void iniciarImageLoader(){

        ImageLoaderConfiguration configuration = new ImageLoaderConfiguration.Builder(this)
                .memoryCache(new LruMemoryCache(2 * 1024 * 1024))
                .memoryCacheSize(2 * 1024 * 1024)
                .diskCacheSize(50 * 1024 * 1024)
                .diskCacheFileCount(100)
                .diskCacheFileNameGenerator(new HashCodeFileNameGenerator())
                .build();
        ImageLoader.getInstance().init(configuration);

    }

    private void configIniciais(){
        //Recuperar Usuario Amigo
        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            amigo = (Usuario) bundle.getSerializable("amigo");

        }

        Toolbar toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle(amigo.getNome());
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_24);

        circleFotoPerfilAmigo = findViewById(R.id.circleFotoPerfilAmigo);
        numPublicacoes = findViewById(R.id.textNumPubliAmigo);
        numSeguidores = findViewById(R.id.textNumSeguidoresAmigo);
        numSeguidos = findViewById(R.id.textNumSeguidosAmigo);
        gridPerfilAmigo = findViewById(R.id.gridViewPerfilAmigo);
        buttonSeguir = findViewById(R.id.buttonSeguir);

        buttonSeguir.setText("Carregando");

        databaseReferenceUsuarios = ConfigFirebase.getFirebaseDatabase().child("usuarios");
        databaseReferenceSeguidores = ConfigFirebase.getFirebaseDatabase().child("seguidores");
        databaseReferencePostagem = ConfigFirebase.getFirebaseDatabase().child("postagens").child(amigo.getId());
        idUsuarioLogado = UsuarioFirebase.getIdUsuario();


    }


    private void recuperarDadosAmigo(){
        databaseReferenceAmigo = ConfigFirebase.getFirebaseDatabase().child("usuarios").child(amigo.getId());
        eventListenerAmigo = databaseReferenceAmigo.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Usuario usuario = snapshot.getValue(Usuario.class);

                String postagens = String.valueOf(usuario.getPostagens());
                String seguidores = String.valueOf(usuario.getSeguidores());
                String seguindo = String.valueOf(usuario.getSeguindo());

                numPublicacoes.setText(postagens);
                numSeguidores.setText(seguidores);
                numSeguidos.setText(seguindo);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void verificaSeSeguindo(){

        databaseReferenceSeguidor = ConfigFirebase.getFirebaseDatabase().child("seguidores")
                .child(amigo.getId()).child(idUsuarioLogado);
        databaseReferenceSeguidor.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    //Já está seguindo
                    habilitarSeguir(true);
                }else{
                    //Não está seguindo
                    habilitarSeguir(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void habilitarSeguir (boolean segueUsuario){
        if (segueUsuario){
            buttonSeguir.setText("Seguindo");
        }else {
            buttonSeguir.setText("Seguir");
            buttonSeguir.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Salvar seguidor
                    salvarSeguidor(usuarioLogado, amigo);
                }
            });
        }
    }

    private void salvarSeguidor(Usuario uLogado, Usuario uAmigo){

        //Salvar mudanças
        HashMap<String, Object> dadosUsuarioLogado = new HashMap<>();
        dadosUsuarioLogado.put("nome", uLogado.getNome());
        dadosUsuarioLogado.put("caminhoFoto", uLogado.getCaminhoFoto());

        DatabaseReference seguidorRef = databaseReferenceSeguidores
                .child(uAmigo.getId())
                .child(uLogado.getId());
        seguidorRef.setValue(dadosUsuarioLogado);

        //Alterar botão
        buttonSeguir.setText("Seguindo");
        buttonSeguir.setOnClickListener(null);

        //incrementar seguidores do amigo
        int seguidores = uAmigo.getSeguidores() + 1;

        HashMap<String, Object> dadosSeguidores = new HashMap<>();
        dadosSeguidores.put("seguidores", seguidores);

        DatabaseReference usuarioSeguidores = databaseReferenceUsuarios
                .child(uAmigo.getId());
        usuarioSeguidores.updateChildren(dadosSeguidores);

        Log.i("seguidores: ",String.valueOf(seguidores));

        //incrementar seguindo do usuario logado
        int seguindo = uLogado.getSeguindo() + 1;

        HashMap<String, Object> dadosSeguindo = new HashMap<>();
        dadosSeguindo.put("seguindo", seguindo);

        DatabaseReference usuarioSeguindo = databaseReferenceUsuarios
                .child(uLogado.getId());
        usuarioSeguindo.updateChildren(dadosSeguindo);

        Log.i("seguindo: ",String.valueOf(seguindo));
    }

    private void carregarFotosPostagem(){
        postagemList = new ArrayList<>();

        databaseReferencePostagem.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //Tamanho grid
                int tamanhoGrid = getResources().getDisplayMetrics().widthPixels;
                int tamanhomagem = tamanhoGrid/3;
                gridPerfilAmigo.setColumnWidth(tamanhomagem);

                List<String> urlFotos = new ArrayList<>();
                for (DataSnapshot ds: snapshot.getChildren()){
                    Postagem postagem = ds.getValue(Postagem.class);
                    postagemList.add(postagem);
                    urlFotos.add(postagem.getCaminhoDaFoto());
                }
                numPublicacoes.setText(String.valueOf(urlFotos.size()));

                //Configurar Adapter
                gridAdapter = new GridAdapter(getApplicationContext(), R.layout.grid_postagem, urlFotos);
                gridPerfilAmigo.setAdapter(gridAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void recuperarDadosUsuarioLogado (){

        databaseReferenceUsuarioLogado = databaseReferenceUsuarios.child(idUsuarioLogado);
        databaseReferenceUsuarioLogado.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //Recupera os dados do usuario logado
                usuarioLogado = snapshot.getValue(Usuario.class);

                //Verifica se já está seguindo
                verificaSeSeguindo();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        recuperarDadosAmigo();
        recuperarDadosUsuarioLogado();
    }

    @Override
    protected void onStop() {
        super.onStop();
        databaseReferenceAmigo.removeEventListener(eventListenerAmigo);
    }
}