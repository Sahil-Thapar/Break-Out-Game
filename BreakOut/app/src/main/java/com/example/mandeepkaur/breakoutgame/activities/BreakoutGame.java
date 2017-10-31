package com.example.mandeepkaur.breakoutgame.activities;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;



import android.widget.ImageView;
import android.widget.LinearLayout;
import android.view.ViewGroup.LayoutParams;
import android.widget.SeekBar;
import android.widget.Toast;

import com.example.mandeepkaur.breakoutgame.models.Ball;
import com.example.mandeepkaur.breakoutgame.models.Brick;
import com.example.mandeepkaur.breakoutgame.models.ScoreDataModel;
import com.example.mandeepkaur.breakoutgame.utilities.FileHandler;
import com.example.mandeepkaur.breakoutgame.models.Paddle;
import com.example.mandeepkaur.breakoutgame.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import static java.lang.Math.abs;

public class BreakoutGame extends AppCompatActivity {

    BreakoutView breakoutView;
    LayoutParams params;
    Button playButton, pauseButton;
    ImageView imageView;
    SeekBar ballSpeedSeekbar;
    public static long fps = 50;

    int firstTimeRun=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        breakoutView = new BreakoutView(this);
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.custom_toolbar, layout, false);
        layout.addView(v);
        layout.addView(breakoutView);


        params = layout.getLayoutParams();
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int screenHeight = metrics.heightPixels;
        int screenWidth = metrics.widthPixels;

        setContentView(layout);

        ballSpeedSeekbar=(SeekBar)findViewById(R.id.seekBar);
        imageView = (ImageView)findViewById(R.id.imageView);
        playButton=(Button)findViewById(R.id.playButton);
        pauseButton=(Button)findViewById(R.id.pauseButton);
        playButton.setVisibility(View.INVISIBLE);


        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                breakoutView.pause();
                startActivity(new Intent(getApplicationContext(), HallOfFame.class));
            }
        });

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playButton.setVisibility(View.INVISIBLE);
                pauseButton.setVisibility(View.VISIBLE);
                breakoutView.resume();
            }
        });

        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pauseButton.setVisibility(View.INVISIBLE);
                playButton.setVisibility(View.VISIBLE);
                breakoutView.pause();
            }
        });

        ballSpeedSeekbar.setProgress(50);
        ballSpeedSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChanged = 0;

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChanged = progress;
                fps = (99 - progressChanged);
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                fps = (99 - progressChanged);
                Toast.makeText(getApplicationContext(), "Speed set to:" + progressChanged,
                        Toast.LENGTH_SHORT).show();
            }
        });

    }

    class BreakoutView extends SurfaceView implements Runnable,SensorEventListener {

        FileHandler fileHandler = new FileHandler(getApplicationContext());
        ScoreDataModel scoreDataModel = new ScoreDataModel();

        Thread gameThread = null;

        SurfaceHolder ourHolder;

        volatile boolean playing;


        boolean paused = true;

        Canvas canvas;
        Paint paint;

        private Object pauseLock;
        private boolean mPause;

        int screenX;
        int screenY;

        private SensorManager senSensorManager;
        private Sensor senAccelerometer;
        private long lastUpdate = -1;
        private float last_x, last_y, last_z;
        private static final int SHAKE_THRESHOLD = 800;

        int maxScore=0;

        Paddle paddle;

        Ball ball;

        long startTime;
        long timer;
        int firstTime=0;

        float initialTouch=0;

        Brick[] bricks = new Brick[200];
        int numBricks = 0;

        SoundPool soundPool;
        int beep1ID = -1;
        int beep2ID = -1;
        int beep3ID = -1;
        int loseLifeID = -1;
        int explodeID = -1;

        int score = 0;

        public BreakoutView(Context context) {

            super(context);

            ourHolder = getHolder();
            paint = new Paint();

            Display display = getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);

            screenX = size.x;
            screenY = size.y;

            paddle = new Paddle(screenX, screenY);

            ball = new Ball(screenX, screenY);

            pauseLock=new Object();
            mPause=false;

            soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC,0);

            try{
                AssetManager assetManager = context.getAssets();
                AssetFileDescriptor descriptor;

                descriptor = assetManager.openFd("beep1.ogg");
                beep1ID = soundPool.load(descriptor, 0);

                descriptor = assetManager.openFd("beep2.ogg");
                beep2ID = soundPool.load(descriptor, 0);

                descriptor = assetManager.openFd("beep3.ogg");
                beep3ID = soundPool.load(descriptor, 0);

                descriptor = assetManager.openFd("loseLife.ogg");
                loseLifeID = soundPool.load(descriptor, 0);

                descriptor = assetManager.openFd("explode.ogg");
                explodeID = soundPool.load(descriptor, 0);

            }catch(IOException e){
                Log.e("error", "failed to load sound files");
            }


            senSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
            senAccelerometer = senSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            senSensorManager.registerListener(this, senAccelerometer , SensorManager.SENSOR_DELAY_NORMAL);


            createBricksAndRestart();

        }

        public int getScore(){
            return score;
        }

        public int getTime(){
            return (int)timer;
        }

        public void createBricksAndRestart(){

             ball.reset(screenX, screenY);
             paddle.reset(screenX,screenY);
             int brickWidth = screenX ;
             int brickHeight = screenX/12 ;

             startTime=0;
             timer=0;
             firstTime=0;
             maxScore=0;

             Random r=new Random();

             numBricks = 0;
             for(int column = 0; column < 8; column ++ ){
                 for(int row = 2; row < 5; row ++ ){
                     bricks[numBricks] = new Brick(row, column, brickWidth/(9-row), brickHeight);
                     if(bricks[numBricks].getRect().left>0 && bricks[numBricks].getRect().right<screenX)
                     {
                         int temp=0;

                     while(temp==0)
                     {
                         temp=r.nextInt(7-1);
                     }
                     while(numBricks>=3 && bricks[numBricks-3].type==temp)
                     {
                         temp=r.nextInt(7-1);
                     }
                     maxScore+=temp;
                     bricks[numBricks].type=temp;
                     bricks[numBricks].hits=bricks[numBricks].type;
                     numBricks ++;
                     }
                 }

             }

             score=0;
             paused=true;

         }

        @Override
        public void run() {
            while (playing) {
                synchronized (pauseLock) {
                    while (mPause) {
                        try {
                            pauseLock.wait();
                            } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }

                if(!paused){

                    if(firstTime==0)
                    {
                        startTime=System.currentTimeMillis();
                        firstTime=1;
                    }
                    timer=System.currentTimeMillis()-startTime;
                    update();
                }

                draw();

            }

        }

        public void update() {

            paddle.update(fps);

            ball.update(fps);

            for(int i = 0; i < numBricks; i++){

                if (bricks[i].getVisibility()){

                    if(intersects(bricks[i].getRect(), ball)) {
                        bricks[i].hits--;
                        if(bricks[i].hits==0) {
                            bricks[i].setInvisible();
                        }
                        ball.reverseYVelocity();
                        score = score + 10;
                        soundPool.play(explodeID, 1, 1, 0, 0, 1);
                    }
                }
            }

            if(intersects(paddle.getRect(), ball)) {
                ball.setRandomXVelocity();
                ball.reverseYVelocity();
                ball.clearObstacleY(paddle.getRect().top - ball.getR() - 2);
                soundPool.play(beep1ID, 1, 1, 0, 0, 1);
            }

            if(ball.getY()+ball.getR() > screenY){
                ball.reverseYVelocity();
                ball.clearObstacleY(screenY - ball.getR() - 2);
                soundPool.play(loseLifeID, 1, 1, 0, 0, 1);
                scoreDataModel.setScore(Integer.toString(getScore()));
                scoreDataModel.setTime(Integer.toString(getTime()));
                if(fileHandler.isInTopTen(scoreDataModel) == 1) {
                    ArrayList<Integer> arrayList = new ArrayList<>();
                    arrayList.add(getScore());
                    arrayList.add(getTime());
                    createBricksAndRestart();
                    startActivity(new Intent(getApplicationContext(), ScoreEntry.class).putIntegerArrayListExtra("caller", arrayList));
                }else {
                    createBricksAndRestart();
                }

            }
            if(ball.getY()-ball.getR() < 0){
                ball.reverseYVelocity();
                ball.clearObstacleY(ball.getR()+12);
                soundPool.play(beep2ID, 1, 1, 0, 0, 1);
            }

            if(ball.getX()-ball.getR() < 0){
                ball.reverseXVelocity();
                ball.clearObstacleX(ball.getR() + 2);
                soundPool.play(beep3ID, 1, 1, 0, 0, 1);
            }

            if(ball.getX()+ball.getR() > screenX - 10){
                ball.reverseXVelocity();
                ball.clearObstacleX(screenX - ball.getR() - 22);
                soundPool.play(beep3ID, 1, 1, 0, 0, 1);
            }

            if(score==maxScore*10){
                paused = true;

                ArrayList<Integer> arrayList = new ArrayList<>();
                arrayList.add(getScore());
                arrayList.add(getTime());
                createBricksAndRestart();

                startActivity(new Intent(getApplicationContext(), ScoreEntry.class).putIntegerArrayListExtra("caller", arrayList));

            }

        }

        public void draw() {

            if (ourHolder.getSurface().isValid()) {
                canvas = ourHolder.lockCanvas();

                canvas.drawColor(Color.rgb( 50, 50, 50));

                paint.setColor(Color.argb(255, 255 , 149 , 124));

                canvas.drawRect(paddle.getRect(), paint);


                paint.setColor(Color.argb(255, 255, 255, 255));

                canvas.drawCircle(ball.centerX, ball.centerY, ball.radius, paint);


                paint.setColor(Color.argb(255,  249, 129, 0));
                paint.setUnderlineText(true);
                paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));

                for(int i = 0; i < numBricks; i++){
                    if(bricks[i].getVisibility()) {
                        if(bricks[i].type==5)
                            paint.setColor(Color.argb(255,255, 235, 59));
                        else if(bricks[i].type==4)
                            paint.setColor(Color.argb(255, 244, 67, 54));
                        else if(bricks[i].type==3)
                            paint.setColor(Color.argb(255, 139, 195, 74));
                        else if(bricks[i].type==2)
                            paint.setColor(Color.argb(255, 26, 35, 126));
                        else
                            paint.setColor(Color.argb(255,000,188,212));
                        canvas.drawRect(bricks[i].getRect(), paint);
                    }
                }

                paint.setColor(Color.argb(255,  255, 255, 255));

                paint.setTextSize(50);
                canvas.drawText("Score: " + score +
                        "  Timer :"+timer, 150,50, paint);

                ourHolder.unlockCanvasAndPost(canvas);
            }

        }


        public boolean intersects(RectF rect,Ball ball){

            if((abs(ball.getX() -rect.left)< ball.getR() || abs(ball.getX() -rect.right)< ball.getR() ))
            {
                if (abs(ball.getY()-rect.top)<=ball.getR())//Top right or Top left
                    return true;
                else if(abs(ball.getY()-rect.bottom)<=ball.getR())//Bottom right or bottom left
                    return true;
                else if((ball.getX()-rect.top)<=0 && (ball.getX()-rect.bottom)>=0)//Ball has hit the middle of the brick on the right or on the left
                    return true;
                else return false;
            }


            return false;
        }


        public void pause() {
            senSensorManager.unregisterListener(this);
            mPause=true;

        }


        public void resume() {
            senSensorManager.registerListener(this, senAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
            playing = true;
            if(firstTimeRun==0)
            {
                firstTimeRun=1;
                gameThread = new Thread(this);
                gameThread.start();
            }
            else{
                synchronized (pauseLock)
                {
                    mPause=false;

                    pauseLock.notifyAll();
                }
            }

        }

        @Override
        public boolean onTouchEvent(MotionEvent motionEvent) {

            switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {

                case MotionEvent.ACTION_DOWN:
                    paused = false;
                    initialTouch=motionEvent.getX();
                    break;

                case MotionEvent.ACTION_MOVE:
                    float x=motionEvent.getX();
                    if(x-initialTouch>0)
                        paddle.setMovementState(paddle.RIGHT);
                    else if(x-initialTouch<0)
                        paddle.setMovementState(paddle.LEFT);
                    else
                    {
                        if(x-paddle.getRect().left<=0)
                            paddle.setMovementState(paddle.LEFT);
                        if(x-paddle.getRect().right>=0)
                            paddle.setMovementState(paddle.RIGHT);
                    }
                    break;

                case MotionEvent.ACTION_UP:

                    paddle.setMovementState(paddle.STOPPED);
                    break;
            }
            return true;
        }

        public  float Round(float Rval, int Rpl) {
            float p = (float)Math.pow(10,Rpl);
            Rval = Rval * p;
            float tmp = Math.round(Rval);
            return (float)tmp/p;
        }

        @Override
        public void onSensorChanged(SensorEvent event) {
            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                long curTime = System.currentTimeMillis();
                if ((curTime - lastUpdate) > 100) {
                    long diffTime = (curTime - lastUpdate);
                    lastUpdate = curTime;


                    float x = event.values[SensorManager.DATA_X];
                    float y = event.values[SensorManager.DATA_Y];
                    float z = event.values[SensorManager.DATA_Z];

                    float prevVelocity=ball.xVelocity;

                    if(Round(x,4)>5.0000){
                        ball.xVelocity-=150;
                    }
                    else if(Round(x,4)<-5.0000){
                        ball.xVelocity+=150;
                    }
                    else
                    {
                        ball.xVelocity=prevVelocity;
                    }

                    last_x = x;
                    last_y = y;
                    last_z = z;
                }
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        breakoutView.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();

        breakoutView.pause();
    }

}