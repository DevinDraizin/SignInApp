package sample;

import org.apache.poi.hssf.usermodel.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;

//This class is responsible for creating and emailing
//the individual time sheets to a user
public class writeEmployeeSheet
{
    private HSSFWorkbook workbook = new HSSFWorkbook();
    private static String filename;
    private static employee user;

    public static void setUser(employee user) {
        writeEmployeeSheet.user = user;
    }

    public static void setFilename(String filename) {
        writeEmployeeSheet.filename = filename;
    }


    //This will effectively be the body of the email
    private String getInstructions()
    {
        return "Time Sheet is formatted using the following conventions:\n\n- Each row is a new day\n" +
                "- Each cell is a In/Out time\n\n\nExample:\n" +
                "01/09/18 14:33 | 01/09/18 14:38\nSigned in at 14:33 and signed out at 14:38\n\n" +
                "01/09/18 14:33 | 01/09/18 14:38 | 01/09/18 15:01 | 01/09/18 19:06\n" +
                "Signed in at 14:33, went on break at 14:38, came back at 15:01, signed out at 19:06";
    }


    //Here we take in the employee we want to email
    //as well a their preferred email. Then we create
    //their time sheet, email it to them, and then
    //attempt to delete the file created in the local
    //directory
    void sendTimeSheet(employee emp,String email)
    {
        filename = emp.firstName + "TimeSheet.xls";

        System.out.println(email);

        user = emp;

        writeFile();
        String old = emailer.getTitle();

        emailer.setTo(email);
        emailer.setFilename(filename);
        emailer.setTitle(old + " " + "Time Sheet");
        emailer.setBody(getInstructions());
        emailer.sendBackup();

        File file = new File(filename);

        if(file.delete())
        {
            System.out.println("\nFile deleted successfully...");
        }
        else
        {
            System.out.println("\nFailed to delete the file...");
        }
    }

    //This returns the grand total of hours
    //worked by the provided employee
    Double getTotal()
    {
        LocalDate start = tableStuff.dateToLocal(user.timeSheet.get(0).get(0).in);
        LocalDate end = tableStuff.dateToLocal(user.timeSheet.get(user.timeSheet.size()-1).get(0).in);

        return Report.getTotal(tableStuff.getReport(start,end,user));
    }


    //This returns the total hours for
    //a specific day in the time sheet
    private String getSubtotal(int index)
    {
        Double tot = 0.0;

        for(int i=0; i<user.timeSheet.get(index).size(); i++)
        {
            user.timeSheet.get(index).get(i).calcTime();
            tot += user.timeSheet.get(index).get(i).totalHours;
        }

        return "Day Total: " + Math.round(tot*100.0)/100.0 + " hrs";
    }


    //Here we call the CSVWriter from the writer class
    //Since that necessarily has to write individual days
    //We also add the grand total at the top of the sheet
    //and find the longest row, skip and extra column, and
    //then write the day subtotals next to each day. This
    //will output a .xls sheet into the local directory
    private void writeFile()
    {
        try {
            FileOutputStream fileOut = new FileOutputStream(filename);
            HSSFSheet worksheet = workbook.createSheet(user.firstName+" "+user.lastName);


            CSVWriter writer = new CSVWriter();
            writer.writeTimesheet(workbook,user,worksheet,1);



            worksheet.createRow(0).createCell(0).setCellValue("Total Hours: " + getTotal() + "hrs");


            int num=0;

            for(int i=0; i<user.timeSheet.size(); i++)
            {
                if(user.timeSheet.get(i).size() > num)
                {
                    num = user.timeSheet.get(i).size();
                }
            }

            for(int i=1; i<worksheet.getPhysicalNumberOfRows(); i++)
            {
                worksheet.getRow(i).createCell((num*2)+1);
                worksheet.getRow(i).getCell((num*2)+1).setCellValue(getSubtotal(i-1));
            }

            for(int i=0; i<((num*2)+2); i++)
            {
                worksheet.autoSizeColumn(i);
            }



            workbook.write(fileOut);
            fileOut.flush();
            fileOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
