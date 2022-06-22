import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;



public class GUI extends JFrame implements ListSelectionListener, ActionListener {
    ArrayList<Todo> todoList;
    ArrayList<String> todoNameIndex = new ArrayList();
    private static TodoListSQLite db = new TodoListSQLite();
    private static ResultSet rs;
    JList name;
    JScrollPane scrollPane;
    JLabel displayTitle = new JLabel();
    JLabel displayDueDate = new JLabel();
    JLabel displayCategory = new JLabel();
    JLabel displayImportance = new JLabel();
    JLabel displayStatus = new JLabel();
    int index;

    public GUI(ArrayList<Todo> list) throws SQLException, ClassNotFoundException {
        super("Todo List Program");
        todoList = list;

        //Rebuilds the database for the program
        rs = db.getTasks();
        while(rs.next()){
            String title = rs.getString("title");
            LocalDateTime due = LocalDateTime.parse(rs.getString("datetime"));
            Category cat = Category.valueOf(rs.getString("category"));
            Importance imp = Importance.valueOf(rs.getString("importance"));
            Status stat = Status.valueOf(rs.getString("status"));

            list.add(new Todo(title, due, cat, imp, stat));
        }

        //A second arraylist storing the names of the todos for the JList
        for (Todo t: list) {
            todoNameIndex.add(t.getText());
        }

        //Setting a default size for the program when opened
        setSize(640, 300);
        setLocation(600, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


        //Setting up the main layout of the gui
        JPanel mainPanel = new JPanel();
        this.add(mainPanel);
        mainPanel.setLayout(new GridLayout(0,3, 10, 10));

        //A new panel to hold the buttons of the gui
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(4, 0, 10, 10));
        buttonPanel.setSize(new Dimension(200, 300));
        mainPanel.add(buttonPanel);



        // Buttons for each action with action listeners
        JButton addB = new JButton("Add Todo");
        JButton updateB = new JButton("Update Todo");
        JButton deleteB = new JButton("Delete Todo");
        JButton exitB = new JButton("Exit Program");

        addB.addActionListener(this);
        updateB.addActionListener(this);
        deleteB.addActionListener(this);
        exitB.addActionListener(this);

        //Adding buttons to the button sub panel
        buttonPanel.add(addB);
        buttonPanel.add(updateB);
        buttonPanel.add(deleteB);
        buttonPanel.add(exitB);

        //Scroll panel for contains the list of todos
        scrollPane = new JScrollPane();
        name = new JList(todoNameIndex.toArray());
        name.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        name.addListSelectionListener(this);
        scrollPane.setViewportView(name);
        mainPanel.add(scrollPane);

        //Display panel for the details of the tasks
        JPanel detailPanel = new JPanel();
        detailPanel.setLayout(new BoxLayout(detailPanel, BoxLayout.Y_AXIS));
        detailPanel.add(Box.createVerticalGlue());
        detailPanel.add(displayTitle);
        detailPanel.add(displayDueDate);
        detailPanel.add(displayCategory);
        detailPanel.add(displayImportance);
        detailPanel.add(displayStatus);
        detailPanel.add(Box.createVerticalGlue());
        mainPanel.add(detailPanel);

        //Displaying the details of the todos in the centre of the display panel and adjusting the size of the text
        displayTitle.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        displayDueDate.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        displayCategory.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        displayImportance.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        displayStatus.setAlignmentX(JComponent.CENTER_ALIGNMENT);

        displayTitle.setFont(new Font("", Font.PLAIN, 20));
        displayDueDate.setFont(new Font("", Font.PLAIN, 20));
        displayCategory.setFont(new Font("", Font.PLAIN, 20));
        displayImportance.setFont(new Font("", Font.PLAIN, 20));
        displayStatus.setFont(new Font("", Font.PLAIN, 20));

        setVisible(true);

    }

    //Updates the content of the ScrollPane when items are edited
    public void updateScrollPane(){
        name = new JList(todoNameIndex.toArray());
        name.addListSelectionListener(this);
        scrollPane.setViewportView(name);
        scrollPane.revalidate();
        scrollPane.repaint();
    }

    //Changes the colour of the text displayed base on the todos category
    public void changeColour(Category colour){
        Color color;
        switch(colour){
            case WHITE -> color = Color.BLACK;
            case RED -> color = Color.RED;
            case BLUE -> color = Color.BLUE;
            case GREEN -> color = Color.GREEN;
            case PURPLE -> color = Color.MAGENTA;
            case YELLOW -> color = Color.YELLOW;
            default -> color = Color.CYAN;
        }
        displayTitle.setForeground(color);
        displayDueDate.setForeground(color);
        displayCategory.setForeground(color);
        displayImportance.setForeground(color);
        displayStatus.setForeground(color);

    }


    public void addTask() throws SQLException, ClassNotFoundException {
        //Creating a pop-up for adding new todos
        JFrame addTodo = new JFrame();
        addTodo.setLayout(new GridLayout(9, 1, 5, 5));
        addTodo.setSize(300, 400);

        //Setting up input fields for the user
        JTextField title = new JTextField("");
        JTextField due = new JTextField();
        JComboBox cat = new JComboBox<>(Category.values());
        JComboBox imp = new JComboBox<>(Importance.values());
        JComboBox status = new JComboBox<>(Status.values());

        //Object array containing the inputs for the user to displayed as an option panel
        Object[] addtodo = {
                "Title", title,
                "Due Date (Format: YYYY-MM-DDTHH:MM)", due,
                cat,
                imp,
                status
        };

        int input = JOptionPane.showConfirmDialog(null, addtodo, "Add a todo", JOptionPane.OK_CANCEL_OPTION);

        //Checks if the user clicked yes before adding the todo
        //to the database
        if(input == 0){
            Todo temp = new Todo(
                    title.getText(),
                    LocalDateTime.parse(due.getText()),
                    (Category) cat.getSelectedItem(),
                    (Importance) imp.getSelectedItem(),
                    (Status) status.getSelectedItem()
            );

            todoList.add(temp);
            todoNameIndex.add(title.getText());
            db.insertTask(title.getText(),
                    LocalDateTime.parse(due.getText()),
                    (Category) cat.getSelectedItem(),
                    (Importance) imp.getSelectedItem(),
                    (Status) status.getSelectedItem());
            updateScrollPane();
        }


    }

    public void updateTask() throws SQLException, ClassNotFoundException {
        //Creating a pop-up for adding new todos
        JFrame updateTodo = new JFrame();
        updateTodo.setLayout(new GridLayout(9, 1, 5, 5));
        updateTodo.setSize(300, 400);

        Todo toUpdate = todoList.get(index);

        //Setting up input fields for the user, existing todo
        //values are displayed in the option pane so users can leave values the same
        JTextField title = new JTextField(toUpdate.getText());
        JTextField due = new JTextField(toUpdate.getDue().toString());
        JComboBox cat = new JComboBox<>(Category.values());
        cat.setSelectedItem(toUpdate.getCat());
        JComboBox imp = new JComboBox<>(Importance.values());
        imp.setSelectedItem(toUpdate.getImportance());
        JComboBox status = new JComboBox<>(Status.values());
        status.setSelectedItem(toUpdate.getCompletion());

        //Object array containing the inputs for the user to displayed as an option panel
        Object[] updatetodo = {
                "Title", title,
                "Due Date (Format: YYYY-MM-DDTHH:MM)", due,
                cat,
                imp,
                status
        };

        int input = JOptionPane.showConfirmDialog(null, updatetodo, "Update Existing Todo", JOptionPane.OK_CANCEL_OPTION);

        //Checks if the user clicked yes before updating the todo
        if(input == 0){
            Todo temp = new Todo(
                    title.getText(),
                    LocalDateTime.parse(due.getText()),
                    (Category) cat.getSelectedItem(),
                    (Importance) imp.getSelectedItem(),
                    (Status) status.getSelectedItem()
            );

            todoList.set(index, temp);
            todoNameIndex.set(index, title.getText());
            db.updateGUI(title.getText(),
                    LocalDateTime.parse(due.getText()),
                    (Category) cat.getSelectedItem(),
                    (Importance) imp.getSelectedItem(),
                    (Status) status.getSelectedItem()
            );
            updateScrollPane();
        }

    }

    public void deleteTask() throws SQLException, ClassNotFoundException {

        //Asks for confirmation before deleting the todo
        Object[] delete = {
                "Delete this todo?",
                todoList.get(index).getText()
        };

        int input = JOptionPane.showConfirmDialog(null, delete, "Delete Todo", JOptionPane.YES_NO_OPTION);

        if(input == 0){
            db.deleteTask(todoList.get(index).getText());
            todoList.remove(index);
            todoNameIndex.remove(index);
            updateScrollPane();

        }
    }




    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getActionCommand().equals("Add Todo")){

            try {
                addTask();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            } catch (ClassNotFoundException classNotFoundException) {
                classNotFoundException.printStackTrace();
            }

        } else if(e.getActionCommand().equals("Update Todo")){
            try {
                updateTask();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            } catch (ClassNotFoundException classNotFoundException) {
                classNotFoundException.printStackTrace();
            }
        } else if(e.getActionCommand().equals("Delete Todo")){
            try {
                deleteTask();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            } catch (ClassNotFoundException classNotFoundException) {
                classNotFoundException.printStackTrace();
            }
        } else {
            //Asks the user if they want to close the program
            int confirm = JOptionPane.showConfirmDialog(null, "Are you sure?", "Exit Program", JOptionPane.YES_NO_OPTION);
            System.out.println(confirm);
            if(confirm == 0) {
                this.dispose();
            }
        }

    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        if(!e.getValueIsAdjusting()) {
            index = name.getSelectedIndex();
            changeColour(todoList.get(index).getCat());
            displayTitle.setText(todoList.get(index).getText());
            displayDueDate.setText(todoList.get(index).getDue().toString());
            displayCategory.setText(todoList.get(index).getCat().toString());
            displayImportance.setText(todoList.get(index).getImportance().toString());
            displayStatus.setText(todoList.get(index).getCompletion().toString());
        }
    }
}
