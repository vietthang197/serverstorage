package com.levietthang.serverstorage.controller;

import com.google.api.client.googleapis.media.MediaHttpUploader;
import com.google.api.client.googleapis.media.MediaHttpUploaderProgressListener;
import com.google.api.client.http.AbstractInputStreamContent;
import com.google.api.client.http.InputStreamContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.levietthang.serverstorage.model.FileUploadProgressListener;
import com.levietthang.serverstorage.service.GoogleDriveService;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.management.modelmbean.ModelMBean;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;

@Controller
@EntityScan(basePackages = {"com.levietthang.serverstorage", "com.levietthang.serverstorage.controller"})
@RequestMapping("/")
public class UploadFileController implements MediaHttpUploaderProgressListener {
    long firstSize = 0;
    float result =0f;

    @GetMapping
    public String defaults() {
        result = 0;
        long firstSize = 0;
        return "upload";
    }

    @PostMapping
    @ResponseBody
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) throws IOException, GeneralSecurityException {
        Drive.Files.Create createFile = GoogleDriveService.createGoogleFileFromInputStream(null, file.getContentType(), file.getOriginalFilename(), file.getInputStream());
            firstSize = file.getSize() / 1024;
            MediaHttpUploader uploader = createFile.getMediaHttpUploader();
            uploader.setDirectUploadEnabled(false);
            uploader.setProgressListener(this);
        File  filed = null;
        try {
            filed = createFile.execute();
            return new ResponseEntity<>(filed.getWebViewLink(),HttpStatus.OK);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return  new ResponseEntity<>("Failed",HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/getPercentUploadFile")
    @ResponseBody
    public int getPercentUploadFile() {
        return (int)result;
    }

    @Override
    public void progressChanged(MediaHttpUploader mediaHttpUploader) throws IOException {
        if (mediaHttpUploader == null) return;
        switch (mediaHttpUploader.getUploadState()) {
            case INITIATION_STARTED:
                System.out.println("Initiation has starteds!");
                break;
            case INITIATION_COMPLETE:
                System.out.println("Initiation is completes!");
                break;
            case MEDIA_IN_PROGRESS:
                 result = ((float)(mediaHttpUploader.getNumBytesUploaded() / 1024)/(float) firstSize) * 100;
                break;
            case MEDIA_COMPLETE:
                System.out.println("Upload is complete!");
        }
    }
}
