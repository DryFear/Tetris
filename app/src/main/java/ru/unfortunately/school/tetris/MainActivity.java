package ru.unfortunately.school.tetris;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity
                                implements IMainActivity{


    private GameFragment mGameFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(savedInstanceState == null){
            getSupportFragmentManager().beginTransaction()
                    .addToBackStack(null)
                    .add(R.id.root, MainMenuFragment.newInstance())
                    .commit();
        }
    }

    @Override
    public void startGame() {
        mGameFragment = GameFragment.newInstance();
        getSupportFragmentManager().beginTransaction()
                .addToBackStack(null)
                .replace(R.id.root, mGameFragment)
                .commit();
    }

    @Override
    public void backToMainMenu() {
        getSupportFragmentManager().beginTransaction()
                .addToBackStack(null)
                .replace(R.id.root, MainMenuFragment.newInstance())
                .commit();
    }

    @Override
    public void resumeGame() {
        getSupportFragmentManager().beginTransaction()
                .addToBackStack(null)
                .replace(R.id.root, mGameFragment)
                .commit();
    }

    @Override
    public void pauseGame() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.pause_root, PauseFragment.newInstance())
                .commit();
    }
}
