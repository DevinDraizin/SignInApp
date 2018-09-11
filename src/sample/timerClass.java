package sample;


import java.util.TimerTask;


//This is the runnable class instantiated by
//the auto writer. Here we create the master
//time sheet and overwrite the current file
//in the local directory. Then we email the
//newly created file to the backup server
public class timerClass extends TimerTask
{
    @Override
    public void run()
    {
        CSVWriter writer = new CSVWriter();
        writer.masterWrite(CSVManager.roster);
        emailer.sendBackup();
        metrics.writeMetrics();
    }

}
