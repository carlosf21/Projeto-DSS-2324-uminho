package data;

import business.SSPostos.Agendamento;
import business.SSPostos.Servico;
import business.SSClientes.*;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class AgendamentoDAO implements Map<String, Agendamento> {
    private static AgendamentoDAO singleton = null;

    private AgendamentoDAO() {
        try {
            Class.forName("org.mariadb.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static AgendamentoDAO getInstance() {
        if (AgendamentoDAO.singleton == null) {
            AgendamentoDAO.singleton = new AgendamentoDAO();
        }
        return AgendamentoDAO.singleton;
    }

    @Override
    public int size() {
        int i = 0;
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
			Statement stm = conn.createStatement();
			ResultSet rs = stm.executeQuery("SELECT count(*) FROM Agendamento")) {
			if(rs.next()) { //resultado estará na primeira linha
				i = rs.getInt(1); // resultado estará na primeira coluna dessa linha, no formato INT
			}
        }
        catch (Exception e) {
            // Erro a criar tabela...
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
        return i;
    }

    @Override
    public boolean isEmpty() {
        return this.size() == 0;
    }

    @Override
    public boolean containsKey(Object key) {
        boolean r;
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
            Statement stm = conn.createStatement();
            ResultSet rs =
                stm.executeQuery("SELECT IdAgendamento FROM Agendamento WHERE IdAgendamento='" + key.toString() + "'")) {
            r = rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
        return r;
    }

    @Override
    public boolean containsValue(Object value) {
		throw new NullPointerException("Not implemented!");
    }

    @Override
    public Agendamento get(Object key) {
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
            Statement stm = conn.createStatement();
            ResultSet rs = stm.executeQuery("SELECT * FROM Agendamento WHERE IdAgendamento='"+ key.toString() + "'")) {
			if (rs.next()) {
				
				String idAgendamento = rs.getString("IdAgendamento");
				java.sql.Timestamp timestampInicio = rs.getTimestamp("HoraInicio");
				LocalDateTime inicio = timestampInicio.toLocalDateTime();
				java.sql.Timestamp timestampFim = rs.getTimestamp("HoraFim");
				LocalDateTime fim = timestampFim.toLocalDateTime();
				Boolean notificar = rs.getBoolean("Notificar");
				Boolean concluido = rs.getBoolean("Concluido");
				String motivo = rs.getString("Motivo");
				String idServico = rs.getString("IdServico");
				String idVeiculo = rs.getString("IdVeiculo");
				String idCliente = rs.getString("IdCliente");
				String idPosto = rs.getString("IdPosto");

                Servico servico = null;
				ResultSet rsb = stm.executeQuery("SELECT * FROM Servico WHERE IdServico='" + idServico + "'");
                if (rsb.next()) {
                    int duracao = rsb.getInt("Duracao");
                    String tipoServ = rsb.getString("Descricao");
                    String idTM = String.valueOf(rsb.getInt("idTM"));

                    ResultSet rsc = stm.executeQuery("SELECT * FROM TipoMotor WHERE IdTipo='" + idTM + "'");
                    if (rsc.next()) {
                        TipoMotor tm = null;
                        String tipoMotor = rsc.getString("Descricao");
						if (tipoMotor.equals("Eletrico")) {
							tm = new Eletrico(idTM);
						}
						else if (tipoMotor.equals("Gasolina")) {
							tm = new Gasolina(idTM);
						}
						else if (tipoMotor.equals("Diesel")) {
							tm = new Diesel(idTM);
						}
						else if (tipoMotor.equals("Combustao")) {
							tm = new Combustao(idTM);
						}
                        else if (tipoMotor.equals("Universal")) {
							tm = new TipoMotor(idTM);
						}
                        else {
                            throw new NullPointerException("Wrong service type");
                        }
                        servico = new Servico(idServico, duracao, tipoServ, tm);
                    }
                return new Agendamento(idAgendamento, inicio, fim, notificar, concluido, motivo, idVeiculo, idCliente, servico, idPosto); // preciso de fazer clones aqui???????????
			    }
            }
        } catch (SQLException e) {
            // Database error!
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
        return null;
    }

    @Override
	// faz Agendamentos mas nao mecanicos
    public Agendamento put(String key, Agendamento a) {
        Agendamento ag = null;
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
			Statement stm = conn.createStatement()) {
			java.sql.Timestamp timestampInicio = java.sql.Timestamp.valueOf(a.getHoraInicio());
			java.sql.Timestamp timestampFim = java.sql.Timestamp.valueOf(a.getHoraFim());
            
            // Converter booleano em TININT(1) por causa do MariaDB
            int notificar = a.getNotificar() ? 1 : 0;
            int concluido = a.getConcluido() ? 1 : 0;
			String motivo = a.getMotivo() == null ? "NULL" : a.getMotivo();

            // Actualizar Agendamento
            stm.executeUpdate(
                    "INSERT INTO Agendamento " +
                                "VALUES ('"+ a.getNumAgendamento()+ "', '"+
                                             timestampInicio + "', '" +
                                             timestampFim + "', '" +
											 notificar + "', '" +
											 concluido + "', '" +
											 motivo + "', '" +
											 a.getServico().getIdServico() + "', '" +
											 a.getCliente().getNif() + "', '" +
											 a.getVeiculo().getMatricula() + "', '" +
											 a.getIdPosto() + "')" +
								" ON DUPLICATE KEY UPDATE "+
											// "IdAgendamento='" + a.getNumAgendamento()+ "', '"+
                                            // "HoraInicio='" + timestampInicio + "', '" +
                                            "HoraFim='" + timestampFim + "', " +
											// "Notificar='" + notificar + "', '" +
											"Concluido='" + concluido + "', " +
											"Motivo='" + motivo + "'"
											// "IdServico='" + a.getServico().getIdServico() + "', '" +
											// "IdCliente='" + a.getCliente().getNif() + "', '" +
											// "IdVeiculo='" + a.getVeiculo().getMatricula() + "', '" +
											// "IdPosto='" + a.getIdPosto() + "'"
											);

        } catch (SQLException e) {
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
        return ag;
    }

    @Override
    public Agendamento remove(Object key) {
        Agendamento a = this.get(key);
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
            Statement stm = conn.createStatement()) {

            stm.executeUpdate("DELETE FROM Agendamento WHERE IdAgendamento='"+ key.toString() +"'");

        } catch (Exception e) {
            // Database error!
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
        return a;
    }

    @Override
    public void putAll(Map<? extends String, ? extends Agendamento> Agendamentos) {
        for(Agendamento a : Agendamentos.values()) {
            this.put(a.getNumAgendamento(), a);
        }
    }

    @Override
    public void clear() {
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             Statement stm = conn.createStatement()) {
            stm.executeUpdate("TRUNCATE Agendamento");;
        } catch (SQLException e) {
            // Database error!
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
    }

    @Override
    public Set<String> keySet() {
        throw new NullPointerException("Not implemented!");
    }

    @Override
    public Collection<Agendamento> values() {
        Collection<Agendamento> res = new HashSet<>();
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
            Statement stm = conn.createStatement();
            ResultSet rs = stm.executeQuery("SELECT IdAgendamento FROM Agendamento")) {
            while (rs.next()) {
                String idAg = rs.getString("IdAgendamento");
                Agendamento a = this.get(idAg);
                res.add(a);
            }
        } catch (Exception e) {
            // Database error!
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
        return res;
    }

    @Override
    public Set<Entry<String, Agendamento>> entrySet() {
        throw new NullPointerException("public Set<Map.Entry<String,Agendamento>> entrySet() not implemented!");
    }
}

