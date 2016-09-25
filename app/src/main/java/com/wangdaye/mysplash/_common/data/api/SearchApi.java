package com.wangdaye.mysplash._common.data.api;

import com.wangdaye.mysplash._common.data.data.SearchCollectionsResult;
import com.wangdaye.mysplash._common.data.data.SearchPhotosResult;
import com.wangdaye.mysplash._common.data.data.SearchUsersResult;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Search api.
 * */

public interface SearchApi {

    @GET("search/photos")
    Call<SearchPhotosResult> searchPhotos(@Query("query") String query,
                                          @Query("page") int page);

    @GET("search/users")
    Call<SearchUsersResult> searchUsers(@Query("query") String query,
                                        @Query("page") int page);

    @GET("search/collections")
    Call<SearchCollectionsResult> searchCollections(@Query("query") String query,
                                                    @Query("page") int page);
}
