package com.yagocurvello.instam.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.yagocurvello.instam.R;
import com.yagocurvello.instam.activity.ComentarioActivity;
import com.yagocurvello.instam.helper.ConfigFirebase;
import com.yagocurvello.instam.helper.UsuarioFirebase;
import com.yagocurvello.instam.model.Feed;
import com.yagocurvello.instam.model.Postagem;
import com.yagocurvello.instam.model.PostagemCurtida;
import com.yagocurvello.instam.model.Usuario;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.MyViewHolder> {

    private List<Feed> feedList;
    private Context context;


    public FeedAdapter(List<Feed> feedList,Context context) {
        this.feedList = feedList;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent,int viewType) {

        View itemLista = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_feed, parent, false);

        return new FeedAdapter.MyViewHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder,int position) {

        final Feed feed = feedList.get(position);
        final Usuario usuarioLogado = UsuarioFirebase.recuperarUsuarioLogado();

        holder.nomeUsuario.setText(feed.getNomeUsuario());
        holder.textDescricao.setText(feed.getDescricao());

        if (feed.getFotoUsuario() != null){
            Uri uriFotoUsuario = Uri.parse(feed.getFotoUsuario());
            Glide.with(context).load(uriFotoUsuario).into(holder.fotoUsuario);
        }else {
            holder.fotoUsuario.setImageResource(R.drawable.padrao);
        }

        Uri uriFotoPostagem = Uri.parse(feed.getFotoPostagem());
        Glide.with(context).load(uriFotoPostagem).into(holder.fotoPostagem);

        holder.visualizarComentario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ComentarioActivity.class);
                intent.putExtra("idPostagem", feed.getId());
                context.startActivity(intent);


            }
        });

        final DatabaseReference curtidasRef = ConfigFirebase.getFirebaseDatabase().child("postagens-curtidas")
                .child(feed.getId());
        curtidasRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                int qntCurtidas = 0;
                if (snapshot.hasChild("qntCurtidas")){
                    PostagemCurtida postagemCurtida = snapshot.getValue(PostagemCurtida.class);
                    qntCurtidas = postagemCurtida.getQntCurtidas();
                }

                //Verifica se já clicado
                if (snapshot.hasChild(usuarioLogado.getId())){
                    holder.likeButton.setLiked(true);
                }else {
                    holder.likeButton.setLiked(false);
                }

                //Preparação para objeto Postagem Curtida
                final PostagemCurtida curtida = new PostagemCurtida();
                curtida.setQntCurtidas(qntCurtidas);
                curtida.setFeed(feed);
                curtida.setUsuario(usuarioLogado);

                //adiciona evento de curtir foto
                holder.likeButton.setOnLikeListener(new OnLikeListener() {
                    @Override
                    public void liked(LikeButton likeButton) {
                        curtida.salvar();
                        holder.textCurtidas.setText(curtida.qntCurtidas + " Curtidas");
                    }

                    @Override
                    public void unLiked(LikeButton likeButton) {
                        curtida.removerQnt();
                        holder.textCurtidas.setText(curtida.qntCurtidas + " Curtidas");
                    }
                });

                holder.textCurtidas.setText(curtida.qntCurtidas + " Curtidas");

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return feedList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        CircleImageView fotoUsuario;
        TextView nomeUsuario, textCurtidas, textDescricao;
        ImageView fotoPostagem, visualizarComentario;
        LikeButton likeButton;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            fotoUsuario = itemView.findViewById(R.id.circleFotoUsuarioPostagem);
            nomeUsuario = itemView.findViewById(R.id.textUsuarioPostagem);
            textCurtidas = itemView.findViewById(R.id.textCurtidas);
            textDescricao = itemView.findViewById(R.id.textDescricaoPostagem);
            visualizarComentario = itemView.findViewById(R.id.imageIrComentarios);
            fotoPostagem = itemView.findViewById(R.id.imagePostagem);
            likeButton = itemView.findViewById(R.id.likeButton);


            //Só dá para usar o find depois de fazer o inflate
        }
    }
}
