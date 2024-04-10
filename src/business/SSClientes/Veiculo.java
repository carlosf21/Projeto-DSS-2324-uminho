package business.SSClientes;

import java.util.List;

public class Veiculo {
    private String matricula;
    private String modelo;
    private TipoMotor tipo;
    private TipoMotor tipo2;
    private String idCliente;
    private List<String> servicos_recomendados;

    public Veiculo(String matricula, String modelo, TipoMotor tipo, TipoMotor tipo2, String idCliente, List<String> servicos_recomendados) {
        this.matricula = matricula;
        this.modelo = modelo;
        this.tipo = tipo;
        this.tipo2 = tipo2;
        this.idCliente = idCliente;
        this.servicos_recomendados = servicos_recomendados;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public TipoMotor getTipoMotor1() {
        return tipo;
    }

    public void setTipoMotor1(TipoMotor tipo) {
        this.tipo = tipo;
    }

    public TipoMotor getTipoMotor2() {
        return tipo2;
    }

    public void setTipoMotor2(TipoMotor tipo2) {
        this.tipo2 = tipo2;
    }


    public String getCliente() {
        return this.idCliente;
    }
    public List<String> getServicosRecomendados() {
        return servicos_recomendados;
    }

    public void setServicosRecomendados(List<String> servicos_recomendados) {
        this.servicos_recomendados = servicos_recomendados;
    }

    public String toStringParaUICurto() {
        StringBuilder sb = new StringBuilder();
        sb.append("matricula: " + this.matricula);
        sb.append("\nmodelo: " + this.modelo);
        sb.append("\ntipoMotor: " + this.tipo.toString());
        if (this.tipo2 != null) {
            sb.append("\ntipoMotor2: " + this.tipo2.toString());
        }
        sb.append("\n");
        return sb.toString();
    }
}
