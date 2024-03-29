package ru.unfortunately.school.tetris.fragments;

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

public class PauseFragment extends Fragment {

    private Button mResumeButton;
    private Button mExitButton;

    private WeakReference<IMainActivity> mMainActivityRef;

    public static PauseFragment newInstance() {

        Bundle args = new Bundle();

        PauseFragment fragment = new PauseFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pause, container, false);
        mExitButton = view.findViewById(R.id.btn_exit);
        mResumeButton = view.findViewById(R.id.btn_resume);
        return view;
    }


    @Override
    public void onStart() {
        super.onStart();
        if(requireActivity() instanceof  IMainActivity) {
            mMainActivityRef = new WeakReference<>((IMainActivity) requireActivity());
            setUpButtons();
        }else{
            throw new RuntimeException("Illegal instance if Activity");
        }
    }

    private void setUpButtons(){
        mResumeButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mMainActivityRef.get().resumeGame();
            }
        });

        mExitButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mMainActivityRef.get().backToMainMenu();
            }
        });
    }

    private PauseFragment(){

    }
}
