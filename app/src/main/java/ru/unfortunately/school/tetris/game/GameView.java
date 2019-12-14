package ru.unfortunately.school.tetris.game;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import ru.unfortunately.school.tetris.models.GameRect;
import ru.unfortunately.school.tetris.R;

/**
 * Prod by Александр Воронин
 */

/**
 *
 * View для игрового поля
 * Вся логика фигур обрабатывается в адаптере (см. {@link GameViewAdapter})
 *
 * Под блоками, которые будут описаны ниже, подразумеваются
 * экземпляры класса GameRect {@link GameRect}
 */

public class GameView extends View {

    private static final String TAG = "GameViewLogcatTag";

    /**
     *  Константы, которые содержат длину и ширину игрового поля в блоках
     */
    public static final int WIDTH_IN_BLOCKS     = 10;
    public static final int HEIGHT_IN_BLOCKS    = 20;


    /**
     * Схемы управления. Задается в preference
     */

    private static final int MOVE_ON_TAP = 0;
    private static final int MOVE_ON_SWIPE = 1;
    private int mControlChema = 0;

    /**
     * Длина и ширина игрового поля.
     * Меняется только в onMeasure {@link GameView#onMeasure(int, int)}
     */
    private int mGameWidth  = 0;
    private int mGameHeight = 0;

    /**
     * Ширина границ игрового поля.
     *
     * Настраивается через XML
     */
    private int mBorderStroke ;
    private static final int DEFAULT_BORDER_STROKE = 10;

    /**
     * Длина и ширина для одного блока
     *
     * Блоки всегда квадратные, поэтому для длины и ширины
     * используется только одна переменная
     *
     * Задается после определения размеров поля в {@link GameView#onSizeChanged(int, int, int, int)}
     */
    private int mBlockLength;


    /**
     * Игровой адаптер, в котором обрабатывается вся логика
     * Без него игра работать не будет {@link GameViewAdapter}
     */
    private GameViewAdapter mAdapter;

    /**
     * Блоки для отрисовки.
     * Блоки задаются только через сеттер: {@link GameView#setGameRects(List)}
     */
    private List<GameRect> mGameRects = new ArrayList<>();

    /**
     * mRectPaint для отрисовки блоков
     * mBoardPaint для отрисовки границ поля и границ блоков
     *
     * Стартовые параметры задаются в {@link GameView#init()}
     */
    private Paint mRectPaint    = new Paint();
    private Paint mBoardPaint   = new Paint();

    /**
     * Детектор для упрощения работы с жестами.
     * Инициализируется в {@link GameView#init()}
     */
    private GestureDetector mDetector;

    /**
     * Размер экрана
     * Используется для задания размеров игрового поля
     * в {@link GameView#onMeasure(int, int)}
     */
    private Point mDisplaySize;

    /**
     * Чувствительность свайпа. Указывает сколько блоков в секунду нужно
     * свайпнуть для действия
     *
     * Настраивается через XML
     *
     */
    private int mSwipeSense;
    private static final int DEFAULT_SWIPE_SENSE = 2;


    /**
     * Флаг, началась игра или нет
     */

    private boolean mIsGameStarted = false;

    /**
     * Стандартные конструкторы View
     */
    public GameView(Context context) {
        this(context, null);
    }

    public GameView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
        extractAttributes(context, attrs);
    }

    public GameView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
        extractAttributes(context, attrs);
    }

    public GameView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
        extractAttributes(context, attrs);
    }

    /**
     * Устанавливает игровые блоки для отрисовки
     * Вызывается обычно из адаптера {@link GameView#mAdapter}
     * Вызывает invalidate() для обновления рисунка на экране
     *
     * @param rects - игровые блоки для отрисовки
     */
    public void setGameRects(@NonNull List<GameRect> rects){
        mGameRects = rects;
        invalidate();
    }

    /**
     * Устанавливает адаптер для игры {@link GameView#mAdapter}
     * и настраивает его.
     *
     */

    public void setAdapter(@NonNull GameViewAdapter adapter){
        mAdapter = adapter;
        mAdapter.setGameView(this);
    }

    /**
     * Метод, который вызывается при onStop() во фрагменте
     * чтобы избежать утечки анимации
     */
    public void cancelAnimation() {
        mAdapter.cancelAnimation();
    }

    /**
     * Настраивает схему управления
     * @param controlChema порядковый номер схемы из preference
     */
    public void setControlChema(int controlChema) {
        mControlChema = controlChema;
    }

    /**
     * @return Передает всю обработку касаний детектору (См {@link GameView#mDetector})
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mDetector.onTouchEvent(event);
    }

    /**
     * Игровое поле хочет Выставить свои размеры в соотношении 20:10
     * с учетом линии границ
     *
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int height      = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode  = MeasureSpec.getMode(heightMeasureSpec);

        int width       = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode   = MeasureSpec.getMode(widthMeasureSpec);

        if(heightMode == MeasureSpec.EXACTLY && widthMode == MeasureSpec.EXACTLY){
            mGameWidth  = width;
            mGameHeight = height;
        }else if(heightMode == MeasureSpec.AT_MOST && widthMode == MeasureSpec.AT_MOST){
            mGameHeight = height;
            mGameWidth  = (height - mBorderStroke*2)/2 + mBorderStroke*2;
        }else if(heightMode == MeasureSpec.UNSPECIFIED && widthMode == MeasureSpec.UNSPECIFIED){
            mGameHeight = mDisplaySize.y;
            mGameWidth = (mGameHeight - mBorderStroke*2)/2 + mBorderStroke*2;
        }else{
            mGameHeight = measureSingle(height, heightMode);
            mGameWidth = measureSingle(width, widthMode);
        }
        setMeasuredDimension(mGameWidth, mGameHeight);
    }

    /**
     * Метод для отдельного measure сторон
     */
    private int measureSingle(int len, int mode) {
        switch (mode) {
            case MeasureSpec.EXACTLY:
            case MeasureSpec.AT_MOST:
                return len;
            case MeasureSpec.UNSPECIFIED:
            default:
                return Math.max(mDisplaySize.x, mDisplaySize.y);
        }
    }


    /**
     * В этом методе меняется только длина и ширина блока на основе
     * установленных размеров поля
     *
     * Потом корректирует длину и ширину поля (из-за погрешности в int)
     *
     * См {@link GameView#mBlockLength}
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mBlockLength = (int)(mGameWidth-2.0*mBorderStroke)/WIDTH_IN_BLOCKS;

        mGameHeight = mBlockLength*HEIGHT_IN_BLOCKS + 2*mBorderStroke;
        mGameWidth  = mBlockLength*WIDTH_IN_BLOCKS + 2*mBorderStroke;
    }

    /**
     * Переопределенный метод {@link View#onDraw(Canvas)}
     * Вызывает 2 метода для отрисовки границ поля и
     * всех игровых блоков
     * см {@link GameView#drawBoards(Canvas)}, {@link GameView#drawRects(Canvas)}
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(!mIsGameStarted){
            mIsGameStarted = true;
            mAdapter.startGame();
        }
        drawBoards(canvas);
        drawRects(canvas);
    }

    /**
     * Задает параметры для {@link GameView#mRectPaint} и {@link GameView#mBoardPaint}
     * А так же инициализирует детектор {@link GameView#mDetector}
     */
    private void init() {
        mBoardPaint.setStyle(Style.STROKE);
        mRectPaint.setStyle(Style.FILL);
        mBoardPaint.setStrokeWidth(mBorderStroke);
        mBoardPaint.setAntiAlias(true);

        mDetector = new GestureDetector(getContext(), new OnGestureListener() {

            /**
             * Не обрабатывает касания, если игра не началась
             */
            @Override
            public boolean onDown(MotionEvent e) {
                return mIsGameStarted;
            }

            @Override
            public void onShowPress(MotionEvent e) {

            }

            /**
             * При тапе на левую часть экрана
             * вызывает {@link GameViewAdapter#moveFigureToLeft()}
             * для перемещения или поворота (зависит от {@link GameView#mControlChema})
             * падающей фигуры влево
             *
             * Аналогично с правой частью экрана и {@link GameViewAdapter#moveFigureToRight()}
             */
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                float x = e.getX();
                if(x > mGameWidth/2.0){
                    switch (mControlChema){
                        case MOVE_ON_SWIPE:
                            mAdapter.transposeToRight();
                            break;
                        case MOVE_ON_TAP:
                        default:
                            mAdapter.moveFigureToRight();
                            break;
                    }
                }
                else{
                    switch (mControlChema){
                        case MOVE_ON_SWIPE:
                            mAdapter.transposeToLeft();
                            break;
                        case MOVE_ON_TAP:
                        default:
                            mAdapter.moveFigureToLeft();
                            break;
                    }
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

            /**
             * При свайпе влево вызывает {@link GameViewAdapter#transposeToLeft()}
             * для поворота или перемещения (зависит от {@link GameView#mControlChema})
             * падающей фигуры влево
             *
             * Аналогично со свпйпом вправо и {@link GameViewAdapter#transposeToRight()}
             *
             */
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                float diffX = e2.getX() - e1.getX();
                float diffY = e2.getY() - e1.getY();
                float sense = mSwipeSense * mBlockLength;
                if(diffY > Math.abs(diffX) && diffY > sense){
                    mAdapter.swipeDown();
                }else{
                    if(diffX > sense){
                        switch (mControlChema){
                            case MOVE_ON_SWIPE:
                                mAdapter.moveFigureToRight();
                                break;
                            case MOVE_ON_TAP:
                            default:
                                mAdapter.transposeToRight();
                                break;
                        }
                    }
                    if(diffX < -sense){
                        switch (mControlChema){
                            case MOVE_ON_SWIPE:
                                mAdapter.moveFigureToLeft();
                                break;
                            case MOVE_ON_TAP:
                            default:
                                mAdapter.transposeToLeft();
                                break;
                        }
                    }
                }
                return true;
            }
        });
    }


    /**
     * Рисует границы поля. Вызывается в {@link GameView#onDraw(Canvas)}
     *
     * Увеличивает толщину кисти перед рисованием, т.к. половина линии границы
     * рисуется за пределами view и не видна
     */
    private void drawBoards(Canvas canvas){
        mBoardPaint.setStrokeWidth(mBorderStroke*2);
        canvas.drawLine(0, 0, 0, mGameHeight, mBoardPaint);
        canvas.drawLine(0, 0, mGameWidth, 0, mBoardPaint);

        mBoardPaint.setStrokeWidth(mBorderStroke);
        canvas.drawLine(mGameWidth, 0, mGameWidth, mGameHeight, mBoardPaint);
        canvas.drawLine(0, mGameHeight, mGameWidth, mGameHeight, mBoardPaint);
    }


    /**
     * Рисует игровые блоки. Вызывается в {@link GameView#onDraw(Canvas)}
     * Перемещает канвас, чтобы блоки не накладывались на границы
     *
     * Блоки хранят внутри себя координаты не в пикселях, а в блоках, поэтому
     * чтобы получить координаты в пикселях,
     * вызвается {@link GameRect#getRectWithScaleInAbsoluteCoordinates(int)}
     *
     */
    private void drawRects(Canvas canvas) {
        canvas.translate(mBorderStroke, mBorderStroke);
        for (GameRect gameRect : mGameRects) {
            mRectPaint.setColor(gameRect.getColor());
            canvas.drawRect(gameRect.getRectWithScaleInAbsoluteCoordinates(mBlockLength), mRectPaint);
            canvas.drawRect(gameRect.getRectWithScaleInAbsoluteCoordinates(mBlockLength), mBoardPaint);
        }
    }

    /**
     * Извлекает атрибуты из параметров xml
     */
    private void extractAttributes(Context context, @Nullable AttributeSet attrs){
        final Resources.Theme theme = context.getTheme();
        final TypedArray typedArray = theme.obtainStyledAttributes(attrs, R.styleable.GameView, R.attr.GameViewStyle, 0);
        try {
            mBorderStroke = typedArray.getInteger(R.styleable.GameView_border_stroke, DEFAULT_BORDER_STROKE);
            mSwipeSense = typedArray.getInteger(R.styleable.GameView_swipe_sensitive, DEFAULT_SWIPE_SENSE);
        }finally {
            typedArray.recycle();
        }
    }

}
