package com.mansha.memoryrecall;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class AddItem extends AppCompatActivity {

    private ImageView imageView;
    private String imagePath;
    private String captionEntered;
    private String guid;
    private String pathToAudioFile;
    private Entity entity;
    private MediaRecorder mRecorder;
    private static String mediaFileName = null;
    boolean mStartRecording = true;
    boolean mStartPlaying = true;
    private MediaPlayer mPlayer = null;
    private boolean isRecording = false;
    private boolean modifyingExistingEntity = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        Button deleteButton = (Button)findViewById(R.id.buttonDelete);
        Intent intent = getIntent();


        Log.d("AddItem", "Is budle null " + savedInstanceState == null? "True" : "False");
        Log.d("AddItem", "Bundle contain key " + intent.hasExtra("Entity"));
        entity =  intent.getParcelableExtra("Entity");
        if (entity == null) {
            //New entity
            this.guid = java.util.UUID.randomUUID().toString();
            mediaFileName = getDataDir().getAbsolutePath();
            mediaFileName += "/" + guid + ".3pg";
            deleteButton.setEnabled(false);
        } else {
            //Update or delete existing entity
            guid = entity.getEntityGuid();
            mediaFileName = entity.getEntitySoundFile();
            Log.d("AddItem", "Media file " + mediaFileName + ". Guid " + guid);
            imagePath = entity.getEntityImage();
            pathToAudioFile = entity.getEntitySoundFile();
            captionEntered = entity.getEntityName();
            modifyingExistingEntity = true;
            showContentsInView();
        }

        Log.d("AddItem", "Media filename " + mediaFileName);
        //Disable start playing button if no audio file exists
        if (mediaFileName != null){
            File audioFile = new File(mediaFileName);
            Log.d("AddItem", "Media exists " + audioFile.exists());
            if (audioFile.exists()){
                enablePlayButton(true, new String[]{"PLAY", "STOP"});
            } else {
                enablePlayButton(false, new String[]{"PLAY", "STOP"});
            }
        } else {
            enablePlayButton(false, new String[]{"PLAY", "STOP"});
        }
    }


    public void showContentsInView(){
        Log.d("AddItem", "Caption entered " + captionEntered);
        EditText editText = (EditText)findViewById(R.id.addtext);
        editText.setText(captionEntered);
        imageView = (ImageView)findViewById(R.id.addimage);
        Log.d("AddItem", "Image path " + imagePath);
        if (imagePath != null) {
            File imgFile = new File(imagePath);
            if (imgFile.exists()) {
                Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                imageView.setImageBitmap(bitmap);
            }
            imageView.setContentDescription(captionEntered);
        }

    }


    public void getImageFromGallery(View view){
        Log.d("AddItem", "Image clicked");
        imageView = (ImageView)findViewById(R.id.addimage);
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, 100);
    }


    protected void enablePlayButton(boolean enable,String... options){
        int visibilityFlag = View.VISIBLE;
        ImageButton imageButton;
        if (!enable){
            visibilityFlag = View.INVISIBLE;
        }
        for (int i = 0; i < options.length; i++){
            if(options[i].compareTo("PLAY") == 0){
                imageButton = (ImageButton)findViewById(R.id.playImageButton);
                imageButton.setVisibility(visibilityFlag);
            } else if(options[i].compareTo("STOP") == 0){
                imageButton = (ImageButton)findViewById(R.id.stopImageButton);
                imageButton.setVisibility(visibilityFlag);
            } else if(options[i].compareTo("RECORD") == 0) {
                imageButton = (ImageButton) findViewById(R.id.recordImageButton);
                imageButton.setVisibility(visibilityFlag);
            }
        }
    }

    protected void enablePlayButton(boolean enable){
        ImageButton playButton = (ImageButton)findViewById(R.id.playImageButton);
        playButton.setVisibility(
                View.INVISIBLE
        );
        //playButton.setEnabled(enable);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case 100:
                if(resultCode == RESULT_OK){
                    Uri selectedImage = data.getData();

                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                        String imageFileName = getDataDir().getAbsolutePath() + "/" + guid + ".jpg";
                        File file = new File(imageFileName);
                        imagePath = file.getAbsolutePath();
                        FileOutputStream fileOutputStream = new FileOutputStream(file);
                        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

                        //Compress the image
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 20, fileOutputStream);
                        fileOutputStream.close();
                        Log.d("AddItem", "file path " + imagePath);

                        imageView.setImageBitmap(bitmap);
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                }
        }
    }



    public  void saveEntity(View view){
        Entity entityCreated;
        Log.d("AddItem", "Save button clicked");
        EditText editText = (EditText)findViewById(R.id.addtext);
        captionEntered = editText.getText().toString();
        Intent intent = new Intent();
        if (imagePath == null){
            entityCreated = new Entity(captionEntered);
        } else {
            entityCreated = new Entity(captionEntered, imagePath);
        }

        if (mediaFileName != null && new File(mediaFileName).length() > 0){
            entityCreated.setEntitySoundFile(mediaFileName);
        }

        entityCreated.entityGuid = guid;
        if(modifyingExistingEntity){
            entityCreated.entityStatus = "EXISTING";
        } else {
            entityCreated.entityStatus = "NEW";
        }

        intent.putExtra("Entity", entityCreated);

        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    public void cancelAdd(View view){
        //setResult(Activity.RESULT_OK, intent);
        finish();
    }

    public void deleteEntity(View view){
        if(mediaFileName != null){
            File mediaFile = new File(mediaFileName);
            if (mediaFile.exists()){
                mediaFile.delete();
            }
        }

        if (imagePath != null){
            File image = new File(imagePath);
            if(image.exists()){
                image.delete();
            }
        }
        Intent intent = new Intent();
        intent.putExtra("GUID", guid);
        setResult(Activity.RESULT_FIRST_USER, intent);
        finish();
    }


    public void startRecording(){
        enablePlayButton(false, new String[]{"RECORD"});
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(mediaFileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mRecorder.setMaxDuration(10000);
        try {
            mRecorder.prepare();
            mRecorder.start();
        }catch (IOException e){
            Log.e("AddItem", "Prepare() failed");
        }

    }

    public void stopRecording(){
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
        if(mediaFileName != null){
            File file = new File(mediaFileName);
            if(file.exists()){
                enablePlayButton(true);
            }
        }
        isRecording = false;
        enablePlayButton(true, new String[]{"PLAY", "RECORD"});
    }



    public void recordButtonPressed(View view){
        startRecording();
        enablePlayButton(true, new String[]{"STOP"});
        enablePlayButton(false, new String[]{"PLAY"});
        isRecording = true;

    }



    private void startPlaying(){
        enablePlayButton(false, new String[]{"PLAY"});
        mPlayer = new MediaPlayer();
        try {
            mPlayer.setDataSource(mediaFileName);
            mPlayer.prepare();
            mPlayer.start();
            mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    enablePlayButton(true, new String[]{"RECORD", "PLAY"});
                }
            });
        } catch (IOException e){
            Log.e("AddItem", "prepare() failed");
        }
    }

    private void stopPlaying(){
        mPlayer.release();
        mPlayer = null;
        enablePlayButton(true, new String[]{"RECORD", "PLAY"});
    }



    public void playButtonPressed(View view){
        startPlaying();
        enablePlayButton(true, new String[]{"STOP"});
        enablePlayButton(false, new String[]{"RECORD"});
    }

    public void stopButtonPressed(View view){
        if(isRecording){
            stopRecording();
        } else {
            stopPlaying();
        }
    }
}
