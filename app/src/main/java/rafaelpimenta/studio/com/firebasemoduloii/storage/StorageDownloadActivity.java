package rafaelpimenta.studio.com.firebasemoduloii.storage;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
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
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.ListItem;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfWriter;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.List;

import rafaelpimenta.studio.com.firebasemoduloii.R;
import rafaelpimenta.studio.com.firebasemoduloii.util.DialogAlerta;
import rafaelpimenta.studio.com.firebasemoduloii.util.Util;

public class StorageDownloadActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView imageView;
    private ProgressBar progressBar;
    private Button button_download, button_remover;
    private FirebaseStorage storage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.storage_download_activity);

        imageView = findViewById(R.id.imageView_StorageDownload);
        progressBar = findViewById(R.id.progressBar_StorageDownload);
        button_download = findViewById(R.id.button_StorageDownload_Download);
        button_remover = findViewById(R.id.button_StorageDownload_Remover);

        button_download.setOnClickListener(this);
        button_remover.setOnClickListener(this);

        //esconde progress bar
        progressBar.setVisibility(View.GONE);
    }

    //--------------------TRATAMENTO DE CLICKS---------------------
    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.button_StorageDownload_Download:
                button_Download();
                break;

            case R.id.button_StorageDownload_Remover:
//                Util.alert(this, "Remover");
                button_Remover();
                break;

        }
    }

    //--------------------TRATAMENTO DE ERROS--------------------
    private void button_Download() {
        if (Util.statusInternet(getBaseContext())) {
            download_imagem_2();
        } else {

            DialogAlerta alerta = new DialogAlerta("Erro de Conexao", "Verifique se sua conexão Wifi ou 3G está funcionando");
            alerta.show(getSupportFragmentManager(), "1");
        }
    }

    private void button_Remover() {
        if (Util.statusInternet(getBaseContext())) {
            remover_imagem_2();
        } else {

            DialogAlerta alerta = new DialogAlerta("Erro de Conexao", "Verifique se sua conexão Wifi ou 3G está funcionando");
            alerta.show(getSupportFragmentManager(), "1");
        }
    }

    //--------------------REMOÇÃO DE IMAGEM--------------------
    /*private void remover_imagem_1() {

        progressBar.setVisibility(View.VISIBLE);
        String url = "https://firebasestorage.googleapis.com/v0/b/fir-moduloii.appspot.com/o/imagem%2Flogo-02.png?alt=media&token=f9c56fe7-d569-43d3-93f1-deeb0c5adb31";

        StorageReference reference = FirebaseStorage.getInstance().getReferenceFromUrl(url);

        reference.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isSuccessful()){
                    imageView.setImageDrawable(null);

                    Util.alert(getBaseContext(),"Suceso ao remover a imagem");
                    progressBar.setVisibility(View.GONE);
                }else {
                    Util.alert(getBaseContext(),"Erro ao remover a imagem");
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }*/

    private void remover_imagem_2() {

        progressBar.setVisibility(View.VISIBLE);

        storage = FirebaseStorage.getInstance();

        StorageReference reference = storage.getReference().child("imagem").child("logo-02.png");

        reference.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()) {
                    imageView.setImageDrawable(null);

                    Util.alert(getBaseContext(), "Suceso ao remover a imagem");
                    progressBar.setVisibility(View.GONE);
                } else {
                    Util.alert(getBaseContext(), "Erro ao remover a imagem");
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }

    //--------------------DOWNLOAD DE IMAGEM--------------------
    private void download_imagem_1() {
        //Mostra progress bar
        progressBar.setVisibility(View.VISIBLE);

        String url = "https://firebasestorage.googleapis.com/v0/b/fir-moduloii.appspot.com/o/imagem%2Flogo-02.png?alt=media&token=f9c56fe7-d569-43d3-93f1-deeb0c5adb31";

        //Abrindo com picasso
        /*Picasso.with(getBaseContext()).load(url).into(imageView, new Callback() {
            @Override
            public void onSuccess() {

                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onError() {
                progressBar.setVisibility(View.GONE);

            }
        });
        */

        //Abrindo com glide
        Glide.with(getBaseContext()).asBitmap().load(url).listener(new RequestListener<Bitmap>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {

                progressBar.setVisibility(View.GONE);
                return false;
            }

            @Override
            public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {

                progressBar.setVisibility(View.GONE);
                return false;
            }
        }).into(imageView);
    }

    private void download_imagem_2() {

        progressBar.setVisibility(View.VISIBLE);

        storage = FirebaseStorage.getInstance();

        StorageReference reference = storage.getReference().child("imagem").child("logo-02.png");

        reference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    String url = task.getResult().toString();

                    Picasso.with(getBaseContext()).load(url).into(imageView, new Callback() {
                        @Override
                        public void onSuccess() {
                            progressBar.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError() {
                            progressBar.setVisibility(View.GONE);
                        }
                    });
                }
            }
        });
    }

    //--------------------CRIAR MENU--------------------

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_storage_download, menu);
        return super.onCreateOptionsMenu(menu);
    }

    //--------------------ITENS DE MENU SELECIONADO---------------------

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.item_compartilhar:
                item_Compartilhar();
                return true;
            case R.id.item_criar_pdf:
                item_GerarPDF();

                return true;
        }
        return super.onOptionsItemSelected(item);

    }
    //--------------------TRATAMENTO DE ERROS--------------------
    private void item_Compartilhar() {
        if (imageView.getDrawable() != null) {
            compartilhar();
        } else {
            DialogAlerta alerta = new DialogAlerta("Erro de Compartilhamento", "Não existe nenhuma imagem para Compartilhar");
            alerta.show(getSupportFragmentManager(), "1");
        }
    }

    private void item_GerarPDF() {
        if (imageView.getDrawable() != null) {
            try {
                gerarPDF();
            } catch (IOException e) {

            } catch (DocumentException e) {
                e.printStackTrace();
                e.printStackTrace();
            }
        } else {
            DialogAlerta alerta = new DialogAlerta("Erro ao Gerar PDF", "Não existe nenhuma imagem para gerar o PDF");
            alerta.show(getSupportFragmentManager(), "1");
        }
    }

    //--------------------GERAR PDF---------------------
    private void gerarPDF() throws IOException, DocumentException {
        File diretorio = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

        String nome_arquivo = diretorio.getPath() + "/" + "FirebaseCurso" + System.currentTimeMillis() + ".pdf";

        File pdf = new File(nome_arquivo);

        OutputStream outputStream = new FileOutputStream(pdf);


        Document document = new Document();
        PdfWriter writer = PdfWriter.getInstance(document, outputStream);

        writer.setBoxSize("firebase", new Rectangle(36, 54, 559, 788));

        document.open();

        Font font = new Font(Font.FontFamily.HELVETICA, 20, Font.BOLD);

        Paragraph paragraph = new Paragraph("Curso Firebase Módulo II", font);
        paragraph.setAlignment(Element.ALIGN_CENTER);

        Paragraph paragraph1 = new Paragraph("Rafael Pimenta", font);
        paragraph1.setAlignment(Element.ALIGN_LEFT);

        ListItem item = new ListItem();

        item.add(paragraph);
        item.add(paragraph1);

        document.add(item);

        BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();

        Bitmap bitmap = drawable.getBitmap();

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();

        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

        //Recebe a imagem em bytes
        Image image = Image.getInstance(bytes.toByteArray());
        image.scaleAbsolute(100f, 100f);

        image.setAlignment(Element.ALIGN_CENTER);

        image.setRotationDegrees(10f);
        document.add(image);

        document.close();

        visualizarPDF(pdf);

    }

    private void visualizarPDF(File pdf) {
        PackageManager packageManager = getPackageManager();

        Intent itent = new Intent(Intent.ACTION_VIEW);
        itent.setType("application/pdf");

        List<ResolveInfo> list = packageManager.queryIntentActivities(itent, PackageManager.MATCH_DEFAULT_ONLY);

        if (list.size() > 0) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            Uri uri = FileProvider.getUriForFile(getBaseContext(), "rafaelpimenta.studio.com.firebasemoduloii", pdf);

            intent.setDataAndType(uri, "application/pdf");

            startActivity(intent);
        } else {

            DialogAlerta dialogAlerta = new DialogAlerta("Erro ao Abrir PDF", "Não foi detectado nenhum leitor de PDF no seu dispositivo");
            dialogAlerta.show(getSupportFragmentManager(), "3");


        }
    }

    //--------------------COMPRARTILHAR IMAGEM---------------------
    private void compartilhar() {

        Intent intent = new Intent(Intent.ACTION_SEND);

        intent.setType("imaage/jpeg");

        BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();

        Bitmap bitmap = drawable.getBitmap();

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();

        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

        String path = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap,
                "Curso Firebase", null);

        Uri uri = Uri.parse(path);

        intent.putExtra(Intent.EXTRA_STREAM, uri);
        startActivity(Intent.createChooser(intent, "Compartilhar imagem Curso"));


    }

}