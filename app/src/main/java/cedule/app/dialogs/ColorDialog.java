package cedule.app.dialogs;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

import cedule.app.R;
import cedule.app.activities.MainActivity;
import cedule.app.data.entities.Category;
import cedule.app.utils.TimeUtils;


public class ColorDialog extends DialogFragment {
    private View view;
    private Integer color = null;

    private void pickColor(View view) {
        color = view.getBackgroundTintList().getDefaultColor();
    }

    @Override @NonNull
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        view = inflater.inflate(R.layout. dialog_color, null);


        builder.setView(view);

        builder.setPositiveButton("Apply", (dialog, which) -> {
            Bundle bundle = new Bundle();
            bundle.putInt("color", color);
            requireActivity().getSupportFragmentManager().setFragmentResult("pickColor", bundle);
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dismiss());

        AlertDialog dialog = builder.create();

        view.findViewById(R.id.ll_color_red).setOnClickListener(this::pickColor);
        view.findViewById(R.id.ll_color_blue).setOnClickListener(this::pickColor);
        view.findViewById(R.id.ll_color_green).setOnClickListener(this::pickColor);
        view.findViewById(R.id.ll_color_purple).setOnClickListener(this::pickColor);

        return dialog;
    }
}
