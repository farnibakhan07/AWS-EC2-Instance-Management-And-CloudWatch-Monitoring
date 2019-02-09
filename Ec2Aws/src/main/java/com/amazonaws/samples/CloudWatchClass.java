package com.amazonaws.samples;


import java.util.Date;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.cloudwatch.AmazonCloudWatch;
import com.amazonaws.services.cloudwatch.AmazonCloudWatchClient;
import com.amazonaws.services.cloudwatch.AmazonCloudWatchClientBuilder;
import com.amazonaws.services.cloudwatch.model.Datapoint;
import com.amazonaws.services.cloudwatch.model.Dimension;
import com.amazonaws.services.cloudwatch.model.GetMetricStatisticsRequest;
import com.amazonaws.services.cloudwatch.model.GetMetricStatisticsResult;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;

public class CloudWatchClass {

    public static void main(String[] args) {
        
        final String instanceId = "i-0f341a1cf43f393bf";
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
       
        AmazonEC2 ec2 = AmazonEC2ClientBuilder.standard()
	            .withCredentials(new AWSStaticCredentialsProvider(credentials))
	            .withRegion("us-west-2")
	            .build();
        
        
        

        final AmazonCloudWatch cw = AmazonCloudWatchClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(credentials)).withRegion("us-west-2").build();
        		
        
        
        final GetMetricStatisticsRequest request = request(instanceId); 
        final GetMetricStatisticsResult result = result(cw, request);
        toStdOut(result, instanceId); 
        
    }


    private static GetMetricStatisticsRequest request(final String instanceId) {
        final long twentyFourHrs = 1000 * 60 * 60 * 24;
        final int oneHour = 60 * 60;
        return new GetMetricStatisticsRequest()
            .withStartTime(new Date(new Date().getTime()-twentyFourHrs))
            .withNamespace("AWS/EC2")
            .withPeriod(oneHour)
            .withDimensions(new Dimension().withName("InstanceId").withValue(instanceId))
            .withMetricName("CPUUtilization")
            .withStatistics("Average", "Maximum")
            .withEndTime(new Date());
    }

    private static GetMetricStatisticsResult result(
            final AmazonCloudWatch cw, final GetMetricStatisticsRequest request) {
         return cw.getMetricStatistics(request);
    }

    private static void toStdOut(final GetMetricStatisticsResult result, final String instanceId) {
       // System.out.println(result); // outputs empty result: {Label: CPUUtilization,Datapoints: []}
        for (final Datapoint dataPoint : result.getDatapoints()) {
        	System.out.printf("time stamp : %s%n", dataPoint.getTimestamp());
            System.out.printf("%s instance's average CPU utilization : %s%n", instanceId, dataPoint.getAverage());      
            System.out.printf("%s instance's max CPU utilization : %s%n", instanceId, dataPoint.getMaximum());
        }
    }
}