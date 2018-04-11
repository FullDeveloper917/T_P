package com.property.david.tp.Dialogs;

import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
//import com.property.david.tp.Fragments.SearchFragment;
import com.property.david.tp.Fragments.SearchFragment;
import com.property.david.tp.MainActivity;
import com.property.david.tp.Models.Topic;
import com.property.david.tp.R;

import java.util.HashMap;

/**
 * Created by david on 11/8/17.
 */

public class CreateNewTopicDialog extends Dialog implements View.OnClickListener{
    public MainActivity activity;
    public Button btnCreate, btnCancel;
    public EditText edtTitle;
    public TextView txtWarning;

    public CreateNewTopicDialog(MainActivity activity) {
        super(activity);
        this.activity = activity;
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_create_new_topic);
        btnCreate= (Button) findViewById(R.id.btnCreate_CreateNewTopicDialog);
        btnCancel = (Button) findViewById(R.id.btnCancel_CreateNewTopicDialog);
        edtTitle = findViewById(R.id.edtTitle_CreateNewTopicDialog);
        txtWarning = findViewById(R.id.txtWarning_CreateNewTopicDialog);
        btnCreate.setOnClickListener(this);
        btnCancel.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnCreate_CreateNewTopicDialog:
                final String newTitle = edtTitle.getText().toString();
                activity.mDatabase.child("topic").child("newTitle").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() == null) {
                            activity.mDatabase.child("topic").child("newTitle").setValue("");
                            Toast.makeText(activity, "Success", Toast.LENGTH_SHORT).show();
                            dismiss();
                        } else {
                            txtWarning.setText(newTitle + " already exits");
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                break;
            case R.id.btnCancel_CreateNewTopicDialog:
                dismiss();
                break;
            default:
                break;
        }
        dismiss();
    }


}
