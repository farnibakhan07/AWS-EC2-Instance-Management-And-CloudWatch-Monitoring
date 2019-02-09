package com.amazonaws.samples;
/*
 * Copyright 2010-2017 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Date;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.RegionUtils;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.cloudwatch.AmazonCloudWatch;
import com.amazonaws.services.cloudwatch.AmazonCloudWatchClient;
import com.amazonaws.services.cloudwatch.AmazonCloudWatchClientBuilder;
import com.amazonaws.services.cloudwatch.model.Datapoint;
import com.amazonaws.services.cloudwatch.model.Dimension;
import com.amazonaws.services.cloudwatch.model.GetMetricStatisticsRequest;
import com.amazonaws.services.cloudwatch.model.GetMetricStatisticsResult;
import com.amazonaws.services.cloudwatch.model.ListMetricsRequest;
import com.amazonaws.services.cloudwatch.model.ListMetricsResult;
import com.amazonaws.services.cloudwatch.model.Metric;
import com.amazonaws.services.codedeploy.model.InstanceStatus;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.AuthorizeSecurityGroupIngressRequest;
import com.amazonaws.services.ec2.model.CreateKeyPairRequest;
import com.amazonaws.services.ec2.model.CreateKeyPairResult;
import com.amazonaws.services.ec2.model.CreateSecurityGroupRequest;
import com.amazonaws.services.ec2.model.CreateSecurityGroupResult;
import com.amazonaws.services.ec2.model.DescribeAvailabilityZonesResult;
import com.amazonaws.services.ec2.model.DescribeInstanceStatusRequest;
import com.amazonaws.services.ec2.model.DescribeInstanceStatusResult;
import com.amazonaws.services.ec2.model.DescribeInstancesRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.IpPermission;
import com.amazonaws.services.ec2.model.KeyPair;
import com.amazonaws.services.ec2.model.RunInstancesRequest;
import com.amazonaws.services.ec2.model.RunInstancesResult;
import com.amazonaws.services.ec2.model.StopInstancesRequest;
import com.amazonaws.services.ec2.model.Tag;
import com.amazonaws.services.ec2.model.DescribeRegionsResult;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.InstanceState;
import com.amazonaws.services.ec2.model.Region;
import com.amazonaws.services.ec2.model.Reservation;
import com.amazonaws.services.ec2.model.MonitorInstancesRequest;
import com.amazonaws.services.cloudwatch.AmazonCloudWatch;
import com.amazonaws.services.cloudwatch.AmazonCloudWatchClientBuilder;
import com.amazonaws.services.cloudwatch.model.ListMetricsRequest;
import com.amazonaws.services.cloudwatch.model.ListMetricsResult;
import com.amazonaws.services.cloudwatch.model.Metric;

public class CreateSecurityGroupApp4 {

    /*
     * Before running the code:
     *      Fill in your AWS access credentials in the provided credentials
     *      file template, and be sure to move the file to the default location
     *      (C:\\Users\\Mourin\\.aws\\credentials) where the sample code will load the
     *      credentials from.
     *      https://console.aws.amazon.com/iam/home?#security_credential
     *
     * WARNING:
     *      To avoid accidental leakage of your credentials, DO NOT keep
     *      the credentials file in your source directory.
     */

    private static CreateKeyPairResult createKeyPairResult;
	private static String privateKey;
	private static RunInstancesResult result;
	private static RunInstancesResult resultFrankfurt;
	private static Collection<String> instancesList;
	private static InstanceState state;

	public static void main(String[] args) {

        /*
         * The ProfileCredentialsProvider will return your [default]
         * credential profile by reading from the credentials file located at
         * (C:\\Users\\Mourin\\.aws\\credentials).
         */
        AWSCredentials credentials = null;
        try {
            credentials = new ProfileCredentialsProvider("default").getCredentials();
        } catch (Exception e) {
            throw new AmazonClientException(
                    "Cannot load the credentials from the credential profiles file. " +
                    "Please make sure that your credentials file is at the correct " +
                    "location (C:\\Users\\Mourin\\.aws\\credentials), and is in valid format.",
                    e);
        }

     //   Create the AmazonEC2Client object so we can call various APIs.
        AmazonEC2 ec2 = AmazonEC2ClientBuilder.standard()
           .withCredentials(new AWSStaticCredentialsProvider(credentials))
            .withRegion("us-west-2")
            .build();

        // Create a new security group.
        try {
        CreateSecurityGroupRequest csgr = new CreateSecurityGroupRequest();
        	csgr.withGroupName("FarTawSecurityGroup").withDescription("My security group");
        	CreateSecurityGroupResult createSecurityGroupResult =
        		    ec2.createSecurityGroup(csgr);
            System.out.println(String.format("Security group created: [%s]",
            		createSecurityGroupResult.getGroupId()));
        } catch (AmazonServiceException ase) {
            // Likely this means that the group is already created, so ignore.
           System.out.println(ase.getMessage());
        }

        

        // Open up port 23 for TCP traffic to the associated IP from above (e.g. ssh traffic).
     /*   IpPermission ipPermission = new IpPermission()
                .withIpProtocol("tcp")
                .withFromPort(new Integer(22))
                .withToPort(new Integer(22))
                .withIpRanges(ipRanges);

        List<IpPermission> ipPermissions = Collections.singletonList(ipPermission);*/

      /*  try {
            // Authorize the ports to the used.
        	AuthorizeSecurityGroupIngressRequest authorizeSecurityGroupIngressRequest =
        		    new AuthorizeSecurityGroupIngressRequest();

        		authorizeSecurityGroupIngressRequest.withGroupName("FarTawSecurityGroup")
        	                                    .withIpPermissions(ipPermission);
        		ec2.authorizeSecurityGroupIngress(authorizeSecurityGroupIngressRequest);
            System.out.println(String.format("Ingress port authroized: [%s]",
                    ipPermissions.toString()));
        } catch (AmazonServiceException ase) {
            // Ignore because this likely means the zone has already been authorized.
            System.out.println(ase.getMessage());
        }*/
        
        //creating key
        
       CreateKeyPairRequest createKeyPairRequest = new CreateKeyPairRequest();
        createKeyPairRequest.withKeyName("keyEc2Far");
        createKeyPairResult = ec2.createKeyPair(createKeyPairRequest);
        
        KeyPair keyPair = new KeyPair();

        keyPair = createKeyPairResult.getKeyPair();

        privateKey = keyPair.getKeyMaterial();*/
       
        //run a instance
        
        RunInstancesRequest runInstancesRequest =
        		   new RunInstancesRequest();

        		runInstancesRequest.withImageId("ami-a23fedda")
        		                   .withInstanceType("t2.micro")
        		                   .withMinCount(1)
        		                   .withMaxCount(1)
        		                   .withKeyName("keyEc2Far")
        		                   .withSecurityGroups("FarTawSecurityGroup");
        		
        		
        	result = ec2.runInstances(runInstancesRequest);*/
        	
        //list of regions
        DescribeRegionsResult regions_response = ec2.describeRegions();

        	for(Region region : regions_response.getRegions()) {
        
        	    System.out.printf(
        	        "Found region %s \n" +
        	        "with endpoint %s \n",
        	        region.getRegionName(),
        	        region.getEndpoint());
        	}
        	
        //create instance in F region
        AmazonEC2 ec2Frankfurt = AmazonEC2ClientBuilder.standard()
        	           .withCredentials(new AWSStaticCredentialsProvider(credentials))
        	            .withRegion("eu-central-1")
        	            .build();
        	
        RunInstancesRequest runInstancesRequestFrankfurt =
         		   new RunInstancesRequest();

         		runInstancesRequestFrankfurt.withImageId("ami-9e2daef1")
         		                   .withInstanceType("t2.micro")
         		                   .withMinCount(1)
         		                   .withMaxCount(1)
         		                   .withKeyName("keyEc2FarTawFrankfurt")
         		                   .withSecurityGroups("fartawFrankfurtSecurityGroup");
         	    resultFrankfurt = ec2Frankfurt.runInstances(runInstancesRequestFrankfurt);
       
       
       //checking running instance status

        DescribeInstanceStatusRequest describeInstanceRequest = new DescribeInstanceStatusRequest().withIncludeAllInstances(true);
        DescribeInstanceStatusResult describeInstanceResult = ec2.describeInstanceStatus(describeInstanceRequest);
        List<com.amazonaws.services.ec2.model.InstanceStatus> state = describeInstanceResult.getInstanceStatuses();
        int i=0;
      
        while (state.size() > i) {
        	
          if(state.get(i).getInstanceState().getName().equals("running")) {
             System.out.println("id-"+state.get(i).getInstanceId()+"\n");
             System.out.println("state-"+state.get(i).getInstanceState()+"\n");
             System.out.println("zone-"+state.get(i).getAvailabilityZone()+"\n");
             System.out.println("system status-"+state.get(i).getSystemStatus()+"\n");
            }
            i++;
       }

	
        final AmazonCloudWatch cw = AmazonCloudWatchClientBuilder
		 .standard().withCredentials(new AWSStaticCredentialsProvider(credentials))
		 .withRegion("us-west-2").build();
        		long offsetInMilliseconds = 1000 * 60 * 60 * 24;
        		GetMetricStatisticsRequest request2 = new GetMetricStatisticsRequest()
        				.withStartTime(new Date(new Date(offsetInMilliseconds).getTime() - offsetInMilliseconds))
        				.withNamespace("AWS/EC2")
        				.withPeriod(60 * 60)
        				.withDimensions(new Dimension().withName("InstanceId").withValue("i-0f341a1cf43f393bf"))
        				.withMetricName("CPUUtilization")
        				.withStatistics("Average", "Maximum")
        				.withEndTime(new Date(offsetInMilliseconds));
        				GetMetricStatisticsResult getMetricStatisticsResult = cw.getMetricStatistics(request2);
      
        				double avgCPUUtilization = 0;
        	        	List<Datapoint> dataPoint = getMetricStatisticsResult.getDatapoints();
        	        	for (Object aDataPoint : dataPoint) {
        	        		Datapoint dp = (Datapoint) aDataPoint;
        	        		avgCPUUtilization = dp.getAverage();
        	        		System.out.println("cpu-"+avgCPUUtilization);
        	        	}
     }
}
       

         	   
    
	

