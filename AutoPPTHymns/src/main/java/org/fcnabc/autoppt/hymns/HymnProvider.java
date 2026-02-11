package org.fcnabc.autoppt.hymns;

import java.util.Map;

import com.google.api.client.util.DateTime;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import org.fcnabc.autoppt.google.GoogleDrive;

@Singleton
public class HymnProvider {
    private Map<String, DateTime> hymnLastUpdatedMap;
    private GoogleDrive googleDrive;

    @Inject
    public HymnProvider(GoogleDrive googleDrive) {
        this.googleDrive = googleDrive;
    }
}
