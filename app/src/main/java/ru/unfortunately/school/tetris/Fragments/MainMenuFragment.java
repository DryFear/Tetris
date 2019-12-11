package ru.unfortunately.school.tetris.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import java.lang.ref.WeakReference;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import ru.unfortunately.school.tetris.IMainActivity;
import ru.unfortunately.school.tetris.R;

public class MainMenuFragment extends Fragment {

    private WeakReference<IMainActivity> mMainActivityRef;
    private Button mStartGameButton;
    private Button mToOptionsButton;
    private Button mToRecordsButton;

    public static MainMenuFragment newInstance() {

        Bundle args = new Bundle();

        MainMenuFragment fragment = new MainMenuFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(requireActivity() instanceof IMainActivity) {
            mMainActivityRef = new WeakReference<>((IMainActivity) getActivity());
        }else{
            throw new RuntimeException("Illegal instance of Activity");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_menu, container, false);
        mStartGameButton = view.findViewById(R.id.btn_start_game);
        mToOptionsButton = view.findViewById(R.id.btn_options);
        mToRecordsButton = view.findViewById(R.id.btn_records);
        setUpButtons();
        return view;
    }

    private void setUpButtons(){

        mStartGameButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mMainActivityRef.get().startGame();
            }
        });

        mToOptionsButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mMainActivityRef.get().toOptions();
            }
        });

        mToRecordsButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mMainActivityRef.get().toRecords();
            }
        });
    }

    private MainMenuFragment(){

    }

}
