package ru.unfortunately.school.tetris.Fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.ref.WeakReference;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import ru.unfortunately.school.tetris.Game.GameView;
import ru.unfortunately.school.tetris.Game.GameViewAdapter;
import ru.unfortunately.school.tetris.GameOverListener;
import ru.unfortunately.school.tetris.IMainActivity;
import ru.unfortunately.school.tetris.R;
import ru.unfortunately.school.tetris.SetScoreListener;

public class GameFragment extends Fragment implements GameOverListener, SetScoreListener {

    private GameView mGameView;
    private ImageView mNextFigureImageView;
    private ImageButton mPauseButton;
    private WeakReference<IMainActivity> mMainActivityRef;
    private GameViewAdapter mGameAdapter;
    private TextView mScoreView;
    private SharedPreferences mPreferences;
    private static final int DEFAULT_DIFFICULT = 2000;

    private GameFragment(){
    }

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
        mPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
        return view;
    }


    private void setUpGameView() {
        mGameAdapter = new GameViewAdapter(this, this);
        mGameAdapter.setGameSpeed(mPreferences.getInt(
                getResources().getString(R.string.difficult_key_preference),
                DEFAULT_DIFFICULT

        ));
        mGameAdapter.setNextFigureImageView(mNextFigureImageView);
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
        setUpGameView();
        setUpButton();
    }

    public GameViewAdapter getGameAdapter(){
        return mGameAdapter;
    }

    @Override
    public void onGameOver() {
        mMainActivityRef.get().endGame();
    }

    @Override
    public void setScore(int score) {
        mScoreView.setText(getResources().getString(R.string.score_n, score));
    }
}
