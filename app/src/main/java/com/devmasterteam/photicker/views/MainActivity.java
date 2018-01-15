package com.devmasterteam.photicker.views;

import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.devmasterteam.photicker.R;

import java.util.List;

import utils.ImageUtil;

public class MainActivity extends AppCompatActivity {

    private final ViewHolder mViewHolder = new ViewHolder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);

        List<Integer> mListImages = ImageUtil.getImageList();

        final RelativeLayout relativeLayout = (RelativeLayout) this.findViewById(R.id.relative_photo_content_draw);
        final LinearLayout content = (LinearLayout) this.findViewById(R.id.linear_horizontal_scroll);

        for (Integer imageId : mListImages) {
            ImageView image = new ImageView(this);
            image.setImageBitmap(ImageUtil.decodeSampledBitmapFromResource(getResources(), imageId, 70, 70));
            image.setPadding(20, 10, 20, 10);

            BitmapFactory.Options dimensions = new BitmapFactory.Options();
            dimensions.inJustDecodeBounds = true;
            BitmapFactory.decodeResource(getResources(), imageId, dimensions);

            final int width = dimensions.outWidth;
            final int height = dimensions.outHeight;

            image.setOnClickListener(onClickImageOption(relativeLayout, imageId, width, height));

            content.addView(image);
        }

        this.mViewHolder.mLinearSharePanel = (LinearLayout) this.findViewById(R.id.linear_share_panel);
        this.mViewHolder.mLinearControlPanel = (LinearLayout) this.findViewById(R.id.linear_control_panel);

    }

    private View.OnClickListener onClickImageOption(final RelativeLayout relativeLayout, final Integer imageId, final int width, final int height) {
        return new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                final ImageView image = new ImageView(MainActivity.this);
                image.setBackgroundResource(imageId);
                relativeLayout.addView(image);

                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) image.getLayoutParams();
                layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
                layoutParams.addRule(RelativeLayout.CENTER_VERTICAL);

                toobleControlePanel(true);
            }
        };
    }

    private void toobleControlePanel(boolean showControls) {
        if (showControls) {
            this.mViewHolder.mLinearControlPanel.setVisibility(View.VISIBLE);
            this.mViewHolder.mLinearSharePanel.setVisibility(View.GONE);
        }else{
            this.mViewHolder.mLinearControlPanel.setVisibility(View.GONE);
            this.mViewHolder.mLinearSharePanel.setVisibility(View.VISIBLE);
        }
    }

    private static class ViewHolder {

        LinearLayout mLinearSharePanel;
        LinearLayout mLinearControlPanel;
    }
}
