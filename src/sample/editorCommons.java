package sample;

import com.jfoenix.controls.JFXDatePicker;
import javafx.collections.ObservableList;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Tab;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;


//Each individual class that initializes
//the three functions in the Edit Day
//window extend this class since
//each of them will share a lot of
//functionality. This also contains
//general support methods for the
//children classes
public class editorCommons
{

    private static employee user;
    private static TableView<day> adminTable;
    private static JFXDatePicker startDate;
    private static JFXDatePicker endDate;


    //Getters and setters for the Edit UI class to call when
    //initializing the whole window
    public static employee getUser() {return user;}

    public static void setUser(employee user) {editorCommons.user = user;}

    static void setAdminTable(TableView<day> adminTable) {editorCommons.adminTable = adminTable;}

    static JFXDatePicker getStartDate() {return startDate;}

    static void setStartDate(JFXDatePicker startDate) {editorCommons.startDate = startDate;}

    static JFXDatePicker getEndDate() {return endDate;}

    static void setEndDate(JFXDatePicker endDate) {editorCommons.endDate = endDate;}


    static DateTimeFormatter dtf = DateTimeFormatter.ofPattern("h:mm a");
    static DateTimeFormatter dtf4 = DateTimeFormatter.ofPattern("M/d/yy");
    static SimpleDateFormat dtf2 = new SimpleDateFormat("h:mm a");
    static SimpleDateFormat dtf3 = new SimpleDateFormat("M/d/yy");


    //This initializes a specified date picker to set all dates, except dates with
    //sign in data associated with them, inactive.
    static Callback<DatePicker, DateCell> getDayCellFactoryStandard(HashSet<LocalDate> nullDates) {

        final Callback<DatePicker, DateCell> dayCellFactory = new Callback<DatePicker, DateCell>() {

            @Override
            public DateCell call(final DatePicker datePicker) {
                return new DateCell() {
                    @Override
                    public void updateItem(LocalDate item, boolean empty) {
                        super.updateItem(item, empty);

                        setDisable(true);

                        if(nullDates.contains(item))
                        {
                            setDisable(false);
                        }

                    }
                };
            }
        };
        return dayCellFactory;
    }

    //Similar to the standard initializer, this method will initialize a specified date picker
    //with all dates, except dates with data associated with them, active
    private Callback<DatePicker, DateCell> getDayCellFactoryAdd(HashSet<LocalDate> filledDates) {

        final Callback<DatePicker, DateCell> dayCellFactory;
        dayCellFactory = new Callback<DatePicker, DateCell>() {

            @Override
            public DateCell call(final DatePicker datePicker) {
                return new DateCell() {
                    @Override
                    public void updateItem(LocalDate item, boolean empty) {
                        super.updateItem(item, empty);

                        if(item.isAfter(LocalDate.now()))
                        {
                            setDisable(true);
                        }
                        else
                        {
                            if(filledDates.contains(item))
                            {
                                setDisable(true);
                            }
                            else
                            {
                                setDisable(false);
                            }

                        }
                    }
                };
            }
        };
        return dayCellFactory;
    }

    //Wrapper method for the 'Add Day' date picker initializer
    void initPickerAdd(JFXDatePicker picker)
    {

        if(getUser().timeSheet.isEmpty()){return;}

        HashSet<LocalDate> filledDates = new HashSet<>();


        for(int i=0; i<getUser().timeSheet.size(); i++)
        {
            filledDates.add(tableStuff.dateToLocal(getUser().timeSheet.get(i).get(0).in));
        }

        Callback<DatePicker, DateCell> dayCellFactory= this.getDayCellFactoryAdd(filledDates);
        picker.setDayCellFactory(dayCellFactory);


    }


    //Wrapper method for the 'standard' date picker initializer
    void initPickerStandard(JFXDatePicker picker)
    {
        if(getUser().timeSheet.isEmpty()){return;}

        //get most recent day
        LocalDate recent = tableStuff.dateToLocal(getUser().timeSheet.get(getUser().timeSheet.size()-1).get(0).in);

        picker.setValue(recent);

        HashSet<LocalDate> bounds = tableStuff.getEmployeeBounds(getUser());


        Callback<DatePicker, DateCell> dayCellFactory= getDayCellFactoryStandard(bounds);
        picker.setDayCellFactory(dayCellFactory);

    }

    //Updates admin table after date edit occurs
    void updateTable()
    {
        LocalDate recent = tableStuff.dateToLocal(user.timeSheet.get(user.timeSheet.size()-1).get(0).in);

        ObservableList<day> col = tableStuff.createEmployeeTable(user, recent, recent);

        adminTable.setItems(col);
    }

    //Cancel whenever called will set the current tab to the first one in the list
    void cancel(Tab tab)
    {
        tab.getTabPane().getSelectionModel().select(0);
    }

    //This always us to close the entire Edit Day window regardless of
    //what tab we are in
    void close(Tab tab)
    {
        Stage a = (Stage)tab.getTabPane().getScene().getWindow();
        a.close();
    }


}
