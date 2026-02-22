package org.fcnabc.autoppt.google;

import java.io.IOException;
import java.security.GeneralSecurityException;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Names;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;

import com.google.api.client.auth.oauth2.Credential;

public class GoogleModule extends AbstractModule {
    
    @Override 
    protected void configure() {
        bindConstant().annotatedWith(Names.named("googleAppName")).to("AutoPPT");
    }

    @Provides
    @Singleton
    NetHttpTransport provideNetHttpTransport() throws GeneralSecurityException, IOException {
        return GoogleNetHttpTransport.newTrustedTransport();
    }

    @Provides
    @Singleton
    Credential provideCredential(GoogleAuth googleAuth) throws IOException {
        return googleAuth.getCredentials();
    }
}
