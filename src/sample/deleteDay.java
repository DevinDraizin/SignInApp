package sample;

/*
    Delete day from roster
    Update admin table

    UI Map:
                    |-----|--------|--------|
    Welcome Label   |   Remove  Cancel    Close
        |           |     |        |        |
        v           |     v        v        v
    Date Picker     | Confirm   Go To     close UI
        |           |     |     Scene1
        v           |     v
    Menu Items -----|  Delete
*/


import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDatePicker;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.Optional;



class deleteDay extends editorCommons
{

    private BorderPane layout = new BorderPane();

    private JFXDatePicker picker = new JFXDatePicker();

    private HBox buttons = new HBox(28);
    private HBox headings = new HBox(28);

    private VBox labels = new VBox(10);
    private VBox centerLayout = new VBox(28);

    private Label userLabel = new Label("Current Employee:");
    private Label desc = new Label("Select A Date To Remove");
    private Label nameLabel = new Label();

    private JFXButton removeButton = new JFXButton("Remove");
    private JFXButton cancelButton = new JFXButton("Cancel");
    private JFXButton closeButton = new JFXButton("Close");



    private void delete(Tab tab)
    {
        Alert conf = new Alert(Alert.AlertType.CONFIRMATION);
        conf.setTitle("Confirm Delete");
        conf.setHeaderText("Are you sure you want to remove this date?\nIt cannot be undone");

        ButtonType cancel = new ButtonType("Cancel");
        ButtonType yes = new ButtonType("Yes");
        ButtonType  no = new ButtonType("No");


        conf.getButtonTypes().setAll(yes,no,cancel);
        Optional<ButtonType> result = conf.showAndWait();

        //Do the thing
        if(result.isPresent() && result.get() == yes)
        {
            getUser().timeSheet.remove(tableStuff.getDateIndex(picker.getValue(),getUser()));

            initPickerStandard(getStartDate());
            initPickerStandard(getEndDate());
            updateTable();
            close(tab);
        }
    }


    void initDelTab(Tab tab)
    {
        tab.setText("Delete Date");
        tab.setClosable(false);

        nameLabel.getStyleClass().clear();
        userLabel.getStyleClass().clear();
        nameLabel.getStyleClass().add("name-label");
        userLabel.getStyleClass().add("content-label");
        desc.getStyleClass().add("header-label");


        nameLabel.setText(getUser().firstName + " " + getUser().lastName);
        labels.getChildren().addAll(userLabel,nameLabel);
        labels.setAlignment(Pos.CENTER);

        buttons.getChildren().addAll(removeButton,cancelButton,closeButton);
        buttons.setAlignment(Pos.TOP_CENTER);

        centerLayout.getChildren().addAll(picker,buttons);
        centerLayout.setAlignment(Pos.CENTER);

        headings.getChildren().add(desc);
        headings.setAlignment(Pos.CENTER);


        picker.setEditable(false);
        initPickerStandard(picker);

        layout.setTop(headings);
        layout.setBottom(labels);
        layout.setCenter(centerLayout);
        layout.setPadding(new Insets(40,20,40,20));


        cancelButton.setOnAction(e -> cancel(tab));
        closeButton.setOnAction(e -> close(tab));
        removeButton.setOnAction(e -> delete(tab));



        tab.setContent(layout);
    }
}
