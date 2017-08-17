package com.homework.wechat.util;

import javax.microedition.khronos.opengles.GL10;

import android.graphics.BitmapFactory;
import android.opengl.GLES10;
import android.util.DisplayMetrics;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * 获取图片大小辅助类
 * 
 * @author chenjiayin
 * 
 */
public class ImageSizeUtil {

	private static final int DEFAULT_MAX_BITMAP_DIMENSION = 2048;

	private static ImageSize maxBitmapSize;

	static {
		int[] maxTextureSize = new int[1];
		GLES10.glGetIntegerv(GL10.GL_MAX_TEXTURE_SIZE, maxTextureSize, 0);
		int maxBitmapDimension = Math.max(maxTextureSize[0],
				DEFAULT_MAX_BITMAP_DIMENSION);
		maxBitmapSize = new ImageSize(maxBitmapDimension, maxBitmapDimension);
	}

	private ImageSizeUtil() {
	}

	public static class ImageSize {
		int width;
		int height;

		public ImageSize() {
		}

		public ImageSize(int width, int height) {
			this.width = width;
			this.height = height;
		}

		public int getWidth() {
			return width;
		}

		public void setWidth(int width) {
			this.width = width;
		}

		public int getHeight() {
			return height;
		}

		public void setHeight(int height) {
			this.height = height;
		}
	}

	/**
	 * 根据传递进来的imageView，后去它的大小
	 * 
	 * @param imageView
	 * @return
	 */
	public static ImageSize getImageViewSize(ImageView imageView) {
		ImageSize imageSize = new ImageSize();

		DisplayMetrics displayMetrics = imageView.getContext().getResources()
				.getDisplayMetrics();
		ViewGroup.LayoutParams layoutParams = imageView.getLayoutParams();
		// 1.直接获取image的width
		int width = imageView.getWidth();
		// 2.通过布局文件获取image的width
		if (width <= 0)
			width = layoutParams.width;
		// 3.使用屏幕的宽度
		if (width <= 0)
			width = displayMetrics.widthPixels;
		// 同理获取image的height
		int height = imageView.getHeight();
		if (height <= 0)
			height = layoutParams.height;
		if (height <= 0)
			height = displayMetrics.heightPixels;

		imageSize.width = width;
		imageSize.height = height;
		return imageSize;
	}

	/**
	 * 根据BitmapFactory.Options中图片实际大小和传递的需求大小计算出InSampleSize
	 * 
	 * @param options
	 * @param reqWidth
	 * @param reqHeight
	 * @return
	 */
	public static int caculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		// int inSampleSize = 1;
		// int width = options.outWidth;
		// int height = options.outHeight;
		// if (width > reqWidth || height > reqHeight) {
		// // 取整
		// int widthRadio = Math.round((float)width / (float)reqWidth);
		// int heightRadio = Math.round((float)height / (float)reqHeight);
		// inSampleSize = Math.max(widthRadio, heightRadio);
		// }
		// return inSampleSize;

		return computeImageSampleSize(options, reqWidth, reqHeight, true);
	}

	public static int computeImageSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight, boolean powerOf2Scale) {
		int srcWidth = options.outWidth;
		int srcHeight = options.outHeight;
		int targetWidth = reqWidth;
		int targetHeight = reqWidth;
		int scale = 1;
		if (powerOf2Scale) {
			int halfWidth = srcWidth / 2;
			int halfHeight = srcHeight / 2;
			while ((halfWidth / scale) > targetWidth
					|| (halfHeight / scale) > targetHeight) { // ||
				scale *= 2;
			}
		} else {
			scale = Math.max(srcWidth / targetWidth, srcHeight / targetHeight); // max
		}
		if (scale < 1) {
			scale = 1;
		}
		scale = considerMaxTextureSize(srcWidth, srcHeight, scale,
				powerOf2Scale);

		return scale;
	}

	private static int considerMaxTextureSize(int srcWidth, int srcHeight,
			int scale, boolean powerOf2) {
		final int maxWidth = maxBitmapSize.getWidth();
		final int maxHeight = maxBitmapSize.getHeight();
		while ((srcWidth / scale) > maxWidth || (srcHeight / scale) > maxHeight) {
			if (powerOf2) {
				scale *= 2;
			} else {
				scale++;
			}
		}
		return scale;
	}
}
