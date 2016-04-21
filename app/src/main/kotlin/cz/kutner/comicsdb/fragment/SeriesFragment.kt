package cz.kutner.comicsdb.fragment

import android.app.SearchManager
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cz.kutner.comicsdb.ComicsDBApplication
import cz.kutner.comicsdb.adapter.SeriesListAdapter
import cz.kutner.comicsdb.connector.service.SeriesService
import cz.kutner.comicsdb.di.Tracker
import cz.kutner.comicsdb.model.Series
import cz.kutner.comicsdb.utils.Utils
import org.jetbrains.anko.async
import org.jetbrains.anko.uiThread
import java.text.Normalizer
import javax.inject.Inject

class SeriesFragment : AbstractFragment<Series>() {

    @Inject lateinit var seriesService: SeriesService
    @Inject lateinit var tracker: Tracker

    init {
        preloadCount = 20
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity.application as ComicsDBApplication).applicationComponent.inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        adapter = SeriesListAdapter(activity, data)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun loadData() {
        if (!searchRunning) {
            searchRunning = true
            val args = this.arguments
            if (args != null && args.containsKey(SearchManager.QUERY)) {
                //neco vyhledavame
                var searchText: String = args.getString(SearchManager.QUERY)
                searchText = Normalizer.normalize(searchText, Normalizer.Form.NFD).replace("[\\p{InCombiningDiacriticalMarks}]".toRegex(), "")
                async() {
                    result = seriesService.searchSeries(searchText)
                    uiThread {
                        showData()
                        lastPage++
                    }
                }
                endless = false
            } else {
                //zobrazujeme nejnovější
                async() {
                    result = seriesService.getSeriesList(lastPage)
                    uiThread {
                        showData()
                        lastPage++
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val args = this.arguments
        if (args != null && args.containsKey(SearchManager.QUERY)) {
            (activity as AppCompatActivity).supportActionBar?.title = "Výsledek pro \"" + args.getString(SearchManager.QUERY) + "\""
            tracker.logVisit(screenName = "SeriesFragment - Search")
        } else {
            (activity as AppCompatActivity).supportActionBar?.title = "Serie"
            tracker.logVisit(screenName = "SeriesFragment - List")
            Utils.logVisitToFabricAnswers(contentName = "Zobrazení seznamu sérií", contentType = "Série")
        }
    }

    companion object {

        fun newInstance(): SeriesFragment {

            val args = Bundle()

            val fragment = SeriesFragment()
            fragment.arguments = args
            return fragment
        }
    }
}


