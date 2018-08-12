package com.levietthang.serverstorage.model;

import com.google.api.client.googleapis.media.MediaHttpUploader;
import com.google.api.client.googleapis.media.MediaHttpUploaderProgressListener;

import javax.servlet.http.HttpSession;
import java.io.IOException;

public class FileUploadProgressListener implements MediaHttpUploaderProgressListener {

    private String mFileUploadedName;

    public FileUploadProgressListener(String fileName) {
        mFileUploadedName = fileName;
    }


    @Override
    public void progressChanged(MediaHttpUploader mediaHttpUploader) throws IOException {
        if (mediaHttpUploader == null) return;
        switch (mediaHttpUploader.getUploadState()) {
            case INITIATION_STARTED:
                System.out.println("Initiation has started!");
                break;
            case INITIATION_COMPLETE:
                System.out.println("Initiation is complete!");
                break;
            case MEDIA_IN_PROGRESS:
                long percent = mediaHttpUploader.getNumBytesUploaded() / 1024;
                System.out.println("======== : " + percent);
                break;
            case MEDIA_COMPLETE:
                System.out.println("Upload is complete!");
        }
    }

}
