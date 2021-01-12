package com.yagocurvello.instam.fragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.yagocurvello.instam.R;
import com.yagocurvello.instam.activity.EditarPerfilActivity;
import com.yagocurvello.instam.activity.FiltroActivity;
import com.yagocurvello.instam.helper.ConfigFirebase;
import com.yagocurvello.instam.helper.Permissao;
import com.yagocurvello.instam.helper.UsuarioFirebase;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;

public class PostarFragment extends Fragment {

    private Button galeria, camera;
    private final static int SELECAO_CAMERA = 100, SELECAO_GALERIA = 200;
    private String[] permissoesNecessarias = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_postar,container,false);

        Permissao.ValidarPermissoes(permissoesNecessarias, getActivity(), 1);

        galeria = view.findViewById(R.id.buttonGaleria);
        camera = view.findViewById(R.id.buttonCamera);

        galeria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                if (in.resolveActivity(getActivity().getPackageManager()) != null){
                    startActivityForResult(in,SELECAO_GALERIA);
                }
            }
        });

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (i.resolveActivity(getActivity().getPackageManager()) != null){
                    startActivityForResult(i,SELECAO_CAMERA);
                }
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode,int resultCode,@Nullable Intent data) {
        super.onActivityResult(requestCode,resultCode,data);

        if (resultCode == getActivity().RESULT_OK){
            Bitmap imagem = null;

            try {
                switch (requestCode){
                    //Caso a imagem venha da galeria
                    case SELECAO_GALERIA:
                        Uri localImagemSelecionada = data.getData();

                        //Configura imagem na tela
                        imagem = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), localImagemSelecionada);

                        if (imagem != null){

                            savePicture(imagem.toString(), imagem, getActivity());
                            Intent intent = new Intent(getActivity(), FiltroActivity.class);
                            intent.putExtra("caminhoFoto", imagem.toString());
                            startActivity(intent);
                        }
                        break;

                    case SELECAO_CAMERA:

                        imagem = (Bitmap) data.getExtras().get("data");
                        savePicture("caminhoFoto", imagem, getActivity());
                        break;
                }

            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private void savePicture(String filename, Bitmap b, Context ctx){
        try {
            ObjectOutputStream oos;
            FileOutputStream out;// = new FileOutputStream(filename);
            out = ctx.openFileOutput(filename, Context.MODE_PRIVATE);
            oos = new ObjectOutputStream(out);
            b.compress(Bitmap.CompressFormat.PNG, 100, oos);

            oos.close();
            oos.notifyAll();
            out.notifyAll();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}