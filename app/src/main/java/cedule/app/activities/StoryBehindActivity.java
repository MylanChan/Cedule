package cedule.app.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.graphics.Color;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.TextView;

import cedule.app.R;
import cedule.app.adapters.SlideAdapter;
import cedule.app.utils.LayoutUtils;

public class StoryBehindActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story_behind);

        getWindow().setNavigationBarColor(getResources().getColor(R.color.surface));
        LayoutUtils.setBarColor(getWindow());

        findViewById(R.id.ib_exit).setOnClickListener(v -> finish());

        WebView wvDoc = findViewById(R.id.wv_doc);
        wvDoc.setBackgroundColor(Color.TRANSPARENT);
        wvDoc.getSettings().setDefaultFontSize(11);

        wvDoc.loadUrl("file:///android_asset/docs/story_behind.html");

        ViewPager2 vpImgSlider = findViewById(R.id.vp_image_slider);
        vpImgSlider.setAdapter(new SlideAdapter(this));

        vpImgSlider.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);

                TextView tvPage = findViewById(R.id.tv_page);
                tvPage.setText((position+1) + "/2");
            }
        });
    }
}