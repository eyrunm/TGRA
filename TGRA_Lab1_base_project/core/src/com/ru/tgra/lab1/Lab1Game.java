package com.ru.tgra.lab1;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;

import java.nio.FloatBuffer;

import com.badlogic.gdx.utils.BufferUtils;

public class Lab1Game extends ApplicationAdapter {
	
	private FloatBuffer vertexBuffer;

	private FloatBuffer modelMatrix;
	private FloatBuffer projectionMatrix;

	private int renderingProgramID;
	private int vertexShaderID;
	private int fragmentShaderID;

	private int positionLoc;

	private int modelMatrixLoc;
	private int projectionMatrixLoc;

	private int colorLoc;
	
	private float position_x;
	private float position_y;
	
	private float posX;
	private float posY;
	
	private int speedX;
	private int speedY;
	
	private int height = 768;
	private int width = 1024;
	
	private float clickX;
	private float clickY;
	
	private float xList[] = new float[100];
	private float yList[] = new float[100];

	private int count;
	

	@Override
	public void create () {

		String vertexShaderString;
		String fragmentShaderString;
		position_x = 300;
		position_y = 300;
		posX = 600;
		posY = 360;
		
		clickX = -50;
		clickY = -50;
		count = 0;
		speedX = 3;
		speedY = 2;

		vertexShaderString = Gdx.files.internal("shaders/simple2D.vert").readString();
		fragmentShaderString =  Gdx.files.internal("shaders/simple2D.frag").readString();

		vertexShaderID = Gdx.gl.glCreateShader(GL20.GL_VERTEX_SHADER);
		fragmentShaderID = Gdx.gl.glCreateShader(GL20.GL_FRAGMENT_SHADER);
	
		Gdx.gl.glShaderSource(vertexShaderID, vertexShaderString);
		Gdx.gl.glShaderSource(fragmentShaderID, fragmentShaderString);
	
		Gdx.gl.glCompileShader(vertexShaderID);
		Gdx.gl.glCompileShader(fragmentShaderID);

		renderingProgramID = Gdx.gl.glCreateProgram();
	
		Gdx.gl.glAttachShader(renderingProgramID, vertexShaderID);
		Gdx.gl.glAttachShader(renderingProgramID, fragmentShaderID);
	
		Gdx.gl.glLinkProgram(renderingProgramID);

		positionLoc				= Gdx.gl.glGetAttribLocation(renderingProgramID, "a_position");
		Gdx.gl.glEnableVertexAttribArray(positionLoc);

		modelMatrixLoc			= Gdx.gl.glGetUniformLocation(renderingProgramID, "u_modelMatrix");
		projectionMatrixLoc	= Gdx.gl.glGetUniformLocation(renderingProgramID, "u_projectionMatrix");

		colorLoc				= Gdx.gl.glGetUniformLocation(renderingProgramID, "u_color");

		Gdx.gl.glUseProgram(renderingProgramID);

		float[] pm = new float[16];

		pm[0] = 2.0f / Gdx.graphics.getWidth(); pm[4] = 0.0f; pm[8] = 0.0f; pm[12] = -1.0f;
		pm[1] = 0.0f; pm[5] = 2.0f / Gdx.graphics.getHeight(); pm[9] = 0.0f; pm[13] = -1.0f;
		pm[2] = 0.0f; pm[6] = 0.0f; pm[10] = 1.0f; pm[14] = 0.0f;
		pm[3] = 0.0f; pm[7] = 0.0f; pm[11] = 0.0f; pm[15] = 1.0f;

		projectionMatrix = BufferUtils.newFloatBuffer(16);
		projectionMatrix.put(pm);
		projectionMatrix.rewind();
		Gdx.gl.glUniformMatrix4fv(projectionMatrixLoc, 1, false, projectionMatrix);


		float[] mm = new float[16];

		mm[0] = 1.0f; mm[4] = 0.0f; mm[8] = 0.0f; mm[12] = 0.0f;
		mm[1] = 0.0f; mm[5] = 1.0f; mm[9] = 0.0f; mm[13] = 0.0f;
		mm[2] = 0.0f; mm[6] = 0.0f; mm[10] = 1.0f; mm[14] = 0.0f;
		mm[3] = 0.0f; mm[7] = 0.0f; mm[11] = 0.0f; mm[15] = 1.0f;

		modelMatrix = BufferUtils.newFloatBuffer(16);
		modelMatrix.put(mm);
		modelMatrix.rewind();

		Gdx.gl.glUniformMatrix4fv(modelMatrixLoc, 1, false, modelMatrix);

		//COLOR IS SET HERE
		Gdx.gl.glUniform4f(colorLoc, 0.7f, 0.2f, 0, 1);


		//VERTEX ARRAY IS FILLED HERE
		float[] array = {-50.0f, -50.0f,
						-50.0f, 50.0f,
						50.0f, -50.0f,
						50.0f, 50.0f};

		vertexBuffer = BufferUtils.newFloatBuffer(8);
		vertexBuffer.put(array);
		vertexBuffer.rewind();
	}
	
	private void update()
	{
		if(Gdx.input.justTouched())
		{	//do mouse/touch input stuff
			clickX = Gdx.input.getX();    
			clickY = height - Gdx.input.getY();	
			
			xList[count] = clickX;
			yList[count] = clickY;
			
			count++;
		}

		//do all updates to the game
	}
	
	private void display()
	{
		//do all actual drawing and rendering here
	}

	@Override
	public void render () {
		
		//put the code inside the update and display methods, depending on the nature of the code
		
		update();
		display();
		
		Gdx.gl.glClearColor(0.4f, 0.6f, 1.0f, 1.0f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		
		//Draw a triangle
		Gdx.gl.glUniform4f(colorLoc, 1f, 1f, 1f, 1f); //the color of the triangle

		vertexBuffer.put(0, 100.0f); //x coordinate 1
		vertexBuffer.put(1, 100.0f); //y coordinate 1
		vertexBuffer.put(2, 150.0f); //x coordinate 2
		vertexBuffer.put(3, 200.0f); //y coordinate 2
		vertexBuffer.put(4, 200.0f); //x coordinate 3
		vertexBuffer.put(5, 100.0f); //y coordinate 3
			//display the triangle
		Gdx.gl.glVertexAttribPointer(positionLoc, 2, GL20.GL_FLOAT, false, 0, vertexBuffer);
		Gdx.gl.glDrawArrays(GL20.GL_TRIANGLES, 0, 3);
		//ends here
		
		Gdx.gl.glUniform4f(colorLoc, 0.1f, 0.1f, 0.f, 1);
		// draw a smaller square
			vertexBuffer.put(0, posX - 30.0f); //x coordinate 1
			vertexBuffer.put(1, posY - 30.0f); //y coordinate 1
			vertexBuffer.put(2, posX - 30.0f); //x coordinate 2
			vertexBuffer.put(3, posY + 30.0f); //y coordinate 2
			vertexBuffer.put(4, posX + 30.0f); //x coordinate 3
			vertexBuffer.put(5, posY - 30.0f); //y coordinate 3
			vertexBuffer.put(6, posX + 30.0f); //x coordinate 4
			vertexBuffer.put(7, posY + 30.0f); //y coordinate 4
			//display the square
			Gdx.gl.glVertexAttribPointer(positionLoc, 2, GL20.GL_FLOAT, false, 0, vertexBuffer);
			Gdx.gl.glDrawArrays(GL20.GL_TRIANGLE_STRIP, 0, 4);
				
			if(Gdx.input.isKeyPressed(Input.Keys.LEFT)&& posX - 30.0f >= 0) {
				posX -= 5;
			}
			if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)&& posX + 30.0f <= width) {
				posX += 5;
			}
			if(Gdx.input.isKeyPressed(Input.Keys.UP) && posY + 30.0f <= height) {
				posY += 5;
			}
			if(Gdx.input.isKeyPressed(Input.Keys.DOWN) && posY - 30.0f >= 0) {
				posY -= 5;
			}	
			
			Gdx.gl.glUniform4f(colorLoc, 0.6f, 0.3f, 1f, 1);
			for(int i = 0; i < count; i++)
			{	//draw the click squares
				vertexBuffer.put(0, xList[i] - 60.0f); //x coordinate 1
				vertexBuffer.put(1, yList[i] - 60.0f); //y coordinate 1
				vertexBuffer.put(2, xList[i] - 60.0f); //x coordinate 2
				vertexBuffer.put(3, yList[i] + 60.0f); //y coordinate 2
				vertexBuffer.put(4, xList[i] + 60.0f); //x coordinate 3
				vertexBuffer.put(5, yList[i] - 60.0f); //y coordinate 3
				vertexBuffer.put(6, xList[i] + 60.0f); //x coordinate 4
				vertexBuffer.put(7, yList[i] + 60.0f); //y coordinate 4
				//display the square
				Gdx.gl.glVertexAttribPointer(positionLoc, 2, GL20.GL_FLOAT, false, 0, vertexBuffer);
				Gdx.gl.glDrawArrays(GL20.GL_TRIANGLE_STRIP, 0, 4);
			}
			
			Gdx.gl.glUniform4f(colorLoc, 1f, 0.5f, 0.5f, 0.5f);
			// draw a square
			vertexBuffer.put(0, position_x - 50.0f); //x coordinate 1
			vertexBuffer.put(1, position_y - 50.0f); //y coordinate 1
			vertexBuffer.put(2, position_x - 50.0f); //x coordinate 2
			vertexBuffer.put(3, position_y + 50.0f); //y coordinate 2
			vertexBuffer.put(4, position_x + 50.0f); //x coordinate 3
			vertexBuffer.put(5, position_y - 50.0f); //y coordinate 3
			vertexBuffer.put(6, position_x + 50.0f); //x coordinate 4
			vertexBuffer.put(7, position_y + 50.0f); //y coordinate 4
				//display the square
			Gdx.gl.glVertexAttribPointer(positionLoc, 2, GL20.GL_FLOAT, false, 0, vertexBuffer);
			Gdx.gl.glDrawArrays(GL20.GL_TRIANGLE_STRIP, 0, 4);
		
			position_x += speedX;
			position_y += speedY;
			
			if(position_x + 50.0f > width || position_x - 50.0f < 0 ) {
				speedX = -speedX;
			}
			if(position_y + 50.0f > height || position_y - 50.0f < 0) {
				speedY = -speedY;
			}
	}


	private void clearModelMatrix()
	{
		modelMatrix.put(0, 1.0f);
		modelMatrix.put(1, 0.0f);
		modelMatrix.put(2, 0.0f);
		modelMatrix.put(3, 0.0f);
		modelMatrix.put(4, 0.0f);
		modelMatrix.put(5, 1.0f);
		modelMatrix.put(6, 0.0f);
		modelMatrix.put(7, 0.0f);
		modelMatrix.put(8, 0.0f);
		modelMatrix.put(9, 0.0f);
		modelMatrix.put(10, 1.0f);
		modelMatrix.put(11, 0.0f);
		modelMatrix.put(12, 0.0f);
		modelMatrix.put(13, 0.0f);
		modelMatrix.put(14, 0.0f);
		modelMatrix.put(15, 1.0f);

		Gdx.gl.glUniformMatrix4fv(modelMatrixLoc, 1, false, modelMatrix);
	}
	private void setModelMatrixTranslation(float xTranslate, float yTranslate)
	{
		modelMatrix.put(12, xTranslate);
		modelMatrix.put(13, yTranslate);

		Gdx.gl.glUniformMatrix4fv(modelMatrixLoc, 1, false, modelMatrix);
	}
	private void setModelMatrixScale(float xScale, float yScale)
	{
		modelMatrix.put(0, xScale);
		modelMatrix.put(5, yScale);

		Gdx.gl.glUniformMatrix4fv(modelMatrixLoc, 1, false, modelMatrix);
	}
}