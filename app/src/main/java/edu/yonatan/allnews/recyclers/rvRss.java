package edu.yonatan.allnews.recyclers;


import android.content.res.Configuration;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import edu.yonatan.allnews.R;
import edu.yonatan.allnews.news_package.activitys.data_source.DataSourceHaaretz;
import edu.yonatan.allnews.data_source.DataSourceMako;
import edu.yonatan.allnews.data_source.DataSourceYnetWalla;
import edu.yonatan.allnews.sports_package.data_source.DataSourceOne;


/**
 * A simple {@link Fragment} subclass.
 */
public class rvRss extends Fragment {


    //props:
    private RecyclerView rv;

    //extracting the url from the class inserted into the bundle in the recycler adapter:
    public static rvRss newInstance(String url) {

        Bundle args = new Bundle();
        args.putString("url", url);

        rvRss fragment = new rvRss();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_rv_rss, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        String url = getArguments().getString("url");

        //progress bar :
        ProgressBar bar = view.findViewById(R.id.progressBar);

        //init views:
        initViews(view);


        //loads the datasource(diffrent json in each site)
        // for each different link -> load different datasource -> load different json;
        if (url.equals("http://rcs.mako.co.il/rss/31750a2610f26110VgnVCM1000005201000aRCRD.xml") ||
                url.equals("http://rcs.mako.co.il/rss/87b50a2610f26110VgnVCM1000005201000aRCRD.xml") ||
                        url.equals("http://rcs.mako.co.il/rss/cd0c4e8fc83b8310VgnVCM2000002a0c10acRCRD.xml")) {

            new DataSourceMako(bar, rv, url).execute();


        } else if (url.equals("https://www.haaretz.co.il/cmlink/1.1617539")) {
            new DataSourceHaaretz(bar, rv, url).execute();

        } else if (url.equals("https://www.one.co.il/cat/coop/xml/rss/newsfeed.aspx")) {

            new DataSourceOne(bar, rv, url).execute();
        } else {
            new DataSourceYnetWalla(bar, rv, url).execute();

        }

    }

    //init views:
    private void initViews(@NonNull View view) {
        rv = view.findViewById(R.id.rvRss);
    }


    //changing the layout of the recycler listener:
    // when landscape strech bottom to match the disappearing bottom nav bar
    // when portrait back to default values when bottom nav is visible;
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {

            //get all the margins params from the view selected, after wards change them
            // and calls the layout to refresh itself:
            setMargins(rv, 0, 40, 0, 0);

        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {

            //get all the margins params from the view selected, after wards change them
            // and calls the layout to refresh itself:
            setMargins(rv, 0, 40, 0, 32);
        }
    }

    //get all the margins params from the view selected, after wards change them
    // and calls the layout to refresh itself:
    public static void setMargins(View v, int l, int t, int r, int b) {
        if (v.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            p.setMargins(l, t, r, b);
            v.requestLayout();
        }
    }

}
