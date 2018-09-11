package sample;

/*
HSSF only works for .xls not .xlsx which is the default in excel
*/

import javafx.scene.control.Alert;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellType;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

public class CSVManager
{


    public static HSSFWorkbook workbook;


    //This is everything
    static ArrayList<employee> roster = new ArrayList<>();



    ArrayList<String> dynamicSearch(String input)
    {
        ArrayList<String> results = new ArrayList<>();

        int len = input.length();

        for (employee aRoster : roster) {
            if (aRoster.firstName.length() >= len) {
                if (aRoster.firstName.substring(0, len).toUpperCase().compareTo(input.toUpperCase()) == 0) {
                    if(aRoster.active == 1)
                    {
                        results.add(aRoster.firstName + " " + aRoster.lastName);
                    }
                }
            }

        }

        return results;
    }


    //Roster is unsorted so this will be O(n)
    //search. If it's too slow we can sort
    //roster and implement binary search (or use hashset)
    public static employee getID(String ID)
    {
        for (employee aRoster : roster) {
            if (aRoster.ID.compareTo(ID) == 0) {
                return aRoster;
            }
        }


        return null;
    }

    public static employee getName(String name)
    {
        for (employee aRoster : roster) {
            if ((aRoster.firstName + " " + aRoster.lastName).compareTo(name) == 0) {
                return aRoster;
            }
        }

        return null;
    }



    //User is always non null here.
    ArrayList<timeBlock> getCurrentTimeSheet(employee user)
    {
        int tSize = user.timeSheet.size() - 1;
        if(tSize<1)
        {
            tSize = 0;
        }

        if(user.timeSheet.isEmpty())
        {
            user.timeSheet.add(new ArrayList<>());
            return user.timeSheet.get(user.timeSheet.size()-1);
        }

        if(user.timeSheet.get(tSize).isEmpty() || user.timeSheet.get(tSize).get(user.timeSheet.get(tSize).size() - 1).end)
        {
            if(tableStuff.dateToLocal(user.timeSheet.get(tSize).get(0).in).isEqual(LocalDate.now()))
            {
                return user.timeSheet.get(tSize);
            }
            else  //Create a new row (new day) in the timesheet and return that list;
            {
                user.timeSheet.add(new ArrayList<>());
                return user.timeSheet.get(user.timeSheet.size()-1);
            }

        }
        else //They have already signed in
        {
            return user.timeSheet.get(user.timeSheet.size()-1);
        }

    }



    //Call on startup to initialize all employee data from database
    //"CSV Reader"
    void initializeEmployeeData(String filename) throws IOException
    {

        int j, k, l, m;

        try {

            workbook = new HSSFWorkbook(new FileInputStream(filename));

        } catch (IOException e)
        {
            Alert err = new Alert(Alert.AlertType.ERROR);
            err.setTitle("File Not Found");
            err.setHeaderText("Ya dun goofed :(");
            err.setContentText("There was an error reading \"" + filename + "\"\n\nCreated new database in local directory instead");
            err.showAndWait();

            workbook = new HSSFWorkbook();
            workbook.createSheet();
            workbook.write(new FileOutputStream(filename));

            return;

        }

        for(int i=0; i<workbook.getNumberOfSheets(); i++)
        {
            HSSFSheet currentSheet = workbook.getSheetAt(i);
            roster.add(new employee());

            j=0;
            while(currentSheet.getRow(0).getCell(j) != null && currentSheet.getRow(0).getCell(j).getCellTypeEnum() != CellType.BLANK)
            {

                if (j == 0) //ID
                {
                    currentSheet.getRow(0).getCell(j).setCellType(CellType.STRING);
                    roster.get(i).ID = currentSheet.getRow(0).getCell(j).getStringCellValue();
                }
                else if (j == 1) //First Name
                {
                    roster.get(i).firstName = currentSheet.getRow(0).getCell(j).getStringCellValue();
                }
                else if (j == 2) //Last Name
                {
                    roster.get(i).lastName = currentSheet.getRow(0).getCell(j).getStringCellValue();
                }
                else if (j == 3) //Salary
                {
                    roster.get(i).salary = currentSheet.getRow(0).getCell(j).getStringCellValue();
                }
                else if(j == 4)//Notes
                {
                    roster.get(i).notes = currentSheet.getRow(0).getCell(j).getStringCellValue();
                }
                else if(j == 5)//phone number
                {
                    currentSheet.getRow(0).getCell(j).setCellType(CellType.STRING);
                    roster.get(i).phone = currentSheet.getRow(0).getCell(j).getStringCellValue();

                }
                else if(j == 6)//email
                {
                    roster.get(i).email = currentSheet.getRow(0).getCell(j).getStringCellValue();

                }
                else if(j == 7)//Activity
                {
                    roster.get(i).active = (int)currentSheet.getRow(0).getCell(j).getNumericCellValue();
                }
                else //Last Paid
                {
                    roster.get(i).lastPaid = currentSheet.getRow(0).getCell(j).getDateCellValue();
                }

                j++;

            }

            k=1;

            while(currentSheet.getRow(k) != null && currentSheet.getRow(k).getCell(0).getCellTypeEnum() != CellType.BLANK)
            {


                l=0;
                m=0;
                roster.get(i).timeSheet.add(new ArrayList<>());

                while(currentSheet.getRow(k).getCell(m) != null && currentSheet.getRow(k).getCell(m).getCellTypeEnum() != CellType.BLANK)
                {

                    roster.get(i).timeSheet.get(k-1).add(new timeBlock());
                    roster.get(i).timeSheet.get(k-1).get(l).in = currentSheet.getRow(k).getCell(m).getDateCellValue();
                    roster.get(i).timeSheet.get(k-1).get(l).out = currentSheet.getRow(k).getCell(m+1).getDateCellValue();
                    roster.get(i).timeSheet.get(k-1).get(l).calcTime();


                    if(roster.get(i).timeSheet.get(k-1).get(l).in.after(roster.get(i).timeSheet.get(k-1).get(l).out))
                    {
                        roster.get(i).timeSheet.get(k-1).get(l).err = true;
                    }

                    l++;
                    m += 2;
                }



                k++;
            }

            for (employee aRoster : roster) {
                boundRoster(aRoster);
            }

        }

        getValErr();

        Collections.sort(roster);
    }



    private void getValErr()
    {
        ArrayList<timeBlock> lastDay;
        int index;

        for (employee aRoster : roster) {
            if (!aRoster.timeSheet.isEmpty()) {
                lastDay = aRoster.timeSheet.get(aRoster.timeSheet.size() - 1);

                //if the last block in the sheet is err and the start date matches the current date
                if (lastDay.get(lastDay.size()-1).out.before(lastDay.get(lastDay.size()-1).in)) {
                    index = aRoster.timeSheet.indexOf(lastDay);
                    aRoster.timeSheet.get(index).get(aRoster.timeSheet.get(index).size()-1).out = null;
                    aRoster.timeSheet.get(index).get(aRoster.timeSheet.get(index).size()-1).end = false;
                }
            }


        }
    }


    private void boundRoster(employee user)
    {
        for(int i=0; i<user.timeSheet.size(); i++)
        {
            user.timeSheet.get(i).get(0).start = true;

            //If the last time block in each day is not today then we can set end to true.
            //Problem is we need a way to differentiate between break and sign out.
            //We can try to fix this with HSSF CellType or maybe some enum
            if(!tableStuff.dateToLocal(user.timeSheet.get(i).get(user.timeSheet.get(i).size()-1).out).isEqual(LocalDate.now()))
            {
                user.timeSheet.get(i).get(user.timeSheet.get(i).size()-1).end = true;
            }

        }
    }



    void addToSheet(ArrayList<timeBlock> day, Date curr)
    {
        if(day.isEmpty())
        {
            day.add(new timeBlock());
            day.get(0).in = curr;
            day.get(0).start = true;
            return;
        }

        if(day.get(day.size()-1).out == null)
        {
            day.get(day.size()-1).out = curr;
        }
        else
        {
            day.add(new timeBlock());
            day.get(day.size()-1).in = curr;
        }
    }





}
