package ru.unfortunately.school.tetris.fragments;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.lang.ref.WeakReference;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import ru.unfortunately.school.tetris.R;
import ru.unfortunately.school.tetris.RecordListAdapter;
import ru.unfortunately.school.tetris.room.DatabaseMigration;
import ru.unfortunately.school.tetris.room.Record;
import ru.unfortunately.school.tetris.room.RecordsDatabase;

public class RecordsFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private View mNoRecordsTextView;

    public static RecordsFragment newInstance() {

        Bundle args = new Bundle();

        RecordsFragment fragment = new RecordsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_records, container, false);
        mRecyclerView = view.findViewById(R.id.recycler_records);
        mNoRecordsTextView = view.findViewById(R.id.text_view_no_records);
        DownloaderFromDatabase downloader = new DownloaderFromDatabase(requireContext());
        downloader.execute();
        return view;
    }

    private void setUpRecyclerView(List<Record> records) {
        RecordListAdapter adapter = new RecordListAdapter(records);
        if(records.size() > 0){
            mNoRecordsTextView.setVisibility(View.GONE);
        }
        mRecyclerView.setAdapter(adapter);
    }

    private RecordsFragment(){

    }

    class DownloaderFromDatabase extends AsyncTask<Void, Void, List<Record>>{

        private WeakReference<Context> mContextRef;

        DownloaderFromDatabase(Context context){
            mContextRef = new WeakReference<>(context);
        }

        @Override
        protected List<Record> doInBackground(Void... voids) {
            Context context = mContextRef.get();
            if(context != null){
                RecordsDatabase db;
                RoomDatabase.Builder<RecordsDatabase> builder = Room.databaseBuilder(context, RecordsDatabase.class, "records.db");
                builder.addMigrations(new DatabaseMigration(1, 2));
                db = builder.build();
                return db.getRecordsDao().getAllRecords();
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<Record> records) {
            setUpRecyclerView(records);
        }
    }
}
