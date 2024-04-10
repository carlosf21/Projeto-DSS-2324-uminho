package business.SSClientes;

public class Diesel extends Combustao {
    public Diesel(String idTM) {
        super(idTM);
    }

    @Override
    public String printTipoMotor() {
        return "Diesel";
    }
}