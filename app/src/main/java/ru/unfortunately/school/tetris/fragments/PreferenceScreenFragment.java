package ru.unfortunately.school.tetris.fragments;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;

import java.lang.ref.WeakReference;

import androidx.preference.Preference;
import androidx.preference.Preference.OnPreferenceChangeListener;
import androidx.preference.Preference.OnPreferenceClickListener;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SeekBarPreference;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import ru.unfortunately.school.tetris.R;
import ru.unfortunately.school.tetris.room.DatabaseMigration;
import ru.unfortunately.school.tetris.room.RecordsDatabase;

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
        Preference deletePreference = getPreferenceManager()
                .findPreference(getResources().getString(R.string.delete_records_key_preference));
        if(deletePreference != null){
            deletePreference.setOnPreferenceClickListener(new OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    new DatabaseDeleter(requireContext()).execute();
                    return true;
                }
            });
        }else{
            throw new RuntimeException("Cannot find delete preference");
        }
    }

    class DatabaseDeleter extends AsyncTask<Void, Void, Void> {

        private WeakReference<Context> mContextRef;

        public DatabaseDeleter(Context context) {
            mContextRef = new WeakReference<>(context);
        }



        @Override
        protected Void doInBackground(Void... voids) {
            Context context = mContextRef.get();
            if(context != null){
                RecordsDatabase db;
                RoomDatabase.Builder<RecordsDatabase> builder = Room.databaseBuilder(context, RecordsDatabase.class, "records.db");
                builder.addMigrations(new DatabaseMigration(1, 2));
                db = builder.build();
                db.getRecordsDao().removeAll();
            }
            return null;
        }
    }
}
