package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import java.util.Optional;


//Loads main UI as well as handles some
//closing actions
public class Main extends Application {


    public static void main(String[] args) {

        launch(args);

    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Super Saucy Sign-In Sheet");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
        primaryStage.setMaximized(true);


        primaryStage.setOnCloseRequest(e -> {
            e.consume();
            closeProgram(primaryStage);
            autoWriter.killThread();
    });

    }

    private void closeProgram(Stage primaryStage)
    {
        Alert closePopup = new Alert(Alert.AlertType.CONFIRMATION);
        closePopup.setTitle("Close Application");
        closePopup.setHeaderText("Would you like to save the current day before you exit?");

        ButtonType cancel = new ButtonType("Cancel");
        ButtonType quitAndSave = new ButtonType("Yes");
        ButtonType  quit = new ButtonType("No");


        closePopup.getButtonTypes().setAll(quitAndSave,quit,cancel);
        Optional<ButtonType> result = closePopup.showAndWait();


        if (result.isPresent() && result.get() == quitAndSave)
        {
            CSVWriter writer = new CSVWriter();
            writer.masterWrite(CSVManager.roster);
            metrics.writeMetrics();
            primaryStage.close();

        }
        else if (result.isPresent() && result.get() == quit)
        {
            Alert conf = new Alert(Alert.AlertType.WARNING);
            conf.setHeaderText("ARE YOU REALLY REALLY SURE YOU WANT TO DELETE TODAY'S DATA?!?!");
            conf.setContentText("If you do this, all of the sign in data from today will be lost");
            ButtonType cancelNo = new ButtonType("Cancel");
            ButtonType reallyquit = new ButtonType("Yes I'm Sure");
            ButtonType  nope = new ButtonType("No!!!!");


            conf.getButtonTypes().setAll(nope,reallyquit,cancelNo);
            Optional<ButtonType> getres = conf.showAndWait();

            if(getres.isPresent() && getres.get() == reallyquit)
            {
                metrics.writeMetrics();
                primaryStage.close();
            }

        }

    }




}
