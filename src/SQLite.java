import java.sql.*;

public class SQLite {
    private static Connection con;
    private static boolean hasData = false;

    public ResultSet displayUsers() throws SQLException, ClassNotFoundException {
        if(con == null) {
            getConnection();
        }

        Statement state = con.createStatement();
        ResultSet res = state.executeQuery("SELECT * FROM user");
        return res;
    }

    private void getConnection() throws ClassNotFoundException, SQLException {
        Class.forName("org.sqlite.JDBC");
        con = DriverManager.getConnection("jdbc:sqlite:TestDatabase.db");
        initialise();
    }

    private void initialise() throws SQLException {
        if(!hasData) {
            hasData = true;
            Statement state = con.createStatement();
            ResultSet res = state.executeQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='user'");
            if(!res.next()){
                System.out.println("Building user table with pre-populated values");
                Statement state2 = con.createStatement();
                state2.execute("CREATE TABLE user(id integer," +
                        "fname varchar(60)," + "lname varchar(60)," +
                        "primary key(id));");

                PreparedStatement prep = con.prepareStatement("INSERT INTO user values(?,?,?);");
                prep.setString(2, "John");
                prep.setString(3, "McNeil");
                prep.execute();

                PreparedStatement prep2 = con.prepareStatement("INSERT INTO user values(?,?,?);");
                prep2.setString(2, "Hayami");
                prep2.setString(3, "Kanade");
                prep2.execute();
            }
        }
    }

    public void addUser(String first, String last) throws SQLException, ClassNotFoundException {
        if (con == null){
            getConnection();
        }

        PreparedStatement prep = con.prepareStatement("INSERT INTO user values(?,?,?);");
        prep.setString(2, first);
        prep.setString(3, last);
        prep.execute();
    }
}
