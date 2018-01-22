package com.home.pavel.myapplication;

import java.util.ArrayList;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;

/**
 * Created by Pavel on 22.01.2018.
 */

public class OneMoreFilmClass {
    private static OneMoreFilmClass _instance = null;

    private OneMoreFilmClass(){
        allFilms = new ArrayList<>();
        favFilms = new ArrayList<>();
        nextPage = 1;
        client = new OkHttpClient();
    }
    private ArrayList<FilmInfo> allFilms;
    private ArrayList<Integer> favFilms;
    private Integer nextPage;
    private OkHttpClient client;
    private Retrofit retrofit;

    public OkHttpClient getClient() {
        return client;
    }

    public void setClient(OkHttpClient client) {
        this.client = client;
    }

    public Retrofit getRetrofit() {
        return retrofit;
    }

    public void setRetrofit(Retrofit retrofit) {
        this.retrofit = retrofit;
    }

    public Integer getNextPage() {
        return nextPage;
    }

    public void setNextPage(Integer nextPage) {
        this.nextPage = nextPage;
    }

    public static boolean isEmpty(){
        return _instance.getCountFilms()==0;
    }

    public ArrayList<FilmInfo> getAllFilms() {
        return allFilms;
    }

    public void setAllFilms(ArrayList<FilmInfo> allFilms) {
        this.allFilms = allFilms;
    }

    public ArrayList<Integer> getFavFilms() {
        return favFilms;
    }

    public void setFavFilms(ArrayList<Integer> favFilms) {
        this.favFilms = favFilms;
    }

    public void addToAllFilms(ArrayList<FilmInfo> infos){
        allFilms.addAll(infos);
    }

    public void clearAllFilms(){
        allFilms.clear();
    }

    public Integer getCountFilms(){
        return allFilms.size();
    }

    public static OneMoreFilmClass getInstance(){
        if (_instance == null){
            _instance = new OneMoreFilmClass();
        }
        return _instance;
    }

}
