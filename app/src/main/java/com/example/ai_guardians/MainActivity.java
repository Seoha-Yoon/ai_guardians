package com.example.ai_guardians;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    ImageView iv;
    Button btn_yes, btn_no;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // layout과 activity 연결
        iv = findViewById(R.id.iv_video);
        btn_yes = findViewById(R.id.btn_yes);
        btn_no = findViewById(R.id.btn_no);

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
    }

    private void getImage(){

    }

}
