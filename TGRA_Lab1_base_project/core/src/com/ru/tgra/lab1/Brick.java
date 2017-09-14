package com.ru.tgra.lab1;

import java.nio.FloatBuffer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.utils.BufferUtils;

public class Brick {
	public Point2D coords;
	public boolean isHit;
	
	private static FloatBuffer vertexBuffer;
	public static int vertexPointer;
	
	public Brick (Point2D p) {
		isHit = false;
		coords = p;
	}

	public static void create(int vertexPointer) {
		
		Brick.vertexPointer = vertexPointer;
		//VERTEX ARRAY IS FILLED HERE
		float[] array = {-0.5f, -0.5f,
						-0.5f, 0.5f,
						0.5f, -0.5f,
						0.5f, 0.5f};

		vertexBuffer = BufferUtils.newFloatBuffer(8);
		vertexBuffer.put(array);
		vertexBuffer.rewind();
	}
	
	public static void draw() { 
		
		Gdx.gl.glVertexAttribPointer(vertexPointer, 2, GL20.GL_FLOAT, false, 0, vertexBuffer);
		Gdx.gl.glDrawArrays(GL20.GL_TRIANGLE_STRIP, 0, 4);
	}
}
