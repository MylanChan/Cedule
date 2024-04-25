package cedule.app.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.TextView;

import cedule.app.R;
import cedule.app.utils.LayoutUtils;

public class AboutUsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

        WebView wvDoc = findViewById(R.id.wv_doc);
        wvDoc.setBackgroundColor(Color.TRANSPARENT);
        wvDoc.getSettings().setDefaultFontSize(11);

        wvDoc.loadUrl("file:///android_asset/docs/about_us.html");

        findViewById(R.id.ib_exit).setOnClickListener(v -> finish());

        getWindow().setNavigationBarColor(getResources().getColor(R.color.surface));
        LayoutUtils.setBarColor(getWindow());
    }
}