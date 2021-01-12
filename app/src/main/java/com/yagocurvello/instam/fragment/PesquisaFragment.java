package com.yagocurvello.instam.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.yagocurvello.instam.R;
import com.yagocurvello.instam.activity.PerfilAmigoActivity;
import com.yagocurvello.instam.adapter.ContatosAdapter;
import com.yagocurvello.instam.adapter.FiltroAdapter;
import com.yagocurvello.instam.helper.ConfigFirebase;
import com.yagocurvello.instam.helper.RecyclerItemClickListener;
import com.yagocurvello.instam.helper.UsuarioFirebase;
import com.yagocurvello.instam.model.Usuario;

import java.util.ArrayList;
import java.util.List;

public class PesquisaFragment extends Fragment {

    private List<Usuario> listausuarios;
    private List<Usuario> contatosListPesquisa;
    private ContatosAdapter adapterContatos;
    private RecyclerView recyclerPesquisa;
    private SearchView searchPesquisa;
    private ValueEventListener eventListenerContatos;
    private DatabaseReference databaseReference;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view =  inflater.inflate(R.layout.fragment_pesquisa,container,false);

        searchPesquisa = view.findViewById(R.id.searchPesquisa);
        recyclerPesquisa = view.findViewById(R.id.recyclerPesquisa);
        databaseReference = ConfigFirebase.getFirebaseDatabase().child("usuarios");
        listausuarios = new ArrayList<>();
        contatosListPesquisa = new ArrayList<>();

        //Configurar SearchView
        searchPesquisa.setQueryHint("Buscar Usuarios");
        searchPesquisa.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                pesquisarContatos(newText);
                return true;
            }
        });

        //Configurar Adapter
        adapterContatos = new ContatosAdapter(contatosListPesquisa, getActivity());
        recyclerPesquisa.setAdapter(adapterContatos);

        //Configurar RecycleView
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerPesquisa.setLayoutManager(layoutManager);
        recyclerPesquisa.setHasFixedSize(true);

        //Evento de click do RecyclerView
        recyclerPesquisa.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        getActivity(),
                        recyclerPesquisa,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view,int position) {
                                List<Usuario> usuarioListAtualizada = adapterContatos.getListContatos();
                                Usuario usuarioSelecionado = usuarioListAtualizada.get(position);
                                Intent i = new Intent(getActivity(), PerfilAmigoActivity.class);
                                i.putExtra("amigo", usuarioSelecionado);
                                startActivity(i);
                            }
                            @Override
                            public void onLongItemClick(View view,int position) {
                            }
                            @Override
                            public void onItemClick(AdapterView<?> adapterView,View view,int i,long l) {
                            }
                        })
        );

        return view;
    }

    public void pesquisarContatos (String texto){

        contatosListPesquisa.clear();

        if (texto.length() > 0){
            for ( Usuario usuario : listausuarios) {
                if (usuario.getNome().toLowerCase().contains(texto.toLowerCase())) {
                    contatosListPesquisa.add(usuario);
                }
            }

            adapterContatos = new ContatosAdapter(contatosListPesquisa, getActivity());
            recyclerPesquisa.setAdapter(adapterContatos);
            adapterContatos.notifyDataSetChanged();
        }

    }

    public void recuperarContatos(){

        eventListenerContatos = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                contatosListPesquisa.clear();

                for (DataSnapshot data : snapshot.getChildren()){
                    Usuario usuario = data.getValue(Usuario.class);
                    if (!UsuarioFirebase.getUsuarioAtual().getEmail().equals(usuario.getEmail()))
                    listausuarios.add(usuario);
                }

                adapterContatos.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        recuperarContatos();
    }

    @Override
    public void onStop() {
        super.onStop();
        databaseReference.removeEventListener(eventListenerContatos);
    }
}