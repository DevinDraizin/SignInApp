package sample;

import com.jfoenix.controls.JFXListView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;

import static sample.CSVManager.roster;

/*
    Support methods for transferring roster data into 'day' objects
    for tableView


    and other stuff


    This class does a lot
 */


class tableStuff
{



    //Here we want to map total hours worked with their respective dates but we also want to make
    //sure we maintain chronological order. For this we can parse the current time sheet and place
    //the hours/dates into a LinkedHashMap.
    //
    //This method is used to populate the report window in the main UI as well as to calculate
    //the grand total/salary
    static LinkedHashMap<LocalDate,Double> getReport(LocalDate start, LocalDate end, employee curr)
    {
        //Get index of start and end in curr's time sheet
        int in = getDateIndex(start,curr);
        int out = getDateIndex(end,curr);

        LinkedHashMap<LocalDate,Double> map = new LinkedHashMap<>();

        double tot;
        LocalDate temp = LocalDate.now();

        for(int i=in; i<=out; i++)
        {
            tot = 0;

            for(int j=0; j<curr.timeSheet.get(i).size(); j++)
            {
                curr.timeSheet.get(i).get(j).calcTime();
                tot += (curr.timeSheet.get(i).get(j).totalHours);
                temp = dateToLocal(curr.timeSheet.get(i).get(j).in);
            }

            map.put(temp,tot);
        }



        return map;
    }


    //The converting Date to LocalDate is helpful because if you want to
    //compare to dates only based on the day, LocalDate.compareTo() makes
    //it easier then doing the same thing with Dates.
    static LocalDate dateToLocal(Date date)
    {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }


    //This returns the index of a specific date in the time sheet Array
    //
    //Should never return -1 but if it does we will know where
    //it came from from the out of bounds exception
    static int getDateIndex(LocalDate date, employee curr)
    {

        for(int i=0; i<curr.timeSheet.size(); i++)
        {
            if(dateToLocal(curr.timeSheet.get(i).get(0).in).compareTo(date) == 0)
            {
                return i;
            }
        }

        return -1;
    }


    //This method returns a HashSet containing all of the days in
    //the employees time sheet so when we initialize date pickers
    //we can show all dates except dates that have data for them
    //as valid dates to pick.
    static HashSet<LocalDate> getEmployeeBounds(employee curr)
    {
        HashSet<LocalDate> ret = new HashSet<>();

        for(int i=0; i<curr.timeSheet.size(); i++)
        {
            ret.add(dateToLocal(curr.timeSheet.get(i).get(0).in));
        }

        return ret;
    }


    //return list bounded by dates. We can assume dates are valid since we init date pickers first
    static ObservableList<day> createEmployeeTable(employee curr, LocalDate start, LocalDate end)
    {
        ObservableList<day> table = FXCollections.observableArrayList();

        if(curr.timeSheet.isEmpty())
        {
            return null;
        }

        int in = getDateIndex(start,curr);
        int out = getDateIndex(end,curr);

        for(int i=in; i<=out; i++)
        {
            table.add(createDay(curr.timeSheet.get(i)));
        }

        return table;
    }

    //Populates the side list in the main UI according to
    //activity and preferences in Employee Activity
    static void initList(JFXListView<String> sideList)
    {
        sideList.getItems().clear();

        ObservableList<String> rosterList;

        rosterList = sideList.getItems();

        for (employee aRoster : roster) {
            if(aRoster.active == 1)
            {
                rosterList.add(aRoster.firstName + " " + aRoster.lastName);
            }
        }
    }


    //Breaks on the current day since time block may not be finished.

    //Here we take a single day from the roster and convert it to an instance of the day class
    //day is used to make dealing with breaks when we are dealing with the UI. It formats breaks
    //as a contiguous list instead of by time blocks.
    private static day createDay(ArrayList<timeBlock> day)
    {
        ArrayList<String> inBreaks = new ArrayList<>();
        ArrayList<String> outBreaks = new ArrayList<>();

        SimpleDateFormat sdf = new SimpleDateFormat("M/dd/yy h:mm a");

        day curr = new day();

        if(day.isEmpty())
        {
            return curr;
        }
        else
        {
            curr.signIn = day.get(0).in;
            if(day.get(day.size()-1).end)
            {
                curr.SignOut = day.get(day.size()-1).out;
            }

            for (timeBlock aDay : day) {
                inBreaks.add(sdf.format(aDay.in));
                if (aDay.out != null)
                    outBreaks.add(sdf.format(aDay.out));
            }

            inBreaks.remove(0);
            if(!outBreaks.isEmpty())
            {
                outBreaks.remove(outBreaks.size()-1);
            }


            curr.breakIn = inBreaks;
            curr.breakOut = outBreaks;

        }

        return curr;
    }
}
