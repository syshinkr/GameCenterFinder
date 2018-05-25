package com.project.admin.gamecenterfinder.Activity;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.project.admin.gamecenterfinder.Fragment.DetailFragment;
import com.project.admin.gamecenterfinder.Model.GCListModel;
import com.project.admin.gamecenterfinder.R;
import com.project.admin.gamecenterfinder.Server.Client;
import com.project.admin.gamecenterfinder.Server.Code;
import com.project.admin.gamecenterfinder.Server.InterfaceRetrofit;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListActivity extends AppCompatActivity {
    String centerName = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        if (savedInstanceState == null) {
            Bundle bundleCenterName = getIntent().getExtras();
            if (bundleCenterName != null) {
                centerName = bundleCenterName.getString("centerName");
            }
        }

        setToolbar();
        setRecyclerView();
    }

    private void setToolbar() {
        Toolbar toolbar = findViewById(R.id.favorite_toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    }

    private void setRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.list_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        if (centerName == null) {
            recyclerView.setAdapter(new ListRecyclerViewAdapter(this.getApplicationContext()));
        } else {
            recyclerView.setAdapter(new ListRecyclerViewAdapter(this.getApplicationContext(), centerName));
        }
    }

    class ListRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        List<GCListModel> gcListModels = new ArrayList<>();
        Context context;

        public ListRecyclerViewAdapter(Context context) {
            this.context = context;
            getData();
        }

        public ListRecyclerViewAdapter(Context context, String centerName) {
            this.context = context;
            getCenterForName(centerName);
        }

        private void getData() {
            InterfaceRetrofit retrofit = Client.getInstance().create(InterfaceRetrofit.class);
            Call<List<GCListModel>> call = retrofit.getCenterData();
            call.enqueue(new Callback<List<GCListModel>>() {
                @Override
                public void onResponse(Call<List<GCListModel>> call, Response<List<GCListModel>> response) {
                    Log.i("RvAdapter", response.raw().request().url().toString());
                    switch (response.code()) {
                        case Code.RESULT_SUCCESS:
                            gcListModels = (ArrayList<GCListModel>) response.body();
                            notifyDataSetChanged();

                            Toast.makeText(ListActivity.this, "데이터 받아오기 성공", Toast.LENGTH_SHORT).show();
                            Log.i("MAIN", "데이터 받아오기 성공, 사이즈 : " + gcListModels.size());
                            break;
                    }
                }

                @Override
                public void onFailure(Call<List<GCListModel>> call, Throwable t) {
                    Log.i("MAIN", "fetch 실패 " + t.getMessage());
                    Snackbar.make(getWindow().getDecorView().getRootView(), "fetch 실패\n에러메시지 : " + t.getMessage().toString(), Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    });
                }
            });
        }

        void getCenterForName(String centerName) {
            InterfaceRetrofit retrofit = Client.getInstance().create(InterfaceRetrofit.class);
            Call<List<GCListModel>> call = retrofit.getCenterDataForName(centerName);
            call.enqueue(new Callback<List<GCListModel>>() {
                @Override
                public void onResponse(Call<List<GCListModel>> call, Response<List<GCListModel>> response) {
                    switch (response.code()) {
                        case Code.RESULT_SUCCESS:
                            gcListModels = response.body();
                            notifyDataSetChanged();
                            break;
                    }
                }

                @Override
                public void onFailure(Call<List<GCListModel>> call, Throwable t) {

                }
            });
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
            return new CustomViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {

            CustomViewHolder viewHolder = (CustomViewHolder) holder;

            viewHolder.name.setText(gcListModels.get(position).name);
            viewHolder.address.setText(gcListModels.get(position).address);
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DetailFragment detailFragment = new DetailFragment().newInstance(gcListModels.get(position));
                    detailFragment.show(getSupportFragmentManager(), "game machine dialog");
                }
            });
        }

        @Override
        public int getItemCount() {
            return gcListModels.size();
        }

        private class CustomViewHolder extends RecyclerView.ViewHolder {
            TextView name;
            TextView address;

            public CustomViewHolder(View view) {
                super(view);
                name = view.findViewById(R.id.listitem_name);
                address = view.findViewById(R.id.listitem_address);
            }
        }
    }
}
