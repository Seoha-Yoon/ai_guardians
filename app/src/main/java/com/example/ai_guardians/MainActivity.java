package com.example.ai_guardians;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    ImageView iv;
    Button btn_yes, btn_no;
    ImageView btn_post;
    TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // layout과 activity 연결
        iv = findViewById(R.id.iv_video);
        btn_yes = findViewById(R.id.btn_yes);
        btn_no = findViewById(R.id.btn_no);
        btn_post = findViewById(R.id.btn_post);
        tv = findViewById(R.id.percentage);

        // imageView에 폭력작면 frame 받아오기
        // getImage();

        // 신고 버튼
        btn_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"112에 전화를 연결합니다.",Toast.LENGTH_SHORT).show();
            }
        });

        // 무시 버튼
        btn_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"앱을 종료합니다.",Toast.LENGTH_SHORT).show();
                // 앱 종료
                finish();
            }
        });

        // 임시롤 결과값 받아오는 버튼
        btn_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendpost();
            }
        });
    }

    private void sendpost(){
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(1, TimeUnit.MINUTES)
                .readTimeout(5, TimeUnit.MINUTES)
                .writeTimeout(5, TimeUnit.MINUTES)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(DjangoAPI.DJANGO_SITE)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();


        DjangoAPI postApi= retrofit.create(DjangoAPI.class);

        Call<ResponseBody> call = postApi.uploadFile();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {

                if (!response.isSuccessful()) {
                    Log.e("연결이 비정상적 : ", "error code : " + response.code());
                    return;
                }

                ResponseBody body = response.body();

                String result = null;
                try {
                    result = body.string();
                    //tv.setText(result);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                byte[] byteArray = Base64.decode(result, Base64.DEFAULT);

                Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                iv.setImageBitmap(bitmap);
            }
            @Override
            public void onFailure (Call < ResponseBody > call, Throwable t){
                Log.d("fail", t.toString());
            }
        });


    }


    private void getImage(){

    }
}
