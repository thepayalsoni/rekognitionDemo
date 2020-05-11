package com.demo.faceRecognition.FaceRecognition;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@RestController
@CrossOrigin("http://localhost:4200")
@EnableWebMvc
public class Controller {

	@PostMapping(value = "/", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public String matcher(@RequestParam("sourceImage") MultipartFile source,
			@RequestParam("targetImage") MultipartFile target

	) {
		FRService fr = new FRService();
		return fr.match(source, target, 80f);
	}

	@PostMapping(value = "/searchFace", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public String searchFace(@RequestParam("sourceImage") MultipartFile source) {
		FRService fr = new FRService();
		return fr.searchForAFace(source);
	}
	
	@PostMapping(value = "/countFace", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public String countFace(@RequestParam("sourceImage") MultipartFile source) {
		FRService fr = new FRService();
		return fr.detectNumberOfFacesInImage(source);
	}

}
