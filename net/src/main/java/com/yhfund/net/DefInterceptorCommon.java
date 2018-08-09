package com.yhfund.net;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.io.IOException;

import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by hyp on 2018/3/13.
 * <p>
 * <p>Title: </p>
 * <p>
 * <p>Description: </p>
 * <p>
 * <p>Copyright: Copyright (c) 2017</p>
 * <p>
 * <p>Company: yhfund</p>
 */

public class DefInterceptorCommon implements Interceptor {

    private Context mContext;

    DefInterceptorCommon(Context context) {
        mContext = context.getApplicationContext();
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        //【1】 Request
        Request.Builder request = chain.request().newBuilder();

        //无网络链接
        if (!isNetConnect(mContext)) {
            request.cacheControl(CacheControl.FORCE_CACHE);
            //联网
        } else {
            request.cacheControl(CacheControl.FORCE_NETWORK);
        }
        request.addHeader("User-Agent", "Android");

        //【2】 Response
        Response.Builder response = chain.proceed(request.build()).newBuilder();

        if (isNetConnect(mContext)) {
            // 有网络时 设置缓存超时时间0个小时
            response.removeHeader("Authorization")
                    .header("Cache-Control", "public, only-if-cached, max-age=" + 0)
                    .build();
        } else {
            // 无网络时，设置超时为4周
            response.removeHeader("Authorization")
                    .header("Cache-Control", "public, only-if-cached, max-stale=" + 60 * 60 * 24 * 28)
                    .build();
        }
        return response.build();
    }

    /**
     * 判断是否链接网络
     *
     * @param context
     *         manager中的context环境
     *
     * @return
     */
    private boolean isNetConnect(Context context) {
        return getNetworkType(context) != 0;
    }


    /**
     * 获取当前网络类型
     *
     * @return 0：未连接   1：WIFI网络   2：Mobile网络    3：ETHERNET网络 （网线）
     */
    private static final int NETTYPE_NULL = 0;
    private static final int NETTYPE_WIFI = 1;
    private static final int NETTYPE_MOBILE = 2;
    private static final int NETTYPE_ETHERNET网络 = 3;

    /**
     * 得到网络类型
     *
     * @param context
     *         manager中的context环境
     *
     * @return
     */
    private int getNetworkType(Context context) {
        int netType = NETTYPE_NULL;
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo == null) {
            return netType;
        }
        int nType = networkInfo.getType();
        if (nType == ConnectivityManager.TYPE_MOBILE) {
            netType = NETTYPE_MOBILE;
        } else if (nType == ConnectivityManager.TYPE_WIFI) {
            netType = NETTYPE_WIFI;
        }
        return netType;
    }
}
