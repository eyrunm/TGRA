package com.ru.tgra.lab1;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;

public class Ball {
	public Point2D coords;
	public float radius;
	
	public Ball(Point2D p, float rad) {
		this.coords = p;
		this.radius = rad;
		
	}
	
	public void draw(int colorLoc) {
		
		Gdx.gl.glUniform4f(colorLoc, 0.7f, 0.2f, 0.4f, 1);  // pink
		Gdx.gl.glDrawArrays(GL20.GL_TRIANGLE_FAN, 0, 3);
		//Gdx.gl.glTranslatef(this.coords.getXFromPair(), this.coords.getYFromPair(), 0);
		/*
		Gdx.gl.glPushMatrix();

		Gdx.gl.glTranslatef(this.get_middle().x, this.get_middle().y, 0);
        
		Gdx.gl.glScalef(4.0f, 4.0f, 4.0f);
        Gdx.gl.glDrawArrays(GL20.GL_TRIANGLE_FAN, 4, 40);
        Gdx.gl.glPopMatrix();
        */
        
        
	}
}
