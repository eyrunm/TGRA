package com.ru.tgra.lab1;

import java.nio.FloatBuffer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;

public class Bar {
	
	public Point2D coords;
	
	public Bar (Point2D coord) {
		this.coords = coord;
	}
	
	public void draw(FloatBuffer vertexBuffer, int colorLoc) { 
		//Lab1Game.setModelMatrixTranslation(this.coords.getXFromPair(), this.coords.getYFromPair());
		Gdx.gl.glUniform4f(colorLoc, 0.7f, 0.2f, 0.4f, 1);  // pink
		Gdx.gl.glDrawArrays(GL20.GL_TRIANGLE_STRIP, 0, 4);
	}
}
