package sample;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDatePicker;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;

public class TimeTable
{

    public JFXDatePicker start, end;

    private TableView<day> userTable;



    static Callback<DatePicker, DateCell> getDayCellFactory(HashSet<LocalDate> nullDates) {

        return editorCommons.getDayCellFactoryStandard(nullDates);
    }

    private void populateTable(employee user, LocalDate start, LocalDate end, tableStuff tableEditor)
    {
        ObservableList<day> col = tableStuff.createEmployeeTable(user, start, end);

        userTable.setItems(col);
    }


    public void createTable()
    {


        TableColumn breaksCol = new TableColumn("Break Times");
        breaksCol.setMinWidth(480);
        breaksCol.setSortable(false);
        breaksCol.setResizable(true);

        TableColumn<day, Date> signInCol = new TableColumn<>("Sign In");
        signInCol.setMinWidth(240);
        signInCol.setSortable(false);
        signInCol.setResizable(true);
        signInCol.setCellValueFactory(new PropertyValueFactory<>("signIn"));
        signInCol.setCellFactory(new ColumnFormatter<>(new SimpleDateFormat("M/dd/yy h:mm a")));


        TableColumn<day, ArrayList<String>> breakStartCol = new TableColumn<>("Break End");
        breakStartCol.setMinWidth(240);
        breakStartCol.setSortable(false);
        breakStartCol.setResizable(true);
        breakStartCol.setCellValueFactory(new PropertyValueFactory<>("breakIn"));


        TableColumn<day, ArrayList<String>> breakEndCol = new TableColumn<>("Break Start");
        breakEndCol.setMinWidth(238);
        breakEndCol.setSortable(false);
        breakEndCol.setResizable(true);
        breakEndCol.setCellValueFactory(new PropertyValueFactory<>("breakOut"));


        TableColumn<day, Date> signOutCol = new TableColumn<>("Sign Out");
        signOutCol.setMinWidth(238);
        signOutCol.setSortable(false);
        signOutCol.setResizable(true);
        signOutCol.setCellValueFactory(new PropertyValueFactory<>("signOut"));
        signOutCol.setCellFactory(new ColumnFormatter<>(new SimpleDateFormat("M/dd/yy h:mm a")));


        userTable.getColumns().addAll(signInCol, breaksCol, signOutCol);
        breaksCol.getColumns().addAll(breakEndCol, breakStartCol);

        userTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

    }

    private void dateChangeListener(tableStuff tableEditor, employee user)
    {
        if(start.getValue() == null || end.getValue() == null)
        {
            return;
        }

        if(start.getValue().isBefore(end.getValue()) || start.getValue().isEqual(end.getValue()))
        {
            ObservableList<day> col = tableStuff.createEmployeeTable(user, start.getValue(), end.getValue());

            userTable.setItems(col);
        }

    }

    private void initDatePickers(employee user, tableStuff tableEditor)
    {
        if(user.timeSheet.isEmpty()){return;}

        //get most recent day
        LocalDate recent = tableStuff.dateToLocal(user.timeSheet.get(user.timeSheet.size()-1).get(0).in);

        start.setValue(recent);
        end.setValue(recent);

        HashSet<LocalDate> bounds = tableStuff.getEmployeeBounds(user);


        Callback<DatePicker, DateCell> dayCellFactory= getDayCellFactory(bounds);
        start.setDayCellFactory(dayCellFactory);
        end.setDayCellFactory(dayCellFactory);

        populateTable(user,recent,recent,tableEditor);


    }

    private void sendSheet(employee user)
    {
        if(user.timeSheet.isEmpty())
        {
            Alert err = new Alert(Alert.AlertType.WARNING);
            err.setTitle("No Input Error");
            err.setHeaderText("Before you can receive your time sheet you must have at least 1 day in the database");
            err.setContentText("You can email your time sheet after you sign in");
            err.showAndWait();
            return;
        }

        sendEmployeeTimesheet sender = new sendEmployeeTimesheet();
        sender.createUI(user);
    }

    void createTimeTable(employee user, tableStuff tableEditor)
    {
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);

        window.setTitle("Time Sheet");
        window.setWidth(1040);
        window.setHeight(600);

        start = new JFXDatePicker();
        start.setEditable(false);
        start.setDefaultColor(Paint.valueOf("#1e334c"));
        end = new JFXDatePicker();
        end.setEditable(false);
        end.setDefaultColor(Paint.valueOf("#1e334c"));
        userTable = new TableView<>();
        userTable.getStylesheets().add("css/table.css");

        Label desc = new Label("Hello " + user.firstName + ", here is your time sheet:");
        desc.setStyle("-fx-font-size: 18; -fx-text-fill: white;");

        Label startLabel = new Label("Start:");
        startLabel.setStyle("-fx-font-size: 18; -fx-text-fill: white;");

        Label endLabel = new Label("End:");
        endLabel.setStyle("-fx-font-size: 18; -fx-text-fill: white;");

        JFXButton close = new JFXButton("Close");
        close.setStyle("-fx-text-fill: white; -fx-font-size: 18; -fx-background-color: #5197d5");

        JFXButton emailButton = new JFXButton("Email Time Sheet");
        emailButton.setStyle("-fx-text-fill: white; -fx-font-size: 18; -fx-background-color: #5197d5");

        start.setStyle("-fx-text-inner-color: #ffffff; -fx-font-size: 18;");
        end.setStyle("-fx-text-inner-color: #ffffff; -fx-font-size: 18");


        close.setOnAction(e -> window.close());
        emailButton.setOnAction(e -> sendSheet(user));


        initDatePickers(user,tableEditor);
        createTable();

        start.setOnAction(e -> dateChangeListener(tableEditor,user));
        end.setOnAction(e -> dateChangeListener(tableEditor,user));


        VBox mainLayout = new VBox(20);
        HBox dateContainer = new HBox(60);
        HBox buttons = new HBox(20);
        buttons.setAlignment(Pos.CENTER);
        buttons.getChildren().addAll(close,emailButton);


        dateContainer.getChildren().addAll(startLabel,start,endLabel,end);
        mainLayout.getChildren().addAll(desc,dateContainer,userTable,buttons);

        mainLayout.setStyle("-fx-background-color: #415F83; -fx-padding: 20;");


        Scene scene = new Scene(mainLayout);

        desc.requestFocus();

        window.setScene(scene);
        window.showAndWait();

    }
}
