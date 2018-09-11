package sample;

import com.jfoenix.controls.JFXListView;
import javafx.scene.control.Alert;
import javafx.scene.layout.BorderPane;

import java.util.regex.Pattern;


//Support class for add/removing employee as well as sanitation


class addRemoveEmployee
{

    static void addEmp(employee add)
    {
        CSVManager.roster.add(add);
        CSVWriter write = new CSVWriter();
        write.masterWrite(CSVManager.roster);
    }

    static void remEmp(employee rem,JFXListView<String> sideList)
    {
        sideList.getItems().remove(rem.firstName+" "+rem.lastName);
        CSVManager.roster.remove(rem);
    }

    static void cancel(BorderPane curr, BorderPane prev)
    {
        curr.setVisible(false);
        prev.setVisible(true);
    }

    static Boolean testInput(String ID, String first, String last, String salary, String phone, String email)
    {
        Boolean a,b,c,d,e,f;

        a = sanID(ID);
        b = sanFirst(first);
        c = sanLast(last);
        d = sanSalary(salary);
        e = sanPhone(phone);
        f = sanEmail(email);

        return a && b && c && d && e && f;

    }


    static Boolean sanID(String ID)
    {

        if((ID.matches("[0-9]+") && (ID.length() == 4)))
        {

            if(CSVManager.getID(ID) != null)
            {
                Alert err = new Alert(Alert.AlertType.ERROR);
                err.setTitle("Validation Error");
                err.setHeaderText("");
                err.setContentText("There is already an employee with that ID");
                err.showAndWait();
                return false;
            }

            return true;


        }
        else
        {
            Alert err = new Alert(Alert.AlertType.ERROR);
            err.setTitle("Validation Error");
            err.setHeaderText("");
            err.setContentText("Employee ID must be 4 digits in length and contain only numbers");
            err.showAndWait();
            return false;


        }

    }


    static Boolean sanFirst(String first)
    {

        if(first.length() == 0)
        {
            Alert err = new Alert(Alert.AlertType.ERROR);
            err.setTitle("Validation Error");
            err.setHeaderText("");
            err.setContentText("You must enter a new name or select cancel");
            err.showAndWait();
            return false;
        }

       return true;
    }



    static Boolean sanLast(String last)
    {

        if(last.length() == 0)
        {
            Alert err = new Alert(Alert.AlertType.ERROR);
            err.setTitle("Validation Error");
            err.setHeaderText("");
            err.setContentText("You must enter a new name or select cancel");
            err.showAndWait();
            return false;
        }

        return true;
    }



    static Boolean sanSalary(String salary)
    {

        if((salary.matches("[0-9]*\\.?[0-9]?[0-9]?") && (salary.length() > 0)))
        {
            return true;
        }
        else
        {
            Alert err = new Alert(Alert.AlertType.ERROR);
            err.setTitle("Validation Error");
            err.setHeaderText("");
            err.setContentText("Employee salary must contain only numbers and cannot be empty or negative");
            err.showAndWait();
            return false;
        }
    }



    static Boolean sanPhone(String phone)
    {

        if((phone.matches("\\d{10}") && (phone.length() > 0)))
        {
            return true;
        }
        else
        {
            Alert err = new Alert(Alert.AlertType.ERROR);
            err.setTitle("Validation Error");
            err.setHeaderText("");
            err.setContentText("Employee Phone Number must contain exactly 10 numbers and cannot be empty or negative");
            err.showAndWait();
            return false;
        }
    }



    static Boolean sanEmail(String email)
    {
        String EMAIL_PATTERN =
                "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                        + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        Pattern pattern = Pattern.compile(EMAIL_PATTERN);


        if((email.matches(pattern.toString()) && (email.length() > 0)))
        {
            return true;
        }
        else
        {
            Alert err = new Alert(Alert.AlertType.ERROR);
            err.setTitle("Validation Error");
            err.setHeaderText("");
            err.setContentText("Email must be valid");
            err.showAndWait();
            return false;
        }
    }



}
