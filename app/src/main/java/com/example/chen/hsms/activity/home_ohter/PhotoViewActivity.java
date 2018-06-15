package com.example.chen.hsms.activity.home_ohter;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.chen.hsms.R;
import com.example.chen.hsms.base.BaseActivity;
import com.example.chen.hsms.bean.local.ImageText;
import com.example.chen.hsms.utils.ImageUtils;

import butterknife.BindView;
import butterknife.OnClick;
import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

public class PhotoViewActivity extends BaseActivity{
    public static final String TAG = PhotoViewActivity.class.getSimpleName();
    @BindView(R.id.iv_photo_back)
    ImageView ivPhotoBack;
    @BindView(R.id.photoView)
    PhotoView photoView;
    @BindView(R.id.tv_phoText)
    TextView tvPhoText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//通知栏隐藏掉
    }

    @Override
    public int setLayoutId() {
        return R.layout.activity_photo_view;
    }

    @Override
    protected void initView() {

    }

    @Override
    public void initDatas() {
        try {
            Intent intent = getIntent();
            Bundle bundle = intent.getExtras();
            ImageText imagetext = (ImageText) bundle.getSerializable("imagetext");
            Bitmap bitmap = ImageUtils.stringtoBitmap(imagetext.getImage());
            photoView.setImageBitmap(bitmap);
            tvPhoText.setText(imagetext.getText());
        } catch (Exception e) {
            e.getMessage();
        }
    }

    @Override
    public void initListeners() {
        photoView.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
            @Override
            public void onPhotoTap(View view, float v, float v1) {
                finish();
            }

            @Override
            public void onOutsidePhotoTap() {
                log.e("222");
            }
        });
    }


    @OnClick({R.id.iv_photo_back, R.id.photoView})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_photo_back:
                finish();
                break;
        }
    }
}
