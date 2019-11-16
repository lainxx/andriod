package com.example.mycamera;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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

import static com.example.mycamera.photo.CHOOSE_PHOTO;

public class register extends Activity {


    private EditText username;
    private EditText passwd1;
    private EditText passwd2;
    EditText ageText;
    EditText nameText;
    RadioGroup radioGroup;
    RadioButton male;
    RadioButton female;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_layout);
        super.onCreate(savedInstanceState);

        username =findViewById(R.id.username_register);
        passwd1 =findViewById(R.id.passwd1);
        passwd2 =findViewById(R.id.passwd2);




        SubmitButton sb = (SubmitButton) findViewById(R.id.btn3);
        sb.setOnClickListener(new View.OnClickListener() {
            public static final String TAG = "onclick";


            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(3000);
                            try {
                                username = (EditText) findViewById(R.id.username_register);//登录用户名
                                passwd1 = (EditText) findViewById(R.id.passwd1);//登录密码
                                passwd2 = (EditText) findViewById(R.id.passwd2);//登录密码
                                ageText = findViewById(R.id.age_text);
                                nameText = findViewById(R.id.username_text);
                                female=findViewById(R.id.female);

                                String gender="male";
                                if(female.isChecked()){
                                    gender = "female";
                                }
                      /*          final String strings =null;
                                radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                                    @Override
                                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                                        switch(checkedId){
                                            case R.id.male:
                                                strings="male";
                                                break;
                                            case R.id.female:
                                                strings="female";
                                                break;
                                        }
                                    }
                                });*/


                                String name = nameText.getText().toString();

                                String age = ageText.getText().toString();
                                String u1 = username.getText().toString();
                                String p1 = passwd1.getText().toString();
                                String p2 = passwd2.getText().toString();
                                Log.d("root", u1);
                                Log.d("passwd", p1);

                                OkHttpClient client = new OkHttpClient();


                                RequestBody requestBody = new FormBody.Builder()
                                        .add("username", u1).add("password1", p1)
                                        .add("password2",p2)
                                        .add("age", age)
                                        .add("gender",gender)
                                        .add("name",name)
                                        .build();
                                //System.out.println(requestBody);
                                Request request = new Request.Builder()  //
                                        .url("http://121.199.51.148:5000/regist")
                                        .post(requestBody)
                                        .build();
                                Response response = null;
                                String result = null;
                                try {
                                    response = client.newCall(request).execute();
                                    result = response.body().string();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                                JsonParser jp = new JsonParser();
                                //将json字符串转化成json对象
                                JsonObject jo = jp.parse(result).getAsJsonObject();
                                //获取message对应的值
                                // String us = jo.get("user").getAsString();
                                //  Log.d("root", a);
                                // Log.d("passwd", b);
                                String t = jo.get("msg").getAsString();
                                //  System.out.println("msg is：" + t);
                                //System.out.println(jo.get("successs"));
                                boolean pw = jo.get("success").getAsBoolean();
//                                //System.out.println(result);

                                if (pw == true) {
                                    Intent intent = new Intent(register.this, login.class);
                                    startActivity(intent);
                                    register.this.finish();
                                } else {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(register.this, "注册失败", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }catch(Exception e){
                                e.printStackTrace();
                            }



                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });

//        TextView mBntSubmit = (TextView) findViewById(R.id.main_btn_submit);
//        ImageView regist_back = (ImageView) findViewById(R.id.regist_back);
//        mBntSubmit.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //注册
//            }
//        });
//        regist_back.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(register.this,login.class);
//                startActivity(intent);
//            }
//        });

    }
    //打开i相册


}
