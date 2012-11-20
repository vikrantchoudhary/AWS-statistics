package com.vikrant.org;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClient;
import com.amazonaws.services.simpleemail.model.SendEmailRequest;
import com.amazonaws.services.simpleemail.model.SendEmailResult;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;


public class FnyAWSEmailService implements Runnable
{
    static Queue<SendEmailRequest> emails = new ConcurrentLinkedQueue<SendEmailRequest>();
    private boolean stopped = false;
    private static AmazonSimpleEmailServiceClient sesClient = null;


    public FnyAWSEmailService() throws IOException
    {

    	AWSCredentials credentials = AWSConfig.FnyAWSCredential();
        sesClient = new AmazonSimpleEmailServiceClient( credentials );
        System.out.println("Emails thread started" );
    }

    public void stop()
    {
        emails.notify();
        stopped = true;
        System.out.println( "Emails thread stopped" );
    }

    public static void addEmail( SendEmailRequest email )
    {
        emails.add( email );
    }

    public int getEmailsQueueSize()
    {
        return emails.size();
    }
    //not sure if we need to synchronize this ; need to refer AWS docs.

    public  String sendMail( SendEmailRequest request )
    {
        SendEmailResult result = null;
        try
        {
            result = sesClient.sendEmail( request );
            System.out.println(
                    " Mesg id is " + result.getMessageId() + ", to " + request.getDestination().getToAddresses() +
                            ", subject is " +
                            request.getMessage().getSubject() );// You should log what all emails you sent
        }
        catch ( Exception ex )
        {
            if ( ex.getCause() instanceof UnknownHostException )
            {
                System.out.println( "Amazon Email server has gone down "+ ex );
                addEmail( request );
                try
                {
                    Thread.sleep( 60000 ); // wait for 1 min
                }
                catch ( Exception exce )
                {
                }

            }
            System.out.println(
                    "Mesg id result is " + result + ", to " + request.getDestination().getToAddresses() +
                            ", subject is " + request.getMessage().getSubject() + ",body is " +
                            request.getMessage().getBody() + ",Exception is " +
                            ex );
            return ex.getMessage();
        }
        return "SUCCESS";
    }

    public void run()
    {
        while ( !stopped )
        {
            try
            {
                synchronized ( emails )
                {
                    emails.wait( 2000 );
                }

                while ( !emails.isEmpty() )
                {
                    SendEmailRequest email = emails.poll();
                    sendMail( email );
                }

            }
            catch ( Throwable e )
            {
                System.out.println( "Main thread of sending emails crashed "+ e );
            }
        }
    }


}
