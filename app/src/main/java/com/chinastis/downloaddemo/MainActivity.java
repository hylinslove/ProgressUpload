package com.chinastis.downloaddemo;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import java.io.File;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    private final int PICK_IMAGE= 44;
    private ImageView photo;
    private Bitmap bitmap;

    private ProgressBar progressBar;

    Handler handler = new Handler();

    private String downloadUrl = "http://img.taopic.com/uploads/allimg/121120/240486-12112015205437.jpg";
    private String filePath = Environment.getExternalStorageDirectory().getPath()+ File.separator+"佟丽娅";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        photo = (ImageView) findViewById(R.id.image_main);

        progressBar = (ProgressBar) findViewById(R.id.progress_main);
    }

    public void upload(View view) {
        ProgressUploadFile.run(new ProgressListener() {
            @Override
            public void onProgress(long totalBytes, long remainingBytes, boolean done) {
                progressBar.setProgress((int) (100 - remainingBytes/totalBytes));
            }
        });
    }

    public void download(View view) {
        ProgressDownloadFile.run(new ProgressListener() {
            @Override
            public void onProgress(long totalBytes, long remainingBytes, boolean done) {
                if(done){

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            photo.setImageBitmap(BitmapFactory.decodeFile(filePath+File.separator+"yaya.jpg"));
                        }
                    });


                }
            }
        },downloadUrl,filePath);

    }

    public void pick(View view) {
        Intent imagePickIntent = new Intent(Intent.ACTION_PICK);
        imagePickIntent.setType("image/*");

        startActivityForResult(imagePickIntent,PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e("MENG","requestCode:"+requestCode+";resultCode:"+resultCode);
        if(requestCode == PICK_IMAGE){
            if(data != null) {
                try {
                    if(bitmap!=null) {
                        bitmap.recycle();
                    }
                    Uri uri = data.getData();
                    ContentResolver contentResolver = this.getContentResolver();
                    bitmap = BitmapFactory.decodeStream(contentResolver.openInputStream(uri));
                    photo.setImageBitmap(bitmap);
                } catch (Exception e){
                  Log.e("MENG","e:"+e);
                }
            }

        }

        super.onActivityResult(requestCode, resultCode, data);
    }


}
