package com.spraut.sprautnote.Image;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.spraut.sprautnote.R;

import java.io.File;

public class ImageActivity extends AppCompatActivity {
    public final static String EXTRA_IMAGE_URL = "image_url";

    private ImageView imageView;
    private ZoomImageView zoomImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        //适配MIUI，沉浸小横条和状态栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        //状态栏文字显示为黑色
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        /*window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);*/
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        String Url=getIntent().getStringExtra(EXTRA_IMAGE_URL);
        zoomImageView=findViewById(R.id.iv_zoom);
        zoomImageView.setTransitionName("image");
        zoomImageView.setImageURI(Uri.fromFile(new File(Url)));
    }
}