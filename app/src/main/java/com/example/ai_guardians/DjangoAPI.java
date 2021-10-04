package com.example.ai_guardians;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Field;

public interface DjangoAPI {

    String DJANGO_SITE="http://10.0.2.2:8000";

    @POST("guardians_of_children/violence")
    Call <ResponseBody> request();
}