package ru.unfortunately.school.tetris;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;

public class GameView extends View {

    //

    private static final String TAG = "GameViewLogcatTag";

    public static final int BLOCK_LENGTH = 70;
    public static final int WIDTH_IN_BlOCKS = 10;
    public static final int HEIGHT_IN_BLOCKS = 20;

    private static final int GAME_FIELD_WIDTH = BLOCK_LENGTH * WIDTH_IN_BlOCKS;

    private GameViewAdapter mAdapter;
    private List<GameRect> mGameRects = new ArrayList<>();

    private Paint mRectPaint = new Paint();
    private Paint mBoardPaint = new Paint();
    private GestureDetector mDetector;

    public GameView(Context context) {
        this(context, null);
    }

    public GameView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public GameView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public GameView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        mBoardPaint.setStyle(Style.STROKE);
        mRectPaint.setStyle(Style.FILL);
        mBoardPaint.setStrokeWidth(10);
        mDetector = new GestureDetector(getContext(), new OnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }

            @Override
            public void onShowPress(MotionEvent e) {

            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                Log.i(TAG, "onSingleTapUp");
                float x = e.getX();
                if(x > GAME_FIELD_WIDTH/2.0){
                    mAdapter.moveFigureToRight();
                }
                else{
                    mAdapter.moveFigureToLeft();
                }
                return true;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                return false;
            }

            @Override
            public void onLongPress(MotionEvent e) {

            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                Log.i(TAG, "onFling: ");
                if(velocityX > 0){
                    mAdapter.swipeRight();
                }
                else{
                    mAdapter.swipeLeft();
                }
                return true;
            }
        });
    }



    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBoards(canvas);
        drawRects(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mDetector.onTouchEvent(event);
    }

    private void drawBoards(Canvas canvas){
        canvas.drawLine(0, 0, 0, HEIGHT_IN_BLOCKS*BLOCK_LENGTH, mBoardPaint);
        canvas.drawLine(0, 0, WIDTH_IN_BlOCKS*BLOCK_LENGTH, 0, mBoardPaint);
        canvas.drawLine(WIDTH_IN_BlOCKS*BLOCK_LENGTH, 0, WIDTH_IN_BlOCKS*BLOCK_LENGTH, HEIGHT_IN_BLOCKS*BLOCK_LENGTH, mBoardPaint);
        canvas.drawLine(0, HEIGHT_IN_BLOCKS*BLOCK_LENGTH, WIDTH_IN_BlOCKS*BLOCK_LENGTH, HEIGHT_IN_BLOCKS*BLOCK_LENGTH, mBoardPaint);
    }

    private void drawRects(Canvas canvas) {
        for (GameRect gameRect : mGameRects) {
            mRectPaint.setColor(gameRect.getColor());
            canvas.drawRect(gameRect.getRectWithScaleInAbsoluteCoordinates(BLOCK_LENGTH), mRectPaint);
        }
    }


    synchronized public void setGameRects(List<GameRect> rects){
        mGameRects = rects;
        invalidate();
    }

    public void setAdapter(GameViewAdapter adapter){
        mAdapter = adapter;
        mAdapter.setGameView(this);
        mAdapter.startGame();
        //sadsd
    }



}
