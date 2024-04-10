package business.SSFuncionarios;

public class Funcionario {
    private String idFunc;
    private String nome;

    public Funcionario(String idFunc, String nome) {
        this.idFunc = idFunc;
        this.nome = nome;
    }

    public String getIdFunc() {
        return idFunc;
    }

    public void setIdFunc(String idFunc) {
        this.idFunc = idFunc;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
}
