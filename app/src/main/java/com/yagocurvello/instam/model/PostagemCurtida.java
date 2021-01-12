package com.yagocurvello.instam.model;

import com.google.firebase.database.DatabaseReference;
import com.yagocurvello.instam.helper.ConfigFirebase;

import java.util.HashMap;

public class PostagemCurtida {

    public int qntCurtidas = 0;
    public Feed feed;
    public Usuario usuario;

    public PostagemCurtida() {
    }

    public int getQntCurtidas() {
        return qntCurtidas;
    }

    public void setQntCurtidas(int qntCurtidas) {
        this.qntCurtidas = qntCurtidas;
    }

    public Feed getFeed() {
        return feed;
    }

    public void setFeed(Feed feed) {
        this.feed = feed;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public void salvar(){
        HashMap<String, Object> dadosUsuario = new HashMap<>();
        dadosUsuario.put("nomeUsuario", usuario.getNome());
        dadosUsuario.put("caminhoFoto", usuario.getCaminhoFoto());


        DatabaseReference databaseReference = ConfigFirebase.getFirebaseDatabase();
        DatabaseReference curtidaRef = databaseReference.child("postagens-curtidas")
                .child(feed.getId()).child(usuario.getId());

        curtidaRef.setValue(dadosUsuario);
        atualizarQnt(1);
    }

    public void atualizarQnt(int valor){
        DatabaseReference databaseReference = ConfigFirebase.getFirebaseDatabase();
        DatabaseReference curtidaRef = databaseReference.child("postagens-curtidas")
                .child(feed.getId()).child("qtdCurtidas");

        setQntCurtidas(getQntCurtidas() + valor);
        curtidaRef.setValue(getQntCurtidas());
    }

    public void removerQnt(){

        DatabaseReference databaseReference = ConfigFirebase.getFirebaseDatabase();
        DatabaseReference curtidaRef = databaseReference.child("postagens-curtidas")
                .child(feed.getId()).child(usuario.getId());

        curtidaRef.removeValue();

        atualizarQnt(-1);
    }
}
