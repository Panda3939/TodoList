import java.sql.ResultSet;
import java.sql.SQLException;

public class SQLliteTestApp {
    public static void main(String[] args) {
        SQLite test = new SQLite();
        ResultSet rs;

        try {
            rs = test.displayUsers();
            System.out.println("hello");
            while(rs.next()){
                System.out.println(rs.getString("id") + " " + rs.getString("fname") + " " + rs.getString("lname"));
            }


        } catch(ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e){
            e.printStackTrace();
        }

    }
}
