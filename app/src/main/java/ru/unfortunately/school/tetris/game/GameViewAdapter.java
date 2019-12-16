package ru.unfortunately.school.tetris.game;

import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.graphics.Point;
import android.view.animation.LinearInterpolator;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import androidx.annotation.Nullable;
import ru.unfortunately.school.tetris.game.listeners.FigureChangeListener;
import ru.unfortunately.school.tetris.game.listeners.GameOverListener;
import ru.unfortunately.school.tetris.game.listeners.SetScoreListener;
import ru.unfortunately.school.tetris.models.FigureModel;
import ru.unfortunately.school.tetris.models.Figures;
import ru.unfortunately.school.tetris.models.GameRect;

/**
 * prod by Александр Воронин
 *
 * Адаптер для {@link GameView}
 *
 * Просчитывает всю логику фигур на поле, и при изменении на поле переводит
 * все поле в блоки {@link GameRect} и передает View {@link GameView}
 *
 */

public class GameViewAdapter{

    /**
     * Константы, которые нужны для обработки перемещения фигур влево и вправо
     * См. {@link GameViewAdapter#moveFigureToLeft()} и {@link GameViewAdapter#moveFigureToRight()}
     *
     * Сделаны для обобщения передвижения через одну функцию в {@link GameViewAdapter#checkIfNearBlocks(int)}
     */
    private static final int FLAG_MOVE_TO_LEFT   = 0;
    private static final int FLAG_MOVE_TO_RIGHT  = 1;

    /**
     * При удалении ряда к игровому счету {@link #mCurrentScore} будет добавляться это значение
     */
    private final static int SCORE_STEP = 10;

    /**
     * Чтобы можно было задавать скорость игры числами от 1 до 9, существует эта константа
     * Используется в {@link GameViewAdapter#setGameSpeed(int)}
     *
     * Скорость игры - длительность анимации падения фигуры вниз
     * Скорость игры {@link GameViewAdapter#mGameSpeed} расчитывается по формуле MIN_SPEED/n,
     * где n - число от 1 до 9, задающая уровень сложности.
     *
     * Получается заабавно: чем больше эта скорость игры, тем медленнее падают фигуры
     */
    private static final int MIN_SPEED = 1000;

    /**
     * Флаг, показывающий - идет игра или нет. Нужен чтобы аниматор {@link GameViewAdapter#mAnimator}
     * не просчитывал падение фигуры, если игра не продолжается
     */
    private boolean mGameRunningFlag = false;

    /**
     * GameView {@link GameView}, за которой закреплен данный адаптер.
     */
    private GameView mGameView;

    /**
     * mCurrentFigure - Фигура {@link FigureModel}, которая падает в данный момент времени.
     * При ее приземлении она делится на блоки {@link GameRect},
     * и в эту переменную записывается новая фигура в методе {@link GameViewAdapter#setNewRandomFigure(boolean)},
     * которая до этого хранится в mNextFigure
     *
     * mNextFigure - фигура, которая появится после преземления текущей. При приземлении текущей
     * в эту переменную записывается случайная фигура из списка фигур {@link Figures#getAllFigures()}
     *
     * В целом нужна только для отображения следующей фигуры на экране.
     * При установке новой mNextFigure, она посылается в {@link ru.unfortunately.school.tetris.fragments.GameFragment}
     * через листенер {@link GameViewAdapter#mFigureChangeListener}
     */
    private FigureModel mCurrentFigure;
    private FigureModel mNextFigure;

    /**
     * Координаты текущей фигуры {@link GameViewAdapter#mCurrentFigure}
     *
     * Координаты храняться не в пикселях, а в размерах блоков.
     * Т.е. нижняя правая точка по этим координатам равна (9, 19)
     * Это сделано, чтобы адаптер не зависел от размеров GameView
     */
    private Point mCurrentPoint;

    /**
     * Упавшие блоки {@link GameRect}, которые уже нельзя двигать.
     */
    private List<GameRect> mDroppedRects;

    /**
     * Блоки для удаления.
     */

    private List<GameView> mRectsToDelete = new ArrayList<>();

    /**
     * Скорость анимации {@link #mAnimator} падения фигуры {@link #mCurrentFigure}
     */
    private int mGameSpeed;

    /**
     * Аниматор, отвечающий за падение фигуры
     */
    private ValueAnimator mAnimator;

    /**
     * Флаг, который переключается, если игрок свайпнул вниз
     * Если этот флаг true, то текущая итерация анимации {@link #mAnimator}
     * Завершится моментально в методе {@link #boost()}
     * После этого метода этот флаг возвращается к значению false
     */
    private boolean mIsBoost = false;

    /**
     * Текущий счет. Изменятеся только в {@link #deleteRows()}
     * Увеличивается на {@link #SCORE_STEP} за каждый удаленный ряд
     * После изменения посылает результат во фрагмент {@link ru.unfortunately.school.tetris.fragments.GameFragment}
     * через листенер {@link #mScoreListener}
     */
    private int mCurrentScore;

    /**
     * Листенеры для взаимодействия с фрагментом {@link ru.unfortunately.school.tetris.fragments.GameFragment}
     *
     * mScoreListener - посылает счет для обновления количества очков на экране
     *
     * mGameOverListener - посылает сигнал о том, что игра окончена
     *
     * mFigureChangeListener - посылает {@link #mNextFigure} при ее изменении для отображения ее на экране
     */
    private SetScoreListener mScoreListener;
    private GameOverListener mGameOverListener;
    private FigureChangeListener mFigureChangeListener;



    public GameViewAdapter(@Nullable GameOverListener gameOverListener,
                           @Nullable SetScoreListener scoreListener,
                           @Nullable FigureChangeListener figureChangeListener){
        mGameOverListener = gameOverListener;
        mScoreListener = scoreListener;
        mFigureChangeListener = figureChangeListener;
    }

    /**
     * Метод для приостановки анимации в {@link #mAnimator}
     */
    public void pauseGame(){
        mAnimator.pause();
    }

    /**
     * Метод для возобновления анимации в {@link #mAnimator}
     */
    public void resumeGame(){
        mAnimator.resume();
    }

    /**
     * Метод для задания скорости падения {@link #mCurrentFigure}
     * @param gameSpeed - число от 1 до 9
     */
    public void setGameSpeed(int gameSpeed) {
        mGameSpeed = MIN_SPEED/gameSpeed;
    }

    /**
     * Метод для разового ускоренного падения {@link #mCurrentFigure}
     * При вызове его в {@link GameView} заставляет аниматору вызвать метод {@link #boost()}
     *
     * См. {@link #mIsBoost}
     */
    void swipeDown() {
        mIsBoost = true;
    }

    void cancelAnimation() {
        mAnimator.cancel();
    }

    void startGame(){
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

    void setGameView(GameView gameView) {
        mGameView = gameView;
    }


    void moveFigureToRight(){
        if(mCurrentFigure.getShape()[FigureModel.X_INDEX] + mCurrentPoint.x < GameView.WIDTH_IN_BLOCKS - 1
                && checkIfNearBlocks(FLAG_MOVE_TO_RIGHT)){
            mCurrentPoint.x++;
            sendRectsToView();
        }
    }


    void transposeToRight(){
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

    void transposeToLeft(){
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

    void moveFigureToLeft(){
        if(mCurrentPoint.x > 0 && checkIfNearBlocks(FLAG_MOVE_TO_LEFT)){
            mCurrentPoint.x--;
            sendRectsToView();
        }

    }

    private void startAnimation(){
        mAnimator.addUpdateListener(new AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if(mGameRunningFlag) {
                    mCurrentPoint.y = (int) animation.getAnimatedValue();
                    if (mIsBoost) {
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
            }
        });
        mAnimator.start();
    }

    private void boost(){
        while (!touchCheck()){
            mCurrentPoint.y++;
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
        return count == GameView.WIDTH_IN_BLOCKS;
    }

    private boolean touchCheck(){
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
        mGameOverListener.onGameOver(mCurrentScore);
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
                            return false;
                        }
                        break;
                    case FLAG_MOVE_TO_RIGHT:
                        if(figCoord.y == droppedCoord.y && figCoord.x + 1 == droppedCoord.x){
                            return false;
                        }
                        break;
                    default: throw new RuntimeException("Unsupported flag");
                }
            }
        }

        return true;
    }
}
