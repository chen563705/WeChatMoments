package com.homework.wechat.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import com.squareup.okhttp.Call;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

public class DownloadImgUtil {

	/**
	 * 从网络下载图片
	 * 
	 * @param imgUrl
	 * @param imageView
	 * @return
	 */
	public static Bitmap downloadImageByUrl(String imgUrl, ImageView imageView) {
		if (null == imgUrl)
			return null;

		try {
			URL url = new URL(imgUrl);
			OkHttpClient okHttpClient = new OkHttpClient();
			Request.Builder builder = new Request.Builder();
			Request request = builder.get().url(url).build();
			Call call = okHttpClient.newCall(request);
			Response resp = call.execute();
			if (resp.isSuccessful()) {
				InputStream is = new BufferedInputStream(resp.body()
						.byteStream());
				// 在InputStream中设置一个标记位置.
				is.mark(is.available());
				BitmapFactory.Options options = new BitmapFactory.Options();
				// decode时将会返回null,可以通过options查询图图片的实际大小，进行缩放
				options.inJustDecodeBounds = true;
				BitmapFactory.decodeStream(is, null, options);
				// 获取imageview想要显示的宽和高
				ImageSizeUtil.ImageSize imageSize = ImageSizeUtil
						.getImageViewSize(imageView);
				options.inSampleSize = ImageSizeUtil.caculateInSampleSize(
						options, imageSize.width, imageSize.height);
				options.inJustDecodeBounds = false;
				// 调用reset()将重新流回到标记的位置
				is.reset();
				Bitmap bitmap = BitmapFactory.decodeStream(is, null, options);
				call.cancel();
				is.close();
				return bitmap;
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 从网络下载图片保存在指定的文件
	 * 
	 * @param imgUrl
	 * @param file
	 * @return
	 */
	public static boolean downloadImageByUrl(String imgUrl, File file) {
		FileOutputStream fos = null;
		InputStream is = null;
		try {
			URL url = new URL(imgUrl);
			OkHttpClient okHttpClient = new OkHttpClient();
			Request.Builder builder = new Request.Builder();
			Request request = builder.get().url(url).build();
			Call call = okHttpClient.newCall(request);
			Response resp = call.execute();
			if (resp.isSuccessful()) {
				is = new BufferedInputStream(resp.body().byteStream());

				fos = new FileOutputStream(file);
				byte[] buf = new byte[512];
				int len = 0;
				while ((len = is.read(buf)) != -1) {
					fos.write(buf, 0, len);
				}
				fos.flush();
				call.cancel();
				return true;
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// 关闭流
			try {
				if (is != null)
					is.close();
			} catch (IOException e) {
			}

			try {
				if (fos != null)
					fos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return false;
	}
}
