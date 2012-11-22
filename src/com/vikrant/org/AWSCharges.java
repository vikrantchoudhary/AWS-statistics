package com.vikrant.org;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.DescribeAvailabilityZonesResult;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.InstanceType;
import com.amazonaws.services.ec2.model.Reservation;


public class AWSCharges
{
    public static class InstanceRate
    {
        private int normalizedHours;
        private double emrRate;

        public InstanceRate(int normalizedHours, double emrRate)
        {
            super();
            this.normalizedHours = normalizedHours;
            this.emrRate = emrRate;
        }

        /**
         * @return the normalizedHours
         */
        public int getNormalizedHours()
        {
            return normalizedHours;
        }

        /**
         * @return the emrRate
         */
        public double getEmrRate()
        {
            return emrRate;
        }
    };

    static final Map<InstanceType, InstanceRate> rateMap =
        new HashMap<InstanceType, AWSCharges.InstanceRate>();
    static AmazonEC2      ec2;

    static
    {

        rateMap.put(InstanceType.T1Micro, new InstanceRate(1, 0.02 ));
        rateMap.put(InstanceType.M1Small, new InstanceRate(2, 0.085 ));
        rateMap.put(InstanceType.C1Medium, new InstanceRate(3, 0.186 ));
        rateMap.put(InstanceType.M1Medium, new InstanceRate(4, 0.17 ));
        rateMap.put(InstanceType.M1Large, new InstanceRate(5, 0.34 ));
        rateMap.put(InstanceType.M1Xlarge, new InstanceRate(6, 0.68 ));
        rateMap.put(InstanceType.C1Xlarge, new InstanceRate(7, 0.744));
        rateMap.put(InstanceType.M22xlarge, new InstanceRate(8, 1.012));
        rateMap.put(InstanceType.M24xlarge, new InstanceRate(9, 2.024));
        rateMap.put(InstanceType.Cc14xlarge, new InstanceRate(10, 1.60));
        rateMap.put(InstanceType.Cg14xlarge, new InstanceRate(11, 2.10));
    }

    private static void init()
        throws Exception
    {
        
        AWSCredentials credentials = AWSConfig.FnyAWSCredential();
        ec2 = new AmazonEC2Client(credentials);
        ec2.setEndpoint("https://ap-southeast-1.ec2.amazonaws.com");
    }

    public static void main(String[] args)
        throws Exception
    {
        
        init();
        DescribeAvailabilityZonesResult availabilityZonesResult = ec2.describeAvailabilityZones();
        DescribeInstancesResult describeInstancesRequest = ec2.describeInstances();
        List<Reservation> reservations = describeInstancesRequest.getReservations();
        Set<Instance> instances = new HashSet<Instance>();
        int count =0;
        double instanceHours =0.0;
        double charge =0.0;
        for (Reservation reservation : reservations) {
        	int n= reservation.getInstances().size();
        	List<Instance> inL = reservation.getInstances();
        	Iterator<Instance> itr = inL.iterator();
        	while (itr.hasNext()) {
        		Instance instance= itr.next(); 
            	if (instance.getState().getName().equalsIgnoreCase("running")) {
            		System.out.print("...." + instance.getInstanceType());
            		count++;
            		instances.add(instance);
            		instanceHours = getInstanceHours(instance.getLaunchTime(),instance.getInstanceType());
            		System.out.println(" .... charges= " + getInstanceCharge(instance.getInstanceType(), instanceHours));
                    charge += getInstanceCharge(instance.getInstanceType(), instanceHours);
            	}
        	}
        }
        System.out.println("============================= ");
        System.out.println("EC2 charges ");
        System.out.println("Total number of instances(running only) = " + count);
        System.out.println("EC2 cost for these instance = " + charge);
        
    }

    /**
     * @param rate
     * @param instanceHours
     * @return
     */
    public static double getInstanceCharge(String instanceType, double instanceHours)
    {
        InstanceRate rate = rateMap.get(InstanceType.fromValue(instanceType));
        return instanceHours * rate.getEmrRate();
    }

    /**
     * @param detail
     * @param rate
     * @return
     */
    public static double getInstanceHours(Date launchTime, String instanceType)
    {
        InstanceRate rate = rateMap.get(InstanceType.fromValue(instanceType));
        double instanceHours =
        		((((new java.util.Date()).getTime())-launchTime.getTime())/(60 * 60 * 1000)) / rate.getNormalizedHours();
        return instanceHours;
    }
}
