package com.property.david.tp.Fragments;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.property.david.tp.Adapters.TopicListAdapter;
import com.property.david.tp.Dialogs.CreateNewTopicDialog;
import com.property.david.tp.MainActivity;
import com.property.david.tp.Models.Topic;
import com.property.david.tp.R;
import com.property.david.tp.Utils.FontManager;

import java.util.Collections;
import java.util.Comparator;

public class RecentFragment extends Fragment {

    private Button btnAdd;
    private ListView listViewTopic;
    private TopicListAdapter topicListAdapter;
    private Typeface iconFont;
    private MainActivity myActivity;

    public MainActivity getMyActivity() {
        return myActivity;
    }

    public void setMyActivity(MainActivity myActivity) {
        this.myActivity = myActivity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_recent, container, false);

        setInit();

        findViews(view);

        refreshListView();

        setFormula(view);

        setEvents();

        return view;
    }

    private void setInit(){
        iconFont = FontManager.getTypeface(myActivity, FontManager.FONTAWESOME);
        topicListAdapter = new TopicListAdapter(myActivity, R.layout.one_topic);
    }

    private void findViews(View view){
        btnAdd = view.findViewById(R.id.btnAdd_RecentFrg);
        listViewTopic = view.findViewById(R.id.listTopics_RecentFrg);
    }

    private void setFormula(View view){
        FontManager.setFontType(view.findViewById(R.id.layoutMain_RecentFrg), iconFont);
    }

    private void setEvents(){
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new CreateNewTopicDialog(myActivity).show();

            }
        });


    }

    public void refreshListView() {

        if (topicListAdapter == null) return;
        if (listViewTopic == null) return;


        Collections.sort(myActivity.myTopicList, new Comparator<Topic>() {
            @Override
            public int compare(Topic lhs, Topic rhs) {
                return rhs.getMyMark().getDate().compareTo(lhs.getMyMark().getDate());
            }
        });

        if (myActivity.myTopicList.size() < 7)
            topicListAdapter.setList(myActivity.myTopicList);
        else
            topicListAdapter.setList(myActivity.myTopicList.subList(0, 7));

        listViewTopic.setAdapter(topicListAdapter);

        myActivity.progressBar.setVisibility(View.GONE);
    }
}
