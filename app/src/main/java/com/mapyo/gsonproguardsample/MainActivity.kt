package com.mapyo.gsonproguardsample

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query


class MainActivity : AppCompatActivity() {

    var stringList = mutableListOf<String>()
    var repoList = mutableListOf<Repository>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val listView = findViewById(R.id.list_view) as ListView
        val adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1)
        listView.adapter = adapter
        listView.setOnItemClickListener { parent, view, position, id ->
            Toast.makeText(this, repoList[position].htmlUrl, Toast.LENGTH_SHORT).show()
        }

        val retrofit = Retrofit.Builder()
                .baseUrl("https://api.github.com")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()

        val user = "mapyo"
        val sort = "created"
        val direction = "desc"
        retrofit.create(GitHubApi::class.java).fetchRepos(user, sort, direction)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ repositories ->
                    repoList.addAll(repositories)

                    repositories.map {
                        stringList.add("${it.id} ${it.name}")
                    }
                    adapter.addAll(stringList)
                }, Throwable::printStackTrace)
    }

    interface GitHubApi {
        @GET("/users/{user}/repos")
        fun fetchRepos(
                @Path("user") user: String,
                @Query("sort") sort: String,
                @Query("direction") direction: String
        ): Observable<List<Repository>>
    }
}
