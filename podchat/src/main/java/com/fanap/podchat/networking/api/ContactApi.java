package com.fanap.podchat.networking.api;

import com.fanap.podchat.model.Contact;

import java.util.List;

import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import rx.Observable;

public interface ContactApi {

    @PUT("nzh/addContacts")
    Observable<Response<Contact>> addContact(@Header("_token_") String token
            , @Header("_token_issuer_") int tokenIssuer
            , @Body Contact contact);

    @POST("nzh/removeContacts")
    Observable<Response> removeContact(@Header("_token_") String token
            , @Header("_token_issuer_") int tokenIssuer
            , @Field("id") List<String> userIds);

    @POST("nzh/updateContacts")
    Observable<Response> updateContact(@Header("_token_") String token
            , @Header("_token_issuer_") int tokenIssuer
            , @Body Contact contact);

}
