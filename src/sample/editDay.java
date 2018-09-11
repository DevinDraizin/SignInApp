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
   1. Select date to edit
   2. Select new times
   3. Add breaks (Optional)
   4. Commit change to roster
   5. update admin table
*/

class editDay extends editorCommons
{
    private JFXTabPane mainLayout;

    private BorderPane scene1Layout;
    private BorderPane scene2Layout;
    private BorderPane scene3Layout;

    private ArrayList<timeBlock> day;
    private LocalDate disassembledDay;
    private ArrayList<LocalTime> disassembledTimes;
    private Label dateLabel;
    private JFXTimePicker inTime, outTime;
    private JFXListView<String> list;
    private Label newDay;


    editDay()
    {
        this.mainLayout = new JFXTabPane();
        this.scene1Layout = new BorderPane();
        this.scene2Layout = new BorderPane();
        this.scene3Layout = new BorderPane();
        this.dateLabel = new Label();
        this.inTime = new JFXTimePicker();
        this.outTime = new JFXTimePicker();
        this.disassembledTimes = new ArrayList<>();
        this.list = new JFXListView<>();
        this.newDay = new Label();
    }


    //Make sure day is initialized and is not empty
    private void disassembleDate()
    {
        disassembledDay = tableStuff.dateToLocal(day.get(0).in);

        for (timeBlock aDay : day)
        {
            disassembledTimes.add(LocalTime.from(LocalDateTime.ofInstant(aDay.in.toInstant(), ZoneId.of("America/New_York"))));
            if(aDay.out != null)
            disassembledTimes.add(LocalTime.from(LocalDateTime.ofInstant(aDay.out.toInstant(), ZoneId.of("America/New_York"))));
        }
    }



    private void assembleDate()
    {
        day.clear();

        for(int i=0; i<(disassembledTimes.size()/2); i++)
        {
            LocalDateTime semIn = LocalDateTime.of(disassembledDay, disassembledTimes.get(2*i));
            LocalDateTime semOut = LocalDateTime.of(disassembledDay, disassembledTimes.get(2*i+1));

            Date in = Date.from(semIn.atZone(ZoneId.of("America/New_York")).toInstant());
            Date out = Date.from(semOut.atZone(ZoneId.of("America/New_York")).toInstant());

            day.add(new timeBlock(in,out));
        }

        if(!tableStuff.dateToLocal(day.get(0).in).isEqual(LocalDate.now()))
        {
            day.get(day.size()-1).end = true;
        }


    }

    //time sheet should have been a linkedHashSet....
    private void selectDay(JFXDatePicker picker)
    {
        if(picker.getValue() != null)
        {
            for(int i=0; i<getUser().timeSheet.size(); i++)
            {
                if(tableStuff.dateToLocal(getUser().timeSheet.get(i).get(0).in).isEqual(picker.getValue()))
                {
                    day = getUser().timeSheet.get(i);
                    disassembleDate();
                    initList();
                    mainLayout.getSelectionModel().select(1);
                    return;
                }
            }
        }

    }


    private void back3()
    {
        disassembledTimes.clear();
        disassembledDay = null;
        list.getItems().clear();
        mainLayout.getSelectionModel().select(0);
    }

    private void createScene1(Tab tab)
    {
        JFXDatePicker picker = new JFXDatePicker();
        picker.setEditable(false);
        JFXButton nextButton = new JFXButton("Next");
        JFXButton closeButton = new JFXButton("Close");
        HBox headings = new HBox(28);
        HBox buttons = new HBox(28);
        VBox centerLayout = new VBox(28);
        VBox labels = new VBox(10);
        Label header = new Label("Select A Date To Edit");
        Label userLabel = new Label("Current Employee:");
        Label nameLabel = new Label();

        initPickerStandard(picker);
        picker.setEditable(false);
        picker.setValue(null);


        header.getStyleClass().add("header-label");
        nameLabel.getStyleClass().add("name-label");
        userLabel.getStyleClass().add("content-label");

        nameLabel.setText(getUser().firstName + " " + getUser().lastName);
        labels.getChildren().addAll(userLabel,nameLabel);
        labels.setAlignment(Pos.CENTER);

        headings.getChildren().add(header);
        headings.setAlignment(Pos.CENTER);

        buttons.getChildren().addAll(nextButton,closeButton);
        buttons.setAlignment(Pos.CENTER);


        centerLayout.getChildren().addAll(picker,buttons);
        centerLayout.setAlignment(Pos.CENTER);

        scene1Layout.setTop(headings);
        scene1Layout.setCenter(centerLayout);
        scene1Layout.setBottom(labels);
        scene1Layout.setPadding(new Insets(40,10,40,10));


        nextButton.setOnAction(e -> selectDay(picker));
        closeButton.setOnAction(e -> close(tab));
    }

    private void initList()
    {
        if(day == null){return;}
        list.getItems().clear();

        dateLabel.setText(dtf3.format(day.get(0).in));

        list.setVisible(true);

        if(disassembledTimes.size() % 2 == 1)
        {
            for(int i=0; i<(disassembledTimes.size()/2+1); i++)
            {
                if(i == disassembledTimes.size()/2)
                {
                    list.getItems().add("IN  " + dtf.format(disassembledTimes.get(2*i)) + "       " + "OUT  TBD");
                    break;
                }

                list.getItems().add("IN  " + dtf.format(disassembledTimes.get(2*i)) + "       " + "OUT  " + dtf.format(disassembledTimes.get(2*i+1)));
            }

            return;
        }

        for(int i=0; i<disassembledTimes.size()/2; i++)
        {
            list.getItems().add("IN  " + dtf.format(disassembledTimes.get(2*i)) + "       " + "OUT  " + dtf.format(disassembledTimes.get(2*i+1)));
        }
    }

    private void selectTime()
    {
        int index = list.getSelectionModel().getSelectedIndex();

        if(list.getSelectionModel().getSelectedIndex() != -1)
        {
            inTime.setValue(disassembledTimes.get(index*2));
            if(index != disassembledTimes.size()/2)
            {
                outTime.setValue(disassembledTimes.get(index*2+1));
            }
            else
            {
                outTime.setValue(null);
            }

        }
    }

    //seems to be working now
    private void splitBlock()
    {
        int index = list.getSelectionModel().getSelectedIndex();

        if(index != -1 && inTime.getValue() != null && outTime.getValue() != null)
        {
            disassembledTimes.add(index*2+1,outTime.getValue());
            disassembledTimes.add(index*2+1,inTime.getValue());

            initList();
        }
    }

    private void removeBlock()
    {
        int index = list.getSelectionModel().getSelectedIndex();

        if(index != -1)
        {
            disassembledTimes.remove(index*2);
            disassembledTimes.remove(index*2);

            initList();
        }
    }

    private void insertBefore()
    {
        int index = list.getSelectionModel().getSelectedIndex();

        if(index != -1 && inTime.getValue() != null && outTime.getValue() != null)
        {
            disassembledTimes.add(index*2,outTime.getValue());
            disassembledTimes.add(index*2,inTime.getValue());
        }

        initList();

    }

    private void insertAfter()
    {
        int index = list.getSelectionModel().getSelectedIndex();

        if(index != -1 && inTime.getValue() != null && outTime.getValue() != null)
        {
            disassembledTimes.add((1+index)*2,outTime.getValue());
            disassembledTimes.add((1+index)*2,inTime.getValue());
        }

        initList();
    }

    private void commitListEdit()
    {
        int index = list.getSelectionModel().getSelectedIndex();

        if(list.getSelectionModel().getSelectedIndex() != -1 && inTime.getValue() != null && outTime.getValue() != null)
        {
            disassembledTimes.remove(index*2);
            disassembledTimes.add(index*2,inTime.getValue());

            if(disassembledTimes.size() % 2 == 0)
            {
                disassembledTimes.remove(index*2+1);
            }

            disassembledTimes.add(index*2+1,outTime.getValue());

            initList();
        }
    }

    private void sanEditTimes()
    {
        Alert err = new Alert(Alert.AlertType.ERROR);
        err.setTitle("Validation Error");
        err.setHeaderText("There must be at least one time block in the day");

        if(disassembledTimes.isEmpty())
        {
            err.showAndWait();
            return;
        }

        for(int i=0; i<((disassembledTimes.size()/2)); i++)
        {
            if(disassembledTimes.get(2*i).isAfter(disassembledTimes.get(2*i+1)))
            {
                err.setHeaderText("Please make sure all of the break times are in chronological order");
                err.setContentText("IN time must be before OUT time");
                err.showAndWait();
                return;
            }
        }

        for(int i=1; i<(disassembledTimes.size()-1); i+=2)
        {
            if(disassembledTimes.get(i).isAfter(disassembledTimes.get(i+1)))
            {
                err.setHeaderText("Please make sure all of the break times are in chronological order");
                err.setHeaderText("Each break must be after the previous one");
                return;
            }
        }

        createDayLabel();
        mainLayout.getSelectionModel().select(2);

    }

    private void createDayLabel()
    {
        StringBuilder concatDay = new StringBuilder();

        concatDay.append("\tDay: ").append(dtf4.format(disassembledDay)).append("\n\n\nSign In:\t\tSign Out:\n\n");

        for (int i=0; i<disassembledTimes.size()/2; i++)
        {
            concatDay.append(dtf.format(disassembledTimes.get(2*i))).append("\t\t").append(dtf.format(disassembledTimes.get(2*i+1))).append("\n");
        }

        newDay.setText(concatDay.toString());
    }

    private void finalEdit(Tab tab)
    {
        Alert conf = new Alert(Alert.AlertType.CONFIRMATION);
        conf.setTitle("Confirm Edit");
        conf.setHeaderText("Are you sure you want to edit this date?\nIt cannot be undone");

        ButtonType cancel = new ButtonType("Cancel");
        ButtonType yes = new ButtonType("Yes");
        ButtonType  no = new ButtonType("No");


        conf.getButtonTypes().setAll(yes,no,cancel);
        Optional<ButtonType> result = conf.showAndWait();

        //Do the thing
        if(result.isPresent() && result.get() == yes)
        {
            assembleDate();
            initPickerStandard(getStartDate());
            initPickerStandard(getEndDate());
            updateTable();
            close(tab);
        }

    }


    private void createScene2(Tab tab)
    {
        JFXButton nextButton = new JFXButton("Next");
        JFXButton changeButton = new JFXButton("Edit");
        JFXButton splitButton = new JFXButton("Split");
        JFXButton afterButton = new JFXButton("After");
        JFXButton beforeButton = new JFXButton("Before");
        JFXButton removeButton = new JFXButton("Remove");
        VBox headings = new VBox(40);
        VBox listBox = new VBox(20);
        HBox insertButtons = new HBox(18);
        HBox buttons = new HBox(28);
        HBox pickerBox = new HBox(28);
        VBox centerLayout = new VBox(40);

        Label header = new Label("Select A Timeblock To Edit");
        header.getStyleClass().add("header-label");
        dateLabel.getStyleClass().add("content-label");

        list.prefHeightProperty().bind(Bindings.size(list.getItems()).multiply(37.1));
        list.setMaxWidth(340);
        list.setVisible(false);

        inTime.setEditable(false);
        outTime.setEditable(false);

        changeButton.setTooltip(new Tooltip("Commit edit to selected timeblock"));
        changeButton.setStyle("-fx-font-size: 15;");
        splitButton.setTooltip(new Tooltip("Insert new timeblock between selected times"));
        splitButton.setStyle("-fx-font-size: 15;");
        afterButton.setTooltip(new Tooltip("Insert new timeblock after selected times"));
        afterButton.setStyle("-fx-font-size: 15;");
        beforeButton.setTooltip(new Tooltip("Insert new timeblock before selected times"));
        beforeButton.setStyle("-fx-font-size: 15;");
        removeButton.setTooltip(new Tooltip("Removes selected timeblock"));
        removeButton.setStyle("-fx-font-size: 15;");

        insertButtons.setAlignment(Pos.CENTER);
        insertButtons.getChildren().addAll(changeButton,beforeButton,splitButton,afterButton,removeButton);


        headings.getChildren().addAll(header,dateLabel);
        headings.setAlignment(Pos.CENTER);

        pickerBox.getChildren().addAll(inTime,outTime);
        pickerBox.setAlignment(Pos.CENTER);

        buttons.getChildren().add(nextButton);
        buttons.setAlignment(Pos.BOTTOM_CENTER);

        listBox.getChildren().addAll(insertButtons,list);
        listBox.setAlignment(Pos.CENTER);

        centerLayout.getChildren().addAll(pickerBox,listBox);
        centerLayout.setAlignment(Pos.CENTER);


        scene2Layout.setTop(headings);
        scene2Layout.setCenter(centerLayout);
        scene2Layout.setBottom(buttons);
        scene2Layout.setPadding(new Insets(40,20,40,20));

        nextButton.setOnAction(e -> sanEditTimes());
        list.setOnMouseClicked(e -> selectTime());
        changeButton.setOnAction(e -> commitListEdit());
        splitButton.setOnAction(e -> splitBlock());
        removeButton.setOnAction(e -> removeBlock());
        beforeButton.setOnAction(e -> insertBefore());
        afterButton.setOnAction(e -> insertAfter());

    }

    private void createScene3(Tab tab)
    {
        HBox buttons = new HBox(28);
        VBox headBox = new VBox(40);
        JFXButton editButton = new JFXButton("Edit Day");
        JFXButton cancelButton = new JFXButton("Start Over");
        JFXButton closeButton = new JFXButton("Close");
        Label header = new Label("Confirm Edit");
        header.getStyleClass().add("header-label");


        newDay.getStyleClass().add("header-label");
        newDay.setStyle("-fx-font-size: 18;");

        headBox.getChildren().addAll(header,newDay);
        headBox.setAlignment(Pos.CENTER);

        buttons.getChildren().addAll(editButton,cancelButton,closeButton);
        buttons.setAlignment(Pos.CENTER);

        editButton.setOnAction(e -> finalEdit(tab));
        cancelButton.setOnAction(e -> back3());
        closeButton.setOnAction(e -> close(tab));

        scene3Layout.setPadding(new Insets(40,20,40,20));

        scene3Layout.setTop(headBox);
        scene3Layout.setCenter(buttons);
    }


    void initEditTab(Tab tab)
    {
        tab.setText("Edit Date");
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
