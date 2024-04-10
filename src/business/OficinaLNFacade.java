package business;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import business.SSClientes.*;
import business.SSPostos.*;
import business.SSPostos.SSPostosFacade.TimeInterval;
import business.SSFuncionarios.*;

public class OficinaLNFacade implements IOficinaLN {
	SSClientesFacade clientes;
	SSFuncionariosFacade funcionarios;
	SSPostosFacade postos;

	public OficinaLNFacade() {
		this.clientes = new SSClientesFacade();
		this.funcionarios = new SSFuncionariosFacade();
		this.postos = new SSPostosFacade();
	}

/////////////////////////////////////////////////////////////////////////////// clientes
	public boolean verificarCliente(String nif) {
		return clientes.verificarCliente(nif);
	}

    public boolean verificarVeiculo(String matricula) {
		return clientes.verificarVeiculo(matricula);
	}

    public void registarCliente(String nome, String nif, String morada, String telefone, String email) {
		clientes.registarCliente(nome, nif, morada, telefone, email);
	}

	public void registarVeiculo(String nifCliente, String matricula, String modelo, String idTipo1, String idTipo2) throws TipoMotorNotFoundException{
		clientes.registarVeiculo(nifCliente, matricula, modelo, idTipo1, idTipo2);
	}

    public boolean enviarSMSCliente(String numero, String msg) throws ClienteNotFoundException {
		return clientes.enviarSMSCliente(numero, msg);
	}

    public void adicionaServicosVeiculo(String idVeiculo, List<String> servicos) throws VeiculoNotFoundException {
		clientes.adicionaServicosVeiculo(idVeiculo, servicos);
	}

	public List<Veiculo> getVeiculosCliente(String nif) throws ClienteNotFoundException {
		return clientes.getVeiculosCliente(nif);
	}
    public Veiculo getVeiculoCliente(String matricula, String nif) throws ClienteNotFoundException {
		return clientes.getVeiculoCliente(matricula, nif);
	}

	public List<TipoMotor> getTiposServico() {
		return clientes.getTiposServico();
	}

    public void removeServicosVeiculo(String idVeiculo, List<String> servicos) throws ServicosNotFoundException, VeiculoNotFoundException {
		clientes.removeServicosVeiculo(idVeiculo, servicos);
	}

/////////////////////////////////////////////////////////////////////////////// funcionarios
    public void registarHorarioEstacao(LocalTime horarioInic, LocalTime horarioFim) {
		funcionarios.registarHorarioEstacao(horarioInic, horarioFim);
	}

	public String registarMecanico(String nome, String idTipoMotor) {
		TipoMotor tm = clientes.getTipoMotor(idTipoMotor);
		String id = funcionarios.registarMecanico(nome, tm);
		return id;
	}

	public String registarAdministrativo(String nome) {
		return funcionarios.registarAdministrativo(nome);

	}

	public void registarServico(int duracao, String descricao, String idTM) {
		postos.addServico(duracao,descricao,clientes.getTipoMotor(idTM));
	}

    // public List<Agendamento> calcAgendamentosPossiveis(List<String> postosDisp, List<String> mecsDisp ) {
	// 	return funcionarios.calcAgendamentosPossiveis(postosDisp, mecsDisp);
	// }
    
    public boolean verificarFuncionarioSerMecanico(String idFunc) {
		return funcionarios.verificarFuncionarioSerMecanico(idFunc);
	}
    
    public void registarEntradaTurno(String idFunc, LocalDateTime horaInic) throws FuncionarioNotFoundException {
		funcionarios.registarEntradaTurno(idFunc, horaInic);
	}

    public boolean verificarCompMecanico(String idMecanico,String idPosto) {
		List<TipoMotor> l = postos.getEspecialidadesPosto(idPosto);
		return funcionarios.verificarCompMecanico(idMecanico, l);
	}
    
    public void registarSaidaTurno(String idFunc, LocalDateTime horaSaid) throws FuncionarioNotFoundException, RegistoNotFoundException {
		funcionarios.registarSaidaTurno(idFunc, horaSaid);
	}

	public Funcionario getFuncionarioPorNome(String nome) {
		return funcionarios.getFuncionarioPorNome(nome);
	}

/////////////////////////////////////////////////////////////////////////////// postos
    public String registarPosto() {
		return postos.registarPosto();
	}

	public List<Servico> getTodosServicos() {
		return postos.getTodosServicos();
	}

	public List<Posto> getTodosPostos() {
		return postos.getTodosPostos();
	}

    public void registarServicoDePosto(String idPosto, String idServico) throws PostoNotFoundException, ServicosNotFoundException {
		postos.registarServicoDePosto(idPosto, idServico);
	}

    public Agendamento getAgendamento(String idPosto) {
		return postos.getAgendamento(idPosto);
	}

    public void registarFimServico(String idAgendamento) {
		postos.registarFimServico(idAgendamento);
	}

    public void registarFalharServico(String idAgendamento, String falha) {
		postos.registarFalharServico(idAgendamento, falha);
	}

	public Map<String, List<TimeInterval>> getPostosLivres(LocalDateTime targetDate, String servico) {
		return postos.getPostosLivres(targetDate, servico, funcionarios.getHoraAbertura(), funcionarios.getHoraFecho());
	}

	public boolean verificarPossivelAgendamento(LocalDateTime horaInicio, LocalDateTime horaFim, String idServico, String idPosto) {
		return postos.verificarPossivelAgendamento(horaInicio,horaFim,idServico,idPosto);
	}
    public String criarAgendamento(LocalDateTime horaInic, Boolean notif, String idVeiculo, String idCliente, String idServico, String idPosto) {
		return postos.criarAgendamento(horaInic, notif, idVeiculo, idCliente, idServico, idPosto);
	}

	public boolean postoExiste(String idPosto) {
		return (postos.getPosto(idPosto) != null);
	}

	public Posto getPosto(String idPosto) {
		return postos.getPosto(idPosto);
	}

	public List<Agendamento> getPostoAgendamentosEntre(String idPosto, LocalDateTime inicio, LocalDateTime fim) {
		return postos.getPostoAgendamentosEntre(idPosto, inicio, fim);
	}

	public boolean servicoExiste(String idServico) {
		return (postos.getServico(idServico) != null);
	}

	public String getMatriculaVeiculoAgendamento(String idAgendamento) {
		Agendamento agendamento = postos.getAgendamento(idAgendamento);
		return agendamento.getVeiculo().getMatricula();
	}

	public List<Servico> getServicosDisponiveis(Veiculo v) {
		TipoMotor motor1 = v.getTipoMotor1();
		Set<Servico> servicos = postos.servicosPara(motor1);
		TipoMotor motor2 = v.getTipoMotor2();
		if (motor2 != null) {
			servicos.addAll(postos.servicosPara(motor2));
		}
		return servicos.stream().sorted((s1, s2) -> s1.compareTo(s2)).collect(Collectors.toList());
	}
}
