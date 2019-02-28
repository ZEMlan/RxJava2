package com.example.rx;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.reactivex.Completable;
import io.reactivex.CompletableEmitter;
import io.reactivex.CompletableObserver;
import io.reactivex.CompletableOnSubscribe;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.rx.controller.adapter.CardRecyclerAdapter;
import com.example.rx.model.entities.Comment;
import com.example.rx.model.ip.JsonPlaceholderApi;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    MaterialButton butLoad;
    String urlPath = "http://jsonplaceholder.typicode.com/comments";

    RecyclerView recyclerView;
    CardRecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initUI();
        initObservers();


    }

    private void initUI() {
        butLoad = findViewById(R.id.butLoad);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CardRecyclerAdapter();
        recyclerView.setAdapter(adapter);
    }


    private void initObservers() {
        getCompletableLoadButton(butLoad)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onComplete() {
                        Log.d("Completable", "Load button clicked");
                        loadComments(urlPath);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }
                });
    }

    private Completable getCompletableLoadButton(final com.google.android.material.button.MaterialButton button) {
        return Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(final CompletableEmitter emitter) {
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v)
                    {
                        emitter.onComplete();
                    }
                });
            }
        });
    }

    private void loadComments(final String urlPath) {
        JsonPlaceholderApi jsonPlaceholderApi = new JsonPlaceholderApi(urlPath);
       jsonPlaceholderApi.getCommentList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<ArrayList<Comment>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.d("Single", "OnSubscribe");
                    }

                    @Override
                    public void onSuccess(ArrayList<Comment> comments) {
                        Log.d("Single", "Success");
                        adapter.setItems(comments);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }
                });
    }
}
