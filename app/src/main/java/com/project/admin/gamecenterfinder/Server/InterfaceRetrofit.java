package com.project.admin.gamecenterfinder.Server;

import com.project.admin.gamecenterfinder.Model.GCListModel;
import com.project.admin.gamecenterfinder.Model.GamesModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface InterfaceRetrofit {
    @GET(Url.DEFAULT_LIST)
    Call<List<GCListModel>> getCenterData(
    );

    @GET(Url.GAME_LIST)
    Call<List<GamesModel>> getGameData(
            @Query("center_id") String id
    );

    @GET(Url.DEFAULT_LIST)
    Call<List<GCListModel>> getCenterDataForName(
            @Query("center_name") String center_name
    );
}
