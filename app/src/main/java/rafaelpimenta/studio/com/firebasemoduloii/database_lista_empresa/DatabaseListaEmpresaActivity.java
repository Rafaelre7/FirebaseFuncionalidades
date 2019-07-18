package rafaelpimenta.studio.com.firebasemoduloii.database_lista_empresa;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import rafaelpimenta.studio.com.firebasemoduloii.R;
import rafaelpimenta.studio.com.firebasemoduloii.database.Gerente;
import rafaelpimenta.studio.com.firebasemoduloii.database_lista_funcionario.DatabaseListaFuncionarioActivity;
import rafaelpimenta.studio.com.firebasemoduloii.util.Util;

public class DatabaseListaEmpresaActivity extends AppCompatActivity implements RecyclerView_ListaEmpresa.ClickEmpresa ,
Runnable{

    private RecyclerView recyclerView;
    private FirebaseDatabase database;

    private RecyclerView_ListaEmpresa recyclerView_listaEmpresa;
    private List<Empresa> empresas = new ArrayList<Empresa>();

    private ChildEventListener childEventListener;
    private DatabaseReference database_reference;
    private List<String> keys = new ArrayList<String>();
    private boolean firebase_offline = false;

    private Handler handler;
    private Thread thread;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.database_lista_empresa_activity);

//        inicializarComponentes();

        recyclerView = findViewById(R.id.recyclerView_Database_Empresa_Lista);
        database = FirebaseDatabase.getInstance();

        handler = new Handler();
        thread = new Thread(this);
        thread.start();

        ativarFirebaseOffLine();
        iniciarRecyclerView();

    }

    @Override
    public void run() {
            try{

                Thread.sleep(1000);

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        conexaoFirebaseBD();
                    }
                });
            }catch (InterruptedException e){

            }
    }
//    private void inicializarComponentes() {
//    }

    //------------------------------------INICIAR RECYCLERVIEW------------------------------------
    private void iniciarRecyclerView() {

        /*INSERIR DADOS MOCADOS
        Empresa empresa1 = new Empresa("Coca cola","0");
        Empresa empresa2 = new Empresa("Pepsi","1");
        empresas.add(empresa1);
        empresas.add(empresa2);*/


        //Como os intens serao mostrado dentro do recycler view
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView_listaEmpresa = new RecyclerView_ListaEmpresa(getBaseContext(), empresas, this);
        recyclerView.setAdapter(recyclerView_listaEmpresa);
    }

    //------------------------------------CLICK NO ITEM------------------------------------
    //Inteface para que possa ser utilizado o metodo da actity dentro do adpter
    @Override
    public void click_Empresa(Empresa empresa) {

        Intent intent = new Intent(getBaseContext(),DatabaseListaFuncionarioActivity.class);
        intent.putExtra("empresa",empresa);

        startActivity(intent);


//        startActivity(new Intent(getBaseContext(), DatabaseListaFuncionarioActivity.class));
//        Util.alert(getBaseContext(), "Nome: " + empresa.getNome() + "\nPasta: " + empresa.getId());
    }

    //------------------------------------OUVINTE------------------------------------
    private void ouvinte() {
        database_reference = database.getReference().child("BD").child("Empresas");

        if (childEventListener == null){
            childEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    //pega o nome do (nó) que esta os dados
                    String key = dataSnapshot.getKey();

                    keys.add(key);

                    Empresa empresa = dataSnapshot.getValue(Empresa.class);
                    empresa.setId(key);

                    empresas.add(empresa);

                    //notifica que foi alteradas informações dentro do recycler view
                    recyclerView_listaEmpresa.notifyDataSetChanged();

                    //keys 0 = coca cola
                    //empresas 0 = coca cola
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    String key = dataSnapshot.getKey();

                    int index = keys.indexOf(key);

                    Empresa empresa = dataSnapshot.getValue(Empresa.class);

                    empresa.setId(key);

                    empresas.set(index,empresa);

                    recyclerView_listaEmpresa.notifyDataSetChanged();

                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                    String key = dataSnapshot.getKey();

                    int index = keys.indexOf(key);
                    empresas.remove(index);

                    keys.remove(index);

                    recyclerView_listaEmpresa.notifyItemRemoved(index);
                    recyclerView_listaEmpresa.notifyItemChanged(index,empresas.size());
                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            };
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

    private void conexaoFirebaseBD(){


        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(".info/connected");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                boolean conexao = dataSnapshot.getValue(Boolean.class);
                if (conexao){
                    Util.alert(getBaseContext(),"Com conexão com o BD");
                }else {

                    if(Util.statusInternet(getBaseContext())){

                        Util.alert(getBaseContext(),"BLOQUEIO AO ACESSAR O BD");

                    }
//                    Util.alert(getBaseContext(),"Sem conexão com o BD");

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}