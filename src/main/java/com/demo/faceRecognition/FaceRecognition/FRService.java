package com.demo.faceRecognition.FaceRecognition;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyStore.Entry.Attribute;
import java.util.List;
import java.util.stream.Stream;

import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.rekognition.RekognitionClient;
import software.amazon.awssdk.services.rekognition.model.BoundingBox;
import software.amazon.awssdk.services.rekognition.model.CompareFacesMatch;
import software.amazon.awssdk.services.rekognition.model.CompareFacesRequest;
import software.amazon.awssdk.services.rekognition.model.CompareFacesResponse;
import software.amazon.awssdk.services.rekognition.model.ComparedFace;
import software.amazon.awssdk.services.rekognition.model.CreateCollectionRequest;
import software.amazon.awssdk.services.rekognition.model.CreateCollectionResponse;
import software.amazon.awssdk.services.rekognition.model.DetectFacesRequest;
import software.amazon.awssdk.services.rekognition.model.DetectFacesResponse;
import software.amazon.awssdk.services.rekognition.model.Face;
import software.amazon.awssdk.services.rekognition.model.FaceDetail;
import software.amazon.awssdk.services.rekognition.model.FaceMatch;
import software.amazon.awssdk.services.rekognition.model.FaceRecord;
import software.amazon.awssdk.services.rekognition.model.Image;
import software.amazon.awssdk.services.rekognition.model.IndexFacesRequest;
import software.amazon.awssdk.services.rekognition.model.IndexFacesResponse;
import software.amazon.awssdk.services.rekognition.model.ListFacesRequest;
import software.amazon.awssdk.services.rekognition.model.ListFacesResponse;
import software.amazon.awssdk.services.rekognition.model.QualityFilter;
import software.amazon.awssdk.services.rekognition.model.Reason;
import software.amazon.awssdk.services.rekognition.model.RekognitionException;
import software.amazon.awssdk.services.rekognition.model.S3Object;
import software.amazon.awssdk.services.rekognition.model.SearchFacesByImageRequest;
import software.amazon.awssdk.services.rekognition.model.SearchFacesByImageResponse;
import software.amazon.awssdk.services.rekognition.model.UnindexedFace;
import software.amazon.awssdk.utils.IoUtils;

public class FRService {

	public void createCollection() {
		RekognitionClient rekognitionClient = RekognitionClient.builder().region(Region.AP_SOUTHEAST_1).build();

		String collectionId = "ClientImagesCollection";
		System.out.println("Creating collection: " + collectionId);

		CreateCollectionRequest request = CreateCollectionRequest.builder().collectionId(collectionId).build();

		CreateCollectionResponse createCollectionResult = rekognitionClient.createCollection(request);
		System.out.println("CollectionArn : " + createCollectionResult.collectionArn());
		System.out.println("Status code : " + createCollectionResult.statusCode().toString());

		try (Stream<Path> paths = Files.walk(Paths.get("C://Users//Payal//Downloads//Cand_Images//reg"))) {
			paths.filter(Files::isRegularFile)

					.forEach(s -> addImagesToCollection(rekognitionClient, collectionId, s.getFileName().toString()));
		} catch (IOException e) {

		}

	}

	public String detectNumberOfFacesInImage(MultipartFile sourceImage) {
		RekognitionClient rekognitionClient = RekognitionClient.builder().region(Region.AP_SOUTHEAST_1).build();

		ByteBuffer sourceImageBytes = null;
		try (InputStream inputStream = sourceImage.getInputStream()) {
			sourceImageBytes = ByteBuffer.wrap(IoUtils.toByteArray(inputStream));
		} catch (Exception e) {
			System.out.println("Failed to load source image " + sourceImage);
			System.exit(1);
		}

		Image source = Image.builder().bytes(SdkBytes.fromByteBuffer(sourceImageBytes)).build();

		DetectFacesRequest request = DetectFacesRequest.builder().image(source)
				.attributes(software.amazon.awssdk.services.rekognition.model.Attribute.ALL).build();

		int count = 0;

		try {
			DetectFacesResponse result = rekognitionClient.detectFaces(request);
			List<FaceDetail> faceDetails = result.faceDetails();

			for (FaceDetail face : faceDetails) {

				count++;

				ObjectMapper objectMapper = new ObjectMapper();
				objectMapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);

				System.out.println(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(face));
			}

		} catch (RekognitionException e) {
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}

		return count + "  faces detected in photo.";
	}

	public static void main(String[] args) {

		FRService fr = new FRService();

		fr.listFacesInCollection();

	}

	private void listFacesInCollection() {
		RekognitionClient rekognitionClient = RekognitionClient.builder().region(Region.AP_SOUTHEAST_1).build();

		ObjectMapper objectMapper = new ObjectMapper();

		ListFacesResponse listFacesResult = null;
		System.out.println("Faces in collection " + "ClientImagesCollection");

		String paginationToken = null;
		do {
			if (listFacesResult != null) {
				paginationToken = listFacesResult.nextToken();
			}

			ListFacesRequest listFacesRequest = ListFacesRequest.builder().collectionId("ClientImagesCollection")
					.maxResults(1).nextToken(paginationToken).build();

			listFacesResult = rekognitionClient.listFaces(listFacesRequest);
			List<Face> faces = listFacesResult.faces();
			for (Face face : faces) {
				try {
					System.out.println(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(face));
				} catch (JsonProcessingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} while (listFacesResult != null && listFacesResult.nextToken() != null);
	}

	private void addImagesToCollection(RekognitionClient rekognitionClient, String collectionId, String photo) {
		Image image = Image.builder().s3Object(S3Object.builder().bucket("registrationimages001").name(photo).build())
				.build();

		IndexFacesRequest indexFacesRequest = IndexFacesRequest.builder().image(image).qualityFilter(QualityFilter.AUTO)
				.maxFaces(1).collectionId(collectionId).externalImageId(photo).detectionAttributesWithStrings("DEFAULT")
				.build();

		IndexFacesResponse indexFacesResult = rekognitionClient.indexFaces(indexFacesRequest);

		System.out.println("Results for " + photo);
		System.out.println("Faces indexed:");
		List<FaceRecord> faceRecords = indexFacesResult.faceRecords();
		for (FaceRecord faceRecord : faceRecords) {
			System.out.println("  Face ID: " + faceRecord.face().faceId());
			System.out.println("  Location:" + faceRecord.faceDetail().boundingBox().toString());
		}

		List<UnindexedFace> unindexedFaces = indexFacesResult.unindexedFaces();
		System.out.println("Faces not indexed:");
		for (UnindexedFace unindexedFace : unindexedFaces) {
			System.out.println("  Location:" + unindexedFace.faceDetail().boundingBox().toString());
			System.out.println("  Reasons:");
			for (Reason reason : unindexedFace.reasons()) {
				System.out.println("   " + reason.name());
			}
		}
	}

	public String match(MultipartFile sourceImage, MultipartFile targetImage, Float similarityThreshold) {

		ByteBuffer sourceImageBytes = null;
		ByteBuffer targetImageBytes = null;

		RekognitionClient rekognitionClient = RekognitionClient.builder().region(Region.AP_SOUTHEAST_1).build();

		// Load source and target images and create input parameters
		try (InputStream inputStream = sourceImage.getInputStream()) {
			sourceImageBytes = ByteBuffer.wrap(IoUtils.toByteArray(inputStream));
		} catch (Exception e) {
			System.out.println("Failed to load source image " + sourceImage);
			System.exit(1);
		}
		try (InputStream inputStream = targetImage.getInputStream()) {
			targetImageBytes = ByteBuffer.wrap(IoUtils.toByteArray(inputStream));
		} catch (Exception e) {
			System.out.println("Failed to load target images: " + targetImage);
			System.exit(1);
		}

		Image source = Image.builder().bytes(SdkBytes.fromByteBuffer(sourceImageBytes)).build();

		Image target = Image.builder().bytes(SdkBytes.fromByteBuffer(targetImageBytes)).build();

		CompareFacesRequest request = CompareFacesRequest.builder().sourceImage(source).targetImage(target)
				.similarityThreshold(similarityThreshold).build();

		// Call operation
		CompareFacesResponse compareFacesResult = rekognitionClient.compareFaces(request);

		// Display results

		StringBuilder builder = new StringBuilder();
		List<CompareFacesMatch> faceDetails = compareFacesResult.faceMatches();

		for (CompareFacesMatch match : faceDetails) {
			ComparedFace face = match.face();
			BoundingBox position = face.boundingBox();
			builder.append("Face at " + position.left().toString() + " " + position.top() + " matches with "
					+ match.similarity().toString() + "% confidence.\n");

		}

		List<ComparedFace> uncompared = compareFacesResult.unmatchedFaces();

		builder.append("There was " + uncompared.size() + " face(s) that did not match \n");

		return builder.toString();

	}

	public String searchForAFace(MultipartFile sourceImage) {
		RekognitionClient rekognitionClient = RekognitionClient.builder().region(Region.AP_SOUTHEAST_1).build();

		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);

		// Get an image object from S3 bucket.
		ByteBuffer sourceImageBytes = null;

		try (InputStream inputStream = sourceImage.getInputStream()) {
			sourceImageBytes = ByteBuffer.wrap(IoUtils.toByteArray(inputStream));
		} catch (Exception e) {
			System.out.println("Failed to load source image " + sourceImage);
			System.exit(1);
		}

		Image source = Image.builder().bytes(SdkBytes.fromByteBuffer(sourceImageBytes)).build();

		// Search collection for faces similar to the largest face in the image.
		SearchFacesByImageRequest searchFacesByImageRequest = SearchFacesByImageRequest.builder()
				.collectionId("ClientImagesCollection").image(source).faceMatchThreshold(90f).maxFaces(2).build();

		SearchFacesByImageResponse searchFacesByImageResult = rekognitionClient
				.searchFacesByImage(searchFacesByImageRequest);

		System.out.println("Faces matching largest face in image from iamge");
		List<FaceMatch> faceImageMatches = searchFacesByImageResult.faceMatches();
		StringBuilder builder = new StringBuilder();
		for (FaceMatch face : faceImageMatches) {
			try {
				builder.append((objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(face)) + "/n");
			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		return builder.toString();
	}
}