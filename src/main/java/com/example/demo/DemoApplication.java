package com.example.demo;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.InvalidMediaTypeException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.InvalidMimeTypeException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.ResponseEntity;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import java.nio.file.Path;
import java.nio.file.Paths;


@SpringBootApplication
@RestController
public class DemoApplication {


	@Autowired()
	ServletContext servletContext;

	@RequestMapping("/")
	String root(){
		return "This is the root directory"; 
	}

	@GetMapping(value = "/resources/static/**")
	public ResponseEntity<Resource> getFileWithType(HttpServletRequest request) throws IOException{
		
		//Get the uri part of the request
		String uri = request.getRequestURI(); 
		
		//Relative Path, may need some testing to ensure this works in all situations. 
		Path path = Paths.get("demo/src/main" + uri ); 
		


		try{
			Resource resource = new UrlResource(path.toUri()); 

			//Get the mime type, /category/filetype
			String mimeType = servletContext.getMimeType(resource.getFile().getAbsolutePath());
			//Parse to create a Media Type, tell Spring Boot what file type is being returned. 
			MediaType mediatype = MediaType.parseMediaType(mimeType);

			return ResponseEntity.ok()
				.contentType(mediatype)
				.body(resource); 

		//File does not exist
		}catch(InvalidMediaTypeException e){
			return ResponseEntity.status(404).body(null); 
		}
		//Unknown Error
		catch(Exception e){
			e.printStackTrace();
			return ResponseEntity.status(500).body(null);
		}
		
		
		


	}


	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

}
