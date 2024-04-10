package business.SSClientes;

public class Gasolina extends Combustao {
    public Gasolina(String idTM) {
        super(idTM);
    }

    @Override
    public String printTipoMotor() {
        return "Gasolina";
    }
}