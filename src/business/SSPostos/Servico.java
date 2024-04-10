package business.SSPostos;

import business.SSClientes.TipoMotor;

public class Servico implements Comparable<Servico> {
	String idServico;
	int duracao;
	String descricao;
	TipoMotor ts;

	public Servico(String idServico, int duracao, String descricao, TipoMotor ts) {
		this.idServico = idServico;
		this.duracao = duracao;
		this.descricao = descricao;
		this.ts = ts;
	}

	public Servico(Servico s) {
		this.idServico = s.idServico;
		this.duracao = s.duracao;
		this.descricao = s.descricao;
		this.ts = s.ts;
	}

	//needed in ServicoDAO for efficient query // ah????????????? mete a null
	// public Servico(String idServico, int duracao) {
	// 	this.idServico = idServico;
	// 	this.duracao = duracao;
	// 	this.ts = null;
	// }

	public String getIdServico() {
		return idServico;
	}

	public void setIdServico(String idServico) {
		this.idServico = idServico;
	}

	public int getDuracao() {
		return duracao;
	}

	public void setDuracao(int duracao) {
		this.duracao = duracao;
	}

	public String getDescricao() {
		return this.descricao;
	}
	
	public TipoMotor getTipoMotor() {
		return ts;
	}

	public void setTipoMotor(TipoMotor ts) {
		this.ts = ts;
	}

	public String toStringParaUI() {
		StringBuilder stringBuilder = new StringBuilder();

		stringBuilder.append("idServico: "); stringBuilder.append(idServico);
		stringBuilder.append("\nduracao(min): "); stringBuilder.append(duracao);
		stringBuilder.append("\ndescricao: "); stringBuilder.append(descricao);
		stringBuilder.append("\ntipo motor: "); stringBuilder.append(ts.toString());
		stringBuilder.append("\n");
		return stringBuilder.toString();
	}

	public Servico clone() {
		return new Servico(this);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Id: " + this.idServico);
		sb.append("duracao (min): " + this.duracao);
		sb.append("descricao: " + this.descricao);
		sb.append("tipoMotor:" + ts.toString());
		return sb.toString();
	}

	public int compareTo(Servico other) {
        // Implement the comparison logic based on your requirements
        return Integer.parseInt(this.getIdServico()) - Integer.parseInt(other.getIdServico());
    }
}
