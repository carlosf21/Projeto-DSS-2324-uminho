package data;

import business.SSClientes.Cliente;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ClienteDAO implements Map<String, Cliente> { // implementa Map para n ter de alterar a Facade
    private static ClienteDAO singleton = null;

    private ClienteDAO(Map<String,Cliente> a) {
        try {
            Class.forName("org.mariadb.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static ClienteDAO getInstance(Map<String,Cliente> a) {
        if (ClienteDAO.singleton == null) {
            ClienteDAO.singleton = new ClienteDAO(a);
        }
        return ClienteDAO.singleton;
    }

    @Override
    public int size() {
        int i = 0;
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             Statement stm = conn.createStatement();
             ResultSet rs = stm.executeQuery("SELECT count(*) FROM Cliente")) { 
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
                     stm.executeQuery("SELECT Nif FROM Cliente WHERE Nif='"+key+"'")) {
            r = rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
        return r;
    }

    @Override
    public boolean containsValue(Object value) {
        return false;
    }

    @Override
    public Cliente get(Object key){// throws ClienteNotFoundException {
        Cliente c = null;
        List<String> matrVeiculosCliente = new ArrayList<String>();
        
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             Statement stm = conn.createStatement();
             ResultSet rs = stm.executeQuery("SELECT * FROM Veiculo WHERE NifCliente='"+key+"'")) {
             while(rs.next()) {
                matrVeiculosCliente.add(rs.getString("Matricula"));
             }
             try (ResultSet rsa = stm.executeQuery("SELECT * FROM Cliente WHERE Nif='"+key+"'")) { 
                if (rsa.next()) { 
                    c = new Cliente(rsa.getString("Nif"), rsa.getString("Nome"),rsa.getString("Morada"),rsa.getString("Telefone"),rsa.getString("Email"),matrVeiculosCliente);
                } else {
                    //throw new ClienteNotFoundException();
                }
             }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
        return c;
    }


    @Override
    public Cliente put(String key, Cliente c) {
        Cliente res = null;
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             Statement stm = conn.createStatement()) {

            // Actualizar Cliente
            stm.executeUpdate(
                    "INSERT INTO Cliente " +
                                "VALUES ('"+ c.getNif()+ "', '"+
                                            c.getNome()+"', '" +
                                            c.getMorada()+"', '" +
                                            c.getTelefone()+"', '" +
                                            c.getEmail()+ "')");

            // Atualizar alguma cena de veículos ???

        } catch (SQLException e) {
            // Database error!
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
        return res;
    }

    @Override
    public Cliente remove(Object key) {
        Cliente t = this.get(key);
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             Statement stm = conn.createStatement()) {
            stm.executeUpdate("DELETE FROM Cliente WHERE Nif='"+key+"'");
        } catch (Exception e) {
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
        return t;
    }

    @Override
    public void putAll(Map<? extends String, ? extends Cliente> clientes) {
        for(Cliente c : clientes.values()) {
            this.put(c.getNif(), c);
        }
    }

    @Override
    public void clear() {
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             Statement stm = conn.createStatement()) {
            stm.executeUpdate("UPDATE Veiculo SET NifCliente=NULL");
            stm.executeUpdate("TRUNCATE Cliente");
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
    public Collection<Cliente> values() {
        Collection<Cliente> res = new HashSet<>();
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             Statement stm = conn.createStatement();
             ResultSet rs = stm.executeQuery("SELECT Nif FROM Cliente")) {
            while (rs.next()) {
                String idt = rs.getString("Nif");
                Cliente t = this.get(idt);
                res.add(t);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
        return res;
    }

    @Override
    public Set<Entry<String, Cliente>> entrySet() {
        throw new NullPointerException("public Set<Map.Entry<String,Cliente>> entrySet() not implemented!");
    }
}
