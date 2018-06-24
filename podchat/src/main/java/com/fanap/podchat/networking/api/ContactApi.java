package com.fanap.podchat.networking.api;

import com.fanap.podchat.model.Contact;

import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.POST;
import rx.Observable;

public interface ContactApi {

    @POST("nzh/addContacts")
    @FormUrlEncoded
    Observable<Response> addContact(@Header("_token_") String token
            , @Header("_token_issuer_") int tokenIssuer
            , @Field("firstName") String firstName
            , @Field("lastName") String lastName
            , @Field("email") String email
            , @Field("uniqueId") String uniqueId
            , @Field("cellphoneNumber") String cellphoneNumber);

    @POST("nzh/removeContacts")
    @FormUrlEncoded
    Observable<Response> removeContact(@Header("_token_") String token
            , @Header("_token_issuer_") int tokenIssuer
            , @Field("id") long userId);

    @POST("nzh/updateContacts")
    Observable<Response> updateContact(@Header("_token_") String token
            , @Header("_token_issuer_") int tokenIssuer
            , @Body Contact contact);
}
