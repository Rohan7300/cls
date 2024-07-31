package com.clebs.celerity.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.clebs.celerity.R
import com.clebs.celerity.adapters.TableViewAdapter
import com.clebs.celerity.databinding.ActivitySplashBinding
import com.clebs.celerity.databinding.ActivityWeeklyPerformanceBinding
import com.clebs.celerity.models.MovieModel

class WeeklyPerformanceActivity : AppCompatActivity() {
    private lateinit var ActivitySplashBinding: ActivityWeeklyPerformanceBinding
    lateinit var TableViewAdapter: TableViewAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val movieArraylist = ArrayList<MovieModel>()
        movieArraylist.add(MovieModel(1, "yo", 1993, 100,1, "yo","kkdsskds", 1993, 100))
        movieArraylist.add(MovieModel(1, "yo", 1993, 100,1, "yo", "kkdsskds",1993, 100))
        movieArraylist.add(MovieModel(1, "yo", 1993, 100,1, "yo", "kkdsskds", 1993, 100))
        movieArraylist.add(MovieModel(1, "yo", 1993, 100,1, "yo", "kkdsskds",1993, 100))
        movieArraylist.add(MovieModel(1, "yo", 1993, 100,1, "yo", "kkdsskds",1993, 100))
        movieArraylist.add(MovieModel(1, "yo", 1993, 100,1, "yo", "kkdsskds",1993, 100))
        movieArraylist.add(MovieModel(1, "yo", 1993, 100,1, "yo", "kkdsskds",1993, 100))
        movieArraylist.add(MovieModel(1, "yo", 1993, 100,1, "yo", "kkdsskds",1993, 100))
        ActivitySplashBinding =
            DataBindingUtil.setContentView(this@WeeklyPerformanceActivity, R.layout.activity_weekly_performance)
        TableViewAdapter = TableViewAdapter(this,movieArraylist)
        ActivitySplashBinding.recyclerViewMovieList.adapter = TableViewAdapter


    }
}