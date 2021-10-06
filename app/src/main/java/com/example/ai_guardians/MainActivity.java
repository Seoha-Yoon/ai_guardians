package com.example.ai_guardians;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    VideoView vv;
    Button btn_yes, btn_no;
    ImageView btn_post;
    TextView tv;

    // Test 용 Notification
    // channel에 대한 id 생성
    private static final String PRIMARY_CHANNEL_ID = "noti_channel";
    private NotificationManager mNotificationManager;
    private static final int NOTIFICATION_ID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // layout과 activity 연결
        vv = findViewById(R.id.vv);
        btn_yes = findViewById(R.id.btn_yes);
        btn_no = findViewById(R.id.btn_no);
        btn_post = findViewById(R.id.btn_post);
        tv = findViewById(R.id.result);

        // imageView에 폭력작면 frame 받아오기
        // getImage();
        // showVideo();

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

        createNotificationChannel();
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

        Call<ResponseBody> call = postApi.request();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {

                if (!response.isSuccessful()) {
                    Log.e("연결이 비정상적 : ", "error code : " + response.code());
                    return;
                }

                ResponseBody body = response.body();
                Log.d("Type",body.contentType().toString());

                String result = null;
                try {
                    result = body.string();

                    if(result.charAt(7)=='v') {
                        // 알림 전송
                        sendNotification();
                        // video 보여주기
                        showVideo();
                    }
                    tv.setText(result);

                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
            @Override
            public void onFailure (Call < ResponseBody > call, Throwable t){
                Log.d("fail", t.toString());
            }
        });


    }

    private void showVideo(){

        // http://127.0.0.1:8000/media/video/21/book.mp4
        //http://127.0.0.1:8000/media/video/21/mushroom_nono.mp4
        // Disable caches

        Uri videoUri= Uri.parse("http://10.0.2.2:8000/media/video/21/mushroom_720.mp4");

        //비디오뷰의 재생, 일시정지 등을 할 수 있는 '컨트롤바'를 붙여주는 작업
        vv.setMediaController(new MediaController(this));

        //VideoView가 보여줄 동영상의 경로 주소(Uri) 설정하기
        vv.setVideoURI(videoUri);

        //리스너 설정
        vv.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                //비디오 시작
                vv.start();
            }
        });
    }

    //화면에 안보일 때
    @Override
    protected void onPause() {
        super.onPause();

        //비디오 일시 정지
        if(vv!=null && vv.isPlaying()) vv.pause();
    }
    //액티비티가 메모리에서 사라질 때
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //
        if(vv!=null) vv.stopPlayback();
    }

    // channel 만드는 메소드
    public void createNotificationChannel(){
        mNotificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel notificationChannel = new NotificationChannel(PRIMARY_CHANNEL_ID, "notification",
                    mNotificationManager.IMPORTANCE_HIGH);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setDescription("detect violence");
            mNotificationManager.createNotificationChannel(notificationChannel);
        }
    }

    // Notification Builder 만들기
    private NotificationCompat.Builder getNotificationBuilder(){
        NotificationCompat.Builder notifyBuilder = new NotificationCompat.Builder(this, PRIMARY_CHANNEL_ID)
                .setContentTitle("알림")
                .setContentText("폭력이 감지되었습니다")
                .setSmallIcon(R.drawable.notification);
        return notifyBuilder;
    }

    // Notification 보내는 메소드
    public void sendNotification(){
        // builder 생성
        NotificationCompat.Builder notifyBuilder = getNotificationBuilder();
        mNotificationManager.notify(NOTIFICATION_ID,notifyBuilder.build());
    }


}
