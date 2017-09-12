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
	
	private int worldWidth = 1024;
	private int worldHeight = 600;

	// Object with coordinates. Will draw itself circle style.
	Point2D theBall = new Point2D(300.0f,400.0f);
	// Object with coordinates. Will draw itself rectangle style.
	Point2D theBar = new Point2D(worldWidth/2, 10.0f);

	ArrayList<Point2D> boxes;
	
	// Movement of the ball should be along the vector. Changes of direction
	// are retrieved by manipulating this vector and changes of position are retrieved by
	// using it : [ positionX + deltaTime * speedX ]
	Vector2D speed = new Vector2D(150.0f, 150.0f);
	
	// a variable used to scale movements between computers/graphic cards so they will feel the same.
	private float deltaTime;
	
	public boolean rightWall;
	private boolean barHit;
	private boolean gameOver;
	private int ballSize = 10;
	private float barWidth = 80.0f;
	private float barHeight = 10.0f;
	
	@Override
	public void create () {

		String vertexShaderString;
		String fragmentShaderString;
		/*
		boxes = new ArrayList<Point2D>();
		*/
		rightWall = false;
		barHit = false;
		gameOver = false;	

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

		//COLOR IS SET HERE
		Gdx.gl.glUniform4f(colorLoc, 0.7f, 0.2f, 0, 1);

		Ball.create(positionLoc);
		Bar.create(positionLoc);
	}
	
	private void update() {
		
		// NOTE the bar has a vector, either (1,0) if going right or (-1,0) for left
		
		deltaTime = Gdx.graphics.getDeltaTime();

		// handle if the ball touches the edges
		if(theBall.getY() <= ballSize) {
			gameOver = true;
		}

		if(theBall.getX() >= worldWidth - ballSize) {
			rightWall = true;
		}
		// Ball radius is 10 and bar width is 80. 
		// Not enough for the edges of the ball touch bar, the ball should not bounce.
		if(theBall.getY() <= 30 && theBall.getY() > ballSize
				&& theBall.getX() <= theBar.getX()+40-10 
				&& theBall.getX() >= theBar.getX()-40+10) {
			barHit = true;
		}
		if(theBall.getX() <= 10) {
			rightWall = false;
		}
		if(theBall.getY() >= 590) {
			barHit = false;
		}
		
		if(rightWall) {
			float x = theBall.getX() - deltaTime * speed.x;
			theBall.setX(x);
		}
		else {
			float x = theBall.getX() + deltaTime * speed.x;
			theBall.setX(x);
		}
		
		if(barHit) {
			float y = theBall.getY() + deltaTime * speed.y;
			theBall.setY(y);
		}
		else {
			float y = theBall.getY() - deltaTime * speed.y;
			theBall.setY(y);
		}
		
		// handle if the bar touches the edges
		if(theBar.getX() >= 981) {
			theBar.setX(982);
		}

		if(theBar.getX() <= 46) {
			theBar.setX(45);
		}
		
		// moving the bar
		if(Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
			float x = theBar.getX() - deltaTime * 400;
			theBar.setX(x);
		}
		if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
			float x = theBar.getX() + deltaTime * 400;
			theBar.setX(x);
		}
		
		
	}
	
	private void display() {
		
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		Gdx.gl.glUniform4f(colorLoc, 0.9f, 0.8f, 0, 1);
		Gdx.gl20.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		
		// the ball
		clearModelMatrix();
		setModelMatrixTranslation(theBall.getX(), theBall.getY());
		setModelMatrixScale(ballSize, ballSize);
		Gdx.gl.glUniform4f(colorLoc, 0.9f, 0.4f, 0, 1);
		Ball.draw();
		
		// the bar
		clearModelMatrix();
		setModelMatrixTranslation(theBar.getX(), theBar.getY());
		setModelMatrixScale(barWidth, barHeight);
		Gdx.gl.glUniform4f(colorLoc, 0.9f, 0.4f, 0, 1);
		Bar.draw();

		/*
		 // BRICKS
		for(Point2D b : boxes) {
			setModelMatrixTranslation(b.getXFromPair(), b.getYFromPair());
			Gdx.gl.glUniform4f(colorLoc, 0.4f, 0.0f, 0.3f, 1); // purple
			Gdx.gl.glDrawArrays(GL20.GL_TRIANGLE_STRIP, 0, 4); // draw the rest in purple
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