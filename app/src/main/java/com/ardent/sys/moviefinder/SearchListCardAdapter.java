package com.ardent.sys.moviefinder;

/**
 * Created by sagar on 01-10-2016.
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.util.List;

/**
 * Created by Belal on 11/9/2015.
 */
public class SearchListCardAdapter extends RecyclerView.Adapter<SearchListCardAdapter.ViewHolder> {

    //Imageloader to load image
    private ImageLoader imageLoader;
    private Context context;

    //List to store all superheroes
    List<SearchListItem> moviesList;

    //Constructor of this class
    public SearchListCardAdapter(List<SearchListItem> superHeroes, Context context){
        super();
        //Getting all superheroes
        this.moviesList = superHeroes;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.search_list_card_design, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        //Getting the particular item from the list
        SearchListItem searchListItem =  moviesList.get(position);

        if(searchListItem.getPosterUrl().equals("N/A")){
            imageLoader = CustomVolleyRequest.getInstance(context).getImageLoader();
            imageLoader.get("http://139.59.16.250/ardent/image.png", ImageLoader.getImageListener(holder.imageView, R.drawable.image, R.drawable.image));
            holder.imageView.setImageUrl("http://139.59.16.250/ardent/image.png", imageLoader);
        }else{
            imageLoader = CustomVolleyRequest.getInstance(context).getImageLoader();
            imageLoader.get(searchListItem.getPosterUrl(), ImageLoader.getImageListener(holder.imageView, R.drawable.image, R.drawable.image));
            holder.imageView.setImageUrl(searchListItem.getPosterUrl(), imageLoader);
        }


        holder.textViewName.setText(searchListItem.getTitle());
        holder.textViewYear.setText(searchListItem.getReleaseYear());
        holder.textViewType.setText(searchListItem.getType());

    }

    @Override
    public int getItemCount() {
        return moviesList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        //Views
        public NetworkImageView imageView;
        public TextView textViewName;
        public TextView textViewYear;
        public TextView textViewType;


        //Initializing Views
        public ViewHolder(View itemView) {
            super(itemView);
            imageView = (NetworkImageView) itemView.findViewById(R.id.imageView);
            textViewName = (TextView) itemView.findViewById(R.id.title_text);
            textViewYear = (TextView) itemView.findViewById(R.id.year_text);
            textViewType = (TextView) itemView.findViewById(R.id.type_text);

        }
    }


}