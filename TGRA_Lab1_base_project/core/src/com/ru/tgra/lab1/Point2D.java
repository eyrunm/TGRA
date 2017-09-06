package com.ru.tgra.lab1;

public class Point2D {
	private float x;
    private float y;
    public Point2D(float x,float y){
        this.x = x;
        this.y = y;
    }
    public float getXFromPair(){ return x; }
    public float getYFromPair(){ return y; }
    public void setXInPair(float x){ this.x = x; }
    public void setYInPair(float y){ this.y = y; }
}
