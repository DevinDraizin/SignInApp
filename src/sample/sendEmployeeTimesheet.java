package sample;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXTextField;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;


//This is the window created when the user selects email time sheet in the user time sheet window
//Here the user can optionally send their own time sheet to the email they have on file or they
//can input a different email to send it to. Then we call the writeEmployeeSheet class to actually
//send the sheet.
public class sendEmployeeTimesheet
{
    private String email;
    private employee user;

    private Label header = new Label("Email Time Sheet");
    private Label desc = new Label("Send To: ");
    private Label emailLabel = new Label();

    private BorderPane mainLayout = new BorderPane();

    private JFXButton confirmButton = new JFXButton("Send");
    private JFXButton cancelButton = new JFXButton("Cancel");

    private JFXCheckBox check = new JFXCheckBox("Use email on file?");

    private JFXTextField input = new JFXTextField();


    //Method called directly by the send button
    //if the user chooses a new email we sanitize
    //the new email. If not we just send the email
    private void send()
    {
        writeEmployeeSheet writer = new writeEmployeeSheet();

        Alert err = new Alert(Alert.AlertType.ERROR);
        err.setTitle("No Input Error");
        err.setHeaderText("You need to add a new email or use the email on file before you can send");

        if(check.isSelected())
        {
            writer.sendTimeSheet(user,email);
        }
        else
        {
            if(!input.getText().isEmpty())
            {
                String newEmail = input.getText();

                //notification is handled in sanEmail()
                if(addRemoveEmployee.sanEmail(newEmail))
                {
                    writer.sendTimeSheet(user,newEmail);
                }
                else
                {
                    return;
                }
            }
            else
            {
                err.showAndWait();
                return;
            }

        }

        //stage reference is out of scope so we can call close indirectly through the listener
        cancelButton.fire();
    }

    //Listener for the checkbox
    private void initCheckbox()
    {
        if(check.isSelected())
        {
            input.setVisible(false);
            email = user.email;
            emailLabel.textProperty().unbind();
            emailLabel.setText(user.email);
        }
        else
        {
            input.clear();
            input.setVisible(true);
            emailLabel.textProperty().bind(input.textProperty());
            input.requestFocus();
        }
    }



    void createUI(employee emp)
    {
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Settings");
        window.setHeight(360);
        window.setWidth(500);

        user = emp;
        email = emp.email;

        header.getStyleClass().clear();
        header.getStyleClass().add("header-label");

        emailLabel.setText(email);
        emailLabel.getStyleClass().clear();
        emailLabel.getStyleClass().add("email-label");

        check.setSelected(true);

        input.setPromptText("Enter New Email");
        input.setMaxWidth(300);
        input.setVisible(false);


        HBox emLabels = new HBox();
        emLabels.setAlignment(Pos.CENTER);
        emLabels.getChildren().addAll(desc,emailLabel);

        HBox buttons = new HBox(28);
        buttons.setAlignment(Pos.CENTER);
        buttons.getChildren().addAll(confirmButton,cancelButton);

        VBox headingLayout = new VBox(18);
        headingLayout.getChildren().addAll(header,emLabels);
        headingLayout.setAlignment(Pos.CENTER);

        VBox content = new VBox(18);
        content.setAlignment(Pos.CENTER);
        content.getChildren().addAll(check,input);


        mainLayout.setTop(headingLayout);
        mainLayout.setCenter(content);
        mainLayout.setBottom(buttons);


        check.setOnAction(e -> initCheckbox());
        cancelButton.setOnAction(e -> window.close());
        confirmButton.setOnAction(e -> send());

        mainLayout.setPadding(new Insets(20,20,20,20));

        Scene scene = new Scene(mainLayout);
        scene.getStylesheets().add("css/emailer.css");
        header.requestFocus();
        window.setScene(scene);
        window.showAndWait();

    }
}
