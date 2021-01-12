package com.yagocurvello.instam.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.yagocurvello.instam.R;
import com.yagocurvello.instam.helper.ConfigFirebase;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private EditText editLoginEmail, editLoginSenha;
    private TextView textLoginCadastro;
    private Button buttonEntrar;
    private ProgressBar progressLogin;
    private String textoEmail, textoSenha;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        configIniciais();

        buttonEntrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressLogin.setVisibility(View.VISIBLE);
                textoEmail = editLoginEmail.getText().toString();
                textoSenha = editLoginSenha.getText().toString();

                if (verificaTextosVazios(textoEmail, textoSenha)) {
                    logarUsuario(textoEmail, textoSenha);
                }
            }
        });

        textLoginCadastro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, CadastroActivity.class));
                finish();
            }
        });
    }

    public void configIniciais (){
        firebaseAuth = ConfigFirebase.getFirebaseAutenticacao();

        editLoginEmail = findViewById(R.id.editLoginEmail);
        editLoginSenha = findViewById(R.id.editLoginSenha);
        textLoginCadastro = findViewById(R.id.textLoginCadastro);
        buttonEntrar = findViewById(R.id.buttonLoginEntrar);
        progressLogin = findViewById(R.id.progressLogin);

        editLoginEmail.requestFocus();
    }

    public boolean verificaTextosVazios (String textoEmail, String textoSenha){
        if (!textoEmail.isEmpty()){
            if (!textoSenha.isEmpty()){
                return true;
            }else {
                Toast.makeText(LoginActivity.this, "Campo Senha vazio!", Toast.LENGTH_SHORT).show();
                progressLogin.setVisibility(View.GONE);
                return false;
            }
        }else {
            Toast.makeText(LoginActivity.this, "Campo Email vazio!", Toast.LENGTH_SHORT).show();
            progressLogin.setVisibility(View.GONE);
            return false;
        }
    }

    public void logarUsuario(String textoEmail, String textoSenha){

        firebaseAuth.signInWithEmailAndPassword(textoEmail, textoSenha)
                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            progressLogin.setVisibility(View.GONE);
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finish();
                        }else{
                            progressLogin.setVisibility(View.GONE);
                            String error;
                            try {
                                throw task.getException();
                            }catch (FirebaseAuthInvalidUserException e ){
                                error = "E-mail e/ou Senha inválidos";
                            }catch (FirebaseAuthInvalidCredentialsException e ){
                                error = "Senha incorreta";
                            } catch (Exception e) {
                                error = "Erro: " + e.getMessage();
                                e.printStackTrace();
                            }
                            Toast.makeText(LoginActivity.this, error, Toast.LENGTH_LONG).show();
                            Log.i("Erro login", error);
                        }
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (firebaseAuth.getCurrentUser() != null){
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }
    }
}