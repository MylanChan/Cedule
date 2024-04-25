package cedule.app.dialogs;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

import cedule.app.R;
import cedule.app.utils.LayoutUtils;
import cedule.app.utils.TimeUtils;


public class FilterDialog extends DialogFragment {
    private View view;

    private Long startDate;
    private Long endDate;

    private boolean isStartDate = false;

    private void handleOnClickFilter() {
        Bundle resultBundle = new Bundle();

        String category = ((AutoCompleteTextView) view.findViewById(R.id.atv_category)).getText().toString();

        if (!category.isEmpty()) {
            resultBundle.putString("category", category);
        }

        if (startDate != null && endDate != null) {
            resultBundle.putLong("startDate", startDate);
            resultBundle.putLong("endDate", endDate);
        }
        getParentFragmentManager().setFragmentResult("filterTask", resultBundle);
    }

    private void showDatePicker() {
        Calendar curr = Calendar.getInstance();

        DatePickerDialog dialog = new DatePickerDialog(requireActivity(), (v, y, m, dayOfM) -> {
                Calendar calendar = Calendar.getInstance();
                calendar.set(y, m, dayOfM);

                TimeUtils.setMidNight(calendar);

                if (isStartDate) {
                    TextView tvStartDate = view.findViewById(R.id.tv_startDate);
                    tvStartDate.setText(TimeUtils.toDateString(startDate = calendar.getTimeInMillis()));
                }
                else {
                    TextView tvEndDate = view.findViewById(R.id.tv_endDate);
                    tvEndDate.setText(TimeUtils.toDateString(endDate = calendar.getTimeInMillis()));
                }

            }, curr.get(Calendar.YEAR),curr.get(Calendar.MONTH), curr.get(Calendar.DAY_OF_MONTH));

        dialog.show();
    }

    @Override @NonNull
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        view = inflater.inflate(R.layout. dialog_filter, null);

        view.findViewById(R.id.tv_startDate).setOnClickListener(v -> {
            isStartDate = true;
            showDatePicker();
        });

        view.findViewById(R.id.tv_endDate).setOnClickListener(v -> {
            isStartDate = false;
            showDatePicker();
        });

        LayoutUtils.setAutoCategory(view.findViewById(R.id.atv_category));

        return new AlertDialog.Builder(getActivity())
                .setView(view)
                .setPositiveButton("Filter", (dialogInterface, which) -> handleOnClickFilter())
                .setNegativeButton("Cancel", (dialogInterface, which) -> dismiss())
                .create();
    }
}
