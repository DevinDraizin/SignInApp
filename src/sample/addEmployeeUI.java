package sample;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextField;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.Optional;


class addEmployeeUI
{

    private JFXTextField IDField = new JFXTextField();
    private JFXTextField firstField = new JFXTextField();
    private JFXTextField lastField = new JFXTextField();
    private JFXTextField phoneField = new JFXTextField();
    private JFXTextField salaryField = new JFXTextField();
    private JFXTextField emailField = new JFXTextField();
    private JFXButton addButton = new JFXButton("Add Employee");
    private JFXButton cancelButton = new JFXButton("Cancel");
    private Label title = new Label("Add Employee");

    private VBox fieldBox = new VBox(18);
    private HBox titleBox = new HBox(28);
    private HBox buttonBox = new HBox(28);



    private void initFields()
    {
        IDField.setPromptText("4 Digit ID");
        firstField.setPromptText("First Name");
        lastField.setPromptText("Last Name");
        phoneField.setPromptText("Phone Number");
        salaryField.setPromptText("Salary");
        emailField.setPromptText("Email");
    }


    private void getEmp(BorderPane curr, BorderPane prev,JFXListView<String> sideList)
    {
        employee em = new employee();

        String a,b,c,d,e,f;

        a = IDField.getText();
        b = firstField.getText();
        c = lastField.getText();
        d = salaryField.getText();
        e = phoneField.getText();
        f = emailField.getText();

        em.notes = "";

        if(a.isEmpty() || b.isEmpty() || c.isEmpty() || d.isEmpty() || e.isEmpty() || f.isEmpty())
        {
            Alert err = new Alert(Alert.AlertType.WARNING);
            err.setTitle("Incomplete Data");
            err.setContentText("");
            err.setHeaderText("One or more of the fields are incomplete");
            err.showAndWait();
            return;
        }

        if(addRemoveEmployee.testInput(a,b,c,d,e,f))
        {
            em.setID(a);
            em.setFirstName(b);
            em.setLastName(c);
            em.setSalary(d);
            em.setPhone(e);
            em.setEmail(f);
            em.active = 1;


            Alert conf = new Alert(Alert.AlertType.CONFIRMATION);
            conf.setTitle("Confirm Add");
            conf.setContentText("");
            conf.setHeaderText("Are you sure you want to add \"" + b + "\" to the database?");

            ButtonType cancelButton = new ButtonType("Cancel");
            ButtonType add = new ButtonType("Yes");
            ButtonType  quit = new ButtonType("No");


            conf.getButtonTypes().setAll(add,quit,cancelButton);
            Optional<ButtonType> result = conf.showAndWait();


            if (result.isPresent() && result.get() == add)
            {

                addRemoveEmployee.addEmp(em);

                Alert added = new Alert(Alert.AlertType.CONFIRMATION);
                added.setTitle("Success!");
                added.setContentText("");
                added.setHeaderText("Successfully Added " + b + " To The Database");
                added.showAndWait();

                sideList.getItems().add(em.firstName + " " + em.lastName);
                addRemoveEmployee.cancel(curr,prev);
            }


        }


    }

    void createAddScene(BorderPane lastLayout, AnchorPane root,JFXListView<String> sideList)
    {
        BorderPane mainLayout = new BorderPane();
        root.getChildren().add(mainLayout);
        mainLayout.setMinHeight(root.getHeight());
        mainLayout.setMinWidth(root.getWidth());

        initFields();


        fieldBox.getChildren().addAll(IDField,firstField,lastField,phoneField,salaryField,emailField);
        fieldBox.setAlignment(Pos.TOP_CENTER);
        fieldBox.setFillWidth(false);

        titleBox.getChildren().addAll(title);
        titleBox.setAlignment(Pos.CENTER);
        titleBox.setPadding(new Insets(48,0,100,0));

        buttonBox.getChildren().addAll(addButton,cancelButton);
        buttonBox.setAlignment(Pos.TOP_CENTER);
        buttonBox.setPadding(new Insets(0,0,200,0));




        mainLayout.setTop(titleBox);
        mainLayout.setCenter(fieldBox);
        mainLayout.setBottom(buttonBox);


        //ugh
        mainLayout.getStyleClass().add("border-pane");
        mainLayout.getStylesheets().add("css/add_rem_style.css");

        cancelButton.setOnAction(e -> addRemoveEmployee.cancel(mainLayout,lastLayout));
        addButton.setOnAction(e -> getEmp(mainLayout,lastLayout,sideList));


        mainLayout.setVisible(true);
        lastLayout.setVisible(false);

    }
}
