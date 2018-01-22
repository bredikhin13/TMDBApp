package com.home.pavel.myapplication.model;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.View;
import android.widget.Toast;

import com.home.pavel.myapplication.CustomAdapter;
import com.home.pavel.myapplication.FilmInfo;
import com.home.pavel.myapplication.MainActivity;
import com.home.pavel.myapplication.Resp;
import com.home.pavel.myapplication.TmdbApiInterface;
import com.home.pavel.myapplication.presenter.FilmPresenter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.content.Context.MODE_APPEND;

/**
 * Created by Pavel on 18.01.2018.
 */

public class FilmModel {
    private Retrofit retrofit;
    private OkHttpClient client;
    private TmdbApiInterface apiInterface;
    private Integer curPage = 1;
    private ArrayList<Bitmap> posters;
    private ArrayList<FilmInfo> filmInfos;
    private ArrayList<Integer> favFilmIds;
    private String apiKey = "6ccd72a2a8fc239b13f209408fc31c33";
    private CustomAdapter adapter;
    private FilmPresenter presenter;




    public void getData(int page, String q) {
//        try {
//            semaphore.acquire();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        presenter = new FilmPresenter(this);
        client = new OkHttpClient();
        retrofit = new Retrofit.Builder()
                .baseUrl("https://api.themoviedb.org")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
        apiInterface = retrofit.create(TmdbApiInterface.class);
        Call<Resp> call;
        if (q.equals("")) {
            call = apiInterface.getAllFilms(apiKey, "ru", curPage);
        } else {
            call = apiInterface.getFiltredFilms(apiKey, "ru", curPage, q);
        }
        System.out.println("Start load");
        call.enqueue(new Callback<Resp>() {
            @Override
            public void onResponse(Call<Resp> call, retrofit2.Response<Resp> response) {
                Resp result = response.body();
                filmInfos = (ArrayList<FilmInfo>) result.getResults();
                getPostersTread();
                //lastPage = result.getTotalPages();
                //GetTask task = new GetTask();
                //task.execute();
                curPage++;
                System.out.println("end load");
//                textView.setText(resp.getTotalResults().toString());
            }

            @Override
            public void onFailure(Call<Resp> call, Throwable t) {
                call.cancel();
            }
        });
    }

    public CustomAdapter createAdapter(Activity context) {
//        this.adapter = new CustomAdapter(context, (ArrayList<FilmInfo>) filmInfos, favFilmIds);
        return adapter;
    }

    private byte[] get(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();
        Response response = client.newCall(request).execute();
        return response.body().bytes();
    }

    public ArrayList<Bitmap> getPosters(List<FilmInfo> filmInfos) {
        GetTask task = new GetTask();
        task.execute(filmInfos);
        return posters;
    }

    private void getPostersTread() {
        GetTask task = new GetTask();
        task.execute();
    }

    public void setCurPage(Integer curPage) {
        this.curPage = curPage;
    }

    public Integer getCurPage() {
        return this.curPage;
    }


    class GetTask extends AsyncTask<List<FilmInfo>, Integer, List<Bitmap>> {
        private Exception exception;

        @Override
        protected List<Bitmap> doInBackground(List<FilmInfo>... params) {
            //Integer count = result.getResults().size();
            Integer count = filmInfos.size();
            Integer progress = 0;
            //progressBar.setProgress(progress);
            try {
                ArrayList<Bitmap> bitmaps1 = new ArrayList<>();
                for (FilmInfo f : filmInfos) {
                    byte[] poster = get("http://image.tmdb.org/t/p/w185/" + f.getPosterPath());
                    if (poster != null && poster.length > 0) {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(poster, 0, poster.length);
//                        bitmaps1.add(bitmap);
                        f.setPoster(bitmap);
                        publishProgress(progress += 100 / count);
                        System.out.println("" + progress);
                    }
                }
                return bitmaps1;
            } catch (Exception e) {
                this.exception = e;
                return null;
            }
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
            // progressBar.setProgress(0);
//            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(List<Bitmap> bitmapList) {
//            progressBar.setVisibility(View.INVISIBLE);
//            if (curPage == 2) {
//                adapter.clearListData();
//            }
//            posters.addAll(bitmapList);
            //presenter.startShowFilms();
//            bitmapArrayList.addAll(bitmapList);
//            adapter.updateListData(resp.getResults());
            //semaphore.release();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
//            progressBar.setProgress(values[0]);
        }

        @Override
        protected void onCancelled() {

        }
    }


    public void openFile(String fileName, Context context, LinkedList<Integer> list) {
        try {
            InputStream inputStream = context.openFileInput(fileName);

            if (inputStream != null) {
                InputStreamReader isr = new InputStreamReader(inputStream);
                BufferedReader reader = new BufferedReader(isr);
                String line;
//                StringBuilder builder = new StringBuilder();

                while ((line = reader.readLine()) != null) {
//                    builder.append(line + "\n");
                    list.add(Integer.parseInt(line));
                }

                inputStream.close();
//                mEditText.setText(builder.toString());
            }
        } catch (Throwable t) {
            Toast.makeText(context.getApplicationContext(),
                    "Exception: " + t.toString(), Toast.LENGTH_LONG).show();
        }
    }

    public void saveFile(String fileName, Context context, LinkedList<Integer> list) {
        try {
            OutputStream outputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            OutputStreamWriter osw = new OutputStreamWriter(outputStream);
            for (Integer i : list) {
                osw.write(i + "\n");
            }
            osw.close();
        } catch (Throwable t) {
            Toast.makeText(context.getApplicationContext(),
                    "Exception: " + t.toString(), Toast.LENGTH_LONG).show();
        }

    }

    public void saveOneFilm(String fileName, Context context, Integer film) {
        try {
            OutputStream outputStream = context.openFileOutput(fileName, MODE_APPEND);
            OutputStreamWriter osw = new OutputStreamWriter(outputStream);
            osw.write(film + "\n");
            osw.close();
        } catch (Throwable t) {
            Toast.makeText(context.getApplicationContext(),
                    "Exception: " + t.toString(), Toast.LENGTH_LONG).show();
        }

    }


}
