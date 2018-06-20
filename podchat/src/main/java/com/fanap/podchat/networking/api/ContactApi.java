package com.fanap.podchat.networking.api;

import com.fanap.podchat.model.AddContact;
import com.fanap.podchat.model.Contact;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.Header;
import retrofit2.http.POST;
import rx.Observable;

public interface ContactApi {

    @POST("nzh/addContacts")
    Observable<Response<AddContact>> addContact(@Header("_token_") String token
            , @Header("_token_issuer_") int tokenIssuer
            , @Body AddContact contacts);

    @POST("nzh/removeContacts")
    Observable<Response> removeContact(@Header("_token_") String token
            , @Header("_token_issuer_") int tokenIssuer
            , @Field("id") String userId);

    @POST("nzh/updateContacts")
    Observable<Response> updateContact(@Header("_token_") String token
            , @Header("_token_issuer_") int tokenIssuer
            , @Body Contact contact);
}
