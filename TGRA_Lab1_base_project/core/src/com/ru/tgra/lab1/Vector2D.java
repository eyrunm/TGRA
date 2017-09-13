package com.ru.tgra.lab1;

public class Vector2D {
	float x,y;
	
	public Vector2D (float x,float y){
		this.x =x;
		this.y=y;
	}
	
	public float dotProduct (Vector2D v1,Vector2D v2) {

		return (v1.x*v2.x)+(v1.y*v2.y);
	}

}
