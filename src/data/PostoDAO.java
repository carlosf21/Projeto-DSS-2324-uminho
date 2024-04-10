package data;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import business.SSClientes.Combustao;
import business.SSClientes.Diesel;
import business.SSClientes.Eletrico;
import business.SSClientes.Gasolina;
import business.SSClientes.TipoMotor;
import business.SSPostos.Posto;
import business.SSPostos.Servico;


public class PostoDAO implements Map<String, Posto> {
    private static PostoDAO singleton = null;

    private PostoDAO(Map<String,Posto> a) {
        try {
            Class.forName("org.mariadb.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static PostoDAO getInstance(Map<String,Posto> a) {
        if (PostoDAO.singleton == null) {
            PostoDAO.singleton = new PostoDAO(a);
        }
        return PostoDAO.singleton;
    }

    @Override
    public int size() {
        int i = 0;
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             Statement stm = conn.createStatement();
             ResultSet rs = stm.executeQuery("SELECT count(*) FROM Posto")) {
            if(rs.next()) { //resultado estará na primeira linha
                i = rs.getInt(1); // resultado estará na primeira coluna dessa linha, no formato INT
            }
        }
        catch (Exception e) {
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
                     stm.executeQuery("SELECT IdPosto FROM Posto WHERE IdPosto='"+key.toString()+"'")) {
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
        Posto p = (Posto) value;
        return this.containsKey(p.getIdPosto());
    }

    @Override
    public Posto get(Object key) {
        Posto t = null;
        List<String> agendamentosPosto = new ArrayList<String>();
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
            Statement stm = conn.createStatement();
            // buscar agendamentos de posto
            ResultSet rs = stm.executeQuery("SELECT * FROM Posto WHERE IdPosto='"+key+"'")) { // devolve uma linha, com o número da Posto (se existir) e a sala correspondente
            if (!rs.next()) {
				// entrada nao existe, logo posto nao existe
				return null;
			}
			ResultSet rsag = stm.executeQuery("SELECT * FROM Agendamento WHERE IdPosto='"+key+"'");
				while (rsag.next()) {  // A chave existe na tabela
                // Reconstruir a colecção de agendamentos da Posto
                agendamentosPosto.add(rsag.getString("IdAgendamento")); // coleção de agendamentos, cujo Posto é a do número dado
            }

            //buscar servicos de posto
            ResultSet rsa = stm.executeQuery("SELECT * FROM PostoServico WHERE IdPosto='"+key+"'");

            List<String> stringServicosPosto = new ArrayList<String>();

            while (rsa.next()) { 
                stringServicosPosto.add(rsa.getString("IdServico"));
            }
            
            // para cada um dos servicos, buscar tipo de motor
            // aproveitar query para construir já parte dos servicos
            Map<String,String> tipoMotorDeServicos = new HashMap<String,String>(); //enquanto delirava, achei que fazer map de IdServico -> idTipoMotor é mais safe para as queries abaixo, caso alguma falhe a meio e as outras não, em vez de guardar só num arrayList os IdsTipoServico
            Map<String,Servico> serv_disponiveis = new HashMap<>();

            for (String serv: stringServicosPosto) {
                ResultSet rsb = stm.executeQuery("SELECT * FROM Servico WHERE IdServico='" + serv + "'");
					if (rsb.next()) {
						int duracao = rsb.getInt("Duracao");
						String descricao = rsb.getString("Descricao");
						Servico novo = new Servico(serv, duracao, descricao, null);
	
						serv_disponiveis.put(serv,novo); // meter já no map o serviço
						tipoMotorDeServicos.put(serv, rsb.getString("IdTM")); //guardar idServico -> idTipoMotor
					} // else??????
            }

            // obter objetos tipo de servico da respetiva tabela
            for (Entry<String,String> tipoMotorServ: tipoMotorDeServicos.entrySet()) {
                ResultSet rsc = stm.executeQuery("SELECT * FROM TipoMotor WHERE IdTipo='" + tipoMotorServ.getValue() + "'");
				if (rsc.next()) {
                    TipoMotor tm;
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
                    
					serv_disponiveis.get(tipoMotorServ.getKey()).setTipoMotor(tm); // atribuir tipoServico a cada servico
				} // else??????????????/
            }

            // Reconstruir a Posto com os dados obtidos da BD 
            t = new Posto(key.toString(), agendamentosPosto, serv_disponiveis); //tendo as partes, constrói-se o objeto Posto e devolve-se
        } catch (SQLException e) {
            // Database error!
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
        return t;
    }

    @Override
    public Posto put(String key, Posto p) {
        Posto res = null;
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             Statement stm = conn.createStatement()) {

            // Atualizar posto na base de dados
            stm.executeUpdate(
                "INSERT INTO Posto (IdPosto) VALUES ('" + p.getIdPosto() + "') " +
                "ON DUPLICATE KEY UPDATE IdPosto=VALUES(IdPosto)");
            
            for (Servico serv : p.getServicosDisponiveis().values()) {
                stm.executeUpdate(
                    "INSERT INTO PostoServico (IdPosto, IdServico) VALUES ('" + key.toString() + "', '" + serv.getIdServico() + "') " +
                    "ON DUPLICATE KEY UPDATE IdPosto=VALUES(IdPosto), IdServico=VALUES(IdServico)");
            }

        } catch (SQLException e) {
            // Database error!
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
        return res;
    }

    @Override
    public Posto remove(Object key) {
        Posto t = this.get(key);
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             Statement stm = conn.createStatement()) {

            stm.executeUpdate("DELETE FROM Postos WHERE Id='"+key.toString()+"'");
            stm.executeUpdate("DELETE FROM PostoServico WHERE IdPsto='" + key.toString() + "'");
        } catch (Exception e) {
            // Database error!
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
        return t;
    }

    @Override
    public void putAll(Map<? extends String, ? extends Posto> Postos) {
        for(Posto t : Postos.values()) {
            this.put(t.getIdPosto(), t);
        }
    }

    @Override
    public void clear() {
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             Statement stm = conn.createStatement()) {
            stm.executeUpdate("UPDATE PostoServico SET IdPosto=NULL");
            stm.executeUpdate("TRUNCATE Posto");
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
    public Collection<Posto> values() {
        Collection<Posto> res = new HashSet<>();
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
			Statement stm = conn.createStatement();
			ResultSet rs = stm.executeQuery("SELECT IdPosto FROM Posto")) {
            while (rs.next()) {
                String id = String.valueOf(rs.getInt("IdPosto"));
                Posto p = this.get(id);
                res.add(p);
            }
        } catch (Exception e) {
            // Database error!
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
        return res;
    }

    @Override
    public Set<Entry<String, Posto>> entrySet() {
        throw new NullPointerException("public Set<Map.Entry<String,Posto>> entrySet() not implemented!");
    }
}
