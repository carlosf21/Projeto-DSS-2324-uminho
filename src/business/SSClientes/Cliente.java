package business.SSClientes;

import java.util.ArrayList;
import java.util.List;

public class Cliente {
    private String nif;
    private String nome;
    private String morada;
    private String telefone;
    private String email;
    private List<String> veiculos;

    public Cliente(String nif, String nome, String morada, String telefone, String email, List<String> veiculos) {
        this.nif = nif;
        this.nome = nome;
        this.morada = morada;
        this.telefone = telefone;
        this.email = email;
        this.veiculos = veiculos;
    }

    public String getNif() {
        return nif;
    }

    public void setNif(String nif) {
        this.nif = nif;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getMorada() {
        return morada;
    }

    public void setMorada(String morada) {
        this.morada = morada;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<String> getVeiculos(){
        return new ArrayList<>(veiculos);
    }

    public void setVeiculos(List<String> veiculos){
        this.veiculos = veiculos;
    }

    public void addVeiculo(String veiculo) {
        veiculos.add(veiculo);
    }

}
