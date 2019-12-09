package ru.unfortunately.school.tetris.Fragments;

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
import ru.unfortunately.school.tetris.Game.GameView;
import ru.unfortunately.school.tetris.Game.GameViewAdapter;
import ru.unfortunately.school.tetris.IMainActivity;
import ru.unfortunately.school.tetris.R;

public class GameFragment extends Fragment {

    private GameView mGameView;
    private ImageView mNextFigureImageView;
    private ImageButton mPauseButton;
    private WeakReference<IMainActivity> mMainActivityRef;
    private GameViewAdapter mGameAdapter;
    private TextView mScoreView;


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
        return view;
    }


    private void setUpGameView() {
        mGameAdapter = new GameViewAdapter();
        mGameAdapter.setGameSpeed(1000);
        mGameAdapter.setScoreView(mScoreView);
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
}
