package comicsdb.kutner.cz;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.parser.Parser;

import cz.kutner.comicsdbclient.comicsdbclient.R;

/**
 * Created by lukas.kutner on 31.3.2015.
 */


public class ComicsDetailFragment extends Fragment {
    private final String LOG_TAG = ComicsDetailFragment.class.getSimpleName();

    public ComicsDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.comics_detail, container, false);
        Bundle args = this.getArguments();
        String url = args.getString("url");
        FetchComicsDetail task = new FetchComicsDetail(this.getActivity());
        task.execute(url);
        return rootView;
    }

    public class FetchComicsDetail extends AsyncTask<String, Void, Comics> {
        private final String LOG_TAG = FetchComicsDetail.class.getSimpleName();

        Activity myActivity;

        public FetchComicsDetail(Activity activity) {
            this.myActivity = activity;
        }


        @Override
        protected void onPostExecute(Comics result) {
            if (result != null) {
                TextView name = (TextView) myActivity.findViewById(R.id.name);
                TextView rating = (TextView) myActivity.findViewById(R.id.rating);
                TextView description = (TextView) myActivity.findViewById(R.id.description);
                TextView url = (TextView) myActivity.findViewById(R.id.url);
                TextView published = (TextView) myActivity.findViewById(R.id.published);
                TextView voteCount = (TextView) myActivity.findViewById(R.id.voteCount);
                TextView genre = (TextView) myActivity.findViewById(R.id.genre);
                TextView publisher = (TextView) myActivity.findViewById(R.id.publisher);
                TextView ISSN = (TextView) myActivity.findViewById(R.id.ISSN);
                TextView issueNumber = (TextView) myActivity.findViewById(R.id.issueNumber);
                TextView binding = (TextView) myActivity.findViewById(R.id.binding);
                TextView format = (TextView) myActivity.findViewById(R.id.format);
                TextView pagesCount = (TextView) myActivity.findViewById(R.id.pagesCount);
                TextView print = (TextView) myActivity.findViewById(R.id.print);
                TextView originalName = (TextView) myActivity.findViewById(R.id.originalName);
                TextView originalPublisher = (TextView) myActivity.findViewById(R.id.originalPublisher);
                TextView price = (TextView) myActivity.findViewById(R.id.price);
                TextView notes = (TextView) myActivity.findViewById(R.id.notes);
                TextView authors = (TextView) myActivity.findViewById(R.id.authors);
                TextView series = (TextView) myActivity.findViewById(R.id.series);

                name.setText(result.getName());
                rating.setText(result.getRating().toString());
                description.setText(result.getDescription());
                url.setText(result.getUrl());
                published.setText(result.getPublished());
                voteCount.setText(result.getVoteCount().toString());
                genre.setText(result.getGenre());
                publisher.setText(result.getPublisher());
                ISSN.setText(result.getISSN());
                issueNumber.setText(result.getIssueNumber());
                binding.setText(result.getBinding());
                format.setText(result.getFormat());
                pagesCount.setText(result.getPagesCount());
                print.setText(result.getPrint());
                originalName.setText(result.getOriginalName());
                originalPublisher.setText(result.getOriginalPublisher());
                price.setText(result.getPrice());
                notes.setText(result.getNotes());
                authors.setText(result.getAuthors());
                series.setText(result.getSeries());

            }
        }

        @Override
        protected Comics doInBackground(String... params) {
            Comics comics = new Comics();
            try {
                Document doc;
                Node sibling;
                doc = Jsoup.connect("http://comicsdb.cz/" + params[0]).get();
                // title - H5
                String name = doc.select("H5").text();
                comics.setName(Parser.unescapeEntities(name, false));
                // rating - #rating_block - vrací Celkové hodnocení 67% (26) 1 2 3 4 5
                String rating_and_count = doc.select("#rating_block h2").first().nextSibling().toString();
                String rating = rating_and_count.substring(1, rating_and_count.indexOf('%'));
                comics.setRating(Integer.valueOf(rating));
                String votes = rating_and_count.substring(rating_and_count.indexOf('(') + 1, rating_and_count.indexOf(')'));
                comics.setVoteCount(Integer.valueOf(votes));
                for (Element titulek : doc.select(".titulek")) {
                    String title_name = titulek.text().substring(0, titulek.text().length() - 1);
                    Node title_value = titulek.nextSibling();
                    switch (title_name) {
                        case "Žánr":
                            comics.setGenre(Parser.unescapeEntities(title_value.toString().replaceAll("&nbsp;", " "), false));
                            break;
                        case "Vydal":
                            comics.setPublisher(Parser.unescapeEntities(title_value.nextSibling().toString(), false));
                            break;
                        case "Rok a měsíc vydání":
                            comics.setPublished(Parser.unescapeEntities(title_value.toString(), false));
                            break;
                        case "ISSN":
                            comics.setISSN(Parser.unescapeEntities(title_value.toString(), false));
                            break;
                        case "Vydání":
                            comics.setIssueNumber(Parser.unescapeEntities(title_value.toString(), false));
                            break;
                        case "Vazba":
                            comics.setBinding(Parser.unescapeEntities(title_value.toString(), false));
                            break;
                        case "Format":
                            comics.setFormat(Parser.unescapeEntities(title_value.toString(), false));
                            break;
                        case "Počet stran":
                            comics.setPagesCount(Parser.unescapeEntities(title_value.toString(), false));
                            break;
                        case "Tisk":
                            comics.setPrint(Parser.unescapeEntities(title_value.toString(), false));
                            break;
                        case "Název originálu":
                            comics.setOriginalName(Parser.unescapeEntities(title_value.toString(), false));
                            break;
                        case "Vydavatel originálu":
                            comics.setOriginalPublisher(Parser.unescapeEntities(title_value.toString(), false));
                            break;
                        case "Cena v době vydání":
                            comics.setPrice(Parser.unescapeEntities(title_value.toString(), false));
                            break;
                        case "Popis":
                            String description = "";
                            sibling = title_value.nextSibling();
                            while (true) {
                                if (!sibling.toString().startsWith("<br")) {
                                    description += sibling.toString();
                                }
                                sibling = sibling.nextSibling();
                                if (sibling.toString().startsWith("<span")) {
                                    break;
                                }
                            }
                            comics.setDescription(Parser.unescapeEntities(description, false));
                            break;
                        case "Poznámky":
                            String notes = "";
                            sibling = title_value.nextSibling();
                            while (true) {
                                if (!sibling.toString().startsWith("<br")) {
                                    notes += sibling.toString();
                                }
                                sibling = sibling.nextSibling();
                                if (sibling.toString().startsWith("<span")) {
                                    break;
                                }
                            }
                            comics.setNotes(Parser.unescapeEntities(notes, false));
                            break;
                        case "Autoři":
                            String authors = "";
                            sibling = title_value.nextSibling();
                            while (true) {
                                if (!sibling.toString().startsWith("<br")) {
                                    authors += sibling.toString();
                                }
                                sibling = sibling.nextSibling();
                                if (sibling.toString().startsWith("<span")) {
                                    break;
                                }
                            }
                            comics.setAuthors(Parser.unescapeEntities(authors, false));
                            break;
                        case "Série":
                            comics.setSeries(Parser.unescapeEntities(title_value.toString(), false));
                            break;
                    }
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                Log.e(LOG_TAG, e.getMessage(), e);
            }
            return comics;
        }
    }
}
