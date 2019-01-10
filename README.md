

# EasyRetrofit  一个基于  Retrofit 请求框架封装的一个通用网络请求库

----

- 来源：由于项目比较老, 从最初的 HttpUrlconnection 请求到 Google Volley 再到 OKHttp 库，项目在网络请求框架方面不断的变更。
      Retrofit RXJava 比较流行后，17 年就变更请求库为 Retrofit，这里只是做了一些通用请求库的封装 。

      需要统一接口定义的可直接采用 API.class 这个类, 里面列举了一些常用的请求方式 和参数，当然也可以自定义 API 

          @FormUrlEncoded
          @POST
          Observable<String> post(@Url String url, @HeaderMap HashMap<String, String> headerMap,
                                  @FieldMap HashMap<String, String> params);
      
          @FormUrlEncoded
          @GET
          Observable<String> get(@Url String url, @HeaderMap HashMap<String, String> headerMap,
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


     如下：

    使用方法：

    - Application 中初始化


            RetrofitClient.init(this, "http://www.baidu.com").build();


    - POST 请求

            RetrofitManager.getInstance().post(url, header, params, new DisposeObserver<RequestCallback>() {
                                @Override
                                protected RequestCallback onSuccess(String json) {
                                    return new DisposeRequestCoordinateJson().handlerRequestSuccess(json, requestCallback);
                                }

                                @Override
                                protected RequestCallback onFailure(Throwable throwable) {
                                    return new DisposeRequestCoordinateJson().handlerRequestError(throwable, requestCallback);
                                }
                            });


    下载图片：

    - File

                RetrofitManager.downloadPicFromNetFile(url, header, path, fileName, new FileCallBack() {
                            @Override
                            public void onResponse(File file) {

                            }

                            @Override
                            public void onError() {

                            }
                        });



    - Bitmap

               RetrofitManager.downloadPicFromNetBitmap(changeHttpUrl(url), readeHeader(), new BitmapCallBack() {
                           @Override
                           public void onResponse(Bitmap bm) {

                           }

                           @Override
                           public void onError() {

                           }
                       });

    ---
    *依赖库
    ---

    [rxjava2](api 'io.reactivex.rxjava2:rxjava:2.1.9')
    [rxandroid](api 'io.reactivex.rxjava2:rxandroid:2.0.2')
    [retrofit2](api 'com.squareup.retrofit2:retrofit:2.4.0')
    [converter-scalars](api 'com.squareup.retrofit2:converter-scalars:2.2.0')
    [adapter-rxjava2](api 'com.squareup.retrofit2:adapter-rxjava2:2.4.0')

---
    <dependency>
      <groupId>com.hyp.easy.easyretrofit</groupId>
      <artifactId>hyp</artifactId>
      <version>1.0.0</version>
      <type>pom</type>
    </dependency>


    implementation 'com.hyp.easy.easyretrofit:hyp:1.0.0'
