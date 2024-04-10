package business.SSPostos;

import java.time.LocalDateTime;

import business.SSClientes.*;
import data.*;

public class Agendamento {
    private String numAgendamento;
    private LocalDateTime horaInicio;
    private LocalDateTime horaFim;
    private Boolean notificar;
    private Boolean concluido;
    private String motivo;
	private String idPosto;
	private Servico servico;

    // private Veiculo veiculo;
    // private Cliente cliente;
    private String idVeiculo; // n podiamos meter só strings e usar isto para aceder com DAOs ao veiculo e cliente???
    private String idCliente; // n podiamos meter só strings e usar isto para aceder com DAOs ao veiculo e cliente???
    private VeiculoDAO veiculoDAO = VeiculoDAO.getInstance(null);
    private ClienteDAO clienteDAO = ClienteDAO.getInstance(null);
    // public Agendamento(String numAgendamento, LocalDateTime horaInicio,
    //                    Boolean notificar, Veiculo veiculo, 
    //                    Cliente cliente, Servico servico, String idPosto) {
    //     this.numAgendamento = numAgendamento;
    //     this.horaInicio = horaInicio;
    //     // this.horaFim = horaFim;
    //     this.notificar = notificar;
    //     this.concluido = false;
    //     // this.motivo = motivo;
    //     this.veiculo = veiculo;
    //     this.cliente = cliente;
	// 	this.servico = servico;
	// 	this.idPosto = idPosto;
    // }

    public Agendamento(String numAgendamento, LocalDateTime horaInicio,
                       LocalDateTime horaFim, Boolean notificar,
                       Boolean concluido, String motivo, String veiculo, 
                       String cliente, Servico servico, String idPosto) {
        this.numAgendamento = numAgendamento;
        this.horaInicio = horaInicio;
        this.horaFim = horaFim;
        this.notificar = notificar;
        this.concluido = concluido;
        this.motivo = motivo;
		this.idPosto = idPosto;
		this.servico = servico;
        this.idVeiculo = veiculo;
        this.idCliente = cliente;
    }

    public String getNumAgendamento() {
        return numAgendamento;
    }

    public void setNumAgendamento(String numAgendamento) {
        this.numAgendamento = numAgendamento;
    }

    public LocalDateTime getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(LocalDateTime horaInicio) {
        this.horaInicio = horaInicio;
    }

    public LocalDateTime getHoraFim() {
        return horaFim;
    }

    public void setHoraFim(LocalDateTime horaFim) {
        this.horaFim = horaFim;
    }

    public Boolean getNotificar() {
        return notificar;
    }

    public void setNotificar(Boolean notificar) {
        this.notificar = notificar;
    }

    public Boolean getConcluido() {
        return concluido;
    }

    public void setConcluido(Boolean concluido) {
        this.concluido = concluido;
    }

    public String getMotivo() {
        return motivo;
    }


    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public Veiculo getVeiculo() {
        return veiculoDAO.get(this.idVeiculo);
    }

    public Cliente getCliente() {
        return clienteDAO.get(this.idCliente);
    }

	public Servico getServico() {
		return this.servico;
	}

	public void setServico(Servico servico) {
		this.servico = servico;
	}

	public String getIdPosto() {
		return idPosto;
	}

	public void setIdPosto(String idPosto) {
		this.idPosto = idPosto;
	}

    public LocalDateTime getHoraInicAgend() {
        return this.horaInicio;
    }

    public LocalDateTime getHoraFimAgend() {
        return this.horaFim;
    }

	public String toStringParaUI() {
		StringBuilder stringBuilder = new StringBuilder();

		stringBuilder.append("numAgendamento: "); stringBuilder.append(numAgendamento);
		stringBuilder.append("\nhoraInicio: "); stringBuilder.append(horaInicio.toString());
		stringBuilder.append("\nhoraFim: "); stringBuilder.append(horaFim.toString());
		stringBuilder.append("\nnotificar: "); stringBuilder.append(notificar.toString());
		stringBuilder.append("\nconcluido: "); stringBuilder.append(notificar.toString());
		if (motivo != null && motivo != "") {
			stringBuilder.append("\nmotivo: "); stringBuilder.append(motivo);
		}
		stringBuilder.append("\nidPosto: "); stringBuilder.append(idPosto);
		stringBuilder.append("\nservico: "); stringBuilder.append(servico.toStringParaUI());
		stringBuilder.append("\nidVeiculo: "); stringBuilder.append(idVeiculo);
		stringBuilder.append("\nidCliente: "); stringBuilder.append(idCliente);
		stringBuilder.append("\n");

		return stringBuilder.toString();
	}
}
