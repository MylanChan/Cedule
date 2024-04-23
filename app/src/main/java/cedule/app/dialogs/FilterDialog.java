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


public class FilterDialog extends DialogFragment {
    private View view;

    private Long startDate;
    private Long endDate;

    private boolean isStartDate = false;


    private void showDateTimePicker() {
        new DatePickerDialog(
                requireActivity(),
                (datePicker, year, month, dayOfMonth) -> {
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, month);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                    calendar.set(Calendar.HOUR_OF_DAY, 0);
                    calendar.set(Calendar.MINUTE, 0);
                    calendar.set(Calendar.SECOND, 0);
                    calendar.set(Calendar.MILLISECOND, 0);

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
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        view = inflater.inflate(R.layout. dialog_filter, null);

        view.findViewById(R.id.tv_startDate).setOnClickListener(v -> {
            isStartDate = true;
            showDateTimePicker();
        });

        view.findViewById(R.id.tv_endDate).setOnClickListener(v -> {
            isStartDate = false;
            showDateTimePicker();
        });

        builder.setView(view);

        builder.setPositiveButton("Filter", (dialog, which) -> {
            Bundle resultBundle = new Bundle();

            String category = ((AutoCompleteTextView) view.findViewById(R.id.tv_category_desc)).getText().toString();

            if (category != null && category.length() > 0) {
                System.out.println("Get category " + category);
                resultBundle.putString("category", category);
            }

            if (startDate != null && endDate != null) {
                resultBundle.putLong("startDate", startDate);
                resultBundle.putLong("endDate", endDate);
            }
            getParentFragmentManager().setFragmentResult("filterTask", resultBundle);
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dismiss());

        AlertDialog dialog = builder.create();

        new Thread(() -> {
            AutoCompleteTextView atv_category = view.findViewById(R.id.tv_category_desc);

            Category[] categories = MainActivity.getDatabase().categoryDAO().getAll();
            String[] categoryNames = new String[categories.length];

            for (int i=0; i < categories.length; i++) {
                categoryNames[i] = categories[i].name;
            }

            getActivity().runOnUiThread(() -> {
                ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_dropdown_item_1line, categoryNames);
                atv_category.setAdapter(adapter);
            });
        }).start();

        return dialog;
    }
}
