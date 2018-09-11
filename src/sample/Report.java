package sample;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDatePicker;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;



class Report
{

    private static DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd/yy");


    static Double getTotal(LinkedHashMap<LocalDate,Double> map)
    {
        Double total = 0.0;

        ArrayList<Double> hours = new ArrayList<>(map.values());

        for (Double hour : hours) {
            total += hour;
        }

        return Math.round(total*100.0)/100.0;
    }


    private static ArrayList<Integer> getErrDates(employee user,LocalDate start, LocalDate end)
    {
        int a = tableStuff.getDateIndex(start,user);
        int b = tableStuff.getDateIndex(end,user);


        ArrayList<Integer> list = new ArrayList<>();


        for(int i=a; i<=b; i++)
        {
            if(user.timeSheet.get(i).get(user.timeSheet.get(i).size()-1).err)
            {
                list.add(i-a);
            }
        }

        return list;
    }

    private static void initPicker(JFXDatePicker picker,employee user)
    {
        picker.setEditable(false);

        if(user.lastPaid != null)
        {
            picker.setValue(tableStuff.dateToLocal(user.lastPaid));
        }
    }

    private static void setPaidDate(LocalDate newDate, Label paidLabel, employee user)
    {
        user.lastPaid = Date.from(newDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        paidLabel.setText(editorCommons.dtf4.format(tableStuff.dateToLocal(user.lastPaid)));
    }


    static void getReport(LocalDate start, LocalDate end, employee user, LinkedHashMap<LocalDate,Double> map)
    {

        ArrayList<Integer> errDates = getErrDates(user,start,end);
        JFXDatePicker lastPaidDate = new JFXDatePicker();
        JFXButton setPaidDateButton = new JFXButton("Set Paid Date");
        initPicker(lastPaidDate,user);
        Label paidLabel = new Label("Last Paid Date: ");
        Label paidDateLabel = new Label(editorCommons.dtf4.format(tableStuff.dateToLocal(user.lastPaid)));
        paidDateLabel.getStyleClass().add("data-label");
        HBox labelBox = new HBox(0);
        labelBox.setAlignment(Pos.CENTER);
        labelBox.getChildren().addAll(paidLabel,paidDateLabel);
        paidLabel.getStyleClass().add("reg-label");

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setPadding(new Insets(10,10,10,10));
        grid.setVgap(10);
        grid.setHgap(38);

        grid.add(labelBox,0,0);
        grid.add(lastPaidDate,0,1);
        grid.add(setPaidDateButton,1,1);

        if(!errDates.isEmpty())
        {
            Alert err = new Alert(Alert.AlertType.WARNING);
            err.setTitle("Report Error");
            err.setHeaderText("");
            err.setContentText("There may be incomplete data in the timesheet that could skew the report calculations");
            err.showAndWait();
        }


        Stage window = new Stage();

        window.setTitle("Employee Report");
        window.initModality(Modality.APPLICATION_MODAL);
        window.setWidth(580);
        window.setHeight(640);

        Double total = getTotal(map);
        Double userSalary = Double.parseDouble(user.salary);

        Label userInfo = new Label("Employee: " + user.firstName + " " + user.lastName + "\nSalary: $"+user.salary);
        Label totalDesc = new Label("Final Statistics:");
        userInfo.getStyleClass().add("reg-label");
        totalDesc.getStyleClass().add("reg-label");

        Label totalHours = new Label("Grand Total Hours: " + total + " hrs");
        totalHours.getStyleClass().add("desc-label");

        Label totalSalary = new Label("Grand Total Salary: $" + Math.round((total*userSalary)*100.0)/100.0);
        totalSalary.getStyleClass().add("desc-label");

        Label dates = new Label("From: " + start.format(dtf) + "\t\t" + "To: " + end.format(dtf));
        dates.getStyleClass().add("desc-label");


        JFXButton close = new JFXButton("Close");


        close.setOnAction(e -> window.close());


        ArrayList<Label> days = new ArrayList<>();

        map.forEach((key,value) ->
        {
            Label a = new Label(key.format(dtf) + ":\t Total Hours: " + Math.round(value*100.0)/100.0
                    + " hrs\t\t\tTotal Salary: $" + Math.round((value*userSalary)*100.0)/100.0);
            a.getStyleClass().add("desc-label");
            days.add(a);
        });


        for (Integer errDate : errDates) {
            days.get(errDate).setStyle("-fx-text-fill: yellow;");
        }

        setPaidDateButton.setOnAction(e -> setPaidDate(lastPaidDate.getValue(),paidDateLabel,user));


        VBox mainLayout = new VBox(40);
        VBox daysLayout = new VBox(10);
        VBox grandTotal = new VBox(6);
        VBox dayContainer = new VBox(6);
        VBox totalContainer = new VBox(1);
        VBox buttons = new VBox(20);
        ScrollPane dayScroll = new ScrollPane();
        ScrollPane grand = new ScrollPane();


        mainLayout.getStylesheets().add("css/report.css");
        dayScroll.setMaxHeight(150);
        grand.setMinHeight(60);

        dayScroll.getStylesheets().add("css/scroll.css");
        grand.getStylesheets().add("css/scroll.css");


        for (Label day : days) {
            daysLayout.getChildren().add(day);
        }
        buttons.getChildren().addAll(grid,close);
        buttons.setAlignment(Pos.CENTER);


        dayScroll.setContent(daysLayout);
        dayContainer.getChildren().addAll(dates,dayScroll);
        grandTotal.getChildren().addAll(totalHours,totalSalary);
        grand.setContent(grandTotal);
        totalContainer.getChildren().addAll(totalDesc,grand);



        mainLayout.getChildren().addAll(userInfo,dayContainer,totalContainer,buttons);

        Scene scene = new Scene(mainLayout);
        window.setScene(scene);

        userInfo.requestFocus();

        window.showAndWait();

    }
}
