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
import com.yagocurvello.instam.model.Usuario;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ContatosAdapter extends RecyclerView.Adapter<ContatosAdapter.MyViewHolder> {

    private List<Usuario> listContatos;
    private Context context;

    public ContatosAdapter(List<Usuario> listContatos,Context context) {
        this.listContatos = listContatos;
        this.context = context;
    }

    public List<Usuario> getListContatos(){
        return this.listContatos;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent,int viewType) {

        View itemLista = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_contatos, parent, false);

        return new MyViewHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder,int position) {

        Usuario contato = listContatos.get(position);
        holder.nome.setText(contato.getNome());
        holder.email.setText(contato.getEmail());
        if (contato.getCaminhoFoto() != null){
            Uri uri = Uri.parse(contato.getCaminhoFoto());
            Glide.with(context).load(uri).into(holder.foto);
        }else {
                holder.foto.setImageResource(R.drawable.padrao);
            }
        }

    @Override
    public int getItemCount() {
        return listContatos.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView nome;
        TextView email;
        CircleImageView foto;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            nome = itemView.findViewById(R.id.textNome);
            email = itemView.findViewById(R.id.textEmail);
            foto = itemView.findViewById(R.id.circleImageViewFotoPerfilContato);
            //Só dá para usar o find depois de fazer o inflate
        }
    }
}
