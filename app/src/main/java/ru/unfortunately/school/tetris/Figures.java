package ru.unfortunately.school.tetris;

import android.graphics.Color;
import android.graphics.Point;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Figures {

    private static int[] sColors = {
        Color.YELLOW,
        Color.RED,
        Color.GRAY,
        Color.GREEN,
        Color.CYAN,
        Color.BLUE,
        Color.MAGENTA
    };


    public static final List<FigureModel> ALL_FIGURES = getFigures();

    public static List<FigureModel> getAllFigures(){
        return new ArrayList<>(ALL_FIGURES);
    }

    private static List<FigureModel> getFigures(){
        List<FigureModel> list = new ArrayList<>();

        list.add(new FigureModel(Arrays.asList(new Point(0,0), new Point(1,0), new Point(2,0), new Point(3,0)), sColors[0]));
        list.add(new FigureModel(Arrays.asList(new Point(0,0), new Point(1,0), new Point(1,1), new Point(2,1)), sColors[1]) );
        list.add(new FigureModel(Arrays.asList(new Point(0,1), new Point(1,1), new Point(1,0), new Point(2,0)), sColors[2]));
        list.add(new FigureModel(Arrays.asList(new Point(0,0), new Point(1,0), new Point(2,0), new Point(1,1)), sColors[3]));
        list.add(new FigureModel(Arrays.asList(new Point(0,0), new Point(1,0), new Point(2,0), new Point(2,1)), sColors[4]));
        list.add(new FigureModel(Arrays.asList(new Point(0,1), new Point(1,1), new Point(2,1), new Point(2,0)), sColors[5]));
        list.add(new FigureModel(Arrays.asList(new Point(0,0), new Point(0,1), new Point(1,0), new Point(1,1)), sColors[6]));

        return list;
    }

}
