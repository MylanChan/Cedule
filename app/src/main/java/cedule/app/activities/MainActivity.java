package cedule.app.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import cedule.app.R;
import cedule.app.fragments.CalendarFragment;
import cedule.app.fragments.TaskFragment;
import cedule.app.fragments.WidgetsFragment;

public class MainActivity extends AppCompatActivity {
    private void switchFragment(View view, Class<? extends Fragment> destFragment) {


        for (int id : new int[] {R.id.iv_task, R.id.iv_calendar, R.id.iv_widgets}) {
            ImageView navItem = findViewById(id);

            if (view.getId() == id) {
                int onPrimary = ContextCompat.getColor(getApplicationContext(), R.color.onPrimary);
                navItem.setImageTintList(ColorStateList.valueOf(onPrimary));
                continue;
            }

            int onPrimaryVariant = ContextCompat.getColor(getApplicationContext(), R.color.onPrimaryVariant);
            navItem.setImageTintList(ColorStateList.valueOf(onPrimaryVariant));
        }

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fr_app_main, destFragment, null)
                .commit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.iv_task)
                .setOnClickListener(v -> switchFragment(v, TaskFragment.class));

        findViewById(R.id.iv_calendar)
                .setOnClickListener(v -> switchFragment(v, CalendarFragment.class));

        findViewById(R.id.iv_widgets)
                .setOnClickListener(v -> switchFragment(v, WidgetsFragment.class));

        getSupportFragmentManager().beginTransaction()
                .add(R.id.fr_app_main, TaskFragment.class, null)
                .commit();
    }
}