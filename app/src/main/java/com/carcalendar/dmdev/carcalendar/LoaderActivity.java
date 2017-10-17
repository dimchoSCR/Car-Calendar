package com.carcalendar.dmdev.carcalendar;


import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;

import model.UserManager;
import model.Vehicle.Vehicle;
import model.util.ImageUtils;

public class LoaderActivity extends AppCompatActivity {

    private ProgressBar bar;
    private TextView loadingTV;
    private final UserManager manager = UserManager.getInstance();
    public static final String USERMANAGER_FILE_STORAGE = "UsermanagerDATA.txt";
    private boolean user_manager_saved_successfully = false;

    public static boolean userManagerFileAvailable(Context context){
        String path= context.getFilesDir().getAbsolutePath()+"/"+USERMANAGER_FILE_STORAGE;
        File file = new File(path);
        if (file.exists()) {
           return true;
        }
        else return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loader);
        String path= this.getFilesDir().getAbsolutePath()+"/"+USERMANAGER_FILE_STORAGE;
        bar = (ProgressBar) findViewById(R.id.progressBar);
        loadingTV = (TextView) findViewById(R.id.loadingTV);
            if (userManagerFileAvailable(this)) {
                ManagerLoader loader = new ManagerLoader();
                loader.execute();
            } else {
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
    }

    private class ManagerLoader extends AsyncTask<Void,Void,Boolean>
    {

        ManagerLoader() {}

        @Override
        protected void onPreExecute() {
            bar.setVisibility(View.VISIBLE);
            loadingTV.setVisibility(View.VISIBLE);
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            synchronized (manager) {

                if (userManagerFileAvailable(getApplicationContext())) {
                    try {
                        String path = getApplicationContext().getFilesDir().getAbsolutePath() + "/" + USERMANAGER_FILE_STORAGE;
                        File file = new File(path);
                        ObjectInputStream in = new ObjectInputStream(new FileInputStream(file));
                        manager.updateFromFile((UserManager) in.readObject());
                        if (manager.getLoggedUser() != null) {
                            ArrayList<Vehicle> list = (ArrayList<Vehicle>) manager.getRegisteredUserVehicles();
                            for (Vehicle x : list) {
                                ImageUtils.mapImageToVehicle(x, ImageUtils.getBitmapFromPath(x.getPathToImage()));
                            }
                            return true;
                        }
                        return false;
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean flag) {

            Intent intent = null;
            if (flag){
                intent = new Intent(LoaderActivity.this,GarageActivity.class);
            }else {
                intent= new Intent(LoaderActivity.this,LoginActivity.class);
            }
            startActivity(intent);
            finish();
        }
    }
}


