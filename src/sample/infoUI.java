package sample;

import com.jfoenix.controls.JFXButton;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;


/*
Displays:

Current Filename
Employee Count
Last Successful backup
Last Successful server backup

*/

public class infoUI
{

    static void close(Stage curr, Stage prev)
    {
        curr.close();
        prev.close();
    }

    private static Label getDataLabel(String input)
    {
        Label label = new Label(input);
        label.getStyleClass().add("data-label");

        return label;
    }

    private static Label getNameLabel(String input)
    {
        Label label = new Label(input);
        label.getStyleClass().add("content-label");

        return label;
    }

    static void createUI(Stage prev)
    {
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Program Information");
        window.setWidth(600);
        window.setHeight(600);

        JFXButton closeButton = new JFXButton("Close");

        GridPane centerGrid = new GridPane();

        centerGrid.setAlignment(Pos.CENTER);
        centerGrid.setPadding(new Insets(10,10,10,10));
        centerGrid.setVgap(12);
        centerGrid.setHgap(10);

        Label header = new Label("HeadsUp Sign In App");
        header.getStyleClass().add("header-label");

        Label desc = new Label("Program Metrics:");

        //************** File Name ****************

        Label fileStart = getNameLabel("Current Database: ");
        Label filename = getDataLabel(CSVWriter.filename);

        centerGrid.add(fileStart,0,0);
        centerGrid.add(filename,1,0);

        //************** Employee Count ****************

        Label empCount = getNameLabel("Employee Count ");
        Label count = getDataLabel(String.valueOf(CSVManager.roster.size()));

        centerGrid.add(empCount,0,1);
        centerGrid.add(count,1,1);

        //************** Currently Signed In ****************

        Label currentSigned = getNameLabel("Currently Signed In: ");
        Label Signed = getDataLabel(metrics.getCurrentSignedIn());

        centerGrid.add(currentSigned,0,2);
        centerGrid.add(Signed,1,2);

        //************** Last Backup ****************

        Label LastBack = getNameLabel("Last Backup: ");
        Label Back = getDataLabel(metrics.getLastBackup());

        centerGrid.add(LastBack,0,3);
        centerGrid.add(Back,1,3);

        //************** Last Server Backup ****************

        Label LastSBack = getNameLabel("Last Server Backup: ");
        Label SBack = getDataLabel(metrics.getLastSBackup());

        centerGrid.add(LastSBack,0,4);
        centerGrid.add(SBack,1,4);




        Label bottomLabel = new Label("Created by Lethaljellybean");
        bottomLabel.getStyleClass().clear();
        bottomLabel.getStyleClass().add("info-label");

        VBox centerLayout = new VBox(20);
        centerLayout.setAlignment(Pos.TOP_CENTER);
        centerLayout.setPadding(new Insets(28,0,0,0));
        centerLayout.getChildren().addAll(desc,centerGrid);

        VBox bottomBox = new VBox(28);
        bottomBox.setAlignment(Pos.CENTER);
        bottomBox.getChildren().addAll(closeButton,bottomLabel);

        BorderPane mainLayout = new BorderPane();
        BorderPane.setAlignment(header,Pos.CENTER);
        BorderPane.setAlignment(bottomLabel,Pos.CENTER);
        mainLayout.setPadding(new Insets(20,20,20,20));

        mainLayout.setTop(header);
        mainLayout.setCenter(centerLayout);
        mainLayout.setBottom(bottomBox);

        closeButton.setOnAction(e -> close(window,prev));


        Scene scene = new Scene(mainLayout);
        scene.getStylesheets().add("css/infoStyle.css");
        window.setScene(scene);

        desc.requestFocus();

        window.showAndWait();
    }
}
