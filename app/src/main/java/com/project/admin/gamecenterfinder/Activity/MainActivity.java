package com.project.admin.gamecenterfinder.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.support.v7.widget.SearchView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.project.admin.gamecenterfinder.Fragment.SearchFragment;
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

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    Toolbar main_toolbar;
    DrawerLayout drawer;
    private final int BACK_DRAWER = 0;
    private final int BACK_SEARCH = 1;
    int backState = BACK_DRAWER;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setDefaultToolbar();

        drawer = (DrawerLayout) findViewById(R.id.main_drawerlayout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, main_toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.main_drawer_navigationView);
        navigationView.setNavigationItemSelectedListener(this);

        View header = navigationView.getHeaderView(0);
        ImageView imageView = header.findViewById(R.id.drawer_header_imageview);

        Glide.with(this)
                .load(R.drawable.img_lights)
                .thumbnail(0.2f)
                .apply(new RequestOptions().circleCrop().override(200, 200))
                .into(imageView);

    }

    void setDefaultToolbar() {
        main_toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(main_toolbar);
        main_toolbar.inflateMenu(R.menu.search);


        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false); //액션바에 표시되는 제목의 표시유무를 설정

        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.mipmap.ic_dehaze_white_18dp);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.search, menu);

        SearchView searchView = (SearchView) menu.findItem(R.id.toolbar_menu_search).getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Intent intent = new Intent(MainActivity.this, ListActivity.class);
                intent.putExtra("centerName", query);
                startActivity(intent);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home :
                onBackPressed();
                return true;
            case R.id.toolbar_menu_search:
                backState = BACK_SEARCH;
                main_toolbar.setBackgroundColor(Color.parseColor("#ffffff"));
                getSupportFragmentManager().beginTransaction().replace(R.id.mainActivity_frameLayout, new SearchFragment()).commit();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.navigation_search:
                break;
            case R.id.navigation_favorite:
                break;
            case R.id.navigation_list:
                Intent intent = new Intent(this, ListActivity.class);
                startActivity(intent);
                break;
            case R.id.navigation_setting:
                break;
            case R.id.navigation_info:
                showDialog();
                break;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("정보");

        String msg = "나무위키 수정하면 좀 더 정확한 정보를 얻으실 수 있습니다. - 리갤 흥해라";
        builder.setMessage(msg).setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).setNegativeButton("나무위키", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://namu.wiki/w/%ED%85%9C%ED%94%8C%EB%A6%BF:%EC%98%A4%EB%9D%BD%EC%8B%A4")));
            }
        });
        builder.create().show();
    }

    @Override
    public void onBackPressed() {
        switch(backState) {
            case BACK_DRAWER :
                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                } else {
                    super.onBackPressed();
                }
                break;
            case BACK_SEARCH :
                super.onBackPressed();
                main_toolbar.setBackgroundColor(Color.parseColor(String.valueOf(getResources().getColor(R.color.colorPrimary))));
                backState = BACK_DRAWER;
                break;
        }

    }


}
