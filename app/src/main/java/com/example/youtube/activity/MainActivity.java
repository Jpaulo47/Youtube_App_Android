package com.example.youtube.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.youtube.R;
import com.example.youtube.adapter.AdapterVideo;
import com.example.youtube.api.YoutubeService;
import com.example.youtube.helper.RetrofitConfig;
import com.example.youtube.helper.YoutubeConfig;
import com.example.youtube.listeners.RecyclerItemClickListener;
import com.example.youtube.model.Item;
import com.example.youtube.model.Resultado;
import com.example.youtube.model.Video;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MainActivity extends YouTubeBaseActivity {

    private RecyclerView recyclerVideos;
    private List<Item> videos = new ArrayList<>();
    private AdapterVideo adapterVideo;
    private Resultado resultado;

    //Retrofit
    private Retrofit retrofit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //Configura toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("TV Leão");
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));

        //Configurações iniciais

        retrofit = RetrofitConfig.getRetrofit();

        //inicializar componentes
        recyclerVideos = findViewById(R.id.recyclerVideos);

        //recupera videos
        recuperarVideos();


    }

    private void recuperarVideos(){

        YoutubeService youtubeService = retrofit.create( YoutubeService.class );
        youtubeService.recuperarVideos("snippet", "date", "20", YoutubeConfig.CHAVE_YOUTUBE_API, YoutubeConfig.CANAL_ID).enqueue(new Callback<Resultado>() {
            @Override
            public void onResponse(Call<Resultado> call, Response<Resultado> response) {

               // Log.d("resultado", "resultado" + response.toString());

                if ( response.isSuccessful() ){
                    resultado = response.body();
                    videos = resultado.items;
                    configurarRecyclerView();
                }
            }

            @Override
            public void onFailure(Call<Resultado> call, Throwable t) {

            }
        });
    }

    public void configurarRecyclerView(){

        adapterVideo = new AdapterVideo(videos, this);
        recyclerVideos.setHasFixedSize( true );
        recyclerVideos.setLayoutManager(new LinearLayoutManager(this));
        recyclerVideos.setAdapter( adapterVideo );

        recyclerVideos.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        this,
                        recyclerVideos,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {

                                Item video = videos.get( position );
                                String idVideo = video.id.videoId;

                                Intent i = new Intent(MainActivity.this, PlayerActivity.class);
                                i.putExtra("idVideo", idVideo);
                                startActivity(i);
                            }

                            @Override
                            public void onLongItemClick(View view, int position) {

                            }

                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                            }
                        }
                ) {
        });


    }

}