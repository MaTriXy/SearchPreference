package com.bytehamster.preferencesearch;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceFragmentCompat;
import com.bytehamster.lib.preferencesearch.SearchConfiguration;
import com.bytehamster.lib.preferencesearch.SearchPreference;
import com.bytehamster.lib.preferencesearch.SearchPreferenceResult;
import com.bytehamster.lib.preferencesearch.SearchPreferenceResultListener;

/**
 * This file demonstrates some additional features that might not be needed when setting it up for the first time
 */
public class EnhancedExample extends AppCompatActivity implements SearchPreferenceResultListener {
    private PrefsFragment prefsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefsFragment = new PrefsFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(android.R.id.content, prefsFragment).commit();
    }

    @Override
    public void onSearchResultClicked(@NonNull final SearchPreferenceResult result) {
        prefsFragment = new PrefsFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(android.R.id.content, prefsFragment).addToBackStack("PrefsFragment")
                .commit(); // Allow to navigate back to search

        new Handler().post(new Runnable() { // Allow fragment to get created
            @Override
            public void run() {
                prefsFragment.onSearchResultClicked(result);
            }
        });
    }

    public static class PrefsFragment extends PreferenceFragmentCompat {
        SearchPreference searchPreference;

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            addPreferencesFromResource(R.xml.preferences);

            searchPreference = (SearchPreference) findPreference("searchPreference");
            SearchConfiguration config = searchPreference.getSearchConfiguration();
            config.setActivity((AppCompatActivity) getActivity());
            config.setFragmentContainerViewId(android.R.id.content);

            config.index().addBreadcrumb("Main file").addFile(R.xml.preferences);
            config.index().addBreadcrumb("Second file").addFile(R.xml.preferences2);
            config.setBreadcrumbsEnabled(true);
            config.setHistoryEnabled(true);
            config.setFuzzySearchEnabled(true);
        }

        private void onSearchResultClicked(SearchPreferenceResult result) {
            if (result.getResourceFile() == R.xml.preferences) {
                searchPreference.setVisible(false); // Do not allow to click search multiple times
                scrollToPreference(result.getKey());
                findPreference(result.getKey()).setTitle("RESULT: " + findPreference(result.getKey()).getTitle());
            } else {
                // Result was found in the other file
                getPreferenceScreen().removeAll();
                addPreferencesFromResource(R.xml.preferences2);
                result.highlight(this, 0x00ff00);
            }
        }
    }
}
