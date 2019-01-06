package com.example.julianschoemaker.eisws1819mayerschoemaker;

import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GitHubService {
    @GET("topics")
    // oder void
    Response getMyThing(
            @Query("name") String name1);
}