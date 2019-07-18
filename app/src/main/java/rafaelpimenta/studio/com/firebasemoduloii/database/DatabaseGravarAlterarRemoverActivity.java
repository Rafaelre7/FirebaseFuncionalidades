package rafaelpimenta.studio.com.firebasemoduloii.database;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import rafaelpimenta.studio.com.firebasemoduloii.R;
import rafaelpimenta.studio.com.firebasemoduloii.util.DialogProgress;
import rafaelpimenta.studio.com.firebasemoduloii.util.Util;

public class DatabaseGravarAlterarRemoverActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText editText_NomePasta;
    private EditText editText_Nome;
    private EditText editText_Idade;

    private Button button_Salvar;
    private Button button_Alterar;
    private Button button_Remover;

    private FirebaseDatabase database;
    private boolean firebase_offline = false;

    private DialogProgress dialogProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.database_gravar_alterar_remover_activity);

        inicializarComponentes();

        button_Alterar.setOnClickListener(this);
        button_Salvar.setOnClickListener(this);
        button_Remover.setOnClickListener(this);

        database = FirebaseDatabase.getInstance();

        ativarFirebaseOffLine();
    }

    private void inicializarComponentes() {

        editText_NomePasta = findViewById(R.id.editText_Database_GravarAlterarRemover_NomePasta);
        editText_Nome = findViewById(R.id.editText_Database_GravarAlterarRemover_Nome);
        editText_Idade = findViewById(R.id.editText_Database_GravarAlterarRemover_Idade);

        button_Salvar = findViewById(R.id.button_Database_GravarAlterarRemover_Salvar);
        button_Alterar = findViewById(R.id.button_Database_GravarAlterarRemover_Alterar);
        button_Remover = findViewById(R.id.button_Database_GravarAlterarRemover_Remover);
    }

    //----------------EVENTOS DE CLICKS-----------------------
    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.button_Database_GravarAlterarRemover_Salvar:
//                Util.alert(getBaseContext(), "button_Salvar");
                buttonSalvar();
                break;
            case R.id.button_Database_GravarAlterarRemover_Alterar:
//                Util.alert(getBaseContext(), "button_Alterar");
                buttonAlterar();
                break;
            case R.id.button_Database_GravarAlterarRemover_Remover:
//                Util.alert(getBaseContext(), "button_Remover");
                buttonRemover();

                break;
        }
    }


    //----------------VALIDAÇÃO-----------------------
    private void buttonRemover() {
        String nome_pasta = editText_NomePasta.getText().toString();

        if (!nome_pasta.isEmpty()) {
            removerDados(nome_pasta);
        } else {
            Util.alert(getBaseContext(), "Preencha o campo com o nome da pasta que voce quer excluir");
        }

    }

    private void buttonSalvar() {

        String nome = editText_Nome.getText().toString();
        String idade_string = editText_Idade.getText().toString();


        //Valida os campos e acesso a internet
        if (Util.verificarCampos(getBaseContext(), nome, idade_string)) {

            int idade = Integer.parseInt(idade_string);
            salvarDados(nome, idade);
        }

    }

    private void buttonAlterar() {

        String nome = editText_Nome.getText().toString();
        String idade_string = editText_Idade.getText().toString();


        //Valida os campos e acesso a internet
        if (Util.verificarCampos(getBaseContext(), nome, idade_string)) {

            int idade = Integer.parseInt(idade_string);
            alterarDados(nome, idade);
        }

    }


    //----------------SALVAR/REMOVER/ALTERAR-----------------------
    private void removerDados(String nome_pasta) {

        dialogProgress = new DialogProgress();

        dialogProgress.show(getSupportFragmentManager(),"1");

        DatabaseReference reference = database.getReference().child("BD").child("Gerentes");

        reference.child(nome_pasta).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Util.alert(getBaseContext(), "Sucesso ao Remover dados");
                    dialogProgress.dismiss();
                    editText_NomePasta.setText("");
                } else {
                    Util.alert(getBaseContext(), "Erro ao Remover dados");
                }
            }
        });
    }

    private void alterarDados(String nome, int idade) {

        dialogProgress = new DialogProgress();

        dialogProgress.show(getSupportFragmentManager(),"1");
        String nome_pasta = editText_NomePasta.getText().toString();

        if (!nome_pasta.isEmpty()) {

            DatabaseReference reference = database.getReference().child("BD").child("Gerentes");

            Gerente gerente = new Gerente(nome, idade, false);

            Map<String, Object> atualizacao = new HashMap<>();

            atualizacao.put(nome_pasta, gerente);

            reference.updateChildren(atualizacao).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Util.alert(getBaseContext(), "Sucesso ao Alterar dados");
                        dialogProgress.dismiss();
                        editText_NomePasta.setText("");
                        editText_Nome.setText("");
                        editText_Idade.setText("");

                    } else {
                        Util.alert(getBaseContext(), "Erro ao Alterar dados");
                    }
                }
            });
        } else {

            Util.alert(getBaseContext(), "Preencha o campo com o nome da pasta que voce quer alterar");
        }
    }


    private void salvarDados(String nome, int idade) {

        dialogProgress = new DialogProgress();

        dialogProgress.show(getSupportFragmentManager(),"1");

        DatabaseReference reference = database.getReference().child("BD").child("Gerentes");

        /* Usar dados mocados com map
        Map<String, Object> valor = new HashMap<>();

        valor.put("nome","Jone Arce");
        valor.put("idade", 28);
        valor.put("fumante", false);*/

        Gerente gerente = new Gerente(nome, idade, false);

//        reference.child("9").setValue(gerente).addOnCompleteListener(new OnCompleteListener<Void>() {
        //push pega id aleatorio
        reference.push().setValue(gerente).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Util.alert(getBaseContext(), "Sucesso ao gravar dados");
                    dialogProgress.dismiss();
                    editText_Nome.setText("");
                    editText_Idade.setText("");
                } else {
                    Util.alert(getBaseContext(), "Erro ao gravar dados");
                    dialogProgress.dismiss();
                }
            }
        });
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