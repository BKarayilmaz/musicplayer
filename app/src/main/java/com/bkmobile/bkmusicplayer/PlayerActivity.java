package com.bkmobile.bkmusicplayer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

public class PlayerActivity extends AppCompatActivity {

    Button playBtn,nextBtn,prevBtn,ffBtn,frBtn;
    TextView txtSongName,textSStart,txtSStop;//txtsname,
    SeekBar seekMusic;
    ImageView imageView;

    String sname;//sname
    public static final String EXTRA_NAME="songName";//song_name
    static MediaPlayer mediaPlayer;
    int position;
    ArrayList<File> mySongs;

    Thread updateSeekBar;

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        playBtn=findViewById(R.id.playBtn);
        nextBtn=findViewById(R.id.nextBtn);
        prevBtn=findViewById(R.id.prevBtn);
        //ffBtn=findViewById(R.id.ffButton);
        //frBtn=findViewById(R.id.frButton);

        txtSongName=findViewById(R.id.txtSong);
        textSStart=findViewById(R.id.txtSStart);
        txtSStop=findViewById(R.id.txtSStop);

        seekMusic=findViewById(R.id.seekBar);

        imageView=findViewById(R.id.imageView);

        if(mediaPlayer!=null){
            mediaPlayer.stop();
            mediaPlayer.release();
        }

        Intent intent=getIntent();
        Bundle bundle=intent.getExtras();

        mySongs=(ArrayList) bundle.getParcelableArrayList("songs");
        String songName=intent.getStringExtra("songName");
        position=bundle.getInt("pos",0);

        txtSongName.setSelected(true);
        Uri uri= Uri.parse(mySongs.get(position).toString());
        sname=mySongs.get(position).getName();
        txtSongName.setText(sname);

        mediaPlayer=MediaPlayer.create(getApplicationContext(),uri);
        mediaPlayer.start();

        updateSeekBar=new Thread()
        {
            @Override
            public void run() {
                int totalDuration=mediaPlayer.getDuration();
                int currenPostition=0;

                while(currenPostition<totalDuration){
                    try {
                        sleep(500);
                        currenPostition=mediaPlayer.getCurrentPosition();
                        seekMusic.setProgress(currenPostition);
                    }catch (InterruptedException | IllegalStateException e){
                        e.printStackTrace();
                    }
                }
            }
        };
        seekMusic.setMax(mediaPlayer.getDuration());
        updateSeekBar.start();
        seekMusic.getProgressDrawable().setColorFilter(getResources().getColor(R.color.purple_200), PorterDuff.Mode.MULTIPLY);
        seekMusic.getThumb().setColorFilter(getResources().getColor(R.color.purple_200),PorterDuff.Mode.SRC_IN);

        seekMusic.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seekBar.getProgress());
            }
        });

        String endTime=createTime(mediaPlayer.getDuration());
        txtSStop.setText(endTime);

        final Handler handler= new Handler();
        final  int delay=1000;

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                String currentTime=createTime(mediaPlayer.getCurrentPosition());
                txtSStop.setText(currentTime);
                handler.postDelayed(this,delay);
            }
        },delay);


        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mediaPlayer.isPlaying()){
                    playBtn.setBackgroundResource(R.drawable.ic_play);
                    mediaPlayer.pause();
                }else{
                    playBtn.setBackgroundResource(R.drawable.ic_pause);
                    mediaPlayer.start();
                }
            }
        });

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.stop();
                mediaPlayer.release();
                position=((position+1)%mySongs.size());
                Uri u=Uri.parse(mySongs.get(position).toString());
                mediaPlayer=MediaPlayer.create(getApplicationContext(),u);
                sname=mySongs.get(position).getName();
                txtSongName.setText(sname);
                mediaPlayer.start();
                playBtn.setBackgroundResource(R.drawable.ic_pause);
                startAnimation(imageView);
            }
        });
        //next listener
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                nextBtn.performClick();
            }
        });

        prevBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.stop();
                mediaPlayer.release();
                position=((position-1)<0)?(mySongs.size()-1):(position-1);

                Uri u=Uri.parse(mySongs.get(position).toString());
                mediaPlayer=MediaPlayer.create(getApplicationContext(),u);
                sname=mySongs.get(position).getName();
                txtSongName.setText(sname);
                mediaPlayer.start();
                playBtn.setBackgroundResource(R.drawable.ic_pause);
                startAnimation(imageView);
            }
        });

/*
        ffBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mediaPlayer.isPlaying()){
                    mediaPlayer.seekTo(mediaPlayer.getCurrentPosition()+10000);
                }
            }
        });

        frBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mediaPlayer.isPlaying()){
                    mediaPlayer.seekTo(mediaPlayer.getCurrentPosition()-10000);
                }
            }
        });

*/
    }

    public void startAnimation(View view){
        ObjectAnimator animator=ObjectAnimator.ofFloat(imageView,"rotation",0f,360f);
        animator.setDuration(1000);
        AnimatorSet animatorSet=new AnimatorSet();
        animatorSet.playTogether(animator);
        animatorSet.start();
    }

    public String createTime(int duration){
        String time="";
        int minute=duration/1000/60;
        int second=duration/1000%60;

        time+=minute+":";
        if(second<10){
            time+="0";
        }
        time+=second;

        return time;
    }
}