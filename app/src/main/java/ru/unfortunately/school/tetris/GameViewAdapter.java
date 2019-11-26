package ru.unfortunately.school.tetris;

import android.graphics.Point;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameViewAdapter implements Runnable {

    private static final String TAG = "GameViewLogCatTag";
    
    private GameView mGameView;


    //Todo: при повороте может произойти конфликт в потоках
    private FigureModel mCurrentFigure;
    private Point mCurrentPoint;
    private List<GameRect> mDroppedRects;
    private int mGameSpeed;

    public void startGame(){
        new Thread(this).start();
        setNewRandomFigure();
        mDroppedRects = new ArrayList<>();
//        GameProcess process = new GameProcess();
//        process.doInBackground();
    }

    public void setGameView(GameView gameView) {
        mGameView = gameView;
    }

    public void setGameSpeed(int gameSpeed) {
        mGameSpeed = gameSpeed;
        //Todo: хардкод. Переделать по-человечески
        gameSpeed = 1000;
    }

    @Override
    public void run() {
        setNewRandomFigure();
        mDroppedRects = new ArrayList<>();
        loop();
    }

    private void loop(){
        while (true){
            mCurrentPoint.y++;
            try {
                Thread.sleep(mGameSpeed);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if(touchCheck()){
                onTouch();
            }
            Log.i(TAG, "loop: ");
            sendRectsToView();
        }
    }

    private void sendRectsToView() {
        List<GameRect> rects = new ArrayList<>(mDroppedRects);
        List<GameRect> figureRects = mCurrentFigure.getRects();
        for (GameRect figureRect : figureRects) {
            rects.add(figureRect);
        }
        mGameView.setGameRects(rects);
    }

    private void onTouch(){
        //Todo: Разобрать страую фигуру на квадраты, создать новую
        setNewRandomFigure();
    }

    private boolean touchCheck(){
        //Todo: проверить коснулась ли фигура низа
        return mCurrentFigure.getShape()[FigureModel.Y_INDEX] + mCurrentPoint.y >= GameView.HEIGHT_IN_BLOCKS;
    }

    private void setNewRandomFigure(){
        List<FigureModel> figures = Figures.getAllFigures();
        Random random = new Random();
        mCurrentFigure = figures.get(random.nextInt(figures.size()));
        mCurrentPoint = new Point(GameView.WIDTH_IN_BlOCKS/2, 0);
    }

    private List<GameRect> getAllRects(){
        List<GameRect> rects = new ArrayList<>(mDroppedRects);
        List<GameRect> figureRects = mCurrentFigure.getRects();
        for (GameRect figureRect : figureRects) {
            rects.add(figureRect);
        }
        return rects;
    }

    private void setRectToView(List<GameRect> rects){
        mGameView.setGameRects(rects);
    }

//    private class GameProcess extends AsyncTask<Void, List<GameRect>, Void> {
//
//
//        @Override
//        protected Void doInBackground(Void... voids) {
//            for (int i = 0; i < 10; i++){
//                mCurrentPoint.y++;
//                try {
//                    Thread.sleep(mGameSpeed);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                if(touchCheck()){
//                    onTouch();
//                }
//                Log.i(TAG, "loop: ");
//                publishProgress(getAllRects());
//            }
//            return null;
//        }
//
//        @Override
//        protected void onProgressUpdate(List<GameRect>... values) {
//            Log.i(TAG, "onProgressUpdate: " + values[0].size());
//            setRectToView(values[0]);
//        }
//    }
}
