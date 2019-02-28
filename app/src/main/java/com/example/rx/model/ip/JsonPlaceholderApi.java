package com.example.rx.model.ip;


import android.util.Log;

import com.example.rx.model.entities.Comment;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;

public class JsonPlaceholderApi {
    private String urlPath;

    public JsonPlaceholderApi(String urlPath) {
        this.urlPath = urlPath;
    }

    public Single<ArrayList<Comment>> getCommentList() {
        return Single.create(new SingleOnSubscribe<ArrayList<Comment>>() {
            @Override
            public void subscribe(SingleEmitter<ArrayList<Comment>> emitter) throws Exception {
                String userJsonStroke = getJsonFromServer(urlPath, 2000);
                JSONArray commentArray = new JSONArray(userJsonStroke);

                ArrayList<Comment> comments = new ArrayList<>();

                for (int i = 0; i< commentArray.length();i++) {

                    JSONObject commentRoot = commentArray.getJSONObject(i);

                    int postId = commentRoot.getInt("postId");
                    int id = commentRoot.getInt("id");
                    String userName = commentRoot.getString("name");
                    String userEmail = commentRoot.getString("email");
                    String body = commentRoot.getString("body");
                    Comment comment = new Comment(postId, id, userName, userEmail, body);
                    comments.add(comment);
                }
                emitter.onSuccess(comments);
            }
        });
    }

    private String getJsonFromServer(String urlPath, int timeout) throws IOException {
        URL url = new URL(urlPath);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(timeout);
        connection.setReadTimeout(timeout);
        connection.connect();

        int serverResponseCode = connection.getResponseCode();
        switch (serverResponseCode) {
            case 200:
            case 201:
                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String tmpLine;
                while ((tmpLine = br.readLine()) != null) {
                    sb.append(tmpLine).append("\n");
                }
                br.close();
                return sb.toString();
        }
        return null;
    }
}