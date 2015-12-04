package com.ngvietan.blackoutschedule.data;

import android.content.SearchRecentSuggestionsProvider;

/**
 * Search Recent Provider
 * Created by NgVietAn on 25/11/2015.
 */
public class SearchRecentProvider extends SearchRecentSuggestionsProvider {
    public final static String AUTHORITY = "com.ngvietan.blackoutschedule.SearchRecentProvider";
    public final static int MODE = DATABASE_MODE_QUERIES;

    public SearchRecentProvider() {
        setupSuggestions(AUTHORITY, MODE);
    }
}
