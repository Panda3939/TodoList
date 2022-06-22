import java.sql.*;
import java.time.LocalDateTime;

/*
Database fields:
title
datetime
category
importance
status
 */


public class TodoListSQLite {
    //Variables to check if the program is connected to a database and if the database has any data
    private static Connection con;
    private static boolean hasData = false;

    //Returns all data store in the database
    public ResultSet getTasks() throws SQLException, ClassNotFoundException {
        if(con == null){
            getConnection();
        }

        Statement state = con.createStatement();
        ResultSet res = state.executeQuery("SELECT * FROM todolist");
        return res;
    }

    public void insertTask(String title, LocalDateTime due, Category cat, Importance imp, Status stat) throws SQLException, ClassNotFoundException {
        if(con == null){
            getConnection();
        }

        String name, datetime, category, importance, status;
        name = title;
        datetime = due.toString();
        category = cat.toString();
        importance = imp.toString();
        status = stat.toString();

        PreparedStatement prep = con.prepareStatement("INSERT INTO todolist VALUES(?,?,?,?,?,?)");
        prep.setString(2, name);
        prep.setString(3, datetime);
        prep.setString(4, category);
        prep.setString(5, importance);
        prep.setString(6, status);
        prep.execute();

    }

    public void updateTask(String taskName, String field, Object obj) throws SQLException, ClassNotFoundException {
        if(con == null){
            getConnection();
        }

        String update;
        Object change = obj;
        if(change.getClass().isEnum()) {
            update = change.toString().toUpperCase();
        } else {
            update = change.toString();
        }

        PreparedStatement prep = con.prepareStatement("UPDATE todolist SET " + field + " = '" + update + "' WHERE title = '" + taskName + "'");
        prep.execute();
    }

    public void updateGUI(String taskname, LocalDateTime due, Category cat, Importance imp, Status stat) throws SQLException, ClassNotFoundException{
        if(con == null){
            getConnection();
        }

        String title = taskname;
        String datetime = due.toString();
        String category = cat.toString();
        String importance = imp.toString();
        String status = stat.toString();

        PreparedStatement prep = con.prepareStatement("UPDATE todolist SET title = '" + title + "', " +
                "datetime = '" + datetime + "', " +
                "category = '" + category + "', " +
                "importance = '" + importance + "', " +
                "status = '" + status + "' " +
                "WHERE title = '" + taskname + "'");
        prep.execute();
    }

    public void deleteTask(String taskName) throws SQLException, ClassNotFoundException {
        if(con == null){
            getConnection();
        }

        PreparedStatement prep = con.prepareStatement("DELETE FROM todolist WHERE title = '" + taskName + "'");
        prep.execute();
    }

    private void getConnection() throws ClassNotFoundException, SQLException {
        Class.forName("org.sqlite.JDBC");
        con = DriverManager.getConnection("jdbc:sqlite:TodoList.db");
        initialise();
    }

    private void initialise() throws SQLException{
        if(!hasData){
            hasData = true;

            Statement state = con.createStatement();
            ResultSet res = state.executeQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='todolist'");

            if(!res.next()){
                Statement createDB = con.createStatement();
                createDB.execute("CREATE TABLE todolist(id integer," +
                        "title varchar(100)," +
                        "datetime varchar(20)," +
                        "category varchar(10)," +
                        "importance varchar(10)," +
                        "status varchar(10)," +
                        "primary key(id));");
            }

        }
    }

}
