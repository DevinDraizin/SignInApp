package sample;

import com.jfoenix.controls.*;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;

/*
    UI Function
   * Create new day
   * Sanitize
   * Update roster
   * Update admin table

   UI Map:
   Scene 1 -> Choose starting date to insert into (Sanitized through DatePicker)
   Scene 2 -> Choose sign in time and sign out time. (some functionality to add breaks)
   Scene 3 -> Result message. Menu (Add Another Date, Close, Done)


*/

class addDay extends editorCommons
{

    private JFXTabPane mainLayout;

    private BorderPane scene1Layout;
    private BorderPane scene2Layout;
    private BorderPane scene3Layout;


    private LocalDate selectedDay;
    private ArrayList<timeBlock> day;
    private ArrayList<LocalTime> breaks;
    private Label newDay;

    //Constructor to initialize UI components
    addDay()
    {
        this.day = new ArrayList<>();
        this.breaks = new ArrayList<>();
        this.mainLayout = new JFXTabPane();
        this.scene1Layout = new BorderPane();
        this.scene2Layout = new BorderPane();
        this.scene3Layout = new BorderPane();
        this.newDay = new Label();
    }



    private void add(Tab tab)
    {
        LocalDate insert;
        int index=0;

        day.get(day.size()-1).end = true;

        Alert conf = new Alert(Alert.AlertType.CONFIRMATION);
        conf.setTitle("Confirm Add");
        conf.setHeaderText("Are you sure you want to add this date?");

        ButtonType cancel = new ButtonType("Cancel");
        ButtonType yes = new ButtonType("Yes");
        ButtonType  no = new ButtonType("No");


        conf.getButtonTypes().setAll(yes,no,cancel);
        Optional<ButtonType> result = conf.showAndWait();

        //Do the thing
        if(result.isPresent() && result.get() == yes)
        {
            //If the new day is the first day in the timesheet
            if(tableStuff.dateToLocal(getUser().timeSheet.get(0).get(0).in).isAfter(tableStuff.dateToLocal(day.get(0).in)))
            {
                getUser().timeSheet.add(index, day);
                initPickerStandard(getStartDate());
                initPickerStandard(getEndDate());
                close(tab);
                return;
            }
            //If the new day is after the last day in the time sheet
            else if(tableStuff.dateToLocal(getUser().timeSheet.get(getUser().timeSheet.size()-1).get(0).in).isBefore(tableStuff.dateToLocal(day.get(0).in)))
            {
                getUser().timeSheet.add(day);
                initPickerStandard(getStartDate());
                initPickerStandard(getEndDate());
                updateTable();
                close(tab);
                return;
            }


            insert = tableStuff.dateToLocal(day.get(0).in);

            //Better insertion algorithm
            //insert in middle
            for(int i=0; i<getUser().timeSheet.size(); i++)
            {
                if(insert.isBefore(tableStuff.dateToLocal(getUser().timeSheet.get(i).get(0).in)))
                {
                    index = i;
                    break;
                }
            }

            getUser().timeSheet.add(index, day);
            initPickerStandard(getStartDate());
            initPickerStandard(getEndDate());
            close(tab);

        }
    }

    private void sanSelectedDay(JFXDatePicker picker)
    {
        selectedDay = picker.getValue();

        if (selectedDay != null) {
            mainLayout.getSelectionModel().select(1);
        }
    }



    private void createScene1(Tab tab)
    {
        JFXDatePicker picker = new JFXDatePicker();
        picker.setEditable(false);
        JFXButton nextButton = new JFXButton("Next");
        JFXButton closeButton = new JFXButton("Close");
        HBox headings = new HBox(28);
        HBox buttons = new HBox(28);
        VBox centerLayout = new VBox(40);
        VBox labels = new VBox(10);
        Label header = new Label("Select A Date To Add");
        Label userLabel = new Label("Current Employee:");
        Label nameLabel = new Label();

        initPickerAdd(picker);
        picker.setEditable(false);


        header.getStyleClass().add("header-label");
        nameLabel.getStyleClass().add("name-label");
        userLabel.getStyleClass().add("content-label");

        nameLabel.setText(getUser().firstName + " " + getUser().lastName);
        labels.getChildren().addAll(userLabel,nameLabel);
        labels.setAlignment(Pos.CENTER);

        headings.getChildren().add(header);
        headings.setAlignment(Pos.CENTER);

        buttons.getChildren().addAll(nextButton,closeButton);
        buttons.setAlignment(Pos.BOTTOM_CENTER);


        centerLayout.getChildren().addAll(picker,buttons);
        centerLayout.setAlignment(Pos.CENTER);

        scene1Layout.setTop(headings);
        scene1Layout.setCenter(centerLayout);
        scene1Layout.setBottom(labels);
        scene1Layout.setPadding(new Insets(40,10,40,10));


        nextButton.setOnAction(e -> sanSelectedDay(picker));
        closeButton.setOnAction(e -> close(tab));

    }

    private void addRemTimes(JFXListView list, JFXTimePicker inTime, JFXTimePicker outTime, int mode)
    {
        if(mode == 1)//add
        {
            if(inTime.getValue() != null && outTime.getValue() != null)
            {
                int index = list.getSelectionModel().getSelectedIndex();
                if(index == -1)
                {
                    list.getItems().add("IN:  " + inTime.getValue().format(dtf) + "        " + "OUT:  " + outTime.getValue().format(dtf));
                }
                else
                {
                    list.getItems().add(index+1,"IN:  " + inTime.getValue().format(dtf) + "        " + "OUT:  " + outTime.getValue().format(dtf));
                }

                breaks.add(inTime.getValue());
                breaks.add(outTime.getValue());
            }
        }
        else
        {
            if(inTime.getValue() != null && outTime.getValue() != null)
            {
                list.getItems().remove(list.getSelectionModel().getSelectedItem());
                breaks.remove(inTime.getValue());
                breaks.remove(outTime.getValue());
            }
        }

        if(list.getItems().isEmpty())
        {
            list.setVisible(false);
        }
        else
        {
            list.setVisible(true);
        }
    }

    private void toggleBreaks(JFXCheckBox checkBox, JFXListView<String> list, JFXButton add, JFXButton rem)
    {
        if(checkBox.isSelected())
        {
            if(!list.getItems().isEmpty())
            list.setVisible(true);
            add.setVisible(true);
            rem.setVisible(true);
        }
        else
        {

            list.setVisible(false);
            add.setVisible(false);
            rem.setVisible(false);
        }
    }

    private void sanSelectedTimes(JFXCheckBox checkBox, JFXTimePicker inTime, JFXTimePicker outTime)
    {
        Alert err = new Alert(Alert.AlertType.ERROR);
        err.setTitle("Validation Error");
        err.setHeaderText("Please make sure all of the break times are in chronological order");
        err.setContentText("IN time must be before OUT time");


        if(checkBox.isSelected())
        {
            if(breaks.isEmpty())
            {
                return;
            }

            //Checks each timeblock for internal order
            for(int i=0; i<((breaks.size()/2)); i++)
            {
                if(breaks.get(2*i).isAfter(breaks.get(2*i+1)))
                {
                    err.showAndWait();
                    return;
                }
            }

            //checks that each timeblock is before the next
            for(int i=1; i<(breaks.size()-1); i+=2)
            {
                if(breaks.get(i).isAfter(breaks.get(i+1)))
                {
                    err.setContentText("Each break must be after the previous one");
                    err.showAndWait();
                    return;
                }
            }

            for(int i=0; i<(breaks.size()/2); i++)
            {

                LocalDateTime semIn = LocalDateTime.of(selectedDay, breaks.get(2*i));
                LocalDateTime semOut = LocalDateTime.of(selectedDay, breaks.get(2*i+1));

                Date in = Date.from(semIn.atZone(ZoneId.of("America/New_York")).toInstant());
                Date out = Date.from(semOut.atZone(ZoneId.of("America/New_York")).toInstant());

                day.add(new timeBlock(in,out));
            }
        }
        else
        {
            if(inTime.getValue() == null)
            {
                return;
            }

            if(outTime.getValue() == null)
            {
                if(selectedDay.isEqual(LocalDate.now()))
                {
                    //if the selected day is today we can
                    //set the out value to whatever we usually
                    //set it to when we don't have a sign out
                    //value yet
                }
                else
                {
                    return;
                }
            }



            if(inTime.getValue().isAfter(outTime.getValue()))
            {
                err.showAndWait();
                return;
            }

            LocalDateTime semIn = LocalDateTime.of(selectedDay, inTime.getValue());
            LocalDateTime semOut = LocalDateTime.of(selectedDay, outTime.getValue());

            Date in = Date.from(semIn.atZone(ZoneId.of("America/New_York")).toInstant());
            Date out = Date.from(semOut.atZone(ZoneId.of("America/New_York")).toInstant());


            day.add(new timeBlock(in,out));

        }

        createDayLabel();
        mainLayout.getSelectionModel().select(2);
    }

    private void back1()
    {
        selectedDay = null;
        mainLayout.getSelectionModel().select(0);
    }


    private void back3()
    {
        selectedDay = null;
        breaks.clear();
        day.clear();
        mainLayout.getSelectionModel().select(0);
    }

    private void createScene2(Tab tab)
    {
        JFXButton nextButton = new JFXButton("Next");
        JFXButton backButton = new JFXButton("Go Back");
        JFXButton addButton = new JFXButton("Add");
        JFXButton removeButton = new JFXButton("Remove");

        JFXTimePicker inTime = new JFXTimePicker();
        inTime.setEditable(false);
        JFXTimePicker outTime = new JFXTimePicker();
        outTime.setEditable(false);

        JFXListView<String> list = new JFXListView<>();
        JFXCheckBox checkBox = new JFXCheckBox();

        HBox buttons = new HBox(28);
        HBox heading = new HBox(28);
        HBox listButtons = new HBox(28);
        HBox pickers = new HBox(56);
        VBox centerLayout = new VBox(56);
        VBox listBox = new VBox(20);
        VBox inBox = new VBox(28);
        VBox outBox = new VBox(28);

        Label header = new Label("Select Sign In & Sign Out Times");
        header.getStyleClass().add("header-label");

        Label inLabel = new Label("Time In");
        Label outLabel = new Label("Time Out");
        inLabel.getStyleClass().add("name-label");
        outLabel.getStyleClass().add("name-label");

        inTime.setEditable(false);
        outTime.setEditable(false);

        list.setVisible(false);
        addButton.setVisible(false);
        removeButton.setVisible(false);
        checkBox.setText("Add Breaks");

        list.prefHeightProperty().bind(Bindings.size(list.getItems()).multiply(37.1));
        list.setMaxWidth(340);


        listButtons.getChildren().addAll(addButton,removeButton);
        listButtons.setAlignment(Pos.CENTER);

        listBox.getChildren().addAll(listButtons,list,checkBox);
        listBox.setAlignment(Pos.CENTER);

        inBox.getChildren().addAll(inLabel,inTime);
        inBox.setAlignment(Pos.CENTER);
        outBox.getChildren().addAll(outLabel,outTime);
        outBox.setAlignment(Pos.CENTER);

        pickers.getChildren().addAll(inBox,outBox);
        pickers.setAlignment(Pos.CENTER);

        heading.getChildren().add(header);
        heading.setAlignment(Pos.CENTER);

        buttons.setAlignment(Pos.BOTTOM_CENTER);
        buttons.getChildren().addAll(backButton,nextButton);

        centerLayout.getChildren().addAll(pickers,listBox,buttons);
        centerLayout.setAlignment(Pos.CENTER);


        scene2Layout.setTop(heading);
        scene2Layout.setCenter(centerLayout);
        scene2Layout.setPadding(new Insets(40,10,40,10));

        backButton.setOnAction(e -> back1());
        nextButton.setOnAction(e -> sanSelectedTimes(checkBox,inTime,outTime));
        addButton.setOnAction(event -> addRemTimes(list,inTime,outTime,1));
        removeButton.setOnAction(e -> addRemTimes(list,inTime,outTime,2));
        checkBox.setOnAction(e -> toggleBreaks(checkBox,list,addButton,removeButton));

    }

    private void createDayLabel()
    {
        StringBuilder concatDay = new StringBuilder();

        concatDay.append("\tDay: ").append(dtf3.format(day.get(0).in)).append("\n\n\nSign In:\t\tSign Out:\n\n");

        if(day.size() == 1)
        {
            concatDay.append(dtf2.format(day.get(0).in)).append("\t\t").append(dtf2.format(day.get(0).out));
        }
        else
        {
            for (timeBlock aDay : day) {
                concatDay.append(dtf2.format(aDay.in)).append("\t\t").append(dtf2.format(aDay.out)).append("\n");
            }
        }

        newDay.setText(concatDay.toString());
    }

    private void createScene3(Tab tab)
    {
        HBox buttons = new HBox(28);
        VBox headBox = new VBox(40);
        JFXButton addButton = new JFXButton("Add Day");
        JFXButton cancelButton = new JFXButton("Start Over");
        JFXButton closeButton = new JFXButton("Close");
        Label header = new Label("Add To Database?");
        header.getStyleClass().add("header-label");


        newDay.getStyleClass().add("header-label");
        newDay.setStyle("-fx-font-size: 18;");

        headBox.getChildren().addAll(header,newDay);
        headBox.setAlignment(Pos.CENTER);

        buttons.getChildren().addAll(addButton,cancelButton,closeButton);
        buttons.setAlignment(Pos.CENTER);

        addButton.setOnAction(e -> add(tab));
        cancelButton.setOnAction(e -> back3());
        closeButton.setOnAction(e -> close(tab));

        scene3Layout.setPadding(new Insets(40,20,40,20));

        scene3Layout.setTop(headBox);
        scene3Layout.setCenter(buttons);
    }



    void initAddTab(Tab tab)
    {
        tab.setText("Add Date");
        tab.setClosable(false);

        createScene1(tab);
        createScene2(tab);
        createScene3(tab);

        Tab scene1 = new Tab();
        scene1.setContent(scene1Layout);
        scene1.setClosable(false);

        Tab scene2 = new Tab();
        scene2.setContent(scene2Layout);
        scene2.setClosable(false);

        Tab scene3 = new Tab();
        scene3.setContent(scene3Layout);
        scene3.setClosable(false);

        mainLayout.getTabs().addAll(scene1,scene2,scene3);
        mainLayout.setStyle("-fx-tab-max-height: 0;");



        tab.setContent(mainLayout);
    }
}
