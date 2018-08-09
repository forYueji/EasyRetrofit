package com.hyp.easy.easyretrofit;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.text.TextUtils;

import com.yhfund.net.api.API;
import com.yhfund.net.base.BaseObserver;
import com.yhfund.net.util.TransformerUtil;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.annotations.Nullable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;


public class RetrofitManager {

    @SuppressLint("StaticFieldLeak")
    private volatile static RetrofitManager sRetrofitManager;

    public static RetrofitManager getInstance() {
        RetrofitManager inst = sRetrofitManager;
        if (inst == null) {
            synchronized (RetrofitManager.class) {
                inst = sRetrofitManager;
                if (inst == null) {
                    inst = new RetrofitManager();
                    sRetrofitManager = inst;
                }
            }
        }
        return inst;
    }

    /**
     * post
     *
     * @param url
     * @param headerMap
     * @param params
     * @param observer
     */
    public void post(@Nullable final String url, @Nullable HashMap<String, String> headerMap,
                     @Nullable HashMap<String, String> params, @Nullable BaseObserver<String> observer) {
        if (!checkMap(headerMap, params)) {
            return;
        }
        RetrofitClient.create(API.class).post(url, headerMap, params)
                .compose(TransformerUtil.<String>subSchedulers())
                .subscribe(observer);
    }

    /**
     * 下载图片 格式 File
     *
     * @param url
     * @param headerMap
     * @param observer
     */
    public static void downloadPicFromNetFile(@Nullable String url, @Nullable HashMap<String, String> headerMap,
                                              @Nullable final String filePath, @Nullable final String fileName,
                                              @Nullable BaseObserver<File> observer) {
        if (!checkMap(headerMap)) {
            return;
        }
        RetrofitClient.create(API.class).downloadPicFromNetFile(url, headerMap)
                .map(new Function<ResponseBody, File>() {
                    @Override
                    public File apply(ResponseBody responseBody) {
                        InputStream inputStream = responseBody.byteStream();
                        byte[] bytes = new RequestFormatTransformation().toByte(inputStream);
                        return new RequestFormatTransformation().toFile(bytes, fileName, filePath);
                    }
                })
                .compose(TransformerUtil.<File>subSchedulers())
                .subscribe(observer);
    }

    /**
     * 下载图片 格式 bitmap
     *
     * @param url
     * @param headerMap
     * @param params
     * @param observer
     */
    public static void downloadPicFromNetBitmap(@Nullable String url, @Nullable HashMap<String, String> headerMap,
                                                @Nullable HashMap<String, String> params, @Nullable BaseObserver<Bitmap> observer) {
        if (!checkMap(headerMap, params)) {
            return;
        }
        RetrofitClient.create(API.class).downloadPicFromNetBM(url, headerMap, params)
                .map(new Function<ResponseBody, Bitmap>() {
                    @Override
                    public Bitmap apply(ResponseBody responseBody) {
                        InputStream inputStream = responseBody.byteStream();
                        return new RequestFormatTransformation().toBitmap(new RequestFormatTransformation().toByte(inputStream));
                    }
                })
                .compose(TransformerUtil.<Bitmap>subSchedulers())
                .subscribe(observer);
    }

    /**
     * 下载图片 格式 bitmap
     *
     * @param url
     * @param headerMap
     * @param observer
     */
    public static void downloadPicFromNetBitmap(@Nullable String url, @Nullable HashMap<String, String> headerMap,
                                                @Nullable BaseObserver<Bitmap> observer) {
        if (!checkMap(headerMap)) {
            return;
        }
        RetrofitClient.create(API.class).downloadPicFromNetBM(url, headerMap)
                .map(new Function<ResponseBody, Bitmap>() {
                    @Override
                    public Bitmap apply(ResponseBody responseBody) {
                        InputStream inputStream = responseBody.byteStream();
                        return new RequestFormatTransformation().toBitmap(new RequestFormatTransformation().toByte(inputStream));
                    }
                })
                .compose(TransformerUtil.<Bitmap>subSchedulers())
                .subscribe(observer);
    }

    /**
     * 上传图片
     *
     * @param url
     * @param headerMap
     * @param params
     * @param observer
     */
    public static void upLoad(@Nullable final String url, @Nullable final HashMap<String, String> headerMap,
                              @Nullable final HashMap<String, String> params, @Nullable final HashMap<String, File> files,
                              @Nullable final BaseObserver<String> observer) {
        if (!checkMap(headerMap, params)) {
            return;
        }
        Observable.create(new ObservableOnSubscribe<MultipartBody.Builder>() {
            @Override
            public void subscribe(ObservableEmitter<MultipartBody.Builder> emitter) {
                MultipartBody.Builder builder = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM);
                /**
                 * 添加参数
                 */
                for (String key : params.keySet()) {

                    if (TextUtils.isEmpty(key)) {
                        continue;
                    }
                    builder.addFormDataPart(key, params.get(key));
                }

                for (String key : files.keySet()) {

                    if (TextUtils.isEmpty(key)) {
                        continue;
                    }
                    File file = new File(String.valueOf(files.get(key)));
                    RequestBody imageBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
                    builder.addFormDataPart(key, file.getName(), imageBody);
                }
                emitter.onNext(builder);
                emitter.onComplete();
            }
        }).compose(TransformerUtil.<MultipartBody.Builder>subSchedulers())
                .subscribe(new Observer<MultipartBody.Builder>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(MultipartBody.Builder builder) {
                        RetrofitClient.create(API.class).upload(url, headerMap, builder.build().parts())
                                .compose(TransformerUtil.<String>subSchedulers())
                                .subscribe(observer);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private static boolean checkMap(HashMap<String, String> headerMap, HashMap<String, String> params) {
        return !(null == headerMap) || !(null == params);
    }

    private static boolean checkMap(HashMap<String, String> headerMap) {
        return !(null == headerMap);
    }
}
