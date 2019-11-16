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
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.transition.Explode;
import android.util.JsonReader;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.example.mycamera.Base64_Image.bitmapToBase64;
import static com.example.mycamera.photo.CHOOSE_PHOTO;

//人脸注册
public class faceIdentify extends AppCompatActivity {
    private Button upload;
    private SubmitButton submit;
    private ImageView picture;
    private TextView mTv_responseText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.face_register_layout);
        upload =  findViewById(R.id.btn_uplaod);
        submit =  findViewById(R.id.btn_submit);
        picture = findViewById(R.id.picture_register);
        mTv_responseText = findViewById(R.id.response_text_register);
        //上传相册按钮监听
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ContextCompat.checkSelfPermission(faceIdentify.this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(faceIdentify.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
                }
                else{
                    openAlbum();
                }
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (picture.getDrawable() != null) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Bitmap bitmap = ((BitmapDrawable) picture.getDrawable()).getBitmap();
                            String base64 = bitmapToBase64(bitmap);
                            Request request= null;
                            Log.d("base64", base64);
                            try {
                                OkHttpClient client = new OkHttpClient();
                                Intent intent =getIntent();
                                String key = intent.getStringExtra("key");

                                //人脸搜索
                                if (key.equals("search")){
                                        RequestBody requestBody = new FormBody.Builder()
                                                .add("image", base64)
                                                .add("image_type","BASE64")
                                                .add("group_id_list","all_face")
                                                .add("max_face_num","10")
                                                .add("match_threshold","70")

                                                .build();
                                        request = new Request.Builder()
                                                .url("http://121.199.51.148:5000/muface_search")

                                                .post(requestBody)
                                                .build();
                                        Response response = client.newCall(request).execute();
                                        String result = response.body().string();

                                    JsonParser jp = new JsonParser();
                                    //parseJsonWithJsonObject(result);
                                    JsonObject jo = jp.parse(result).getAsJsonObject();

                                    String error_msg = jo.get("error_msg").getAsString();
                                    String total = "";
                                    if (error_msg.equals("SUCCESS")) {

                                        JsonObject msg = jo.get("result").getAsJsonObject();
                                        JsonArray jsonArray = msg.get("face_list").getAsJsonArray();

                                        //  Toast.makeText(faceIdentify.this, "检测成功", Toast.LENGTH_SHORT).show();
                                        for (int i = 0; i < jsonArray.size(); ++i) {
                                            JsonObject msg2 = (JsonObject) jsonArray.get(i);

                                            String face_token = msg2.get("face_token").getAsString();
                                            JsonObject location = msg2.get("location").getAsJsonObject();
                                            String left = location.get("left").getAsString();
                                            JsonArray array = msg2.get("user_list").getAsJsonArray();

                                            if (array.size()==0){
                                                //total += "第" + String.valueOf(i) + "个人不在人脸库中\n";
                                            }
                                            else{
                                                for (int j=0;j<array.size();++j){

                                                    JsonObject list=(JsonObject) array.get(j);
                                                    total += "图中有";
                                                    String name = list.get("user_id").getAsString();
                                                    total += name+"、";


                                                }

                                            }



                                           // float score = list.get("score").getAsFloat();


                                            // System.out.println(age);
                                            // System.out.println(beauty);
                                        }

                                        showResponse(total);

                                        //String facelist =jo.get("result").getAsString();
                                        // Log.d("facelist", facelist);

                                    }else{
                                        total ="人脸不存在库中";
                                        showResponse(total);
                                    }

                                }
                                //人脸注册
                                if (key.equals("register")){
                                    RequestBody requestBody = new FormBody.Builder()
                                            .add("image", base64)
                                            .add("image_type","BASE64")
                                            .add("group_id","all_face")
                                            .add("user_id","huge")
                                            .add("action_type","APPEND")
                                            .build();
                                    request = new Request.Builder()
                                            .url("http://121.199.51.148:5000/face_upload")
                                            .post(requestBody)
                                            .build();
                                    Response response = client.newCall(request).execute();
                                    String result = response.body().string();
                                    JsonParser jp = new JsonParser();
                                    //parseJsonWithJsonObject(result);
                                    JsonObject jo = jp.parse(result).getAsJsonObject();

                                    final String error_msg = jo.get("error_msg").getAsString();
                                    // Log.d("Result", response.toString());

                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                if (error_msg.equals("SUCCESS")){
                                                    Toast.makeText(faceIdentify.this, "注册成功", Toast.LENGTH_LONG).show();
                                                }else{
                                                    Toast.makeText(faceIdentify.this, "注册失败", Toast.LENGTH_LONG).show();
                                                }

                                            }
                                        });

                                }
                                //人脸检测
                                if (key.equals("detect")){
                                    RequestBody requestBody = new FormBody.Builder()
                                            .add("image", base64)
                                            .add("image_type","BASE64")
                                            .add("max_face_num","10")
                                            .build();
                                    request = new Request.Builder()
                                            .url("http://121.199.51.148:5000/face_detect")
                                            .post(requestBody)
                                            .build();
                                    Response response = client.newCall(request).execute();
                                    String result = response.body().string();
                                    Log.d("1", result);
                                   JsonParser jp = new JsonParser();
                                    //parseJsonWithJsonObject(result);
                                    JsonObject jo = jp.parse(result).getAsJsonObject();
                                    JsonObject msg = jo.get("result").getAsJsonObject();
                                    JsonArray jsonArray =  msg.get("face_list").getAsJsonArray();
                                     String error_msg = jo.get("error_msg").getAsString();
                                     String total="";
                                   if (error_msg.equals("SUCCESS")){
                                     //  Toast.makeText(faceIdentify.this, "检测成功", Toast.LENGTH_SHORT).show();
                                       for (int i=0;i<jsonArray.size();++i){
                                           JsonObject msg2= (JsonObject) jsonArray.get(i);
                                           total +="第"+String.valueOf(i)+"个人信息\n";
                                           String face_token = msg2.get("face_token").getAsString();
                                           JsonObject location =msg2.get("location").getAsJsonObject();
                                           String left = location.get("left").getAsString();

                                           int age =msg2.get("age").getAsInt();
                                           String beauty =msg2.get("beauty").getAsString();
                                           String expression = msg2.get("expression").getAsJsonObject().get("type").getAsString();
                                           String gender= msg2.get("gender").getAsJsonObject().get("type").getAsString();
                                           if (gender.equals("female")){
                                               gender="女";
                                           }else{
                                               gender="男";
                                           }
                                           total +="性别："+gender+"   年龄："+String.valueOf(age)+"   表情："+expression+"   颜值"+beauty+"分\n";
                                           // System.out.println(age);
                                           // System.out.println(beauty);
                                       }
                                       showResponse(total);

                                      //String facelist =jo.get("result").getAsString();
                                      // Log.d("facelist", facelist);


                                   }

                                    //showResponse(result);
                                  // Log.d("Result", msg);
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                } else {
                    Toast.makeText(faceIdentify.this, "wrong", Toast.LENGTH_SHORT).show();
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
            picture.setImageBitmap(bitmap);
        } else {
            Toast.makeText(this, "faild to get image", Toast.LENGTH_SHORT).show();
        }
    }
    private void showResponse(final String string) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mTv_responseText.setText(string);
            }
        });
    }

}
