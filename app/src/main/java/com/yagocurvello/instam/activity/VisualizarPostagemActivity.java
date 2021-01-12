package com.yagocurvello.instam.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.yagocurvello.instam.R;
import com.yagocurvello.instam.model.Postagem;
import com.yagocurvello.instam.model.Usuario;

import de.hdodenhof.circleimageview.CircleImageView;

public class VisualizarPostagemActivity extends AppCompatActivity {

    private CircleImageView fotoUsuario;
    private TextView nomeUsuario, textCurtidas, textDescricao;
    private ImageView fotoPostagem;
    private Postagem postagem;
    private Usuario usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visualizar_postagem);

        Toolbar toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle("Visualizar Postagem");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_24);

        //Receber dados
        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            postagem = (Postagem) bundle.getSerializable("postagem");
            usuario = (Usuario) bundle.getSerializable("usuario");
        }

        //Config iniciais
        fotoUsuario = findViewById(R.id.circleFotoUsuarioPostagem);
        fotoPostagem = findViewById(R.id.imagePostagem);
        nomeUsuario = findViewById(R.id.textUsuarioPostagem);
        textCurtidas = findViewById(R.id.textCurtidas);
        textDescricao = findViewById(R.id.textDescricaoPostagem);

        //Config Usuario
        if (usuario.getCaminhoFoto() != null){
            Uri uri = Uri.parse(usuario.getCaminhoFoto());
            Glide.with(VisualizarPostagemActivity.this).load(uri).into(fotoUsuario);
        }else {
            fotoUsuario.setImageResource(R.drawable.padrao);
        }
        nomeUsuario.setText(usuario.getNome());

        //Config Postagem
        Uri uri2 = Uri.parse(postagem.getCaminhoDaFoto());
        Glide.with(VisualizarPostagemActivity.this).load(uri2).into(fotoPostagem);

        textDescricao.setText('"' + postagem.getDescricao() + '"');

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }
}