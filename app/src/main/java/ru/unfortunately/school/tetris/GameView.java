package ru.unfortunately.school.tetris;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;

public class GameView extends View {

    private static final String TAG = "GameViewLogcatTag";
    private static final boolean IS_LOGGING = false;

    public static final int BLOCK_LENGTH = 30;
    public static final int WIDTH_IN_BlOCKS = 10;
    public static final int HEIGHT_IN_BLOCKS = 20;

    private GameViewAdapter mAdapter;
    private List<GameRect> mGameRects = new ArrayList<>();

    private Paint mPaint = new Paint();

    public GameView(Context context) {
        super(context);
    }

    public GameView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public GameView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public GameView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawRects(canvas);
    }

    private void drawRects(Canvas canvas) {
        for (GameRect gameRect : mGameRects) {
            mPaint.setColor(gameRect.getColor());
            if(IS_LOGGING){
                Rect rect = gameRect.getRectWithScaleInAbsoluteCoordinates(BLOCK_LENGTH);
                Log.i(TAG, "drawRects method, this is absolute coordinates of rect: left: " +
                        rect.left + ", top: " +
                        rect.top + ", right: " +
                        rect.right + ", bottom: " +
                        rect.bottom);
            }
            canvas.drawRect(gameRect.getRectWithScaleInAbsoluteCoordinates(BLOCK_LENGTH), mPaint);
        }
    }


    synchronized public void setGameRects(List<GameRect> rects){

        mGameRects = rects;
        if(IS_LOGGING){
            Log.i(TAG, "setGameRects method in GameView: Input List size:" + rects.size() + " and their coordinates:");
            for (GameRect rect : rects) {
                Log.i(TAG, "Rect coordinates : " + rect.getCoordinate().x + " , " + rect.getCoordinate().y);
            }
        }
        invalidate();
    }

    public void setAdapter(GameViewAdapter adapter){
        mAdapter = adapter;
        mAdapter.setGameView(this);
        mAdapter.startGame();
    }



}
