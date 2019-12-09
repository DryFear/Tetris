package ru.unfortunately.school.tetris;

import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.graphics.Point;
import android.util.Log;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;





public class GameViewAdapter{

    private static final String TAG = "GameViewLogCatTag";
    private GameView mGameView;

    public static final int FLAG_MOVE_TO_LEFT   = 0;
    public static final int FLAG_MOVE_TO_RIGHT  = 1;

    //Todo: при повороте может произойти конфликт в потоках
    private FigureModel mCurrentFigure;
    private Point mCurrentPoint;
    private List<GameRect> mDroppedRects;
    private int mGameSpeed;
    private ValueAnimator mAnimator;
    private boolean mIsBoost = false;

    private ImageView mNextFigureImageView;


    public void startGame(){
        setNewRandomFigure();
        mDroppedRects = new LinkedList<>();
        mAnimator = ValueAnimator.ofInt(0, GameView.HEIGHT_IN_BLOCKS);
        mAnimator.setDuration(mGameSpeed*GameView.HEIGHT_IN_BLOCKS);
        mAnimator.setInterpolator(new LinearInterpolator());
        startAnimation();
    }

    public void setGameView(GameView gameView) {
        mGameView = gameView;
    }

    public void setGameSpeed(int gameSpeed) {
        mGameSpeed = gameSpeed;
        //Todo: хардкод. Переделать по-человечески
        mGameSpeed = 1000;
    }


    private void startAnimation(){
        mAnimator.addUpdateListener(new AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (int)animation.getAnimatedValue();
                mCurrentPoint.y = value;
                if(mIsBoost){
                    mAnimator.pause();
                    boost();
                }
                if (touchCheck()) {
                    mAnimator.cancel();
                    onTouch();
                    mAnimator.start();
                }
                sendRectsToView();
            }
        });
        mAnimator.start();
    }

    private void boost(){
        while (!touchCheck()){
            mCurrentPoint.y++;
            Log.i(TAG, "boost: " + mCurrentPoint.y);
        }
        mIsBoost = false;
    }

    private void sendRectsToView() {
        List<GameRect> rects = new LinkedList<>(mDroppedRects);
        List<GameRect> figureRects = mCurrentFigure.getRects();
        for (GameRect figureRect : figureRects) {
            rects.add(figureRect.getGameRectInAbsoluteCoolrinates(mCurrentPoint));
        }
        mGameView.setGameRects(rects);
    }

    private void onTouch(){
        List<GameRect> rects = mCurrentFigure.getRects();
        for (GameRect rect : rects) {
            mDroppedRects.add(rect.getGameRectInAbsoluteCoolrinates(mCurrentPoint));
        }
        deleteRows();
        sendRectsToView();
        setNewRandomFigure();
    }

    private void deleteRows() {
        for (int i = 0; i < GameView.HEIGHT_IN_BLOCKS; i++) {
            if(checkFillRow(i)){
                List<GameRect> tempList = new ArrayList<>(mDroppedRects);
                for (GameRect droppedRect : tempList) {
                    int y = droppedRect.getCoordinate().y;
                    if(y == i){
                        mDroppedRects.remove(droppedRect);
                    }
                    if(y < i){
                        Point coord = droppedRect.getCoordinate();
                        coord.y++;
                        droppedRect.setCoordinate(coord);
                    }
                }
            }
        }
    }

    private boolean checkFillRow(int row){
        int count = 0;
        for (GameRect droppedRect : mDroppedRects) {
            if(droppedRect.getCoordinate().y == row){
                count++;
            }
        }
        if(count == GameView.WIDTH_IN_BLOCKS){
            return true;
        }
        return false;
    }

    private boolean touchCheck(){
        //TODO: подумать как лучше заменить "-1"
        for (GameRect droppedRect : mDroppedRects) {
            List<GameRect> figureRects = mCurrentFigure.getRects();
            for (GameRect figureRect : figureRects) {
                Point figCoord = figureRect.getGameRectInAbsoluteCoolrinates(mCurrentPoint).getCoordinate();
                Point droppedCoord = droppedRect.getCoordinate();
                if(figCoord.x == droppedCoord.x && figCoord.y + 1 == droppedCoord.y){
                    return true;
                }
            }
        }

        return mCurrentFigure.getShape()[FigureModel.Y_INDEX] + mCurrentPoint.y>= GameView.HEIGHT_IN_BLOCKS - 1;
    }

    private void setNewRandomFigure(){
        List<FigureModel> figures = Figures.getAllFigures();
        Random random = new Random();
        mCurrentFigure = figures.get(random.nextInt(figures.size()));
        mCurrentPoint = new Point(GameView.WIDTH_IN_BLOCKS /2, 0);
        mNextFigureImageView.setImageBitmap(
                FigureModel.getBitmap(
                        50,
                        50,
                        mCurrentFigure
                ));
    }

    public void moveFigureToRight(){
        //TODO: здесь тоже некрасивая "-1"
        if(mCurrentFigure.getShape()[FigureModel.X_INDEX] + mCurrentPoint.x < GameView.WIDTH_IN_BLOCKS - 1
                    && !checkIfNearBlocks(FLAG_MOVE_TO_RIGHT)){
            mCurrentPoint.x++;
            sendRectsToView();
        }
    }

    private boolean checkIfNearBlocks(int mode){
        for (GameRect droppedRect : mDroppedRects) {
            List<GameRect> figureRects = mCurrentFigure.getRects();
            for (GameRect figureRect : figureRects) {
                Point figCoord = figureRect.getGameRectInAbsoluteCoolrinates(mCurrentPoint).getCoordinate();
                Point droppedCoord = droppedRect.getCoordinate();
                switch (mode){
                    case FLAG_MOVE_TO_LEFT:
                        if(figCoord.y == droppedCoord.y && figCoord.x - 1 == droppedCoord.x){
                            return true;
                        }
                        break;
                    case FLAG_MOVE_TO_RIGHT:
                        if(figCoord.y == droppedCoord.y && figCoord.x + 1 == droppedCoord.x){
                            return true;
                        }
                        break;
                    default: throw new RuntimeException("Unsupported flag");
                }
            }
        }

        return false;
    }


    //TODO: При свайпе фигуры могут наложиться друг на друга. Добавить проверку на возможность
    public void swipeRight(){
        mCurrentFigure.transposeToRight();
    }

    public void swipeLeft(){
        mCurrentFigure.transposeToLeft();
    }

    public void moveFigureToLeft(){
        if(mCurrentPoint.x > 0 && !checkIfNearBlocks(FLAG_MOVE_TO_LEFT)){
            mCurrentPoint.x--;
            sendRectsToView();
        }

    }

    public void swipeDown() {
        mIsBoost = true;
    }

    public void setNextFigureImageView(ImageView nextFigureImage) {
        mNextFigureImageView = nextFigureImage;
    }
}
