package cz.kutner.comicsdb.fragment


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.crashlytics.android.answers.Answers
import com.crashlytics.android.answers.ContentViewEvent
import com.google.android.gms.analytics.HitBuilders
import cz.kutner.comicsdb.ComicsDBApplication
import cz.kutner.comicsdb.R
import cz.kutner.comicsdb.Utils
import cz.kutner.comicsdb.adapter.AuthorDetailRVAdapter
import cz.kutner.comicsdb.model.Author
import kotlinx.android.synthetic.fragment.*
import org.jetbrains.anko.onClick
import pl.aprilapps.switcher.Switcher
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers


public class AuthorDetailFragment : Fragment() {

    private var switcher: Switcher? = null

    var subscription: Subscription? = null

    private var author: Author? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        switcher = Switcher.Builder().withContentView(content).withEmptyView(empty_view).withProgressView(progress_view).withErrorView(error_view).build()
        val llm = LinearLayoutManager(view?.context)
        try_again.onClick {
            if (Utils.isConnected()) {
                onResume()
            }
        }
        recycler_view.layoutManager = llm
        spinner.visibility = View.GONE
        filter_text.visibility = View.GONE
        switcher?.showProgressView()
    }

    private fun loadData() {
        subscription = ComicsDBApplication.authorDetailService.authorDetail(this.arguments.getInt("id")).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe { author1 ->
            author = author1
            showData()
        }
    }

    override fun onResume() {
        super.onResume()
        if (!Utils.isConnected()) {
            switcher?.showErrorView()
        } else {
            switcher?.showProgressView()
            if (author != null) {
                showData()
            } else {
                loadData()
            }
        }
    }


    private fun showData() {
        if (author != null) {
            var existing_author: Author = author as Author
            (activity as AppCompatActivity).supportActionBar?.title = existing_author.name
            val adapter = AuthorDetailRVAdapter(existing_author)
            recycler_view.adapter = adapter
            recycler_view.setHasFixedSize(true)
            switcher?.showContentView()
            val tracker = ComicsDBApplication.tracker
            tracker.setScreenName("AuthorDetailFragment")
            tracker.send(HitBuilders.ScreenViewBuilder().build())
            tracker.send(HitBuilders.EventBuilder().setCategory("Detail").setAction(existing_author.name).build())
            Answers.getInstance().logContentView(ContentViewEvent().putContentName("Zobrazení detailu autora").putContentType("Autor").putContentId(existing_author.name))
        }
    }

    companion object {

        public fun newInstance(): AuthorDetailFragment {
            val args = Bundle()
            val fragment = AuthorDetailFragment()
            fragment.arguments = args
            return fragment
        }
    }
}
