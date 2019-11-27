package ru.unfortunately.school.tetris;

import android.graphics.Point;
import android.graphics.Rect;

public class GameRect {

    private Rect mRect;
    private int mColor;
    private Point mCoordinate;


    public GameRect(Point coordinate, int color) {
        mRect = new Rect(0, 0, 1, 1);
        mCoordinate = new Point(coordinate);
        mColor = color;
    }

    public GameRect(GameRect gameRect){
        mRect = new Rect(gameRect.getRect());
        mColor = gameRect.getColor();
        mCoordinate = new Point(gameRect.getCoordinate());
    }

    public Rect getRect() {
        return new Rect(mRect);
    }

    public void setRect(Rect rect) {
        mRect = rect;
    }

    public int getColor() {
        return mColor;
    }

    public void setColor(int color) {
        mColor = color;
    }

    public Point getCoordinate() {
        return new Point(mCoordinate);
    }

    public void setCoordinate(Point coordinate) {
        mCoordinate = coordinate;
    }

    public Rect getRectWithScaleInAbsoluteCoordinates(int scale){
        final int width = mRect.width();
        final int height = mRect.height();
        final int l = mCoordinate.x*scale;
        final int t = mCoordinate.y*scale;
        final int r = l + (width * scale);
        final int b = t + (height * scale);
        return new Rect(l, t, r, b);
    }

    public GameRect getGameRectInAbsoluteCoolrinates(Point absCoord){
        Point point = new Point(mCoordinate);
        point.x += absCoord.x;
        point.y += absCoord.y;
        return new GameRect(point, mColor);
    }
}
