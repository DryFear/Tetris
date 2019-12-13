package ru.unfortunately.school.tetris.models;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.graphics.Point;

import java.util.Random;

import ru.unfortunately.school.tetris.background.BackgroundViewListener;

public class AnimationHelper {

    private FigureModel mModel;
    private ValueAnimator mAnimator;
    private Point mCoord;
    private BackgroundViewListener mListener;
    private int mMaxWidth;


    public Point getCoord() {
        return mCoord;
    }

    public void setCoord(Point coord) {
        mCoord = coord;
    }


    public AnimationHelper(FigureModel model, ValueAnimator animator, BackgroundViewListener listener, int maxWidth) {
        mModel = model;
        mAnimator = animator;
        mCoord = new Point();
        mListener = listener;
        mMaxWidth = maxWidth;
        addListeners();
    }

    private void addListeners() {
        mAnimator.addUpdateListener(new AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mCoord.y = (int) animation.getAnimatedValue();
                mListener.update();
            }
        });
        mAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationRepeat(Animator animation) {
                super.onAnimationRepeat(animation);
                mCoord.x = new Random().nextInt(mMaxWidth);
            }
        });
    }

    public FigureModel getModel() {
        return mModel;
    }

    public ValueAnimator getAnimator() {
        return mAnimator;
    }

    public void startAnimation(){

        mAnimator.start();
    }

    public boolean isAnimationRunning(){
        return mAnimator.isRunning();
    }

    public void cancelAnimation(){
        mAnimator.cancel();
    }

}
