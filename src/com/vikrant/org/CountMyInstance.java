package com.vikrant.org;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.DescribeAvailabilityZonesResult;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.Reservation;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.simpledb.AmazonSimpleDB;
import com.amazonaws.services.simpledb.AmazonSimpleDBClient;


public class CountMyInstance {

    static AmazonEC2      ec2;
    private static StringBuilder  message = new StringBuilder();
    //message = "FnY AWS details";
    
    private static void init() throws Exception {
        AWSCredentials credentials = AWSConfig.FnyAWSCredential();

        ec2 = new AmazonEC2Client(credentials);
        //by defaul ec2 select the us zone. if you don't want set it for other zone as..
        ec2.setEndpoint("https://ap-southeast-1.ec2.amazonaws.com");
        message.append( "<html><head><title>FnY AWS details</title></head><body>");
    }


    public static void main(String[] args) throws Exception {

        System.out.println("===========================================");
        
        System.out.println("FnY AWS details!");
        System.out.println("===========================================");
        
        
        init();
        
       
        try {
            DescribeAvailabilityZonesResult availabilityZonesResult = ec2.describeAvailabilityZones();
            System.out.println("You have access to " + availabilityZonesResult.getAvailabilityZones().size() +
                    " Availability Zones.");

            DescribeInstancesResult describeInstancesRequest = ec2.describeInstances();
            List<Reservation> reservations = describeInstancesRequest.getReservations();
            Set<Instance> instances = new HashSet<Instance>();

            for (Reservation reservation : reservations) {
                instances.addAll(reservation.getInstances());
            }
            message.append("...........Running instances ..........<br>");
            Iterator<Instance> it = instances.iterator();
            int count =0,count2=0;;
            while (it.hasNext()) {
            	Instance instance= it.next(); 
            	//if (it.next().)
            	if (instance.getState().getName().equalsIgnoreCase("running")) {
            		//System.out.println("ImageID=" + instance.getImageId() + " , InstanceID=" + instance.getInstanceId() +   
            		//		" ,virtualizationType=" + instance.getVirtualizationType() +  
            		//		" ,LaunchTime=" + instance.getLaunchTime());
            		message.append( "InstanceID=" + instance.getInstanceId() + " ,LaunchTime=" + instance.getLaunchTime() +"\n<br>");
            		if(instance.getVirtualizationType().equalsIgnoreCase("hvm")) {
            			count2++;
            		}
            		count++;
            	}
            }
            message.append("...........summary ..........<br>");
            message.append("Total instances (including stopped) =  " + instances.size()  +"\n<br>");
            System.out.println("Total instances (including stopped) =  " + instances.size());
            System.out.println("Total no of running instances =  " + count );
            message.append("Total no of running instances =  " + count +"\n<br>");
            System.out.println("Running Linux Instances = " + (count-count2));
            message.append("Running Linux Instances = " + (count-count2)+"\n<br>");
            System.out.println("Running Window Instances =  " + count2);
            message.append("Running Window Instances =  " + count2 +"\n<br>");
            message.append("=========================================== </body></html>");
            System.out.println("sending mail");
            EmailUtility.sendMail("vikrant.choudhary@fashionandyou.com", "vikrant choudhary", message.toString(),
    				"AWS Details", null, "vikrantchoudhary@gmail.com");
           
        } catch (AmazonServiceException ase) {
                System.out.println("Caught Exception: " + ase.getMessage());
                System.out.println("Reponse Status Code: " + ase.getStatusCode());
                System.out.println("Error Code: " + ase.getErrorCode());
                System.out.println("Request ID: " + ase.getRequestId());
        }

        
    }
}
