package com.ardent.sys.moviefinder;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private List<SearchListItem> moviesList;

    private RadioGroup radioGroup;
    Button searchButton;
    EditText searchInput;
    ProgressBar progressBar;
    RecyclerView recyclerView;
    public String SEARCH_TERM;
    RecyclerView.Adapter recyclerViewAdapter;
    Context mContext;
    RequestQueue requestQueue;
    String TYPE="";
    TextView noInfoText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        intiFields();

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if(i == R.id.radio_both_btn){
                    TYPE="";
                }else if(i ==  R.id.radio_movies_btn){
                    TYPE="&type=movie";
                }else if(i ==  R.id.radio_series_btn){
                    TYPE="&type=series";
                }
            }
        });

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(searchInput.getText().length() == 0){
                    searchInput.setError("Invalid Entry");
                }else{
                    InputMethodManager imm = (InputMethodManager)getSystemService(
                            Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(searchInput.getWindowToken(), 0);

                    searchInput.setError(null);

                    SEARCH_TERM = searchInput.getText().toString();

                    if(moviesList.size()>0){clearData();}

                    if(!isNetworkAvailable()){
                        noInfoText.setVisibility(View.VISIBLE);
                    }
                    else {
                        noInfoText.setVisibility(View.INVISIBLE);
                        getDataFromServer();
                    }

                }
            }
        });

        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(mContext, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, final int position) {
                       SearchListItem searchListItem =  moviesList.get(position);
                       Intent intent = new Intent(mContext,MovieDetailsActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString(Config.TAG_STRING_KEY,searchListItem.getImdbId());
                        intent.putExtra(Config.TAG_BUNDLE,bundle);
                        startActivity(intent);
                    }
                })
        );
    }


    public void intiFields(){
        mContext=getApplicationContext();
        searchButton=(Button)findViewById(R.id.search_button);
        searchInput=(EditText)findViewById(R.id.search_input);
        recyclerView=(RecyclerView)findViewById(R.id.recycler_view);
        progressBar=(ProgressBar)findViewById(R.id.progressBar);
        radioGroup=(RadioGroup)findViewById(R.id.radio_group);
        moviesList = new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        requestQueue = Volley.newRequestQueue(mContext);
        recyclerViewAdapter= new SearchListCardAdapter(moviesList,mContext);
        recyclerView.setAdapter(recyclerViewAdapter);
        progressBar.setVisibility(View.INVISIBLE);
        noInfoText = (TextView)findViewById(R.id.errorView);
        noInfoText.setVisibility(View.INVISIBLE);
    }

   private void getDataFromServer(){
       progressBar.setVisibility(View.VISIBLE);
       progressBar.setIndeterminate(true);
       String url = Config.MOVIE_SEARCH_LINK;
       String query="";
       try {
           query = URLEncoder.encode(SEARCH_TERM, "utf-8");
           url = Config.MOVIE_SEARCH_LINK+query+TYPE;
       } catch (UnsupportedEncodingException e) {
           e.printStackTrace();
       }
       Log.d("rag",Config.MOVIE_SEARCH_LINK+query+TYPE);
       JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,url,null,new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                progressBar.setVisibility(View.GONE);
                ListItem listItem = new ListItem();
                Log.d("tag",response.toString());

                try {

                    JSONArray jsonArray= response.getJSONArray("Search");
                    parseData(jsonArray);

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
                        error.getMessage()+Config.MOVIE_SEARCH_LINK+SEARCH_TERM, Toast.LENGTH_SHORT).show();
            }
        });

        requestQueue.add(jsonObjectRequest);
   }


    private void parseData(JSONArray array) {
        for (int i = 0; i < array.length(); i++) {

            SearchListItem searchListItem = new SearchListItem();
            JSONObject json = null;
            try {
                //Getting json
                json = array.getJSONObject(i);
                String title= json.getString(Config.TAG_TITLE);
                String type = json.getString(Config.TAG_TYPE);
                String year=json.getString(Config.TAG_YEAR);
                String posterUrl=json.getString(Config.TAG_POSTER_URL);
                String imdbId =  json.getString(Config.TAG_IMDB_ID);
                searchListItem.setTitle(title);
                searchListItem.setReleaseYear(year);
                searchListItem.setType(type);
                searchListItem.setPosterUrl(posterUrl);
                searchListItem.setImdbId(imdbId);

            } catch (JSONException e) {
                e.printStackTrace();
            }
          moviesList.add(searchListItem);
        }

        //Notifying the adapter that data has been added or changed
        recyclerViewAdapter.notifyDataSetChanged();
    }
    private boolean isNetworkAvailable(){
        ConnectivityManager connector=(ConnectivityManager)mContext.getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo active=connector.getActiveNetworkInfo();
        return active!=null&&active.isConnected();
    }

    public void clearData() {
        int size = this.moviesList.size();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                this.moviesList.remove(0);
            }
            recyclerViewAdapter.notifyItemRangeRemoved(0, size);
        }
    }

}
