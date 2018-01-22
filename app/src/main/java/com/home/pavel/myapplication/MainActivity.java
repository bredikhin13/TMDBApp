package com.home.pavel.myapplication;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.home.pavel.myapplication.model.SaveInFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;


import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class MainActivity extends AppCompatActivity {

    private OkHttpClient client;
    private SwipeRefreshLayout swipeRefreshLayout;
    //    ArrayList<Integer> favFilmId = new ArrayList<>();
    Semaphore semaphore;
    ProgressBar progressBar;
    ProgressBar progressBarCyrcle;
    ListView listView;
    Retrofit retrofit;
    TmdbApiInterface apiInterface;
    //    Resp resp;
    TextView alertTextView;
    ArrayList<FilmInfo> filmInfos;
    int nextPage;
    int lastPage = 1000;
    int progress = 0;
    CustomAdapter adapter;
    String query = "";
    SearchView searchView;
    OneMoreFilmClass filmClass;
    FrameLayout frameLayout;
//    private String apiKey = "6ccd72a2a8fc239b13f209408fc31c33";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        filmClass = OneMoreFilmClass.getInstance();
        nextPage = filmClass.getNextPage();
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        searchView = findViewById(R.id.searchView);
        alertTextView = findViewById(R.id.alertTextView);
        progressBar = findViewById(R.id.progressBar);
        progressBarCyrcle = findViewById(R.id.progressBar2);
        frameLayout = findViewById(R.id.frameLayout);
        //frameLayout.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.INVISIBLE);
        progressBarCyrcle.setVisibility(View.INVISIBLE);
        client = filmClass.getClient();
        retrofit = new Retrofit.Builder()
                .baseUrl("https://api.themoviedb.org")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
        apiInterface = retrofit.create(TmdbApiInterface.class);
        listView = findViewById(R.id.listView);

//        resp = new Resp();
//        SaveInFile.openFile("fav", this, favFilmId);
        SaveInFile.openFile(Constants.FILE_NAME, this, filmClass.getFavFilms());
        semaphore = new Semaphore(1);
//        resp.setResults(new ArrayList<FilmInfo>());
        adapter = new CustomAdapter(this);
        listView.setAdapter(adapter);
        if (OneMoreFilmClass.isEmpty()) {
            getData(nextPage);
        }
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast toast = Toast.makeText(getApplicationContext(),
                        filmClass.getAllFilms().get(i).getTitle(), Toast.LENGTH_SHORT);
                toast.show();
            }
        });
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
            }

            @Override
            public void onScroll(AbsListView absListView,
                                 int firstVisibleItem,
                                 int visibleItemCount,
                                 int totalItemCount) {
                if (visibleItemCount > 0 && firstVisibleItem + visibleItemCount == totalItemCount && nextPage <= lastPage && semaphore.availablePermits() != 0) {
                    getData(nextPage);
                }
            }
        });
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (semaphore.availablePermits() != 0) {
                    nextPage = 1;
                    getData(nextPage);
                }
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                query = s;
                nextPage = 1;
                getData(nextPage);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if (s.equals("") && searchView.isFocused()) {
                    query = s;
                    nextPage = 1;
                    adapter.clearListData();
                    getData(nextPage);
                }
                return false;
            }
        });


    }


    public void getData(int page) {
        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Call<Resp> call;
        if (query.equals("")) {
            call = apiInterface.getAllFilms(Constants.API_KEY, "ru", page);
        } else {
            call = apiInterface.getFiltredFilms(Constants.API_KEY, "ru", page, query);
        }
        System.out.println("Start load");
        progress = 0;
        progressBar.setProgress(progress);
        call.enqueue(new Callback<Resp>() {
            @Override
            public void onResponse(Call<Resp> call, retrofit2.Response<Resp> response) {
                Resp resp = response.body();
                if(resp.getTotalResults()!=0) {
                    filmInfos = (ArrayList<FilmInfo>) resp.getResults();
                    //filmClass.addToAllFilms((ArrayList<FilmInfo>) resp.getResults());
//                lastPage = resp.getTotalPages();
//                Kek();
                    new GetTask().execute();
                    filmClass.setNextPage(++nextPage);
                    System.out.println("end load");
                } else {
                    alertTextView.setCompoundDrawablesWithIntrinsicBounds(0,R.drawable.ic_big_search,0,0);
                    alertTextView.setText("По запросу \""+query+"\" ничего не найдено");
                    alertTextView.setVisibility(View.VISIBLE);
                    adapter.clearListData();
                }
//                textView.setText(resp.getTotalResults().toString());
            }

            @Override
            public void onFailure(Call<Resp> call, Throwable t) {
                System.out.println("azazazazazaza");
                call.cancel();
            }
        });
    }


    public class GetTask extends AsyncTask<String, Integer, Void> {

        @Override
        protected Void doInBackground(String... params) {
            //Integer count = filmClass.getAllFilms().size();
            Integer count = filmInfos.size();
            try {
                for (FilmInfo f : filmInfos) {
                    byte[] poster = get("http://image.tmdb.org/t/p/w185/" + f.getPosterPath());
                    if (poster != null && poster.length > 0) {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(poster, 0, poster.length);
                        f.setPoster(bitmap);
                        publishProgress(progress += 100 / count);
//                        System.out.println("" + progress);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        public byte[] get(String url) throws IOException {
            Request request = new Request.Builder()
                    .url(url)
                    .build();
            Response response = client.newCall(request).execute();
            return response.body().bytes();
        }

        @Override
        protected void onPreExecute() {
            if (!OneMoreFilmClass.isEmpty()) {
                progressBar.setProgress(0);
                frameLayout.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.VISIBLE);
            } else {
                progressBarCyrcle.setVisibility(View.VISIBLE);
            }
        }


        @Override
        protected void onPostExecute(Void params) {
            frameLayout.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.INVISIBLE);
            progressBarCyrcle.setVisibility(View.INVISIBLE);
            if (nextPage == 2) {
                adapter.clearListData();
            }
            //filmClass.setAllFilms(filmInfos);
            adapter.updateListData(filmInfos);
//            adapter.notifyDataSetChanged();
            semaphore.release();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            if (!OneMoreFilmClass.isEmpty()) {
                progressBar.setProgress(values[0]);
            }
        }

        @Override
        protected void onCancelled() {
        }
    }
}
