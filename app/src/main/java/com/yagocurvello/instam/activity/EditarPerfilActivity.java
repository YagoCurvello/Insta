package com.yagocurvello.instam.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.yagocurvello.instam.R;
import com.yagocurvello.instam.helper.ConfigFirebase;
import com.yagocurvello.instam.helper.Permissao;
import com.yagocurvello.instam.helper.UsuarioFirebase;
import com.yagocurvello.instam.model.Usuario;

import java.io.ByteArrayOutputStream;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditarPerfilActivity extends AppCompatActivity {

    private EditText editNome, editEmail;
    private CircleImageView circleFoto;
    private Button buttonSalvar;
    private Usuario usuario;
    private StorageReference storageReference;
    private FirebaseUser usuarioFirebase;
    private static final int SELECAO_GALERIA = 200;
    private final static int SELECAO_CAMERA = 100;


    private String[] permissoesNecessarias = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_perfil);

        Toolbar toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle("Editar Perfil");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_24);

        Permissao.ValidarPermissoes(permissoesNecessarias, EditarPerfilActivity.this, 1);

        editNome = findViewById(R.id.editNomeUsuario);
        editEmail = findViewById(R.id.editEmailUsuario);
        circleFoto = findViewById(R.id.circleFotoPerfilEditar);
        buttonSalvar = findViewById(R.id.buttonSalvarPerfil);

        //Recupera o Usuario Logado
        usuarioFirebase = UsuarioFirebase.getUsuarioAtual();
        usuario = UsuarioFirebase.recuperarUsuarioLogado();
        //Recupera imagem
        Uri url = usuarioFirebase.getPhotoUrl();
        if (url != null){
            Glide.with(EditarPerfilActivity.this).load(url).into(circleFoto);
        }else {
            circleFoto.setImageResource(R.drawable.padrao);
        }

        //Inicia os campos de texto com os dados do Usuario Recuperado
        editNome.setText(usuario.getNome());
        editEmail.setText(usuario.getEmail());

        buttonSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (editNome != null || !editNome.equals("")){
                    usuario.setNome(editNome.getText().toString());
                    UsuarioFirebase.atualizarUsuario(usuario);
                    UsuarioFirebase.atualizarNomeUsuarioFb(editNome.getText().toString());
                    finish();
                }else {
                    Toast.makeText(EditarPerfilActivity.this, "Nome não pode ser vazio", Toast.LENGTH_SHORT).show();
                }
            }
        });

        circleFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                if (i.resolveActivity(getPackageManager()) != null){
                    startActivityForResult(i ,SELECAO_GALERIA);
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode,int resultCode,@Nullable Intent data) {
        super.onActivityResult(requestCode,resultCode,data);

        if (resultCode == RESULT_OK){
            Bitmap imagem = null;

            try {
                switch (requestCode){
                    //Caso a imagem venha da galeria
                    case SELECAO_GALERIA:
                        Uri localImagemSelecionada = data.getData();

                        //Configura imagem na tela
                        imagem = MediaStore.Images.Media.getBitmap(getContentResolver(), localImagemSelecionada);

                        //Recupera os dados para salvar no firebase
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        imagem.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                        byte[] dadosImagem = baos.toByteArray();

                        //Salvar imagem para o firebase
                        final StorageReference imagemRef = storageReference = ConfigFirebase.getFirebaseStorage().child("imagens")
                                .child("perfil").child(usuario.getId() + ".jpeg");
                        UploadTask uploadTask = imagemRef.putBytes(dadosImagem);
                        uploadTask.addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(EditarPerfilActivity.this,
                                        "Falha ao salvar Imagem", Toast.LENGTH_SHORT).show();

                            }
                        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                //Recuperar Local da foto para salvar no usuario
                                imagemRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Uri> task) {
                                        Uri url = task.getResult();

                                        //Salvar caminho da foto
                                        usuario.setCaminhoFoto(url.toString());
                                        //Salvar imagem no usuario Firebase
                                        UsuarioFirebase.atualizarFotoUsuarioFb(url);
                                    }
                                });

                                Toast.makeText(EditarPerfilActivity.this,
                                        "Imagem salva com sucesso", Toast.LENGTH_SHORT).show();
                            }
                        });

                        break;
                }

                //Se foi escolhida uma imagem
                if (imagem != null){
                    circleFoto.setImageBitmap(imagem);
                }

            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}