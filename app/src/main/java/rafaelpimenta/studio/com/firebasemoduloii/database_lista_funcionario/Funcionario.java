package rafaelpimenta.studio.com.firebasemoduloii.database_lista_funcionario;

import android.os.Parcel;
import android.os.Parcelable;

public class Funcionario implements Parcelable {

    private String id;
    private String nome;
    private int idade;
    private String urlImagem;
    private String id_empresa;


    public Funcionario() {
    }

    public Funcionario(String id, String nome, int idade, String urlImagem) {

        this.id = id;
        this.nome = nome;
        this.idade = idade;
        this.urlImagem = urlImagem;

    }

    public Funcionario( String nome, int idade, String urlImagem) {

        this.nome = nome;
        this.idade = idade;
        this.urlImagem = urlImagem;

    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrlImagem() {
        return urlImagem;
    }

    public void setUrlImagem(String urlImagem) {
        this.urlImagem = urlImagem;
    }

    public int getIdade() {
        return idade;
    }

    public void setIdade(int idade) {
        this.idade = idade;
    }

    public String getId_empresa() {
        return id_empresa;
    }

    public void setId_empresa(String id_empresa) {
        this.id_empresa = id_empresa;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.nome);
        dest.writeInt(this.idade);
        dest.writeString(this.urlImagem);
        dest.writeString(this.id_empresa);
    }

    protected Funcionario(Parcel in) {
        this.id = in.readString();
        this.nome = in.readString();
        this.idade = in.readInt();
        this.urlImagem = in.readString();
        this.id_empresa = in.readString();
    }

    public static final Creator<Funcionario> CREATOR = new Creator<Funcionario>() {
        @Override
        public Funcionario createFromParcel(Parcel source) {
            return new Funcionario(source);
        }

        @Override
        public Funcionario[] newArray(int size) {
            return new Funcionario[size];
        }
    };
}
