package com.property.david.tp.Fragments;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SearchView;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SearchFragment extends Fragment {

    private boolean isLived;
    private Button btnAdd;
//    private Button btnCancel;
    private SearchView searchView;
    private ListView listViewTopic;
    private TopicListAdapter topicListAdapter;
    private TopicListAdapter searchedTopicAdapter;
    public List<Topic> searchedTopicList;

    private Typeface iconFont;

    private MainActivity myActivity;

    public MainActivity getMyActivity() {
        return myActivity;
    }

    public void setMyActivity(MainActivity myActivity) {
        this.myActivity = myActivity;
        isLived = false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        setInit();

        findViews(view);

        refreshListView();

        setFormula(view);

        setEvents();


        return view;
    }

    @Override
    public void onStart() {
        isLived = true;
        super.onStart();
    }

    @Override
    public void onStop() {
        isLived = false;
        super.onStop();
    }

    private void setInit(){
        iconFont = FontManager.getTypeface(myActivity, FontManager.FONTAWESOME);
        searchedTopicList = new ArrayList<>();
        searchedTopicAdapter = new TopicListAdapter(myActivity, R.layout.one_topic);
        topicListAdapter = new TopicListAdapter(myActivity, R.layout.one_topic);
    }

    private void findViews(View view){
        btnAdd = view.findViewById(R.id.btnAdd_SearchFrg);
        searchView = view.findViewById(R.id.searchView_SearchFrg);
        listViewTopic = view.findViewById(R.id.listTopics_SearchFrg);
    }

    private void setFormula(View view){
        FontManager.setFontType(view.findViewById(R.id.layoutMain_SearchFrg), iconFont);
    }

    private void setEvents(){
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new CreateNewTopicDialog(myActivity).show();
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                if (newText.isEmpty()) {
                    topicListAdapter.setList(myActivity.allTopicList);
                    listViewTopic.setAdapter(topicListAdapter);
                } else {
                    searchedTopicList.clear();
                    for (int i = 0; i < myActivity.allTopicList.size(); i++) {
                        Topic currentTopic = myActivity.allTopicList.get(i);
                        if (currentTopic.getTitle().toLowerCase().contains(newText.toLowerCase())) {
                            searchedTopicList.add(currentTopic);
                        }
                    }
                    searchedTopicAdapter.setList(searchedTopicList);
                    listViewTopic.setAdapter(searchedTopicAdapter);
                }
                return false;
            }
        });

    }

    public void refreshListView() {
        if (topicListAdapter == null) return;
        if (listViewTopic == null) return;

        Collections.sort(myActivity.allTopicList, new Comparator<Topic>() {
            @Override
            public int compare(Topic lhs, Topic rhs) {
                return lhs.getMarkList().size() > rhs.getMarkList().size() ? -1 : (lhs.getMarkList().size() < rhs.getMarkList().size()) ? 1 : 0;
            }
        });

        if (myActivity.allTopicList.size() < 7)
            topicListAdapter.setList(myActivity.allTopicList);
        else
            topicListAdapter.setList(myActivity.allTopicList.subList(0, 7));

        listViewTopic.setAdapter(topicListAdapter);

        myActivity.progressBar.setVisibility(View.GONE);

    }
}