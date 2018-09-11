package sample;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXListView;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;


//This class creates and initializes the
//activity window from settings.
//This allows admin see last sign in
//for each employee as well as a checkbox
//that will toggle the employee in the
//admin panel list. Toggled off employees
//will not show in the sign panel and cannot
//be searched for until they are checked again.
//Additionally after 60 days of inactivity
//employees will automatically be removed from
//the list
public class employeeActivity
{
    private Stage window = new Stage();
    private BorderPane mainLayout = new BorderPane();
    private GridPane employeeLabels = new GridPane();
    private VBox content = new VBox(10);
    private HBox headings = new HBox(10);
    private HBox buttons = new HBox(28);
    private Label headerLabel = new Label("Active Employees");
    private JFXListView<String> list;
    private JFXButton closeButton = new JFXButton("Done");



    private void checkListener(employee user)
    {
        if(user.active == 1)//Just Unchecked
        {
            user.active = 0;
        }
        else//Just checked
        {
            user.active = 1;
        }

        tableStuff.initList(list);
    }

    private int getDSLSI(employee user) //returns # of days since last sign in
    {
        if(user.timeSheet.size() == 0)
        {
            return 0;
        }
        else
        {
            return (int)ChronoUnit.DAYS.between(tableStuff.dateToLocal(user.timeSheet.get(user.timeSheet.size()-1).get(0).in),LocalDate.now());
        }
    }

    private JFXCheckBox initCheck(employee user)
    {
        JFXCheckBox check = new JFXCheckBox();

        if(user.active == 1)
        {
            check.setSelected(true);
        }
        else
        {
            check.setSelected(false);
        }

        check.setOnAction(e -> checkListener(user));

        return check;
    }

    private void makeContent()
    {
        HBox headers = new HBox(48);
        headers.setAlignment(Pos.CENTER);
        headers.setMinHeight(40);

        content.setAlignment(Pos.CENTER);

        employeeLabels.setAlignment(Pos.CENTER);
        employeeLabels.setPadding(new Insets(10,10,10,10));
        employeeLabels.setVgap(28);
        employeeLabels.setHgap(28);

        Label DSLSILabel = new Label("Last Seen");
        DSLSILabel.getStyleClass().add("descriptions");

        Label nameLabel = new Label("Employees");
        nameLabel.getStyleClass().add("descriptions");

        Label checksLabel = new Label("Active");
        checksLabel.getStyleClass().add("descriptions");

        headers.getChildren().addAll(DSLSILabel,nameLabel,checksLabel);


        for(int i=0; i<CSVManager.roster.size(); i++)
        {
            HBox numbers = new HBox(6);
            numbers.setAlignment(Pos.CENTER_RIGHT);

            Label days = new Label(String.valueOf(getDSLSI(CSVManager.roster.get(i))));
            Label lab = new Label(" Days Ago");
            days.getStyleClass().add("number-label");
            numbers.getChildren().addAll(days,lab);

            employeeLabels.add(numbers,0,i);
            employeeLabels.add(new Label(CSVManager.roster.get(i).firstName + " " + CSVManager.roster.get(i).lastName),1,i);

            JFXCheckBox check = initCheck(CSVManager.roster.get(i));
            GridPane.setHalignment(check, HPos.CENTER);
            employeeLabels.add(check,2,i);
        }

        ScrollPane scroll = new ScrollPane();
        scroll.setContent(employeeLabels);
        scroll.setMaxHeight(450);

        content.getChildren().addAll(headers,scroll);
    }


    void createUI(JFXListView<String> sideList,Stage settingsWindow)
    {
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Employee Activity");
        window.setWidth(450);
        window.setHeight(680);

        list = sideList;

        headings.getChildren().add(headerLabel);
        headings.setAlignment(Pos.CENTER);

        buttons.setAlignment(Pos.CENTER);
        buttons.getChildren().add(closeButton);

        headerLabel.getStyleClass().add("header-label");

        makeContent();

        mainLayout.setTop(headings);
        mainLayout.setCenter(content);
        mainLayout.setBottom(buttons);
        mainLayout.setPadding(new Insets(20,20,20,20));

        closeButton.setOnAction(e -> window.close());

        Scene scene = new Scene(mainLayout);
        scene.getStylesheets().add("css/employeeActivity.css");
        window.setScene(scene);
        headerLabel.requestFocus();
        window.showAndWait();
        settingsWindow.close();
    }
}
