package rafaelpimenta.studio.com.firebasemoduloii.database_lista_funcionario;

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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.ListItem;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rafaelpimenta.studio.com.firebasemoduloii.R;
import rafaelpimenta.studio.com.firebasemoduloii.database.Gerente;
import rafaelpimenta.studio.com.firebasemoduloii.util.DialogAlerta;
import rafaelpimenta.studio.com.firebasemoduloii.util.DialogProgress;
import rafaelpimenta.studio.com.firebasemoduloii.util.Util;

public class DatabaseListaFuncionarioDadosActivity extends AppCompatActivity implements View.OnClickListener {


    private ImageView imageView;
    private ProgressBar progressBar;

    private EditText editText_Nome;
    private EditText editText_Idade;

    private Button button_Alterar;
    private Button button_Remover;

    private Funcionario funcionarioSelecioado;

    private Uri uri_imagem = null;
    private boolean imagem_Alterada = false;

    private FirebaseDatabase database;
    private FirebaseStorage storage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.database_lista_funcionario_dados_activity);

        inicializarComponentes();

        imageView.setOnClickListener(this);
        button_Remover.setOnClickListener(this);
        button_Alterar.setOnClickListener(this);

        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();

        funcionarioSelecioado = getIntent().getParcelableExtra("funcionario");

        carregarDadosFuncionario();

    }

    //----------------------------------------------- CARREGAR DADOS -----------------------------------------------
    private void carregarDadosFuncionario() {

        progressBar.setVisibility(View.VISIBLE);

        editText_Nome.setText(funcionarioSelecioado.getNome());
        editText_Idade.setText(funcionarioSelecioado.getIdade() + "");//SEMPRE QUE FOR INTEIRO CONCATENAR COM STRING VAZIA

        Picasso.with(getBaseContext()).load(funcionarioSelecioado.getUrlImagem()).into(imageView, new Callback() {
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

    //----------------------------------------------- INICIALIZA COMPONENTES -----------------------------------------------
    private void inicializarComponentes() {

        imageView = findViewById(R.id.imageView_Database_Dados_Funcionario);
        progressBar = findViewById(R.id.progressBar_Database_Dados_Funcionario);

        editText_Nome = findViewById(R.id.editText_Database_Dados_Funcionario_Nome);
        editText_Idade = findViewById(R.id.editText_Database_Dados_Funcionario_Idade);

        button_Alterar = findViewById(R.id.button_Database_dados_Funcionario_Alterar);
        button_Remover = findViewById(R.id.button_Database_dados_Funcionario_Remover);
    }

    //----------------------------------------------- TRATAMENTO DE CLICKS -----------------------------------------------
    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.imageView_Database_Dados_Funcionario:
                obterImagem_Galeria();
                break;
            case R.id.button_Database_dados_Funcionario_Alterar:

               buttonAlterar();
                break;
            case R.id.button_Database_dados_Funcionario_Remover:

                buttonRemover();

                break;

        }
    }

    private void buttonRemover() {

        if(Util.statusInternet(getBaseContext())){
            removerFuncionarioImagem();
        }else {
            Util.alert(getBaseContext(),"Sem conexão com a Internet");
        }

    }

    private void buttonAlterar() {

        String nome = editText_Nome.getText().toString();
        String idade_String = editText_Idade.getText().toString();

        if(Util.verificarCampos(getBaseContext(),nome,idade_String)){

            int idade = Integer.parseInt(idade_String);

            if (!nome.equals(funcionarioSelecioado.getNome())
                    || idade != funcionarioSelecioado.getIdade()
                    || imagem_Alterada){

                if (imagem_Alterada) {
                    //alterar imagem
                    removerImagem(nome, idade);

                } else {
                    //alterar dados
                    alterarDados(nome, idade, funcionarioSelecioado.getUrlImagem());
                }
            }else {
                DialogAlerta alerta = new DialogAlerta("Erro", "Nenhuma informação foi alterada para poder salvar no Banco de Dados");
                alerta.show(getSupportFragmentManager(),"2");
            }

        }else {

        }




    }

    //----------------------------------------------- TRATAMENTO DE REMOÇÃO DE DADOS -----------------------------------------------
    private void removerFuncionarioImagem() {

        String url = funcionarioSelecioado.getUrlImagem();

        StorageReference reference = FirebaseStorage.getInstance().getReferenceFromUrl(url);

        reference.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()) {

                      removerFuncionario();
//                    Util.alert(getBaseContext(),"Sucesso ao remover a imagem");
//                    progressBar.setVisibility(View.GONE);
                } else {
                    Util.alert(getBaseContext(), "Erro ao remover a imagem");
//                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }

    private void removerFuncionario() {

//        dialogProgress = new DialogProgress();
//
//        dialogProgress.show(getSupportFragmentManager(),"1");

        DatabaseReference reference = database.getReference().child("BD").child("Empresas")
                .child(funcionarioSelecioado.getId_empresa()).child("Funcionarios");

        reference.child(funcionarioSelecioado.getId()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Util.alert(getBaseContext(), "Sucesso ao Remover Funcionario");
                    finish();
//                    dialogProgress.dismiss();
//                    editText_NomePasta.setText("");
                } else {
                    Util.alert(getBaseContext(), "Erro ao Remover Funcionario");
                }
            }
        });
    }

    //-------------------------------TRATAMENTO DE ALTERAÇÃO DE DADOS------------------------------
    private void removerImagem(final String nome, final int idade) {

        final DialogProgress progress = new DialogProgress();
        progress.show(getSupportFragmentManager(),"1");

        String url = funcionarioSelecioado.getUrlImagem();

        StorageReference reference = FirebaseStorage.getInstance().getReferenceFromUrl(url);

        reference.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()) {

                    progress.dismiss();
                    salvarDadoStorage(nome, idade);

//                    Util.alert(getBaseContext(),"Sucesso ao remover a imagem");
//                    progressBar.setVisibility(View.GONE);
                } else {
                    Util.alert(getBaseContext(), "Erro ao remover a imagem");
//                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }

    private void salvarDadoStorage(final String nome, final int idade) {

        final DialogProgress progress = new DialogProgress();
        progress.show(getSupportFragmentManager(),"1");

        StorageReference reference = storage.getReference().child("BD").child("Empresas")
                .child(funcionarioSelecioado.getId_empresa());

        final StorageReference nome_imagem = reference.child("CursoFirebase" + System.currentTimeMillis() + ".jpg");

        Glide.with(getBaseContext()).asBitmap().load(uri_imagem).apply(new RequestOptions().override(1024, 768))
                .listener(new RequestListener<Bitmap>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {

                        Util.alert(getBaseContext(), "Erro ao transformar imagem");

                        progress.dismiss();
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

                                    progress.dismiss();
                                    Uri uri = task.getResult();
                                    String url_imagem = uri.toString();

                                    alterarDados(nome, idade, url_imagem);

//                                    Util.alert(getBaseContext(), "Sucesso ao realizar upload");

                                } else {

                                    progress.dismiss();

                                    Util.alert(getBaseContext(), "Erro ao realizar upload");

                                }
                            }
                        });

                        return false;
                    }
                }).submit();
    }

    //----------------------------------------------- IMPLEMENTAR MENU -----------------------------------------------

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_storage_download, menu);

        return super.onCreateOptionsMenu(menu);
    }

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
                            //verifica se a imagem foi selecionada
                            imagem_Alterada = true;
                            return false;
                        }
                    }).into(imageView);
                } else {
                    Util.alert(getBaseContext(), "Falha ao selecionar imagem");
                }

            }
        }
    }

    private void alterarDados(String nome, int idade, String url_imagem) {

        final DialogProgress progress = new DialogProgress();
        progress.show(getSupportFragmentManager(),"1");

        DatabaseReference reference = database.getReference().child("BD").child("Empresas")
                .child(funcionarioSelecioado.getId_empresa()).child("Funcionarios");

        Funcionario funcionario = new Funcionario(nome, idade, url_imagem);

        Map<String, Object> atualizacao = new HashMap<>();

        atualizacao.put(funcionarioSelecioado.getId(), funcionario);

        reference.updateChildren(atualizacao).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {

                    progress.dismiss();
                    Util.alert(getBaseContext(), "Sucesso ao Alterar Dados");
                    finish();

                } else {

                    progress.dismiss();
                    Util.alert(getBaseContext(), "Erro ao Alterar Dados");
                }
            }
        });

    }

    private void item_Compartilhar() {
        if (imageView.getDrawable() != null) {
            compartilhar();
        } else {
            DialogAlerta alerta = new DialogAlerta("Erro de Compartilhamento", "Não existe nenhuma imagem para Compartilhar");
            alerta.show(getSupportFragmentManager(), "1");
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

        Paragraph paragraph = new Paragraph("Dados Funcionario - "+funcionarioSelecioado.getNome(), font);
        paragraph.setAlignment(Element.ALIGN_CENTER);



        ListItem item = new ListItem();

        item.add(paragraph);

        document.add(item);

        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setSpacingBefore(25f);

        BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();

        Bitmap bitmap = drawable.getBitmap();

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();

        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

        //Recebe a imagem em bytes
        Image image = Image.getInstance(bytes.toByteArray());
        image.scaleAbsolute(100f, 100f);

        image.setAlignment(Element.ALIGN_CENTER);

//        image.setRotationDegrees(10f);

        table.addCell(image);

        String dados = "Nome: "+funcionarioSelecioado.getNome()+"\nIdade: "+funcionarioSelecioado.getIdade();

        Font font_dados = new Font(Font.FontFamily.HELVETICA, 30, Font.BOLD);
        PdfPCell cell = new PdfPCell(new Paragraph(dados,font_dados));

        cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setBorder(PdfPCell.NO_BORDER);

        table.getDefaultCell().setBorder(PdfPCell.NO_BORDER);
        table.addCell(cell);

        document.add(table);
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


}