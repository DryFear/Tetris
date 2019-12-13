package ru.unfortunately.school.tetris.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import ru.unfortunately.school.tetris.IMainActivity;
import ru.unfortunately.school.tetris.R;
import ru.unfortunately.school.tetris.game.GameView;
import ru.unfortunately.school.tetris.game.GameViewAdapter;
import ru.unfortunately.school.tetris.game.listeners.FigureChangeListener;
import ru.unfortunately.school.tetris.game.listeners.GameOverListener;
import ru.unfortunately.school.tetris.game.listeners.SetScoreListener;
import ru.unfortunately.school.tetris.models.FigureModel;
import ru.unfortunately.school.tetris.room.DatabaseMigration;
import ru.unfortunately.school.tetris.room.Record;
import ru.unfortunately.school.tetris.room.RecordsDatabase;

public class GameFragment extends Fragment
        implements GameOverListener, SetScoreListener, FigureChangeListener {

    private GameView mGameView;
    private ImageView mNextFigureImageView;
    private ImageButton mPauseButton;
    private WeakReference<IMainActivity> mMainActivityRef;
    private GameViewAdapter mGameAdapter;
    private TextView mScoreView;
    private SharedPreferences mPreferences;
    private static final int DEFAULT_DIFFICULT = 2000;

    private int mNextFigureWidth = 0;
    private int mNextFigureHeight = 0;



    public static GameFragment newInstance() {

        Bundle args = new Bundle();

        GameFragment fragment = new GameFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_game, container, false);
        mGameView = view.findViewById(R.id.game_view);
        mNextFigureImageView = view.findViewById(R.id.img_next_figure);
        mPauseButton = view.findViewById(R.id.btn_pause);
        mScoreView = view.findViewById(R.id.txt_score);

        return view;
    }


    private void setUpGameView() {
        mGameAdapter = new GameViewAdapter(this, this, this);
        mGameAdapter.setGameSpeed(mPreferences.getInt(
                getResources().getString(R.string.difficult_key_preference),
                DEFAULT_DIFFICULT

        ));
        mGameView.setAdapter(mGameAdapter);
    }

    private void setUpButton(){
        mPauseButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mMainActivityRef.get().pauseGame();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        if(requireActivity() instanceof  IMainActivity) {
            mMainActivityRef = new WeakReference<>((IMainActivity) requireActivity());
        }else{
            throw new RuntimeException("Illegal instance if Activity");
        }
        mPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
        setUpGameView();
        setUpButton();
    }

    @Override
    public void onPause() {
        super.onPause();
        mGameView.cancelAnimation();
    }


    public GameViewAdapter getGameAdapter(){
        return mGameAdapter;
    }

    @Override
    public void onGameOver(int score) {
        IMainActivity activity = mMainActivityRef.get();
        if (activity != null) {
            mMainActivityRef.get().endGame();
        }
        DatabaseInserter inserter = new DatabaseInserter(requireContext());
        Record record = new Record();
        record.setDate(new Date());
        String nickname = mPreferences.getString(getResources().getString(R.string.name_key_preference), "Player");
        record.setNickname(nickname);
        record.setScore(score);
        inserter.execute(record);
    }

    @Override
    public void setScore(int score) {
        mScoreView.setText(getResources().getString(R.string.score_n, score));
    }

    @Override
    public void onNextFigureChange(@NonNull FigureModel figure) {
        if(mNextFigureHeight == 0){
            mNextFigureHeight = mNextFigureImageView.getHeight();
        }
        if(mNextFigureWidth == 0){
            mNextFigureWidth = mNextFigureImageView.getWidth();
        }
        Bitmap bitmap;
        bitmap = FigureModel.getBitmap(mNextFigureWidth, mNextFigureHeight, figure);
        mNextFigureImageView.setImageBitmap(bitmap);
    }

    class DatabaseInserter extends AsyncTask<Record, Void, Void>{

        private WeakReference<Context> mContextRef;

        public DatabaseInserter(Context context) {
            mContextRef = new WeakReference<>(context);
        }


        @Override
        protected Void doInBackground(Record... records) {
            Context context = mContextRef.get();
            if(context != null){
                RecordsDatabase db;
                RoomDatabase.Builder<RecordsDatabase> builder = Room.databaseBuilder(context, RecordsDatabase.class, "records.db");
                builder.addMigrations(new DatabaseMigration(1, 2));
                db = builder.build();
                for (Record record : records) {
                    db.getRecordsDao().addRecord(record);
                }
            }
            return null;
        }
    }
}
