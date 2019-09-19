package edu.yonatan.allnews.fragments;


import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import de.hdodenhof.circleimageview.CircleImageView;
import edu.yonatan.allnews.R;
import edu.yonatan.allnews.news_package.activitys.NewsActivity;
import edu.yonatan.allnews.models.News;


import static edu.yonatan.allnews.news_package.activitys.NewsActivity.newsProfileImgCiv;
import static edu.yonatan.allnews.news_package.activitys.NewsActivity.toCategoriesBtn;
import static edu.yonatan.allnews.sports_package.activities.SportsActivity.sportsProfileImgCiv;
import static edu.yonatan.allnews.tech_package.TechActivity.techProfileImgCiv;


/**
 * A simple {@link Fragment} subclass.
 */
public class WebView extends Fragment {


    //properties:
    private android.webkit.WebView webView;
    private CircleImageView whatsupBtn, shareBtn, allShareBtn;
    private boolean shareIsVisible = false;

    public static boolean inWebview = true;


    private FirebaseAuth mAuth;
    private DatabaseReference mRef;

    public static WebView newInstance(News model) {

        Bundle args = new Bundle();
        args.putParcelable("model", model);

        WebView fragment = new WebView();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        return inflater.inflate(R.layout.fragment_news_details, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        //init the views:
        initViews();


        //firebase init
        mAuth = FirebaseAuth.getInstance();
        mRef = FirebaseDatabase.getInstance().getReference();


        News model = getArguments().getParcelable("model");

        //share btn
        //boolean to set invisible-> visible to the share buttons:
        //responsible for showing the sharing options to the user in the webview:
        startShareButtonsBtn();


        //whatsup share button:
        //shares the link of the webview
        whatsupShareBtn(model);

//enables javascript code in the webview:
        //TODO -> defend xss attacks

        webView.getSettings().setJavaScriptEnabled(true);

        //loads the url from the json through the news model into the webview:
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(android.webkit.WebView view, String url) {
                webView.loadUrl(url);

                return true;
            }
        });

        //btn that opens all the share options the phone have;
        //shows only apps that are installed
        //shares the link of the webview
        generalShareBtn(model);


        //loads the url link into the webView:

        webView.loadUrl(model.getLink());


    }


    @Override
    public void onStart() {
        super.onStart();

        //sets the back to categories button in the toolbar to be invisible:
        toCategoriesBtn.setVisibility(View.GONE);


        //in webView state propertie:
        inWebview = true;


    }

    //btn that opens all the share options the phone have;
    //shows only apps that are installed
    //shares the link of the webview
    private void generalShareBtn(News model) {
        allShareBtn.setOnClickListener(v -> {

            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, model.getLink());
            startActivity(Intent.createChooser(shareIntent, "Share..."));


        });
    }

    //share btn
    //boolean to set invisible-> visible to the share buttons:
    //responsible for showing the sharing options to the user in the webview:
    private void startShareButtonsBtn() {
        shareBtn.setOnClickListener(v -> {
            if (!shareIsVisible) {
                whatsupBtn.setVisibility(View.VISIBLE);
                allShareBtn.setVisibility(View.VISIBLE);
                shareIsVisible = !shareIsVisible;
            } else {
                whatsupBtn.setVisibility(View.INVISIBLE);
                allShareBtn.setVisibility(View.INVISIBLE);
                shareIsVisible = !shareIsVisible;
            }


        });
    }

    //whatsup share button:
    //shares the link of the webview
    private void whatsupShareBtn(News model) {
        whatsupBtn.setOnClickListener(v -> {


            Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
            whatsappIntent.setType("text/plain");
            whatsappIntent.setPackage("com.whatsapp");
            whatsappIntent.putExtra(Intent.EXTRA_TEXT, model.getLink());
            try {
                startActivity(whatsappIntent);
            } catch (android.content.ActivityNotFoundException ex) {

                Toast.makeText(getContext(), "Whatsup Not Installed", Toast.LENGTH_SHORT).show();
            }

        });
    }


    //init the views:
    private void initViews() {
        whatsupBtn = getActivity().findViewById(R.id.whatsupBtn);
        webView = getActivity().findViewById(R.id.webView);
        shareBtn = getActivity().findViewById(R.id.shareBtn);
        allShareBtn = getActivity().findViewById(R.id.allShareBtn);

    }


    //changing the layout of the webview listener:
    // when landscape strech bottom to match the disappearing bottom nav bar
    // when portrait back to default values when bottom nav is visible;
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            //get all the margins params from the view selected, after wards change them
            // and calls the layout to refresh itself:
            setMargins(webView, 0, 40, 0, 0);

        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            //get all the margins params from the view selected, after wards change them
            // and calls the layout to refresh itself:
            setMargins(webView, 0, 40, 0, 32);
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


    //sends the user to newsActivity:
    private void sendUserToNewsActivity() {
        String userID = mAuth.getCurrentUser().getUid();

        mRef.child("Users").child(userID).child("category_state").setValue("news");

        inWebview = false;
        Intent newsActivity = new Intent(getActivity(), NewsActivity.class);
        startActivity(newsActivity);


    }

}

