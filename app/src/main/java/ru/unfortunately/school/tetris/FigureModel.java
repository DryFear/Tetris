package ru.unfortunately.school.tetris;

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
        // Todo: Сделать поворот фигуры на лево
    }

    public void transposeToRight(){
        // Todo: Сделать поворот фигуры на право
    }
}
