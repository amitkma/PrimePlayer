package com.github.amitkma.dictionary.api;

import com.github.amitkma.dictionary.model.OxfordModel;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by falcon on 27/1/18.
 */

public interface OxfordApiService {

    String API_BASE_URL = "https://od-api.oxforddictionaries.com/api/v1/";
    String APP_KEY = "8135a43940bb5877b79b8c127bc1c99c";
    String APP_ID = "adc0285b";

    @GET("entries/en/{wordId}")
    Call<OxfordModel> getWordDetails(@Path("wordId") String wordId);

    class Creator {

        public static OxfordApiService makeOxfordService() {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(OxfordApiService.API_BASE_URL)
                    .client(makeOkHttpClient())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            return retrofit.create(OxfordApiService.class);
        }

        private static OkHttpClient makeOkHttpClient() {
            return new OkHttpClient.Builder().addInterceptor(new Interceptor() {
                public Response intercept(Chain chain) throws IOException {
                    Request original = chain.request();
                    Request.Builder requestBuilder;
                    requestBuilder = original.newBuilder()
                            .header("app_id", APP_ID)
                            .header("app_key", APP_KEY)
                            .header("Accept", "application/json")
                            .method(original.method(), original.body());

                    Request request = requestBuilder.build();
                    return chain.proceed(request);
                }
            }).addInterceptor(new HttpLoggingInterceptor().setLevel(
                    HttpLoggingInterceptor.Level.BODY)).build();
        }
    }


}
