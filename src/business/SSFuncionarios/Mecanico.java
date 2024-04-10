package business.SSFuncionarios;

import business.SSClientes.TipoMotor;

public class Mecanico extends Funcionario {
    private TipoMotor competencias;

    public Mecanico(String idFunc, String nome, TipoMotor comp) {
        super(idFunc, nome);
        this.competencias = comp;
    }

    public TipoMotor getCompetencias() {
        return this.competencias;
    }

    public void setCompetencias(TipoMotor comp) {
        this.competencias = comp;
    }
}
