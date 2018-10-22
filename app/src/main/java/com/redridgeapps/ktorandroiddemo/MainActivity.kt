package com.redridgeapps.ktorandroiddemo

import android.os.Bundle
import android.preference.PreferenceManager
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.readystatesoftware.chuck.ChuckInterceptor
import com.redridgeapps.ktorandroiddemo.repo.PreferenceRepository
import com.redridgeapps.ktorandroiddemo.repo.RedditRepository
import com.squareup.moshi.Moshi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class MainActivity : AppCompatActivity(), CoroutineScope {

    private lateinit var job: Job
    private lateinit var redditRepo: RedditRepository
    private lateinit var recyclerView: RecyclerView
    private lateinit var postAdapter: PostAdapter

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        job = Job()

        initializeDependencies()
        setupRecyclerView()

        launch {
            val listing = redditRepo.getAndroidDevHot().data.children.map { it.post.title }
            postAdapter.submitList(listing)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    private fun initializeDependencies() {

        val chuckInterceptor = ChuckInterceptor(applicationContext)

        val moshi = Moshi.Builder().build()
        val moshiSerializer = MoshiSerializer(moshi)

        val prefs = PreferenceManager.getDefaultSharedPreferences(applicationContext)

        val prefRepo = PreferenceRepository(moshi, prefs)
        redditRepo = RedditRepository(moshiSerializer, prefRepo, chuckInterceptor)
    }

    private fun setupRecyclerView() {
        recyclerView = findViewById(R.id.recycler_view)
        postAdapter = PostAdapter()

        val newLayoutManager = LinearLayoutManager(this@MainActivity)

        recyclerView.setHasFixedSize(true)
        recyclerView.addItemDecoration(DividerItemDecoration(this@MainActivity, newLayoutManager.orientation))

        recyclerView.layoutManager = newLayoutManager
        recyclerView.adapter = postAdapter
    }
}
