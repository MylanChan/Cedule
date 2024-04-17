package cedule.app.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;


public class FilterDialog extends DialogFragment {

    @Override @NonNull
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setView(requireActivity().getLayoutInflater().
                inflate(cedule.app.R.layout.dialog_filter, null));

        builder.setPositiveButton("Filter", (dialog, which) -> {

        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dismiss());

        return builder.create();
    }
}
