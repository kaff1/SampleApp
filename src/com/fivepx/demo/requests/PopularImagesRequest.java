package com.fivepx.demo.requests;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.fivepx.demo.dtos.ResponseData;

public class PopularImagesRequest {
	private static final String popImagesRoute =
			"https://api.500px.com/v1/photos?feature=popular" +
			"&consumer_key=JElLcyzH2xCJ7MJTgBahmt5D8pViM7akaQnjFJ7R&sort=rating&rpp=%s&page=%s&image_size=3";
	
	private GsonRequest<ResponseData> req;
	
	public PopularImagesRequest(int itemsPerPage, int page, Listener<ResponseData> listener, ErrorListener errorListener) {
		String fetchRoute = String.format(popImagesRoute, itemsPerPage, page);
		req = new GsonRequest<ResponseData> (fetchRoute, ResponseData.class, null, listener, errorListener);
	}
	
	public GsonRequest<ResponseData> getRequest() {
		return req;
	}
}
