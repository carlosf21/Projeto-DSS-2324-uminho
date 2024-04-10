package data;

import business.SSFuncionarios.Registo;
import business.SSFuncionarios.Mecanico;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class RegistoDAO implements Map<String, Registo> {
    private static RegistoDAO singleton = null;
	private static FuncionarioDAO funcionarioDAO = FuncionarioDAO.getInstance(null);

    private RegistoDAO(Map<String, Registo> a) {
        try {
            Class.forName("org.mariadb.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static RegistoDAO getInstance(Map<String, Registo> a) {
        if (RegistoDAO.singleton == null) {
            RegistoDAO.singleton = new RegistoDAO(a);
        }
        return RegistoDAO.singleton;
    }

    @Override
    public int size() {
        int i = 0;
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
			Statement stm = conn.createStatement();
			ResultSet rs = stm.executeQuery("SELECT count(*) FROM Registo")) { 
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
                stm.executeQuery("SELECT IdRegisto FROM Registo WHERE IdRegisto='" + key.toString() + "'")) {
            r = rs.next();
        } catch (SQLException e) {
            // Database error!
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
    public Registo get(Object key) {
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
            Statement stm = conn.createStatement();
            ResultSet rs = stm.executeQuery("SELECT * FROM Registo WHERE IdRegisto='"+ key.toString() + "'")) {
			if (rs.next()) {
				String idRegisto = rs.getString("IdRegisto");
				LocalDateTime entrada = rs.getTimestamp("HoraEntrada").toLocalDateTime();				
				Object checkSaida = rs.getObject("HoraSaida");
				LocalDateTime saida = null;
				if (checkSaida != null) {
					saida = rs.getTimestamp("HoraSaida").toLocalDateTime();
				}
				String idMecanico = rs.getString("IdMecanico");
				Mecanico mecanico = (Mecanico)funcionarioDAO.get(idMecanico); // !!!!!!!!!!!!!!!!!!!!!! ja temos a certeza que e mecanico mas mesmo assim nao gosto muito disto. tive preguica de tar a ir a mais 2 tabelas manualmente aqui
				return new Registo(idRegisto, entrada, saida, mecanico);
			}
        } catch (SQLException e) {
            // Database error!
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
        return null;
    }

    @Override
	// faz registos mas nao mecanicos
    public Registo put(String key, Registo reg) {
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
            PreparedStatement pstmt = conn.prepareStatement("INSERT INTO Registo (IdRegisto, HoraEntrada, HoraSaida, IdMecanico) VALUES (?, ?, ?, ?)")) {
            pstmt.setInt(1,Integer.parseInt(key));
            pstmt.setTimestamp(2, java.sql.Timestamp.valueOf(reg.getHoraEntrada()));  // Assuming registo.getHoraEntrada() returns LocalDateTime
            if (reg.getHoraSaida() != null) {
                pstmt.setTimestamp(3, java.sql.Timestamp.valueOf(reg.getHoraSaida()));  // Assuming registo.getHoraSaida() returns LocalDateTime
            } else {
                pstmt.setNull(3, java.sql.Types.TIMESTAMP);  // Set HoraSaida to NULL
            }
            pstmt.setInt(4, Integer.valueOf(reg.getMecanico().getIdFunc()));  // Assuming registo.getIdMecanico() returns int

            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
        return null;
    }

    @Override
    public Registo remove(Object key) {
        Registo a = this.get(key);
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
            Statement stm = conn.createStatement()) {

            stm.executeUpdate("DELETE FROM Registo WHERE IdRegisto='"+ key.toString() +"'");

        } catch (Exception e) {
            // Database error!
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
        return a;
    }

    @Override
    public void putAll(Map<? extends String, ? extends Registo> Registos) {
        for(Registo r : Registos.values()) {
            this.put(r.getIdRegisto(), r);
        }
    }

    @Override
    public void clear() {
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             Statement stm = conn.createStatement()) {
            stm.executeUpdate("TRUNCATE Registo");;
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
    public Collection<Registo> values() {
        Collection<Registo> res = new HashSet<>();
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             Statement stm = conn.createStatement();
             ResultSet rs = stm.executeQuery("SELECT IdRegisto FROM Registo")) {
            while (rs.next()) {
                String idt = rs.getString("IdRegisto");
                Registo t = this.get(idt);
                res.add(t);
            }
        } catch (Exception e) {
            // Database error!
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
        return res;
    }

    @Override
    public Set<Entry<String, Registo>> entrySet() {
        throw new NullPointerException("public Set<Map.Entry<String,Registo>> entrySet() not implemented!");
    }
}
