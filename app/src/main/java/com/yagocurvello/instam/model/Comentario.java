package com.yagocurvello.instam.model;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.yagocurvello.instam.helper.ConfigFirebase;

public class Comentario {

    private String idComentario, idPostagem, idUsuario, caminhoFoto, nomeUsuario, comentario;

    public Comentario() {

        DatabaseReference databaseReference = ConfigFirebase.getFirebaseDatabase().child("comentarios");
        String idComentario = databaseReference.push().getKey();
        setIdComentario(idComentario);

    }

    @Exclude
    public String getIdComentario() {
        return idComentario;
    }

    public void setIdComentario(String idComentario) {
        this.idComentario = idComentario;
    }

    @Exclude
    public String getIdPostagem() {
        return idPostagem;
    }

    public void setIdPostagem(String idPostagem) {
        this.idPostagem = idPostagem;
    }

    @Exclude
    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getCaminhoFoto() {
        return caminhoFoto;
    }

    public void setCaminhoFoto(String caminhoFoto) {
        this.caminhoFoto = caminhoFoto;
    }

    public String getNomeUsuario() {
        return nomeUsuario;
    }

    public void setNomeUsuario(String nomeUsuario) {
        this.nomeUsuario = nomeUsuario;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public void salvar(){

        DatabaseReference reference = ConfigFirebase.getFirebaseDatabase();
        reference.child("comentarios").child(this.idPostagem).child(this.idComentario).setValue(this);

    }
}
