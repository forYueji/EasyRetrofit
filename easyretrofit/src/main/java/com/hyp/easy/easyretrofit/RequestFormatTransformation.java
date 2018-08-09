package com.hyp.easy.easyretrofit;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import io.reactivex.annotations.Nullable;

/**
 * request format transformation
 */
public class RequestFormatTransformation {

    private interface TransformationInterface<T> {
        T transformation();
    }

    public synchronized File toFile(@Nullable byte[] bytes, @Nullable String fileName, String filePath) {
        return new FileTransformation(bytes, fileName, filePath).transformation();
    }

    public synchronized byte[] toByte(@Nullable InputStream inputStream) {
        return new ByteTransformation(inputStream).toByteArray();
    }

    public synchronized Bitmap toBitmap(@Nullable byte[] bytes) throws NullPointerException {
        return new BitmapTransformation(bytes).transformation();
    }

    private static class BitmapTransformation implements TransformationInterface<Bitmap> {

        private byte[] mBytes;

        BitmapTransformation(@Nullable byte[] bytes) {
            this.mBytes = bytes;
        }

        @Override
        public Bitmap transformation() {
            return BitmapFactory.decodeByteArray(mBytes, 0, mBytes.length);
        }
    }

    private static class ByteTransformation implements TransformationInterface<byte[]> {

        private InputStream mInputStream;

        ByteTransformation(@Nullable InputStream inputStream) {
            this.mInputStream = inputStream;
        }

        @Override
        public byte[] transformation() {
            return toByteArray();
        }

        private byte[] toByteArray() {
            int len = 0;
            byte[] buf = new byte[1024 * 1024];
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            try {
                while ((len = mInputStream.read(buf, 0, buf.length)) != -1) {
                    bos.write(buf, 0, len);
                    bos.flush();
                }
                return bos.toByteArray();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return buf;
        }
    }

    private static class FileTransformation implements TransformationInterface<File> {
        private byte[] mBytes;
        private String mFileName;
        private String mFilePath;

        FileTransformation(byte[] bytes, @Nullable String fileName, @Nullable String filePath) {
            this.mBytes = bytes;
            this.mFileName = fileName;
            this.mFilePath = filePath;
        }

        private File handlerTransformation() {
            if (TextUtils.isEmpty(mFileName) || TextUtils.isEmpty(mFilePath)) {
                return new File("");
            }

            File file = null;

            BufferedOutputStream bos = null;
            FileOutputStream fos = null;
            try {
                File dir = new File(mFilePath);
                if (!dir.exists() && dir.isDirectory()) {
                    dir.mkdirs();
                }
                file = new File(mFilePath + File.separator + mFileName);
                fos = new FileOutputStream(file);
                bos = new BufferedOutputStream(fos);
                bos.write(mBytes);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (bos != null) {
                    try {
                        bos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return file;
        }

        @Override
        public File transformation() {
            return handlerTransformation();
        }
    }
}
