package com.example.chen.hsms.activity.home_ohter;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.chen.hsms.R;
import com.example.chen.hsms.base.BaseActivity;
import com.example.chen.hsms.bean.local.ImageText;
import com.example.chen.hsms.utils.BarUtils;
import com.example.chen.hsms.utils.Constant;
import com.example.chen.hsms.utils.ImageUtils;
import com.example.chen.hsms.utils.NetUtils;
import com.example.chen.hsms.utils.ToastUtils;
import com.example.chen.hsms.view.RecyclerViewDivider;
import com.example.chen.hsms.view.ShowDialog;
import com.example.chen.hsms.webserviceutils.WebServiceUtils;
import com.example.chen.hsms.ws.Hsms;
import com.example.chen.hsms.ws.WSOpraTypeCode;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class SearchImageAct extends BaseActivity {

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_right)
    TextView tvRight;
    @BindView(R.id.tv_serach)
    TextView tvSerach;

    @BindView(R.id.rcy_image)
    RecyclerView rcyImage;
    @BindView(R.id.ll_srkuang)
    LinearLayout llSrkuang;
    private String tiaoma = "";
    private String canshu = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public int setLayoutId() {
        return R.layout.activity_search_image;
    }

    @Override
    protected void initView() {
        BarUtils.setColorForSwipeBack(this, ContextCompat.getColor(mContext, R.color.state), 0);
        llSrkuang.setVisibility(View.GONE);
        tvTitle.setText(getResources().getText(R.string.wpbimage));
    }

    @Override
    public void initDatas() {
        try {
            Intent intent = getIntent();
            tiaoma = intent.getStringExtra("tiaoma");
            dialog.show();
            cachedThreadPool.execute(new Runnable() {
                @Override
                public void run() {
                    wpbaoImage(tiaoma);// 清洗机 照片
                }
            });
        } catch (Exception e) {
            log.e(e.getMessage());
        }

    }

    @Override
    public void initListeners() {
    }

    @OnClick(R.id.iv_back)
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
        }
    }

    /**
     * 取灭菌锅照片
     *
     * @param id
     */
    private void wpbaoImage(final String id) {
        HashMap<String, Object> params = new HashMap<>();
        String s = Constant.STRING + "'" + id + "'";
        params.put(Constant.CODE, WSOpraTypeCode.获取物品追踪记录照片);
        params.put(Constant.PARAMETER, s);
        log.e("获取物品追踪记录照片--" + s);
        WebServiceUtils.callWebService(dialog, NetUtils.isNetAvailable(mContext), params, new WebServiceUtils.WebServiceCallBack() {
            @Override
            public void callBack(Hsms hsms) {
                dismissLoading();
                if (hsms != null) {
                    log.e(hsms.getResturnMsg() + "--" + hsms.getReturnID() + "--" + hsms.getReturnCode() + "--" + hsms.getReturnJson());
                    if (hsms.getReturnCode() == 0) {
                        String result = hsms.getResturnMsg();
                        list_image.clear();
                        list_image.add(new ImageText(tiaoma, result));
                        setAdapter();
                    } else {
                        dismissLoading();
                        ShowDialog.setShowErro(mContext, hsms.getResturnMsg() + "--" + hsms.getReturnID() + "--" + hsms.getReturnCode() + "--" + hsms.getReturnJson());
                        ToastUtils.showLong("未能找到照片" + WSOpraTypeCode.获取物品追踪记录照片);
                    }
                } else {
                    ToastUtils.showLong(Constant.HSMS+ WSOpraTypeCode.获取物品追踪记录照片);
                    log.e(Constant.HSMS);
                }
            }
        });
    }

    ;

    private List<ImageText> list_image = new ArrayList<>();

    private void setAdapter() {
        rcyImage.setLayoutManager(new LinearLayoutManager(this));
        rcyImage.addItemDecoration(new RecyclerViewDivider(mContext, LinearLayoutManager.HORIZONTAL, (int) getResources().getDimension(R.dimen.x1), ContextCompat.getColor(mContext, R.color.item_view)));
        rcyImage.setAdapter(new CommonAdapter<ImageText>(mContext, R.layout.item_imagetext_rvy, list_image) {
            @Override
            protected void convert(ViewHolder holder, final ImageText imageText, int position) {
                Bitmap bitmap = ImageUtils.stringtoBitmap(imageText.getImage());
                holder.setImageBitmap(R.id.image, bitmap);
                holder.setText(R.id.text, imageText.getText());
                holder.setOnClickListener(R.id.ll_imagetext, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mContext, PhotoViewActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("imagetext", imageText);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                });
            }
        });
    }
}
