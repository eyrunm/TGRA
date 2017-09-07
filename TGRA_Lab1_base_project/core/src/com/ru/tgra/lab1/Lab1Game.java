package com.ru.tgra.lab1;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer.*;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import java.nio.FloatBuffer;
import java.util.ArrayList;

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

	// Hlutur á hnit. Munum láta hann teikna sig eins og hring.
	Point2D theBall = new Point2D(0,0);
	
	/*
	private float position_xb;
	private float position_yb;
	private float position_xc;
	private float position_yc;
	*/
	ArrayList<Point2D> boxes;
	
	public boolean rightWall;
	private boolean floor;
	private boolean gameOver;
	private int ballSize = 20;
	
	@Override
	public void create () {

		String vertexShaderString;
		String fragmentShaderString;
		/*
		boxes = new ArrayList<Point2D>();
		*/
		rightWall = false;
		floor = false;
		gameOver = false;
		
		//Point2D newPairTwo = new Point2D(position_xb, position_yb);
		//boxes.add(newPairTwo);
		

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
		
		// the window
		Gdx.gl.glClearColor(0.4f, 0.6f, 1.0f, 1.0f);
		//Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		//COLOR IS SET HERE
		Gdx.gl.glUniform4f(colorLoc, 0.7f, 0.2f, 0, 1);

		//VERTEX ARRAY IS FILLED HERE
		float[] array = {-10.0f, -10.0f,
						-10.0f, 10.0f,
						10.0f, -10.0f,
						10.0f, 10.0f};

		vertexBuffer = BufferUtils.newFloatBuffer(8);
		vertexBuffer.put(array);
		vertexBuffer.rewind();
		
		// where to point
		Gdx.gl.glVertexAttribPointer(positionLoc, 2, GL20.GL_FLOAT, false, 0, vertexBuffer);
		Ball.create(positionLoc);
	}
	
	private void update()
	{
		//Point2D newCoords = new Point2D(position_x, position_y);

		// handle if the ball touches the edges

		if(theBall.getXFromPair() >= 1014) {
			rightWall = true;
		}
		if(theBall.getYFromPair() <= 10) {
			floor = true;
		}
		if(theBall.getXFromPair() <= 10) {
			rightWall = false;
		}
		if(theBall.getYFromPair() >= 590) {
			floor = false;
		}
		
		if(rightWall) {
			float x = theBall.getXFromPair();
			x -= 2;
			theBall.setXInPair(x);
			//boxes.set(0, newCoords);
		}
		else {
			float x = theBall.getXFromPair();
			x += 2;
			theBall.setXInPair(x);
			//boxes.set(0, newCoords);
		}
		
		if(floor) {
			float y = theBall.getYFromPair();
			y += 2;
			theBall.setYInPair(y);
			//boxes.set(0, newCoords);
		}
		else {
			float y = theBall.getYFromPair();
			y -= 2;
			theBall.setYInPair(y);
			//boxes.set(0, newCoords);
		}
		/*
		
		// moving box no.2
		if(Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
			position_xb -= 2;
			boxes.set(1, newCoordsB);
		}
		if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
			position_xb += 2;
			boxes.set(1, newCoordsB);
		}

		*/
		
		
	}
	
	private void display() {
		
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		Gdx.gl.glUniform4f(colorLoc, 0.9f, 0.8f, 0, 1);
		Gdx.gl20.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		
		clearModelMatrix();
		setModelMatrixTranslation(theBall.getXFromPair(), theBall.getYFromPair());
		setModelMatrixScale(ballSize, ballSize);
		Gdx.gl.glUniform4f(colorLoc, 0.9f, 0.4f, 0, 1);
		Ball.draw();

		
		//setModelMatrixTranslation(theBall.getXFromPair(), theBall.getYFromPair());
			
		int count = 0;
		/*
		for(Point2D b : boxes) { // go through all boxes created
			if(count == 0) {	// first box
				setModelMatrixTranslation(b.getXFromPair(), b.getYFromPair());
				Gdx.gl.glUniform4f(colorLoc, 0.7f, 0.2f, 0.4f, 1);  // pink
				Gdx.gl.glDrawArrays(GL20.GL_TRIANGLE_STRIP, 0, 4);	// draw first box
			}
			if(count == 1) {	// second box
				setModelMatrixTranslation(b.getXFromPair(), b.getYFromPair());
				Gdx.gl.glUniform4f(colorLoc, 0.1f, 0.6f, 0.1f, 1); // green
				
				if(gameOver) {
					Gdx.gl.glUniform4f(colorLoc, 1.0f, 0.0f, 0, 1);	// red if game over
				}

				Gdx.gl.glDrawArrays(GL20.GL_TRIANGLE_STRIP, 0, 4);	//draw second box
			}
			else if(count > 1) { // all the other boxes
				setModelMatrixTranslation(b.getXFromPair(), b.getYFromPair());
				Gdx.gl.glUniform4f(colorLoc, 0.4f, 0.0f, 0.3f, 1); // purple
				Gdx.gl.glDrawArrays(GL20.GL_TRIANGLE_STRIP, 0, 4); // draw the rest in purple
			}
			count++;
		}
		*/

	}

	@Override
	public void render () {		
		//put the code inside the update and display methods, depending on the nature of the code
		update();
		display();
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