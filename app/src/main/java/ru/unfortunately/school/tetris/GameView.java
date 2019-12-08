package ru.unfortunately.school.tetris;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
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
    public static final int WIDTH_IN_BLOCKS = 10;
    public static final int HEIGHT_IN_BLOCKS = 20;

    private int mGameWidth = 0;
    private int mGameHeight = 0;
    private int mBorderStroke = 10;
    private int mBlockLength;

    private static final int GAME_FIELD_WIDTH = BLOCK_LENGTH * WIDTH_IN_BLOCKS;

    private GameViewAdapter mAdapter;
    private List<GameRect> mGameRects = new ArrayList<>();

    private Paint mRectPaint = new Paint();
    private Paint mBoardPaint = new Paint();
    private GestureDetector mDetector;
    private Point mDisplaySize;

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

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        if(heightMode == MeasureSpec.EXACTLY && widthMode == MeasureSpec.EXACTLY){
            mGameWidth = width;
            mGameHeight = height;
        }else if(heightMode == MeasureSpec.AT_MOST && widthMode == MeasureSpec.AT_MOST){
            mGameHeight = height;
            mGameWidth = (height - mBorderStroke*2)/2 + mBorderStroke*2;
        }else if(heightMode == MeasureSpec.UNSPECIFIED && widthMode == MeasureSpec.UNSPECIFIED){

        }else{
            //TODO: Рассмотреть случай когда моды не равны
        }
        setMeasuredDimension(mGameWidth, mGameHeight);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        mBlockLength = (int)(mGameWidth-2.0*mBorderStroke)/WIDTH_IN_BLOCKS;
        Log.i(TAG, "BlockLen: " + mBlockLength);
        Log.i(TAG, "onLayout: ");
        Log.i(TAG, "Height: " + mGameHeight);
        super.onLayout(changed, left, top, right, bottom);
    }

    private void init() {
        mBoardPaint.setStyle(Style.STROKE);
        mRectPaint.setStyle(Style.FILL);
        mBoardPaint.setStrokeWidth(mBorderStroke);
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
        mBoardPaint.setStrokeWidth(mBorderStroke*2);
        canvas.drawLine(0, 0, 0, mGameHeight, mBoardPaint);
        canvas.drawLine(0, 0, mGameWidth, 0, mBoardPaint);
        canvas.drawLine(mGameWidth, 0, mGameWidth, mGameHeight, mBoardPaint);
        canvas.drawLine(0, mGameHeight, mGameWidth, mGameHeight, mBoardPaint);
        mBoardPaint.setStrokeWidth(mBorderStroke);
    }

    private void drawRects(Canvas canvas) {
        canvas.translate(mBorderStroke, mBorderStroke);
        for (GameRect gameRect : mGameRects) {
            mRectPaint.setColor(gameRect.getColor());
            canvas.drawRect(gameRect.getRectWithScaleInAbsoluteCoordinates(mBlockLength), mRectPaint);
            canvas.drawRect(gameRect.getRectWithScaleInAbsoluteCoordinates(mBlockLength), mBoardPaint);
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
