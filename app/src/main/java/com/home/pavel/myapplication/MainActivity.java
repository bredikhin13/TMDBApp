package com.home.pavel.myapplication;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
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
    private boolean isSearchActive = false;
    private Semaphore semaphore;
    private ProgressBar progressBar;
    private ProgressBar progressBarCircle;
    private ListView listView;
    private Retrofit retrofit;
    private TmdbApiInterface apiInterface;
    private TextView alertTextView;
    private ArrayList<FilmInformationDTO> filmInformationDTOList;
    private int nextPage;
    private int lastPage;
    private int progress = 0;
    private FilmListAdapter adapter;
    private String query = "";
    private SearchView searchView;
    private FilmDataModel dataFilms;
    private FrameLayout frameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dataFilms = FilmDataModel.getInstance();
        nextPage = dataFilms.getNextPage();
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        searchView = findViewById(R.id.searchView);
        alertTextView = findViewById(R.id.alertTextView);
        progressBar = findViewById(R.id.progressBar);
        progressBarCircle = findViewById(R.id.progressBar2);
        frameLayout = findViewById(R.id.frameLayout);
        progressBar.setVisibility(View.INVISIBLE);
        progressBarCircle.setVisibility(View.INVISIBLE);
        client = dataFilms.getClient();
        retrofit = new Retrofit.Builder()
                .baseUrl("https://api.themoviedb.org")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
        apiInterface = retrofit.create(TmdbApiInterface.class);
        listView = findViewById(R.id.listView);
        SaveInFile.openFile(Constants.FILE_NAME, this, dataFilms.getFavFilms());
        semaphore = new Semaphore(1);
        adapter = new FilmListAdapter(this);
        listView.setAdapter(adapter);
        initListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        query = searchView.getQuery().toString();
        switch (dataFilms.getRequestStatus()) {
            case 0:
                break;
            case 1:
                showNetErrScreen();
                break;
            case 2:
                showSearchErrScreen();
                break;
            case 3:
                getData(nextPage);
                break;
            default:
                break;
        }
    }

    private void initListeners() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast toast = Toast.makeText(getApplicationContext(),
                        dataFilms.getAllFilms().get(i).getTitle(), Toast.LENGTH_SHORT);
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
                if (visibleItemCount > 0 && firstVisibleItem + visibleItemCount == totalItemCount && nextPage < lastPage && semaphore.availablePermits() != 0) {
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
                isSearchActive = true;
                getData(nextPage);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
//                if (s.equals("") && isSearchActive) {
//                    query = s;
//                    nextPage = 1;
//                    isSearchActive = false;
//                    adapter.clearListData();
//                    getData(nextPage);
//                }
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
        Call<RequestDTO> call;
        if (query.equals("")) {
            call = apiInterface.getAllFilms(Constants.API_KEY, Constants.LANG_RU, page);
        } else {
            call = apiInterface.getFiltredFilms(Constants.API_KEY, Constants.LANG_RU, page, query);
        }
        progress = 5;
        progressBar.setProgress(progress);
        call.enqueue(new Callback<RequestDTO>() {
            @Override
            public void onResponse(Call<RequestDTO> call, retrofit2.Response<RequestDTO> response) {
                RequestDTO resp = response.body();
                if (resp.getTotalResults() != 0) {
                    filmInformationDTOList = (ArrayList<FilmInformationDTO>) resp.getResults();
                    lastPage = resp.getTotalPages();
                    dataFilms.setLastPage(lastPage);
                    dataFilms.setRequestStatus(Constants.STATUS_OK);
                    new GetTask().execute();
                    dataFilms.setNextPage(++nextPage);
                    alertTextView.setVisibility(View.GONE);
                } else {
                    semaphore.release();
                    nextPage = 1;
                    adapter.clearListData();
                    showSearchErrScreen();

                }
            }

            @Override
            public void onFailure(Call<RequestDTO> call, Throwable t) {
                semaphore.release();
                showNetErrScreen();
                call.cancel();
            }
        });
    }


    public class GetTask extends AsyncTask<String, Integer, Void> {

        @Override
        protected Void doInBackground(String... params) {
            Integer count = filmInformationDTOList.size();
            try {
                for (FilmInformationDTO f : filmInformationDTOList) {
                    byte[] poster = get("http://image.tmdb.org/t/p/w185/" + f.getPosterPath());
                    if (poster != null && poster.length > 0) {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(poster, 0, poster.length);
                        f.setPoster(bitmap);
                        publishProgress(progress += 95 / count);
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
            if (!FilmDataModel.isEmpty()) {
                progressBar.setProgress(0);
                frameLayout.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.VISIBLE);
            } else {
                progressBarCircle.setVisibility(View.VISIBLE);
            }
        }


        @Override
        protected void onPostExecute(Void params) {
            frameLayout.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.INVISIBLE);
            progressBarCircle.setVisibility(View.INVISIBLE);
            if (nextPage == 2) {
                adapter.clearListData();
            }
            adapter.updateListData(filmInformationDTOList);
            semaphore.release();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            if (!FilmDataModel.isEmpty()) {
                progressBar.setProgress(values[0]);
            }
        }

        @Override
        protected void onCancelled() {
        }
    }

    private void showNetErrScreen() {
        if (dataFilms.getRequestStatus().equals(Constants.STATUS_OK)) {
            Snackbar.make(listView, R.string.snack_message, Snackbar.LENGTH_LONG).show();
        } else {
            alertTextView.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_alert_triangle, 0, 0);
            alertTextView.setText(R.string.search_trouble);
            alertTextView.setVisibility(View.VISIBLE);
            dataFilms.setRequestStatus(Constants.STATUS_NETWORK_ERR);
            adapter.clearListData();
        }
    }

    private void showSearchErrScreen() {
        alertTextView.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_big_search, 0, 0);
        alertTextView.setText("\n\nПо запросу \"" + query + "\" ничего не найдено");
        alertTextView.setVisibility(View.VISIBLE);
        dataFilms.setRequestStatus(Constants.STATUS_SEARCH_ERR);
    }
}
