package business.SSClientes;

public class Eletrico extends TipoMotor {
    public Eletrico(String idTM) {
        super(idTM);
    }

    @Override
    public String printTipoMotor() {
        return "Eletrico";
    }
}