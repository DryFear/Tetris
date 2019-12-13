package ru.unfortunately.school.tetris.game;

import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.graphics.Point;
import android.util.Log;
import android.view.animation.LinearInterpolator;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import ru.unfortunately.school.tetris.game.listeners.FigureChangeListener;
import ru.unfortunately.school.tetris.game.listeners.GameOverListener;
import ru.unfortunately.school.tetris.game.listeners.SetScoreListener;
import ru.unfortunately.school.tetris.models.FigureModel;
import ru.unfortunately.school.tetris.models.Figures;
import ru.unfortunately.school.tetris.models.GameRect;


public class GameViewAdapter{

    private static final String TAG = "GameViewLogCatTag";
    private static final int MIN_SPEED = 1000;
    private boolean mGameRunningFlag = false;
    private GameView mGameView;

    public static final int FLAG_MOVE_TO_LEFT   = 0;
    public static final int FLAG_MOVE_TO_RIGHT  = 1;

    private FigureModel mCurrentFigure;
    private FigureModel mNextFigure;
    private Point mCurrentPoint;
    private List<GameRect> mDroppedRects;
    private int mGameSpeed;
    private ValueAnimator mAnimator;
    private boolean mIsBoost = false;

    private final static int SCORE_STEP = 10;
    private int mCurrentScore;
    private SetScoreListener mScoreListener;
    private GameOverListener mGameOverListener;
    private FigureChangeListener mFigureChangeListener;

    public GameViewAdapter(GameOverListener gameOverListener,
                           SetScoreListener scoreListener,
                           FigureChangeListener figureChangeListener){
        mGameOverListener = gameOverListener;
        mScoreListener = scoreListener;
        mFigureChangeListener = figureChangeListener;
    }

    public void startGame(){
        mDroppedRects = new LinkedList<>();
        mCurrentScore = 0;
        mScoreListener.setScore(mCurrentScore);
        mGameRunningFlag = true;
        setNewRandomFigure(true);

        mAnimator = ValueAnimator.ofInt(0, GameView.HEIGHT_IN_BLOCKS);
        mAnimator.setDuration(mGameSpeed*GameView.HEIGHT_IN_BLOCKS);
        mAnimator.setInterpolator(new LinearInterpolator());
        startAnimation();
    }

    public void setGameView(GameView gameView) {
        mGameView = gameView;
    }

    public void setGameSpeed(int gameSpeed) {
        mGameSpeed = MIN_SPEED/gameSpeed;
    }


    private void startAnimation(){
        mAnimator.addUpdateListener(new AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if(mGameRunningFlag) {
                    int value = (int) animation.getAnimatedValue();
                    mCurrentPoint.y = value;
                    if (mIsBoost) {
                        mAnimator.pause();
                        boost();
                    }
                    if (touchCheck()) {
                        mAnimator.cancel();
                        Log.i(TAG, "onAnimationUpdate: ");
                        onTouch();
                        mAnimator.start();
                    }
                    sendRectsToView();
                }
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
            rects.add(figureRect.getGameRectInAbsoluteCoordinates(mCurrentPoint));
        }
        mGameView.setGameRects(rects);
    }

    private void onTouch(){
        List<GameRect> rects = mCurrentFigure.getRects();
        for (GameRect rect : rects) {
            mDroppedRects.add(rect.getGameRectInAbsoluteCoordinates(mCurrentPoint));
        }
        deleteRows();
        sendRectsToView();
        setNewRandomFigure(false);
    }

    private void deleteRows() {
        for (int i = 0; i < GameView.HEIGHT_IN_BLOCKS; i++) {
            if(checkFillRow(i)){
                mCurrentScore += SCORE_STEP;
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
        mScoreListener.setScore(mCurrentScore);
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
                Point figCoord = figureRect.getGameRectInAbsoluteCoordinates(mCurrentPoint).getCoordinate();
                Point droppedCoord = droppedRect.getCoordinate();
                if(figCoord.x == droppedCoord.x && figCoord.y + 1 == droppedCoord.y){
                    return true;
                }
            }
        }

        return mCurrentFigure.getShape()[FigureModel.Y_INDEX] + mCurrentPoint.y>= GameView.HEIGHT_IN_BLOCKS - 1;
    }

    private void setNewRandomFigure(boolean isFirst){
        List<FigureModel> figures = Figures.getAllFigures();
        Random random = new Random();
        if(isFirst){
            mNextFigure = figures.get(random.nextInt(figures.size()));
        }
        mCurrentFigure = mNextFigure;
        mCurrentPoint = new Point(GameView.WIDTH_IN_BLOCKS /2, 0);
        mNextFigure = figures.get(random.nextInt(figures.size()));
        mFigureChangeListener.onNextFigureChange(mNextFigure);
        if(touchCheck()){
            gameOver();
        }
    }

    private void gameOver(){
        mGameRunningFlag = false;
        mAnimator.pause();
        mAnimator.cancel();
        if(mAnimator.isRunning()){
            Log.i(TAG, "gameOver: ");
        }
        mGameOverListener.onGameOver(mCurrentScore);
    }

    public void moveFigureToRight(){
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
                Point figCoord = figureRect.getGameRectInAbsoluteCoordinates(mCurrentPoint).getCoordinate();
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
        int saveX = mCurrentPoint.x;
        while (touchCheck() || mCurrentPoint.x +
                mCurrentFigure.getShape()[FigureModel.X_INDEX] > GameView.WIDTH_IN_BLOCKS - 1){
            mCurrentPoint.x--;
        }
        if(mCurrentPoint.x < 0){
            mCurrentFigure.transposeToLeft();
            mCurrentPoint.x = saveX;
        }
    }

    public void swipeLeft(){
        mCurrentFigure.transposeToLeft();
        int saveX = mCurrentPoint.x;
        while (touchCheck() || mCurrentPoint.x +
                mCurrentFigure.getShape()[FigureModel.X_INDEX] > GameView.WIDTH_IN_BLOCKS - 1){
            mCurrentPoint.x--;
        }
        if(mCurrentPoint.x < 0){
            mCurrentFigure.transposeToRight();
            mCurrentPoint.x = saveX;
        }
    }

    public void moveFigureToLeft(){
        if(mCurrentPoint.x > 0 && !checkIfNearBlocks(FLAG_MOVE_TO_LEFT)){
            mCurrentPoint.x--;
            sendRectsToView();
        }

    }

    public void pauseGame(){
        mAnimator.pause();
    }

    public void resumeGame(){
        mAnimator.resume();
    }

    public void swipeDown() {
        mIsBoost = true;
    }

    public void cancelAnimation() {
        mAnimator.cancel();
    }
}
