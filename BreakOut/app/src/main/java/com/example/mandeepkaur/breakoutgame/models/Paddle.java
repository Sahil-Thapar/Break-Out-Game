package com.example.mandeepkaur.breakoutgame.models;

import android.graphics.RectF;

public class Paddle {

    private RectF rect;
    private float length;
    private float height;
    private int screenWidth;
    private int screenHeight;
    private float x;
    private float y;
    private float paddleSpeed;
    public final int STOPPED = 0;
    public final int LEFT = 1;
    public final int RIGHT = 2;
    private int paddleMoving = STOPPED;

    public Paddle(int screenX, int screenY){
        length = screenX/6;
        height = screenY/6;
        screenWidth=screenX;
        screenHeight=screenY;

        x = screenX / 2-length/2;
        y =  screenY-150;

        rect = new RectF(x, y, x + length, y+height );

        paddleSpeed = 250;
    }


    public RectF getRect(){
        return rect;
    }


    public void reset(float screenX,float screenY){
        rect.left = screenX / 2-length/2;
        rect.top =  screenY-250;
        rect.right=rect.left+length;
        rect.bottom=rect.top+length;
    }

    public void setMovementState(int state){
        paddleMoving = state;
    }

    public void update(long fps){
        if(x - paddleSpeed / fps>=10 && paddleMoving == LEFT){
            x = x - paddleSpeed / fps;
        }

        if(x + paddleSpeed / fps+length<= screenWidth-10 &&  paddleMoving == RIGHT){
            x = x + paddleSpeed / fps;
        }

        rect.left = x;
        rect.right = x + length;
    }

}