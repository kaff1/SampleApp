package com.fivepx.demo.managers;

import java.util.ArrayList;
import java.util.List;

import com.fivepx.demo.dtos.ImageData;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

public class ImageManager {
    final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
    final int cacheSize = maxMemory / 4;

	private LruCache<Long, Bitmap> imageCache;
	private List<ImageData> imageData;
	private static ImageManager instance;
	private int lastFetchTrigger = 0;
	private int nextPageToFetch = 0;
	
	private ImageManager() {
		imageCache = new LruCache<Long, Bitmap>(cacheSize) {
	        @Override
	        protected int sizeOf(Long key, Bitmap bitmap) {
	            return bitmap.getByteCount() / 1024;
	        }
		};
	}
	
	public static ImageManager getInstance() {
		if (instance == null) {
			instance = new ImageManager();
		}
		
		return instance;
	}
	
	public Bitmap getImage(long key) {
		return imageCache.get(key);
	}
	
	public void putImage(long key, Bitmap image) {
		imageCache.put(key, image);
	}
	
	public void storeImageData(List<ImageData> imageData) {
		this.imageData = new ArrayList<ImageData>(imageData);
	}
	
	public List<ImageData> getStoredImageData() {
		return imageData;
	}
	
	public int getLastFetchTrigger() {
		return lastFetchTrigger;
	}
	
	public void setLastFetchTrigger(int fetchTrigger) {
		lastFetchTrigger = fetchTrigger;
	}
	
	public int getNextPageToFetch() {
		return nextPageToFetch;
	}
	
	public void setNextPageToFetch(int nextPage) {
		nextPageToFetch = nextPage;
	}
	
}
