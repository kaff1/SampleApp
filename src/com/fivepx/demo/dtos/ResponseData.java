package com.fivepx.demo.dtos;

import com.google.gson.annotations.SerializedName;

public class ResponseData {
	@SerializedName("current_page")
	private int currentPage;
	
	@SerializedName("photos")
	private ImageData[] photos;
	
	public int getCurrentPage() {
		return currentPage;
	}
	
	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}
	
	public ImageData[] getPhotos() {
		return photos;
	}
	
	public void setImageData(ImageData[] photos) {
		this.photos = photos;
	}
}
