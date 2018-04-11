package com.property.david.tp.Adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.property.david.tp.Fragments.MapFragment;
import com.property.david.tp.MainActivity;
import com.property.david.tp.Models.Topic;
import com.property.david.tp.R;
import com.property.david.tp.Utils.FontManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by david on 11/8/17.
 */

public class TopicListAdapter extends ArrayAdapter {
    private final Context context;
    private List<Topic> lists = new ArrayList<>();

    private TextView txtTitle;
    private TextView txtSize;
    private LinearLayout layoutTopic;

    private MainActivity activity;

    public TopicListAdapter(MainActivity activity, @LayoutRes int resource) {
        super(activity, resource);
        this.context = activity;
        this.activity = activity;
    }

    public void setList(List<Topic> lists){
        this.lists = lists;
    }


    @Override
    public View getView(int position, final View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.one_topic, parent, false);

        findViews(rowView);

        setEvents(rowView, position);

        return rowView;
    }

    private void findViews(View view){

        txtTitle = view.findViewById(R.id.txtTitle_OneTopic);
        txtSize = view.findViewById(R.id.txtSize_OneTopic);
        layoutTopic = view.findViewById(R.id.layoutTopic_OneTopic);

    }

    private void setEvents(View view, int position) {

        int markCount = lists.get(position).getMarkList().size();
        if (markCount < 1000)
            txtSize.setText(String.valueOf(markCount));
        else
            txtSize.setText(String.valueOf(markCount / 1000) + "k");

        txtTitle.setText(lists.get(position).getTitle());

        layoutTopic.setTag(position);
        layoutTopic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int i = (int) view.getTag();
                MapFragment mapFragment = activity.mapFragment;
                mapFragment.setTopic(lists.get(i));
                activity.getSupportFragmentManager().beginTransaction().add(R.id.frgMain, mapFragment).commit();
            }
        });

    }

    @Override
    public int getCount() {
        return lists.size();
    }
}
