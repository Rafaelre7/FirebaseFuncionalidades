package rafaelpimenta.studio.com.firebasemoduloii.database;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import rafaelpimenta.studio.com.firebasemoduloii.R;
import rafaelpimenta.studio.com.firebasemoduloii.util.DialogAlerta;

public class DatabaseLerDadosctivity extends AppCompatActivity {

    private TextView textView_nome;
    private TextView textView_idade;
    private TextView textView_fumante;

    private TextView textView_nome_2;
    private TextView textView_idade_2;
    private TextView textView_fumante_2;

    //1º Passo para ler
    private FirebaseDatabase database;
    private DatabaseReference reference;
    private ValueEventListener valueEventListener;
    private ChildEventListener childEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database_ler_dadosctivity);

        inicializarComponentes();

        //2º Passo para ler
        database = FirebaseDatabase.getInstance();

//        ouvinte_1();
    }


    //-----------------------PRIMEIRO OUVINTE-----------------------
    //3º Passo
    private void ouvinte_1() {
        //Sempre passar o caminho de onde pegar as informações

        /*PRIMEIRO TIPO DE LEITURA
        DatabaseReference reference =  database.getReference().child("BD").child("Gerentes")
                .child("0");*/

        //SEGUNDO TIPO DE LEITURA
        //DatabaseReference reference =  database.getReference().child("BD").child("Gerentes");


        //TERCEIRO TIPO DE LEITURA
        DatabaseReference reference = database.getReference().child("BD").child("Gerentes");

        //ForSingle le apenas uma vez o banco, apos ler uma vez e desligado o ouvinte
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            //Quando tiver alteração sera acionado
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                /*PRIMEIRO TIPO DE LEITURA
                String nome = dataSnapshot.child("nome").getValue(String.class);
                int idade = dataSnapshot.child("idade").getValue(int.class);

                List<String> list = new ArrayList<String>();
                //percorre a lista de contatos
                for (DataSnapshot data: dataSnapshot.child("contatos").getChildren()){
                    //não precisa passaro child pq ja esta com key 0, 1 ,2 no banco de dados entao interpreta como lista
                    String valor = data.getValue(String.class);

                    list.add(valor);
                }


                String valores = list.get(0)+ "--" + list.get(1) + "--" + list.get(2);

                //verifica se o no realmente existe
                if (dataSnapshot.child("fumante").exists()){
                    boolean fumante = dataSnapshot.child("fumante").getValue(boolean.class);
                    DialogAlerta dialogAlerta = new DialogAlerta("Valor", nome+"\n"+idade+"\n"+fumante+"\n"+valores);
                    dialogAlerta.show(getSupportFragmentManager(),"1");
                }else{
                    DialogAlerta dialogAlerta = new DialogAlerta("Valor", nome+"\n"+idade);
                    dialogAlerta.show(getSupportFragmentManager(),"1");
                }

            */

                /*CHILD LE OS NOME PAIS , CHILDREN OS NOS FILHOS*/

              /*  //SEGUNDO TIPO DE LEITURA
                List<String> nomes = new ArrayList<String>();
                List<Integer> idades = new ArrayList<Integer>();
                List<Boolean> fumantes = new ArrayList<Boolean>();
                for (DataSnapshot data: dataSnapshot.getChildren()){

                    String nome = data.child("nome").getValue(String.class);
                    int idade = data.child("idade").getValue(int.class);
                    boolean fumante = data.child("fumante").getValue(boolean.class);


                    nomes.add(nome);
                    idades.add(idade);
                    fumantes.add(fumante);

                }
                textView_nome.setText(nomes.get(0));
                textView_idade.setText(idades.get(0)+""); //quando for inteiro o boolean contatenar com uma string vazia
                textView_fumante.setText(fumantes.get(0)+"");

                textView_nome_2.setText(nomes.get(1));
                textView_idade_2.setText(idades.get(1)+"");
                textView_fumante_2.setText(fumantes.get(1)+"");*/

                //TERCEIRA MANEIRA DE FAZER COM ENCAPSULAMENTO
                List<Gerente> gerentes = new ArrayList<Gerente>();

                for (DataSnapshot data : dataSnapshot.getChildren()) {

                    Gerente gerente = data.getValue(Gerente.class);

                    gerentes.add(gerente);
                }


                textView_nome.setText(gerentes.get(0).getNome());
                textView_idade.setText(gerentes.get(0).getIdade() + "");
                textView_fumante.setText(gerentes.get(0).isFumante() + "");

                textView_nome_2.setText(gerentes.get(1).getNome());
                textView_idade_2.setText(gerentes.get(1).getIdade() + "");
                textView_fumante_2.setText(gerentes.get(1).isFumante() + "");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    //-----------------------SEGUNDO OUVINTE-----------------------
    private void ouvinte_2() {

        reference = database.getReference().child("BD").child("Gerentes");

        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                List<Gerente> gerentes = new ArrayList<Gerente>();

                for (DataSnapshot data : dataSnapshot.getChildren()) {

                    Gerente gerente = data.getValue(Gerente.class);

                    Log.i("testeOuvinte2",gerente.getNome()+"");

                    gerentes.add(gerente);
                }


                textView_nome.setText(gerentes.get(0).getNome());
                textView_idade.setText(gerentes.get(0).getIdade() + "");
                textView_fumante.setText(gerentes.get(0).isFumante() + "");

                textView_nome_2.setText(gerentes.get(1).getNome());
                textView_idade_2.setText(gerentes.get(1).getIdade() + "");
                textView_fumante_2.setText(gerentes.get(1).isFumante() + "");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        reference.addValueEventListener(valueEventListener);
    }

    //-----------------------TERCEIRO OUVINTE-----------------------
    //recomendado para leitura de listas
    private void ouvinte_3(){
        reference = database.getReference().child("BD").child("Gerentes");

        childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                String chave = dataSnapshot.getKey();
                Gerente gerente = dataSnapshot.getValue(Gerente.class);

                Log.i("testeouvinte_Added3",gerente.getNome()+"--"+chave);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                String chave = dataSnapshot.getKey();
                Log.i("testeouvinte_Changed3","--"+chave);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                String chave = dataSnapshot.getKey();
                Log.i("testeouvinte_Remove3","--"+chave);
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        reference.addChildEventListener(childEventListener);
    }
    @Override
    protected void onStart() {
        super.onStart();
        ouvinte_2();
//        ouvinte_3();
    }

    //sempre que o app fica em backgroup executa o metodo
    @Override
    protected void onStop() {
        super.onStop();
        if (valueEventListener != null) {
            reference.removeEventListener(valueEventListener);
        }
    }

    //quando o app é fechado
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (valueEventListener != null) {
            reference.removeEventListener(valueEventListener);
        }
    }

    private void inicializarComponentes() {
        textView_nome = findViewById(R.id.textView_Database_LerDados_Nome);
        textView_idade = findViewById(R.id.textView_Database_LerDados_Idade);
        textView_fumante = findViewById(R.id.textView_Database_LerDados_Fumante);

        textView_nome_2 = findViewById(R.id.textView_Database_LerDados_Nome_2);
        textView_idade_2 = findViewById(R.id.textView_Database_LerDados_Idade_2);
        textView_fumante_2 = findViewById(R.id.textView_Database_LerDados_Fumante_2);
    }
}
