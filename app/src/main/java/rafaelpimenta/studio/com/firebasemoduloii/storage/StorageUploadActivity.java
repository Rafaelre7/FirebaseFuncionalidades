package rafaelpimenta.studio.com.firebasemoduloii.storage;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import rafaelpimenta.studio.com.firebasemoduloii.R;
import rafaelpimenta.studio.com.firebasemoduloii.util.DialogAlerta;
import rafaelpimenta.studio.com.firebasemoduloii.util.DialogProgress;
import rafaelpimenta.studio.com.firebasemoduloii.util.Permissao;
import rafaelpimenta.studio.com.firebasemoduloii.util.Util;

public class StorageUploadActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView imageView;
    private Button button_Enviar;
    private Uri uri_imagem;
    private FirebaseStorage storage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.storage_upload_activity);

        inicializarComponentes();

        button_Enviar.setOnClickListener(this);
        storage = FirebaseStorage.getInstance();
        permissao();
    }

    //--------------------PERMISSAO DO USUARIO----------------------

    private void permissao() {

        String permissoes[] = new String[]{
                Manifest.permission.CAMERA,
        };

        Permissao.permissao(this, 0, permissoes);
    }

    private void inicializarComponentes() {

        imageView = findViewById(R.id.imageView_StorageUpload);
        button_Enviar = findViewById(R.id.button_StorageUpload_Enviar);


    }

    //-------------------------------TRATAMENTO DE CLICKS------------------------------
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_StorageUpload_Enviar:
//                Util.alert(getBaseContext(), "Button_storage_enviar");

                button_upload();
                break;
        }
    }


    private void button_upload() {

        if (Util.statusInternet(getBaseContext())) {

            if (imageView.getDrawable() != null) {
                //upload_Imagem_1();
                upload_Imagem_2();
            } else {
                Util.alert(getBaseContext(), "Não existe imagem para realizar o upload");
            }

        } else {
            Util.alert(getBaseContext(), "Erro de conexão - verifique se o seu WIFI ou 3G está funcionando");
        }


    }

    //-------------------------------UPLOAD DE IMAGENS------------------------------
    private void upload_Imagem_1() {

        final DialogProgress dialogProgress = new DialogProgress();
        dialogProgress.show(getSupportFragmentManager(), "");
        StorageReference reference = storage.getReference().child("upload").child("imagens");

        StorageReference nome_imagem = reference.child("CursoFirebaseUpload" + System.currentTimeMillis() + ".jpg");

        BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();

        Bitmap bitmap = drawable.getBitmap();

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();

        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

        UploadTask uploadTask = nome_imagem.putBytes(bytes.toByteArray());

        uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    dialogProgress.dismiss();
                    Util.alert(getBaseContext(), "Sucesso ao realizar upload");
                } else {
                    dialogProgress.dismiss();
                    Util.alert(getBaseContext(), "Erro ao realizar upload");
                }
            }
        });
    }


    private void upload_Imagem_2() {

        final DialogProgress dialogProgress = new DialogProgress();
        dialogProgress.show(getSupportFragmentManager(), "");
        StorageReference reference = storage.getReference().child("upload").child("imagens");

        final StorageReference nome_imagem = reference.child("CursoFirebaseUpload" + System.currentTimeMillis() + ".jpg");

        Glide.with(getBaseContext()).asBitmap().load(uri_imagem).apply(new RequestOptions().override(1024, 768))
                .listener(new RequestListener<Bitmap>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {

                        Util.alert(getBaseContext(), "Erro ao transformar imagem");
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {

                        ByteArrayOutputStream bytes = new ByteArrayOutputStream();

                        resource.compress(Bitmap.CompressFormat.JPEG, 70, bytes);

                        ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes.toByteArray());

                        try {
                            bytes.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        UploadTask uploadTask = nome_imagem.putStream(inputStream);

                        uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                            @Override
                            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                return nome_imagem.getDownloadUrl();
                            }
                        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                if (task.isSuccessful()) {
                                    dialogProgress.dismiss();

                                    Uri uri = task.getResult();
                                    String url_imagem = uri.toString();

                                    DialogAlerta alerta = new DialogAlerta("URL IMAGEM", url_imagem);
                                    alerta.show(getSupportFragmentManager(), "3");

                                    Util.alert(getBaseContext(), "Sucesso ao realizar upload");

                                } else {
                                    Util.alert(getBaseContext(), "Erro ao realizar upload");
                                }
                            }
                        });

                        return false;
                    }
                }).submit();

    }
    //-------------------------------MENU------------------------------

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_storage_upload, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.item_galeria:
//                Util.alert(getBaseContext(),"item_galeria");
                obterImagem_Galeria();
                break;
            case R.id.item_camera:
//                Util.alert(getBaseContext(), "item_camera");
                item_camera();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //-------------------------------TRATAMENTO DE ERROS------------------------------
    private void item_camera() {
        if (ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_DENIED) {
            DialogAlerta dialogAlerta = new DialogAlerta("Permissão necesaria", "Acess as configurações do aplicativo " +
                    "para poder utilizar a campera no aplicativo.");

            dialogAlerta.show(getSupportFragmentManager(), "1");
        } else {
            obterImagem_Camera();
        }
    }

    //-------------------------------OBTER CAMERA------------------------------
    private void obterImagem_Camera() {

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        File diretorio = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        String nomeImagem = diretorio.getPath() + "/" + "CursoImagem" + System.currentTimeMillis() + ".jpg";

        File file = new File(nomeImagem);

        String autorizacao = "rafaelpimenta.studio.com.firebasemoduloii";

        uri_imagem = FileProvider.getUriForFile(getBaseContext(), autorizacao, file);

        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri_imagem);

        startActivityForResult(intent, 1);
    }

    //-------------------------------OBTER GALERIA------------------------------
    private void obterImagem_Galeria() {

        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);

        startActivityForResult(Intent.createChooser(intent, "Escolha uma imagem"), 0);

    }

    //-------------------------------RESPOSTAS DE COMUNICAÇÃO------------------------------
    //responsavel de receber oque a galeria respondeu
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == 0) { //RESPOSTA DA GALERIA

                if (data != null) {//CONTEUDO DA ESCOLHA DA IMAGEM DA GALERIA
                    //uri armazena idenficacao e caminhao da imagem
                    uri_imagem = data.getData();
                    Glide.with(getBaseContext()).asBitmap().load(uri_imagem).listener(new RequestListener<Bitmap>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                            Util.alert(getBaseContext(), "Falha ao selecionar imagem");
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                            return false;
                        }
                    }).into(imageView);
                } else {
                    Util.alert(getBaseContext(), "Falha ao selecionar imagem");
                }
            } else if (requestCode == 1) {//RESPOSTA DA CAMERA
                if (uri_imagem != null) {//VERIFICAR RESPOSTA CAMERA
                    Glide.with(getBaseContext()).asBitmap().load(uri_imagem).listener(new RequestListener<Bitmap>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                            Util.alert(getBaseContext(), "Erro ao carregar imagem");
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                            return false;
                        }
                    }).into(imageView);
                } else {
                    Util.alert(getBaseContext(), "Falha ao capturar imagem");
                }
            }

        }
    }


    //saber o resultado das permissoes
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        for (int result : grantResults) {
            if (result == PackageManager.PERMISSION_DENIED) {
                Toast.makeText(this, "Aceite as permissões para o aplicativo acessar sua camera", Toast.LENGTH_LONG).show();
                finish();

                break;
            }
        }
    }
}
