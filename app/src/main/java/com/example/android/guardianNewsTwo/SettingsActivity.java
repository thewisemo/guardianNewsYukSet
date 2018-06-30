package com.example.android.guardianNewsTwo;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

/**
 * This activity class toke me a long time to be written right, Specially the 2 CheckBoxPreference.
 */

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
    }

    public static class StoryPreferenceFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings_main);

            Preference searchKeyword = findPreference(getString(R.string.settings_search_key));
            Preference date = findPreference(getString(R.string.settings_date_key));

            bindPreferenceSummaryToValue(searchKeyword);
            bindPreferenceSummaryToValue(date);

            clearPreferenceValues(searchKeyword);
            clearPreferenceValues(date);

            CheckBoxPreference imagesCheckbox = (CheckBoxPreference) getPreferenceManager().findPreference(getString(R.string.settings_images_key));
            assert imagesCheckbox != null;
            imagesCheckbox.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    if (newValue.toString().equals("true")) {
                        Toast.makeText(preference.getContext(), "Images will be loaded",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(preference.getContext(), "Images will be hidden",
                                Toast.LENGTH_SHORT).show();
                    }
                    return true;
                }
            });

            CheckBoxPreference authorCheckbox = (CheckBoxPreference) getPreferenceManager().findPreference(getString(R.string.settings_author_key));
            assert authorCheckbox != null;
            authorCheckbox.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    if (newValue.toString().equals("true")) {
                        Toast.makeText(preference.getContext(), "Author name will be shown",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(preference.getContext(), "Author name will be hidden",
                                Toast.LENGTH_SHORT).show();
                    }
                    return true;
                }
            });
        }

        private void bindPreferenceSummaryToValue(Preference preference) {
            preference.setOnPreferenceChangeListener(this);
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(preference.getContext());
            String preferenceString = preferences.getString(preference.getKey(), "");
            onPreferenceChange(preference, preferenceString);
        }

        private void clearPreferenceValues(Preference preference) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(preference.getContext());
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean(getString(R.string.settings_images_key), true);
            editor.putBoolean(getString(R.string.settings_author_key), false);
            editor.clear();
            editor.apply();
            editor.commit();
        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            // The code in this method takes care of updating the displayed preference summary after it has been changed
            String stringValue = newValue.toString();
            preference.setSummary(stringValue);
            return true;
        }
    }
}
