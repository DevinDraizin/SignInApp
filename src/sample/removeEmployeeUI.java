package sample;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXListView;
import javafx.collections.ObservableList;
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


public class removeEmployeeUI
{
    private JFXButton closeButton = new JFXButton("Cancel");

    private void initSelector(JFXComboBox<String> selector)
    {
        ObservableList<String> list = selector.getItems();

        for(int i=0; i<CSVManager.roster.size(); i++)
        {
            list.add(CSVManager.roster.get(i).firstName + " " + CSVManager.roster.get(i).lastName);
        }
    }

    private void remove(JFXComboBox<String> selector, JFXListView<String> sideList)
    {
        if(selector.getSelectionModel().getSelectedItem() == null){return;}

        employee selected = CSVManager.getName(selector.getSelectionModel().getSelectedItem());
        if(selected == null){return;}


        Alert conf = new Alert(Alert.AlertType.CONFIRMATION);
        conf.setTitle("Confirm Add");
        conf.setContentText("");
        conf.setHeaderText("Are you sure you want to remove \"" + selected.firstName + " " + selected.lastName + "\" from the database?");

        ButtonType cancelButton = new ButtonType("Cancel");
        ButtonType rem = new ButtonType("Yes");
        ButtonType  quit = new ButtonType("No");


        conf.getButtonTypes().setAll(rem,quit,cancelButton);
        Optional<ButtonType> result = conf.showAndWait();


        if (result.isPresent() && result.get() == rem)
        {
            addRemoveEmployee.remEmp(selected,sideList );
            closeButton.fire();
        }



    }


    void createRemScene(BorderPane lastLayout, AnchorPane root, JFXListView<String> sideList)
    {
        BorderPane mainLayout = new BorderPane();
        root.getChildren().add(mainLayout);
        mainLayout.setMinHeight(root.getHeight());
        mainLayout.setMinWidth(root.getWidth());
        mainLayout.setPadding(new Insets(28,28,28,28));

        JFXComboBox<String> selector = new JFXComboBox<>();
        selector.setPromptText("Select Employee");
        Label header = new Label("Remove Employee");
        JFXButton remButton = new JFXButton("Remove");

        initSelector(selector);

        HBox headerBox = new HBox();
        headerBox.setAlignment(Pos.CENTER);
        headerBox.getChildren().add(header);

        HBox buttons = new HBox(18);
        buttons.setAlignment(Pos.CENTER);
        buttons.getChildren().addAll(remButton,closeButton);

        VBox content = new VBox(160);
        content.setAlignment(Pos.CENTER);
        content.getChildren().addAll(selector,buttons);

        mainLayout.setTop(headerBox);
        mainLayout.setCenter(content);

        mainLayout.getStyleClass().add("border-pane");
        mainLayout.getStylesheets().add("css/add_rem_style.css");

        closeButton.requestFocus();

        closeButton.setOnAction(e -> addRemoveEmployee.cancel(mainLayout,lastLayout));
        remButton.setOnAction(e -> remove(selector,sideList));

        mainLayout.setVisible(true);
        lastLayout.setVisible(false);
    }


}
