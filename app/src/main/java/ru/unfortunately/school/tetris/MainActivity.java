package ru.unfortunately.school.tetris;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private GameView mGameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onStart() {
        super.onStart();
        setUpGameView();
    }


    /**
     * Настройка игрового поля и добавление для него адаптера
     */

    private void setUpGameView() {
        mGameView = findViewById(R.id.game_view);
        GameViewAdapter adapter = new GameViewAdapter();
        adapter.setGameSpeed(1000);
        mGameView.setAdapter(adapter);

    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
