package cedule.app.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import android.graphics.Color;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.TextView;

import cedule.app.R;
import cedule.app.utils.LayoutUtils;

public class DocumentActivity extends AppCompatActivity {
    public final static int TYPE_STORY = 0;
    public final static int TYPE_ABOUT = 1;

    private void loadHtmlByType(int type) {
        WebView wvDoc = findViewById(R.id.wv_doc);
        wvDoc.setBackgroundColor(Color.TRANSPARENT);
        wvDoc.getSettings().setDefaultFontSize(11);

        switch (type) {
            case TYPE_STORY: {
                wvDoc.loadUrl("file:///android_asset/docs/story_behind.html");
                ((TextView) findViewById(R.id.tv_title)).setText("Story Behind");
                return;
            }
            case TYPE_ABOUT: {
                wvDoc.loadUrl("file:///android_asset/docs/about_us.html");
                ((TextView) findViewById(R.id.tv_title)).setText("About Us");
                return;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document);

        if (getIntent() == null || getIntent().getExtras() == null) return;

        int type = getIntent().getExtras().getInt("type", -1);
        loadHtmlByType(type);

        findViewById(R.id.ib_exit).setOnClickListener(v -> finish());

        getWindow().setNavigationBarColor(getResources().getColor(R.color.surface));
        LayoutUtils.setBarColor(getWindow());
    }
}