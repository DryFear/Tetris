package ru.unfortunately.school.tetris.fragments;

import android.os.Bundle;

import androidx.preference.Preference;
import androidx.preference.Preference.OnPreferenceChangeListener;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SeekBarPreference;
import ru.unfortunately.school.tetris.R;

public class PreferenceScreenFragment extends PreferenceFragmentCompat {

    private static final String ARG_ROOT = "root";


    public static PreferenceScreenFragment newInstance(String root) {

        Bundle args = new Bundle();

        args.putString(ARG_ROOT, root);

        PreferenceScreenFragment fragment = new PreferenceScreenFragment();
        fragment.setArguments(args);
        return fragment;
    }



    private PreferenceScreenFragment(){

    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, getArguments().getString(ARG_ROOT));
        SeekBarPreference seekBarPreference = getPreferenceManager()
                .findPreference(getString(R.string.difficult_key_preference));
        if (seekBarPreference != null) {
            seekBarPreference.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    preference.setSummary(String.valueOf((int)newValue));
                    return true;
                }
            });
            seekBarPreference.setSummary(String.valueOf(seekBarPreference.getValue()));
        }else{
            throw new RuntimeException("Cannot find seekBarPreference");
        }
    }
}
