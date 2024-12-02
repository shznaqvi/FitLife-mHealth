package edu.aku.hassannaqvi.fitlife.adapters;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import edu.aku.hassannaqvi.fitlife.R;
import edu.aku.hassannaqvi.fitlife.databinding.SyncListAdapterBinding;
import edu.aku.hassannaqvi.fitlife.models.SyncModel;

import java.util.List;


public class SyncListAdapter extends RecyclerView.Adapter<SyncListAdapter.SyncListViewHolder> {
    private static final String TAG = "SyncListAdapter";
    List<SyncModel> synclist;
    SyncListViewHolder holder;

    public SyncListAdapter(List<SyncModel> synclist) {
        this.synclist = synclist;
        this.setHasStableIds(true);
    }

    public void updatesyncList(List<SyncModel> synclist) {
        this.synclist = synclist;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SyncListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.sync_list_adapter, parent, false);
        return new SyncListViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull SyncListViewHolder holder, int position) {
        this.holder = holder;
        this.holder.bindUser(this.synclist.get(position));
    }

    @Override
    public int getItemCount() {
        return Math.max(synclist.size(), 0);
    }

    private int checkStatus(int i) {
        switch (i) {
            case 1:
                return Color.RED;
            case 2:
                return Color.YELLOW;
            case 3:
                return Color.GREEN;
            case 4:
                return Color.GRAY;
            default:
                return Color.BLACK;
        }
    }

    public class SyncListViewHolder extends RecyclerView.ViewHolder {
        SyncListAdapterBinding binding;


        public SyncListViewHolder(View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);

        }

        public void bindUser(SyncModel model) {
            binding.statusColor.setBackgroundColor(checkStatus(model.getstatusID()));
            String tableName = model.getTableTitle().toUpperCase();

            binding.tvTableName.setText(tableName);
            binding.tvInfo.setText(model.getInfo());
            binding.tvStatus.setText(model.getstatus());
            Log.d(TAG, "bindUser: " + tableName + " - " + model.getmessage());
            if (tableName.contains("VERSION") && model.getmessage().contains("New"))
                binding.tvMsg.setTextColor(Color.RED);
            binding.tvMsg.setText(model.getmessage());
            if (model.getstatusID() == 1 || model.getstatusID() == 3 || model.getstatusID() == 4) {
                binding.pb.setVisibility(View.GONE);
            } else {
                binding.pb.setVisibility(View.VISIBLE);
            }
        }
    }
}
