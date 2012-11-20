package com.vikrant.org;

import java.io.IOException;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.PropertiesCredentials;

public class AWSConfig {
	
	public static AWSCredentials FnyAWSCredential() throws IOException{
		AWSCredentials credentials = new PropertiesCredentials(
				AWSConfig.class.getResourceAsStream("../../../AwsCredentials.properties"));
		return credentials;
	}
}
