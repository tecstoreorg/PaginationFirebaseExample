package com.devlomi.paginationinfirebaseexample;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.marcorei.infinitefire.InfiniteFireArray;

import static com.devlomi.paginationinfirebaseexample.R.id.rv;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Query query = FirebaseDatabase.getInstance().getReference().orderByKey();

        final InfiniteFireArray<Model> array = new InfiniteFireArray<>(
                Model.class,// Model Class
                query,//Database Ref
                20,// Initial Size
                20, //how many items to Load every time
                true,//limitToFirst //True means the old appears on top
                true
        );

        final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_to_refresh);
        RecyclerView recyclerView = (RecyclerView) findViewById(rv);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        final Adapter adapter = new Adapter(array, this);
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                array.reset();
            }
        });


        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);


        array.addOnLoadingStatusListener(new InfiniteFireArray.OnLoadingStatusListener() {
            @Override
            public void onChanged(EventType type) {
                switch (type) {
                    case LoadingContent:
                        adapter.setLoadingMore(true);
                        break;
                    case LoadingNoContent:
                        adapter.setLoadingMore(false);
                        break;
                    case Done:
                        swipeRefreshLayout.setRefreshing(false);
                        adapter.setLoadingMore(false);
                        break;
                }
            }
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy < 0) {
                    return;
                }
                if (layoutManager.findLastVisibleItemPosition() < array.getCount() - 20) {
                    return;
                }

                array.more();
            }
        });
    }
}
