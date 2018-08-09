package com.hyp.easy.easyretrofit;

import android.content.Context;
import android.text.TextUtils;

import java.io.File;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.annotations.Nullable;
import okhttp3.Cache;
import okhttp3.CookieJar;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;


/**
 * retrofit client
 */
public class RetrofitClient {

    private volatile static Retrofit sRetrofit;

    public static Builder init(@Nullable Context context, @Nullable String baseUrl) throws IllegalArgumentException {
        if (TextUtils.isEmpty(baseUrl)) {
            throw new IllegalArgumentException("baseUrl is null ?");
        }
        if (!baseUrl.endsWith("/")) {
            baseUrl = baseUrl + "/";
        }
        sRetrofit = null;
        return new Builder(context.getApplicationContext(), baseUrl);
    }

    protected static void build(Builder builder) {
        if (null == sRetrofit) {
            synchronized (RetrofitClient.class) {
                if (null == sRetrofit) {
                    sRetrofit = initRetrofit(findClient(builder), builder.getBaseUrl());
                }
            }
        }
    }

    private static Retrofit initRetrofit(@Nullable OkHttpClient client, @Nullable String baseUrl) {
        return new Retrofit.Builder()
                .client(client)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(ScalarsConverterFactory.create())
                .baseUrl(baseUrl)
                .build();
    }

    private static OkHttpClient findClient(@Nullable Builder builder) {
        return new OkHttpClient.Builder()
                .cache(builder.getCache())
                .retryOnConnectionFailure(true)
                .connectTimeout(builder.getConnectTimeoutSeconds(), TimeUnit.SECONDS)
                .readTimeout(builder.getConnectTimeoutSeconds(), TimeUnit.SECONDS)
                .addInterceptor(builder.getDefInterceptorCommon())
                .addInterceptor(builder.getInterceptorParams())
                .cookieJar(builder.getCookie())
                .build();
    }

    public static void resetBaseUrl(@Nullable Context context, @Nullable String baseUrl) {
        init(context, baseUrl).build();
    }

    protected synchronized static <T> T create(Class<T> clazz) {
        if (null == sRetrofit) {
            throw new IllegalArgumentException("retrofit unable null ");
        }
        return sRetrofit.create(clazz);
    }

    public static class Builder {
        private String mBaseUrl;
        /**
         * cookie
         */
        private CookieJar mCookie;
        /**
         * 拦截器
         */
        private Interceptor mInterceptorCommon;

        private Interceptor mInterceptorParams;

        private Interceptor mGzipRequestInterceptor;
        /**
         * 超时时间
         */
        private long mConnectTimeoutSeconds;

        private Map<String, String> mParamsMap;

        private final int HTTP_RESPONSE_DISK_CACHE_MAX_SIZE = 10 * 1024 * 1024;

        private Context mContext;

        Builder(Context context, String baseUrl) {
            mContext = context.getApplicationContext();
            mBaseUrl = baseUrl;
        }

        public void build() {
            RetrofitClient.build(this);
        }

        public Builder setBaseUrl(String val) {
            this.mBaseUrl = val;
            return this;
        }

        public String getBaseUrl() {
            return mBaseUrl;
        }

        public CookieJar getCookie() {
            if (null == mCookie) {
                mCookie = new DefManagerCookie();
            }
            return mCookie;
        }

        public Interceptor getDefInterceptorCommon() {
            if (null == mInterceptorCommon) {
                mInterceptorCommon = new DefInterceptorCommon(mContext);
            }
            return mInterceptorCommon;
        }

        public long getConnectTimeoutSeconds() {
            if (mConnectTimeoutSeconds == 0) {
                mConnectTimeoutSeconds = 500;
            }
            return mConnectTimeoutSeconds;
        }

        public Interceptor getInterceptorParams() {
            if (mInterceptorParams == null) {
                if (mParamsMap != null) {
                    mInterceptorParams = new DefInterceptorParams(mParamsMap);
                } else {
                    mInterceptorParams = new DefInterceptorParams();
                }
            }
            return mInterceptorParams;
        }

        public Interceptor getGzipRequestInterceptor() {
            if (null == mGzipRequestInterceptor) {
                mGzipRequestInterceptor = new GzipRequestInterceptor();
            }
            return mGzipRequestInterceptor;
        }

        public Builder setParamsMap(Map<String, String> paramsMap) {
            this.mParamsMap = paramsMap;
            return this;
        }

        public Cache getCache() {
            File baseDir = mContext.getCacheDir();
            File cacheDir = new File(baseDir, "HttpResponseCache");
            return new Cache(cacheDir, HTTP_RESPONSE_DISK_CACHE_MAX_SIZE);
        }
    }
}