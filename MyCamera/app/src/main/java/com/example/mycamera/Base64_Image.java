package com.example.mycamera;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;


import net.coobird.thumbnailator.Thumbnails;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;

public class Base64_Image {

    public static String bitmapToBase64(Bitmap bitmap) {
        String result = null;
        ByteArrayOutputStream baos = null;
        try {
            if (bitmap != null) {
                baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
                //bitmap压缩成jpeg
                baos.flush();
                baos.close();

                byte[] bitmapBytes = baos.toByteArray();
                //转换成字节数组
                result = Base64.encodeToString(bitmapBytes, Base64.DEFAULT);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (baos != null) {
                    baos.flush();
                    baos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }
    public static Bitmap base64ToBitmap(String base64Data) {
        byte[] bytes = Base64.decode(base64Data, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }
//    public static ByteArrayOutputStream  thumbnail(String filename)  {
//        ByteArrayOutputStream out = new ByteArrayOutputStream();
//        URL url = Thread.currentThread().getContextClassLoader()
//                .getResource("1.jpg");
//
//        //压缩后转向到内存中
//        try {
//            Thumbnails
//                    .of("C:\\Users\\ZYJ\\Desktop\\img\\huge.jpg")//可以是文件名或输入流
//                    .size(100, 100)
//                    .rotate(90)//转90度
//                    .keepAspectRatio(true)//保持比例(默认)
//                    .toOutputStream(out);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//
//        //将压缩后的图片变成Base64
//       // String s=Base64.getEncoder().encodeToString(out.toByteArray());
//        //System.out.println(s);
//        return out;
//    }
}
