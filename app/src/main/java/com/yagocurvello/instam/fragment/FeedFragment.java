package com.yagocurvello.instam.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.yagocurvello.instam.R;
import com.yagocurvello.instam.adapter.FeedAdapter;
import com.yagocurvello.instam.helper.ConfigFirebase;
import com.yagocurvello.instam.helper.UsuarioFirebase;
import com.yagocurvello.instam.model.Feed;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class FeedFragment extends Fragment {

    private RecyclerView recyclerViewFeed;
    private FeedAdapter feedAdapter;
    private List<Feed> feedList = new ArrayList<>();
    private ValueEventListener eventListenerFeed;
    private DatabaseReference databaseReference;
    private DatabaseReference databaseReferenceFeed;
    private String idUsuarioLogado;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_feed,container,false);

        recyclerViewFeed = view.findViewById(R.id.recyclerFeed);
        idUsuarioLogado = UsuarioFirebase.getIdUsuario();
        databaseReference = ConfigFirebase.getFirebaseDatabase();
        databaseReferenceFeed = databaseReference.child("feed").child(idUsuarioLogado);

        //Configurar Adapter
        feedAdapter = new FeedAdapter(feedList, getActivity());
        recyclerViewFeed.setAdapter(feedAdapter);

        //Configurar RecyclerView
        recyclerViewFeed.setHasFixedSize(true);
        recyclerViewFeed.setLayoutManager(new LinearLayoutManager(getActivity()));


        return view;
    }

    private void recuperarFeed(){
        eventListenerFeed = databaseReferenceFeed.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    feedList.add(dataSnapshot.getValue(Feed.class));
                }
                Collections.reverse(feedList);
                feedAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        recuperarFeed();
    }

    @Override
    public void onStop() {
        super.onStop();
        databaseReferenceFeed.removeEventListener(eventListenerFeed );
    }
}