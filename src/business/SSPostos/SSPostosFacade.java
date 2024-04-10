package business.SSPostos;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import business.SSClientes.*;
import business.SSFuncionarios.Administrativo;
import business.SSFuncionarios.Mecanico;
import business.SSFuncionarios.Registo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;

import data.AgendamentoDAO;
import data.DAOconfig;
import data.PostoDAO;

import java.sql.*;

// TODO que sao estes ids???
public class SSPostosFacade implements ISubPostos {
    private Map<String, Posto> todos_postos;
    private Map<String, Agendamento> todos_agendamentos;
    private Map<String, Servico> todos_servicos;

	public static class TimeInterval {
		private LocalDateTime start;
		private LocalDateTime end;
	
		public TimeInterval(LocalDateTime start, LocalDateTime end) {
			this.start = start;
			this.end = end;
		}
	
		public LocalDateTime getStart() {
			return start;
		}

		public LocalDateTime getEnd() {
			return end;
		}

		@Override
		public String toString() {
			return "From " + start + " to " + end;
		}
	}

    // public SSPostosFacade(Map<String, Posto> todosPostos, Map<String, Agendamento> todosAgendamentos, Map<String, Servico> todos_servicos) {
    //     this.todos_postos = todosPostos;
    //     this.todos_agendamentos = todosAgendamentos;
	// 	this.todos_servicos = todos_servicos;
    // }

	public SSPostosFacade() {
		this.todos_postos = PostoDAO.getInstance(null);
		this.todos_agendamentos = AgendamentoDAO.getInstance();
		this.todos_servicos = getServicos();
	}

	// TODO tirar daqui?????????
	private Map<String, Servico> getServicos() {
		HashMap<String, Servico> map = new HashMap<>();
		try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
			Statement stm = conn.createStatement()) {
			Map<String,String> tipoMotorDeServicos = new HashMap<String,String>(); // achei que fazer map de IdServico -> idTipoMotor é mais safe para as queries abaixo, caso alguma falhe a meio e as outras não, em vez de guardar só num arrayList os IdsTipoServico
			ResultSet rsa = stm.executeQuery("SELECT * FROM Servico");
			while (rsa.next()) {
				String idServico = rsa.getString("IdServico");
				int duracao = rsa.getInt("Duracao");
				String descricao = rsa.getString("Descricao");
				Servico novo = new Servico(idServico, duracao, descricao, null);
				
				tipoMotorDeServicos.put(idServico, String.valueOf(rsa.getInt("IdTM"))); //guardar idServico -> idTipoMotor
				map.put(idServico,novo); // meter já no map o serviço
			}

			// obter objetos tipo de servico da respetiva tabela
			for (Map.Entry<String,String> tipoMotorServ: tipoMotorDeServicos.entrySet()) {
				ResultSet rsc = stm.executeQuery("SELECT * FROM TipoMotor WHERE IdTipo='" + tipoMotorServ.getValue() + "'");
				if (rsc.next()) {
					TipoMotor tm = null;
					String tipoMotor = rsc.getString("Descricao");
					if (tipoMotor.equals("Eletrico")) {
						tm = new Eletrico(tipoMotorServ.getValue());
					}
					else if (tipoMotor.equals("Gasolina")) {
						tm = new Gasolina(tipoMotorServ.getValue());
					}
					else if (tipoMotor.equals("Diesel")) {
						tm = new Diesel(tipoMotorServ.getValue());
					}
					else if (tipoMotor.equals("Combustao")) {
						tm = new Combustao(tipoMotorServ.getValue());
					}
					else if (tipoMotor.equals("Universal")) {
						tm = new TipoMotor(tipoMotorServ.getValue());
					}
					else {
						throw new NullPointerException("Wrong service type");
					}
					map.get(tipoMotorServ.getKey()).setTipoMotor(tm);
				}
			}
        } catch (SQLException e) {
            // Database error!
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
		return map;
	}

	private void putServico(Servico s) {
		try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
			Statement stm = conn.createStatement()) {
			String tipo;
            stm.executeUpdate(
                    "INSERT INTO Servico " +
                                "VALUES ('"+ s.getIdServico()+"', '" +
											s.getDuracao()+ "', '"+
                                            s.getDescricao()+"', '" +
                                            s.getTipoMotor().getidTM() + "')");
			} catch (SQLException e) {
				// Database error!
				e.printStackTrace();
				throw new NullPointerException(e.getMessage());
			}
	}

	// TODO OCL a dizer que ta sorted e que considera horario funcionamento??
	// TODO provavelmente vai ao ar quando o intervalo de datas e maior do que um dia, mas pode ser um dia qualquer e fora do horario de funcionamento
	// LocalDateTime mas nao pegamos nas horas, e so por simplicidade
	// id posto -> lista de intervalos de tempo que podem ter o agendamento
	public Map<String, List<TimeInterval>> getPostosLivres(LocalDateTime targetDate, String servico, LocalTime abertura, LocalTime fecho) {
		Collection<Posto> postos = todos_postos.values();
		Servico ser = todos_servicos.get(servico);
		LocalDateTime horaAbertura = targetDate.toLocalDate().atTime(abertura); // pode-se aceder a métodos locais de outras Facades??? tava a bugar, passei como argumento à função, a classe superior da business layer que faça a chamada de SSFuncionarios.getHoraAbertura() em vez de abertura
		LocalDateTime horaFecho = targetDate.toLocalDate().atTime(fecho);
		Map<String, List<TimeInterval>> res = new HashMap<>();
		for (Posto posto : postos) {

			// intervalo de datas para usar em baixo
			// 0h.0m.0s
			LocalDateTime start = targetDate.with(LocalTime.MIN);
			// 23h.59m.59s
			LocalDateTime end = targetDate.with(LocalTime.MAX);

			List<TimeInterval> slots = posto.getVagas(start, end, ser, horaAbertura, horaFecho);
			if (slots != null) {
				res.put(posto.getIdPosto(), slots);
			}
		}
		return res;
	}

	public List<Servico> getTodosServicos() {
		return this.todos_servicos.values().stream()
					.map( s -> (Servico) s.clone())
					.sorted((s1,s2) -> s1.compareTo(s2))
					.collect(Collectors.toList());
	}

	public List<Posto> getTodosPostos() {
		return this.todos_postos.values().stream()
				.map( p -> (Posto) p.clone())
				.sorted((p1,p2) -> p1.compareTo(p2))
				.collect(Collectors.toList());
	}

	public Set<Servico> servicosPara(TipoMotor tm) {
		Set<Servico> servs = new TreeSet<>();
		for (Servico s: todos_servicos.values()) {
			TipoMotor servTipo = s.getTipoMotor();
			if (servTipo.getClass().isInstance(tm)) {
				servs.add(s.clone());
			}
		}
		return servs;
	}

	// assume que addServicoDisponivel trata de repeticoes
	public void registarServicoDePosto(String idPosto, String idServico) throws PostoNotFoundException, ServicosNotFoundException {
		Posto posto = todos_postos.get(idPosto);
		if (posto == null) {
			throw new PostoNotFoundException("Posto não existe");
		}
		Servico s = todos_servicos.get(idServico);
		if (s == null) {
			throw new ServicosNotFoundException("Servico não existe");
		}
		posto.addServicoDisponivel(s);
		todos_postos.put(idPosto, posto);
	}

	// apenas muda o bool do agendamento
	public void registarFimServico(String idAgendamento) {
		Agendamento agendamento = todos_agendamentos.get(idAgendamento);
		agendamento.setConcluido(true);
		todos_agendamentos.put(idAgendamento, agendamento);
	}

    public Posto getPosto(String idPosto) {
        return todos_postos.get(idPosto);
    }

    public void addPosto(Posto posto) {
        todos_postos.put(posto.getIdPosto(), posto);
    }

	public void addServico(int duracao, String descricao, TipoMotor tm) {
		String id = String.valueOf(todos_servicos.size() +1);
		Servico s = new Servico(id,duracao,descricao,tm);
		todos_servicos.put(id,s);
		putServico(s);
	}

    public Agendamento getAgendamento(String numAgendamento) {
        return todos_agendamentos.get(numAgendamento);
    }

    public void addAgendamento(Agendamento agendamento) {
        todos_agendamentos.put(agendamento.getNumAgendamento(), agendamento);
    }

	public boolean verificarPossivelAgendamento(LocalDateTime horaInic, LocalDateTime horaFim, String idServico, String idPosto) {
		//TODO
		return true;
		// String agendId = String.valueOf(todos_agendamentos.size() +1);
        // Servico serv = this.todos_servicos.get(idServico);

	}
    // public void criarAgendamento(LocalDateTime horaInic, Boolean notif, Veiculo veiculo, Cliente cliente, Servico servico, idPosto)
    public String criarAgendamento(LocalDateTime horaInic, Boolean notif, String idVeiculo, String idCliente, String idServico, String idPosto) {
        String agendId = String.valueOf(todos_agendamentos.size() +1);
        Servico serv = this.todos_servicos.get(idServico);
        Agendamento novoAgend = new Agendamento(agendId, horaInic, horaInic.plusMinutes(serv.getDuracao()), notif, false, null, idVeiculo, idCliente, serv, idPosto);
        this.todos_agendamentos.put(agendId,novoAgend);
        return agendId;
    }

    public void registarFalharServico(String idAgendamento, String falha) {
        Agendamento a = this.todos_agendamentos.get(idAgendamento);
        a.setConcluido(true);
        a.setMotivo(falha);
    }

    public String registarPosto() {
        String postoId = String.valueOf(todos_postos.size() + 1);
        Posto p = new Posto(postoId);
        this.todos_postos.put(postoId,p);
        return postoId;
    }

	public List<TipoMotor> getEspecialidadesPosto(String idPosto) {
		Posto p = todos_postos.get(idPosto);
		List<TipoMotor> list = p.getEspecialidades();
		return list;
	}

	public List<Agendamento> getPostoAgendamentosEntre(String idPosto, LocalDateTime inicio, LocalDateTime fim) {
		Posto posto = todos_postos.get(idPosto);
		return posto.getAgendamentosEntre(inicio, fim);
	}

	public Servico getServico(String idServico) {
		return todos_servicos.get(idServico);
	}
}
