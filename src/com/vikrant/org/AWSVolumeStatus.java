package com.vikrant.org;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.DescribeVolumeStatusRequest;
import com.amazonaws.services.ec2.model.DescribeVolumeStatusResult;
import com.amazonaws.services.ec2.model.VolumeDetail;
import com.amazonaws.services.ec2.model.VolumeStatusItem;

public class AWSVolumeStatus {
	
	static AmazonEC2Client      ec2;
    //message = "FnY AWS details";
    
    private static void init() throws Exception {
        AWSCredentials credentials = AWSConfig.FnyAWSCredential();

        ec2 = new AmazonEC2Client(credentials);
        ec2.setEndpoint("https://ap-southeast-1.ec2.amazonaws.com");
    }
	
	//AmazonEC2Client ec2 = new AmazonEC2Client(myAwsCredentials);
    public static void main(String args[]) throws Exception {
		// retrieve status for a specific volume, by volume id
		//System.out.println(ec2.describeVolumeStatus(
		//		new DescribeVolumeStatusRequest().withVolumesIds(myVolumeId));
    	init();
		DescribeVolumeStatusRequest request = new DescribeVolumeStatusRequest();
		DescribeVolumeStatusResult  result  = null;
		int count=0;
		do {
		    result = ec2.describeVolumeStatus(request);
		    request.setNextToken(result.getNextToken());
		    
		    for (VolumeStatusItem status : result.getVolumeStatuses()) {
		        System.out.println(".... " + status.getVolumeId());
		    	//System.out.println(ec2.describeVolumeStatus(
		    	//			new DescribeVolumeStatusRequest().withVolumeIds(status.getVolumeId())));
		        count++;
		    }
		} while (result.getNextToken() != null);
		System.out.println("=====================");
		System.out.println("total EBS unit=" + count);
	   }
	
}