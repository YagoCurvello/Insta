    package com.yagocurvello.instam.helper;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.yagocurvello.instam.helper.ConfigFirebase;
import com.yagocurvello.instam.model.Usuario;

import java.util.HashMap;
import java.util.Map;

    public class UsuarioFirebase {

    //Metodo que retorna o id do usuario
    public static String getIdUsuario(){
        FirebaseAuth usuario = ConfigFirebase.getFirebaseAutenticacao();
        if (usuario.getCurrentUser() != null){
            return usuario.getCurrentUser().getUid();
        }
        return null;
    }

    //Metodo que retorna o usuarioFirebase
    public static FirebaseUser getUsuarioAtual(){
        FirebaseAuth usuario = ConfigFirebase.getFirebaseAutenticacao();
        return usuario.getCurrentUser();
    }



    //Metodo que atualiza a foto do usuario
    public static boolean atualizarFotoUsuarioFb(Uri url){

        try {
            FirebaseUser user = getUsuarioAtual();
            UserProfileChangeRequest upcr = new UserProfileChangeRequest.Builder()
                    .setPhotoUri(url).build();

            user.updateProfile(upcr)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (!task.isSuccessful()){
                                Log.d("Perfil", "Erro ao atualizar foto de Perfil do UsuarioFirebase");
                            }
                        }
                    });
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    //Metodo que atualiza o nome do usuario
    public static boolean atualizarNomeUsuarioFb(final String name){

        try {
            FirebaseUser user = getUsuarioAtual();

            UserProfileChangeRequest upcr = new UserProfileChangeRequest.Builder()
                    .setDisplayName(name).build();

            user.updateProfile(upcr)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (!task.isSuccessful()){
                                Log.d("Perfil", "Erro ao atualizar nome de Perfil");
                            }
                        }
                    });
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public static boolean salvarUsuario(Usuario usuario){
        if (usuario.getId() != null){
            DatabaseReference reference = ConfigFirebase.getFirebaseDatabase();
            reference.child("usuarios").child(usuario.getId()).setValue(usuario);
            return true;
        }else {
            return false;
        }
    }


    public static void atualizarUsuario (Usuario usuario){
        DatabaseReference firebaseDatabaseRef = ConfigFirebase.getFirebaseDatabase();

        Usuario usuarioAtual = recuperarUsuarioLogado();

        Map objeto = new HashMap();
        objeto.put("/usuarios/" + getIdUsuario() + "/nome", usuarioAtual.getNome());
        objeto.put("/usuarios/" + getIdUsuario() + "/caminhoFoto", usuarioAtual.getCaminhoFoto());

        firebaseDatabaseRef.updateChildren(objeto);
    }

    public static void atualizarUsuarioPostagens (Usuario usuario){
        DatabaseReference firebaseDatabaseRef = ConfigFirebase.getFirebaseDatabase();
        DatabaseReference usuarioDatabaseRef = firebaseDatabaseRef.child("usuarios").child(usuario.getId());

        HashMap<String, Object> usuarioMap = new HashMap<>();
        usuarioMap.put("postagens", usuario.getPostagens());

        usuarioDatabaseRef.updateChildren(usuarioMap);

    }

    public static Usuario recuperarUsuarioLogado(){
        Usuario usuario = new Usuario();
        FirebaseUser firebaseUser = getUsuarioAtual();

        usuario.setEmail(firebaseUser.getEmail());
        usuario.setNome(firebaseUser.getDisplayName());
        usuario.setId(firebaseUser.getUid());

        if (firebaseUser.getPhotoUrl() == null){
            usuario.setCaminhoFoto("");
        }else {
            usuario.setCaminhoFoto(firebaseUser.getPhotoUrl().toString());
        }

        return usuario;
    }

    public static Usuario recuperarUsuarioCompleto(String id){
        final Usuario[] u = {new Usuario()};
        DatabaseReference databaseReference = ConfigFirebase.getFirebaseDatabase().child("usuarios").child(id);
        ValueEventListener eventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                u[0] = snapshot.getValue(Usuario.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        databaseReference.removeEventListener(eventListener);
        return u[0];
    }


}
