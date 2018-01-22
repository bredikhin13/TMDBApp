package com.home.pavel.myapplication;

import android.app.Activity;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ArrayAdapter;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class FilmListAdapter extends ArrayAdapter<FilmInformationDTO> {

    private final Activity context;
    private final ArrayList<FilmInformationDTO> films;
    private final ArrayList<Integer> favFilmsId;
//    private final FilmDataModel data;

    public FilmListAdapter(Activity context) {
        super(context, R.layout.list_item, FilmDataModel.getInstance().getAllFilms());
        this.context = context;
        this.films = FilmDataModel.getInstance().getAllFilms();
        this.favFilmsId = FilmDataModel.getInstance().getFavFilms();
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
                FilmInformationDTO filmInformationDTO = films.get(position);
                Integer favFilmId = filmInformationDTO.getId();
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

        FilmInformationDTO filmInformationDTO = films.get(position);
        poster.setImageBitmap(filmInformationDTO.getPoster());
        if (favFilmsId.contains(filmInformationDTO.getId())) {
            favButton.setImageResource(R.drawable.ic_heart_fill);
        }
        title.setText(filmInformationDTO.getTitle());
        overview.setText(filmInformationDTO.getOverview());
        SimpleDateFormat oldDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat newDateFormat = new SimpleDateFormat("d MMMM yyyy", Locale.getDefault());
        try {
            date.setText(newDateFormat.format(oldDateFormat.parse(filmInformationDTO.getReleaseDate())));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return rowView;
    }


    public void clearListData() {
        films.clear();
        FilmDataModel.getInstance().clearAllFilms();
        this.notifyDataSetChanged();
    }

    public void updateListData(List<FilmInformationDTO> f) {
        films.addAll(f);
        //FilmDataModel.getInstance().addToAllFilms((ArrayList<FilmInformationDTO>) f);
        this.notifyDataSetChanged();
    }

    public void updateListData(List<FilmInformationDTO> f, List<Bitmap> b) {
        films.addAll(f);
        this.notifyDataSetChanged();
    }
}
