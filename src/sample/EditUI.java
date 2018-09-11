package sample;


import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXTabPane;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TableView;
import javafx.stage.Modality;
import javafx.stage.Stage;

//This will be the main window containing the
//'Edit Day' 'Delete Day' and 'Add Day'
//Functionality. We handle each function
//by creating a tabbed pane where each
//tab corresponds to a function.
class EditUI
{

    //These are the classes responsible for creating
    //and implementing the individual tabs
    private editDay dayEditor = new editDay();
    private addDay dayAdder = new addDay();
    private deleteDay dayDelete = new deleteDay();



    //Here is the creation of the main UI called directly from the main admin UI.
    //we pass in current user, the actual table and date picker components
    //so we can update them later on
    void createUI(employee user, TableView<day> adminTable, JFXDatePicker startDate, JFXDatePicker endDate)
    {
        if(user.timeSheet.isEmpty()){return;}

        Stage window = new Stage();
        window.setTitle("Timesheet Editor");
        window.setHeight(700);
        window.setWidth(700);
        window.initModality(Modality.APPLICATION_MODAL);

        JFXTabPane mainLayout = new JFXTabPane();

        Tab editTab = new Tab();
        Tab addTab = new Tab();
        Tab delTab = new Tab();


        //Setting the data we pass in
        //to the parent class since all
        //the classes that create the tabs
        //extend editorCommons
        editorCommons.setUser(user);
        editorCommons.setAdminTable(adminTable);
        editorCommons.setStartDate(startDate);
        editorCommons.setEndDate(endDate);

        //Now that we initialized editorCommons
        //we can create all of the tabs.
        //We pass a reference to the tab
        //and set the layout in the other classes
        dayEditor.initEditTab(editTab);
        dayAdder.initAddTab(addTab);
        dayDelete.initDelTab(delTab);


        //Action listeners to resize the whole Edit UI when we switch tabs.
        //Right now we will keep all of them the same size
        mainLayout.getSelectionModel().selectedItemProperty().addListener((ov, oldTab, newTab) -> {
            if(mainLayout.getSelectionModel().getSelectedIndex() == 0)
            {
                window.setHeight(700);
                window.setWidth(700);
                window.centerOnScreen();
            }
            else if(mainLayout.getSelectionModel().getSelectedIndex() == 1)
            {
                window.setHeight(700);
                window.setWidth(700);
                window.centerOnScreen();
            }
            else if(mainLayout.getSelectionModel().getSelectedIndex() == 2)
            {
                window.setHeight(700);
                window.setWidth(700);
                window.centerOnScreen();
            }
        });


        mainLayout.getTabs().addAll(editTab,addTab,delTab);


        Scene rootScene = new Scene(mainLayout);

        rootScene.getStylesheets().add("css/editDay.css");
        window.setScene(rootScene);
        window.showAndWait();

    }
}
