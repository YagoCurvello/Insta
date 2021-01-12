package com.yagocurvello.instam.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.yagocurvello.instam.R;
import com.yagocurvello.instam.model.Comentario;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ComentarioAdapter extends RecyclerView.Adapter<ComentarioAdapter.MyViewHolder> {

    List<Comentario> comentarioList;
    Context context;

    public ComentarioAdapter(List<Comentario> comentarioList,Context context) {
        this.comentarioList = comentarioList;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent,int viewType) {

        View itemLista = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_comentario, parent, false);

        return new ComentarioAdapter.MyViewHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder,int position) {

        Comentario comentario = comentarioList.get(position);

        holder.comentario.setText('"' + comentario.getComentario() + '"');
        holder.nomeUsuario.setText(comentario.getNomeUsuario());
        Glide.with(context).load(comentario.getCaminhoFoto()).into(holder.imagemPerfil);

    }

    @Override
    public int getItemCount() {
        return comentarioList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        CircleImageView imagemPerfil;
        TextView nomeUsuario, comentario;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imagemPerfil = itemView.findViewById(R.id.circleImageViewFotoPerfilComentario);
            nomeUsuario = itemView.findViewById(R.id.textNomeComentario);
            comentario = itemView.findViewById((R.id.textComentarioR));
        }
    }
}
