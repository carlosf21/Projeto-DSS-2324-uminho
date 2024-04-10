import java.util.Scanner;

import java.util.List;
import java.util.ArrayList;

public class Menu {

    public interface Operation {
        void execute();
    }

    // Variáveis de instância

    private List<String> options;   // Lista de opções
    private List<Operation> operations;     // Lista de operações
    private String menuName;
    private Scanner scanner;


    // Construtor

    /**
     * Constructor for objects of class NewMenu
     */
    public Menu(String[] options, String menuName, Scanner scanner) {
        this.options = new ArrayList<>(List.of(options));
        this.menuName = menuName;
        this.scanner = scanner;
        this.operations = new ArrayList<>();
    }

    public void addOperation(String option, Operation operation) {
        int index = options.indexOf(option);
        if (index != -1) {
            operations.add(index, operation);
        }
    }

    public void setOperation(int i, Operation h) {
        this.operations.set(i-1, h);
    }


    public void run() {
        int choice;
        do {
            displayMenu();
            choice = readChoice();
            if (choice > 0 && choice <= operations.size()) {
                operations.get(choice - 1).execute();
            }
        } while (choice != 0);
    }


    private void displayMenu() {
        System.out.println("\n * " + menuName + " * ");
        for (int i = 0; i < options.size(); i++) {
            System.out.println((i + 1) + " - " + options.get(i));
        }
        System.out.println("0 - Sair");
    }

    private int readChoice() {
        System.out.print("Escolha uma opção: ");
        try {
            return scanner.nextInt();
        } catch (Exception e) {
            scanner.nextLine(); // Consumir a quebra de linha
            System.out.println("Opção inválida. Tente novamente.");
            return -1;
        }
    }
}