package anjuman.e.badri;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;

import com.github.chrisbanes.photoview.PhotoView;
import com.squareup.picasso.Picasso;

public class FullScreenActivity extends AppCompatActivity {

    public static final String KEY_URL = "URL";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen);

        ActionBar supportActionBar = getSupportActionBar();

        if (supportActionBar != null) {
            SpannableString s = new SpannableString("Zoom");
            s.setSpan(new TypefaceSpan(this, "calibri.ttf"), 0, s.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            supportActionBar.setTitle(s);
        }

        Intent intent = getIntent();
        String url = intent.getStringExtra(KEY_URL);
        if (!TextUtils.isEmpty(url)) {
            final PhotoView photoView = findViewById(R.id.iv_photo);
            Picasso.get()
                    .load(url)
                    .into(photoView);
        }

    }
}
