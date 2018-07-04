package com.fanap.podchat.networking.api;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface FileApi {
    @Multipart
    @POST("/nzh/uploadFile/")
    Call<ResponseBody> sendFile(
            @Part MultipartBody.Part file
            , @Header("_token_") String token
            , @Header("_token_issuer_") int tokenIssuer
            , @Field("fileName") String fileName);
}
