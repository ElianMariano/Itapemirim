package com.prefeitura.myapplication.AppFragments;

import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.prefeitura.myapplication.R;

public class SugestFragment extends Fragment {
    private ScrollView parent;
    private TextView text;
    private ImageView image;
    private boolean isVisible = false;
    public Button go_button;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the sugest layout
        View view = inflater.inflate(R.layout.place_sugest, container, false);

        // Create the references to the widgets
        parent = view.findViewById(R.id.parent);
        text = view.findViewById(R.id.description);
        image = view.findViewById(R.id.image);
        go_button = view.findViewById(R.id.go_button);

        return view;
    }

    // Change the visibility of the fragment
    public void setVisibility(boolean vis){
        if (vis){
            isVisible = true;
            parent.setVisibility(View.VISIBLE);
        }
        else{
            isVisible = false;
            parent.setVisibility(View.GONE);
        }
    }

    // Get the current state of the fragment
    public boolean getVisible(){
        return isVisible;
    }

    public void setImage(int res){
        image.setImageDrawable(getResources().getDrawable(res));
    }

    public void setText(String txt){
        text.setText(txt);
    }

    public String getText(){
        return String.valueOf(text.getText());
    }
}
