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

/**
 * Prod by Unfortunately Still Alive (Александр Воронин)
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
    public static final int WIDTH_IN_BLOCKS = 10;
    public static final int HEIGHT_IN_BLOCKS = 20;

    /**
     * Длина и ширина игрового поля.
     * Меняется только в onMeasure {@link GameView#onMeasure(int, int)}
     */
    private int mGameWidth = 0;
    private int mGameHeight = 0;

    /**
     * Ширина границ игрового поля.
     * //TODO: Сделать ее настраиваемой через xml
     */
    private int mBorderStroke = 10;

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
    private Paint mRectPaint = new Paint();
    private Paint mBoardPaint = new Paint();

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
     * Стандартные конструкторы View
     */
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

    /**
     * Устанавливает игровые блоки для отрисовки
     * Вызывается обычно из адаптера {@link GameView#mAdapter}
     * Вызывает invalidate() для обновления рисунка на экране
     *
     * @param rects - игровые блоки для отрисовки
     */
    public void setGameRects(List<GameRect> rects){
        mGameRects = rects;
        invalidate();
    }

    /**
     * Устанавливает адаптер для игры {@link GameView#mAdapter}
     * и настраивает его.
     *
     * //TODO: Сделать startGame() отдельным методом view
     */

    public void setAdapter(GameViewAdapter adapter){
        mAdapter = adapter;
        mAdapter.setGameView(this);
        mAdapter.startGame();
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

    /**
     * В этом методе меняется только длина и ширина блока на основе
     * установленных размеров поля
     *
     * См {@link GameView#mBlockLength}
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mBlockLength = (int)(mGameWidth-2.0*mBorderStroke)/WIDTH_IN_BLOCKS;
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
        mDetector = new GestureDetector(getContext(), new OnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }

            @Override
            public void onShowPress(MotionEvent e) {

            }

            /**
             * При тапе на левую часть экрана
             * вызывает {@link GameViewAdapter#moveFigureToLeft()}
             * для перемещения падающей фигуры влево
             *
             * Аналогично с правой частью экрана и {@link GameViewAdapter#moveFigureToRight()}
             */
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                float x = e.getX();
                if(x > mGameWidth/2.0){
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

            /**
             * При свайпе влево вызывает {@link GameViewAdapter#swipeLeft()}
             * для поворота падающей фигуры влево
             *
             * Аналогично со свпйпом вправо и {@link GameViewAdapter#swipeRight()}
             *
             * TODO: Сделать определение свайпа менее чувствительным и добавить свайп вниз для ускорения фигуры
             */
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
        canvas.drawLine(mGameWidth, 0, mGameWidth, mGameHeight, mBoardPaint);
        canvas.drawLine(0, mGameHeight, mGameWidth, mGameHeight, mBoardPaint);
        mBoardPaint.setStrokeWidth(mBorderStroke);
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

}
