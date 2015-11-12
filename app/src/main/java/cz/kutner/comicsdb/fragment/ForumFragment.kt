package cz.kutner.comicsdb.fragment

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.crashlytics.android.answers.Answers
import com.crashlytics.android.answers.ContentViewEvent
import com.google.android.gms.analytics.HitBuilders
import cz.kutner.comicsdb.ComicsDBApplication
import cz.kutner.comicsdb.connector.helper.ForumHelper
import cz.kutner.comicsdb.holder.ForumViewHolder
import cz.kutner.comicsdb.model.ForumEntry
import org.jetbrains.anko.async
import org.jetbrains.anko.uiThread
import uk.co.ribot.easyadapter.EasyRecyclerAdapter

public class ForumFragment : AbstractFragment<ForumEntry>() {

    init {
        preloadCount = 20
        spinnerEnabled = true
        spinnerValues = arrayOf("Všechna fora", "* Připomínky a návrhy", "Fabula Rasa", "Filmový klub", "Pindárna", "Povinná četba", "Poznej comics nebo postavu", "Sběratelský klub", "Slevy, výprodeje, bazary", "Srazy, cony, festivaly", "Stripy, jouky, fejky :)")
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        adapter = EasyRecyclerAdapter(
                context,
                ForumViewHolder::class.java,
                data as List<ForumEntry>)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun loadData() {
        if (!searchRunning) {
            searchRunning = true
            val searchText = ""
            async {
                result = ComicsDBApplication.forumService.filteredForumList(lastPage, ForumHelper.getForumId(filter), searchText)
                uiThread {
                    showData()
                    lastPage++
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        (activity as AppCompatActivity).supportActionBar?.title = "Forum"
        val tracker = ComicsDBApplication.tracker
        tracker.setScreenName("ForumFragment")
        tracker.send(HitBuilders.ScreenViewBuilder().build())
        Answers.getInstance().logContentView(ContentViewEvent().putContentName("Zobrazení fór").putContentType("Fórum"))
    }

    companion object {

        public fun newInstance(): ForumFragment {

            val args = Bundle()

            val fragment = ForumFragment()
            fragment.arguments = args
            return fragment
        }
    }
}
