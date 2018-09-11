package sample;

import javafx.scene.control.Alert;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.*;


//This class implements a method to email time sheets
//we can optionally choose to set the title, body,
//email to send to, and any attachment file IN
//THE LOCAL DIRECTORY ONLY. Emails are hard coded to
//always be from one email.
class emailer
{

    private static DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd/yy");



    private static String title = dtf.format(LocalDate.now());
    private static String body = " ";
    private static String to = "HeadsupSignIn@gmail.com";
    private static String filename = CSVWriter.filename;

    public static void setTitle(String title) {emailer.title = title;}
    public static void setBody(String body) {emailer.body = body;}

    public static String getTitle() {return title;}
    public static String getBody(){return body;}

    public static String getFilename() {return filename;}
    public static void setFilename(String filename) {emailer.filename = filename;}

    public static void setTo(String to) {emailer.to = to;}
    public static String getTo() {return to;}


    //Exception handling and all the popup windows
    //will cause exceptions to be thrown right now
    //since they are trying to run outside of the
    //main javaFX thread. We have to create a wrapper
    //contained in the FX thread to call these popups
    static void sendBackup()
    {
        System.out.println("Sending Backup...\n");

        String from = "HeadsupSignIn@gmail.com";
        String pass ="Z%76$GE750Tx";
        // Recipient's email ID needs to be mentioned.

        String host = "smtp.gmail.com";

        // Get system properties
        Properties properties = System.getProperties();
        // Setup mail server
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.user", from);
        properties.put("mail.smtp.password", pass);
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.auth", "true");

        // Get the default Session object.
        Session session = Session.getDefaultInstance(properties);

        try{
            // Create a default MimeMessage object.
            MimeMessage message = new MimeMessage(session);

            // Set From: header field of the header.
            message.setFrom(new InternetAddress(from));

            // Set To: header field of the header.
            message.addRecipient(Message.RecipientType.TO,
                    new InternetAddress(to));


            // Set Subject: header field
            message.setSubject(title);

            // Now set the actual message
            message.setText(body);

            Multipart multipart = new MimeMultipart();

            BodyPart messageBodyPart = new MimeBodyPart();

            // Gets so fussy without this
            messageBodyPart.setText(body);

            // Set text message part
            multipart.addBodyPart(messageBodyPart);

            // Part two is attachment
            messageBodyPart = new MimeBodyPart();
            String file = filename;
            DataSource source = new FileDataSource(file);
            messageBodyPart.setDataHandler(new DataHandler(source));
            messageBodyPart.setFileName(file);
            multipart.addBodyPart(messageBodyPart);

            // Send the complete message parts
            message.setContent(multipart);


            // Send message
            Transport transport = session.getTransport("smtp");
            transport.connect(host, from, pass);
            transport.sendMessage(message, message.getAllRecipients());
            transport.close();


            System.out.println("Sent message successfully....");
            Alert sendSus = new Alert(Alert.AlertType.CONFIRMATION);
            sendSus.setTitle("Success");
            sendSus.setHeaderText("Time sheet has been sent to your email");
            sendSus.showAndWait();


        } catch (MessagingException e){

            e.printStackTrace();
            Alert sendErr = new Alert(Alert.AlertType.ERROR);
            sendErr.setTitle("Sender Exception");
            sendErr.setHeaderText("There was an error while trying to send the email");
            sendErr.setContentText("Please make sure you are connected to the internet.");
            sendErr.showAndWait();
            System.out.println("Message failed to send.\n\n");
            return;
        }

        metrics.lastServerBackup = new Date();
    }


}
