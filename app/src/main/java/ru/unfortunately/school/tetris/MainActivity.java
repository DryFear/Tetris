package ru.unfortunately.school.tetris;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity
                                implements IMainActivity{


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
        getSupportFragmentManager().beginTransaction()
                .addToBackStack(null)
                .replace(R.id.root, GameFragment.newInstance())
                .commit();
    }

    @Override
    public void backToMainMenu() {

    }
}
