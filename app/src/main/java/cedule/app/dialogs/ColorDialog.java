package cedule.app.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import cedule.app.R;


public class ColorDialog extends DialogFragment {
    private View view;
    private ImageButton ib = null;

    private void pickColor(View view) {
        if (ib != null) {
            ib.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_circle));
        }
        ib = (ImageButton) view;
        ib.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_circle_selected));
    }

    @Override @NonNull
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        view = inflater.inflate(R.layout. dialog_color, null);


        builder.setView(view);

        builder.setPositiveButton("Apply", (dialog, which) -> {
            Bundle bundle = new Bundle();
            bundle.putInt("color", ib.getImageTintList().getDefaultColor());
            requireActivity().getSupportFragmentManager().setFragmentResult("pickColor", bundle);
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dismiss());

        AlertDialog dialog = builder.create();

        view.findViewById(R.id.ib_color_red).setOnClickListener(this::pickColor);
        view.findViewById(R.id.ib_color_purple).setOnClickListener(this::pickColor);
        view.findViewById(R.id.ib_color_blue).setOnClickListener(this::pickColor);
        view.findViewById(R.id.ib_color_green).setOnClickListener(this::pickColor);

        return dialog;
    }
}
