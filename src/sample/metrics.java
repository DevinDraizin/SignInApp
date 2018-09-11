package sample;

import java.io.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;

public class metrics
{
    private static final String filename = "metrics.txt";
    private static final String NULL = "NULL";
    static Date lastBackup;
    static Date lastServerBackup;

    private static SimpleDateFormat formatter = new SimpleDateFormat("M/d/yy h:mm a");
    private static DateTimeFormatter df2 = DateTimeFormatter.ofPattern("M/d/yy");



    static String getLastSBackup()
    {
        if(metrics.lastServerBackup == null)
        {
            return "Last Backup Unknown";
        }

        return formatter.format(metrics.lastServerBackup);
    }

    static String getLastBackup()
    {
        if(metrics.lastBackup == null)
        {
            return "Last Backup Unknown";
        }
        return formatter.format(metrics.lastBackup);
    }

    static String  getCurrentSignedIn()
    {
        int num=0,size;

        for(int i=0; i<CSVManager.roster.size(); i++)
        {
            if(CSVManager.roster.get(i).timeSheet == null) {continue;}

            size = CSVManager.roster.get(i).timeSheet.size();

            if(size == 0) {continue;}

            if(tableStuff.dateToLocal(CSVManager.roster.get(i).timeSheet.get(size-1).get(0).in).equals(LocalDate.now()))
            {
                if(!CSVManager.roster.get(i).timeSheet.get(size-1).get(0).end)
                {
                    num++;
                }
            }
        }

        return String.valueOf(num);
    }

    static void readMetrics()
    {
        ArrayList<String> input = new ArrayList<>();
        DateFormat df = new SimpleDateFormat("E MMM dd hh:mm:ss Z yyyy");


        // This will reference one line at a time
        String line;

        try {
            FileReader fileReader = new FileReader(filename);

            BufferedReader bufferedReader = new BufferedReader(fileReader);

            while((line = bufferedReader.readLine()) != null) {
                input.add(line);
            }

            if(input.size() == 0)
            {
                return;
            }

            if(input.get(0).compareTo(NULL) == 0)
            {
                lastBackup = null;
            }
            else
            {
                lastBackup = df.parse(input.get(0));
            }

            if(input.get(1).compareTo(NULL) == 0)
            {
                lastServerBackup = null;
            }
            else
            {
                lastServerBackup = df.parse(input.get(1));
            }


            bufferedReader.close();
        }
        catch(FileNotFoundException ex) {
            System.out.println(
                    "Unable to open file '" +
                            filename + "'");
        }
        catch(IOException ex) {
            System.out.println(
                    "Error reading file '"
                            + filename + "'");

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    static void writeMetrics()
    {

        try {
            FileWriter writer = new FileWriter(filename);
            BufferedWriter bufferedWriter = new BufferedWriter(writer);

            if(lastBackup == null)
            {
                bufferedWriter.write(NULL);
            }
            else
            {
                bufferedWriter.write(lastBackup.toString());
            }

            bufferedWriter.newLine();

            if(lastServerBackup == null)
            {
                bufferedWriter.write(NULL);
            }
            else
            {
                bufferedWriter.write(lastServerBackup.toString());
            }

            bufferedWriter.newLine();


            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
