package ru.unfortunately.school.tetris;

import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.graphics.Point;
import android.util.Log;
import android.view.animation.LinearInterpolator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;





public class GameViewAdapter{

    private static final String TAG = "GameViewLogCatTag";
    private GameView mGameView;

    public static final int FLAG_MOVE_TO_LEFT = 0;
    public static final int FLAG_MOVE_TO_RIGHT = 1;

    //Todo: при повороте может произойти конфликт в потоках
    private FigureModel mCurrentFigure;
    private Point mCurrentPoint;
    private List<GameRect> mDroppedRects;
    private int mGameSpeed;
    private ValueAnimator mAnimator;

    public void startGame(){
        setNewRandomFigure();
        mDroppedRects = new ArrayList<>();
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
        mGameSpeed = 300;
    }


    private void startAnimation(){
        mAnimator.addUpdateListener(new AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {

                if(touchCheck()){
                    mAnimator.cancel();
                    Log.i(TAG, "onAnimationUpdate: touchCheck - true ");
                    onTouch();
                    mAnimator.start();
                }

                mCurrentPoint.y = (int)animation.getAnimatedValue();
                sendRectsToView();
            }
        });
        mAnimator.start();
    }

    private void sendRectsToView() {
        List<GameRect> rects = new ArrayList<>(mDroppedRects);
        List<GameRect> figureRects = mCurrentFigure.getRects();
        for (GameRect figureRect : figureRects) {
//            Point newCoord = new Point();
//            newCoord.x = figureRect.getCoordinate().x;
//            newCoord.x = mCurrentPoint.x + figureRect.getCoordinate().x;
//            newCoord.y = mCurrentPoint.y + figureRect.getCoordinate().y;
//            GameRect rect = new GameRect(newCoord, figureRect.getColor());
            rects.add(figureRect.getGameRectInAbsoluteCoolrinates(mCurrentPoint));
        }
        mGameView.setGameRects(rects);
    }

    private void onTouch(){
        List<GameRect> rects = mCurrentFigure.getRects();
        for (GameRect rect : rects) {
            mDroppedRects.add(rect.getGameRectInAbsoluteCoolrinates(mCurrentPoint));
        }
        setNewRandomFigure();
        //TODO: Проверить на заполненные ряды
    }

    private boolean touchCheck(){
        //TODO: проверить коснулась ли фигура других фигур
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
        mCurrentPoint = new Point(GameView.WIDTH_IN_BlOCKS/2, 0);
    }

    public void moveFigureToRight(){
        //TODO: здесь тоже некрасивая "-1"
        if(mCurrentFigure.getShape()[FigureModel.X_INDEX] + mCurrentPoint.x < GameView.WIDTH_IN_BlOCKS - 1
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

    public void moveFigureToLeft(){
        if(mCurrentPoint.x > 0 && !checkIfNearBlocks(FLAG_MOVE_TO_LEFT)){
            mCurrentPoint.x--;
            sendRectsToView();
        }

    }
}
