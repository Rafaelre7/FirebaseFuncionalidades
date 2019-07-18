package rafaelpimenta.studio.com.firebasemoduloii;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;

import rafaelpimenta.studio.com.firebasemoduloii.database.DatabaseGravarAlterarRemoverActivity;
import rafaelpimenta.studio.com.firebasemoduloii.database.DatabaseLerDadosctivity;
import rafaelpimenta.studio.com.firebasemoduloii.database_lista_empresa.DatabaseListaEmpresaActivity;
import rafaelpimenta.studio.com.firebasemoduloii.storage.StorageDownloadActivity;
import rafaelpimenta.studio.com.firebasemoduloii.storage.StorageUploadActivity;
import rafaelpimenta.studio.com.firebasemoduloii.util.Permissao;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private CardView cardView_Upload;
    private CardView cardView_Download;
    private CardView cardView_Database_LerDados;
    private CardView cardView_GravrAlterarExcluir;
    private CardView cardView_Empresa;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inicializarComponentes();


        cardView_Upload.setOnClickListener(this);
        cardView_Download.setOnClickListener(this);
        cardView_Database_LerDados.setOnClickListener(this);
        cardView_GravrAlterarExcluir.setOnClickListener(this);
        cardView_Empresa.setOnClickListener(this);

        permissao();
    }

    private void inicializarComponentes() {

        cardView_Upload = findViewById(R.id.cardView_StorageUpload);
        cardView_Download = findViewById(R.id.cardView_StorageDownload);
        cardView_Database_LerDados = findViewById(R.id.cardView_Database_LerDados);
        cardView_GravrAlterarExcluir = findViewById(R.id.cardView_Storage_GravarAlterarExcluir);
        cardView_Empresa = findViewById(R.id.cardView_Empresas);

    }

    //--------------------TRATAMENTO DE CLICKS----------------------
    @Override
    public void onClick(View view) {


        switch (view.getId()) {
            case R.id.cardView_StorageDownload:
                Intent intent = new Intent(getBaseContext(), StorageDownloadActivity.class);
                startActivity(intent);
                break;
            case R.id.cardView_StorageUpload:
                startActivity(new Intent(getBaseContext(), StorageUploadActivity.class));
                break;
            case R.id.cardView_Database_LerDados:
                startActivity(new Intent(getBaseContext(), DatabaseLerDadosctivity.class));
                break;
            case R.id.cardView_Storage_GravarAlterarExcluir:
                startActivity(new Intent(getBaseContext(), DatabaseGravarAlterarRemoverActivity.class));
                break;
            case R.id.cardView_Empresas:
                startActivity(new Intent(getBaseContext(), DatabaseListaEmpresaActivity.class));
                break;

            default:
                break;
        }
    }

    //--------------------PERMISSAO DO USUARIO----------------------

    private void permissao() {

        String permissoes[] = new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,

        };

        Permissao.permissao(this, 0, permissoes);
    }

    //saber o resultado das permissoes
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        for (int result : grantResults) {
            if (result == PackageManager.PERMISSION_DENIED) {
                Toast.makeText(this, "Aceite as permissões para o aplicativo funcionar corretamete", Toast.LENGTH_LONG).show();
                finish();

                break;
            }
        }
    }

    public void alert(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}