package com.yagocurvello.instam.model;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.yagocurvello.instam.helper.ConfigFirebase;
import com.yagocurvello.instam.helper.UsuarioFirebase;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Postagem implements Serializable {
    private String id, idUsuario, descricao, caminhoDaFoto;

    public Postagem() {

        DatabaseReference databaseReference = ConfigFirebase.getFirebaseDatabase().child("postagens");
        String idPostagem = databaseReference.push().getKey();
        setId(idPostagem);

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getCaminhoDaFoto() {
        return caminhoDaFoto;
    }

    public void setCaminhoDaFoto(String caminhoDaFoto) {
        this.caminhoDaFoto = caminhoDaFoto;
    }

    public boolean salvar (DataSnapshot snapshot){

        Map objeto = new HashMap();
        Usuario usuarioLogado = UsuarioFirebase.recuperarUsuarioLogado();

        DatabaseReference firebaseDatabase = ConfigFirebase.getFirebaseDatabase();

        //Referencia para postagem
        String combinacaoID = "/" + getIdUsuario() + "/" + getId();
        objeto.put("/postagens" + combinacaoID, this);

        //Referencia para feed
        for (DataSnapshot dataSnapshot : snapshot.getChildren()){

            String idSeguidor = dataSnapshot.getKey();

            //Monta objeto para salvar
            HashMap<String, Object> dadosSeguidor = new HashMap<>();
            dadosSeguidor.put("fotoPostagem", getCaminhoDaFoto());
            dadosSeguidor.put("descricao", getDescricao());
            dadosSeguidor.put("id", getId());
            dadosSeguidor.put("nomeUsuario", usuarioLogado.getNome());
            dadosSeguidor.put("fotoUsuario", usuarioLogado.getCaminhoFoto());

            String idsAtualizacao = "/" + idSeguidor + "/" + getId();
            objeto.put("/feed" + idsAtualizacao, dadosSeguidor);

        }


        firebaseDatabase.updateChildren(objeto);
        return true;
    }

}
