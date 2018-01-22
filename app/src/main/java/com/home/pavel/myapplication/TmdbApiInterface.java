package com.home.pavel.myapplication;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface TmdbApiInterface {
    @GET("/3/discover/movie")
    Call<RequestDTO> getAllFilms(@Query("api_key") String apiKey,
                                 @Query("language") String language,
                                 @Query("page") Integer page);
    @GET("/3/search/movie")
    Call<RequestDTO> getFiltredFilms(@Query("api_key") String apiKey,
                                     @Query("language") String language,
                                     @Query("page") Integer page,
                                     @Query("query") String query);
}
