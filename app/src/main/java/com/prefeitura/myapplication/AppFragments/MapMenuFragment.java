package com.prefeitura.myapplication.AppFragments;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.prefeitura.myapplication.R;

public class MapMenuFragment extends Fragment {
    public EditText AutoText;
    public TextView route;
    public Button cancel, Conclude;
    public LinearLayout RouteTask;
    public boolean isVisible = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.map_menu, container, false);

        RouteTask = view.findViewById(R.id.RoutTask);
        route = view.findViewById(R.id.route);
        cancel = view.findViewById(R.id.cancel);
        Conclude = view.findViewById(R.id.Conclude);
        AutoText = view.findViewById(R.id.AutoText);

        AutoText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (keyEvent.getAction() == KeyEvent.ACTION_DOWN &&
                    i == KeyEvent.KEYCODE_ENTER){
                    String text = AutoText.getText().toString();

                    if (!text.isEmpty() && !isVisible){
                        RouteTask.setVisibility(View.VISIBLE);
                        route.setText(text);
                    }

                    return true;
                }
                return false;
            }
        });

        return view;
    }
}