package rafaelpimenta.studio.com.firebasemoduloii.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

public class Util {


    //Verifica internet
    public static boolean statusInternet(Context context) {

        ConnectivityManager conexao = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo informacao = conexao.getActiveNetworkInfo();


        if (informacao != null && informacao.isConnected()) {
            //com internte
            return true;
        } else {
            //Sem internet
            return false;
        }

    }

    //verifica se as strings estao vazias
    public static boolean verificarCampos(Context context, String texto_1, String texto_2) {

        if (!texto_1.isEmpty() && !texto_2.isEmpty()) {
            if (statusInternet(context)) {


                return true;
            } else {
                Toast.makeText(context, "Sem conexão com a Internet", Toast.LENGTH_LONG).show();

                return false;
            }
        } else {

            Toast.makeText(context, "Preencha os campos", Toast.LENGTH_LONG).show();
            return false;
        }

    }

    public static void alert(Context context, String msg) {

        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();

    }
}
