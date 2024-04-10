package business.SSClientes;

public class TipoMotor {
    private String idTM;
    // private String descricao;

    public TipoMotor(String idTM) {
        this.idTM = idTM;
        // this.descricao = "Universal";
    }

    public TipoMotor(TipoMotor tv) {
        this.idTM = tv.idTM;
    }

    public String getidTM() {
        return idTM;
    }

    private void setidTM(String idTM) {
        this.idTM = idTM;
    }

    public String printTipoMotor() {
        return "Universal";
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TipoMotor otherA = (TipoMotor) o;
        return this.idTM == otherA.idTM;
    }

    public TipoMotor clone() {
        return new TipoMotor(this);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.printTipoMotor());
        return sb.toString();
    }

    public String toStringParaUI() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.idTM + ". ");
        sb.append(this.printTipoMotor());
        return sb.toString();
    }
}
	// public String getDescricao() {
	// 	return this.descricao;
	// }

	// private void setDescricao(String descricao) {
	// 	this.descricao = descricao;
	// }
