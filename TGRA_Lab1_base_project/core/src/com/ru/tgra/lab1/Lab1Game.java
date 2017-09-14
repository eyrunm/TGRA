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
	//List of objects with coordinates. Will draw themself rectangle style
	public static ArrayList<Brick> bricks;
	
	// Movement of the ball should be along the vector. Changes of direction
	// are retrieved by manipulating this vector and changes of position are retrieved by
	// using it : [ positionX + deltaTime * speedX ]
	Vector2D speed = new Vector2D(180.0f, 180.0f);
	
	// a variable used to scale movements between computers/graphic cards so they will feel the same.
	private float deltaTime;
	
	public boolean rightWall;
	private boolean floorHit;
	private boolean barCollision;
	private boolean gameOver;
	private boolean gameWon;
	private int gameWonCounter = 200;
	private int gameOverCounter = 200;
	private int endCounter = 0;
	private int ballSize = 10;
	private float barWidth = 80.0f;
	private float barHeight = 10.0f;
	private float brickWidth = 85.0f;
	private float brickHeight = 35.0f;
	public int brickHits = 0;
	
	@Override
	public void create () {

		String vertexShaderString;
		String fragmentShaderString;		
	
		rightWall = false;
		floorHit = false;
		gameOver = false;
		gameWon = false;
		barCollision = true;

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
		Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

		//COLOR IS SET HERE
		Gdx.gl.glUniform4f(colorLoc, 0.7f, 0.2f, 0, 1);
		
		bricks = new ArrayList<Brick>();
		
		int w = 85;
		int h = 565;
		for(int y = 0; y < 3; y++) {
			w = 85;
			for(int x = 0; x < 10; x++) {
				bricks.add(new Brick(new Point2D(w,h)));
				w += 95;
			}
			h -= 45;
		}

		Ball.create(positionLoc);
		Bar.create(positionLoc);
		Brick.create(positionLoc);

	}
	
	private void update() {
		
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
		if(theBall.getY() <= 20 && theBall.getY() > ballSize
				&& theBall.getX() <= theBar.getX()+40 
				&& theBall.getX() >= theBar.getX()-40) {
			floorHit = true;
			barCollision = true;
		}
		if(theBall.getX() <= ballSize) {
			rightWall = false;
		}
		if(theBall.getY() >= worldHeight - ballSize) {		
			floorHit = false;
		}
		
		for(int i = bricks.size() -1 ; i >= 0; i--) {
			if(theBall.getY()+10 >= 430 && bricks.get(i).isHit == false
					&& theBall.getY()+10 >= bricks.get(i).coords.getY() - 17.5
					&& theBall.getY()-10 <= bricks.get(i).coords.getY() + 17.5
					&& theBall.getX()-10 <= bricks.get(i).coords.getX() + 42.5
					&& theBall.getX()+10 >= bricks.get(i).coords.getX() - 42.5) {
				// hide the brick thats hit
				bricks.get(i).isHit = true;
				brickHits++;
				brickCollision(bricks.get(i));
			}
		}

		if(rightWall) {
			float s = speed.x;
			if(speed.x < 0) s *= -1;
			float x = theBall.getX() - deltaTime * s;
			theBall.setX(x);
		}
		else {
			float s = speed.x;
			if(speed.x < 0) s *= -1;
			float x = theBall.getX() + deltaTime * s;
			theBall.setX(x);
		}
		
		if(floorHit) {
			if(barCollision) handleBar();
				
			float s = speed.y;
			if(speed.y < 0) s *= -1;
			float y = theBall.getY() + deltaTime * s;
			theBall.setY(y);
		}
		else {
			float s = speed.y;
			if(speed.y < 0) s *= -1;
			float y = theBall.getY() - deltaTime * s;
			theBall.setY(y);
		}

		// Do stuff when game is won
		if(brickHits >= 30) {
			gameWon = true;
			gameWon();
		}
		
		// handle if the bar touches the edges
		if(theBar.getX() >= 980) {
			theBar.setX(980);
		}

		if(theBar.getX() <= 44) {
			theBar.setX(44);
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
		if(gameOver) {
			//gameOver();
			gameWon();
		}
		else {
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		}
		
		
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

		clearModelMatrix();
		for(Brick b : bricks) {
			if(b.isHit == false) {
				setModelMatrixTranslation(b.coords.getX(), b.coords.getY());
				setModelMatrixScale(brickWidth, brickHeight);
				Gdx.gl.glUniform4f(colorLoc, 1f, 66/255, 87/255, 255/244);
				Brick.draw();
			}
		}
	}

	@Override
	public void render () {		
		//put the code inside the update and display methods, depending on the nature of the code
		update();
		display();
	}
	
	private void handleBar() {
		// When the ball hits the bar the position on the paddle (and the angle the ball is traveling at 
		// before the bounce?) should control the angle the ball travels at after the bounce.
		// Find the ball's overall speed, pythagorean theorem. 
		// Use this value  to keep speedY proportional to speedX
    	double speedXY = Math.sqrt(speed.x*speed.x + speed.y*speed.y);
		
		// find the position of the ball relative to the center of the bar (-1 to 1)
		float newPosX = ((theBall.getX() - theBar.getX()) / (barWidth/2));

		// find the direction X value using newPosX and some float indicating effect percentage.
		speed.x = (float) (speedXY * newPosX * 0.9);
		
		// update speedY to keep the overall speed the same.
		speed.y = (float) (Math.sqrt(speedXY*speedXY - speed.x*speed.x));
		barCollision = false;
	}
	
	private void brickCollision(Brick b) {
		if(speed.x < 0) {			// DOWN-LEFT
			rightWall = true;
			if(speed.y < 0) {
				// ball touches the bricks right side,
				// the ball center is on the right side of brick on collision.
				if(theBall.getX()+10 >= b.coords.getX() + 17.5) {
					rightWall = false;
					theBall.setX(theBall.getX()-6);
				}
				// ball hits the bricks top
				floorHit = true;
				theBall.setY(theBall.getY()+6);
				
			}						// UP-LEFT
			if(speed.y >= 0) {
				if(theBall.getX()+10 >= b.coords.getX() + 17.5) {
					rightWall = false;
					theBall.setX(theBall.getX()-6);
				}
				floorHit = false;
				theBall.setY(theBall.getY()-6);
			}
		}
		if(speed.x >= 0) {			// UP-RIGHT
			rightWall = false;
			if(speed.y >= 0) {
				// the ball touched the bricks bottom
				if(theBall.getX()+10 >= b.coords.getX() - 17.5) {
					floorHit = false;
					theBall.setY(theBall.getY()-6);
				}
				// the ball touched the bricks left side
				rightWall = true;	
				theBall.setX(theBall.getX()+6);
			}
			if(speed.y < 0) {		// DOWN-RIGHT
				if(theBall.getX()+10 <= b.coords.getX() - 17.5) {
					rightWall = true;
					theBall.setX(theBall.getX()+6);
				}
				floorHit = true;
				theBall.setY(theBall.getY()+6);
			}
		}
	}
	
	private void gameOver() {
		if(gameOverCounter < 20) {
			// the window
			Gdx.gl.glClearColor(0.9f, 0.1f, 0.0f, 1.0f); // RED
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
			gameOverCounter++;
		}
		if(gameOverCounter >= 20) {
			// the window
			Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f); // BLACK
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
			gameOverCounter++;
		}
		if(gameOverCounter >= 40) {
			gameOverCounter = 0;
			endCounter++;
		}
		if(endCounter == 8) {
			endCounter = 0;
			gameOver = false;
			// Object with coordinates. Will draw itself circle style.
			theBall.setX(300.0f);
			theBall.setY(400.0f);
			theBar.setX(worldWidth/2);
			theBar.setY(10.0f);
			speed.x = 150.0f;
			speed.y = 150.0f;
			rightWall = false;
			floorHit = false;
			gameOver = false;	
			barCollision = true;
			brickHits = 0;
			for(Brick b : bricks) {
				b.isHit = false;
			}
		}
	}
	
	private void gameWon() {
		
		bricks = new ArrayList<Brick>();
		Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f); // BLACK
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		int w = 85;
		int h = 565;
		for(int y = 0; y < 10; y++) {
			w = 85;
			for(int x = 0; x < 9; x++) {
				bricks.add(new Brick(new Point2D(w,h)));
				w += 95;
			}
			h -= 45;
		}
		
		bricks.get(20).isHit=true;
		bricks.get(24).isHit=true;
		bricks.get(40).isHit=true;
		bricks.get(46).isHit=true;
		bricks.get(52).isHit=true;
		bricks.get(56).isHit=true;
		bricks.get(60).isHit=true;
		bricks.get(66).isHit=true;
		bricks.get(68).isHit=true;
		bricks.get(76).isHit=true;

		clearModelMatrix();
		for(Brick b : bricks) {
			if(b.isHit == false) {
				setModelMatrixTranslation(b.coords.getX(), b.coords.getY());
				setModelMatrixScale(brickWidth, brickHeight);
				Gdx.gl.glUniform4f(colorLoc, 1f, 66/255, 87/255, 255/244);
				Brick.draw();
			}
		}
				
		if(gameWonCounter < 20) {
			// the window
			//Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f); // BLACK
			//Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
			gameWonCounter++;
		}
		if(gameWonCounter >= 20) {
			// the window
			//Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f); // BLACK
			//Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
			gameWonCounter++;
		}
		if(gameWonCounter >= 40) {
			gameWonCounter = 0;
			endCounter++;
		}
		
		if(endCounter == 8) {
			endCounter = 0;
			theBall.setX(300.0f);
			theBall.setY(400.0f);
			theBar.setX(worldWidth/2);
			theBar.setY(10.0f);
			speed.x = 150.0f;
			speed.y = 150.0f;
			rightWall = false;
			floorHit = false;
			gameOver = false;	
			barCollision = true;
			bricks = new ArrayList<Brick>();
			
			w = 85;
			h = 565;
			for(int y = 0; y < 3; y++) {
				w = 85;
				for(int x = 0; x < 10; x++) {
					bricks.add(new Brick(new Point2D(w,h)));
					w += 95;
				}
				h -= 45;
			}
			for(Brick b : bricks) {
				b.isHit = false;
			}
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