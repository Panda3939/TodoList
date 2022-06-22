import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Scanner;

public class CLIMenu {
    private static ArrayList<Todo> list;
    private static TodoListSQLite db = new TodoListSQLite();
    private static ResultSet rs;


    CLIMenu(ArrayList<Todo> todolist){
        list = todolist;
    }



    public static void menu() throws SQLException, ClassNotFoundException {
        String key;
        String edit;
        String sqltitle;
        Scanner input = new Scanner(System.in);
        Scanner editor = new Scanner(System.in);

        //Initialises the database and rebuilds the todolist
        rs = db.getTasks();
        while(rs.next()){
            String title = rs.getString("title");
            LocalDateTime due = LocalDateTime.parse(rs.getString("datetime"));
            Category cat = Category.valueOf(rs.getString("category"));
            Importance imp = Importance.valueOf(rs.getString("importance"));
            Status stat = Status.valueOf(rs.getString("status"));

            list.add(new Todo(title, due, cat, imp, stat));
        }



        do{
            //Main menu option list
            System.out.println("Options");
            System.out.println("1. List todos");
            System.out.println("2. Add todo");
            System.out.println("3. Update todo");
            System.out.println("4. Delete todo");
            System.out.println("0. Exit Program");

            key = input.next();
            switch(key){
                //Case 1: Print all tasks in the list
                case "1":
                    if(list.size() == 0){
                        System.out.println("Your todo list is currently empty");
                    } else {
                        int i = 1;
                        for(Todo t: list){
                            System.out.println(t.getCat().getColour()
                                    + i
                                    + ". "
                                    + t.toString());
                            i++;
                        }
                    }
                    break;
                //Case 2: Add a new tasks to the list
                case "2":
                    //Declaring values to create task
                    String newTitle;
                    LocalDateTime newDueDate;
                    Category newColour;
                    Importance newImp;

                    //Setting Title
                    System.out.println("What is the title of the task?");
                    edit = editor.nextLine();
                    newTitle = edit;

                    //Setting Due Date
                    System.out.println("When is the task due? ");
                    System.out.println("(Format: YYYY-MM-DDTHH:MM)");
                    edit = editor.nextLine();
                    newDueDate = LocalDateTime.parse(edit.toUpperCase());

                    //Select colour category
                    System.out.println("What category does the task belong to?");
                    System.out.println("1. Red, 2. Green, 3. Blue, 4. Purple, 5. Yellow, 6. White");
                    newColour = selectCategory();

                    //State task importance
                    System.out.println("What is the priority of the task?");
                    System.out.println("1. Low, 2. Normal, 3. High");
                    newImp = selectImportance();


                    //Add the task to the todolist
                    list.add(new Todo(newTitle,
                            newDueDate,
                            newColour,
                            newImp,
                            Status.PENDING));
                    db.insertTask(newTitle, newDueDate, newColour, newImp, Status.PENDING);

                    break;
                //Case 3: Update tasks
                case "3":
                    if(list.size() == 0){
                        System.out.println("Your list is empty");
                        break;
                    }
                    //Display titles of saved tasks
                    int index;
                    int j = 1;
                    for (Todo t : list) {
                        System.out.println(j + ". " + t.getText());
                        j++;
                    }

                    System.out.println("Which task do you want to update?");
                    edit = editor.nextLine();
                    index = Integer.parseInt(edit);
                    if (Integer.parseInt(edit) > list.size()) {
                        System.out.println("Task doesn't exist");
                    } else {

                        System.out.println("1. Title, 2. Date, 3. Category, 4. Importance, 5. Status");
                        edit = editor.nextLine();

                        if (edit.equals("1")) {
                            //Renaming the title
                            System.out.println("Rename your title");
                            sqltitle = list.get(index - 1).getText();
                            edit = editor.nextLine();
                            list.get(index - 1).setText(edit);
                            db.updateTask(sqltitle, "title", edit);

                        } else if (edit.equals("2")) {
                            //Sets a new date
                            System.out.println("Set the date (Format: YYYY-MM-DDTHH:MM)");
                            sqltitle = list.get(index - 1).getText();
                            edit = editor.nextLine();
                            list.get(index - 1).setDue(LocalDateTime.parse(edit));
                            db.updateTask(sqltitle, "datetime", edit);

                        } else if (edit.equals("3")) {
                            //Changes the category of the task
                            System.out.println("1. Red, 2. Green, 3. Blue, 4. Purple, 5. Yellow, 6. White");
                            Category temp = selectCategory();
                            sqltitle = list.get(index - 1).getText();
                            list.get(index - 1).setCat(temp);
                            db.updateTask(sqltitle, "category", temp);

                        } else if (edit.equals("4")) {
                            //Changes the importance of the task
                            System.out.println("1. Low, 2. Normal, 3. High");
                            Importance temp = selectImportance();
                            sqltitle = list.get(index - 1).getText();
                            list.get(index - 1).setImportance(temp);
                            db.updateTask(sqltitle, "importance", temp);

                        } else if (edit.equals("5")) {
                            //Changes the Status of the task
                            System.out.println("1. Pending, 2. Started, 3. Partial, 4. Completed");
                            Status temp = selectStatus();
                            sqltitle = list.get(index - 1).getText();
                            list.get(index - 1).setCompletion(temp);
                            db.updateTask(sqltitle, "status", temp);
                        } else {
                            System.out.println("That key is not valid");
                        }
                    }
                    break;
                //Case 4: Delete tasks
                case "4":
                    if(list.size() == 0){
                        System.out.println("Your list is empty");
                    } else {
                        int i = 1;
                        int task;
                        for (Todo t : list) {
                            System.out.println(i + ". " + t.getText());
                            i++;
                        }
                        System.out.println("Which task would you like to delete");
                        edit = editor.nextLine();
                        if(Integer.parseInt(edit) > list.size()){
                            System.out.println("Task doesn't exist");
                        } else {
                            task = Integer.parseInt(edit) - 1;
                            sqltitle = list.get(task).getText();
                            list.remove(task);
                            db.deleteTask(sqltitle);
                        }

                    }
                    break;
                case "0":
                    System.out.println("Exiting program");
                    break;
                default:
                    System.out.println("Invalid Key");
                    break;
            }

        } while(!key.equals("0"));
    }

    //Returns a Category enum based on entered integer
    public static Category selectCategory(){
        int num;
        Scanner cat = new Scanner(System.in);
        do {

            num = cat.nextInt();
            switch (num) {
                case 1:
                    return Category.RED;
                case 2:
                    return Category.GREEN;
                case 3:
                    return Category.BLUE;
                case 4:
                    return Category.PURPLE;
                case 5:
                    return Category.YELLOW;
                case 6:
                    return Category.WHITE;
                default:
                    System.out.println("Invalid Category, try again");
                    break;
            }
        }while(true);
    }

    //Returns an Importance enum based on entered integer
    public static Importance selectImportance(){
        int num;
        Scanner cat = new Scanner(System.in);
        do {
            num = cat.nextInt();
            switch (num) {
                case 1:
                    return Importance.LOW;
                case 2:
                    return Importance.NORMAL;
                case 3:
                    return Importance.HIGH;
                default:
                    System.out.println("Invalid Importance, try again");
                    break;
            }
        }while(true);
    }

    //Returns an Status enum based on entered integer
    public static Status selectStatus(){
        int num;
        Scanner cat = new Scanner(System.in);
        do {
            num = cat.nextInt();
            switch (num) {
                case 1:
                    return Status.PENDING;
                case 2:
                    return Status.STARTED;
                case 3:
                    return Status.PARTIAL;
                case 4:
                    return Status.COMPLETED;
                default:
                    System.out.println("Invalid Status, try again");
                    break;
            }
        }while(true);
    }
}
