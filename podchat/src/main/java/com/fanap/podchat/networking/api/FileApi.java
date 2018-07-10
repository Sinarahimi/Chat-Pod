package com.fanap.podchat.networking.api;

import com.fanap.podchat.model.FileUpload;

import okhttp3.MultipartBody;
import retrofit2.Response;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import rx.Observable;

public interface FileApi {
    @Multipart
    @POST("/nzh/uploadFile/")
    Observable<Response<FileUpload>> sendFile(
            @Part MultipartBody.Part file
            , @Header("_token_") String token
            , @Header("_token_issuer_") int tokenIssuer
            , @Part("fileName") String fileName);
}
