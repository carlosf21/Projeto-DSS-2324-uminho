package business.SSClientes;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import business.SSPostos.ServicosNotFoundException;
import data.ClienteDAO;
import data.DAOconfig;
import data.VeiculoDAO;

public class SSClientesFacade implements ISubClientes {
    private Map<String, Cliente> clientes;
    private Map<String, Veiculo> todos_veiculos;
    private Map<String, TipoMotor> tipos_motor;

    // public SSClientesFacade(Map<String, Cliente> clientes, Map<String, Veiculo> todos_veiculos, Map<String, TipoVeiculo> tipos_motor) {
    //     // Initialize the map in the constructor
    //     this.clientes = clientes;
    //     this.todos_veiculos = todos_veiculos;
    //     this.tipos_motor = tipos_motor;
    // }

	public SSClientesFacade() {
		this.clientes = ClienteDAO.getInstance(null);
		this.todos_veiculos = VeiculoDAO.getInstance(null);
		this.tipos_motor = getTiposMotor();
	}

    private Map<String,TipoMotor> getTiposMotor() {

        Map<String,TipoMotor> tipos_motor = new HashMap<>();
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
            Statement stm = conn.createStatement();
            ResultSet rs = stm.executeQuery("SELECT * FROM TipoMotor")) {

            String id;
            String tipoMotor;
            TipoMotor tm = null;
            while (rs.next()) {
                id = String.valueOf(rs.getInt("IdTipo"));
                tipoMotor = rs.getString("Descricao"); // TODO switch????????
                if (tipoMotor.equals("Eletrico")) {
                    tm = new Eletrico(id);
                }
                else if (tipoMotor.equals("Gasolina")) {
                    tm = new Gasolina(id);
                }
                else if (tipoMotor.equals("Diesel")) {
                    tm = new Diesel(id);
                } else if (tipoMotor.equals("Combustao")) {
					tm = new Combustao(id);
				} else if (tipoMotor.equals("Universal")) {
					tm = new TipoMotor(id);
				} else {
                    throw new NullPointerException("Wrong motor type: " + tipoMotor);
                }
                tipos_motor.put(id, tm);
            }

        } catch (SQLException e) {
            // Database error!
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
        return tipos_motor;
    }

    public Cliente getCliente(String nif) throws ClienteNotFoundException {
        Cliente c = clientes.get(nif);
        if (c == null) {
            throw new ClienteNotFoundException(nif);
        }
        return c;
    }

    public List<TipoMotor> getTiposServico() {
        return this.tipos_motor.values().stream().collect(Collectors.toList());
    }
    
    public void addCliente(Cliente cliente) {
        clientes.put(cliente.getNif(), cliente);
    }

    public Veiculo getVeiculo(String matricula) {
        return todos_veiculos.get(matricula);
    }

    public void addVeiculo(Veiculo veiculo) {
        todos_veiculos.put(veiculo.getMatricula(), veiculo);
    }

    public TipoMotor getTipoMotor(String idTV) {
        return tipos_motor.get(idTV);
    }

    public void addTipoVeiculo(TipoMotor tipoVeiculo) {
        tipos_motor.put(tipoVeiculo.getidTM(), tipoVeiculo);
    }

    public boolean verificarCliente(String nif){
        return clientes.containsKey(nif);
    }

    public boolean verificarVeiculo(String matricula){
        return todos_veiculos.containsKey(matricula);
    }

    public boolean verificarTipoMotor(String tipoMotor) {
        return tipos_motor.containsKey(tipoMotor);
    }

    public boolean enviarSMSCliente(String numero, String msg) throws ClienteNotFoundException {
        // Obtém o cliente com o número fornecido
        Cliente cliente = getCliente(numero);
    
        // Se o cliente existir, envia o SMS
        if (cliente != null) {
            System.out.println("SMS enviado para " + numero + ": " + msg);
            return true;
        } else {
            // Cliente não encontrado, SMS não enviado
            return false;
        }
    }
    
    public void adicionaServicosVeiculo(String matricula, List<String> servicos) throws VeiculoNotFoundException {
        // Verifica se o veículo existe
        if (verificarVeiculo(matricula)) {
            // Obtém o veículo
            Veiculo veiculo = getVeiculo(matricula);

            // Verifica se a lista de serviços no veículo é nula
            if (veiculo.getServicosRecomendados() == null) {
                veiculo.setServicosRecomendados(new ArrayList<>());
            }

            // Adiciona os novos serviços à lista
            veiculo.getServicosRecomendados().addAll(servicos);
            
        } else {
            throw new VeiculoNotFoundException("Veículo não encontrado. Não foi possível adicionar os serviços.");
        }
    }

    public List<Veiculo> getVeiculosCliente(String nif) throws ClienteNotFoundException {
        Cliente cliente = getCliente(nif);

        List<Veiculo> veiculos = new ArrayList<>();
        // Verifica os veículos associados a ele
        List<String> veiculosCliente = cliente.getVeiculos();

        // Itera sobre os veículos do cliente
        for (String matriculaVeiculo : veiculosCliente) {
            // Se encontrar um veículo com a matrícula desejada, retorna o veículo
            veiculos.add(getVeiculo(matriculaVeiculo));
        }
        return veiculos;
    }
    

    public Veiculo getVeiculoCliente(String matricula, String nif) throws ClienteNotFoundException {
        // Obtém o cliente com o NIF fornecido
        Cliente cliente = getCliente(nif);
    
        // Verifica os veículos associados a ele
        List<String> veiculosCliente = cliente.getVeiculos();

        // Itera sobre os veículos do cliente
        for (String matriculaVeiculo : veiculosCliente) {
            // Se encontrar um veículo com a matrícula desejada, retorna o veículo
            if (matriculaVeiculo.equals(matricula)) {
                return getVeiculo(matricula);
            }
        }
        return null;
    }
    
    public void removeServicosVeiculo(String matricula, List<String> servicos) throws VeiculoNotFoundException, ServicosNotFoundException{
        // Verifica se o veículo existe
        if (verificarVeiculo(matricula)) {
            // Obtém o veículo
            Veiculo veiculo = getVeiculo(matricula);

            // Verifica se a lista de serviços no veículo não é nula
            if (veiculo.getServicosRecomendados() != null) {
                // Remove todos os serviços especificados da lista do veículo
                veiculo.getServicosRecomendados().removeAll(servicos);
            } else {
               throw new ServicosNotFoundException("A lista de serviços no veículo " + matricula + " está vazia. Não foi possível remover os serviços.");
            }
        } else {
            throw new VeiculoNotFoundException("Veículo não encontrado. Não foi possível remover os serviços.");
        }
    }

	public void registarCliente(String nome, String nif, String morada, String telefone, String email) {
		Cliente cliente = new Cliente(nif, nome, morada, telefone, email, new ArrayList<>());
		clientes.put(nif, cliente);
	}

	public void registarVeiculo(String nifCliente, String matricula, String modelo, String idTipo1, String idTipo2) throws TipoMotorNotFoundException {
		if (!verificarTipoMotor(idTipo1) || (idTipo2 != null && !verificarTipoMotor(idTipo2)))
            throw new TipoMotorNotFoundException("Couldn't find tipo motor on register veiculo");

        TipoMotor motor1 = tipos_motor.get(idTipo1);
        TipoMotor motor2 = null;
        if (idTipo2 != null) {
            motor2 = tipos_motor.get(idTipo2);
        }

		Veiculo veiculo = new Veiculo(matricula, modelo, motor1, motor2, nifCliente, new ArrayList<>());
		todos_veiculos.put(matricula, veiculo);
	}
}
