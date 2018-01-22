package com.home.pavel.myapplication;

import android.app.Activity;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.home.pavel.myapplication.model.SaveInFile;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Pavel on 18.01.2018.
 */

public class CustomAdapter extends ArrayAdapter<FilmInfo> {

    private final Activity context;
    private final ArrayList<FilmInfo> films;
    private final ArrayList<Integer> favFilmsId;
//    private final OneMoreFilmClass data;

    public CustomAdapter(Activity context) {
        super(context, R.layout.list_item, OneMoreFilmClass.getInstance().getAllFilms());
        this.context = context;
        this.films = OneMoreFilmClass.getInstance().getAllFilms();
        this.favFilmsId = OneMoreFilmClass.getInstance().getFavFilms();
    }

    public View getView(final int position, View view, ViewGroup parent) {

        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.list_item, null, true);
        ImageView poster = rowView.findViewById(R.id.posterView);
        TextView title = rowView.findViewById(R.id.titleView);
        TextView overview = rowView.findViewById(R.id.overviewView);
        TextView date = rowView.findViewById(R.id.dateView);
        final ImageButton favButton = rowView.findViewById(R.id.favoriteButton);
        favButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FilmInfo filmInfo = films.get(position);
                Integer favFilmId = filmInfo.getId();
                if (favFilmsId.contains(favFilmId)) {
                    favFilmsId.remove(favFilmId);
                    SaveInFile.saveFile(Constants.FILE_NAME, context, favFilmsId);
                    favButton.setImageResource(R.drawable.ic_heart);
                } else {
                    favFilmsId.add(favFilmId);
                    SaveInFile.saveOneFilm(Constants.FILE_NAME ,context,favFilmId);
                    favButton.setImageResource(R.drawable.ic_heart_fill);
                }
            }
        });

        FilmInfo filmInfo = films.get(position);
        poster.setImageBitmap(filmInfo.getPoster());
        if (favFilmsId.contains(filmInfo.getId())) {
            favButton.setImageResource(R.drawable.ic_heart_fill);
        }
        title.setText(filmInfo.getTitle());
        overview.setText(filmInfo.getOverview());
        SimpleDateFormat oldDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat newDateFormat = new SimpleDateFormat("d MMMM yyyy", Locale.getDefault());
        try {
            date.setText(newDateFormat.format(oldDateFormat.parse(filmInfo.getReleaseDate())));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return rowView;
    }


    public void clearListData() {
        films.clear();
        OneMoreFilmClass.getInstance().clearAllFilms();
        this.notifyDataSetChanged();
    }

    public void updateListData(List<FilmInfo> f) {
        films.addAll(f);
        //OneMoreFilmClass.getInstance().addToAllFilms((ArrayList<FilmInfo>) f);
        this.notifyDataSetChanged();
    }

    public void updateListData(List<FilmInfo> f, List<Bitmap> b) {
        films.addAll(f);
        this.notifyDataSetChanged();
    }
}
