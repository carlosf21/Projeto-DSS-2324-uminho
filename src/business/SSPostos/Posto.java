package business.SSPostos;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import business.SSClientes.TipoMotor;
import business.SSPostos.SSPostosFacade.TimeInterval;
import data.*;

public class Posto {
    private String idPosto;
    private List<String> agendamentos;
    private Map<String, Servico> servicos_disponiveis;
    private AgendamentoDAO agendamentoDAO = AgendamentoDAO.getInstance();

    public Posto(String id) {
        this.idPosto = id;
        this.agendamentos = new ArrayList<>();
        this.servicos_disponiveis = new HashMap<>();
    }
    
	public Posto(Posto p) {
		this.idPosto = p.idPosto;
		this.agendamentos = p.agendamentos.stream().collect(Collectors.toList());
		this.servicos_disponiveis = p.servicos_disponiveis.entrySet().stream()
										.collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().clone()));
		this.agendamentoDAO = AgendamentoDAO.getInstance();
	}

    public Posto(String idPosto, List<String> agendamentos, Map<String, Servico> servicosDisponiveis) {
        this.idPosto = idPosto;
        this.agendamentos = new ArrayList<>(agendamentos);
		// sort dos agendamentos por data
		this.agendamentos.sort((ida, idb) -> agendamentoDAO.get(ida).getHoraInicio().compareTo(agendamentoDAO.get(idb).getHoraInicio()));
        this.servicos_disponiveis = servicosDisponiveis;
    }

    public String getIdPosto() {
        return idPosto;
    }

    public void setIdPosto(String idPosto) {
        this.idPosto = idPosto;
    }

    public List<String> getAgendamentos() {
        return agendamentos;
    }

    public void setAgendamentos(List<String> agendamentos) {
        this.agendamentos = agendamentos;
    }

    public Map<String, Servico> getServicosDisponiveis() {
        return servicos_disponiveis;
    }

	public List<TipoMotor> getEspecialidades() {
		return this.servicos_disponiveis.values().stream().map(s -> s.getTipoMotor()).distinct().collect(Collectors.toList());
	}
	
    // public void setServicosDisponiveis(Map<String, Servico> servicosDisponiveis) {
    //     this.servicos_disponiveis = servicosDisponiveis;
    // }

	public void addServicoDisponivel(Servico servico) {
		this.servicos_disponiveis.put(servico.getIdServico(), servico);
	}

    public List<Agendamento> getAgendamentosEntre(LocalDateTime start, LocalDateTime end) {
        List<Agendamento> agends = new ArrayList<>();
        for (String id: agendamentos) {
			Agendamento a = agendamentoDAO.get(id);
			if (a != null) agends.add(a);
        }
        agends.sort((agA, agB) -> agA.getHoraInicio().compareTo(agB.getHoraInicio()));
        return agends.stream().filter(
            agend -> {
			return (agend.getHoraInicio().isAfter(start) && agend.getHoraFim().isBefore(end));
		}).collect(Collectors.toList());
    }

	public List<TimeInterval> getVagas(LocalDateTime start, LocalDateTime end, Servico ser, LocalDateTime horaAbertura, LocalDateTime horaFecho) {
		// System.out.println("A procurar no posto " + getIdPosto());
		// verificar se o posto e compativel com o servico
		if (getServicosDisponiveis().containsKey(ser.getIdServico())) {
			// System.out.println("Servico tem compatibilidade");
			// ir buscar agendamentos apenas do dia dado
			List<Agendamento> agendamentos = getAgendamentosEntre(start, end);
			ArrayList<TimeInterval> slots = new ArrayList<>();
		
			// verificar se existe tempo suficiente neste intervalo de datas
			if (agendamentos.size() == 0) { // tudo livre, nao precisamos de ver mais nada
				// livre = true;
				// System.out.println("Posto nao tem nenhum servico");
				slots.add(new TimeInterval(horaAbertura, horaFecho));
			} else {
				Iterator<Agendamento> iter = agendamentos.iterator(); // assumimos que estao sorted por data
				Agendamento firstFinger = iter.next(); // size != 0, logo tem de funceminar
		
//////////////////// verificar se existe tempo suficiente antes do primeiro agendamento
				long minutesBetween = ChronoUnit.MINUTES.between(horaAbertura, firstFinger.getHoraInicio());
				if (minutesBetween >= ser.getDuracao()) { // servico cabe aqui
					// livre = true;
					// System.out.println("Existe tempo entre hora de abertura e primeiro servico");
					slots.add(new TimeInterval(horaAbertura, firstFinger.getHoraInicio()));
				}
		
//////////////////// encontrar um slot entre os varios agendamentos
				while (/*livre == false &&*/ iter.hasNext()) {
					Agendamento secondFinger = iter.next();
					// verificar tempo entre estes dois, fim do primeiro e inicio do segundo
					minutesBetween = ChronoUnit.MINUTES.between(firstFinger.getHoraFim(), secondFinger.getHoraInicio());
					if (minutesBetween >= ser.getDuracao()) { // servico cabe aqui
						// livre = true;
						// System.out.println("Existe tempo entre 2 servicos");
						slots.add(new TimeInterval(firstFinger.getHoraFim(), secondFinger.getHoraInicio()));
					} 
					firstFinger = secondFinger;
					secondFinger = null;
				}
		
//////////////////// ver se cabe entre o ultimo agendamento e a data de fecho
				// firstfinger neste caso vai ser o ultimo
				minutesBetween = ChronoUnit.MINUTES.between(firstFinger.getHoraFim(), horaFecho);
				if (/*livre == false &&*/ minutesBetween >= ser.getDuracao()) { // servico cabe aqui
					// livre = true;
					// System.out.println("Existe tempo entre ultimo servico e hora de fim");
					slots.add(new TimeInterval(firstFinger.getHoraFim(), horaFecho));
				}
			}
			return slots;
		}
		return null;
	}

	public Posto clone() {
		return new Posto(this);
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("idPosto: " + this.idPosto);
		return sb.toString();
	}

	public int compareTo(Posto other) {
        // Implement the comparison logic based on your requirements
        return Integer.parseInt(this.getIdPosto()) - Integer.parseInt(other.getIdPosto());
    }
}






