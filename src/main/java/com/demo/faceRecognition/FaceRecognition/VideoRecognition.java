package com.demo.faceRecognition.FaceRecognition;

import software.amazon.awssdk.services.rekognition.model.NotificationChannel;
import software.amazon.awssdk.services.rekognition.model.S3Object;
import software.amazon.awssdk.services.rekognition.model.StartFaceDetectionRequest;
import software.amazon.awssdk.services.rekognition.model.StartFaceDetectionResponse;
import software.amazon.awssdk.services.rekognition.model.Video;

public class VideoRecognition {
	
	
/*
private static void StartFaceDetection(String bucket, String video) throws Exception{
         
    NotificationChannel channel=  NotificationChannel.builder().snsTopicArn("").roleArn("").build();
           
    
    StartFaceDetectionRequest req = StartFaceDetectionRequest.builder().video(
    		Video.builder().s3Object(S3Object.builder().bucket("").name("").build()).build()
    		).notificationChannel(channel).build()
            
                        
                        
    
    StartFaceDetectionResponse startLabelDetectionResult = rek.startFaceDetection(req);
    startJobId=startLabelDetectionResult.getJobId();
    
} 

private static void GetFaceDetectionResults() throws Exception{
    
    int maxResults=10;
    String paginationToken=null;
    GetFaceDetectionResult faceDetectionResult=null;
    
    do{
        if (faceDetectionResult !=null){
            paginationToken = faceDetectionResult.getNextToken();
        }
    
        faceDetectionResult = rek.getFaceDetection(new GetFaceDetectionRequest()
             .withJobId(startJobId)
             .withNextToken(paginationToken)
             .withMaxResults(maxResults));
    
        VideoMetadata videoMetaData=faceDetectionResult.getVideoMetadata();
            
        System.out.println("Format: " + videoMetaData.getFormat());
        System.out.println("Codec: " + videoMetaData.getCodec());
        System.out.println("Duration: " + videoMetaData.getDurationMillis());
        System.out.println("FrameRate: " + videoMetaData.getFrameRate());
            
            
        //Show faces, confidence and detection times
        List<FaceDetection> faces= faceDetectionResult.getFaces();
     
        for (FaceDetection face: faces) { 
            long seconds=face.getTimestamp()/1000;
            System.out.print("Sec: " + Long.toString(seconds) + " ");
            System.out.println(face.getFace().toString());
            System.out.println();           
        }
    } while (faceDetectionResult !=null && faceDetectionResult.getNextToken() != null);
      
        
}

*/}
