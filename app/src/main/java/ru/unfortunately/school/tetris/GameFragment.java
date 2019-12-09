package ru.unfortunately.school.tetris;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class GameFragment extends Fragment {

    private GameView mGameView;

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
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        setUpGameView();
        super.onViewCreated(view, savedInstanceState);
    }

    private void setUpGameView() {
        GameViewAdapter adapter = new GameViewAdapter();
        adapter.setGameSpeed(1000);
        mGameView.setAdapter(adapter);
    }
}
