package com.example.mycamera;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class photo extends AppCompatActivity {
    public static final int TAKE_PHOTO = 1;
    public static final int CHOOSE_PHOTO = 2;
    private ImageView picture;
    private Uri imageUri;
    //private View progress;
    private TextView mInputLayout;

    //text
    private TextView mTv_responseText;

    private DrawerLayout mDrawerlayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.photo);
//        Button identify =findViewById(R.id.photo_identify);
//        Button choose=findViewById(R.id.select_photo);
//        Button take_photo = findViewById(R.id.take_photo);
//        picture=findViewById(R.id.picture);
//        mInputLayout =(TextView) findViewById(R.id.input_layout);
//        mTv_responseText = findViewById(R.id.response_text);

        NavigationView navigationView = findViewById(R.id.nav_view);
        CircleMenuView circleMenuView = findViewById(R.id.view);
        mDrawerlayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        TextView nameView =findViewById(R.id.name_text);
        MenuItem genderItem = findViewById(R.id.gender_view);
        MenuItem ageItem = findViewById(R.id.age_view);

        Intent intent = getIntent();

        String name =intent.getStringExtra("name");
        final String age =intent.getStringExtra("age");
        final String gender = intent.getStringExtra("gender");


        Log.d("Hello1", name+age+gender);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar !=null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.mipmap.menu);
        }

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.logout:
                        photo.this.finish();
                        break;
                    case R.id.age_view:
                        Toast.makeText(photo.this, "Your age is "+age, Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.gender_view:
                        Toast.makeText(photo.this, "Your gender is "+gender, Toast.LENGTH_SHORT).show();
                        break;
                }
                return true;
            }
        });
        circleMenuView.setOnClickListener(new CircleMenuView.onYuanPanClickListener() {
            @Override
            public void onClick(View v, int position) {
                //点击事件，拍照=0，选择图片=1，人脸注册=2，人脸搜索=3，人脸检测=4
                switch (position) {
                    case 0:
                        Toast.makeText(photo.this, position + "", Toast.LENGTH_SHORT).show();
                        String saveFormat = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                        String imageFileName = "JPEG_" + saveFormat + "_" + ".jpg";
                        String storePath = Environment.getExternalStorageDirectory().getAbsolutePath()
                                + File.separator + "DCIM" + File.separator + "Camera1";
                        File file = new File(storePath);

                        if (!file.exists()) {
                            file.mkdir();
                        }

                        //imageFileName = storePath+File.separator+imageFileName;
                        File outputImage = new File(storePath, imageFileName);//用于存放摄像头拍摄的照片


                        Log.d("button", String.valueOf(outputImage.getAbsolutePath()));

                        try {
                            if (!outputImage.exists()) {
                                outputImage.createNewFile();
                            }

                            // MediaStore.Images.Media.insertImage(getContentResolver(), outputImage.getAbsolutePath(), imageFileName, "1234");
                            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                            Uri uri = Uri.fromFile(outputImage);
                            intent.setData(uri);
                            photo.this.sendBroadcast(intent);
                            // galleryAddPic();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        if (Build.VERSION.SDK_INT >= 24) {//根据android的版本对URI执行操作
                            imageUri = FileProvider.getUriForFile
                                    (photo.this, "com.example.cameratest.fileproviderr", outputImage);
                        } else {
                            imageUri = Uri.fromFile(outputImage);
                        }
                        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");//系统会找到能够找到能响应这个intent程序去执行
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                        startActivityForResult(intent, TAKE_PHOTO);
                        break;
                    case 1:
                        Intent intent_c = new Intent(photo.this, faceCompare.class);
                        intent_c.putExtra("key","compare");
                        startActivity(intent_c);
                        break;
                    case 2:

                        Intent intent_r = new Intent(photo.this, faceIdentify.class);
                        intent_r.putExtra("key","register");
                        startActivity(intent_r);
                        break;
                    case 3:
                        Intent intent_s = new Intent(photo.this, faceIdentify.class);
                        intent_s.putExtra("key","search");
                        startActivity(intent_s);
                        break;
                    case 4:
                        Intent intent_d = new Intent(photo.this, faceIdentify.class);
                        intent_d.putExtra("key","detect");
                        startActivity(intent_d);
                        break;
                    default:
                        break;
                }
                Toast.makeText(photo.this, position + "", Toast.LENGTH_SHORT).show();
            }
        });


//        choose.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {//确认是否有权限开启
//                if(ContextCompat.checkSelfPermission(photo.this,
//                            Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
//                    ActivityCompat.requestPermissions(photo.this,
//                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
//                }
//                else{
//                    openAlbum();
//                }
//            }
//        });
//        take_photo.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                String saveFormat = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
//                String imageFileName = "JPEG_" + saveFormat + "_"+".jpg";
//                String storePath = Environment.getExternalStorageDirectory().getAbsolutePath()
//                        +File.separator+ "DCIM"+File.separator+ "Camera1";
//                File file = new File(storePath);
//
//                    if (!file.exists()){
//                        file.mkdir();
//                    }
//
//                //imageFileName = storePath+File.separator+imageFileName;
//                File outputImage=new File(storePath,imageFileName);//用于存放摄像头拍摄的照片
//
//
//
//                Log.d("button", String.valueOf(outputImage.getAbsolutePath()));
//
//                try {
//                    if (!outputImage.exists()){
//                        outputImage.createNewFile();
//                    }
//
//                   // MediaStore.Images.Media.insertImage(getContentResolver(), outputImage.getAbsolutePath(), imageFileName, "1234");
//                    Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
//                    Uri uri = Uri.fromFile(outputImage);
//                    intent.setData(uri);
//                    photo.this.sendBroadcast(intent);
//                   // galleryAddPic();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//
//                if (Build.VERSION.SDK_INT>=24){//根据android的版本对URI执行操作
//                    imageUri= FileProvider.getUriForFile
//                            (photo.this,"com.example.cameratest.fileproviderr",outputImage);}
//                else{
//                    imageUri=Uri.fromFile(outputImage);
//                }
//                Intent intent=new Intent("android.media.action.IMAGE_CAPTURE");//系统会找到能够找到能响应这个intent程序去执行
//                intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
//                startActivityForResult(intent,TAKE_PHOTO);
//            }
//        });
//
//        identify.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (picture.getDrawable()!=null){
//                    new Thread(new Runnable(){
//                        @Override
//                        public void run() {
//                            Bitmap bitmap = ((BitmapDrawable)picture.getDrawable()).getBitmap();
//                            String base64 = bitmapToBase64(bitmap);
//
//
//                            Log.d("base64", base64);
//                            try {
//                                OkHttpClient client = new OkHttpClient();
//                               RequestBody requestBody = new FormBody.Builder()
//                                        .add("image", base64)
//                                        .build();
//                                Request request = new Request.Builder()
//                                        .url("http://121.199.22.113:9091")
//                                        .post(requestBody)
//                                        .build();
//                                Response response = client.newCall(request).execute();
//                                String result = response.body().string();
//                                showResponse(result);
//                                Log.d("Result", result);
//                            }catch(Exception e){
//                                e.printStackTrace();
//                            }
//                        }
//                    }).start();
//                }
//                else
//                    Toast.makeText(photo.this, "wrong", Toast.LENGTH_SHORT).show();
//            }
//        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case android.R.id.home:
                mDrawerlayout.openDrawer(GravityCompat.START);
                break;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case TAKE_PHOTO:
//                if(resultCode==RESULT_OK){
//                    try{
//                        Log.d("take", imageUri.toString());
//                        //galleryAddPic();
//
//                        //Bitmap bitmap= BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
//
//                        //picture.setImageBitmap(bitmap);
//
//
//                    }catch (FileNotFoundException e){
//                        e.printStackTrace();
//                    }
//                }
                break;
            case CHOOSE_PHOTO:
                if (resultCode == RESULT_OK) {
                    //判断手机版本号
                    if (Build.VERSION.SDK_INT >= 19) {
                        //handleImageOnKitKat(data);
                    } else {
                        // handleImageBeforeKitKat(data);
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

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {//看是否权限已经打开
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openAlbum();
                } else {
                    Toast.makeText(this, "You denied the permission", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }
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
            picture.setImageBitmap(bitmap);
        } else {
            Toast.makeText(this, "faild to get image", Toast.LENGTH_SHORT).show();
        }
    }
    //显示文本
    private void showResponse(final String string) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mTv_responseText.setText(string);
            }
        });
    }

//    public String bitmapToBase64(Bitmap bitmap) {
//        String result = null;
//        ByteArrayOutputStream baos = null;
//        try {
//            if (bitmap != null) {
//                baos = new ByteArrayOutputStream();
//                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
//                //bitmap压缩成jpeg
//                baos.flush();
//                baos.close();
//
//                byte[] bitmapBytes = baos.toByteArray();
//                //转换成字节数组
//                result = Base64.encodeToString(bitmapBytes, Base64.DEFAULT);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            try {
//                if (baos != null) {
//                    baos.flush();
//                    baos.close();
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//        return result;
//    }
//    public Bitmap base64ToBitmap(String base64Data) {
//        byte[] bytes = Base64.decode(base64Data, Base64.DEFAULT);
//        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
//    }
//if (ContextCompat.checkSelfPermission(photo.this,
//    Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//        ActivityCompat.requestPermissions(photo.this,
//                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
//    } else {
//        openAlbum();
//    }

}
