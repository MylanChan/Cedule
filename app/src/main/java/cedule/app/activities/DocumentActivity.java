package cedule.app.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.webkit.WebView;

import cedule.app.R;

public class DocumentActivity extends AppCompatActivity {
    public final static int TYPE_STORY = 0;
    public final static int TYPE_ABOUT = 1;

    private void loadHtmlByType(int type) {
        WebView wvDoc = findViewById(R.id.wv_doc);

        switch (type) {
            case TYPE_STORY: {
                wvDoc.loadUrl("file:///android_asset/docs/story_behind.html");
                return;
            }
            case TYPE_ABOUT: {
                wvDoc.loadUrl("file:///android_asset/docs/about_us.html");
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document);

        findViewById(R.id.btn_pos).setOnClickListener(v -> finish());

        if (getIntent() == null || getIntent().getExtras() == null) return;

        int type = getIntent().getExtras().getInt("type", -1);
        loadHtmlByType(type);
    }
}