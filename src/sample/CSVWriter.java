package sample;

import javafx.scene.control.Alert;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormat;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class CSVWriter
{
    private HSSFWorkbook workbook = new HSSFWorkbook();
    public static final String filename = "TimeSheetData.xls";


    //this is here because im too lazy to write 2 more lines of code in the controller class
    void masterWrite(ArrayList<employee> roster)
    {
        closeUnfinishedDays(roster);
        writeToFile(roster);
    }



    //This is an actual time machine
    private void closeDay(ArrayList<timeBlock> day)
    {
        Date prev;
        Date last;


        if(!day.isEmpty() && day.get(day.size()-1).out == null)
        {
            prev = day.get(day.size()-1).in;

            Calendar cal = Calendar.getInstance();
            cal.setTime(prev);
            cal.add(Calendar.MINUTE, -1);
            last = cal.getTime();

            day.get(day.size()-1).out = last;
            day.get(day.size()-1).end = true;
            day.get(day.size()-1).err = true;
        }
    }


    //This is the time machine's mom.
    private void closeUnfinishedDays(ArrayList<employee> roster)
    {

        for (employee aRoster : roster) {
            if (!aRoster.timeSheet.isEmpty()) {
                closeDay(aRoster.timeSheet.get(aRoster.timeSheet.size() - 1));
            }
        }

    }

    //If FileOutputStream fails here im 80% sure it will not create
    //a new data sheet like I want
    private void writeToFile(ArrayList<employee> roster)
    {


        for (employee aRoster : roster) {
            HSSFSheet currentSheet = workbook.createSheet(aRoster.ID);


            writeUserInfo(aRoster, currentSheet);
            writeTimesheet(workbook,aRoster, currentSheet,1);

        }

        try
        {
            workbook.write(new FileOutputStream(filename));
        }
        catch (IOException e)
        {
            Alert err = new Alert(Alert.AlertType.ERROR);
            err.setTitle("File Not Found");
            err.setHeaderText("IOException Thrown");
            err.setContentText("There was an error writing to \"" + filename + "\"");
            err.showAndWait();
            return;
        }

        //If we successful save, update metrics
        metrics.lastBackup = new Date();

    }

    //I hard coded here
    private void writeUserInfo(employee user, HSSFSheet curr)
    {
        if(user.lastPaid == null)
        {
            user.lastPaid = new Date();
        }

        HSSFRow row = curr.createRow(0);

        for(int i=0; i<9; i++)
        {
            row.createCell(i);
        }

        row.getCell(0).setCellValue(user.ID);
        row.getCell(0).setCellType(CellType.STRING);
        row.getCell(1).setCellValue(user.firstName);
        row.getCell(2).setCellValue(user.lastName);
        row.getCell(3).setCellValue(user.salary);
        row.getCell(4).setCellValue(user.notes);
        row.getCell(5).setCellValue(user.phone);
        row.getCell(6).setCellValue(user.email);
        row.getCell(7).setCellValue(user.active);
        row.getCell(8).setCellValue(user.lastPaid);
    }


    //This method name is a lie. We don't write anything here.
    void writeTimesheet(HSSFWorkbook workbook, employee user, HSSFSheet curr, int start)
    {
        int size = user.timeSheet.size();


        for(int i=0; i<size; i++)
        {
            curr.createRow((i+start));

            for(int j=0; j<user.timeSheet.get(i).size(); j++)
            {
                writeTimeblock(workbook,curr.getRow(i+start),user.timeSheet.get(i).get(j),j);
            }


        }
    }


    //Mitochondria is the power house of the cell.
    //this method is a lot like Mitochondria
    private void writeTimeblock(HSSFWorkbook workbook,HSSFRow row, timeBlock times, int index)
    {
        DataFormat format = workbook.createDataFormat();
        CellStyle dateStyle = workbook.createCellStyle();
        dateStyle.setDataFormat(format.getFormat("MM/dd/yy hh:mm"));


        row.createCell(index*2).setCellValue(times.in);
        row.getCell(index*2).setCellStyle(dateStyle);

        if(times.out != null)
        {
            row.createCell(index*2+1).setCellValue(times.out);
            row.getCell(index*2+1).setCellStyle(dateStyle);
        }


    }


}

