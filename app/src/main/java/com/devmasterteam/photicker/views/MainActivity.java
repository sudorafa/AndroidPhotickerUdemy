package com.devmasterteam.photicker.views;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.devmasterteam.photicker.R;
import com.devmasterteam.photicker.utils.PermissionUtil;

import com.devmasterteam.photicker.utils.LongEventType;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.devmasterteam.photicker.utils.ImageUtil;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener, View.OnTouchListener {

    private static final int REQUEST_TAKE_PHOTO = 2;
    private final ViewHolder mViewHolder = new ViewHolder();
    private Handler mRepeatUpdateHandler = new Handler();

    private ImageView mImageSelected;
    private boolean mAutoIncrement = false;
    private LongEventType mLongEventType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //se não colocar isso dar erro,
        //https://stackoverflow.com/questions/42251634/android-os-fileuriexposedexception-file-jpg-exposed-beyond-app-through-clipdata/45569709#45569709
        
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);

        List<Integer> mListImages = ImageUtil.getImageList();

        this.mViewHolder.mRelativePhotoContent = (RelativeLayout) this.findViewById(R.id.relative_photo_content_draw);
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

            image.setOnClickListener(onClickImageOption(this.mViewHolder.mRelativePhotoContent, imageId, width, height));

            content.addView(image);
        }

        this.mViewHolder.mLinearSharePanel = (LinearLayout) this.findViewById(R.id.linear_share_panel);
        this.mViewHolder.mLinearControlPanel = (LinearLayout) this.findViewById(R.id.linear_control_panel);

        this.mViewHolder.mButtonZoomIn = (ImageView) this.findViewById(R.id.image_zoom_in);
        this.mViewHolder.mButtonZoomOut = (ImageView) this.findViewById(R.id.image_zoom_out);
        this.mViewHolder.mButtonRotateLeft = (ImageView) this.findViewById(R.id.image_rotate_left);
        this.mViewHolder.mButtonRotateRight = (ImageView) this.findViewById(R.id.image_rotate_right);
        this.mViewHolder.mImageFinish = (ImageView) this.findViewById(R.id.image_finish);
        this.mViewHolder.mImageRemove = (ImageView) this.findViewById(R.id.image_remove);

        this.setListeners();

    }

    private void setListeners() {
        this.findViewById(R.id.image_take_photo).setOnClickListener(this);
        this.findViewById(R.id.image_zoom_in).setOnClickListener(this);
        this.findViewById(R.id.image_zoom_out).setOnClickListener(this);
        this.findViewById(R.id.image_rotate_left).setOnClickListener(this);
        this.findViewById(R.id.image_rotate_right).setOnClickListener(this);
        this.findViewById(R.id.image_finish).setOnClickListener(this);
        this.findViewById(R.id.image_remove).setOnClickListener(this);

        this.findViewById(R.id.image_zoom_in).setOnLongClickListener(this);
        this.findViewById(R.id.image_zoom_out).setOnLongClickListener(this);
        this.findViewById(R.id.image_rotate_left).setOnLongClickListener(this);
        this.findViewById(R.id.image_rotate_right).setOnLongClickListener(this);

        this.findViewById(R.id.image_zoom_in).setOnTouchListener(this);
        this.findViewById(R.id.image_zoom_out).setOnTouchListener(this);
        this.findViewById(R.id.image_rotate_left).setOnTouchListener(this);
        this.findViewById(R.id.image_rotate_right).setOnTouchListener(this);
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

                toogleControlPanel(true);

                mImageSelected = image;

                image.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {

                        float x, y;
                        switch (motionEvent.getAction()) {
                            case MotionEvent.ACTION_DOWN:
                                mImageSelected = image;
                                toogleControlPanel(true);
                                break;
                            case MotionEvent.ACTION_MOVE:

                                int coords[] = {0, 0};
                                relativeLayout.getLocationOnScreen(coords);

                                x = (motionEvent.getRawX() - (image.getWidth() / 2));
                                y = motionEvent.getRawY() - ((coords[1] + 100) + (image.getHeight() / 2));
                                image.setX(x);
                                image.setY(y);

                                break;
                            case MotionEvent.ACTION_UP:
                                break;
                        }
                        return true;
                    }
                });
            }
        };
    }

    private void toogleControlPanel(boolean showControls) {
        if (showControls) {
            this.mViewHolder.mLinearControlPanel.setVisibility(View.VISIBLE);
            this.mViewHolder.mLinearSharePanel.setVisibility(View.GONE);
        } else {
            this.mViewHolder.mLinearControlPanel.setVisibility(View.GONE);
            this.mViewHolder.mLinearSharePanel.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_take_photo:
                if (!PermissionUtil.hasCameraPermission(this)) {
                    PermissionUtil.asksCameraPermission(this);
                } else {
                    dispatchTakePictureIntent();
                }

                break;
            case R.id.image_zoom_in:
                ImageUtil.handleZoomIn(this.mImageSelected);
                break;

            case R.id.image_zoom_out:
                ImageUtil.handleZoomOut(this.mImageSelected);
                break;

            case R.id.image_rotate_left:
                ImageUtil.handleRotateLeft(this.mImageSelected);
                break;

            case R.id.image_rotate_right:
                ImageUtil.handleRotateRight(this.mImageSelected);
                break;

            case R.id.image_finish:
                toogleControlPanel(false);
                break;

            case R.id.image_remove:
                this.mViewHolder.mRelativePhotoContent.removeView(this.mImageSelected);
                toogleControlPanel(false);
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PermissionUtil.CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                dispatchTakePictureIntent();
            } else {
                new AlertDialog.Builder(this)
                        .setMessage(getString(R.string.without_permission_camera_explanation))
                        .setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        }).show();
            }
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Certifica que a Activity da camera existe e consegue responder
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {

            // Cria o arquivo onde a foto será salva
            File photoFile = null;
            try {
                photoFile = ImageUtil.createImageFile(this);
                // Save a file: path for use with ACTION_VIEW intents
                this.mViewHolder.mUriPhotoPath = Uri.fromFile(photoFile);
            } catch (IOException ex) {

            }

            // Continua somente se teve sucesso na criação do arquivo
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    @Override
    public boolean onLongClick(View v) {
        if (v.getId() == R.id.image_zoom_in) {
            mAutoIncrement = true;
            this.mLongEventType = LongEventType.ZoomIn;
            new RptUpdater().run();
        } else if (v.getId() == R.id.image_zoom_out) {
            mAutoIncrement = true;
            this.mLongEventType = LongEventType.ZoomOut;
            new RptUpdater().run();
        } else if (v.getId() == R.id.image_rotate_left) {
            mAutoIncrement = true;
            this.mLongEventType = LongEventType.RotateLeft;
            new RptUpdater().run();
        } else if (v.getId() == R.id.image_rotate_right) {
            mAutoIncrement = true;
            this.mLongEventType = LongEventType.RotateRight;
            new RptUpdater().run();
        }

        return false;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int id = v.getId();
        if (id == R.id.image_zoom_in || id == R.id.image_zoom_out || id == R.id.image_rotate_left || id == R.id.image_rotate_right) {

            if (event.getAction() == MotionEvent.ACTION_UP && mAutoIncrement) {
                mAutoIncrement = false;
                this.mLongEventType = null;
            }
        }
        return false;
    }

    private static class ViewHolder {

        ImageView mButtonZoomIn;
        ImageView mButtonZoomOut;
        ImageView mButtonRotateLeft;
        ImageView mButtonRotateRight;
        ImageView mImageFinish;
        ImageView mImageRemove;

        LinearLayout mLinearSharePanel;
        LinearLayout mLinearControlPanel;
        RelativeLayout mRelativePhotoContent;
        public Uri mUriPhotoPath;
    }

    private class RptUpdater implements Runnable {
        public void run() {

            // Se o usuário ainda estiver pressionando o botão
            if (mAutoIncrement)
                mRepeatUpdateHandler.postDelayed(new RptUpdater(), 50);

            // Verifica o tipo de evento e toma a ação
            if (mLongEventType != null) {
                switch (mLongEventType) {
                    case ZoomIn:
                        ImageUtil.handleZoomIn(mImageSelected);
                        break;
                    case ZoomOut:
                        ImageUtil.handleZoomOut(mImageSelected);
                        break;
                    case RotateLeft:
                        ImageUtil.handleRotateLeft(mImageSelected);
                        break;
                    case RotateRight:
                        ImageUtil.handleRotateRight(mImageSelected);
                        break;
                }
            }
        }
    }
}
