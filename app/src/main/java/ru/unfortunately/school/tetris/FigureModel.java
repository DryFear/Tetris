package ru.unfortunately.school.tetris;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Point;

import java.util.ArrayList;
import java.util.List;

public class FigureModel {

    private List<GameRect> mRects;

    public static final int X_INDEX = 0;
    public static final int Y_INDEX = 1;

    public static final int RECT_COUNT = 4;

    public FigureModel(List<Point> points, int color) {
        mRects = new ArrayList<>();
        for (Point point : points) {
            GameRect rect = new GameRect(point, color);
            mRects.add(rect);
        }
    }

    public List<GameRect> getRects() {
        return new ArrayList<>(mRects);
    }

    public int[] getShape(){
        int[] res = new int[2];
        int maxX = 0;
        int maxY = 0;
        for (int i = 0; i < RECT_COUNT; i++) {
            Point point = mRects.get(i).getCoordinate();
            if(point.x > maxX) maxX = point.x;
            if(point.y > maxY) maxY = point.y;
        }
        res[X_INDEX] = maxX;
        res[Y_INDEX] = maxY;
        return res;
    }


    public void transposeToLeft(){
        int[] shape = getShape();
        for (GameRect rect : mRects) {
            int x = rect.getCoordinate().y;
            int y = shape[X_INDEX] - rect.getCoordinate().x;
            rect.setCoordinate(new Point(x, y));
        }
    }

    public void transposeToRight(){
        int[] shape = getShape();
        for (GameRect rect : mRects) {
            int y = rect.getCoordinate().x;
            int x = shape[Y_INDEX] - rect.getCoordinate().y;
            rect.setCoordinate(new Point(x, y));
        }
    }

    public static Bitmap getBitmap(int width, int height, FigureModel figure){
        Bitmap bitmap = Bitmap.createBitmap(width, height, Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        //TODO: Нарисовать фигуру на битмапе
        return bitmap;
    }
}
