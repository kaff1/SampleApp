package com.fivepx.demo.fragments;

import java.util.ArrayList;
import java.util.Arrays;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.GridView;

import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.fivepx.demo.R;
import com.fivepx.demo.adapters.PopularAdapter;
import com.fivepx.demo.dtos.ImageData;
import com.fivepx.demo.dtos.ResponseData;
import com.fivepx.demo.requests.PopularImagesRequest;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.LifecycleCallback;
import de.keyboardsurfer.android.widget.crouton.Style;

public class PopularFragment extends Fragment implements
		Listener<ResponseData>, ErrorListener {
	private final int firstFetchPerPage = 100;
	private final int subsequentFetchPerPage = 20;
	private final int firstPageToFetch = 1;

	private GridView imageGrid;
	private View emptyView;
	private View loadingView;
	private RequestQueue reqQueue;
	private PopularAdapter adapter;
	private boolean firstFetch;
	private int nextPageToFetch;
	private int lastFetchTrigger;
	private Crouton errorCrouton;
	private boolean croutonShowing = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		firstFetch = true;

		adapter = new PopularAdapter(getActivity());
		lastFetchTrigger = adapter.getLastFetchTrigger();
		nextPageToFetch = adapter.getNextPageToFetch() != 0 
				? adapter.getNextPageToFetch() : (firstFetchPerPage / subsequentFetchPerPage) + 1;;

		reqQueue = Volley.newRequestQueue(getActivity());
	}

	@Override
	public void onResume() {
		super.onResume();
		if (adapter.getCount() == 0) {
			reqQueue.add(new PopularImagesRequest(firstFetchPerPage,
					firstPageToFetch, this, this).getRequest());
		} else {
			showLoading(false);
			firstFetch = false;
		}
		reqQueue.start();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.popular, container);

		loadingView = v.findViewById(R.id.loading);

		imageGrid = (GridView) v.findViewById(R.id.popularImages);
		emptyView = v.findViewById(R.id.popularImagesEmpty);

		imageGrid.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				if ((totalItemCount > 0)
						&& ((firstVisibleItem - lastFetchTrigger) >= 10)) {
					reqQueue.add(new PopularImagesRequest(
							subsequentFetchPerPage, nextPageToFetch,
							PopularFragment.this, PopularFragment.this)
							.getRequest());
					lastFetchTrigger = firstVisibleItem;
					nextPageToFetch++;
					
					// Store our fetch params in case of suspend/rotate/etc
					adapter.setLastFetchTrigger(lastFetchTrigger);
					adapter.setNextPageToFetch(nextPageToFetch);
				}
			}

			@Override
			public void onScrollStateChanged(AbsListView arg0, int arg1) {
				// Nothing to do here
			}

		});

		imageGrid.setAdapter(adapter);

		return v;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.image_fragment_menu, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_hide_titles:
			adapter.toggleTitles();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onErrorResponse(VolleyError error) {
		if (errorCrouton == null) {
			errorCrouton = Crouton.makeText(getActivity(),
					R.string.genericError, Style.ALERT);
			errorCrouton.setLifecycleCallback(new LifecycleCallback() {

				@Override
				public void onDisplayed() {
					croutonShowing = true;
				}

				@Override
				public void onRemoved() {
					croutonShowing = false;
				}
			});
		}

		showLoading(false);
		if (adapter.getCount() == 0) {
			imageGrid.setEmptyView(emptyView);
		}

		if (!croutonShowing) {
			errorCrouton.show();
		}
	}

	@Override
	public void onResponse(ResponseData response) {
		ImageData[] photos = response.getPhotos();
		if (photos != null) {
			adapter.setData(new ArrayList<ImageData>(Arrays.asList(photos)),
					firstFetch);
			firstFetch = false;
			if (adapter.getCount() == 0) {
				imageGrid.setEmptyView(emptyView);
			}
		}
		showLoading(false);
	}

	private void showLoading(boolean show) {
		if (show) {
			loadingView.setVisibility(View.VISIBLE);
			imageGrid.setVisibility(View.GONE);
		} else {
			loadingView.setVisibility(View.GONE);
			imageGrid.setVisibility(View.VISIBLE);
		}
	}
}
