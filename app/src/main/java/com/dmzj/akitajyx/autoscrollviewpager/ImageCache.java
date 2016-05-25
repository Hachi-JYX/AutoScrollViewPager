package com.dmzj.akitajyx.autoscrollviewpager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.os.Handler;
import android.os.Message;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.widget.ImageView;

public class ImageCache {

	// 一级缓存 内部是LinkedHashMap
	private LruCache<String, Bitmap> cache = null;

	// 使用线程池
	private ExecutorService threadPool = null;
	private File localDir = null;

	public ImageCache(Context context) {
		threadPool = Executors.newFixedThreadPool(5);
		localDir = context.getCacheDir();// 缓存目录
		// 1/8 100M
		long maxSize = Runtime.getRuntime().maxMemory() / 8;
		// 图片大小
		cache = new LruCache<String, Bitmap>((int) maxSize) {
			// 图片大小的计算规则
			@Override
			protected int sizeOf(String key, Bitmap value) {
				int bytesRow = value.getRowBytes();
				int rowCount = value.getHeight();
				return bytesRow * rowCount;
			}
		};
	}

	// http://a.hiphotos.baidu.com/image/w%3D310/sign=35424e84143853438ccf8120a312b01f/e61190ef76c6a7efce7315cef9faaf51f2de6684.jpg
	public void disPlayImage(ImageView imageView, String url) {
		//
		// ①　访问 集合 HashMap<url,Bitmap> 比LinkedhashMap LRUCache 设计最大值 100M(
		// 超过设置内存 丢弃 访问比较少的图片 )
		// ②　访问sd/cache
		// ③　访问 网络
		Bitmap bitmap = getFromCache(url);
		if (bitmap != null) {
			Log.i("wzx", "从内存取得图片");
			imageView.setImageBitmap(bitmap);
			return;
		}
		bitmap = getFromLocal(url);
		if (bitmap != null) {
			Log.i("wzx", "从本地取得图片");
			imageView.setImageBitmap(bitmap);
			return;
		}
		getFromNet(imageView, url);
		return;
	}

	private class ImageRunnable implements Runnable {

		private String url;

		private ImageView imageView;

		public ImageRunnable(ImageView imageView, String url) {
			super();
			this.imageView = imageView;
			this.url = url;
		}

		@Override
		public void run() {
			try {
				// 在线程内存访问网络 HttpURLConnection
				// 拿到一个流
				URL urlObj = new URL(url);
				// 生成Bitmap
				HttpURLConnection conn = (HttpURLConnection) urlObj.openConnection();
				conn.setRequestMethod("GET");
				conn.setConnectTimeout(5000);
				if (200 == conn.getResponseCode()) {
					InputStream input = conn.getInputStream();
					// 使用工具快速生成bitmap对象
					byte[] bytes = StreamUtils.readInputStream(input);
					// Bitmap bitmap = BitmapFactory.decodeStream(input);
					Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

					if (bitmap != null) {

						Message msg = new Message();
						msg.what = 200;
						HashMap<String, Object> data = new HashMap<String, Object>();
						data.put("imagview", imageView);// 停止出现图片错乱问题
						data.put("bitmap", bitmap);
						msg.obj = data;
						handler.sendMessage(msg);// -->handlemesssage();
						// Log.i("wzx", "imageview");
						// Log.i("wzx", "gridview");
						// Log.i("wzx", "listview");
						// 保存到集合
						cache.put(url, bitmap);
						// 保存到文件目录
						writeToLocal(url, bitmap);

					}
					return;

				}
			} catch (Exception e) {
			}

			Message msg = new Message();
			msg.what = 404;
			handler.sendMessage(msg);
		}

	}

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == 200) {
				HashMap<String, Object> data = (HashMap<String, Object>) msg.obj;
				ImageView imageView = (ImageView) data.get("imagview");
				Bitmap bitmap = (Bitmap) data.get("bitmap");
				imageView.setImageBitmap(bitmap);
			}
		};
	};

	// 把图片保存成文件
	private void writeToLocal(String url, Bitmap bitmap) {
		try {
			String rightFileName = URLEncoder.encode(url, "utf-8");
			File imgeFile = new File(localDir.getAbsolutePath() + "/" + rightFileName);
			if (!imgeFile.exists()) {
				imgeFile.createNewFile();
			}
			// 打开一个输出流 往里面写数据
			FileOutputStream fos = new FileOutputStream(imgeFile);
			// bitmap.compress(格式 jpg png, quality, 输出流);//将图片数据写入到一个文件输出流
			bitmap.compress(CompressFormat.JPEG, 80, fos);// 将图片数据写入到一个文件输出流
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void getFromNet(ImageView imageView, String url) {
		ImageRunnable r = new ImageRunnable(imageView, url);
		// 获 取一个线程
		threadPool.execute(r);

	}

	// 从本地文件获取图片
	private Bitmap getFromLocal(String url) {
		// data/data/包名/cache
		// linux http://www.baidu.com/1.jpg
		try {// %ss%dd
			String rightFileName = URLEncoder.encode(url, "utf-8");
			File imgeFile = new File(localDir.getAbsolutePath() + "/" + rightFileName);
			// 使用图片工具类 BitmapFratory 将文件转换成图片
			Bitmap bitmap = BitmapFactory.decodeFile(imgeFile.getAbsolutePath());
			// 为了下次访问提高速度 添加到集合中
			cache.put(url, bitmap);
			return bitmap;
		} catch (Exception e) {
		}
		return null;
	}

	// 从集合取
	private Bitmap getFromCache(String url) {
		return cache.get(url);
	}

}
