package rafaelpimenta.studio.com.firebasemoduloii.database_lista_funcionario;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

import rafaelpimenta.studio.com.firebasemoduloii.R;

public class RecyclerView_ListaFuncionario extends RecyclerView.Adapter<RecyclerView_ListaFuncionario.ViewHolder> {

    private Context context;
    private List<Funcionario> funcionarios;
    private ClickFuncionario clickFuncionario;

    public RecyclerView_ListaFuncionario(Context context, List<Funcionario> funcionarios, ClickFuncionario clickFuncionario) {

        this.context = context;
        this.funcionarios = funcionarios;
        this.clickFuncionario = clickFuncionario;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.database_lista_funcionario_conteudo_recyclerview,
                parent, false);

        ViewHolder holder = new ViewHolder(view);
        view.setTag(holder);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

       final  Funcionario funcionario = funcionarios.get(position);

        holder.textView_Nome.setText(funcionario.getNome());
        holder.textView_Idade.setText(funcionario.getIdade()+"");

        holder.progressBar.setVisibility(View.VISIBLE);

        Picasso.with(context).load(funcionario.getUrlImagem()).into(holder.imageView, new Callback() {
            @Override
            public void onSuccess() {
                holder.progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onError() {

                holder.progressBar.setVisibility(View.GONE);
            }
        });

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                clickFuncionario.click_Funcionario(funcionario);
            }
        });
    }

    @Override
    public int getItemCount() {
        return funcionarios.size();
    }

    public interface ClickFuncionario {
        void click_Funcionario(Funcionario funcionario);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        CardView cardView;
        TextView textView_Nome;
        TextView textView_Idade;
        ImageView imageView;
        ProgressBar progressBar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.cardView_ListaFuncionario_Item_CardView);
            textView_Nome = itemView.findViewById(R.id.textView_ListaFuncionario_Nome);
            textView_Idade = itemView.findViewById(R.id.textView_ListaFuncionario_Idade);
            imageView = itemView.findViewById(R.id.imageView_ListaFuncionario_Item);
            progressBar = itemView.findViewById(R.id.progressBar_ListaFuncionario_Item);

        }
    }
}
