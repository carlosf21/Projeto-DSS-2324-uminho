package business.SSFuncionarios;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import business.SSClientes.TipoMotor;
import data.DAOconfig;
import data.FuncionarioDAO;
import data.RegistoDAO;

public class SSFuncionariosFacade implements ISubFuncionarios {
    private Map<String, Registo> todos_registos;
    // private Map<String, Funcionario> todos_funcionarios; // tive de adicionar um getPorNome deixou de poder ser um map
	private FuncionarioDAO todos_funcionarios;
    private LocalTime horaAbertura;
    private LocalTime horaFecho;

    // public SSFuncionariosFacade(Map<String, Registo> registos, Map<String, Funcionario> todosFuncionarios, LocalTime horaAbertura, LocalTime horaFecho) {
    //     this.todos_registos = registos;
    //     this.todos_funcionarios = todosFuncionarios;
    //     this.horaAbertura = horaAbertura;
    //     this.horaFecho = horaFecho;
    // }

    public SSFuncionariosFacade() {
        this.todos_registos = RegistoDAO.getInstance(null);
        this.todos_funcionarios = FuncionarioDAO.getInstance(null);
        Pair<LocalTime, LocalTime> horario = getHorarioOficina();
        this.horaAbertura = horario.fst;
        this.horaFecho = horario.snd;
    }

    //TODO este tipo de funções n deviam passar para a camada de  data talvez e chamar só aqui as funções de lá???

    private Pair<LocalTime, LocalTime> getHorarioOficina() {
        Pair<LocalTime, LocalTime> res = null;

        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
            Statement stm = conn.createStatement();
            ResultSet rs = stm.executeQuery("SELECT * FROM Horario")) {

            if (rs.next()) {
                Time horaAberturaSql = rs.getTime("HoraAbertura");
                Time horaFechoSql = rs.getTime("HoraFecho");

                LocalTime horaAbertura = horaAberturaSql.toLocalTime();
                LocalTime horaFecho = horaFechoSql.toLocalTime();

                res = new Pair<>(horaAbertura, horaFecho);
            }
        } catch (SQLException e) {
            // Database error!
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }

        return res;
    }

    //TODO este tipo de funções n deviam passar para a camada de  data talvez e chamar só aqui as funções de lá???
    //atualiza variáveis locais mas também mete na tabela da BD
    private void setHorarioEstacao(LocalTime inic, LocalTime fim) {
        this.horaAbertura = inic;
        this.horaFecho = fim;

        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
			Statement stm = conn.createStatement()) {
            java.sql.Time sqlHoraAbertura = java.sql.Time.valueOf(inic);
            java.sql.Time sqlHoraFecho = java.sql.Time.valueOf(fim);

            // Actualizar Horario estacao
            stm.executeUpdate(
                "INSERT INTO Horario (IdHorario, HoraAbertura, HoraFecho)" +
                        "VALUES (1, '" + sqlHoraAbertura + "', '" + sqlHoraFecho + "')" +
                        "ON DUPLICATE KEY UPDATE HoraAbertura = '" + sqlHoraAbertura + "', HoraFecho = '" + sqlHoraFecho + "'");
    } catch (SQLException e) {
            // Database error!
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
    }

    public LocalTime getHoraAbertura() {
        return this.horaAbertura;
    }

    public LocalTime getHoraFecho() {
        return this.horaFecho;
    }

    public Registo getRegistro(String idRegisto) {
        return todos_registos.get(idRegisto);
    }

    public void addRegistro(Registo registo) {
        this.todos_registos.put(registo.getIdRegisto(), registo);
    }

    public Funcionario getFuncionario(String idFuncionario) {
        return todos_funcionarios.get(idFuncionario);
    }

    public void addFuncionario(Funcionario funcionario) {
        todos_funcionarios.put(funcionario.getIdFunc(), funcionario);
    }

    public String registarAdministrativo(String nome) {
        // Gerar um ID único para o novo funcionário (cursed)
        String idFuncionario = String.valueOf(todos_funcionarios.size() +1);
    
        Funcionario novoFuncionario = new Administrativo(idFuncionario, nome);

		// Adicionar o novo funcionário à coleção
        addFuncionario(novoFuncionario);
        return idFuncionario;
    }

	public String registarMecanico(String nome, TipoMotor idTipoMotor) {
        // Gerar um ID único para o novo funcionário (cursed)
        String idFuncionario = String.valueOf(todos_funcionarios.size() +1);
        Funcionario novoFuncionario = new Mecanico(idFuncionario, nome, idTipoMotor);

		// Adicionar o novo funcionário à coleção
        addFuncionario(novoFuncionario);
        return idFuncionario;
    }

    public void registarEntradaTurno(String idFunc, LocalDateTime horaInic) throws FuncionarioNotFoundException{
        Funcionario funcionario = getFuncionario(idFunc);

        if (funcionario != null) {
            // Gerar um ID único para o novo registro
            String idRegisto = String.valueOf(todos_registos.size() +1); // cursed

            // Criar um novo registro de entrada de turno
            Registo novoRegisto = new Registo(idRegisto, horaInic, null, (Mecanico) funcionario);

            // Adicionar o novo registro à coleção
            addRegistro(novoRegisto);

        } else {
            throw new FuncionarioNotFoundException("Funcionário não encontrado. Não foi possível registrar a entrada de turno.");
        }
    }

    public void registarSaidaTurno(String idFunc, LocalDateTime horaSaid) throws FuncionarioNotFoundException, RegistoNotFoundException  {
        Funcionario funcionario = getFuncionario(idFunc);

        if (funcionario != null) {
            // Procurar o último registro de entrada de turno para o funcionário
            Registo ultimoRegistoEntrada = null;
            for (Registo registo : this.todos_registos.values()) {
                if (registo.getMecanico().getIdFunc().equals(idFunc) && registo.getHoraSaida() == null) { // TODO ver se este null vai efetivamente estar a null
                    ultimoRegistoEntrada = registo;
                    break;
                }
            }

            if (ultimoRegistoEntrada != null) {
                // Atualizar a hora de saída no último registro de entrada
                ultimoRegistoEntrada.setHoraSaida(horaSaid);

            } else {
                throw new RegistoNotFoundException ("Não foi encontrada uma entrada de turno em aberto para o funcionário " + idFunc);
            }
        } else {
            throw new FuncionarioNotFoundException ("Funcionário não encontrado. Não foi possível registrar a saída de turno.");
        }
    }

    public boolean verificarFuncionarioSerMecanico(String idFunc) {
        Funcionario funcionario = getFuncionario(idFunc);

		if (funcionario == null) return false;

		if (funcionario instanceof Mecanico) return true;

		return false;
    }

	public boolean verificarCompMecanico(String idMecanico, List<TipoMotor> tms) {
        boolean res = false;
        Funcionario f = getFuncionario(idMecanico);
        if (verificarFuncionarioSerMecanico(idMecanico)) {
            res = true;
            TipoMotor competencias = ((Mecanico) f).getCompetencias();
            for (TipoMotor t: tms) {
                if (! (t.getClass().isInstance(competencias))) {
                    res = false;
                    break;
                }
            }
        }
		return res;
	}

	public void registarHorarioEstacao(LocalTime horaAbertura, LocalTime horaFecho) {
		this.horaAbertura = horaAbertura;
		this.horaFecho = horaFecho;
        setHorarioEstacao(horaAbertura, horaFecho);
	}

	public Funcionario getFuncionarioPorNome(String nome) {
		return todos_funcionarios.getPorNome(nome);
	}

    public class Pair<T, U> {         
        public final T fst;
        public final U snd;
    
        public Pair(T t, U u) {         
            this.fst= t;
            this.snd= u;
        }
    }
}