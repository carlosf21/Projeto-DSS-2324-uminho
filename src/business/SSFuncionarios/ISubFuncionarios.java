package business.SSFuncionarios;

import business.SSClientes.*;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public interface ISubFuncionarios {	
    void registarHorarioEstacao(LocalTime horaAbertura, LocalTime horaFecho);
    
    void registarEntradaTurno(String idFunc, LocalDateTime horaInic) throws FuncionarioNotFoundException;
    
    boolean verificarCompMecanico(String idMecanico, List<TipoMotor> tms);
    
    void registarSaidaTurno(String idFunc, LocalDateTime horaSaid) throws FuncionarioNotFoundException, RegistoNotFoundException;	
	/////////////////////////////////////// alteradas / novas
    // List<Agendamento> calcAgendamentosPossiveis(List<String> postosDisp, List<String> mecsDisp);
    // String registarFuncionario(String nome,String tipoFunc);
    // String verificarCargoFuncionario(String idFunc);
    // List<String> getMecanicoDisponivel(String servico);
	String registarMecanico(String nome, TipoMotor idTipoMotor);
	String registarAdministrativo(String nome);
	boolean verificarFuncionarioSerMecanico(String idFunc);
}