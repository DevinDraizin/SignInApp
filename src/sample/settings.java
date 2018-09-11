package sample;


import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXToggleButton;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.Optional;

import static sample.CSVManager.roster;


public class settings
{
    private JFXButton cancel = new JFXButton("Close");
    private JFXButton addEmp = new JFXButton("Add Employee");
    private JFXButton remEmp = new JFXButton("Remove Employee");
    private JFXButton inactButton = new JFXButton("Employee Activity");
    private JFXButton write = new JFXButton("Save Data");
    private JFXButton emailButton = new JFXButton("Email Backup");
    private JFXButton metricsButton = new JFXButton("Program Metrics");

    private Label header = new Label("Settings");
    private JFXToggleButton autoWriteToggle = new JFXToggleButton();

    private Stage window = new Stage();
    private BorderPane mainLayout = new BorderPane();
    private HBox bottomButtons = new HBox(16);
    private HBox topBanner = new HBox(10);
    private VBox centerControls = new VBox(14);



    private void toggleAutoWriter()
    {
        if(autoWriteToggle.isSelected())
        {
            autoWriter.startThread();
        }
        else
        {
            Alert conf = new Alert(Alert.AlertType.CONFIRMATION);
            conf.setHeaderText("Are you sure you want to disable auto backup?");
            conf.setContentText("This will stop local AND email backups!\nYou will still be able to force backups from settings");

            ButtonType cancel = new ButtonType("Cancel");
            ButtonType yes = new ButtonType("Yes");
            ButtonType  no = new ButtonType("No");


            conf.getButtonTypes().setAll(yes,no,cancel);
            Optional<ButtonType> result = conf.showAndWait();

            if(result.isPresent() && result.get() == yes)
            {
                autoWriter.killThread();
            }
            else
            {
                autoWriteToggle.setSelected(true);
            }

        }
    }

    private void writeData()
    {

        Alert conf = new Alert(Alert.AlertType.CONFIRMATION);
        conf.setTitle("Confirm Save");
        conf.setHeaderText("Are you sure you want to save all current data?");



        ButtonType cancel = new ButtonType("Cancel");
        ButtonType yes = new ButtonType("Yes");
        ButtonType  no = new ButtonType("No");


        conf.getButtonTypes().setAll(yes,no,cancel);
        Optional<ButtonType> result = conf.showAndWait();

        if(result.isPresent() && result.get() == yes)
        {
            CSVWriter writer = new CSVWriter();


            writer.masterWrite(roster);

            Alert success = new Alert(Alert.AlertType.CONFIRMATION);
            success.setTitle("Success!");
            success.setHeaderText("Successfully saved to the database");
            success.setContentText(" ");
            success.showAndWait();


        }

    }


    private void emailBackup()
    {
        Alert conf = new Alert(Alert.AlertType.CONFIRMATION);
        conf.setTitle("Confirm Backup");
        conf.setHeaderText("Are you sure you want to send a backup of the current database?");


        ButtonType cancel = new ButtonType("Cancel");
        ButtonType yes = new ButtonType("Yes");
        ButtonType  no = new ButtonType("No");


        conf.getButtonTypes().setAll(yes,no,cancel);
        Optional<ButtonType> result = conf.showAndWait();

        if(result.isPresent() && result.get() == yes)
        {
            String old = emailer.getTitle();
            emailer.setTitle("Forced Backup on "+old);
            emailer.sendBackup();
            emailer.setTitle(old);

        }


    }


    private void getAddScene(BorderPane lastLayout, AnchorPane root,JFXListView<String> sideList)
    {
        window.close();
        addEmployeeUI ui = new addEmployeeUI();
        ui.createAddScene(lastLayout,root,sideList);

    }

    private void getRemScene(BorderPane lastLayout, AnchorPane root, JFXListView<String> sideList)
    {
        window.close();
        removeEmployeeUI ui = new removeEmployeeUI();
        ui.createRemScene(lastLayout,root,sideList);
    }

    private void initUI()
    {
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Settings");
        window.setWidth(300);
        window.setHeight(640);

        mainLayout.setPadding(new Insets(22,22,22,22));

        bottomButtons.setAlignment(Pos.CENTER);
        bottomButtons.getChildren().addAll(cancel);
        bottomButtons.setPadding(new Insets(22,0,0,0));

        centerControls.setAlignment(Pos.TOP_CENTER);
        autoWriteToggle.setText("Auto Backup");
        autoWriteToggle.setTooltip(new Tooltip("This will save locally and to server at 11:50 everyday"));
        autoWriteToggle.contentDisplayProperty().setValue(ContentDisplay.TOP);

        if(autoWriter.hasStarted)
        {
            autoWriteToggle.setSelected(true);
        }
        centerControls.getChildren().addAll(addEmp,remEmp,inactButton,metricsButton,emailButton,write,autoWriteToggle);

        topBanner.setAlignment(Pos.CENTER);
        topBanner.setPadding(new Insets(0,0,40,0));
        topBanner.getChildren().addAll(header);


        mainLayout.setBottom(bottomButtons);
        mainLayout.setCenter(centerControls);
        mainLayout.setTop(topBanner);

        Scene scene = new Scene(mainLayout);
        scene.getStylesheets().add("css/settingsStyle.css");
        header.requestFocus();
        window.setScene(scene);
        window.showAndWait();
    }



    void createSettings(BorderPane lastLayout, AnchorPane root, JFXListView<String> sideList)
    {
        cancel.setOnAction(e -> window.close());
        write.setOnAction(e -> writeData());
        inactButton.setOnAction(e -> new employeeActivity().createUI(sideList,window));
        emailButton.setOnAction(e -> emailBackup());
        autoWriteToggle.setOnAction(e -> toggleAutoWriter());
        metricsButton.setOnAction(e -> infoUI.createUI(window));
        addEmp.setOnAction(e -> getAddScene(lastLayout,root,sideList));
        remEmp.setOnAction(e -> getRemScene(lastLayout,root,sideList));

        initUI();
    }

}
