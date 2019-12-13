package ru.unfortunately.school.tetris.fragments;

import android.content.SharedPreferences;
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
import androidx.preference.PreferenceManager;
import ru.unfortunately.school.tetris.IMainActivity;
import ru.unfortunately.school.tetris.R;
import ru.unfortunately.school.tetris.background.BackgroundView;
import ru.unfortunately.school.tetris.models.Figures;

public class MainMenuFragment extends Fragment {

    private WeakReference<IMainActivity> mMainActivityRef;
    private Button mStartGameButton;
    private Button mToOptionsButton;
    private Button mToRecordsButton;
    private BackgroundView mBackgroundView;
    private SharedPreferences mPreferences;

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
        initViews(view);
        setUpButtons();
        setUpPreference();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mBackgroundView.startAnimation();
    }

    @Override
    public void onPause() {
        super.onPause();
        mBackgroundView.cancelAnimation();
    }


    private void initViews(View view){
        mStartGameButton = view.findViewById(R.id.btn_start_game);
        mToOptionsButton = view.findViewById(R.id.btn_options);
        mToRecordsButton = view.findViewById(R.id.btn_records);
        mBackgroundView = view.findViewById(R.id.background_view);
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

    private void setUpPreference() {
        mPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
        String colorValue = mPreferences.getString(getResources().getString(R.string.color_list_key_preference), null);
        if(colorValue == null) return;
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

    private MainMenuFragment(){

    }



}
