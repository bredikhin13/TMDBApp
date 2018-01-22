package com.home.pavel.myapplication.presenter;

import android.graphics.Bitmap;

import com.home.pavel.myapplication.CustomAdapter;
import com.home.pavel.myapplication.FilmInfo;
import com.home.pavel.myapplication.MainActivity;
import com.home.pavel.myapplication.Resp;
import com.home.pavel.myapplication.model.FilmModel;
import com.home.pavel.myapplication.model.SaveInFile;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Pavel on 21.01.2018.
 */

public class FilmPresenter {
    //    private SaveInFile fileModel;
    private FilmModel filmModel;
    private MainActivity view;

    public FilmPresenter(FilmModel model) {
        this.filmModel = model;
    }

    public void attachView(MainActivity usersActivity) {
        view = usersActivity;
    }

    public void detachView() {
        view = null;
    }


    public void viewIsReady() {
        loadFilms();
    }

//
//    public void startShowFilms() {
//        view.showFilms();
//    }

    public CustomAdapter getAdapter() {
        return filmModel.createAdapter(view);
    }

    public void loadFilms() {
        filmModel.getData(1, "");
//        ArrayList<Bitmap> bitmaps = filmModel.getPosters(films);
//        view.showFilms(films,bitmaps);
    }

}
