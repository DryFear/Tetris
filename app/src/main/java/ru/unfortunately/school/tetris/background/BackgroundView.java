package ru.unfortunately.school.tetris.background;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import androidx.annotation.Nullable;
import ru.unfortunately.school.tetris.R;
import ru.unfortunately.school.tetris.models.Figures;
import ru.unfortunately.school.tetris.models.GameRect;

public class BackgroundView extends View implements BackgroundViewListener {

    public static final int DEFAULT_STROKE_WIDTH = 10;
    private int mStrokeWidth;

    public static final int DEFAULT_ANIMATION_TIME = 8000;
    private int mAnimationTime;

    public static final int DEFAULT_DELAY = 1200;
    private int mDelay;

    public static final int DEFAULT_BLOCK_LENGTH = 80;
    private int mBlockLength;

    private List<GameRect> mGameRects = new ArrayList<>();
    private List<AnimationHelper> mHelpers = new ArrayList<>();

    private Paint mRectPaint = new Paint();
    private Paint mBoardPaint = new Paint();
    private Random mRandom = new Random();




    public BackgroundView(Context context) {
        this(context, null);
    }

    public BackgroundView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        extractResources(context, attrs);
        init();
    }

    public BackgroundView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        extractResources(context, attrs);
        init();
    }

    public BackgroundView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        extractResources(context, attrs);
        init();
    }

    private void init() {
        mBoardPaint.setColor(Color.BLACK);
        mBoardPaint.setStyle(Style.STROKE);
        mBoardPaint.setStrokeWidth(mStrokeWidth);
        mRectPaint.setStyle(Style.FILL);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        for (int i = 0; i < Figures.getAllFigures().size(); i++) {
            ValueAnimator animator = ValueAnimator.ofInt(-mBlockLength*2, h);
            animator.setRepeatMode(ValueAnimator.RESTART);
            animator.setRepeatCount(ValueAnimator.INFINITE);
            animator.setInterpolator(new LinearInterpolator());
            animator.setStartDelay(mDelay *i);
            animator.setDuration(mAnimationTime);
            AnimationHelper helper = new AnimationHelper(Figures.getAllFigures().get(i), animator, this, w, mBlockLength);
            helper.setCoord(new Point(mRandom.nextInt(w - mBlockLength*4), 2*h));
            mHelpers.add(i, helper);
        }
        for (AnimationHelper helper : mHelpers) {
            if(!helper.isAnimationRunning()){
                helper.startAnimation();
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.WHITE);
        for (GameRect gameRect : mGameRects) {

            mRectPaint.setColor(gameRect.getColor());
            canvas.drawRect(gameRect.getRectWithScale(mBlockLength), mRectPaint);
            canvas.drawRect(gameRect.getRectWithScale(mBlockLength), mBoardPaint);
        }
    }

    @Override
    public void update() {
        mGameRects.clear();
        for (AnimationHelper helper : mHelpers) {
            for (GameRect rect : helper.getModel().getRects()) {
                int x = mBlockLength * rect.getCoordinate().x + helper.getCoord().x;
                int y = mBlockLength * rect.getCoordinate().y + helper.getCoord().y;
                GameRect gameRect = new GameRect(new Point(x, y), rect.getColor());
                mGameRects.add(gameRect);
            }
        }
        invalidate();
    }

    public void startAnimation(){
        for (AnimationHelper helper : mHelpers) {
            if(!helper.isAnimationRunning()){
                helper.startAnimation();
            }
        }
    }

    public void cancelAnimation(){
        for (AnimationHelper helper : mHelpers) {
            helper.cancelAnimation();
        }
    }

    private void extractResources(Context context, @Nullable AttributeSet attrs){
        final Resources.Theme theme = context.getTheme();
        final TypedArray typedArray = theme.obtainStyledAttributes(attrs, R.styleable.BackgroundView, R.attr.BackgroundViewStyle, 0);
        try {
            mAnimationTime = typedArray.getInteger(R.styleable.BackgroundView_animation_time, DEFAULT_ANIMATION_TIME);
            mDelay = typedArray.getInteger(R.styleable.BackgroundView_delay, DEFAULT_DELAY);
            mBlockLength = typedArray.getDimensionPixelSize(R.styleable.BackgroundView_block_length, DEFAULT_BLOCK_LENGTH);
            mStrokeWidth = typedArray.getDimensionPixelSize(R.styleable.BackgroundView_block_border_stroke_size, DEFAULT_STROKE_WIDTH);
        }finally {
            typedArray.recycle();
        }
    }
}
