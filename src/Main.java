import business.OficinaLNFacade;

public class Main {

    public static void main(String[] args) {
        OficinaLNFacade facade = new OficinaLNFacade();
        UI2 ui = new UI2(facade);
        ui.run();
    }
}