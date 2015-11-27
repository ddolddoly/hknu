package com.phone.ignore.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import com.phone.ignore.R;
import com.phone.ignore.ScheduleMenuDialog;
import com.phone.ignore.constants.Schedule;

import java.util.ArrayList;

public class ScheduleEditAdapter extends ArrayAdapter<Schedule> {

    Context context;
    int editMode = ScheduleMenuDialog.MODE_MODIFY;
    private boolean[] isCheckedConfrim;

    public ScheduleEditAdapter(Context context, int resource) {
        super(context, resource);
        this.context = context;
        int dataSize = getCount();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;
        ViewHolder holder;

        if(v == null){
            holder =  new ViewHolder();
            v = View.inflate(context, R.layout.item_schedule, null);

            holder.tvTitle = (TextView)v.findViewById(R.id.tv_schedule_title);
            holder.tvTime= (TextView)v.findViewById(R.id.tv_schedule_time);
            holder.cbDelete = (CheckBox)v.findViewById(R.id.cb_item_delete);

            v.setTag(holder);
            isCheckedConfrim = new boolean[getCount()];
        }else{
            holder = (ViewHolder)v.getTag();
        }

        holder.cbDelete.setVisibility(editMode == ScheduleMenuDialog.MODE_DELETE  ? View.VISIBLE : View.GONE);
        holder.tvTitle.setText(getItem(position).strTitle);
        holder.tvTime.setText(getItem(position).strDate + " " + getItem(position).strStartTime + " ~ " + getItem(position).strEndTime);
        holder.cbDelete.setChecked(isCheckedConfrim[position]);

        return v;
    }

    public void setEditMode(int mode){
        editMode = mode;
    }

    public void setAllChecked(boolean isChecked) {
        for(int i=0 ; i<isCheckedConfrim.length ; i++){
            isCheckedConfrim[i] =isChecked;
        }
    }

    public void setChecked(int position) {
        isCheckedConfrim[position] = !isCheckedConfrim[position];
    }

    public ArrayList<Integer> getCheckedItemPosition(){
        ArrayList<Integer> checkedArray = new ArrayList<Integer>();
        for(int i=0 ; i<isCheckedConfrim.length ; i++){
            if(isCheckedConfrim[i]){
                checkedArray.add(i);
            }
        }
        return checkedArray;
    }
    
    public class ViewHolder {
        public TextView tvTitle;
        public TextView tvTime;
        public CheckBox cbDelete;
    }
}
