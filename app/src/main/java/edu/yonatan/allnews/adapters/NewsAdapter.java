package edu.yonatan.allnews.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import edu.yonatan.allnews.R;
import edu.yonatan.allnews.news_package.activitys.NewsActivity;
import edu.yonatan.allnews.fragments.WebView;
import edu.yonatan.allnews.models.News;
import edu.yonatan.allnews.sports_package.activities.SportsActivity;
import edu.yonatan.allnews.tech_package.TechActivity;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> {

    private List<News> newsList;
    private Context context;

    public NewsAdapter(List<News> newsList) {
        this.newsList = newsList;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View v = inflater.inflate(R.layout.item_row, parent, false);
        if(v.getContext() instanceof NewsActivity ) {

            switch (viewType) {
                case 0:
                    v = inflater.inflate(R.layout.item_row_top, parent, false);
                    break;

                case 1:
                    v = inflater.inflate(R.layout.item_row, parent, false);
                    break;
            }
        }
        if(v.getContext() instanceof SportsActivity ) {

            switch (viewType) {
                case 0:
                    v = inflater.inflate(R.layout.item_row_top_sports, parent, false);
                    break;

                case 1:
                    v = inflater.inflate(R.layout.item_row_sports, parent, false);
                    break;
            }
        }
        if(v.getContext() instanceof TechActivity ) {

            switch (viewType) {
                case 0:
                    v = inflater.inflate(R.layout.item_row_top_tech, parent, false);
                    break;

                case 1:
                    v = inflater.inflate(R.layout.item_row_tech, parent, false);
                    break;
            }
        }



        ViewHolder holder = new ViewHolder(v);

        return holder;
    }

    @Override
    public int getItemViewType(int position) {

        int type = 1;
        if (position == 0) type = 0;
        return type;
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        News aNews = newsList.get(position);


    switch (position) {
        case 0:

            try {
                holder.tvTitleTop.setText(aNews.getTitle());
                holder.tvDescriptionTop.setText(aNews.getExecrpt());
                Picasso.get().load(aNews.getImg()).placeholder(R.drawable.broken_pic_placeholder).resize(500, 500).into(holder.ivImageTop);
            } catch (Exception error) {

            }
            holder.model = aNews;
            break;

        default:
            try {
                holder.tvTitle.setText(aNews.getTitle());
                holder.tvDescription.setText(aNews.getExecrpt());
                Picasso.get().load(aNews.getImg()).resize(150, 150).placeholder(R.drawable.broken_pic_placeholder).into(holder.ivImage);
            } catch (Exception error) {

            }

            holder.model = aNews;

    }

}




    @Override
    public int getItemCount() {
        return 20;
    }



    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle;
        TextView tvDescription;
        ImageView ivImage;
        TextView tvTitleTop;
        TextView tvDescriptionTop;
        ImageView ivImageTop;


        News model;


        ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            ivImage = itemView.findViewById(R.id.ivImage);
            tvDescriptionTop = itemView.findViewById(R.id.tvDescriptionTop);
            tvTitleTop = itemView.findViewById(R.id.tvTitleTop);
            ivImageTop = itemView.findViewById(R.id.ivImgTop);


            itemView.setOnClickListener(v -> {
                if (itemView.getContext() instanceof NewsActivity) {
                    AppCompatActivity activity = (AppCompatActivity) v.getContext();
                    activity.getSupportFragmentManager().
                            beginTransaction().
                            addToBackStack("details").
                            replace(R.id.newsContent, new WebView().newInstance(model)).
                            commit();

                }
                if (itemView.getContext() instanceof SportsActivity) {
                    AppCompatActivity activity = (AppCompatActivity) v.getContext();
                    activity.getSupportFragmentManager().
                            beginTransaction().
                            addToBackStack("details").
                            replace(R.id.sportsContent, new WebView().newInstance(model)).
                            commit();

                }

                if (itemView.getContext() instanceof TechActivity) {
                    AppCompatActivity activity = (AppCompatActivity) v.getContext();
                    activity.getSupportFragmentManager().
                            beginTransaction().
                            addToBackStack("details").
                            replace(R.id.techContent, new WebView().newInstance(model)).
                            commit();

                }

            });


        }

    }


}

