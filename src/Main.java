import javax.swing.*;
import java.sql.SQLException;
import java.sql.SQLOutput;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Scanner;


public class Main {
    public static ArrayList<Todo> todoList = new ArrayList<>();
    public static Todo test = new Todo("Coursework 3",
            LocalDateTime.parse("2021-04-26T09:00:00"),
            Category.BLUE,
            Importance.HIGH,
            Status.PENDING);
    public static Todo test2 = new Todo("Coursework 3",
            LocalDateTime.parse("2021-04-26T09:00:00"),
            Category.RED,
            Importance.HIGH,
            Status.PENDING);


    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        //todoList.add(test);
        Scanner open = new Scanner(System.in);
        String input;
        boolean menu = false;
        do {
            System.out.println("Press 1 for CLI Menu ");
            System.out.println("Press 2 for GUI Menu");
            input = open.next();

            if(input.equals("1") || input.equals("2")){
                menu = true;
            }
        }while(!menu);


        if(input.equals("1")) {
            CLIMenu program = new CLIMenu(todoList);
            program.menu();
        } else {
            SwingUtilities.invokeLater(() -> {
                try {
                    new GUI(todoList);
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            });
        }
    }


}
