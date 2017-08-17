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
	 * ����������ͼƬ
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
				// ��InputStream������һ�����λ��.
				is.mark(is.available());
				BitmapFactory.Options options = new BitmapFactory.Options();
				// decodeʱ���᷵��null,����ͨ��options��ѯͼͼƬ��ʵ�ʴ�С����������
				options.inJustDecodeBounds = true;
				BitmapFactory.decodeStream(is, null, options);
				// ��ȡimageview��Ҫ��ʾ�Ŀ�͸�
				ImageSizeUtil.ImageSize imageSize = ImageSizeUtil
						.getImageViewSize(imageView);
				options.inSampleSize = ImageSizeUtil.caculateInSampleSize(
						options, imageSize.width, imageSize.height);
				options.inJustDecodeBounds = false;
				// ����reset()���������ص���ǵ�λ��
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
	 * ����������ͼƬ������ָ�����ļ�
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
			// �ر���
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
