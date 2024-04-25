package cedule.app.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
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
    private ImageButton ib = null;

    private void pickColor(View view) {
        if (ib != null) {
            ib.setImageDrawable(ContextCompat.getDrawable(requireActivity(), R.drawable.ic_circle));
        }

        ib = (ImageButton) view;
        ib.setImageDrawable(ContextCompat.getDrawable(requireActivity(), R.drawable.ic_circle_selected));
    }

    private void handleOnClickPosBtn(DialogInterface dialogInterface, int which) {
        Bundle bundle = new Bundle();
        bundle.putInt("color", ib.getImageTintList().getDefaultColor());

        requireActivity().getSupportFragmentManager()
                .setFragmentResult("pickColor", bundle);
    }

    @Override @NonNull
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_color, null);

        view.findViewById(R.id.ib_color_yellow).setOnClickListener(this::pickColor);
        view.findViewById(R.id.ib_color_purple).setOnClickListener(this::pickColor);
        view.findViewById(R.id.ib_color_blue).setOnClickListener(this::pickColor);
        view.findViewById(R.id.ib_color_green).setOnClickListener(this::pickColor);

        return new AlertDialog.Builder(getActivity())
                .setView(view)
                .setPositiveButton("Apply", this::handleOnClickPosBtn)
                .setNegativeButton("Cancel", (dialogInterface, which) -> dismiss())
                .create();
    }
}
