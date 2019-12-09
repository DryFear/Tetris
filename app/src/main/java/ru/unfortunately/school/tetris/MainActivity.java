package ru.unfortunately.school.tetris;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import ru.unfortunately.school.tetris.Fragments.GameFragment;
import ru.unfortunately.school.tetris.Fragments.MainMenuFragment;
import ru.unfortunately.school.tetris.Fragments.PauseFragment;

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
        mGameFragment.getGameAdapter().resumeGame();
    }

    @Override
    public void pauseGame() {
        getSupportFragmentManager().beginTransaction()
                .add(R.id.root, PauseFragment.newInstance())
                .commit();
        mGameFragment.getGameAdapter().pauseGame();
    }
}
