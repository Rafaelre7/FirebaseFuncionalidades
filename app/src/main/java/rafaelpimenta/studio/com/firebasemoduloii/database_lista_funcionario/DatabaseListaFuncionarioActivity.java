package rafaelpimenta.studio.com.firebasemoduloii.database_lista_funcionario;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import rafaelpimenta.studio.com.firebasemoduloii.R;
import rafaelpimenta.studio.com.firebasemoduloii.database_lista_empresa.Empresa;
import rafaelpimenta.studio.com.firebasemoduloii.util.DialogAlerta;
import rafaelpimenta.studio.com.firebasemoduloii.util.DialogProgress;
import rafaelpimenta.studio.com.firebasemoduloii.util.PdfCreator;
import rafaelpimenta.studio.com.firebasemoduloii.util.Util;

public class DatabaseListaFuncionarioActivity extends AppCompatActivity implements View.OnClickListener, RecyclerView_ListaFuncionario.ClickFuncionario {

    private LinearLayout linearLayout;
    private ImageView imageView_LimparCampos;

    private EditText editText_Nome;
    private EditText editText_Idade;

    private Button button_Salvar;
    private ImageView imageView_Galeria;

    private RecyclerView recyclerView;
    private RecyclerView_ListaFuncionario recyclerView_listaFuncionario;
    private List<Funcionario> funcionarios = new ArrayList<Funcionario>();

    private FirebaseDatabase database;
    private FirebaseStorage storage;

    private Uri uri_imagem = null;

    private ChildEventListener childEventListener;
    private DatabaseReference database_reference;
    private List<String> keys = new ArrayList<String>();

    private Empresa empresa;
    private DialogProgress progress;
    private boolean imagem_selecionada = false;
    private boolean firebase_offline = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.database_lista_funcionario_activity);

        inicializarComponentes();

        imageView_LimparCampos.setOnClickListener(this);
        button_Salvar.setOnClickListener(this);
        imageView_Galeria.setOnClickListener(this);

        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();

        //Pegando os objetos pela intent
        empresa = getIntent().getParcelableExtra("empresa");

        ativarFirebaseOffLine();
        iniciarRecyclerView();
    }


    private void inicializarComponentes() {

        linearLayout = findViewById(R.id.linearLayout_Database_Funcionario);
        imageView_LimparCampos = findViewById(R.id.imageView_Database_Funcionario_LimparCampos);

        editText_Nome = findViewById(R.id.edirText_Database_Funcionario_Nome);
        editText_Idade = findViewById(R.id.edirText_Database_Funcionario_Idade);

        button_Salvar = findViewById(R.id.button_Database_Funcionario_Salvar);
        imageView_Galeria = findViewById(R.id.imageView_Database_Funcionario_Imagem);

        recyclerView = findViewById(R.id.recyclerView_Database_Funcionario_Lista);

    }

    //------------------------------INICIAR RECYCLER VIEW   ------------------------------

    private void iniciarRecyclerView() {
    /*
        //DADOS MOCADOS
        Funcionario funcionario1 = new Funcionario("1", "Rafael", 22,
                "https://firebasestorage.googleapis.com/v0/b/fir-moduloii.appspot.com/o/upload%2Fimagens%2FCursoFirebaseUpload1562869004636.jpg?alt=media&token=5d4dc571-37c6-4331-8d65-102338df0f8c");

        Funcionario funcionario2 = new Funcionario("2", "Pimenta", 45,
                "https://firebasestorage.googleapis.com/v0/b/fir-moduloii.appspot.com/o/upload%2Fimagens%2FCursoFirebaseUpload1562869325627.jpg?alt=media&token=07325257-0e70-4e08-b2a0-6229ad6c31ec");

        funcionarios.add(funcionario1);
        funcionarios.add(funcionario2);
*/

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView_listaFuncionario = new RecyclerView_ListaFuncionario(getBaseContext(), funcionarios, this);

        recyclerView.setAdapter(recyclerView_listaFuncionario);
    }

    //------------------------------TRATAMENTO DE CLICKS------------------------------
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imageView_Database_Funcionario_LimparCampos:
                limparCampos();
                break;
            case R.id.button_Database_Funcionario_Salvar:
                buttonSalvar();

                break;
            case R.id.imageView_Database_Funcionario_Imagem:
                obterImagem_Galeria();
                break;
        }
    }

    private void buttonSalvar() {


        String nome = editText_Nome.getText().toString();
        String idade_string = editText_Idade.getText().toString();


        //Valida os campos e acesso a internet
        if (Util.verificarCampos(getBaseContext(), nome, idade_string)) {

            int idade = Integer.parseInt(idade_string);


            if (imagem_selecionada) {
                salvarDadosStorage(nome, idade);
            } else {
                DialogAlerta alerta = new DialogAlerta("Imagem - Erro", "É obrigatorio escolher uma imagem para salvar os dados do funcionario");
                alerta.show(getSupportFragmentManager(), "1");
            }

        }

    }

    //------------------------------LIMPAR DADOS------------------------------
    private void limparCampos() {

        editText_Nome.setText("");
        editText_Idade.setText("");
        uri_imagem = null;
        imagem_selecionada = false;

        imageView_Galeria.setImageResource(R.drawable.ic_galeria_24dp);
    }

    //------------------------------MENU------------------------------


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_database_lista_funcionario, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_esconder_layout:
                linearLayout.setVisibility(View.GONE);
                return true;

            case R.id.item_mostrar_layout:
                linearLayout.setVisibility(View.VISIBLE);
                return true;
            case R.id.item_criar_pdf_funcionarios:
                itemCriarPdf();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void itemCriarPdf() {
        //verifica se tem funcionarios cadastrados
        if (funcionarios.size() > 0) {

            new GerarPDF().execute();


        } else {
            DialogAlerta alerta = new DialogAlerta("Erro ao gerar PDF", "Não existem funcionarios para gerar o Relatório PDF");
            alerta.show(getSupportFragmentManager(), "1");

        }
    }


    //--------------------GERAR PDF---------------------
    private void gerarPDF() throws IOException, DocumentException {
        File diretorio = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

        String nome_arquivo = diretorio.getPath() + "/" + "RelatorioFuncionarios" + System.currentTimeMillis() + ".pdf";

        File pdf = new File(nome_arquivo);

        OutputStream outputStream = new FileOutputStream(pdf);


        Document document = new Document();

        PdfCreator event = new PdfCreator();
        PdfWriter writer = PdfWriter.getInstance(document, outputStream);

        writer.setBoxSize("box_a", new Rectangle(36, 54, 559, 788));
        writer.setPageEvent(event);

        document.open();

        Font font = new Font(Font.FontFamily.HELVETICA, 20, Font.BOLD);
        Font font_dados = new Font(Font.FontFamily.HELVETICA, 20, Font.NORMAL);

        Paragraph paragraph = new Paragraph("Relatório de Funcionarios " + empresa.getNome(), font);
        paragraph.setAlignment(Element.ALIGN_CENTER);

        document.add(paragraph);

        PdfPTable table = new PdfPTable(1);
        table.setWidthPercentage(100);
        table.setSpacingBefore(30f);
        table.setSpacingAfter(30f);


        for (Funcionario funcionario : funcionarios) {
            String dados = "Nome: " + funcionario.getNome() + "\n\nIdade: " + funcionario.getIdade();

            PdfPCell cell = new PdfPCell(new Paragraph(dados, font_dados));

            cell.setPadding(10);
            //Falar se quer borda ou não
//            cell.setBorder(Rectangle.NO_BORDER);
//            cell.setBorder(PdfPCell.NO_BORDER);

            table.addCell(cell);

        }

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

    @Override
    public void click_Funcionario(Funcionario funcionario) {
//        Util.alert(getBaseContext(), "Nome: " + funcionario.getNome() + "\n\nIdade: " + funcionario.getIdade());

        funcionario.setId_empresa(empresa.getId());

        Intent intent = new Intent(getBaseContext(), DatabaseListaFuncionarioDadosActivity.class);

        intent.putExtra("funcionario",funcionario);

        startActivity(intent);
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
                            imagem_selecionada = true;
                            return false;
                        }
                    }).into(imageView_Galeria);
                } else {
                    Util.alert(getBaseContext(), "Falha ao selecionar imagem");
                }

            }
        }
    }

    //-------------------------------SALVAR DADOS------------------------------
    private void salvarDadosStorage(final String nome, final int idade) {

        progress = new DialogProgress();
        progress.show(getSupportFragmentManager(), "2");

        StorageReference reference = storage.getReference().child("BD").child("Empresas")
                .child(empresa.getNome());

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

                                    salvarDadosDatabase(nome, idade, url_imagem);

                                    Util.alert(getBaseContext(), "Sucesso ao realizar upload");

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

    private void salvarDadosDatabase(String nome, int idade, String urlImagem) {

        progress = new DialogProgress();
        progress.show(getSupportFragmentManager(), "2");

        Funcionario funcionario = new Funcionario(nome, idade, urlImagem);

        DatabaseReference databaseReference = database.getReference().child("BD").child("Empresas")
                .child(empresa.getId()).child("Funcionarios");

        databaseReference.push().setValue(funcionario).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {

                    Util.alert(getBaseContext(), "Sucesso ao realizar Upload - Database");
                    progress.dismiss();
                } else {

                    Util.alert(getBaseContext(), "Erro ao realizar Upload - Storage");
                    progress.dismiss();
                }

            }
        });
    }

    //------------------------------------OUVINTE------------------------------------
    private void ouvinte() {
        //Buscando no banco o usuario Flavio Augusto
//        Query busca1 =  database.getReference().child("BD").child("Empresas")
//                .child(empresa.getId()).child("Funcionarios").orderByChild("nome").equalTo("Flavio Augusto ");

        //Buscando no banco o usuario que comeca com a Letra "R"
//        Query busca1 =  database.getReference().child("BD").child("Empresas")
//                .child(empresa.getId()).child("Funcionarios").orderByChild("nome").startAt("R");

        //Buscando no banco o usuario com idade a partir de 45
//        Query busca1 =  database.getReference().child("BD").child("Empresas")
//                .child(empresa.getId()).child("Funcionarios").orderByChild("idade").startAt(45);

        //Buscando no banco o usuario com idade entre 20 e 50 anos
        Query busca1 =  database.getReference().child("BD").child("Empresas")
                .child(empresa.getId()).child("Funcionarios").orderByChild("idade").startAt(20).endAt(50);



        database_reference = database.getReference().child("BD").child("Empresas")
                .child(empresa.getId()).child("Funcionarios");

        if (childEventListener == null) {
            childEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    //pega o nome do (nó) que esta os dados
                    String key = dataSnapshot.getKey();

                    keys.add(key);

                    Funcionario funcionario = dataSnapshot.getValue(Funcionario.class);
                    funcionario.setId(key);

                    funcionarios.add(funcionario);

                    //notifica que foi alteradas informações dentro do recycler view
                    recyclerView_listaFuncionario.notifyDataSetChanged();

                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    String key = dataSnapshot.getKey();

                    int index = keys.indexOf(key);

                    Funcionario funcionario = dataSnapshot.getValue(Funcionario.class);

                    funcionario.setId(key);

                    funcionarios.set(index, funcionario);

                    recyclerView_listaFuncionario.notifyDataSetChanged();

                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                    String key = dataSnapshot.getKey();

                    int index = keys.indexOf(key);
                    funcionarios.remove(index);

                    keys.remove(index);

                    recyclerView_listaFuncionario.notifyItemRemoved(index);
                    recyclerView_listaFuncionario.notifyItemChanged(index, funcionarios.size());
                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            };
//            busca1.addChildEventListener(childEventListener);
            database_reference.addChildEventListener(childEventListener);
        }
    }


    //------------------------------------CICLOS DE VIDA------------------------------------
    @Override
    protected void onStart() {
        super.onStart();
        ouvinte();
    }


    //quando o app é fechado
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (childEventListener != null) {


            database_reference.removeEventListener(childEventListener);
        }
    }

    private class GerarPDF extends AsyncTask<Void, Void, Void> {

        private DialogProgress dialogProgress;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialogProgress = new DialogProgress();
            dialogProgress.show(getSupportFragmentManager(), "2");

        }


        @Override
        protected Void doInBackground(Void... voids) {
            try {
                gerarPDF();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (DocumentException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            dialogProgress.dismiss();
        }
    }

    private void ativarFirebaseOffLine() {

        try {
            if (!firebase_offline) {

                //Dando a permissao para trabalhar offline
                FirebaseDatabase.getInstance().setPersistenceEnabled(true);

                firebase_offline = true;

            } else {

                //firebase ja estiver funcionando offline

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}