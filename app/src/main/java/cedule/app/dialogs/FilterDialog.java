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

    private void filter() {
        Bundle resultBundle = new Bundle();

        String category = ((AutoCompleteTextView) view.findViewById(R.id.atv_category)).getText().toString();

        if (category != null && category.length() > 0) {
            resultBundle.putString("category", category);
        }

        if (startDate != null && endDate != null) {
            resultBundle.putLong("startDate", startDate);
            resultBundle.putLong("endDate", endDate);
        }
        getParentFragmentManager().setFragmentResult("filterTask", resultBundle);
    }

    private void showDatePicker() {
        new DatePickerDialog(
                requireActivity(),
                (datePicker, year, month, dayOfMonth) -> {
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(year, month, dayOfMonth);

                    TimeUtils.setMidNight(calendar);

                    if (isStartDate) {
                        startDate = calendar.getTimeInMillis();

                        ((TextView) view.findViewById(R.id.tv_startDate))
                                .setText(TimeUtils.toDateString(startDate));
                    }
                    else {
                        endDate = calendar.getTimeInMillis();
                        ((TextView) view.findViewById(R.id.tv_endDate))
                                .setText(TimeUtils.toDateString(endDate));
                    }

                },
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH)).show();
    }

    @Override @NonNull
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        LayoutInflater inflater = getActivity().getLayoutInflater();
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
                .setPositiveButton("Filter", (dialogInterface, which) -> filter())
                .setNegativeButton("Cancel", (dialogInterface, which) -> dismiss())
                .create();
    }
}
