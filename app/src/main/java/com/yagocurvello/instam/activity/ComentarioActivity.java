package com.yagocurvello.instam.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.yagocurvello.instam.R;
import com.yagocurvello.instam.adapter.ComentarioAdapter;
import com.yagocurvello.instam.helper.ConfigFirebase;
import com.yagocurvello.instam.helper.UsuarioFirebase;
import com.yagocurvello.instam.model.Comentario;
import com.yagocurvello.instam.model.Usuario;

import java.util.ArrayList;
import java.util.List;

public class ComentarioActivity extends AppCompatActivity {

    private EditText editComentario;
    private Button buttonSend;
    private RecyclerView recyclerComentarios;
    private ComentarioAdapter comentarioAdapter;
    private String idPostagem;
    private Usuario usuarioLogado;
    private List<Comentario> comentarioList = new ArrayList<>();
    private DatabaseReference comentarioRef;
    private DatabaseReference firebaseRef;
    private ValueEventListener eventListenerComentario;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comentario);

        Toolbar toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle("Comentarios");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_24);


        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            idPostagem = bundle.getString("idPostagem");
        }

        editComentario = findViewById(R.id.editTextComentario);
        buttonSend = findViewById(R.id.buttonSend);
        recyclerComentarios = findViewById(R.id.recycleComentarios);
        usuarioLogado = UsuarioFirebase.recuperarUsuarioLogado();
        firebaseRef = ConfigFirebase.getFirebaseDatabase();


        //Configurar Adapter
        comentarioAdapter = new ComentarioAdapter(comentarioList, this);
        recyclerComentarios.setAdapter(comentarioAdapter);

        //Configurar RecyclerView
        recyclerComentarios.setHasFixedSize(true);
        recyclerComentarios.setLayoutManager(new LinearLayoutManager(this));


        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                salvarComentario();
            }
        });

    }

    private void salvarComentario(){
        String textComentario = editComentario.getText().toString();
        if (textComentario != null && !textComentario.equals("")){

            Comentario comentario = new Comentario();
            comentario.setIdPostagem(idPostagem);
            comentario.setCaminhoFoto(usuarioLogado.getCaminhoFoto());
            comentario.setNomeUsuario(usuarioLogado.getNome());
            comentario.setIdUsuario(usuarioLogado.getId());
            comentario.setComentario(textComentario);

            comentario.salvar();


        }else {
            Toast.makeText(this, "Insira um comentario antes de postar!", Toast.LENGTH_SHORT).show();
        }
        editComentario.setText("");
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    private void recuperaComentarios(){


        comentarioRef = firebaseRef.child("comentarios").child(idPostagem);

        eventListenerComentario = comentarioRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                comentarioList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Comentario comentario = dataSnapshot.getValue(Comentario.class);
                    comentarioList.add(comentario);
                }
                comentarioAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        recuperaComentarios();
    }

    @Override
    protected void onStop() {
        super.onStop();
        comentarioRef.removeEventListener(eventListenerComentario);
    }
}