package com.example.chen.hsms.activity.home_ohter;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.chen.hsms.R;
import com.example.chen.hsms.application.MyApplication;
import com.example.chen.hsms.base.BaseActivity;
import com.example.chen.hsms.bean.data.KeShiName;
import com.example.chen.hsms.bean.local.MessageEvent;
import com.example.chen.hsms.utils.BarUtils;
import com.example.chen.hsms.view.RecyclerViewDivider;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import org.greenrobot.eventbus.EventBus;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class KeShiActivity extends BaseActivity {

    @BindView(R.id.iv_keshi_back)
    ImageView ivKeshiBack;
    @BindView(R.id.rcy_keshi)
    RecyclerView rcyKeshi;
    @BindView(R.id.activity_ke_shi)
    LinearLayout activityKeShi;

    private List<KeShiName> list_keshi;
    private String tag = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        //各个页面跳转到科室页面
        //0--发放  1--发放汇总  2--请领查询
        tag = getIntent().getStringExtra("keshi");
    }

    @Override
    public int setLayoutId() {
        return R.layout.activity_ke_shi;
    }

    @Override
    protected void initView() {
        BarUtils.setColorForSwipeBack(this, ContextCompat.getColor(mContext, R.color.brlb), 0);
    }

    @Override
    public void initDatas() {
//        if (SaveUtils.contains(mContext, "6")) {
//            String ss = (String) SaveUtils.get(mContext, "6","");
//            list_keshi = JSON.parseArray(ss, KeShiName.class);
////            JsonArray array = new JsonParser().parse(json).getAsJsonArray();
////            for (final JsonElement elem : array) {
////                lst.add(new Gson().fromJson(elem, clazz));
////            }
//        }else {
            list_keshi = MyApplication.getInstance().getList_keshi();
//        }
        setAdapter();
    }

    //展示科室
    private void setAdapter() {
        rcyKeshi.setLayoutManager(new LinearLayoutManager(this));
        rcyKeshi.addItemDecoration(new RecyclerViewDivider(mContext, LinearLayoutManager.HORIZONTAL, (int) getResources().getDimension(R.dimen.x1), ContextCompat.getColor(mContext, R.color.item_view)));
        rcyKeshi.setAdapter(new CommonAdapter<KeShiName>(mContext, R.layout.item_keshi, list_keshi) {
            @Override
            protected void convert(ViewHolder holder, final KeShiName bean, final int position) {
                holder.setText(R.id.tv_keshiid,  bean.getID());
                holder.setText(R.id.tv_keshi, bean.getName());
                holder.setOnClickListener(R.id.fl_keshi, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (tag.equals("1")) {
                            EventBus.getDefault().post(new MessageEvent(bean.getID(), 1, bean.getName()));
                        } else if (tag.equals("2")) {
                            EventBus.getDefault().post(new MessageEvent(bean.getID(), 2, bean.getName()));
                        } else if(tag.equals("0")){
                            EventBus.getDefault().post(new MessageEvent(bean.getID(), 0, bean.getName()));
                        }
                        finish();
                    }
                });
            }
        });
    }


    @Override
    public void initListeners() {


    }

    @OnClick(R.id.iv_keshi_back)
    public void onViewClicked() {
        finish();
    }
}
