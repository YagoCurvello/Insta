package com.yagocurvello.instam.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.yagocurvello.instam.R;
import com.yagocurvello.instam.model.Usuario;
import com.zomato.photofilters.utils.ThumbnailItem;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class FiltroAdapter extends RecyclerView.Adapter<FiltroAdapter.MyViewHolder> {

    private Context context;
    private List<ThumbnailItem> listaFiltros;

    public FiltroAdapter(List<ThumbnailItem> listaFiltros, Context context) {
        this.listaFiltros = listaFiltros;
        this.context = context;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent,int viewType) {

        View itemLista = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_filtro, parent, false);

        return new MyViewHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder,int position) {

        ThumbnailItem item = listaFiltros.get(position);
        holder.nome.setText(item.filterName);
        holder.foto.setImageBitmap(item.image);


    }

    @Override
    public int getItemCount() {
        return listaFiltros.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView nome;
        ImageView foto;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            nome = itemView.findViewById(R.id.textFiltroName);
            foto = itemView.findViewById(R.id.imageFotoFiltro);
            //Só dá para usar o find depois de fazer o inflate
        }
    }
}
