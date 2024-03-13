package cedule.app.dialogs;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import cedule.app.R;

public class ConfirmationDialog extends DialogFragment {
    private final String title;
    private final Function handleOnClickPosBtn;

    public interface Function {
        void run(DialogFragment dialog);
    }

    @Override @Nullable
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_confirmation, container);

        // avoid dialog has white corners background if it has radius
        Window window = requireDialog().getWindow();
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        ((TextView) view.findViewById(R.id.tv_title)).setText(title);

        view.findViewById(R.id.btn_pos).setOnClickListener(v -> handleOnClickPosBtn.run(this));
        view.findViewById(R.id.btn_neg).setOnClickListener(v -> dismiss());

        return view;
    }

    public ConfirmationDialog(String title, Function handOnClickPosBtn) {
        this.title = title;
        this.handleOnClickPosBtn = handOnClickPosBtn;
    }
}
