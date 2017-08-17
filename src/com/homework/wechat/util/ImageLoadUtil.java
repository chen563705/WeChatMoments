package com.homework.wechat.util;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.util.LruCache;
import android.widget.ImageView;

import com.homework.wechat.MyApplication;
import com.homework.wechat.R;
import com.homework.wechat.constant.Constant.QueueType;


public class ImageLoadUtil {

	public static final String TAG = "ImageLoadUtil";

	private static ImageLoadUtil mInstance;
	private LruCache<String, Bitmap> mLruCache; // ͼƬ����ĺ��Ķ���
	private ExecutorService mThreadPool; // �̳߳�
	//һ���Լ���5��tweets��5*9+5+2=52���������Ҫ52���߳�ȥ����ͼƬ
	private static final int DEFAULT_THREAD_COUNT = 55;

	private LinkedList<Runnable> mTaskQueue; // �������

	private Thread mBackstageThread; // ��̨��ѯ�߳�
	private Handler mBackstageThreadHandler;

	// Semaphore, ������Э�������߳�,
	private Semaphore mBackstageThreadSemaphore;
	private Semaphore mBackstageThreadHandlerSemaphore = new Semaphore(0);

	private static final Object syncObject = new Object(); // ����ģʽ &&
															// synchronized

	private QueueType mType = QueueType.LIFO;

	private boolean isDiskCacheEnable = true; // Ӳ�̻������

	// UI Thread
	private Handler mUIHandler;

	/**
	 * ����ģʽ
	 * 
	 * @param threadCount
	 * @return
	 */
	public static ImageLoadUtil getInstance(int threadCount, QueueType type) {
		if (mInstance == null) {
			synchronized (syncObject) {
				if (mInstance == null) {
					mInstance = new ImageLoadUtil(threadCount, type);
				}
			}
		}
		return mInstance;
	}

	public static ImageLoadUtil getInstance() {
		if (mInstance == null) {
			synchronized (syncObject) {
				if (mInstance == null) {
					mInstance = new ImageLoadUtil(DEFAULT_THREAD_COUNT,
							QueueType.LIFO);
				}
			}
		}
		return mInstance;
	}

	private ImageLoadUtil(int threadCount, QueueType type) {
		init(threadCount, type);
	}

	private void init(int threadCount, QueueType type) {

		initBackThread();
		// get the max available memory
		int cacheMemory = MyApplication.getInstance().getMemoryCacheSize();
		// �̳�LruCacheʱ������Ҫ��дsizeof���������ڼ���ÿ����Ŀ�Ĵ�С
		mLruCache = new LruCache<String, Bitmap>(cacheMemory) {
			@Override
			protected int sizeOf(String key, Bitmap value) {
				return value.getByteCount() / 1024;
			}
		};

		// create thread pool that
		// queue.
		mThreadPool = Executors.newFixedThreadPool(threadCount);
		mTaskQueue = new LinkedList<>();
		mType = type;
		mBackstageThreadSemaphore = new Semaphore(threadCount);
	}

	/**
	 * ��ʼ����̨��ѯ�߳�
	 */
	private void initBackThread() {
		mBackstageThread = new Thread() {
			@Override
			public void run() {
				Looper.prepare();
				mBackstageThreadHandler = new Handler() {
					@Override
					public void handleMessage(Message msg) {
						mThreadPool.execute(getTask()); // �̳߳�ȥȡ��һ���������ִ��
						try {
							mBackstageThreadSemaphore.acquire();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				};
				mBackstageThreadHandlerSemaphore.release(); // �ͷ�һ���ź���
				Looper.loop();
			}
		};
		mBackstageThread.start();
	}

	public void loadImage(String path, final ImageView imageView,
			boolean isFromNet) {
		imageView.setTag(R.id.image_ulr, path);
		if (mUIHandler == null) {
			mUIHandler = new Handler() {
				@Override
				public void handleMessage(Message msg) {
					ImageBeanHolder holder = (ImageBeanHolder) msg.obj;
					Bitmap bm = holder.bitmap;
					ImageView iv = holder.imageView;
					String path2 = holder.path;
					if (iv.getTag(R.id.image_ulr).toString().equals(path2) && bm != null) {
						iv.setImageBitmap(bm);
					}
				}
			};
		}
		// ����path�ڻ����л�ȡbitmap
		Bitmap bitmap = getBitmapFromLruCache(path);
		if (bitmap != null) {
			refreshBitmap(path, imageView, bitmap);
		} else {
			addTask(buildTask(path, imageView, isFromNet));
		}
	}

	private void refreshBitmap(String path, final ImageView imageView,
			Bitmap bitmap) {
		Message msg = Message.obtain();
		ImageBeanHolder holder = new ImageBeanHolder();
		holder.bitmap = bitmap;
		holder.path = path;
		holder.imageView = imageView;
		msg.obj = holder;
		mUIHandler.sendMessage(msg);
	}

	/**
	 * ����runnable����TaskQueue�����ͬʱʹ��mBackstageThreadHandler
	 * ȥ����һ����Ϣ����̨�̣߳�����ȥȡ��һ������ִ��
	 * 
	 * @param runnable
	 */
	private synchronized void addTask(Runnable runnable) {
		mTaskQueue.add(runnable);
		try {
			if (mBackstageThreadHandler == null)
				mBackstageThreadHandlerSemaphore.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		mBackstageThreadHandler.sendEmptyMessage(0x110); //
	}

	/**
	 * ���Ǹ���Type���������ͷ����β����ȡ����
	 * 
	 * @return
	 */
	private Runnable getTask() {
		if (mType == QueueType.FIFO) {
			return mTaskQueue.removeFirst();
		} else if (mType == QueueType.LIFO) {
			return mTaskQueue.removeLast();
		}
		return null;
	}

	/**
	 * �½�����˵�����ڴ���û���ҵ������bitmap�����ǵ��������ȥ����path����ѹ�����bitmap���ؼ��ɣ�Ȼ�����LruCache��
	 * ���ûص���ʾ�� ���������ж��Ƿ����������� ����ǣ�����ȥӲ�̻�������һ�£���Ӳ�����ļ���Ϊ������path���ɵ�md5Ϊ���ƣ���
	 * ���Ӳ�̻�����û�У���ôȥ�ж��Ƿ�����Ӳ�̻��棺
	 * �����˵Ļ�������ͼƬ��ʹ��loadImageFromLocal���ؼ���ͼƬ�ķ�ʽ���м��أ�ѹ���Ĵ���ǰ���Ѿ���ϸ˵������
	 * ���û�п�������ֱ�Ӵ������ȡ��ѹ����ȡ�Ĵ��룬ǰ����ϸ˵������
	 * �����������ͼƬ��ֱ��loadImageFromLocal���ؼ���ͼƬ�ķ�ʽ���м���
	 * �������棬�ͻ����bitmap��Ȼ�����addBitmapToLruCache��refreashBitmap�ص���ʾͼƬ
	 * 
	 * @param path
	 * @param imageView
	 * @param isFromNet
	 * @return
	 */
	private Runnable buildTask(final String path, final ImageView imageView,
			final boolean isFromNet) {
		return new Runnable() {
			@Override
			public void run() {
				Bitmap bitmap = null;
				if (isFromNet) {
					File file = getDiskCacheDir(imageView.getContext(),
							md5(path));
					if (file.exists()) { // ��������Ѿ������˸��ļ�
						bitmap = loadImageFromLocal(file.getAbsolutePath(),
								imageView);
						if (bitmap == null)
							Log.d(TAG, "load image failed from local: " + path);
					} else { // ��Ҫ����������
						if (isDiskCacheEnable) { // ����Ƿ���Ӳ�̻���
							boolean downloadState = DownloadImgUtil
									.downloadImageByUrl(path, file);
							if (downloadState) {
								bitmap = loadImageFromLocal(
										file.getAbsolutePath(), imageView);
							}
							if (bitmap == null)
								Log.d(TAG,
										"download image failed to diskcache("
												+ path + ")");
						} else { // ֱ�Ӵ�������ص�imageView
							bitmap = DownloadImgUtil.downloadImageByUrl(path,
									imageView);
							if (bitmap == null)
								Log.d(TAG, "download image failed to memory("
										+ path + ")");
						}
					}
				} else {
					// ���ر���ͼƬ
					bitmap = loadImageFromLocal(path, imageView);
				}
				addBitmapToLruCache(path, bitmap);
				refreshBitmap(path, imageView, bitmap);
				mBackstageThreadSemaphore.release();
			}
		};
	}

	/**
	 * ʹ��loadImageFromLocal���ؼ���ͼƬ�ķ�ʽ���м���
	 * 
	 * @param path
	 * @param imageView
	 * @return
	 */
	private Bitmap loadImageFromLocal(final String path,
			final ImageView imageView) {
		Bitmap bitmap = null;
		// 1�����ͼƬ��Ҫ��ʾ�Ĵ�С
		ImageSizeUtil.ImageSize imageSize = ImageSizeUtil
				.getImageViewSize(imageView);
		// 2��ѹ��ͼƬ
		bitmap = decodeSampledBitmapFromPath(path, imageSize.width,
				imageSize.height);
		return bitmap;
	}

	/**
	 * ��ͼƬ����LruCache
	 * 
	 * @param path
	 * @param bitmap
	 */
	protected void addBitmapToLruCache(String path, Bitmap bitmap) {
		if (getBitmapFromLruCache(path) == null) {
			if (bitmap != null)
				mLruCache.put(path, bitmap);
		}
	}

	/**
	 * ����ͼƬ��Ҫ��ʾ�Ŀ�͸߶�ͼƬ����ѹ��
	 * 
	 * @param path
	 * @param width
	 * @param height
	 * @return
	 */
	protected Bitmap decodeSampledBitmapFromPath(String path, int width,
			int height) {
		// ���ͼƬ�Ŀ�͸ߣ�������ͼƬ���ص��ڴ���
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path, options);

		options.inSampleSize = ImageSizeUtil.caculateInSampleSize(options,
				width, height);
		// ʹ�û�õ���InSampleSize�ٴν���ͼƬ
		options.inJustDecodeBounds = false;
		Bitmap bitmap = BitmapFactory.decodeFile(path, options);

		if (null == bitmap)
			Log.d(TAG, "options.inSampleSize = " + options.inSampleSize + ", "
					+ path);
		return bitmap;
	}

	/**
	 * ��û���ͼƬ�ĵ�ַ
	 * 
	 * @param context
	 * @param uniqueName
	 * @return
	 */
	public File getDiskCacheDir(Context context, String uniqueName) {
		
		String cachePath = context.getCacheDir().getPath();
		
		return new File(cachePath + File.separator + uniqueName);
	}

	/**
	 * ����path�ڻ����л�ȡbitmap
	 * 
	 * @param key
	 * @return
	 */
	private Bitmap getBitmapFromLruCache(String key) {
		return mLruCache.get(key);
	}

	/**
	 * ����ǩ�������࣬���ַ����ֽ�����
	 * 
	 * @param str
	 * @return
	 */
	public String md5(String str) {
		byte[] digest = null;
		try {
			MessageDigest md = MessageDigest.getInstance("md5");
			digest = md.digest(str.getBytes());
			return bytes2hex02(digest);

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * ��ʽ��
	 * 
	 * @param bytes
	 * @return
	 */
	public String bytes2hex02(byte[] bytes) {
		StringBuilder sb = new StringBuilder();
		String tmp = null;
		for (byte b : bytes) {
			// ��ÿ���ֽ���0xFF���������㣬Ȼ��ת��Ϊ10���ƣ�Ȼ�������Integer��ת��Ϊ16����
			tmp = Integer.toHexString(0xFF & b);
			if (tmp.length() == 1)// ÿ���ֽ�8Ϊ��תΪ16���Ʊ�־��2��16����λ
			{
				tmp = "0" + tmp;
			}
			sb.append(tmp);
		}

		return sb.toString();
	}
    
	/**
	 * Ӧ�ó���رպ������̳߳�
	 */
	public void shutDownThreadPool() {
		mThreadPool.shutdown();
	}

	private class ImageBeanHolder {
		Bitmap bitmap;
		ImageView imageView;
		String path;
	}

}
