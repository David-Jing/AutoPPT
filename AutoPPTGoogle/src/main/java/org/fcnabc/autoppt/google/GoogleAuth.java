package org.fcnabc.autoppt.google;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.DriveScopes;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

/**
 * Handles Google OAuth2 authentication and credential management.
 * If modifying scopes, delete previously saved tokens.
 */
@Singleton
public class GoogleAuth {
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final List<String> SCOPES = Collections.singletonList(DriveScopes.DRIVE);
    
    private final NetHttpTransport httpTransport;
    private final String googleCredentials;
    private final Path googleTokenDirectory;

    @Inject
    public GoogleAuth(
        NetHttpTransport httpTransport,
        @Named("GOOGLE_CREDENTIALS") String googleCredentials,
        @Named("GOOGLE_TOKEN_DIRECTORY") Path googleTokenDirectory
    ) {
        this.googleCredentials = googleCredentials;
        this.httpTransport = httpTransport;
        this.googleTokenDirectory = googleTokenDirectory;
    }

    /**
     * Creates an authorized Credential object.
     */
    public Credential getCredentials() throws IOException {
        // Load client secrets.
        InputStream in = new ByteArrayInputStream(googleCredentials.getBytes());
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                httpTransport, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(googleTokenDirectory.toFile()))
                .setAccessType("offline")
                .build();
        
        /*
        LocalServerReceiver will automatically open the browser, listen for the 
        redirect on a local port, and capture the token.
        */
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }
}
