package com.levietthang.serverstorage.service;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.media.MediaHttpUploader;
import com.google.api.client.http.AbstractInputStreamContent;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.InputStreamContent;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resources;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.FileHandler;

public class GoogleDriveService {

    public static Logger logger = LoggerFactory.getLogger(GoogleDriveService.class);

    private static final String APPLICATION_NAME = "Google Drive API Java Quickstart";

    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    // Directory to store user credentials for this application.
    private static final java.io.File CREDENTIALS_FOLDER //
            = new java.io.File("asus", "credentials");

    private static final String CLIENT_SECRET_FILE_NAME =
            "client_secret_1004552806313-pecc9adfeomorgltujgcearm9c5rrjq0.apps.googleusercontent.com.json";
    //
    // Global instance of the scopes required by this quickstart. If modifying these
    // scopes, delete your previously saved credentials/ folder.
    //
    private static final List<String> SCOPES = Collections.singletonList(DriveScopes.DRIVE);

    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {

        java.io.File clientSecretFilePath = new java.io.File(CREDENTIALS_FOLDER, CLIENT_SECRET_FILE_NAME);

        // Load client secrets.
        InputStream in = new FileInputStream(clientSecretFilePath);

        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY,
                clientSecrets, SCOPES).setDataStoreFactory(new FileDataStoreFactory(CREDENTIALS_FOLDER))
                .setAccessType("offline").build();

        return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
    }

    public static Drive  getDriveService() throws IOException, GeneralSecurityException {

        System.out.println("CREDENTIALS_FOLDER: " + CREDENTIALS_FOLDER.getAbsolutePath());
        logger.debug("This is mother fucker");

        // 1: Create CREDENTIALS_FOLDER
        if (!CREDENTIALS_FOLDER.exists()) {
            CREDENTIALS_FOLDER.mkdirs();
            java.io.File file = new ClassPathResource(CLIENT_SECRET_FILE_NAME).getFile();
            FileUtils.copyFileToDirectory(file, CREDENTIALS_FOLDER);
            logger.debug("Created Folder: " + CREDENTIALS_FOLDER.getAbsolutePath());
            logger.debug("Copy file " + CLIENT_SECRET_FILE_NAME + " into folder above.. and rerun this class!!");
        }

        // 2: Build a new authorized API client service.
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();

        // 3: Read client_secret.json file & create Credential object.
        Credential credential = getCredentials(HTTP_TRANSPORT);

        // 5: Create Google Drive Service.
        Drive service = new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential) //
                .setApplicationName(APPLICATION_NAME).build();


       return service;


    }

    public static void createFolder(Drive driveService, String folderName) throws IOException {
        File fileMetadata = new File();
        fileMetadata.setName(folderName);
        fileMetadata.setMimeType("application/vnd.google-apps.folder");

        File file = driveService.files().create(fileMetadata)
                .setFields("id")
                .execute();
        logger.debug("Folder ID: " + file.getId());
    }

    public static Drive.Files.Create createGoogleFileFromInputStream(String googleFolderIdParent, String contentType, String customFileName, InputStream inputStream) throws IOException, GeneralSecurityException {
        AbstractInputStreamContent uploadStreamContent = new InputStreamContent(contentType, inputStream);
        return _createGoogleFile(googleFolderIdParent, contentType, customFileName, uploadStreamContent);
    }

    public static Drive.Files.Create _createGoogleFile(String googleFolderIdParent, String contentType, String customFileName, AbstractInputStreamContent uploadStreamContent) throws IOException, GeneralSecurityException {
        File fileMetadata = new File();
        fileMetadata.setName(customFileName);

        List<String> parents = Arrays.asList(googleFolderIdParent);
        fileMetadata.setParents(parents);
        //
        Drive driveService = getDriveService();

        Drive.Files.Create createFile  = driveService.files().create(fileMetadata, uploadStreamContent)
                .setFields("id, webContentLink, webViewLink, parents");
        return createFile;
    }

    public static String getFileNameFromId(String id) {
        String fileName = null;
        try {
            fileName = getDriveService().files().get(id).execute().getName();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
        return fileName;
    }
}
