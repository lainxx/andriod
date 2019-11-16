package com.example.mycamera;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.example.mycamera.Base64_Image.bitmapToBase64;
import static com.example.mycamera.photo.CHOOSE_PHOTO;

//人脸搜索
public class faceCompare extends AppCompatActivity  {


    private Button upload_left;
    private Button upload_right;
    private ImageView picture_left;
    private ImageView picture_right;
    private TextView textView;
    public String loc;
    private SubmitButton submitButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.face_compare_layout);
        upload_left = findViewById(R.id.upload_left);
        upload_right = findViewById(R.id.upload_right);
        picture_left = findViewById(R.id.picture_left);
        picture_right = findViewById(R.id.picture_right);
        textView = findViewById(R.id.text_face_compare);
        submitButton = findViewById(R.id.submit_face);
        upload_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ContextCompat.checkSelfPermission(faceCompare.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(faceCompare.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
                }
                else{
                    loc ="left";
                    openAlbum();

                }
            }
        });
        upload_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ContextCompat.checkSelfPermission(faceCompare.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(faceCompare.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
                }
                else{
                    loc ="right";
                    openAlbum();

                }
            }
        });
        submitButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                if (picture_left.getDrawable()==null){
                    Toast.makeText(faceCompare.this,"请上传第一张图片",Toast.LENGTH_SHORT).show();
                }else if (picture_right.getDrawable() ==null){
                    Toast.makeText(faceCompare.this,"请上传第二张图片",Toast.LENGTH_SHORT).show();
                }else{
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Bitmap bitmap_left =((BitmapDrawable) picture_left.getDrawable()).getBitmap();
                            String base64_left = bitmapToBase64(bitmap_left);

                            Bitmap bitmap_right =((BitmapDrawable) picture_right.getDrawable()).getBitmap();
                            String base64_right = bitmapToBase64(bitmap_right);
                            Request request= null;
                            OkHttpClient client = new OkHttpClient();
                            RequestBody requestBody = new FormBody.Builder()
                                    .add("image1", base64_left)
                                    .add("image2",base64_right)


                                    .build();
                            request = new Request.Builder()
                                    .url("http://121.199.22.113:9091/face_compare")
                                    .post(requestBody)
                                    .build();
                            Response response = null;
                            try {
                                response = client.newCall(request).execute();

                                String result = response.body().string();

                                Log.d("111111111111", result);
                                JsonParser jp = new JsonParser();
                                //parseJsonWithJsonObject(result);

                               JsonObject jo = jp.parse(result).getAsJsonObject();

                                String error_msg = jo.get("error_msg").getAsString();
                                // Log.d("Result", response.toString());
                                String total ="";
                                if (error_msg.equals("SUCCESS")){
                                    JsonObject msg = jo.get("result").getAsJsonObject();
                                    float score=msg.get("score").getAsFloat();

                                    total = "相似度为%"+String.valueOf(score);
                                }else{
                                    total="error";
                                }
                                showResponse(total);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }




                        }
                    }).start();
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        switch (requestCode) {

            case CHOOSE_PHOTO:
                if (resultCode == RESULT_OK) {
                    //判断手机版本号
                    if (Build.VERSION.SDK_INT >= 19) {
                        handleImageOnKitKat(data);
                    } else {
                        handleImageBeforeKitKat(data);
                    }
                }
            default:
                break;
        }
    }

    public void openAlbum() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");

        startActivityForResult(intent, CHOOSE_PHOTO);//打开相册
    }
    @TargetApi(19)
    private void handleImageOnKitKat(Intent data) {//根据版本对uri进行解析
        String imagePath = null;
        Uri uri = data.getData();
        if (DocumentsContract.isDocumentUri(this, uri)) {
            //如果是document类型的Uri，则通过document id处理
            String docId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docId.split(":")[1];//解析出数字格式的id
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                imagePath = getImagePath(contentUri, null);
            } else if ("content".equalsIgnoreCase(uri.getScheme())) {
                imagePath = getImagePath(uri, null);
            } else if ("file".equalsIgnoreCase(uri.getScheme())) {
                imagePath = uri.getPath();
            }
            displayImage(imagePath);
        }
    }

    private void handleImageBeforeKitKat(Intent data) {//老版本的解析
        Uri uri = data.getData();
        String imagePath = getImagePath(uri, null);
        displayImage(imagePath);
    }

    private String getImagePath(Uri uri, String selection) {//获取路径
        String path = null;
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    private void displayImage(String imagePath) {//展示照片
        if (imagePath != null) {
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);

            // ByteArrayOutputStream out = Base64_Image.thumbnail(imagePath);
            // Bitmap bitmap=BitmapFactory.decodeByteArray(out.toByteArray(),0,out.size());
            if (loc.equals("left")){
                picture_left.setImageBitmap(bitmap);
            }else{
                picture_right.setImageBitmap(bitmap);
            }

        } else {
            Toast.makeText(this, "faild to get image", Toast.LENGTH_SHORT).show();
        }
    }
    private void showResponse(final String string) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                textView.setText(string);
            }
        });
    }
}
