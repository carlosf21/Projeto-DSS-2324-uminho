package business.SSClientes;

public class Combustao extends TipoMotor {
    public Combustao(String idTM) {
        super(idTM);
    }

    @Override
    public String printTipoMotor() {
        return "Combustao";
    }
}