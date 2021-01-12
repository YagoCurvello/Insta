package com.yagocurvello.instam.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.yagocurvello.instam.R;
import com.yagocurvello.instam.adapter.FiltroAdapter;
import com.yagocurvello.instam.helper.ConfigFirebase;
import com.yagocurvello.instam.helper.RecyclerItemClickListener;
import com.yagocurvello.instam.helper.UsuarioFirebase;
import com.yagocurvello.instam.model.Postagem;
import com.yagocurvello.instam.model.Usuario;
import com.zomato.photofilters.FilterPack;
import com.zomato.photofilters.imageprocessors.Filter;
import com.zomato.photofilters.utils.ThumbnailItem;
import com.zomato.photofilters.utils.ThumbnailsManager;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

public class FiltroActivity extends AppCompatActivity {

    static {
        System.loadLibrary("NativeImageProcessor");
    }

    private ImageView imageFotoEscolhida;
    private Bitmap imagem;
    private Bitmap imagemFiltro;
    private EditText textDescricao;
    private RecyclerView recyclerFiltro;
    private FiltroAdapter filtroAdapter;
    private List<ThumbnailItem> listaFiltros;
    private String idUsuarioLogado;
    private StorageReference storageReference;
    private DatabaseReference databaseReferenceUsuario;
    private DatabaseReference firebaseRef;
    private ValueEventListener eventListenerUsuario;
    private Usuario usuarioLogado;
    private AlertDialog dialog;
    private DataSnapshot seguidoresSnapshot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filtro);

        Toolbar toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle("Aplicar Filtros");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_24);

        listaFiltros = new ArrayList<>();
        imageFotoEscolhida = findViewById(R.id.imageFotoEscolhida);
        textDescricao = findViewById(R.id.textDescricao);
        recyclerFiltro = findViewById(R.id.recyclerFiltros);
        idUsuarioLogado = UsuarioFirebase.getIdUsuario();
        storageReference = ConfigFirebase.getFirebaseStorage();
        firebaseRef = ConfigFirebase.getFirebaseDatabase();



        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String caminhoFoto = bundle.getString("caminhoFoto");
            imagem = loadPicture(caminhoFoto);
            imageFotoEscolhida.setImageBitmap(imagem);
            imagemFiltro = imagem.copy(imagem.getConfig(), true);
        }

        //Configurar Adapter
        filtroAdapter = new FiltroAdapter(listaFiltros,getApplicationContext());
        recyclerFiltro.setAdapter(filtroAdapter);

        //Configurar RecycleView
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        recyclerFiltro.setLayoutManager(layoutManager);
        recyclerFiltro.setHasFixedSize(true);

        //Evento de click do Recyclerview
        recyclerFiltro.addOnItemTouchListener(new RecyclerItemClickListener(getApplicationContext(),recyclerFiltro,new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view,int position) {
                ThumbnailItem item = listaFiltros.get(position);
                imagemFiltro = imagem.copy(imagem.getConfig(), true);
                Filter filter = item.filter;
                imageFotoEscolhida.setImageBitmap(filter.processFilter(imagemFiltro));
            }

            @Override
            public void onLongItemClick(View view,int position) {

            }

            @Override
            public void onItemClick(AdapterView<?> adapterView,View view,int i,long l) {

            }
        }));

        recuperaFiltros();

    }

    private void recuperaFiltros() {
        listaFiltros.clear();
        ThumbnailsManager.clearThumbs();

        //Configurar um filtro normal
        ThumbnailItem item = new ThumbnailItem();
        item.image = imagem;
        item.filterName = "Normal";
        ThumbnailsManager.addThumb(item);

        //Lista todos os filtros
        List<Filter> filters = FilterPack.getFilterPack(getApplicationContext());
        for (Filter filter : filters) {
            ThumbnailItem itemFiltro = new ThumbnailItem();
            itemFiltro.image = imagem;
            itemFiltro.filter = filter;
            itemFiltro.filterName = filter.getName();
            ThumbnailsManager.addThumb(itemFiltro);
        }

        listaFiltros.addAll(ThumbnailsManager.processThumbs(getApplicationContext()));
        filtroAdapter.notifyDataSetChanged();
    }

    //Recuperar foto
    private Bitmap loadPicture(String filename) {
        Bitmap b = null;

        try {
            FileInputStream fis = openFileInput(filename);
            ObjectInputStream ois = null;
            try {
                ois = new ObjectInputStream(fis);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            b = BitmapFactory.decodeStream(ois);
            try {
                ois.close();
                fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return b;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_filtro,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menuPublicar:
                publicarPostagem();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }

    private void abrirCarregamento (String titulo){

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(titulo);
        alert.setCancelable(false);
        alert.setView(R.layout.carregamento);

        dialog = alert.create();
        dialog.show();

    }

    private void publicarPostagem(){
        abrirCarregamento ("Salvando postagem");

        final Postagem postagem = new Postagem();
        postagem.setIdUsuario(idUsuarioLogado);
        postagem.setDescricao(textDescricao.getText().toString());

        //Recupera os dados para salvar no firebase
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        imagemFiltro.compress(Bitmap.CompressFormat.JPEG, 70, baos);
        byte[] dadosImagem = baos.toByteArray();

        //Salvar imagem para o firebase
        final StorageReference postagemRef = storageReference = ConfigFirebase.getFirebaseStorage()
                .child("imagens")
                .child("postagens")
                .child(idUsuarioLogado)
                .child(postagem.getId() + ".jpeg");

        UploadTask uploadTask = postagemRef.putBytes(dadosImagem);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(FiltroActivity.this,
                        "Falha ao salvar Imagem", Toast.LENGTH_SHORT).show();

            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                //Recuperar Local da foto para salvar no usuario
                postagemRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        Uri url = task.getResult();

                        //Salvar caminho da foto
                        postagem.setCaminhoDaFoto(url.toString());

                        usuarioLogado.setPostagens(usuarioLogado.getPostagens()+1);
                        UsuarioFirebase.atualizarUsuarioPostagens(usuarioLogado);

                        if (postagem.salvar(seguidoresSnapshot)){
                            Toast.makeText(FiltroActivity.this,
                                    "Imagem salva com sucesso", Toast.LENGTH_SHORT).show();

                            dialog.cancel();
                            finish();
                        }
                    }
                });
            }
        });

    }

    private void recuperarDadosPostagem(){
        abrirCarregamento ("Recuperando dados do Usuario");

        databaseReferenceUsuario = ConfigFirebase.getFirebaseDatabase()
                .child("usuarios").child(idUsuarioLogado);
        eventListenerUsuario = databaseReferenceUsuario.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //Recupera dados do usuario
                usuarioLogado = snapshot.getValue(Usuario.class);
                //Recupera lista de seguidores
                DatabaseReference seguidoresRef = firebaseRef.child("seguidores").child(idUsuarioLogado);
                seguidoresRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        seguidoresSnapshot = snapshot;

                       dialog.cancel();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        recuperarDadosPostagem();
    }

    @Override
    protected void onStop() {
        super.onStop();
        databaseReferenceUsuario.removeEventListener(eventListenerUsuario);
    }
}