package com.xinlan.imageeditlibrary.editimage.model;

import android.graphics.Paint;

public class DrawPath{
    private float last_x;
    private float last_y;
    private float x;
    private float y;
    private Paint paint;
    private int color;
    private float width;

    public DrawPath(float last_x,float last_y,float x,float y,Paint paint,int color,float width){
        this.last_x = last_x;
        this.last_y = last_y;
        this.x = x;
        this.y = y;
        this.paint = paint;
        this.color = color;
        this.width = width;

    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getLast_x() {
        return last_x;
    }

    public void setLast_x(float last_x) {
        this.last_x = last_x;
    }

    public float getLast_y() {
        return last_y;
    }

    public void setLast_y(float last_y) {
        this.last_y = last_y;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public Paint getPaint() {
        return paint;
    }

    public void setPaint(Paint paint) {
        this.paint = paint;
    }
}
