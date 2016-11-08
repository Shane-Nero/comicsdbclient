package cz.kutner.comicsdb.activity

import android.os.Bundle
import cz.kutner.comicsdb.ComicsDBApplication
import cz.kutner.comicsdb.R
import cz.kutner.comicsdb.adapter.ComicsDetailAdapter
import cz.kutner.comicsdb.connector.service.ComicsDetailService
import cz.kutner.comicsdb.di.Tracker
import cz.kutner.comicsdb.model.Comics
import cz.kutner.comicsdb.utils.Utils
import kotlinx.android.synthetic.main.fragment_list.*
import javax.inject.Inject

class ComicsDetailActivity : AbstractDetailActivity<Comics>() {

    override val prefix: String by lazy { getString(R.string.url_comics_detail) }
    override val extraName = MainActivity.COMICS_ID

    @Inject lateinit var comicsDetailService: ComicsDetailService
    @Inject lateinit var tracker: Tracker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (application as ComicsDBApplication).applicationComponent.inject(this)
    }

    override fun loadData() {
        runAsync(comicsDetailService.getComics(id))
    }

    override fun showData() {
        supportActionBar?.title = result.name
        val adapter = ComicsDetailAdapter(this, listOf(result) + result.comments)
        recycler_view.adapter = adapter
        recycler_view.setHasFixedSize(true)
        switcher.showContentView()
        tracker.logVisit(screenName = "ComicsDetailFragment", category = "Detail", action = result.name)
        Utils.logVisit(contentName = "Zobrazení detailu komiksu", contentType = "Comics", contentId = result.name)
    }
}
