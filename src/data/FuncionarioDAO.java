package data;

import business.SSClientes.Combustao;
import business.SSClientes.Diesel;
import business.SSClientes.Eletrico;
import business.SSClientes.Gasolina;
import business.SSClientes.TipoMotor;
import business.SSFuncionarios.Administrativo;
import business.SSFuncionarios.Funcionario;
import business.SSFuncionarios.Mecanico;

import java.sql.*;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class FuncionarioDAO implements Map<String, Funcionario> {
    private static FuncionarioDAO singleton = null;

    private FuncionarioDAO(Map<String, Funcionario> a) {
        try {
            Class.forName("org.mariadb.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static FuncionarioDAO getInstance(Map<String, Funcionario> a) {
        if (FuncionarioDAO.singleton == null) {
            FuncionarioDAO.singleton = new FuncionarioDAO(a);
        }
        return FuncionarioDAO.singleton;
    }

    @Override
    public int size() {
        int i = 0;
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
			Statement stm = conn.createStatement();
			ResultSet rs = stm.executeQuery("SELECT count(*) FROM Funcionario")) { 
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
                     stm.executeQuery("SELECT IdFunc FROM Funcionario WHERE IdFunc='"+key.toString()+"'")) {
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
    public Funcionario get(Object key) {
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
            Statement stm = conn.createStatement();
            ResultSet rs = stm.executeQuery("SELECT * FROM Funcionario WHERE IdFunc='"+ key.toString() + "'")) {
            if (rs.next()) {
                String idFunc = key.toString();
                String nome = rs.getString("Nome");
                String tipo = rs.getString("TipoFunc");
                
                if (tipo.equals("Administrativo")) {
                    return new Administrativo(idFunc, nome);
                    
                } else if (tipo.equals("Mecanico")) {
                    TipoMotor tm = null;
                    ResultSet rsb = stm.executeQuery("SELECT * FROM Mecanico WHERE IdMecanico='"+ idFunc + "'");
                    if (rsb.next()) {
                        String idTipoMotor = String.valueOf(rsb.getInt("IdTipoMotor"));
                        ResultSet rsc = stm.executeQuery("SELECT * FROM TipoMotor WHERE IdTipo='"+ idTipoMotor + "'");
                        if (rsc.next()) {
                            String tipoMotor = rsc.getString("Descricao");
                            if (tipoMotor.equals("Eletrico")) {
                                tm = new Eletrico(idTipoMotor);
                            }
                            else if (tipoMotor.equals("Gasolina")) {
                                tm = new Gasolina(idTipoMotor);
                            }
                            else if (tipoMotor.equals("Diesel")) {
                                tm = new Diesel(idTipoMotor);
                            }
                            else if (tipoMotor.equals("Combustao")) {
                                tm = new Combustao(idTipoMotor);
                            }
                            else if (tipoMotor.equals("Universal")) {
                                tm = new TipoMotor(idTipoMotor);
                            }
                            else {
                                throw new NullPointerException("Wrong service type");
                            }
                        }
						return new Mecanico(idFunc, nome, tm);
                    } else {
						return null;
					}
                } else {
                    return null;
                }
            }
        } catch (SQLException e) {
            // Database error!
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
        return null;
    }

	public Funcionario getPorNome(String nome) {
		try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
            Statement stm = conn.createStatement();
            ResultSet rs = stm.executeQuery("SELECT * FROM Funcionario WHERE Nome='"+ nome + "'")) {
            if (rs.next()) {
                String idFunc = rs.getString("IdFunc");
                String tipo = rs.getString("TipoFunc");

                if (tipo.equals("Administrativo")) {
                    return new Administrativo(idFunc, nome);
                    
                } else if (tipo.equals("Mecanico")) {
                    TipoMotor tm = null;
                    ResultSet rsb = stm.executeQuery("SELECT * FROM Mecanico WHERE IdMecanico='"+ idFunc + "'");
                    if (rsb.next()) {
                        String idTipoMotor = rsb.getString("IdTipoMotor");
                        ResultSet rsc = stm.executeQuery("SELECT * FROM TipoMotor WHERE IdTipo='"+ idTipoMotor + "'");
                        if (rsc.next()) {
                            String tipoMotor = rsc.getString("Descricao");
                            if (tipoMotor.equals("Eletrico")) {
                                tm = new Eletrico(idTipoMotor);
                            }
                            else if (tipoMotor.equals("Gasolina")) {
                                tm = new Gasolina(idTipoMotor);
                            }
                            else if (tipoMotor.equals("Diesel")) {
                                tm = new Diesel(idTipoMotor);
                            }
                            else if (tipoMotor.equals("Combustao")) {
                                tm = new Combustao(idTipoMotor);
                            }
                            else if (tipoMotor.equals("Universal")) {
                                tm = new TipoMotor(idTipoMotor);
                            }
                            else {
                                throw new NullPointerException("Wrong service type");
                            }
                        }
                    }
                    return new Mecanico(idFunc, nome, tm);
                } else {
                    return null;
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
    public Funcionario put(String key, Funcionario f) {
        Funcionario res = null;
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
			Statement stm = conn.createStatement()) {
			String tipo;

			// chato ter de repetir isto mas temos de saber o tipo do funcionario, e o funcionario tem de ser criado ja
			if (f instanceof Mecanico) {
				tipo = "Mecanico";
			} else if (f instanceof Administrativo) {
				tipo = "Administrativo";
			} else {
				tipo = null; // vai dar erro mas acho que nunca acontece
			}

			// Actualizar Funcionario
			stm.executeUpdate(
					"INSERT INTO Funcionario " +
								"VALUES ('"+ f.getIdFunc()+ "', '"+
											 f.getNome()+"', '" +
											 tipo+"')");

			if (f instanceof Mecanico) {
				tipo = "Mecanico";
				stm.executeUpdate(
					"INSERT INTO Mecanico " +
						"VALUES ('" + f.getIdFunc() + "', '" +
									  ((Mecanico) f).getCompetencias().getidTM()+"')"
				);
			} else if (f instanceof Administrativo) {
				tipo = "Administrativo";
				stm.executeUpdate(
					"INSERT INTO Administrativo " +
						"VALUES ('" + f.getIdFunc() + "')");
			} else {
				return null;
			}

        } catch (SQLException e) {
            // Database error!
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
        return res;
    }

    @Override
    public Funcionario remove(Object key) {
        Funcionario f = this.get(key);
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
            Statement stm = conn.createStatement()) {

			if (f instanceof Mecanico) {
				stm.executeUpdate("DELETE FROM Mecanico WHERE IdMecanico='" + key.toString() + "'");
			} else if (f instanceof Administrativo) {
				stm.executeUpdate("DELETE FROM Administrativo WHERE IdAdministrativo='" + key.toString() + "'");
			}
            stm.executeUpdate("DELETE FROM Funcionario WHERE IdFunc='"+ key.toString() +"'");

        } catch (Exception e) {
            // Database error!
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
        return f;
    }

    @Override
    public void putAll(Map<? extends String, ? extends Funcionario> Funcionarios) {
        for(Funcionario c : Funcionarios.values()) {
            this.put(c.getIdFunc(), c);
        }
    }

    @Override
    public void clear() {
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             Statement stm = conn.createStatement()) {
            stm.executeUpdate("TRUNCATE Funcionario");
			stm.executeUpdate("TRUNCATE Mecanico");
			stm.executeUpdate("TRUNCATE Administrativo");
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
    public Collection<Funcionario> values() {
        Collection<Funcionario> res = new HashSet<>();
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             Statement stm = conn.createStatement();
             ResultSet rs = stm.executeQuery("SELECT IdFunc FROM Funcionario")) {
            while (rs.next()) {
                String idt = rs.getString("IdFunc");
                Funcionario t = this.get(idt);
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
    public Set<Entry<String, Funcionario>> entrySet() {
        throw new NullPointerException("public Set<Map.Entry<String,Funcionario>> entrySet() not implemented!");
    }
}
