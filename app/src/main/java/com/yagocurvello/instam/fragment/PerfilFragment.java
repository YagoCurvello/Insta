package com.yagocurvello.instam.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.yagocurvello.instam.R;
import com.yagocurvello.instam.activity.EditarPerfilActivity;
import com.yagocurvello.instam.activity.PerfilAmigoActivity;
import com.yagocurvello.instam.activity.VisualizarPostagemActivity;
import com.yagocurvello.instam.adapter.GridAdapter;
import com.yagocurvello.instam.helper.ConfigFirebase;
import com.yagocurvello.instam.helper.UsuarioFirebase;
import com.yagocurvello.instam.model.Postagem;
import com.yagocurvello.instam.model.Usuario;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PerfilFragment extends Fragment {

    private TextView textPublicacoes, textSeguidores, textSeguidos;
    private Button buttonEditarPerfil;
    private CircleImageView circleFotoPerfil;
    private ProgressBar progressPerfil;
    private GridView gridViewPerfil;
    private GridAdapter gridAdapter;
    private DatabaseReference databaseReferencePerfil;
    private DatabaseReference databaseReferencePostagem;
    private ValueEventListener eventListenerPerfil;
    private Usuario usuarioLogado;
    private List<Postagem> postagemList;
    private FirebaseUser usuarioFirebase;

    public PerfilFragment (){

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_perfil,container,false);


        //Recupera o Usuario Logado
        usuarioFirebase = UsuarioFirebase.getUsuarioAtual();
        usuarioLogado = UsuarioFirebase.recuperarUsuarioLogado();

        //Configurações iniciais
        textPublicacoes = view.findViewById(R.id.textNumPubli);
        textSeguidores = view.findViewById(R.id.textNumSeguidores);
        textSeguidos = view.findViewById(R.id.textNumSeguidos);
        buttonEditarPerfil = view.findViewById(R.id.buttonEditarPerfil);
        circleFotoPerfil = view.findViewById(R.id.circleFotoPerfil);
        progressPerfil = view.findViewById(R.id.progressPerfil);
        gridViewPerfil = view.findViewById(R.id.gridViewPerfil);
        databaseReferencePostagem = ConfigFirebase.getFirebaseDatabase().child("postagens").child(usuarioLogado.getId());



        buttonEditarPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), EditarPerfilActivity.class));
            }
        });

        iniciarImageLoader();
        carregarFotosPostagem();

        gridViewPerfil.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView,View view,int i,long l) {

                Postagem postagem = postagemList.get(i);
                Intent intent = new Intent(getActivity(), VisualizarPostagemActivity.class);
                intent.putExtra("postagem", postagem);
                intent.putExtra("usuario", usuarioLogado);
                startActivity(intent);

            }
        });

        return view;
    }

    private void recuperarDadosPerfil(){
        databaseReferencePerfil = ConfigFirebase.getFirebaseDatabase().child("usuarios").child(usuarioLogado.getId());
        eventListenerPerfil = databaseReferencePerfil.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Usuario usuario = snapshot.getValue(Usuario.class);

                String postagens = String.valueOf(usuario.getPostagens());
                String seguidores = String.valueOf(usuario.getSeguidores());
                String seguindo = String.valueOf(usuario.getSeguindo());

                textPublicacoes.setText(postagens);
                textSeguidores.setText(seguidores);
                textSeguidos.setText(seguindo);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void iniciarImageLoader(){

        ImageLoaderConfiguration configuration = new ImageLoaderConfiguration.Builder(getActivity())
                .memoryCache(new LruMemoryCache(2 * 1024 * 1024))
                .memoryCacheSize(2 * 1024 * 1024)
                .diskCacheSize(50 * 1024 * 1024)
                .diskCacheFileCount(100)
                .diskCacheFileNameGenerator(new HashCodeFileNameGenerator())
                .build();
        ImageLoader.getInstance().init(configuration);

    }

    private void carregarFotosPostagem(){
        postagemList = new ArrayList<>();

        databaseReferencePostagem.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //Tamanho grid
                int tamanhoGrid = getResources().getDisplayMetrics().widthPixels;
                int tamanhomagem = tamanhoGrid / 3;
                gridViewPerfil.setColumnWidth(tamanhomagem);

                List<String> urlFotos = new ArrayList<>();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Postagem postagem = ds.getValue(Postagem.class);
                    postagemList.add(postagem);
                    urlFotos.add(postagem.getCaminhoDaFoto());
                }
                textPublicacoes.setText(String.valueOf(urlFotos.size()));

                //Configurar Adapter
                gridAdapter = new GridAdapter(getActivity(),R.layout.grid_postagem,urlFotos);
                gridViewPerfil.setAdapter(gridAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
            @Override
    public void onStart() {
        super.onStart();
        recuperarDadosPerfil();
        
        //Setar foto do Usuario
        if (usuarioLogado.getCaminhoFoto() != null){
             Uri uri = usuarioFirebase.getPhotoUrl();
             Glide.with(getActivity()).load(uri).into(circleFotoPerfil);
        }else {
             circleFotoPerfil.setImageResource(R.drawable.padrao);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        databaseReferencePerfil.removeEventListener(eventListenerPerfil);
    }
}