package com.hyp.easy.easyretrofit.api;


import java.util.HashMap;
import java.util.List;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.HeaderMap;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Url;

/**
 * API Definition
 */
public interface API {

    /**
     * POST Request FormUrlEncoded
     * @param url
     * @param headerMap
     * @param params
     * @return
     */
    @FormUrlEncoded
    @POST
    Observable<String> post(@Url String url, @HeaderMap HashMap<String, String> headerMap,
                            @FieldMap HashMap<String, String> params);

    @Multipart
    @POST
    Observable<String> upload(@Url String url, @HeaderMap HashMap<String, String> headerMap,
                              @Part List<MultipartBody.Part> files);

    @POST
    Observable<ResponseBody> downloadPicFromNetFile(@Url String url, @HeaderMap HashMap<String, String> headerMap);

    @FormUrlEncoded
    @POST
    Observable<ResponseBody> downloadPicFromNetBM(@Url String url, @HeaderMap HashMap<String, String> headerMap,
                                                  @FieldMap HashMap<String, String> params);

    @POST
    Observable<ResponseBody> downloadPicFromNetBM(@Url String url, @HeaderMap HashMap<String, String> headerMap);
}
