package oms.mmc.fortunetelling.fate.lib.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.smartydroid.android.starter.kit.app.StarterActivity;
import com.smartydroid.android.starter.kit.utilities.ViewUtils;
import com.umeng.analytics.MobclickAgent;

import oms.mmc.fortunetelling.fate.lib.Constant;
import oms.mmc.fortunetelling.fate.lib.R;

public class ActionBreakIndexActivity extends StarterActivity {

    private RecyclerView mRecyclerView;
    private String data [];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_action_break_index);
    }

    @Override
    protected void setupViews() {
        mRecyclerView = ViewUtils.getView(this, R.id.action_index_recycle_view);
        mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);

        initData();

        mRecyclerView.setAdapter(new ActionBreakAdapt());
    }

    private void initData() {
        data = getResources().getStringArray(R.array.action_name);
    }


    class ActionBreakAdapt extends RecyclerView.Adapter<ActionBreakViewHolder>{

        @Override
        public ActionBreakViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ActionBreakViewHolder(
                    LayoutInflater.from(mContext).inflate(R.layout.item_action_break_view,null));
        }

        @Override
        public void onBindViewHolder(ActionBreakViewHolder holder, final int position) {
            holder.textView.setText(data[position]);
            holder.actionbreakContain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, ActionBreakActivity.class);
                    intent.putExtra(Constant.EXTRA_TAG_ACTION_INTENT,position);
                    startActivity(intent);
                    umStatistics(position);
                }
            });
        }

        @Override
        public int getItemCount() {
            return data.length;
        }
    }

    class ActionBreakViewHolder extends RecyclerView.ViewHolder{
        public TextView textView;
        private View actionbreakContain;
        public ActionBreakViewHolder(View itemView) {
            super(itemView);
            textView = ViewUtils.getView(itemView, R.id.action_break_text_view);
            actionbreakContain = ViewUtils.getView(itemView, R.id.action_break_contain);
        }
    }

    @Override
    public void onCreateCustomToolBar(Toolbar toolbar) {
        super.onCreateCustomToolBar(toolbar);
        toolbar.setTitle(R.string.activity_name_movement_break);
    }

    /**
     * 友盟统计
     */
    private void umStatistics(int index ){
        String eventId = "action_break_";
        MobclickAgent.onEvent(this, eventId+index);
    }
}
