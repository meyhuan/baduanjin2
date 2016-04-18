package oms.mmc.fortunetelling.fate.lib.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.smartydroid.android.starter.kit.app.StarterActivity;
import com.smartydroid.android.starter.kit.utilities.ViewUtils;

import oms.mmc.fortunetelling.fate.lib.Constant;
import oms.mmc.fortunetelling.fate.lib.R;

public class ActionBreakActivity extends StarterActivity {

    LinearLayout actionLayout;
    private static int ACTION = 0;

    //标题
    private int TYPE_1 = 1;
    //正文
    private int TYPE_2 = 2;
    //图片
    private int TYPE_3 = 3;

    private int[][] content_action_0 = {
            {TYPE_1, R.string.action_0_0},
            {TYPE_2, R.string.action_0_1},
            {TYPE_1, R.string.action_0_2},
            {TYPE_2, R.string.action_0_3},
            {TYPE_3, R.drawable.action_0_1},
            {TYPE_3, R.drawable.action_0_2},
            {TYPE_3, R.drawable.action_0_3},
            {TYPE_3, R.drawable.action_0_4},
    };


    private int[][] content_action_1 = {
            {TYPE_1, R.string.action_1_0},
            {TYPE_2, R.string.action_1_1},
            {TYPE_3, R.drawable.action_1_1},
            {TYPE_3, R.drawable.action_1_2},
            {TYPE_3, R.drawable.action_1_3},
            {TYPE_2, R.string.action_1_2},
            {TYPE_3, R.drawable.action_1_4},
            {TYPE_3, R.drawable.action_1_5},
            {TYPE_3, R.drawable.action_1_6},
            {TYPE_3, R.drawable.action_1_1},
            {TYPE_3, R.drawable.action_1_1},
            {TYPE_2, R.string.action_1_3},
    };

    private int[][] content_action_2 = {
            {TYPE_1, R.string.action_2_0},
            {TYPE_2, R.string.action_2_1},
            {TYPE_3, R.drawable.action_2_1},
            {TYPE_3, R.drawable.action_2_3},
            {TYPE_2, R.string.action_2_2},
            {TYPE_3, R.drawable.action_2_6},
            {TYPE_3, R.drawable.action_2_7},
            {TYPE_2, R.string.action_2_3},
            {TYPE_3, R.drawable.action_2_4},
            {TYPE_3, R.drawable.action_2_2},
            {TYPE_3, R.drawable.action_0_4},
            {TYPE_2, R.string.action_2_4},
    };

    private int[][] content_action_3 = {
            {TYPE_1, R.string.action_3_0},
            {TYPE_2, R.string.action_3_1},
            {TYPE_3, R.drawable.action_3_1},
            {TYPE_3, R.drawable.action_3_2},
            {TYPE_3, R.drawable.action_3_3},
            {TYPE_2, R.string.action_3_2},
            {TYPE_3, R.drawable.action_3_4},
            {TYPE_3, R.drawable.action_3_1},
            {TYPE_2, R.string.action_3_3},
            {TYPE_3, R.drawable.action_3_5},
            {TYPE_3, R.drawable.action_3_6},
            {TYPE_3, R.drawable.action_0_4},
            {TYPE_2, R.string.action_3_4},
    };

    private int[][] content_action_4 = {
            {TYPE_1, R.string.action_4_0},
            {TYPE_2, R.string.action_4_1},
            {TYPE_3, R.drawable.action_3_1},
            {TYPE_3, R.drawable.action_4_1},
            {TYPE_3, R.drawable.action_4_3},
            {TYPE_2, R.string.action_4_2},
            {TYPE_3, R.drawable.action_4_2},
            {TYPE_3, R.drawable.action_3_1},
            {TYPE_2, R.string.action_3_3},
            {TYPE_3, R.drawable.action_3_1},
            {TYPE_2, R.string.action_4_3},
    };

    private int[][] content_action_5 = {
            {TYPE_1, R.string.action_5_0},
            {TYPE_2, R.string.action_5_1},
            {TYPE_3, R.drawable.action_5_5},
            {TYPE_3, R.drawable.action_5_1},
            {TYPE_3, R.drawable.action_5_2},
            {TYPE_3, R.drawable.action_5_3},
            {TYPE_3, R.drawable.action_5_4},
            {TYPE_2, R.string.action_5_2},
            {TYPE_3, R.drawable.action_5_3},
            {TYPE_3, R.drawable.action_5_4},
            {TYPE_2, R.string.action_5_3},
    };

    private int[][] content_action_6 = {
            {TYPE_1, R.string.action_6_0},
            {TYPE_2, R.string.action_6_1},
            {TYPE_3, R.drawable.action_6_1},
            {TYPE_2, R.string.action_6_2},
            {TYPE_3, R.drawable.action_6_2},
            {TYPE_3, R.drawable.action_6_3},
            {TYPE_3, R.drawable.action_6_4},
            {TYPE_2, R.string.action_6_3},
            {TYPE_3, R.drawable.action_6_5},
            {TYPE_2, R.string.action_6_4},
    };

    private int[][] content_action_7 = {
            {TYPE_1, R.string.action_7_0},
            {TYPE_2, R.string.action_7_1},
            {TYPE_3, R.drawable.action_7_1},
            {TYPE_2, R.string.action_7_2},
            {TYPE_3, R.drawable.action_7_2},
            {TYPE_3, R.drawable.action_7_3},
            {TYPE_2, R.string.action_7_3},
            {TYPE_3, R.drawable.action_7_2},
            {TYPE_3, R.drawable.action_7_5},
            {TYPE_2, R.string.action_7_4},
    };

    private int[][] content_action_8 = {
            {TYPE_1, R.string.action_8_0},
            {TYPE_2, R.string.action_8_1},
            {TYPE_3, R.drawable.action_0_1},
            {TYPE_2, R.string.action_8_2},
            {TYPE_3, R.drawable.action_8_1},
            {TYPE_3, R.drawable.action_8_2},
            {TYPE_2, R.string.action_8_3},
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_action_break);

    }

    @Override
    protected void setupViews() {
        actionLayout = ViewUtils.getView(this, R.id.action_break_layout);
        ACTION = getIntent().getIntExtra(Constant.EXTRA_TAG_ACTION_INTENT, 0);
        switch (ACTION) {
            case Constant.EXTRA_TAG_ACTION_0:
                initView(content_action_0);
                break;
            case Constant.EXTRA_TAG_ACTION_1:
                initView(content_action_1);
                break;
            case Constant.EXTRA_TAG_ACTION_2:
                initView(content_action_2);
                break;
            case Constant.EXTRA_TAG_ACTION_3:
                initView(content_action_3);
                break;
            case Constant.EXTRA_TAG_ACTION_4:
                initView(content_action_4);
                break;
            case Constant.EXTRA_TAG_ACTION_5:
                initView(content_action_5);
                break;
            case Constant.EXTRA_TAG_ACTION_6:
                initView(content_action_6);
                break;
            case Constant.EXTRA_TAG_ACTION_7:
                initView(content_action_7);
                break;
            case Constant.EXTRA_TAG_ACTION_8:
                initView(content_action_8);
                break;
            default:
                initView(content_action_0);
        }
    }

    private void initView(int[][] content_action) {
        for(int i = 0; i < content_action.length; i++) {
            if (content_action[i][0] == TYPE_1) {
                TextView textView = (TextView) LayoutInflater.from(this).inflate(R.layout.text_view_action_break_title, null);
                textView.setText(content_action[i][1]);
                actionLayout.addView(textView);
            } else if (content_action[i][0] == TYPE_2) {
                TextView textView = (TextView) LayoutInflater.from(this).inflate(R.layout.text_view_action_break, null);
                textView.setText(content_action[i][1]);
                actionLayout.addView(textView);
            } else if (content_action[i][0] == TYPE_3) {
                ImageView imageView = (ImageView) LayoutInflater.from(this).inflate(R.layout.image_view_action_break, null);
                imageView.setImageResource(content_action[i][1]);
                actionLayout.addView(imageView);
            }
        }
    }

    @Override
    public void onCreateCustomToolBar(Toolbar toolbar) {
        super.onCreateCustomToolBar(toolbar);
        toolbar.setTitle(R.string.activity_name_movement_break);
    }
}
