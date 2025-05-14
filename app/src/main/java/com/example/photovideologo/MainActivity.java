package com.example.photovideologo;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.util.Random;


public class MainActivity extends AppCompatActivity {

    Button save_img, save_vid, upload_img, upload_vid;
    ImageView image,image1;
    VideoView video,video1;
    int SELECT_PICTURE = 200;
    int SELECT_VIDEO = 300;
    Bitmap bmp1, bmp2, bmp3;
    private static final String root = Environment.getExternalStorageDirectory().toString();
    private static final String app_folder = root + "/video_logo/";
    private AlertDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestAllFilesAccessPermission();

        image = findViewById(R.id.image);
        image1 = findViewById(R.id.image1);
        video = findViewById(R.id.video);
        video1 = findViewById(R.id.video1);
        upload_img = findViewById(R.id.upload_img);
        upload_img.setOnClickListener(view -> {
            imageChooser();
        });
        upload_vid = findViewById(R.id.upload_vid);
        upload_vid.setOnClickListener(view -> {
            videoChooser();
        });
        save_img = findViewById(R.id.save_img);
        save_img.setOnClickListener(view -> {
            save_img();
        });
        save_vid = findViewById(R.id.save_vid);
        save_vid.setOnClickListener(view -> {
            save_vid();
        });
    }

    void imageChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, SELECT_PICTURE);
    }

    void videoChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, SELECT_VIDEO);
        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(video);
        mediaController.setMediaPlayer(video);
        video.setMediaController(mediaController);
        video.start();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                Uri selectedImageUri = data.getData();
                if (null != selectedImageUri) {
                    image.setImageURI(selectedImageUri);
                }
            } else if (requestCode == SELECT_VIDEO) {
                Uri selectedVideoUri = data.getData();
                if (null != selectedVideoUri) {
                    video.setVideoURI(selectedVideoUri);
                }
            }
        }
    }

    public static Bitmap overlayBitmapToCenter(Bitmap bitmap1, Bitmap bitmap2) {
        int bitmap1Width = bitmap1.getWidth();
        int bitmap1Height = bitmap1.getHeight();
        int bitmap2Width = bitmap2.getWidth();
        int bitmap2Height = bitmap2.getHeight();

        float marginLeft = (float) (bitmap1Width * 0.2 - bitmap2Width * 0.2);
        float marginTop = (float) (bitmap1Height * 0.2 - bitmap2Height * 0.2);

        Bitmap overlayBitmap = Bitmap.createBitmap(bitmap1Width, bitmap1Height, bitmap1.getConfig());
        Canvas canvas = new Canvas(overlayBitmap);
        canvas.drawBitmap(bitmap1, new Matrix(), null);
        canvas.drawBitmap(bitmap2, marginLeft, marginTop, null);
        return overlayBitmap;
    }

    private void SaveImage(Bitmap finalBitmap) {

        File myDir = new File(app_folder);
        if (!myDir.exists()) {
            myDir.mkdirs();
        }
        Random generator = new Random();
        int n = 10000;
        n = generator.nextInt(n);
        String fname = "Image-"+ n +".jpg";
        File file = new File (myDir, fname);
        if (file.exists ())
            file.delete ();
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Video.Media.DATA };
            cursor = context.getContentResolver().query(contentUri,  proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public void save_img(){
        bmp2 = BitmapFactory.decodeResource(getResources(),R.drawable.amazon);
        image.buildDrawingCache();
        bmp1 = image.getDrawingCache();
        bmp3=overlayBitmapToCenter(bmp1,bmp2);
        SaveImage(bmp3);
        image1.setImageBitmap(bmp3);
    }

    public void save_vid(){
        FFmpeg ffmpeg = FFmpeg.getInstance(this);
        try {
            ffmpeg.loadBinary(new LoadBinaryResponseHandler() {

                @Override
                public void onStart() {}

                @Override
                public void onFailure() {}

                @Override
                public void onSuccess() {}

                @Override
                public void onFinish() {}
            });
        } catch (FFmpegNotSupportedException e) {
            // Handle if FFmpeg is not supported by device
        }

        Uri mUri = null;
        try {
            Field mUriField = VideoView.class.getDeclaredField("mUri");
            mUriField.setAccessible(true);
            mUri = (Uri)mUriField.get(video);
        } catch(Exception e) {}

        String [] command = {"-y","-i" , getRealPathFromURI(this,mUri), "-i" ,  app_folder+"logo.png" , "-filter_complex", "overlay=x=(main_w)*cos(t/5)^2:y=(main_h)*sin(t/5)^2" , app_folder+"output_video.mp4"};

        try {
            // to execute "ffmpeg -version" command you just need to pass "-version"
            ffmpeg.execute(command, new ExecuteBinaryResponseHandler() {

                @Override
                public void onStart() {
                    Toast.makeText(MainActivity.this, "Process Started", Toast.LENGTH_SHORT).show();
                    //wait_ya3m.setIcon(android.R.drawable.ic_dialog_alert).setTitle("Please Wait...").setMessage("Video editing in progress").show();

                    runOnUiThread(() -> {
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setIcon(android.R.drawable.ic_dialog_alert)
                                .setTitle("Please Wait...")
                                .setMessage("Video editing in progress");
                        progressDialog = builder.create();
                        progressDialog.show();
                        Toast.makeText(MainActivity.this, "Process Started", Toast.LENGTH_SHORT).show();
                    });
                }

                @Override
                public void onProgress(String message) {

                }

                @Override
                public void onFailure(String message) {
                    Log.e("error",message);
                    Toast.makeText(MainActivity.this, "Process Failed", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onSuccess(String message) {
                    Log.e("success",message);
                    video1.setVideoPath(app_folder+"output_video.mp4");
                    MediaController mediaController = new MediaController(MainActivity.this);
                    mediaController.setAnchorView(video1);
                    mediaController.setMediaPlayer(video1);
                    video1.setMediaController(mediaController);
                    video1.start();
                }

                @Override
                public void onFinish() {
                    runOnUiThread(() -> {
                        if (progressDialog != null && progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                    });
                }

            });
        } catch (FFmpegCommandAlreadyRunningException e) {
            // Handle if FFmpeg is already running
        }
    }

    private void requestAllFilesAccessPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                try {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                    intent.setData(Uri.parse("package:" + getPackageName()));
                    startActivityForResult(intent, 2296); // You can use any request code
                } catch (Exception e) {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                    startActivityForResult(intent, 2296);
                }
            }
        }
    }

}