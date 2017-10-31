package com.example.mandeepkaur.breakoutgame.models;

import java.util.Random;

public class Ball {
    public float xVelocity;
    public float yVelocity;
    public float centerX;
    public float centerY;
    public float radius;

    public Ball(int screenX, int screenY){

        xVelocity = 100;
        yVelocity = -200;

        radius=screenX/12;
    }

    public void update(long fps){
        centerX=centerX+(xVelocity/fps);
        centerY=centerY+(yVelocity/fps);
    }

    public float getX(){
        return centerX;
    }

    public float getY(){
        return centerY;
    }

    public float getR(){
        return radius;
    }

    public void reverseYVelocity(){
        yVelocity = -yVelocity;
    }

    public void reverseXVelocity(){
        xVelocity = - xVelocity;
    }

    public void setRandomXVelocity(){
        Random generator = new Random();
        int answer = 7-generator.nextInt(8);

        if(answer == 0){
            reverseXVelocity();
        }
    }

    public void clearObstacleY(float y){
        centerY = y;
    }

    public void clearObstacleX(float x){
        centerX=x;
    }

    public void reset(int x, int y){
        centerX = x / 2;
        centerY= y - 350;
    }

}

