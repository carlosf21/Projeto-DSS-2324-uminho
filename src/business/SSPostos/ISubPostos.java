package business.SSPostos;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import business.SSPostos.SSPostosFacade.TimeInterval;

public interface ISubPostos  {
    String registarPosto();
    void registarServicoDePosto(String idPosto, String idServico) throws PostoNotFoundException, ServicosNotFoundException;
    Agendamento getAgendamento(String numAgendamento);
    void registarFimServico(String idAgendamento);
    void registarFalharServico(String idAgendamento, String falha);
	Map<String, List<TimeInterval>> getPostosLivres(LocalDateTime targetDate, String servico, LocalTime abertura, LocalTime fecho);
    String criarAgendamento(LocalDateTime horaInic, Boolean notif, String idVeiculo, String idCliente, String idServico, String idPosto);
}