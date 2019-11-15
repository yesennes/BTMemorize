package com.lsenseney.btmemorize.model;

import android.content.Context;
import android.util.Log;

import androidx.core.util.Consumer;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import okhttp3.ResponseBody;
import retrofit2.Callback;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Converter;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;

public class APIBible {
    private static APIBible instance;
    private static final String TAG = "APIBible";
    private static String key = "7b49bb79780270ec2aa314396b820813";
    private static String endpoint = "https://api.scripture.api.bible/v1/";

    private RawAPIBible api;
    private Map<String, String> versionAbbrToID;
    private Map<String, List<Book>> versionBooks;


    private APIBible(String key, Context context) {
        versionAbbrToID = new TreeMap<>();
        versionAbbrToID.put("WEBBE", "7142879509583d59-04");
        versionAbbrToID.put("WEB", "9879dbb7cfe39e4d-04");
        versionAbbrToID.put("ASV", "06125adad2d5898a-01");
        versionAbbrToID.put("RV", "40072c4a5aba4022-01");
        versionAbbrToID.put("KJV", "de4e12af7f28f599-02");

        OkHttpClient okClient = new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    Request request = chain.request();
                    request = request.newBuilder()
                            .addHeader("api-key", key).build();
                    okhttp3.Response response = chain.proceed(request);
                    return response;
                }).build();

        api = new Retrofit.Builder()
                .baseUrl(endpoint)
                .addConverterFactory(MoshiConverterFactory.create())
                .client(okClient).build().create(RawAPIBible.class);
    }

    public static APIBible getInstance(Context context) {
        if (instance == null) {
            instance = new APIBible(key, context);
        }
        return instance;
    }

    public Set<String> getVersions() {
        return versionAbbrToID.keySet();
    }

    public void getBooks(String version, Consumer<List<Book>> callback, Consumer<Throwable> onError) {
        Call<RawAPIBible.DataWrapper<List<Book>>> raw = api.getBooks(versionAbbrToID.get(version));
        raw.enqueue(new Callback<RawAPIBible.DataWrapper<List<Book>>>() {
            @Override
            public void onResponse(Call<RawAPIBible.DataWrapper<List<Book>>> call, Response<RawAPIBible.DataWrapper<List<Book>>> response) {
                callback.accept(response.body().data);
            }

            @Override
            public void onFailure(Call<RawAPIBible.DataWrapper<List<Book>>> call, Throwable t) {
                Log.e(TAG, "Failed to load books of bible", t);
                if (onError != null) {
                    onError.accept(t);
                }
            }
        });
    }


    private interface RawAPIBible {
        class DataWrapper<T> {
            public T data;
        }
        @GET("bibles/{id}/books?include-chapters=true")
        Call<DataWrapper<List<Book>>> getBooks(@Path("id") String books);
    }
}
