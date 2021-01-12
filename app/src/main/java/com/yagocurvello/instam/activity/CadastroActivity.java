package com.yagocurvello.instam.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.yagocurvello.instam.R;
import com.yagocurvello.instam.helper.Base64Custom;
import com.yagocurvello.instam.helper.ConfigFirebase;
import com.yagocurvello.instam.helper.UsuarioFirebase;
import com.yagocurvello.instam.model.Usuario;

public class CadastroActivity extends AppCompatActivity {

    private EditText editCadastroNome, editCadastroEmail, editCadastroSenha;
    private TextView textCadastroLogin;
    private Button buttonCadastro;
    private ProgressBar progressCadastro;
    private Usuario usuario;
    private FirebaseAuth firebaseAuth;
    private String textoNome, textoEmail, textoSenha;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        configIniciais();

        //Cadastrar novo Usuario
        buttonCadastro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                textoNome = editCadastroNome.getText().toString();
                textoEmail = editCadastroEmail.getText().toString();
                textoSenha = editCadastroSenha.getText().toString();

                if (verificaTextosVazios(textoNome, textoEmail, textoSenha)){
                    cadastrarUsuario(textoNome, textoEmail, textoSenha);
                }
            }
        });

        textCadastroLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(CadastroActivity.this, LoginActivity.class));
                finish();
            }
        });
    }

    public void configIniciais(){
        //Configurações iniciais dos elementos de interface
        editCadastroNome = findViewById(R.id.editCadastroNome);
        editCadastroEmail = findViewById(R.id.editCadastroEmail);
        editCadastroSenha = findViewById(R.id.editCadastroSenha);
        textCadastroLogin = findViewById(R.id.textCadastroLogin);
        buttonCadastro = findViewById(R.id.buttonCadastroEntrar);
        progressCadastro = findViewById(R.id.progressCadastro);

        editCadastroNome.requestFocus();

        firebaseAuth = ConfigFirebase.getFirebaseAutenticacao();

        progressCadastro.setVisibility(View.GONE);
    }

    public void cadastrarUsuario(String textoNome,String textoEmail,String textoSenha){

        progressCadastro.setVisibility(View.VISIBLE);

        usuario = new Usuario();
        usuario.setNome(textoNome);
        usuario.setEmail(textoEmail);
        usuario.setSenha(textoSenha);

        firebaseAuth.createUserWithEmailAndPassword(textoEmail, textoSenha)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    try {
                        //Salvar dados no firebase
                        usuario.setId(task.getResult().getUser().getUid());
                        usuario.salvar();
                        UsuarioFirebase.atualizarNomeUsuarioFb(usuario.getNome());

                        Toast.makeText(getApplicationContext(),"Cadastro Realizado!", Toast.LENGTH_LONG).show();

                        startActivity(new Intent(CadastroActivity.this, MainActivity.class));
                        finish();
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }else {
                    String error;
                    try {
                        throw task.getException();
                    } catch (FirebaseAuthWeakPasswordException e) {
                        error = "Senha fraca";
                    } catch (FirebaseAuthInvalidCredentialsException e){
                        error = "email inválido";
                    } catch (FirebaseAuthUserCollisionException e){
                        error = "email já cadastrado";
                    }catch (Exception e){
                        error = "Erro: " + e.getMessage();
                        e.printStackTrace();
                    }
                    Toast.makeText(CadastroActivity.this, error, Toast.LENGTH_LONG).show();
                    progressCadastro.setVisibility(View.GONE);
                }
            }
                });
    }

    public boolean verificaTextosVazios (String textoNome, String textoEmail, String textoSenha){

        if (!textoNome.isEmpty()){
            if (!textoEmail.isEmpty()){
                if (!textoSenha.isEmpty()){
                    return true;
                }else {
                    Toast.makeText(CadastroActivity.this, "Campo Senha vazio!", Toast.LENGTH_SHORT).show();
                    progressCadastro.setVisibility(View.GONE);
                    return false;
                }
            }else {
                Toast.makeText(CadastroActivity.this, "Campo Email vazio!", Toast.LENGTH_SHORT).show();
                progressCadastro.setVisibility(View.GONE);
                return false;
            }
        }else {
            Toast.makeText(CadastroActivity.this, "Campo Nome vazio!", Toast.LENGTH_SHORT).show();
            progressCadastro.setVisibility(View.GONE);
            return false;
        }
    }

}