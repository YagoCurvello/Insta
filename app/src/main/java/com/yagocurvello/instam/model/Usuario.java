package com.yagocurvello.instam.model;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.yagocurvello.instam.helper.ConfigFirebase;

import java.io.Serializable;

public class Usuario implements Serializable {

    private  String id, nome, email, senha, caminhoFoto;
    private int seguidores = 0, seguindo = 0, postagens =0;

    public Usuario() {
    }

    public void salvar (){
            DatabaseReference reference = ConfigFirebase.getFirebaseDatabase();
            reference.child("usuarios").child(this.id).setValue(this);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) { this.id = id; }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Exclude
    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getCaminhoFoto() {
        return caminhoFoto;
    }

    public void setCaminhoFoto(String caminhoFoto) {
        this.caminhoFoto = caminhoFoto;
    }

    public int getSeguidores() {
        return seguidores;
    }

    public void setSeguidores(int seguidores) {
        this.seguidores = seguidores;
    }

    public int getSeguindo() {
        return seguindo;
    }

    public void setSeguindo(int seguindo) {
        this.seguindo = seguindo;
    }

    public int getPostagens() {
        return postagens;
    }

    public void setPostagens(int postagens) {
        this.postagens = postagens;
    }
}
