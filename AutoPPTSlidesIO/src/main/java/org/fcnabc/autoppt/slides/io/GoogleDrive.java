package org.fcnabc.autoppt.slides.io;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.services.drive.Drive;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

@Singleton
public class GoogleDrive {
    private final Credential credential;
    private Drive service;

    @Inject
    public GoogleDrive(Credential credential, @Named("googleAppName") String applicationName) {
        this.credential = credential;
        this.service = new Drive.Builder(
                credential.getTransport(),
                credential.getJsonFactory(),
                credential)
                .setApplicationName(applicationName)
                .build();
    }
}
