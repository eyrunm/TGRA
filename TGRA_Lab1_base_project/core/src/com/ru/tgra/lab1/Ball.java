package com.ru.tgra.lab1;

import java.nio.FloatBuffer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.utils.BufferUtils;

public class Ball {
	
	private static FloatBuffer vertexBuffer;
	private static int vertexPointer;
	private static int verticesPerCircle = 50;

	public static void create(int vertexPointer) {
		Ball.vertexPointer = vertexPointer;
		float[] array = new float[2 * verticesPerCircle];
		int index = 0;
		// VERTEX ARRAY IS FILLED HERE
		for (float i = 0; i < 2 * Math.PI; i += (2 * Math.PI) / (verticesPerCircle / 2)) {
			array[index] = (float) (Math.cos(i));
			index++;
			array[index] = (float) (Math.sin(i));
			index++;
		}

		vertexBuffer = BufferUtils.newFloatBuffer(2 * verticesPerCircle);
		vertexBuffer.put(array);
		vertexBuffer.rewind();
	}

	public static void draw() {

		Gdx.gl.glVertexAttribPointer(vertexPointer, 2, GL20.GL_FLOAT, false, 0, vertexBuffer);

		Gdx.gl.glDrawArrays(GL20.GL_TRIANGLE_FAN, 0, verticesPerCircle);

	}
}
