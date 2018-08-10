

#EasyRetrofit

----

    EasyRetrofit 一个基于  Retrofit 请求框架封装的 请求库
    
    
    来源：由于项目比较老, 从最初的 HttpUrlconnection 请求到 Google Volley 在到 鸿洋的 OKHttpUtils 库， 项目在网络请求框架方面不断的变更,
     从 Retrofit RXJava 比较流行后，17 年就变更请求库为 Retrofit，这里只是做了一些通用请求库的封装 。
     
     如下：
      
    使用方法：
        
        RetrofitManager.getInstance().post(changeHttpUrl(url), readeHeader(), params, new DisposeObserver<RequestCallback>() {
                    @Override
                    protected RequestCallback onSuccess(String json) {
                        return new DisposeRequestCoordinateJson().handlerRequestSuccess(json, requestCallback);
                    }
        
                    @Override
                    protected RequestCallback onFailure(Throwable throwable) {
                        return new DisposeRequestCoordinateJson().handlerRequestError(throwable, requestCallback);
                    }
                });
        
        DisposeObserver<T> 
        
    下载图片：
        
        File
        
        RetrofitManager.downloadPicFromNetFile(changeHttpUrl(url), readeHeader(), path, fileName, new FileCallBack() {
                    @Override
                    public void onResponse(File file) {
                        
                    }
        
                    @Override
                    public void onError() {
        
                    }
                });
                
        Bitmap
                
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
    