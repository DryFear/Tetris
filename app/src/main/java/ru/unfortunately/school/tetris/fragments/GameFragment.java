package ru.unfortunately.school.tetris.fragments;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
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
import ru.unfortunately.school.tetris.game.listeners.FigureChangeListener;
import ru.unfortunately.school.tetris.game.listeners.GameOverListener;
import ru.unfortunately.school.tetris.game.listeners.SetScoreListener;
import ru.unfortunately.school.tetris.game.GameView;
import ru.unfortunately.school.tetris.game.GameViewAdapter;
import ru.unfortunately.school.tetris.IMainActivity;
import ru.unfortunately.school.tetris.models.FigureModel;
import ru.unfortunately.school.tetris.models.Figures;
import ru.unfortunately.school.tetris.R;

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
        setUpPreference();
        setUpGameView();
        setUpButton();
    }

    @Override
    public void onPause() {
        super.onPause();
        mGameView.cancelAnimation();
    }

    private void setUpPreference() {
        mPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
        String colorValue = mPreferences.getString(getResources().getString(R.string.color_list_key_preference), null);
        if(colorValue == null) return;
        String[] colorSetNames = getResources().getStringArray(R.array.color_values);
        int[] colors;
        switch (colorValue){
            case "default":
                colors = getResources().getIntArray(R.array.default_color_set);
                Figures.setColors(colors);
                break;
            case "black_white":
                colors = getResources().getIntArray(R.array.white_black_color_set);
                Figures.setColors(colors);
                break;
            case "rainbow":
                colors = getResources().getIntArray(R.array.rainbow_color_set);
                Figures.setColors(colors);
                break;
            case "white":
                colors = getResources().getIntArray(R.array.white_color_set);
                Figures.setColors(colors);
                break;
            default:
                throw new RuntimeException("Illegal color value");
        }

    }

    public GameViewAdapter getGameAdapter(){
        return mGameAdapter;
    }

    @Override
    public void onGameOver() {
        IMainActivity activity = mMainActivityRef.get();
        if (activity != null) {
            mMainActivityRef.get().endGame();
        }
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
        Log.i("TEST", "onNextFigureChange: " + mNextFigureHeight);
        Bitmap bitmap;
        bitmap = FigureModel.getBitmap(mNextFigureWidth, mNextFigureHeight, figure);
        mNextFigureImageView.setImageBitmap(bitmap);
    }
}
