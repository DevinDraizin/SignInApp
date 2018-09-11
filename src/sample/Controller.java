package sample;

import com.jfoenix.controls.*;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.util.Callback;
import javafx.util.Duration;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;



public class Controller
{
    private static CSVManager database = new CSVManager();
    private static tableStuff tableEditor = new tableStuff();
    public static ArrayList<timeBlock> today;
    private static SimpleDateFormat dtf = new SimpleDateFormat("M/dd/yy h:mm a");
    private static final String adminPass = "Jellybean";
    private static String lastSelected;



    @FXML
    JFXTextField input, searchBar, IDField, firstNameField, lastNameField, salaryField, phoneField, emailField;

    @FXML
    JFXTextArea notesField;

    @FXML
    Label dateLabel;

    @FXML
    JFXButton admin, signIn, breakButton, signOutButton, timeSheetButton, editDayButton, returnHome, setNotes, clearNotes, settingsButton, jokeButton;

    @FXML
    BorderPane adminPanel, homePanel;

    @FXML
    JFXListView<String> sideList;

    @FXML
    JFXDatePicker startDate,endDate;

    @FXML
    JFXCheckBox notifyCheck;

    @FXML
    TableView<day> adminTable;

    @FXML
    AnchorPane rootPane;



    public void initialize() throws IOException
    {
        database.initializeEmployeeData(CSVWriter.filename);
        initDateLabel();
        initActivity();
        initList();
        createTable();
        initAutoWriter();
        dateLabel.setTooltip(new Tooltip("I should be accurate to within a 10th of a second but if not, click me!"));
        funFacts.readFile();
        metrics.readMetrics();

    }

    private void initActivity()
    {
        employee user;

        for(int i=0; i< CSVManager.roster.size(); i++)
        {
            user = CSVManager.roster.get(i);
            if(!user.timeSheet.isEmpty())
            {
                int inactiveLimit = 60;
                if(user.active == 0){}
                else if((int)ChronoUnit.DAYS.between(tableStuff.dateToLocal(user.timeSheet.get(user.timeSheet.size()-1).get(0).in),LocalDate.now()) > inactiveLimit)
                {
                    user.active = 0;
                }

            }
        }
    }

    private void applyDialogStyle(Alert alert)
    {
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add("css/dialog.css");
        dialogPane.getStyleClass().add("myDialog");
    }

    public void getSettings()
    {
        settings setStuff = new settings();
        setStuff.createSettings(adminPanel,rootPane,sideList);
    }


    private void initAutoWriter()
    {
        autoWriter.startThread();

    }


    public void editDay()
    {

        EditUI ui = new EditUI();
        ui.createUI(CSVManager.getID(IDField.getText()),adminTable,startDate,endDate);

    }



    public void getTimeSheet()
    {
        employee user;

        if(input.getText().compareTo("") == 0)
        {
            input.setPromptText("Please Enter ID:");
        }
        else
        {
            user = CSVManager.getID(input.getText());

            if (user == null) {
                input.setText("");
                input.setPromptText("INVALID ID");
                admin.requestFocus();
                return;
            }
            else
            {
                TimeTable table = new TimeTable();
                table.createTimeTable(user,tableEditor);

            }
        }

        input.setText("");
        input.setPromptText("Please Enter ID:");
        admin.requestFocus();

    }


    public void calcReport()
    {
        LocalDate start = startDate.getValue();
        LocalDate end = endDate.getValue();

        if(start.isAfter(end))
        {
            return;
        }

        employee user = CSVManager.getID(IDField.getText());

        if(user == null || user.timeSheet.isEmpty()){return;}
        LinkedHashMap<LocalDate,Double> map = tableStuff.getReport(start,end,user);


        Report.getReport(start,end,user,map);
    }

    public void setJoke()
    {
        notesField.clear();
        notesField.setText(funFacts.getJoke());
    }


    private Callback<DatePicker, DateCell> getDayCellFactory(HashSet<LocalDate> nullDates) {
        return TimeTable.getDayCellFactory(nullDates);
    }


    public void createTable()
    {


        TableColumn breaksCol = new TableColumn("Break Times");
        breaksCol.setMinWidth(498);
        breaksCol.setSortable(false);
        breaksCol.setResizable(true);

        TableColumn<day, Date> signInCol = new TableColumn<>("Sign In");
        signInCol.setMinWidth(249);
        signInCol.setSortable(false);
        signInCol.setResizable(true);
        signInCol.setCellValueFactory(new PropertyValueFactory<>("signIn"));
        signInCol.setCellFactory(new ColumnFormatter<>(dtf));


        TableColumn<day, ArrayList<String>> breakStartCol = new TableColumn<>("Break End");
        breakStartCol.setMinWidth(249);
        breakStartCol.setSortable(false);
        breakStartCol.setResizable(true);
        breakStartCol.setCellValueFactory(new PropertyValueFactory<>("breakIn"));


        TableColumn<day, ArrayList<String>> breakEndCol = new TableColumn<>("Break Start");
        breakEndCol.setMinWidth(245);
        breakEndCol.setSortable(false);
        breakEndCol.setResizable(true);
        breakEndCol.setCellValueFactory(new PropertyValueFactory<>("breakOut"));


        TableColumn<day, Date> signOutCol = new TableColumn<>("Sign Out");
        signOutCol.setMinWidth(245);
        signOutCol.setSortable(false);
        signOutCol.setResizable(true);
        signOutCol.setCellValueFactory(new PropertyValueFactory<>("signOut"));
        signOutCol.setCellFactory(new ColumnFormatter<>(dtf));


        adminTable.getColumns().addAll(signInCol, breaksCol, signOutCol);
        breaksCol.getColumns().addAll(breakEndCol, breakStartCol);

        adminTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

    }

    private void populateTable(employee user, LocalDate start, LocalDate end)
    {
        ObservableList<day> col = tableStuff.createEmployeeTable(user, start, end);

        adminTable.setItems(col);
    }

    public void dateChangeListener()
    {
        if(startDate.getValue() == null || endDate.getValue() == null)
        {
            return;
        }

        if(startDate.getValue().isBefore(endDate.getValue()) || startDate.getValue().isEqual(endDate.getValue()))
        {
            ObservableList<day> col = tableStuff.createEmployeeTable(CSVManager.getID(IDField.getText()), startDate.getValue(), endDate.getValue());

            adminTable.setItems(col);
        }

    }



    private void initDatePickers(employee user)
    {
        if(user.timeSheet.isEmpty()){return;}

        //get most recent day
        LocalDate recent = tableStuff.dateToLocal(user.timeSheet.get(user.timeSheet.size()-1).get(0).in);

        startDate.setValue(recent);
        endDate.setValue(recent);

        HashSet<LocalDate> bounds = tableStuff.getEmployeeBounds(user);


        Callback<DatePicker, DateCell> dayCellFactory = this.getDayCellFactory(bounds);
        startDate.setDayCellFactory(dayCellFactory);
        endDate.setDayCellFactory(dayCellFactory);

        populateTable(user,recent,recent);


    }

    public void setHomeScreen()
    {
        Alert retConf = new Alert(Alert.AlertType.CONFIRMATION);
        retConf.setTitle("Confirmation");
        retConf.setHeaderText("");
        retConf.setContentText("Are you sure you want to leave the admin panel?");
        Optional button = retConf.showAndWait();

        //Switch only if we select 'Ok'
        if(button.isPresent() && button.get() == retConf.getButtonTypes().get(0))
        {
            adminPanel.setVisible(false);
            homePanel.setVisible(true);
            admin.requestFocus();
        }

    }

    //Time is accurate within 10th of a seconds
    public void initDateLabel()
    {
        DateFormat df = new SimpleDateFormat("EEE, MMM d   h:mm:ss a");

        Date date = new Date();
        String stringDate = df.format(date);

        dateLabel.setText("Today's Date:\n" + stringDate);

        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(0),
                actionEvent -> {
                    Date update = new Date();
                    String stringNewDate = df.format(update);
                    dateLabel.setText("Today's Date:\n" + stringNewDate);
                }

        ), new KeyFrame(Duration.seconds(.1)));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();

    }



    public void displaySelect()
    {
        String out = sideList.getSelectionModel().getSelectedItem();
        if(out == null){return;}


        employee selected = CSVManager.getName(out);
        if(selected == null){return;}

        if(lastSelected != null && lastSelected.compareTo(out) == 0) {return;}

        lastSelected = out;

        IDField.setText(selected.ID);
        firstNameField.setText(selected.firstName);
        lastNameField.setText(selected.lastName);
        salaryField.setText("* * * *");
        String temp = selected.phone;
        temp = temp.substring(0,3) + "-" + temp.substring(3,6) + "-" + temp.substring(6);
        phoneField.setText(temp);
        emailField.setText(selected.email);
        notesField.setText(selected.notes);
        if(selected.notify)
        {
            notifyCheck.selectedProperty().setValue(true);
        }
        else
        {
            notifyCheck.selectedProperty().setValue(false);
        }

        initDatePickers(selected);


    }

    //Checkbox action
    public void sendNotify()
    {
        employee user = CSVManager.getID(IDField.getText());

        if(user == null){return;}


        if(notifyCheck.isSelected())
        {
            if(!IDField.getText().isEmpty())
            {
                String note = notesField.getText();

                employee temp = CSVManager.getID(IDField.getText());

                if(temp != null)
                temp.showNotes = note;

            }

            user.notify = true;
        }
        else
        {
            user.showNotes = "";
            user.notify = false;
        }
    }

    private void initList()
    {
        tableStuff.initList(sideList);
    }

    public void searchList()
    {
        String input = searchBar.getText();

        if(!input.isEmpty())
        {
            sideList.getItems().clear();

            ArrayList<String> newList = database.dynamicSearch(input);

            sideList.getItems().addAll(newList);
        }
        else
        {
            initList();
        }
    }

    public void requestAdmin()
    {
        //Temporary
        //force update date label on home page
        //every time we click the admin button
        //initDateLabel();

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Admin Panel");
        dialog.setHeaderText("Administrative access requires a password");
        dialog.setContentText("Enter Password:");
        Optional<String> usrPass = dialog.showAndWait();

        if(usrPass.isPresent())
        {
            if(usrPass.get().compareTo(adminPass) == 0)
            {
                homePanel.setVisible(false);
                adminPanel.setVisible(true);

                sideList.getSelectionModel().select(0);
                displaySelect();
                sideList.requestFocus();


            }
            else
            {
                Alert wrongPass = new Alert(Alert.AlertType.ERROR);
                wrongPass.setTitle("Validation Error");
                wrongPass.setHeaderText("");
                wrongPass.setContentText("Incorrect Password");
                wrongPass.showAndWait();
            }
        }


    }

    public void getID()
    {
        if(IDField.getText().isEmpty()){return;}

        TextInputDialog IDDialog = new TextInputDialog();
        IDDialog.setTitle("Set Employee ID");
        IDDialog.setHeaderText("Please Enter A New 4 Digit ID");
        IDDialog.setContentText("");
        Optional<String> newID = IDDialog.showAndWait();

        if(newID.isPresent() && addRemoveEmployee.sanID(newID.get()))
        {
            employee curr = CSVManager.getID(IDField.getText());

            //We check for this when we check if IDField is empty
            assert curr != null;
            curr.setID(newID.get());

            IDField.setText(newID.get());
        }

    }

    public void getFirst()
    {
        if(IDField.getText().isEmpty()){return;}

        TextInputDialog IDDialog = new TextInputDialog();
        IDDialog.setTitle("Set Employee First Name");
        IDDialog.setHeaderText("Please Enter A New Name");
        IDDialog.setContentText("");
        Optional<String> newFirstName = IDDialog.showAndWait();

        if(newFirstName.isPresent() && addRemoveEmployee.sanFirst(newFirstName.get()))
        {
            employee curr = CSVManager.getID(IDField.getText());

            assert curr != null;
            curr.setFirstName(newFirstName.get());

            firstNameField.setText(newFirstName.get());
            initList();
        }
    }


    public void getLast()
    {
        if(IDField.getText().isEmpty()){return;}

        TextInputDialog IDDialog = new TextInputDialog();
        IDDialog.setTitle("Set Employee Last Name");
        IDDialog.setHeaderText("Please Enter A New Name");
        IDDialog.setContentText("");
        Optional<String> newLastName = IDDialog.showAndWait();

        if(newLastName.isPresent() && addRemoveEmployee.sanLast(newLastName.get()))
        {
            employee curr = CSVManager.getID(IDField.getText());

            assert curr != null;
            curr.setLastName(newLastName.get());

            lastNameField.setText(newLastName.get());
            initList();
        }
    }

    public void getSalary()
    {
        if(IDField.getText().isEmpty()){return;}

        employee curr = CSVManager.getID(IDField.getText());

        TextInputDialog IDDialog = new TextInputDialog();
        IDDialog.setTitle("Set Employee Salary");
        IDDialog.setHeaderText("Current Salary: " + curr.salary + "$");
        IDDialog.setContentText("Enter New Salary");
        Optional<String> newSalary = IDDialog.showAndWait();

        if(newSalary.isPresent() && addRemoveEmployee.sanSalary(newSalary.get()))
        {
            curr.setSalary(newSalary.get());
        }
    }


    public void getPhone()
    {
        if(phoneField.getText().isEmpty()){return;}

        TextInputDialog phoneDialog = new TextInputDialog();
        phoneDialog.setTitle("Set Employee Phone Number");
        phoneDialog.setHeaderText("Please Enter A New Phone Number (Only Numbers)");
        phoneDialog.setContentText("");
        Optional<String> newPhone = phoneDialog.showAndWait();

        if(newPhone.isPresent() && addRemoveEmployee.sanPhone(newPhone.get()))
        {
            employee curr = CSVManager.getID(IDField.getText());

            assert curr != null;
            curr.setPhone(newPhone.get());

            String temp = newPhone.get();
            temp = temp.substring(0,3) + "-" + temp.substring(3,6) + "-" + temp.substring(6);

            phoneField.setText(temp);
        }
    }

    public void getEmail()
    {

        if(emailField.getText().isEmpty()){return;}

        TextInputDialog emailDialog = new TextInputDialog();
        emailDialog.setTitle("Set Employee Email");
        emailDialog.setHeaderText("Please Enter A New Email");
        emailDialog.setContentText("");
        Optional<String> newEmail = emailDialog.showAndWait();

        if(newEmail.isPresent() && addRemoveEmployee.sanEmail(newEmail.get()))
        {
            employee curr = CSVManager.getID(IDField.getText());

            assert curr != null;
            curr.setEmail(newEmail.get());

            emailField.setText(newEmail.get());
        }
    }

    public void getNotes()
    {

        if(setNotes.getText().compareTo("Edit") == 0)
        {
            setNotes.setText("Set");
            notesField.setEditable(true);
            clearNotes.setVisible(true);
            jokeButton.setVisible(true);
            notesField.requestFocus();
            notesField.positionCaret(notesField.getText().length());

        }
        else
        {
            employee curr = CSVManager.getID(IDField.getText());
            assert curr != null;
            curr.setNotes(notesField.getText());
            notesField.setEditable(false);
            clearNotes.setVisible(false);
            jokeButton.setVisible(false);
            setNotes.setText("Edit");
        }
    }

    public void clearNoteText()
    {
        notesField.clear();
        notesField.setPromptText("Enter New Note:");
    }


    public void signIn()
    {
        employee user;
        ArrayList<timeBlock> usrTimeSheet;
        String specNotes = "";

        if(input.getText().compareTo("") == 0)
        {
            input.setPromptText("Please Enter ID:");
        }
        else
        {
            user = CSVManager.getID(input.getText());

            if(user == null)
            {
                input.setText("");
                input.setPromptText("INVALID ID");
                admin.requestFocus();
            }
            else
            {
                usrTimeSheet = database.getCurrentTimeSheet(user);

                if(usrTimeSheet.isEmpty())
                {
                    database.addToSheet(usrTimeSheet, new Date());

                    if(user.notify)
                    {
                        specNotes = "\n\nNotes:\n" + user.showNotes;
                        user.showNotes = "";
                        user.notify = false;
                    }

                    Alert welcome = new Alert(Alert.AlertType.CONFIRMATION);
                    welcome.setTitle("Sign In");
                    welcome.setHeaderText("Hello " + user.firstName + "!\nYou signed in at " + new SimpleDateFormat("h:mm:ss a").format(Calendar.getInstance(TimeZone.getTimeZone("America/New_York")).getTime()) + specNotes);
                    applyDialogStyle(welcome);
                    welcome.showAndWait();

                }
                else if(usrTimeSheet.get(usrTimeSheet.size() - 1).end)
                {
                    Alert signInErr = new Alert(Alert.AlertType.WARNING);
                    signInErr.setTitle("Sign In Error");
                    signInErr.setHeaderText("You have already signed out today " + user.firstName + "!");
                    applyDialogStyle(signInErr);
                    signInErr.showAndWait();
                }
                else
                {
                    Alert signInErr = new Alert(Alert.AlertType.WARNING);
                    signInErr.setTitle("Sign In Error");
                    signInErr.setHeaderText("You have already signed in " + user.firstName + "!");
                    applyDialogStyle(signInErr);
                    signInErr.showAndWait();
                }

                input.setText("");
                input.setPromptText("Please Enter ID:");
                admin.requestFocus();

            }
        }
    }

    //Id love to name this method "break" but syntax
    public void pause()
    {
        employee user;
        ArrayList<timeBlock> usrTimeSheet;

        if(input.getText().compareTo("") == 0)
        {
            input.setPromptText("Please Enter ID:");
        }
        else
        {
            user = CSVManager.getID(input.getText());

            if(user == null)
            {
                input.setText("");
                input.setPromptText("INVALID ID");
                admin.requestFocus();
            }
            else
            {
                usrTimeSheet = database.getCurrentTimeSheet(user);



                if(usrTimeSheet.isEmpty())
                {
                    Alert signInErr = new Alert(Alert.AlertType.WARNING);
                    signInErr.setTitle("Break Error");
                    signInErr.setHeaderText("You have not yet signed in " + user.firstName + "!");
                    applyDialogStyle(signInErr);
                    signInErr.showAndWait();
                    user.timeSheet.remove(user.timeSheet.size()-1);
                }
                else if(usrTimeSheet.get(usrTimeSheet.size() - 1).end)
                {
                    Alert signInErr = new Alert(Alert.AlertType.WARNING);
                    signInErr.setTitle("Break Error");
                    signInErr.setHeaderText("You have already signed out today" + user.firstName + "!");
                    applyDialogStyle(signInErr);
                    signInErr.showAndWait();
                }
                else
                {
                    if(usrTimeSheet.get(usrTimeSheet.size()-1).out == null)
                    {
                        Alert welcome = new Alert(Alert.AlertType.CONFIRMATION);
                        welcome.setTitle("Sign In");
                        welcome.setHeaderText("You are on break as of "  + new SimpleDateFormat("h:mm:ss a").format(Calendar.getInstance(TimeZone.getTimeZone("America/New_York")).getTime()) + ", " + user.firstName + "!");
                        applyDialogStyle(welcome);
                        welcome.showAndWait();
                    }
                    else
                    {
                        Alert welcome = new Alert(Alert.AlertType.CONFIRMATION);
                        welcome.setTitle("Sign In");
                        welcome.setHeaderText("You are no longer on break as of "  + new SimpleDateFormat("h:mm:ss a").format(Calendar.getInstance(TimeZone.getTimeZone("America/New_York")).getTime()) + ", " + user.firstName + "!");
                        applyDialogStyle(welcome);
                        welcome.showAndWait();

                    }


                    database.addToSheet(usrTimeSheet, new Date());
                }

                //reset textArea
                input.setText("");
                input.setPromptText("Please Enter ID:");
                admin.requestFocus();

            }
        }
    }

    public void signOut()
    {
        employee user;
        ArrayList<timeBlock> usrTimeSheet;


        if(input.getText().compareTo("") == 0)
        {
            input.setPromptText("Please Enter ID:");
        }
        else
        {
            user = CSVManager.getID(input.getText());

            if(user == null)
            {
                input.setText("");
                input.setPromptText("INVALID ID");
                admin.requestFocus();
            }
            else
            {
                usrTimeSheet = database.getCurrentTimeSheet(user);

                if(usrTimeSheet.isEmpty())
                {
                    Alert signInErr = new Alert(Alert.AlertType.WARNING);
                    signInErr.setTitle("Sign Out Error");
                    signInErr.setHeaderText("You have not yet signed in " + user.firstName + "!");
                    applyDialogStyle(signInErr);
                    signInErr.showAndWait();
                    user.timeSheet.remove(user.timeSheet.size()-1);
                }
                else if(usrTimeSheet.get(usrTimeSheet.size() - 1).end)
                {
                    Alert signInErr = new Alert(Alert.AlertType.WARNING);
                    signInErr.setTitle("Sign out Error");
                    signInErr.setHeaderText("You have already signed out today" + user.firstName + "!");
                    applyDialogStyle(signInErr);
                    signInErr.showAndWait();
                }
                else
                {

                    Alert closePopup = new Alert(Alert.AlertType.CONFIRMATION);
                    closePopup.setTitle("Sign Out Confirmation");
                    closePopup.setHeaderText("Are you sure you want to sign out?\n\nYou will not be able to sign in again until tomorrow");
                    applyDialogStyle(closePopup);

                    ButtonType cancel = new ButtonType("Cancel");
                    ButtonType signOut = new ButtonType("Yes");
                    ButtonType  quit = new ButtonType("No");


                    closePopup.getButtonTypes().setAll(signOut,quit,cancel);
                    Optional<ButtonType> result = closePopup.showAndWait();

                    if (result.isPresent() && (result.get() == cancel || result.get() == quit))
                    {
                        input.setText("");
                        input.setPromptText("Please Enter ID:");
                        admin.requestFocus();
                        return;
                    }

                    if(usrTimeSheet.get(usrTimeSheet.size()-1).out == null)
                    {
                        database.addToSheet(usrTimeSheet, new Date());
                    }

                    Alert welcome = new Alert(Alert.AlertType.CONFIRMATION);
                    welcome.setTitle("Sign In");
                    welcome.setHeaderText("Goodbye " + user.firstName + "!\nYou signed out at " + new SimpleDateFormat("h:mm:ss a").format(Calendar.getInstance(TimeZone.getTimeZone("America/New_York")).getTime()));
                    applyDialogStyle(welcome);
                    welcome.showAndWait();

                    usrTimeSheet.get(usrTimeSheet.size()-1).end = true;
                }

                //reset textArea
                input.setText("");
                input.setPromptText("Please Enter ID:");
                admin.requestFocus();

            }
        }
    }


    public void over()
    {
        signIn.setStyle("-fx-background-color: #9BAEBE");
    }

    public void exit()
    {
        signIn.setStyle("-fx-background-color: transparent");
    }

}
