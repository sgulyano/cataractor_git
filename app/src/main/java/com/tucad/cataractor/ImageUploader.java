package com.tucad.cataractor;

import android.support.annotation.NonNull;
import android.util.Log;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

class ImageUploader {

    private static final String TAG = "ImageUploader";
    private ImageService service;

    ImageUploader() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        // Change base URL to your upload server URL.
        // 10.0.2.2	Special alias to your host loopback interface
        // (i.e., 127.0.0.1 on your development machine)
        // https://developer.android.com/studio/run/emulator-networking
        service = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:49160")
                .client(client)
                .build()
                .create(ImageService.class);
    }

    void uploadFile(File file) {
        RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), file);
        Log.e(TAG, file.getName());
        MultipartBody.Part body = MultipartBody.Part.createFormData("upload", file.getName(), reqFile);
        RequestBody name = RequestBody.create(MediaType.parse("text/plain"), "upload_test");

        retrofit2.Call<okhttp3.ResponseBody> req = service.postImage(body, name);
        req.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                // Do Something
                Log.e(TAG, "upload response finish");
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                t.printStackTrace();
                Log.e(TAG, "upload response failed");
            }
        });
    }
}
