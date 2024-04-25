package cedule.app.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.webkit.WebView;

import cedule.app.R;
import cedule.app.utils.LayoutUtils;

public class AboutUsActivity extends AppCompatActivity {
    private void loadHTML() {
        WebView wvDoc = findViewById(R.id.wv_doc);
        wvDoc.setBackgroundColor(Color.TRANSPARENT);
        wvDoc.getSettings().setDefaultFontSize(11);

        wvDoc.loadUrl("file:///android_asset/docs/about_us.html");
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

        getWindow().setNavigationBarColor(getResources().getColor(R.color.surface));
        LayoutUtils.setBarColor(getWindow());

        findViewById(R.id.ib_exit).setOnClickListener(v -> finish());

        loadHTML();
    }
}