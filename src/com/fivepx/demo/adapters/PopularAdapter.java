package com.fivepx.demo.adapters;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;
import com.fivepx.demo.R;
import com.fivepx.demo.dtos.ImageData;
import com.fivepx.demo.managers.ImageManager;

public class PopularAdapter extends BaseAdapter {
	private Context context;
	private List<ImageData> popularImages;
	private ImageManager imageManager;
	private RequestQueue reqQueue;
	private boolean showImageTitles = true;
	
	public PopularAdapter(Context context) {
		this.context = context;
		imageManager = ImageManager.getInstance();
		popularImages = imageManager.getStoredImageData() != null ? imageManager.getStoredImageData() : new ArrayList<ImageData>();
		reqQueue = Volley.newRequestQueue(context);
	}
	
	@Override
	public boolean areAllItemsEnabled() {
		return false;
	}

	@Override
	public int getCount() {
		return popularImages.size();
	}

	@Override
	public Object getItem(int position) {
		return popularImages.get(position);
	}

	@Override
	public long getItemId(int position) {
		return popularImages.get(position).getId();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ImageData currentImageData = popularImages.get(position);
		
		GridItemViewHolder holder;
		
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.image_grid_item, parent, false);
			holder = new GridItemViewHolder();
			holder.position = position;
			holder.itemImage = (ImageView)convertView.findViewById(R.id.grid_item_image);
			holder.itemText = (TextView)convertView.findViewById(R.id.grid_item_title);
			convertView.setTag(holder);
		} else {
			holder = (GridItemViewHolder)convertView.getTag();
		}
				
		holder.itemText.setText(currentImageData.getTitle());
		if (showImageTitles) {
			holder.itemText.setVisibility(View.VISIBLE);
		} else {
			holder.itemText.setVisibility(View.INVISIBLE);
		}
		
		Bitmap currentImage = imageManager.getImage(currentImageData.getId());
		Object imageViewTag = holder.itemImage.getTag();
		long storedImageId = 0;
		if (imageViewTag != null) {
			storedImageId = ((Long)imageViewTag).longValue();
		}
		if ((currentImage != null) && (storedImageId == currentImageData.getId())) {
			holder.itemImage.setImageBitmap(currentImage);
		} else {
			fetchImage(currentImageData.getId(), currentImageData.getImageUrl(), holder.itemImage);
			holder.itemImage.setTag(currentImageData.getId());
			holder.itemImage.setImageBitmap(null);
		}
		
		return convertView;
	}
	
	private void fetchImage(final long imageId, String imageUrl, final ImageView view) {
		reqQueue.add(new ImageRequest(imageUrl, new Listener<Bitmap>() {

			@Override
			public void onResponse(Bitmap response) {
				long viewImageId = ((Long)view.getTag()).longValue();
				if (imageId == viewImageId) {
					view.setImageBitmap(response);
				}
				imageManager.putImage(imageId, response);
			}
			
		}, 0, 0, null, null));
	}
	
	public void setData(List<ImageData> newImages, boolean firstFetch) {
		if (firstFetch) {	// New list if it's our first time setting data
			this.popularImages = new ArrayList<ImageData>(newImages);
		} else {			// Add to the existing list if we've already got some data
			this.popularImages.addAll(newImages);
		}
		notifyDataSetChanged();
		imageManager.storeImageData(this.popularImages);
	}
	
	public void toggleTitles() {
		showImageTitles = !showImageTitles;
		notifyDataSetChanged();
	}
	
	public int getLastFetchTrigger() {
		return imageManager.getLastFetchTrigger();
	}
	
	public void setLastFetchTrigger(int fetchTrigger) {
		imageManager.setLastFetchTrigger(fetchTrigger);
	}
	
	public int getNextPageToFetch() {
		return imageManager.getNextPageToFetch();
	}
	
	public void setNextPageToFetch(int nextPage) {
		imageManager.setNextPageToFetch(nextPage);
	}
	
	static class GridItemViewHolder {
		ImageView itemImage;
		TextView itemText;
		int position;
	}
}
