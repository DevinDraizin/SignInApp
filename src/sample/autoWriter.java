package sample;


import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


//This class creates the background thread
//that runs the auto save and auto email at
//the specified time schedule defined here
class autoWriter
{

    private static final ScheduledExecutorService autoBackup = Executors.newSingleThreadScheduledExecutor();

    static Boolean hasStarted = false;


    //We are using the scheduled executor service to execute the runnable
    //every day at 11:55 PM EST This should also correct for daylights savings
    //The runnable class is TimeClass which implements the save features
    static void startThread()
    {
        LocalDateTime localNow = LocalDateTime.now();
        ZoneId currentZone = ZoneId.of("America/New_York");
        ZonedDateTime zonedNow = ZonedDateTime.of(localNow, currentZone);
        ZonedDateTime zonedNext5 ;
        zonedNext5 = zonedNow.withHour(12).withMinute(46).withSecond(0);
        //for daylights savings
        if(zonedNow.compareTo(zonedNext5) > 0)
            zonedNext5 = zonedNext5.plusDays(1);

        Duration duration = Duration.between(zonedNow, zonedNext5);
        long initialDelay = duration.getSeconds();

        autoBackup.scheduleAtFixedRate(new timerClass(), initialDelay, 24*60*60, TimeUnit.SECONDS);
        hasStarted = true;

    }


    static void killThread()
    {
        if(hasStarted)
        {
            autoBackup.shutdown();
            hasStarted = false;
        }

    }




}
