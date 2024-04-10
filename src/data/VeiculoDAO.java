package data;

import business.SSClientes.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class VeiculoDAO implements Map<String, Veiculo> { // implementa Map para n ter de alterar a Facade
    private static VeiculoDAO singleton = null;

    private VeiculoDAO(Map<String,Veiculo> a) {
        //touch mariadb
        try {
            Class.forName("org.mariadb.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static VeiculoDAO getInstance(Map<String,Veiculo> a) {
        if (VeiculoDAO.singleton == null) {
            VeiculoDAO.singleton = new VeiculoDAO(a);
        }
        return VeiculoDAO.singleton;
    }

    @Override
    public int size() {
        int i = 0;
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             Statement stm = conn.createStatement();
             ResultSet rs = stm.executeQuery("SELECT count(*) FROM Veiculo")) { 
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
                     stm.executeQuery("SELECT Matricula FROM Veiculo WHERE Matricula='"+key.toString()+"'")) {
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
        return false;
    }

    @Override
    public Veiculo get(Object key) {
        Veiculo res = null;
        List<String> servRecomendadosVeiculo = new ArrayList<String>();
        String modelo = null;
        String nifCliente = null;
        TipoMotor motor1 = null;
        TipoMotor motor2 = null;

        int i = 0;
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
            Statement stm = conn.createStatement()) {
                ResultSet rs = stm.executeQuery("SELECT * FROM ServicosRecomendados WHERE MatrVeiculo='"+key+"'");
                    while (rs.next()) {
                        i = rs.getInt("IdServico");
                        servRecomendadosVeiculo.add(String.valueOf(i));
                    }

                ResultSet rsa = stm.executeQuery("SELECT * FROM Veiculo WHERE Matricula='"+key+"'");
                    if(rsa.next()) {
                        modelo = rsa.getString("Modelo");
                        nifCliente = rsa.getString("NifCliente");
                        String idTM = String.valueOf(rsa.getInt("IdTipoMotor"));
                        int idTM2 = -1;
                        Object checkTm2 = rsa.getObject("IdTipoMotor2");
                        if (checkTm2 != null) {
                            idTM2 = rsa.getInt("IdTipoMotor2");
                        }
                        ResultSet rsb = stm.executeQuery("SELECT Descricao FROM TipoMotor WHERE IdTipo='"+idTM+"'");
                        if (rsb.next()) {
                            String tipoMotor = rsb.getString("Descricao");
                            if (tipoMotor.equals("Eletrico")) {
                                motor1 = new Eletrico(idTM);
                            }
                            else if (tipoMotor.equals("Gasolina")) {
                                motor1 = new Gasolina(idTM);
                            }
                            else if (tipoMotor.equals("Diesel")) {
                                motor1 = new Diesel(idTM);
                            }
                            else {
                                throw new NullPointerException("Wrong service type");
                            }
                        }
                        if (idTM2 != -1) {
                            ResultSet rsc = stm.executeQuery("SELECT Descricao FROM TipoMotor WHERE IdTipo='"+idTM2+"'");
							if (rsc.next()) {
                                String tipoMotor = rsc.getString("Descricao");
                                if (tipoMotor.equals("Eletrico")) {
                                    motor2 = new Eletrico(idTM);
                                }
                                else if (tipoMotor.equals("Gasolina")) {
                                    motor2 = new Gasolina(idTM);
                                }
                                else if (tipoMotor.equals("Diesel")) {
                                    motor2 = new Diesel(idTM);
                                }
                                else {
                                    throw new NullPointerException("Wrong service type");
                                }
							}
                        }
					}

                res = new Veiculo(key.toString(), modelo, motor1, motor2, nifCliente, servRecomendadosVeiculo);

        } catch (SQLException e) {
                // Database error!
                e.printStackTrace();
                throw new NullPointerException(e.getMessage());
        }
        return res;
    }


    @Override
    public Veiculo put(String key, Veiculo v) {
        Veiculo res = null;

        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
            PreparedStatement pstmt = conn.prepareStatement("INSERT INTO ServicosRecomendados VALUES (?, ?) " +
            "ON DUPLICATE KEY UPDATE ServicosRecomendados=VALUES(ServicosRecomendados)")) {
            for (String serv : v.getServicosRecomendados()) {
                pstmt.setString(1, key.toString());
                pstmt.setString(2, serv);
                pstmt.executeUpdate();
            }

            PreparedStatement pstmt2 = conn.prepareStatement("INSERT INTO Veiculo (Matricula, Modelo, IdTipoMotor, IdTipoMotor2, NifCliente) VALUES (?, ?, ?, ?, ?)");
                pstmt2.setString(1,v.getMatricula());
                pstmt2.setString(2, v.getModelo());
                pstmt2.setInt(3, Integer.parseInt(v.getTipoMotor1().getidTM()));
                if (v.getTipoMotor2() != null) {
                    pstmt2.setInt(4, Integer.parseInt(v.getTipoMotor2().getidTM()));  // Replace tipoMotor2.getId() with the actual ID or value
                } else {
                    pstmt2.setNull(4, Types.INTEGER);  // Set the second foreign key to NULL
                }
                pstmt2.setString(5, v.getCliente());  // Replace otherColumnValue with the actual value
            
                pstmt2.executeUpdate();

        } catch (SQLException e) {
            // Database error!
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
        return res;
    }

    @Override
    public Veiculo remove(Object key) {
        Veiculo t = this.get(key);
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             Statement stm = conn.createStatement()) {
            stm.executeUpdate("DELETE FROM Veiculo WHERE Matricula'"+key+"'");
            stm.executeUpdate("DELETE FROM ServicosRecomendados WHERE MatrVeiculo'"+key+"'");
        } catch (Exception e) {
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
        return t;
    }

    @Override
    public void putAll(Map<? extends String, ? extends Veiculo> Veiculos) {
        for(Veiculo c : Veiculos.values()) {
            this.put(c.getMatricula (), c);
        }
    }

    @Override
    public void clear() {
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             Statement stm = conn.createStatement()) {
            stm.executeUpdate("TRUNCATE Veiculo");
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
    public Collection<Veiculo> values() {
        Collection<Veiculo> res = new HashSet<>();
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             Statement stm = conn.createStatement();
             ResultSet rs = stm.executeQuery("SELECT Matricula FROM Veiculo")) {
            while (rs.next()) {
                String idt = rs.getString("Matricula");
                Veiculo t = this.get(idt);
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
    public Set<Entry<String, Veiculo>> entrySet() {
        throw new NullPointerException("public Set<Map.Entry<String,Veiculo>> entrySet() not implemented!");
    }
}
