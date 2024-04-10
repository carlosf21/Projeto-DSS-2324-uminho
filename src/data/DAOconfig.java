package data;

public class DAOconfig {
    public static final String USERNAME = "dss";
    public static final String PASSWORD = "dss";
    private static final String DATABASE = "Oficina";
    private static final String DRIVER = "jdbc:mariadb";
    // private static final String DRIVER = "jdbc:mysql";
    public static final String URL = DRIVER+"://localhost:3306/"+DATABASE;
}
