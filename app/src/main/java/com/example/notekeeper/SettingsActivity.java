package com.example.notekeeper;

import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceFragmentCompat;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingsFragment())
                .commit();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

    }

    public static class SettingsFragment extends PreferenceFragmentCompat {

//        private EditTextPreference namePreference;
//        private EditTextPreference emailPreference;
//        private EditTextPreference snPreference;

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
//            namePreference = findPreference("user_display_name");
//            emailPreference = findPreference("user_email");
//            namePreferenceSummaryUpdates();
//            emailPreferenceSummaryUpdates();
        }

//        private void namePreferenceSummaryUpdates() {
//            if (namePreference != null){
//                namePreference.setSummaryProvider(new Preference.SummaryProvider<EditTextPreference>() {
//                    @Override
//                    public CharSequence provideSummary(EditTextPreference preference) {
//                        String text = preference.getText();
//                        if (TextUtils.isEmpty(text))
//                            return "Your name";
//                        return text;
//                    }
//                });
//            }
//        }
//        private void emailPreferenceSummaryUpdates() {
//            if (emailPreference != null){
//                emailPreference.setSummaryProvider(new Preference.SummaryProvider<EditTextPreference>() {
//                    @Override
//                    public CharSequence provideSummary(EditTextPreference preference) {
//                        String text = preference.getText();
//                        if (TextUtils.isEmpty(text))
//                            return "yourname@yourhost.com";
//                        return text;
//                    }
//                });
//            }
//        }

    }
}