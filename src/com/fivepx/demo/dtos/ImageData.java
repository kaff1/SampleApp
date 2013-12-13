package com.fivepx.demo.dtos;

import com.google.gson.annotations.SerializedName;

public class ImageData {
	@SerializedName("id")
	private long id;
	
	@SerializedName("name")
	private String title;
	
	@SerializedName("image_url")
	private String imageUrl;
	
	public long getId() {
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
	}
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getImageUrl() {
		return imageUrl;
	}
	
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	
}
