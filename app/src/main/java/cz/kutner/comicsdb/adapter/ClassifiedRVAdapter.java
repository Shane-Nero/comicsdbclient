package cz.kutner.comicsdb.adapter;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cz.kutner.comicsdb.ComicsDBApplication;
import cz.kutner.comicsdb.R;
import cz.kutner.comicsdb.model.Classified;

public class ClassifiedRVAdapter extends RecyclerView.Adapter<ClassifiedRVAdapter.ClassifiedViewHolder> {

    public static class ClassifiedViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.classified_nick_icon)
        ImageView classifiedNickIcon;
        @Bind(R.id.classified_nick)
        TextView classifiedNick;
        @Bind(R.id.classified_category)
        TextView classifiedCategory;
        @Bind(R.id.classified_time)
        TextView classifiedTime;
        @Bind(R.id.classified_text)
        TextView classifiedText;
        @Bind(R.id.card_view_comments)
        CardView cardViewComments;

        ClassifiedViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }
    }

    private List<Classified> entries;

    public ClassifiedRVAdapter(List<Classified> entries) {
        this.entries = entries;
    }


    @Override
    public int getItemCount() {
        return entries.size();
    }

    @Override
    public ClassifiedViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_classified, viewGroup, false);
        return new ClassifiedViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ClassifiedViewHolder classifiedViewHolder, int i) {
        classifiedViewHolder.classifiedNick.setText(entries.get(i).getNick());
        classifiedViewHolder.classifiedTime.setText(entries.get(i).getTime());
        classifiedViewHolder.classifiedText.setText(Html.fromHtml(entries.get(i).getText()));
        Picasso.with(ComicsDBApplication.getContext()).load(entries.get(i).getIconUrl()).into(classifiedViewHolder.classifiedNickIcon);
        classifiedViewHolder.classifiedCategory.setText(entries.get(i).getCategory());
    }

}
