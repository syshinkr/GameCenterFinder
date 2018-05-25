package com.project.admin.gamecenterfinder.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.project.admin.gamecenterfinder.Model.GCListModel;
import com.project.admin.gamecenterfinder.Model.GamesModel;
import com.project.admin.gamecenterfinder.R;
import com.project.admin.gamecenterfinder.Server.Client;
import com.project.admin.gamecenterfinder.Server.Code;
import com.project.admin.gamecenterfinder.Server.InterfaceRetrofit;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailFragment extends BottomSheetDialogFragment {
    private static final String ARG_NAME = "name";
    private static final String ARG_ADDRESS = "address";
    private static final String ARG_OPENING = "opening";
    private static final String ARG_ID = "id";
    private static final String ARG_LATITUDE = "latitude"; //위도
    private static final String ARG_LONGITUDE = "longitude"; //경도

    private GCListModel centerModel = new GCListModel();
    private String latitude;
    private String longitude;

    public DetailFragment() {
        // Required empty public constructor
    }

    public static DetailFragment newInstance(GCListModel gcListModel) {
        DetailFragment fragment = new DetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_NAME, gcListModel.name);
        args.putString(ARG_ADDRESS, gcListModel.address);
        args.putString(ARG_OPENING, gcListModel.opening);
        args.putString(ARG_ID, gcListModel.id);

        args.putString(ARG_LATITUDE, gcListModel.location.get(0).toString());
        args.putString(ARG_LONGITUDE, gcListModel.location.get(1).toString());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            centerModel.name = getArguments().getString(ARG_NAME);
            centerModel.address = getArguments().getString(ARG_ADDRESS);
            centerModel.opening = getArguments().getString(ARG_OPENING);
            centerModel.id = getArguments().getString(ARG_ID);
            latitude = getArguments().getString(ARG_LONGITUDE);
            longitude = getArguments().getString(ARG_LATITUDE);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.bottomSheetDialog_recyclerview);
        recyclerView.setAdapter(new DetailRecyclerViewAdapter());
        recyclerView.setLayoutManager(new LinearLayoutManager(inflater.getContext()));

        TextView textView_name = view.findViewById(R.id.bottomSheetDialog_textview_title);
        TextView textView_address = view.findViewById(R.id.bottomSheetDialog_textview_address);
        TextView textView_opening = view.findViewById(R.id.bottomSheetDialog_textview_opening);

        textView_name.setText(centerModel.name);
        textView_address.setText(centerModel.address);
        textView_opening.setText(centerModel.opening);

        ImageButton imageButton = view.findViewById(R.id.bottomSheetDialog_imagebutton);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //url 스키마 추가 요망
//                Intent intent = new Intent(Intent.ACTION_VIEW, );
            }
        });

        return view;
    }

    class DetailRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        List<GamesModel> gameModels = new ArrayList<>();

        public DetailRecyclerViewAdapter() {
            getGamesModel(centerModel.id);
        }

        private void getGamesModel(String centerId) {
            InterfaceRetrofit retrofit = Client.getInstance().create(InterfaceRetrofit.class);
            Call<List<GamesModel>> call = retrofit.getGameData(centerId);
            call.enqueue(new Callback<List<GamesModel>>() {
                @Override
                public void onResponse(Call<List<GamesModel>> call, Response<List<GamesModel>> response) {
                    Log.i("detail", response.code() + "");
                    switch (response.code()) {
                        case Code.RESULT_SUCCESS :
                            gameModels = response.body();
                            notifyDataSetChanged();
                            Log.d("detail", "game model size : " + gameModels.size());
                            break;
                    }

                }

                @Override
                public void onFailure(Call<List<GamesModel>> call, Throwable t) {
                    Snackbar.make(getView(), "fetch 실패\n에러메시지 : " + t.getMessage().toString(), Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    });
                }
            });
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.games_item, parent, false);
            return new DetailViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            DetailViewHolder detailViewHolder = (DetailViewHolder)holder;
            detailViewHolder.textView.setText(gameModels.get(position).name + " : " + gameModels.get(position).count);

        }

        @Override
        public int getItemCount() {
            return gameModels.size();
        }

        private class DetailViewHolder extends RecyclerView.ViewHolder {
            TextView textView; //게임기 + 갯수

            public DetailViewHolder(View view) {
                super(view);
                textView = view.findViewById(R.id.gamesItem_text);
            }
        }
    }
}
