package sample;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class funFacts
{

    private static ArrayList<String> factsList = new ArrayList<>();

    static String getJoke()
    {
        int index = new Random().nextInt(factsList.size());

        return factsList.get(index);
    }



    static void readFile()
    {
        // The name of the file to open.
        String fileName = "funFacts.txt";

        // This will reference one line at a time
        String line = null;

        try {
            FileReader fileReader = new FileReader(fileName);

            BufferedReader bufferedReader = new BufferedReader(fileReader);

            while((line = bufferedReader.readLine()) != null) {
                factsList.add(line);
            }

            bufferedReader.close();
        }
        catch(FileNotFoundException ex) {
            System.out.println(
                    "Unable to open file '" +
                            fileName + "'");
        }
        catch(IOException ex) {
            System.out.println(
                    "Error reading file '"
                            + fileName + "'");

        }
    }
}
