package com.example.glondhe.nytimessearch.activities;


import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.glondhe.nytimessearch.R;
import com.example.glondhe.nytimessearch.adapter.RecyclerAdapter;
import com.example.glondhe.nytimessearch.filter.SearchFilter;
import com.example.glondhe.nytimessearch.listener.EndlessRecyclerViewScrollListener;
import com.example.glondhe.nytimessearch.listener.RecyclerItemClickListener;
import com.example.glondhe.nytimessearch.model.Article;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class SearchActivity extends AppCompatActivity {

    EditText etQuery;
    RecyclerView gvResults;
    Button btnSearch;
    RecyclerAdapter adapter;
    ArrayList<Article> articles;
    String textname;
    RequestParams params;
    private String sortOrder = "";
    private String deskValue = "";
    private String date = "";
    private Boolean cb1 = Boolean.FALSE;
    private Boolean cb2 = Boolean.FALSE;
    private Boolean cb3 = Boolean.FALSE;
    private int sortOrderPosition = -1;
    private int pageoffset=0;
    private String currentQuery = "";
    private String newQuery = "";
    private Toolbar toolbar;
    private ShareActionProvider mShareActionProvider;
    Intent shareIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        final Calendar calendar =  Calendar.getInstance();
//        int year = calendar.get(Calendar.YEAR);
//        int month = calendar.get(Calendar.MONTH);
//        int date = calendar.get(Calendar.DATE);
//
//        this.date = month + "/" + date + "/" + year;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        toolbar = (Toolbar) findViewById(R.id.tool_bar); // Attaching the layout to the toolbar object
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setIcon(android.R.color.transparent);

        setupViews();
    }
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    private void setupViews() {
        etQuery = (EditText) findViewById(R.id.etQuery);
        gvResults = (RecyclerView) findViewById(R.id.recycler_view);
        btnSearch = (Button) findViewById(R.id.btnSearch);
        articles = new ArrayList<>();
     //   adapter = new ArticleArrayAdapter(this, articles);
        adapter = new RecyclerAdapter(this,articles);
        gvResults.setAdapter(adapter);
        gvResults.addOnItemTouchListener(new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Intent i = new Intent(getApplicationContext(), ArticleActivity.class);
                        Article article = articles.get(position);
                        //i.putExtra("Book", Parcels.wrap(article));
                        i.putExtra("article", article);
                        startActivity(i);
                    }
                })
        );

        // First param is number of columns and second param is orientation i.e Vertical or Horizontal
        StaggeredGridLayoutManager gridLayoutManager =
                new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        // Attach the layout manager to the recycler view
        gvResults.setLayoutManager(gridLayoutManager);

        gvResults.setOnScrollListener(new EndlessRecyclerViewScrollListener(gridLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                Log.d("DEBUG", "Loading more API calls");
                callNYSearchAPI();
            }
        });
    }

    public void onSelectSettings(MenuItem item) {
        Intent i = new Intent(SearchActivity.this, SearchFilter.class);
        i.putExtra("date", this.date);
        i.putExtra("sortOrderPosition", this.sortOrderPosition);
        i.putExtra("cb1", this.cb1);
        i.putExtra("cb2", this.cb2);
        i.putExtra("cb3", this.cb3);
        startActivityForResult(i, 20);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        getMenuInflater().inflate(R.menu.menu_search, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
//        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(searchItem);
//        mShareActionProvider.setShareIntent(shareIntent);

        searchView.setIconifiedByDefault(false);
        searchView.setQueryHint("Search");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                // perform query here
                newQuery = query;
                callNYSearchAPI();
                // workaround to avoid issues with some emulators and keyboard devices firing twice if a keyboard enter is used
                // see https://code.google.com/p/android/issues/detail?id=24599
                searchView.clearFocus();

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });



        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        String cancel = data.getExtras().getString("cancel");
        if (cancel.equals("false")) {
            this.date = data.getExtras().getString("date");
            this.sortOrder = data.getExtras().getString("sortOrder");
            this.sortOrderPosition = data.getExtras().getInt("sortOrderPosition");
            this.deskValue = data.getExtras().getString("deskValue");
            this.cb1 = data.getExtras().getBoolean("cb1");
            this.cb2 = data.getExtras().getBoolean("cb2");
            this.cb3 = data.getExtras().getBoolean("cb3");

            callNYSearchAPI();
        }
    }


    private void callNYSearchAPI() {

        final AsyncHttpClient client = new AsyncHttpClient();
        final String url = "http://api.nytimes.com/svc/search/v2/articlesearch.json?";
        final RequestParams params = new RequestParams();
        params.put("api-key", "afb8daecdd2e8883ced96fa872378851:13:74359498");
        String query = newQuery;

        if(!isNetworkAvailable())
            Toast.makeText(this, "Cannot connect to NYTimes! \nPlease check your Internet connectivity.", Toast.LENGTH_LONG).show();

        if (query.isEmpty() && currentQuery.isEmpty()){
            return;
        }
        if (!this.currentQuery.isEmpty()) {
            if (!this.currentQuery.equalsIgnoreCase(query)) {
//                articles.clear();
                adapter.clearData();
                pageoffset = 0;
                Log.d("DEBUG", "Adding offset"+pageoffset);
                this.currentQuery = query;
            } else {
                Log.d("DEBUG", "Adding offset"+pageoffset);
                pageoffset += 4;
            }
        }
        else
            this.currentQuery =query;
        params.put("page", this.pageoffset);
        params.put("q", query);

        if(!this.sortOrder.isEmpty())
            params.put("sort",this.sortOrder);
        if(!this.deskValue.isEmpty())
            params.put("fq", "news_desk:(" + this.deskValue + ")");
        if(!this.date.isEmpty())
            params.put("begin_date", this.date.replace("/",""));
        Log.d("DEBUG", this.date);
        Log.d("DEBUG", this.date.replace("/",""));
        client.get(url, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                JSONArray articleJsonResults = null;
                try {
                    Log.d("DEBUG", url.toString() + params.toString());
                    Log.d("DEBUG", response.toString());
//                    String hints =  response.getJSONObject("response").getJSONObject("meta").getString("hints");
                    articleJsonResults = response.getJSONObject("response").getJSONArray("docs");
                    Log.d("DEBUG", Article.fromJSONArray(articleJsonResults).toString());
//                    Log.d("DEBUG", String.valueOf(adapter.getItemCount()));
                    articles.addAll(Article.fromJSONArray(articleJsonResults));
                    adapter.notifyItemInserted(articles.size() - 1);
                    //                Log.d("DEBUG", hints);
                  //  Log.d("DEBUG", adapter.toString());
                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(),
                            "ERROR reading response", Toast.LENGTH_SHORT).show();
                    Log.e(getClass().toString(), "Error encountered while parsing JSON", e);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("DEBUG", url.toString() + params.toString());
                Toast.makeText(getApplicationContext(),
                        "ERROR reading response", Toast.LENGTH_SHORT).show();
                Log.e(getClass().toString(), "Error encountered while parsing JSON");
            }
        });
    }

//    public void onArticleSearch(View view) {
//        callNYSearchAPI();
//    }

    private Boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

}
