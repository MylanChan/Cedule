package cedule.app.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import cedule.app.R;
import cedule.app.activities.DocumentActivity;

public class WidgetsFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
        View view = inflater.inflate(R.layout.fragment_widgets, container, false);

        view.findViewById(R.id.btn_about).setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), DocumentActivity.class);
            intent.putExtra("type", DocumentActivity.TYPE_ABOUT);
            startActivity(intent);
        });


        view.findViewById(R.id.btn_story).setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), DocumentActivity.class);
            intent.putExtra("type", DocumentActivity.TYPE_STORY);
            startActivity(intent);
        });

        return view;
    }
}
