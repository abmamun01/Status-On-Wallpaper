package com.mamunsproject.status_onwallpaper_api.Utilities;

import com.mamunsproject.status_onwallpaper_api.Api.SearchModel;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

import static com.mamunsproject.status_onwallpaper_api.Utilities.ApiUtilities.API;

public interface ApiInterface {

    String BASE_URL = "https://api.pexels.com/v1/";

    @Headers("AUTHORIZATION:" + API)
    @GET("curated")
    Call<SearchModel> getImage(
            @Query("page") int page,
            @Query("per_page") int per_page
    );

    @Headers("AUTHORIZATION:" + API)
    @GET("search")
    Call<SearchModel> getSearchImage(
            @Query("query") String query,
            @Query("page") int page,
            @Query("per_page") int per_page
    );


}
