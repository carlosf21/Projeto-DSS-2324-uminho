package business;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import business.SSClientes.*;
import business.SSFuncionarios.FuncionarioNotFoundException;
import business.SSFuncionarios.RegistoNotFoundException;
import business.SSPostos.*;
import business.SSPostos.SSPostosFacade.TimeInterval;

public interface IOficinaLN {

// ### ISUBCLIENTES
    boolean verificarCliente(String nif);

    boolean verificarVeiculo(String matricula);
	
    boolean enviarSMSCliente(String numero, String msg) throws ClienteNotFoundException;
	
    void adicionaServicosVeiculo(String idVeiculo, List<String> servicos) throws VeiculoNotFoundException;
	
    Veiculo getVeiculoCliente(String matricula, String nif) throws ClienteNotFoundException;
	
    void removeServicosVeiculo(String idVeiculo, List<String> servicos) throws ServicosNotFoundException, VeiculoNotFoundException;

/////////////////////////////////////// alteradas / novas
    // String registarCliente(String nome, String nif, String morada, String telefone, String email, String matricula, String modelo, String tipoCarro); // naof az sentido nenhum
	void registarCliente(String nome, String nif, String morada, String telefone, String email);
	void registarVeiculo(String nifCliente, String matricula, String modelo, String idTipo, String idTipo2) throws TipoMotorNotFoundException;

// #### ISUBFUNCIONARIOS
    void registarHorarioEstacao(LocalTime horaAbertura, LocalTime horaFecho);
    
    void registarEntradaTurno(String idFunc, LocalDateTime horaInic) throws FuncionarioNotFoundException;
    
    //como é que vamos lidar com as competências 
    boolean verificarCompMecanico(String idMecanico, String idPosto);
    
    void registarSaidaTurno(String idFunc, LocalDateTime horaSaid) throws FuncionarioNotFoundException, RegistoNotFoundException;
	
	/////////////////////////////////////// alteradas / novas
	String registarMecanico(String nome, String idTipoMotor);
	String registarAdministrativo(String nome);
	boolean verificarFuncionarioSerMecanico(String idFunc);

// #### ISUBPOSTOS

    String registarPosto();
    void registarServicoDePosto(String idPosto, String idServico) throws PostoNotFoundException, ServicosNotFoundException;
    Agendamento getAgendamento(String idPosto);
    void registarFimServico(String idAgendamento);
    void registarFalharServico(String idAgendamento, String falha);
	Map<String, List<TimeInterval>> getPostosLivres(LocalDateTime targetDate, String servico);
    String criarAgendamento(LocalDateTime horaInic, Boolean notif, String idVeiculo, String idCliente, String idServico, String idPosto);

}
