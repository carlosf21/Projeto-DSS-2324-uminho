import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import business.*;
import business.SSClientes.*;
import business.SSPostos.*;
import business.SSPostos.SSPostosFacade.TimeInterval;
import business.SSFuncionarios.*;

public class UI2 {

    private Scanner scanner;
    private OficinaLNFacade oficinaLNFacade;

	private LocalDateTime now; // para permitir manipular a data atual

    public UI2(OficinaLNFacade oficinaLNFacade) {
        this.scanner = new Scanner(System.in);
        this.oficinaLNFacade = oficinaLNFacade;
    }

    public void run() {
		System.out.println("Insira a data atual (ou deixe vazio) yyyy-MM-dd HH:mm:ss");
		String input = scanner.nextLine();
		if (input.isEmpty()) {
			this.now = LocalDateTime.now();
		} else {
			this.now = LocalDateTime.parse(input, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
		}

        NewMenu mainMenu = new NewMenu(new String[] {
            "Administrador",
            "Login"
        }, "Menu Principal");

        mainMenu.setHandler(1, () -> menuAdministrador());
        mainMenu.setHandler(2, () -> subMenuFuncionarios());

        mainMenu.run();
    }

    public void subMenuFuncionarios() {
		System.out.print("Insira o nome de funcionario: "); // Na vida real seria algum sistema de login com password, etc...
        String nome = scanner.nextLine();
		Funcionario func = oficinaLNFacade.getFuncionarioPorNome(nome);

        if (func != null) {
            if (func instanceof Administrativo) {
                menuAdministrativo(func);
            }
            else {
                menuMecanico(func);
            }
        } else {
            System.out.println("Funcionario nao existe");
        }
    }

    //menu administrativo
    public void menuAdministrativo(Funcionario func) { // nao usa o func por agora, mas podera ser necessario
        NewMenu rececMenu = new NewMenu(new String[] {
            "Registar cliente",
            "Registar veiculo",
            "Registar serviço do posto",
            "Agendar serviço"
        }, "Menu Administrativo");

        rececMenu.setHandler(1, () -> realizarRegistoCliente());
        rececMenu.setHandler(2, () -> realizarRegistoVeiculo());
        rececMenu.setHandler(3, () -> realizarRegistoServico());
        rececMenu.setHandler(4, () -> marcarAgendamento());

        rececMenu.run();
    }

    public void menuMecanico(Funcionario func) {
        NewMenu mechanicMenu = new NewMenu(new String[] {
			"Entrar no posto",
            }, "Menu Mecânico");

		mechanicMenu.setHandler(1, () -> menuPosto((Mecanico) func)); // TODO cuidado com este cast manhoso
        // mechanicMenu.setHandler(3, () -> atualizarServicosRecomendados(oficinaLNFacade));

        mechanicMenu.run();
    }

	public void menuPosto(Mecanico mec) {
		// escolher o posto onde entrar
		System.out.print("Insira o ID do posto: ");
        String idPosto = scanner.nextLine();

		System.out.println(mec.getIdFunc());

		if (oficinaLNFacade.postoExiste(idPosto)) {
			if (oficinaLNFacade.verificarCompMecanico(mec.getIdFunc(), idPosto)) {
                try {
                    oficinaLNFacade.registarEntradaTurno(mec.getIdFunc(), now);
        
                    NewMenu menuPosto = new NewMenu(new String[] {
                        "Ver agendamentos de hoje",
                        "Registar serviço realizado",
                        "Registar falha de serviço"
                    }, "Menu Posto");

                    // por simplicidade, nao iniciamos um servico mas apenas podemos marcar como acabado

                    menuPosto.setHandler(1, () -> showAgendamentosDeHoje(idPosto));
                    menuPosto.setHandler(2, () -> realizarRegistoServicoRealizado());
                    menuPosto.setHandler(3, () -> realizarRegistoFalhaServico());

                    menuPosto.run();

                    
                    System.out.print("Fast forward quanto tempo para simular? HH:mm:ss: ");
                    String hora = this.scanner.nextLine();
					if (! hora.isEmpty()) {
						LocalTime timeToAdd = LocalTime.parse(hora, DateTimeFormatter.ofPattern("HH:mm:ss"));
						this.now = this.now.with(timeToAdd); // adicionar ao tempo atual
					}
					System.out.println("Nova data atual: " + now.toString());

                    oficinaLNFacade.registarSaidaTurno(mec.getIdFunc(), now);
                } catch (FuncionarioNotFoundException e) {
                    System.out.println("Funcionario nao encontrado");
                } catch (RegistoNotFoundException e) {
                    System.out.println("Registo nao encontrado");
                }
 			} else {
				System.out.println("O mecanico nao tem competencias para este posto, que estudasse mais");
			}
		} else {
			System.out.println("O posto nao existe");
		}
	}

	public void showAgendamentosDeHoje(String idPosto) { // assumimos que posto ja existe
		// podia ser entre as horas de fecho e abertura tbm mas deve ser igual
		LocalDateTime inicio = now.with(LocalTime.MIN);
		LocalDateTime fim = now.with(LocalTime.MAX);

		for (Agendamento ag : oficinaLNFacade.getPostoAgendamentosEntre(idPosto, inicio, fim)) {
			System.out.println(ag.toStringParaUI());
		}
	}

    public void menuAdministrador() {
        NewMenu administrador = new NewMenu(new String[] {
            "Registar horário de funcionamento da estação",
            "Registar posto",
            "Registar servico",
            "Registar funcionário"
        }, "Menu Administrador");

        administrador.setHandler(1, () -> realizarRegistoHorarioEstacao());
        administrador.setHandler(2, () -> realizarRegistarPosto());
        administrador.setHandler(3, () -> realizarRegistarServico());
        administrador.setHandler(4, () -> realizarRegistoFuncionario());

        administrador.run();
    }

    // Métodos auxiliares
    private void realizarRegistoCliente() {
        System.out.println("### Registar Cliente ###");
    
        System.out.print("Insira o nome do cliente: ");
        String nome = scanner.nextLine();
    
        System.out.print("Insira o NIF do cliente: ");
        String nif = scanner.nextLine();
    
        System.out.print("Insira a morada do cliente: ");
        String morada = scanner.nextLine();
    
        System.out.print("Insira o telefone do cliente: ");
        String telefone = scanner.nextLine();
    
        System.out.print("Insira o email do cliente: ");
        String email = scanner.nextLine();
    
        // Verificar se o cliente já existe pelo NIF
        if (oficinaLNFacade.verificarCliente(nif)) {
            System.out.println("Cliente já registado com o NIF fornecido.");
        } else {
            // Caso não exista, regista-o
            oficinaLNFacade.registarCliente(nome, nif, morada, telefone, email);
            System.out.println("Cliente registado com sucesso.");
        }
    }
    

    private void realizarRegistoVeiculo() {
        System.out.println("### Registar Veículo ###");

        System.out.println("Insira o Nif do Cliente");
        String nif= scanner.nextLine();

        if (!oficinaLNFacade.verificarCliente(nif)) {
            System.out.println("Cliente não existe no sistema");
        }
        else {
            System.out.println("Insira a matricula");
            String matricula = scanner.nextLine();

            System.out.println("Insira o modelo");
            String modelo = scanner.nextLine();

            System.out.println("Insira o tipo do Motor (1-Gasolina, 2-Diesel, 3-Elétrico)");
            String idTipo= scanner.nextLine();

            System.out.println("Se for híbrido, insira o tipo do 2ºMotor (0-Nenhum, 1-Gasolina, 2-Diesel, 3-Elétrico)");
            String idTipo2 = scanner.nextLine();
            if (idTipo2.equals("0")) {
                idTipo2 = null;
            } 
            // Verificar se o cliente já existe pelo NIF
            if (oficinaLNFacade.verificarVeiculo(matricula)) {
                System.out.println("Veiculo já registado com a matricula fornecida.");
            } else {
                // Caso não exista, regista-o
                try {
                    oficinaLNFacade.registarVeiculo(nif, matricula, modelo, idTipo, idTipo2);
                    System.out.println("Veiculo registado com sucesso.");
                } catch (TipoMotorNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void realizarRegistoFuncionario() {
        System.out.println("### Registar Funcionário ###");

        System.out.print("Insira o nome do funcionário: ");
        String nome = this.scanner.nextLine();
        System.out.print("Escolha o tipo de funcionário (Administrativo/Mecanico): ");
        String tipoFunc = this.scanner.nextLine();
        String idFinal = null;
        // Chamar o método registarFuncionario da SSFuncionariosFacade
        if (tipoFunc.equals("Administrativo")) {
            idFinal = oficinaLNFacade.registarAdministrativo(nome);
        }
        else if (tipoFunc.equals("Mecanico")) {
            System.out.println("Tipos de Especialide disponiveis:");
            List<TipoMotor> tipos = oficinaLNFacade.getTiposServico();
            for (TipoMotor tm: tipos) {
                System.out.println(tm.toStringParaUI());
            }
            System.out.println("\n");

            System.out.print("Insira especialidade do Mecanico: ");
            String idTipo = this.scanner.nextLine();
            idFinal = oficinaLNFacade.registarMecanico(nome, idTipo);
        }
        System.out.println("Funcionario id: "+ idFinal + " registado com sucesso");
    }

    private void realizarRegistarPosto() {
        System.out.println("### Registar Posto ###");
        String postoId = oficinaLNFacade.registarPosto();
        System.out.println("Posto registado com sucesso. ID do Posto: " + postoId);
    }

    private void realizarRegistarServico() {
        System.out.println("### Registar Servico ###");
        System.out.println("Tipos de Servico disponiveis:");

        List<TipoMotor> tipos = oficinaLNFacade.getTiposServico();
        for (TipoMotor tm: tipos) {
            System.out.println(tm.toStringParaUI());
        }
        System.out.println("\n");

        System.out.println("Insira tipo do Servico:");
        String tipo = this.scanner.nextLine();
        System.out.println("Insira duraçao do Servico:");
        int duracao = Integer.valueOf(this.scanner.nextLine());
        System.out.println("Insira descricao do Servico:");
        String descricao = this.scanner.nextLine();
        oficinaLNFacade.registarServico(duracao,descricao,tipo);
        System.out.println("\nServiço registado com sucesso.");
    }

    private void realizarRegistoServico() {
        System.out.println("### Registar Serviço do Posto ###");
        System.out.println("Servicos disponiveis:\n");
        List<Servico> servicos = oficinaLNFacade.getTodosServicos();
        for (Servico s: servicos) {
              System.out.println(s.toStringParaUI() + "\n");
        }
        System.out.println("Postos disponiveis:");
        List<Posto> postos = oficinaLNFacade.getTodosPostos();
        for (Posto p: postos) {
             System.out.println(p.toString());
        }
        System.out.print("Insira o ID do Posto: ");
        String idPosto = this.scanner.nextLine();
        System.out.print("Insira o ID do Serviço: ");
        String idServico = this.scanner.nextLine();
        try {
        oficinaLNFacade.registarServicoDePosto(idPosto, idServico);

        System.out.println("Serviço do Posto registado com sucesso.");
        } catch (PostoNotFoundException e){
            System.out.println("Posto nao existe");
        } catch(ServicosNotFoundException e) {
            System.out.println("Servico nao existe");
        }
    }


    private void marcarAgendamento() {
        System.out.println("### Marcar Agendamento ###");
        try {
            System.out.print("Insira o NIF do Cliente: ");
            String idCliente = this.scanner.nextLine();

            if (oficinaLNFacade.verificarCliente(idCliente)) {
                //menu prints dos veiculos
                List<Veiculo> veiculos = oficinaLNFacade.getVeiculosCliente(idCliente);
                System.out.println("Veiculos do cliente");
                for (Veiculo v: veiculos) {
                    System.out.println(v.toStringParaUICurto());
                }

                System.out.print("Insira a matricula do Veículo: ");
                String matrVeiculo = this.scanner.nextLine();
                
                Veiculo veiculo = oficinaLNFacade.getVeiculoCliente(matrVeiculo, idCliente);
                List<Servico> servicosDisp = oficinaLNFacade.getServicosDisponiveis(veiculo);
                System.out.println("Servicos possiveis para veículo: ");
                System.out.println("Got N servicos:" + servicosDisp.size());
                for (Servico s: servicosDisp) {
                    System.out.println(s.toStringParaUI());
                }

                System.out.print("Insira o ID do Serviço: ");
                String idServico = this.scanner.nextLine();

                Map<String, List<TimeInterval>> livres = oficinaLNFacade.getPostosLivres(now, idServico);
				if (livres.size() == 0) {
					System.out.println("Nao existe disponibilidade para hoje");
				} else {
					for (Map.Entry<String,List<TimeInterval>> posto: livres.entrySet()) {
						System.out.println("Posto " + posto.getKey() + " disponibilidade:");
						for (TimeInterval t : posto.getValue()) {
	
							DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
							String dataInicio = t.getStart().format(formatter);
							String dataFim = t.getEnd().format(formatter);
	
							System.out.println("De: " + dataInicio + " Até: " + dataFim);
						}
						System.out.println("\n");
					}
                    // se n der, cancelar agendamento
                    // se der:
                    System.out.print("Insira o ID do Posto: ");
                    String idPosto = this.scanner.nextLine();
                    
                    System.out.print("Insira a data e hora de início (formato: yyyy-MM-dd HH:mm): ");
                    String inputHoraInicio = this.scanner.nextLine();
                    LocalDateTime horaInicio = LocalDateTime.parse(inputHoraInicio, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
                    
                    // boolean possivel = oficinaLNFacade.verificarPossivelAgendamento(horaInicio,horaFim, idServico, idPosto);
                    System.out.print("Deseja receber notificação? (true/false): ");
                    boolean notificacao = this.scanner.nextBoolean();
                    
                    this.scanner.nextLine(); 
                    
                    String agendamentoId = oficinaLNFacade.criarAgendamento(horaInicio, notificacao, matrVeiculo, idCliente, idServico, idPosto);
                    
                    System.out.println("Agendamento criado com sucesso. ID do Agendamento: " + agendamentoId);
                }
            } else {
                System.out.println("Cliente não existe");
            }
        } catch (ClienteNotFoundException e) {
            System.out.println("Cliente nao encontrado");
        }
    }


    private void realizarRegistoServicoRealizado() {
        System.out.println("### Registar Serviço Realizado ###");
    
        System.out.print("Insira o ID do Agendamento: ");
        String idAgendamento = this.scanner.nextLine();
    
        oficinaLNFacade.registarFimServico(idAgendamento); // TODO verificar que agendamento existe. a hora de fim passa a ser a hora atual, ou deixamos como esta (a hora em que ficou planeado terminar)??????????
        System.out.println("Fim de serviço registado");

		System.out.println("Insira os IDs dos servicos a recomendar (ou linha vazia para acabar):");

		ArrayList<String> servicosRecomendados = new ArrayList<>();
		String idServico = null;
		while (true) {
            idServico = scanner.nextLine();
			if (idServico.isEmpty()) break;
            if (oficinaLNFacade.servicoExiste(idServico)) {
			} else {
				System.out.println("ID servico invalido");
			}
		}
		if (servicosRecomendados.size() > 0) {
            try {
			String idVeiculo = oficinaLNFacade.getMatriculaVeiculoAgendamento(idAgendamento);
			oficinaLNFacade.adicionaServicosVeiculo(idVeiculo, servicosRecomendados);
            } catch (VeiculoNotFoundException e) {
                System.out.println("Veiculo não foi encontrado");
            }
		}
	}
    
    private void realizarRegistoFalhaServico() {
        System.out.println("### Registar Falha de Serviço ###");
    
        
        System.out.print("Insira o ID do Agendamento: ");
        String idAgendamento = this.scanner.nextLine();
        System.out.print("Descreva a falha no serviço: ");
        String descricaoFalha = this.scanner.nextLine();
        oficinaLNFacade.registarFalharServico(idAgendamento, descricaoFalha);
    
        System.out.println("Falha de serviço registada com sucesso.");
    }
    
    private void realizarRegistoHorarioEstacao() {
        System.out.println("Registar Horário de Funcionamento da Estação");
        System.out.print("Insira a hora de abertura (Formato: HH:mm::ss): ");
        String horaAbertura = this.scanner.nextLine();
        System.out.print("Insira a hora de encerramento (Formato: HH:mm::ss): ");
        String horaEncerramento = this.scanner.nextLine();

        try {
            LocalTime horarioInic = LocalTime.parse(horaAbertura, DateTimeFormatter.ofPattern("HH:mm:ss"));
            LocalTime horarioFim = LocalTime.parse(horaEncerramento, DateTimeFormatter.ofPattern("HH:mm:ss"));

            // Chamar o método registarHorarioEstacao da SSFuncionariosFacade
            oficinaLNFacade.registarHorarioEstacao(horarioInic, horarioFim);
        } catch (DateTimeParseException e) {
            System.out.println("Formato de data e hora inválido. Certifique-se de seguir o formato correto.");
        }
    }
}

/* 
    private void atualizarServicosRecomendados( OficinaLNFacade oficinaLNFacade) {
        System.out.println("### Atualizar Serviços Recomendados ###");
    
        System.out.println("Insira a matricula do Veículo: ");
        String matricula=this.scanner.nextLine();
        
        List<String> servicosRecomendados = oficinaLNFacade.getServiçosRecomendados(matricula);
    
        System.out.println("Serviços Recomendados Atuais: " + servicosRecomendados);
    
        // Solicita ao usuário que insira os novos serviços recomendados
        System.out.print("Insira os novos serviços recomendados (separados por vírgula): ");
        String novosServicosInput = this.scanner.nextLine();
        List<String> novosServicos = Arrays.asList(novosServicosInput.split(","));
    
        // Chama o método da OficinaLNFacade para atualizar os serviços recomendados
        oficinaLNFacade.adicionaServicosVeiculo(idVeiculo, novosServicos);
    
        System.out.println("Serviços recomendados atualizados com sucesso.");
    }
    

}
*/   
