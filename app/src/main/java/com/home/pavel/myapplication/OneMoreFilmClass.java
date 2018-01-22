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
        lastPage = 1001;
        requestStatus = Constants.STATUS_EMPTY_LIST;
        client = new OkHttpClient();
    }
    private ArrayList<FilmInfo> allFilms;
    private ArrayList<Integer> favFilms;
    private Integer nextPage;
    private OkHttpClient client;
    private Integer lastPage;

    public Integer getRequestStatus() {
        return requestStatus;
    }

    public void setRequestStatus(Integer requestStatus) {
        this.requestStatus = requestStatus;
    }

    private Integer requestStatus;



    public Integer getLastPage() {
        return lastPage;
    }

    public void setLastPage(Integer lastPage) {
        this.lastPage = lastPage;
    }

    public OkHttpClient getClient() {
        return client;
    }

    public void setClient(OkHttpClient client) {
        this.client = client;
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
        nextPage = 1;
        lastPage = 1001;
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
