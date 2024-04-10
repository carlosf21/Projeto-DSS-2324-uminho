package business.SSClientes;

import java.util.List;

import business.SSPostos.ServicosNotFoundException;

public interface ISubClientes {
    
    boolean verificarCliente(String nif);

    boolean verificarVeiculo(String matricula);
	
    boolean enviarSMSCliente(String numero, String msg) throws ClienteNotFoundException;
	
    void adicionaServicosVeiculo(String matricula, List<String> servicos) throws VeiculoNotFoundException;
	
    public Veiculo getVeiculoCliente(String matricula, String nif) throws ClienteNotFoundException;
	
    void removeServicosVeiculo(String matricula, List<String> servicos) throws VeiculoNotFoundException, ServicosNotFoundException;


/////////////////////////////////////// alteradas / novas
    // String registarCliente(String nome, String nif, String morada, String telefone, String email, String matricula, String modelo, String tipoCarro); // naof az sentido nenhum
	void registarCliente(String nome, String nif, String morada, String telefone, String email);
	void registarVeiculo(String nifCliente, String matricula, String modelo, String idTipo, String idTipo2) throws TipoMotorNotFoundException;
}