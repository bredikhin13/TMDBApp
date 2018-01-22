package com.home.pavel.myapplication.model;

import android.content.Context;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.LinkedList;

import static android.content.Context.MODE_APPEND;

/**
 * Created by Pavel on 19.01.2018.
 */

public class SaveInFile {
    public static void openFile(String fileName, Context context, ArrayList<Integer> list) {
        try {
            File file = new File(context.getFilesDir(), fileName);
            file.createNewFile();
            FileInputStream inputStream = context.openFileInput(fileName);
            if (inputStream != null) {
                InputStreamReader isr = new InputStreamReader(inputStream);
                BufferedReader reader = new BufferedReader(isr);
                String line;
                while ((line = reader.readLine()) != null) {
                    list.add(Integer.parseInt(line));
                }
                inputStream.close();
            }
        } catch (Throwable t) {
            Toast.makeText(context.getApplicationContext(),
                    "Exception: " + t.toString(), Toast.LENGTH_LONG).show();
        }
    }

    public static void saveFile(String fileName, Context context, ArrayList<Integer> list) {
        try {
            FileOutputStream outputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            OutputStreamWriter osw = new OutputStreamWriter(outputStream);
            for(Integer i: list){
                osw.write(i+"\n");
            }
            osw.close();
        } catch (Throwable t) {
            Toast.makeText(context.getApplicationContext(),
                    "Exception: " + t.toString(), Toast.LENGTH_LONG).show();
        }

    }
    public static void saveOneFilm(String fileName, Context context, Integer film) {
        try {
            FileOutputStream outputStream = context.openFileOutput(fileName, MODE_APPEND);
            OutputStreamWriter osw = new OutputStreamWriter(outputStream);
            osw.write(film+"\n");
            osw.close();
        } catch (Throwable t) {
            Toast.makeText(context.getApplicationContext(),
                    "Exception: " + t.toString(), Toast.LENGTH_LONG).show();
        }

    }
}
