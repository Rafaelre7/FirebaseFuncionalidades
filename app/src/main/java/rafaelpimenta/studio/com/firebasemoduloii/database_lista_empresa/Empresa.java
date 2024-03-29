package rafaelpimenta.studio.com.firebasemoduloii.database_lista_empresa;

import android.os.Parcel;
import android.os.Parcelable;

public class Empresa implements Parcelable {

    private String nome;
    private String id;

    public Empresa() {
    }

    public Empresa(String nome, String id) {
        this.nome = nome;
        this.id = id;
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


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.nome);
        dest.writeString(this.id);
    }

    protected Empresa(Parcel in) {
        this.nome = in.readString();
        this.id = in.readString();
    }

    public static final Creator<Empresa> CREATOR = new Creator<Empresa>() {
        @Override
        public Empresa createFromParcel(Parcel source) {
            return new Empresa(source);
        }

        @Override
        public Empresa[] newArray(int size) {
            return new Empresa[size];
        }
    };
}
