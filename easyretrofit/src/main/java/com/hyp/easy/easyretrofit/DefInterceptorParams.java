package com.hyp.easy.easyretrofit;
import android.text.TextUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;

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

public class DefInterceptorParams implements Interceptor {

    private Map<String, String> queryParamsMap = new HashMap<>();
    private Map<String, String> paramsMap = new HashMap<>();
    private Map<String, String> headerParamsMap = new HashMap<>();
    private List<String> headerLinesList = new ArrayList<>();

    DefInterceptorParams() {
        super();
    }

    public DefInterceptorParams(String key, String value) {
        super();
        if (paramsMap != null && paramsMap.size() > 0)
            this.paramsMap.put(key, value);
    }

    DefInterceptorParams(Map<String, String> paramsMap) {
        super();
        if (paramsMap != null && paramsMap.size() > 0)
            this.paramsMap.putAll(paramsMap);
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Request.Builder requestBuilder = request.newBuilder();

        Headers.Builder headerBuilder = request.headers().newBuilder();
        if (headerParamsMap.size() > 0) {
            Iterator iterator = headerParamsMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry entry = (Map.Entry) iterator.next();
                headerBuilder.add((String) entry.getKey(), (String) entry.getValue());
            }
        }

        if (headerLinesList.size() > 0) {
            for (String line : headerLinesList) {
                headerBuilder.add(line);
            }
        }

        requestBuilder.headers(headerBuilder.build());

        if (queryParamsMap.size() > 0) {
            injectParamsIntoUrl(request, requestBuilder, queryParamsMap);
        }


        if (canInjectIntoBody(request)) {
            FormBody.Builder formBodyBuilder = new FormBody.Builder();
            if (paramsMap.size() > 0) {
                for (Map.Entry entry : paramsMap.entrySet()) {
                    formBodyBuilder.add((String) entry.getKey(), (String) entry.getValue());
                }
            }
            RequestBody formBody = formBodyBuilder.build();
            String postBodyString = bodyToString(request.body());
            postBodyString += ((postBodyString.length() > 0) ? "&" : "") + bodyToString(formBody);
            requestBuilder.post(RequestBody.create(MediaType.parse("application/x-www-form-urlencoded;charset=UTF-8"), postBodyString));
        } else {
            injectParamsIntoUrl(request, requestBuilder, paramsMap);
        }

        request = requestBuilder.build();
        return chain.proceed(request);
    }

    private boolean canInjectIntoBody(Request request) {
        if (request == null) {
            return false;
        }
        if (!TextUtils.equals(request.method(), "POST")) {
            return false;
        }
        RequestBody body = request.body();
        if (body == null) {
            return false;
        }
        MediaType mediaType = body.contentType();
        if (mediaType == null) {
            return false;
        }
        if (!TextUtils.equals(mediaType.subtype(), "x-www-form-urlencoded")) {
            return false;
        }
        return true;
    }

    private void injectParamsIntoUrl(Request request, Request.Builder requestBuilder, Map<String, String> paramsMap) {
        HttpUrl.Builder httpUrlBuilder = request.url().newBuilder();
        if (paramsMap.size() > 0) {
            Iterator iterator = paramsMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry entry = (Map.Entry) iterator.next();
                httpUrlBuilder.addQueryParameter((String) entry.getKey(), (String) entry.getValue());
            }
        }

        requestBuilder.url(httpUrlBuilder.build());
    }

    private static String bodyToString(final RequestBody request) {
        try {
            final RequestBody copy = request;
            final Buffer buffer = new Buffer();
            if (copy != null)
                copy.writeTo(buffer);
            else
                return "";
            return buffer.readUtf8();
        } catch (final IOException e) {
            return "did not work";
        }
    }

    @Deprecated
    public static class Builder {

        DefInterceptorParams interceptor;

        public Builder() {
            interceptor = new DefInterceptorParams();
        }

        public Builder addParam(String key, String value) {
            interceptor.paramsMap.put(key, value);
            return this;
        }

        public Builder addParamsMap(Map<String, String> paramsMap) {
            interceptor.paramsMap.putAll(paramsMap);
            return this;
        }

        public Builder addHeaderParam(String key, String value) {
            interceptor.headerParamsMap.put(key, value);
            return this;
        }

        public Builder addHeaderParamsMap(Map<String, String> headerParamsMap) {
            interceptor.headerParamsMap.putAll(headerParamsMap);
            return this;
        }

        public Builder addHeaderLine(String headerLine) {
            int index = headerLine.indexOf(":");
            if (index == -1) {
                throw new IllegalArgumentException("Unexpected header: " + headerLine);
            }
            interceptor.headerLinesList.add(headerLine);
            return this;
        }

        public Builder addHeaderLinesList(List<String> headerLinesList) {
            for (String headerLine : headerLinesList) {
                int index = headerLine.indexOf(":");
                if (index == -1) {
                    throw new IllegalArgumentException("Unexpected header: " + headerLine);
                }
                interceptor.headerLinesList.add(headerLine);
            }
            return this;
        }

        public Builder addQueryParam(String key, String value) {
            interceptor.queryParamsMap.put(key, value);
            return this;
        }

        public Builder addQueryParamsMap(Map<String, String> queryParamsMap) {
            interceptor.queryParamsMap.putAll(queryParamsMap);
            return this;
        }

        public DefInterceptorParams build() {
            return interceptor;
        }

    }

    public DefInterceptorParams addParam(String key, String value) {
        if (value != null && value.length() > 0)
            this.paramsMap.put(key, value);
        return this;
    }

    public DefInterceptorParams addParamsMap(Map<String, String> paramsMap) {
        if (paramsMap != null && paramsMap.size() > 0)
            this.paramsMap.putAll(paramsMap);
        return this;
    }

    public DefInterceptorParams addHeaderParam(String key, String value) {
        if (value != null && value.length() > 0)
            this.headerParamsMap.put(key, value);
        return this;
    }

    public DefInterceptorParams addHeaderParamsMap(Map<String, String> headerParamsMap) {
        if (headerParamsMap != null && headerParamsMap.size() > 0)
            this.headerParamsMap.putAll(headerParamsMap);
        return this;
    }

    public DefInterceptorParams addHeaderLine(String headerLine) {
        if (headerLine != null && headerLine.length() > 0) {
            int index = headerLine.indexOf(":");
            if (index == -1) {
                throw new IllegalArgumentException("Unexpected header: " + headerLine);
            }
            this.headerLinesList.add(headerLine);
        }
        return this;
    }

    public DefInterceptorParams addHeaderLinesList(List<String> headerLinesList) {
        if (headerLinesList != null && headerLinesList.size() > 0) {
            for (String headerLine : headerLinesList) {
                int index = headerLine.indexOf(":");
                if (index == -1) {
                    throw new IllegalArgumentException("Unexpected header: " + headerLine);
                }
                this.headerLinesList.add(headerLine);
            }
        }
        return this;
    }

    public DefInterceptorParams addQueryParam(String key, String value) {
        if (value != null && value.length() > 0)
            this.queryParamsMap.put(key, value);
        return this;
    }

    public DefInterceptorParams addQueryParamsMap(Map<String, String> queryParamsMap) {
        if (queryParamsMap != null && queryParamsMap.size() > 0)
            this.queryParamsMap.putAll(queryParamsMap);
        return this;
    }

}
