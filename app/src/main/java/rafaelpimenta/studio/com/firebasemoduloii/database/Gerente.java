package rafaelpimenta.studio.com.firebasemoduloii.database;

import java.util.List;

public class Gerente {

    private String nome;
    private int idade;
    private boolean fumante;
    private List<String> contatos;


    public Gerente() {
    }

    public Gerente(String nome, int idade, boolean fumante) {
        this.nome = nome;
        this.idade = idade;
        this.fumante = fumante;

    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public int getIdade() {
        return idade;
    }

    public void setIdade(int idade) {
        this.idade = idade;
    }

    public boolean isFumante() {
        return fumante;
    }

    public void setFumante(boolean fumante) {
        this.fumante = fumante;
    }

    public List<String> getContatos() {
        return contatos;
    }

    public void setContatos(List<String> contatos) {
        this.contatos = contatos;
    }
}