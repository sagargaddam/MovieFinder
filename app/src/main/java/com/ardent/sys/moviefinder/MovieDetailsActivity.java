package com.ardent.sys.moviefinder;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

public class MovieDetailsActivity extends AppCompatActivity {

    RequestQueue requestQueue;
    ProgressBar progressBar;
    Context mContext;
    String MOVIE_NAME="";
    TextView titleText,plotText,releaseDateText,ratingText,genreText,languageText,actorsText,noInfoText;
    NetworkImageView networkImageView;
    private ImageLoader imageLoader;
    CardView detailsCardView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_deatils);

        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra(Config.TAG_BUNDLE);
        MOVIE_NAME = bundle.getString(Config.TAG_STRING_KEY);

        intiFields();

        if(!isNetworkAvailable()){
            noInfoText.setVisibility(View.VISIBLE);
            detailsCardView.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.INVISIBLE);
        }
        else {
            noInfoText.setVisibility(View.INVISIBLE);
            detailsCardView.setVisibility(View.VISIBLE);
            getDataFromServer();
        }
    }

    private void intiFields() {
        progressBar=(ProgressBar)findViewById(R.id.progress_bar);
        mContext=getApplicationContext();
        requestQueue = Volley.newRequestQueue(mContext);
        titleText=(TextView)findViewById(R.id.title_text);
        plotText=(TextView)findViewById(R.id.plot_text);
        releaseDateText=(TextView)findViewById(R.id.rel_date_text);
        ratingText=(TextView)findViewById(R.id.rating_text);
        genreText=(TextView)findViewById(R.id.genre_text);
        networkImageView=(NetworkImageView)findViewById(R.id.imageView);
        languageText=(TextView)findViewById(R.id.lang_text);
        actorsText=(TextView)findViewById(R.id.actor_text);
        noInfoText = (TextView)findViewById(R.id.errorView);
        noInfoText.setVisibility(View.INVISIBLE);
        detailsCardView=(CardView)findViewById(R.id.details_cardView);
    }


    private void getDataFromServer(){
        progressBar.setVisibility(View.VISIBLE);
        progressBar.setIndeterminate(true);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,Config.MOVIE_DETAILS_LINK+MOVIE_NAME,null,new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                progressBar.setVisibility(View.GONE);

                Log.d("tag",response.toString());
                Log.d("rag",Config.MOVIE_SEARCH_LINK+MOVIE_NAME);
                try {
                    String title = response.getString(Config.TAG_TITLE);
                    String plot = response.getString(Config.TAG_PLOT);
                    String releaseDate = response.getString(Config.TAG_RELEASE_DATE);
                    String genre = response.getString(Config.TAG_GENRE);
                    String posterUrl = response.getString(Config.TAG_POSTER_URL);
                    String rating = response.getString(Config.TAG_RATING);
                    String actors= response.getString(Config.TAG_ACTORS);
                    String language= response.getString(Config.TAG_LANGUAGE);

                    if(posterUrl.equals("N/A")){
                        imageLoader = CustomVolleyRequest.getInstance(mContext).getImageLoader();
                        imageLoader.get("http://139.59.16.250/ardent/image.png", ImageLoader.getImageListener(networkImageView, R.drawable.image, R.drawable.image));
                        networkImageView.setImageUrl("http://139.59.16.250/ardent/image.png", imageLoader);
                    }else {
                        imageLoader = CustomVolleyRequest.getInstance(mContext).getImageLoader();
                        imageLoader.get(posterUrl, ImageLoader.getImageListener(networkImageView, R.drawable.image, R.drawable.image));
                        networkImageView.setImageUrl(posterUrl, imageLoader);
                    }
                    titleText.setText(title);
                    plotText.setText(plot);
                    releaseDateText.setText(releaseDate);
                    genreText.setText(genre);
                    ratingText.setText(rating);
                    languageText.setText(language);
                    actorsText.setText(actors);

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(mContext,
                            "Error: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(mContext,
                        error.getMessage()+Config.MOVIE_SEARCH_LINK+MOVIE_NAME, Toast.LENGTH_SHORT).show();
            }
        });

        requestQueue.add(jsonObjectRequest);
    }

    private boolean isNetworkAvailable(){
        ConnectivityManager connector=(ConnectivityManager)mContext.getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo active=connector.getActiveNetworkInfo();
        return active!=null&&active.isConnected();
    }
}
