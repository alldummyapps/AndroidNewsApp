package edu.yonatan.allnews.data_source;

import android.os.AsyncTask;

import android.view.View;
import android.widget.ProgressBar;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import edu.yonatan.allnews.models.News;
import edu.yonatan.allnews.adapters.NewsAdapter;


public class DataSourceYnetWalla extends AsyncTask<Void, Void, List<News>> {


    private WeakReference<ProgressBar> progressBar;
    private WeakReference<RecyclerView> recyclerView;
    private String address;


    public DataSourceYnetWalla(ProgressBar progressBar, RecyclerView recyclerView, String address) {
        this.progressBar = new WeakReference<>(progressBar);
        this.recyclerView = new WeakReference<>(recyclerView);
        this.address = address;
    }


    @Override
    protected List<News> doInBackground(Void... voids) {
        List<News> myNews = new ArrayList<>();

        try {
            Document document = Jsoup.connect(address).get();
            Elements itemList = document.getElementsByTag("item");

            for (Element element : itemList) {


                    String title = element.getElementsByTag("title").first().text().trim();
                    String descriptionHTML = element.getElementsByTag("description").first().text();
                    Document description = Jsoup.parse(descriptionHTML);
                    String excerpt = description.text().trim();
                    String img = description.getElementsByTag("img").attr("src");
                    String link = description.getElementsByTag("a").attr("href");


                    News aNews = new News(title, excerpt, img, link);

                    myNews.add(aNews);

            }


        } catch (IOException e) {
            e.printStackTrace();
        }


        return myNews;
    }


    @Override
    protected void onPostExecute(List<News> news) {
        super.onPostExecute(news);
        ProgressBar bar = this.progressBar.get();
        if (bar == null) return;
        bar.setVisibility(View.GONE);

        RecyclerView recycler = this.recyclerView.get();


        recycler.setAdapter(new NewsAdapter(news));
        recycler.setLayoutManager(new LinearLayoutManager(recycler.getContext()));


    }
}
