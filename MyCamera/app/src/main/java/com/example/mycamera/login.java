package com.example.mycamera;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.Window;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class login extends Activity implements OnClickListener {
    private TextView mBtnLogin;
    private TextView mBtnRegister;
    public int input_width_left;
    public int input_width_right;
    private View progress;
    private EditText username ;
    private EditText passwd ;
    private View mInputLayout;

    private float mWidth, mHeight;

    private LinearLayout mName, mPsw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.login);

        initView();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mInputLayout = findViewById(R.id.input_layout);
        ViewGroup.MarginLayoutParams params = (MarginLayoutParams) mInputLayout.getLayoutParams();
        params.leftMargin =  0;
        params.rightMargin =  0;
       // params.width = params.

        mInputLayout.setLayoutParams(params);

        mName.setVisibility(View.VISIBLE);
        mPsw.setVisibility(View.VISIBLE);
        mInputLayout.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //mInputLayout = findViewById(R.id.input_layout);
       // ViewGroup.MarginLayoutParams params = (MarginLayoutParams) mInputLayout.getLayoutParams();
      // Log.d("input_width_left",String.valueOf(params.width));
      //  Log.d("input_width_right",String.valueOf(params.rightMargin));
      //  Log.d("left",String.valueOf(input_width_left));
       // Log.d("right",String.valueOf(input_width_right));
        //input_width_left = params.leftMargin;
        // right_width_right= params.rightMargin;
       // params.leftMargin =  10;
       // params.rightMargin =  10;

      //  mInputLayout.setLayoutParams(params);

//        mName.setVisibility(View.VISIBLE);
//        mPsw.setVisibility(View.VISIBLE);
//        mInputLayout.setVisibility(View.VISIBLE);
    }



    private void initView() {
        mBtnLogin = (TextView) findViewById(R.id.main_btn_login);
        mBtnRegister = (TextView) findViewById(R.id.main_btn_register);
        progress = findViewById(R.id.layout_progress);
     //   progress.setVisibility(View.INVISIBLE);
        mInputLayout = findViewById(R.id.input_layout);
        mInputLayout.setVisibility(View.VISIBLE);
        mName = (LinearLayout) findViewById(R.id.input_layout_name);
        mPsw = (LinearLayout) findViewById(R.id.input_layout_psw);


        username=findViewById(R.id.username);
        passwd = findViewById(R.id.passwd);


        ViewGroup.MarginLayoutParams params = (MarginLayoutParams) mInputLayout.getLayoutParams();
        input_width_left = params.leftMargin;
        input_width_right = params.rightMargin;
        Log.d("leftMargin",String.valueOf(params.width));
       Log.d("right",String.valueOf(input_width_right));
       // mInputLayout.setLayoutParams(params);
        mBtnLogin.setOnClickListener(this);
        mBtnRegister.setOnClickListener(this);






    }
    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.main_btn_login:
                mWidth = mBtnLogin.getMeasuredWidth();
                mHeight = mBtnLogin.getMeasuredHeight();

                mName.setVisibility(View.INVISIBLE);
                mPsw.setVisibility(View.INVISIBLE);

                inputAnimator(mInputLayout, mWidth, mHeight);

                break;
            case R.id.main_btn_register:
                Intent intent =new Intent(login.this,register.class);
                startActivity(intent);
                default:
                    break;
        }


    }

    private void inputAnimator(final View view, float w, float h) {

        AnimatorSet set = new AnimatorSet();

        ValueAnimator animator = ValueAnimator.ofFloat(0, w);
        animator.addUpdateListener(new AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (Float) animation.getAnimatedValue();
                ViewGroup.MarginLayoutParams params = (MarginLayoutParams) view.getLayoutParams();
               // input_width_left = params.leftMargin;
                //input_width_right= params.rightMargin;
               // Log.d("input_width_left",String.valueOf(input_width_left));
               // Log.d("value",String.valueOf(value));
                params.leftMargin = (int) value;
                params.rightMargin = (int) value;
                view.setLayoutParams(params);
            }
        });

        ObjectAnimator animator2 = ObjectAnimator.ofFloat(mInputLayout,
               "scaleX", 1f, 0.5f);
        set.setDuration(1000);
        set.setInterpolator(new AccelerateDecelerateInterpolator());
        set.playTogether(animator, animator2);
        set.start();
        set.addListener(new AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animation) {

                //progress.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mInputLayout.setVisibility(View.INVISIBLE);
                progressAnimator(progress);

                progress.setVisibility(View.VISIBLE);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(3000);


                            try {
                                EditText editText = (EditText) findViewById(R.id.username);//登录用户名
                                EditText editText1 = (EditText) findViewById(R.id.passwd);//登录密码
                                String a = editText.getText().toString();
                                String b = editText1.getText().toString();
                                Log.d("root", a);
                                Log.d("passwd", b);
                                System.out.println(a);
                                System.out.println(b);
                                OkHttpClient client = new OkHttpClient();
                                RequestBody requestBody = new FormBody.Builder()
                                        .add("username", a).add("password", b)
                                        .build();
                                //System.out.println(requestBody);
                                Request request = new Request.Builder()  //
                                        .url("http://121.199.51.148:5000/login")
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
                                String name = jo.get("name").getAsString();
                                String age = jo.get("age").getAsString();
                                String gender = jo.get("gender").getAsString();

                                Log.d("Hello", name+age+gender);

//                                //System.out.println(result);
                                pw=true;

                                if (pw == true) {
                                    Intent intent = new Intent(login.this, photo.class);
                                    intent.putExtra("name",name);
                                    intent.putExtra("age",age);
                                    intent.putExtra("gender",gender);
                                    startActivity(intent);
                                    login.this.finish();
                                } else {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(login.this, "账户名或密码错误", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }catch(Exception e){
                                e.printStackTrace();
                            }
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    progress.setVisibility(View.INVISIBLE);
                                    //initView();
                                    mInputLayout.setVisibility(View.VISIBLE);

                                }
                            });


                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();



               // progress.setVisibility(View.INVISIBLE);

               // progress.setDuration();


               // progress



                //  progress.setVisibility(View.INVISIBLE);

            }

            @Override
            public void onAnimationCancel(Animator animation) {
                // TODO Auto-generated method stub
                progress.setVisibility(View.INVISIBLE);
            }
        });
        //animator.

    }

    private void progressAnimator(final View view) {
        PropertyValuesHolder animator = PropertyValuesHolder.ofFloat("scaleX",
                0.5f, 1f);
        PropertyValuesHolder animator2 = PropertyValuesHolder.ofFloat("scaleY",
                0.5f, 1f);
        ObjectAnimator animator3 = ObjectAnimator.ofPropertyValuesHolder(view,
                animator, animator2);
        animator3.setDuration(1000);
        animator3.setInterpolator(new JellyInterpolator());
        animator3.start();

    }
}
