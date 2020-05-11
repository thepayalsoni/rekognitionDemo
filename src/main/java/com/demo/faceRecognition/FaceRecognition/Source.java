package com.demo.faceRecognition.FaceRecognition;

import org.springframework.web.multipart.MultipartFile;

public class Source {

	MultipartFile sourceImage;
	MultipartFile targetImage;
	Float similarityThreshold;

	public MultipartFile getSourceImage() {
		return sourceImage;
	}

	public void setSourceImage(MultipartFile sourceImage) {
		this.sourceImage = sourceImage;
	}

	public MultipartFile getTargetImage() {
		return targetImage;
	}

	public void setTargetImage(MultipartFile targetImage) {
		this.targetImage = targetImage;
	}

	public Float getSimilarityThreshold() {
		return similarityThreshold;
	}

	public void setSimilarityThreshold(Float similarityThreshold) {
		this.similarityThreshold = similarityThreshold;
	}

}
