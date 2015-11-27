package com.phone.ignore.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import com.phone.ignore.R;
import com.phone.ignore.ScheduleMenuDialog;
import com.phone.ignore.constants.BlockNumber;
import com.phone.ignore.constants.Schedule;

import java.util.ArrayList;

public class BlockedNumberAdapter extends ArrayAdapter<BlockNumber> {

    Context context;
    private boolean[] isCheckedConfrim;

    public BlockedNumberAdapter(Context context, int resource) {
        super(context, resource);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;
        ViewHolder holder;

        if(v == null){
            holder =  new ViewHolder();
            v = View.inflate(context, R.layout.item_ignore, null);

            holder.tvPhoneNum = (TextView)v.findViewById(R.id.tv_ignore_num);
            holder.tvRemark= (TextView)v.findViewById(R.id.tv_ignore_time);
            holder.cbDelete = (CheckBox)v.findViewById(R.id.cb_item_delete);

            v.setTag(holder);
            isCheckedConfrim = new boolean[getCount()];
        }else{
            holder = (ViewHolder)v.getTag();
        }

        holder.tvRemark.setVisibility(getItem(position).strRemark.equals("") ? View.GONE : View.VISIBLE);

        holder.tvPhoneNum.setText(getItem(position).strPhoneNum);
        holder.tvRemark.setText(getItem(position).strRemark);
        holder.cbDelete.setChecked(isCheckedConfrim[position]);

        return v;
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
        public TextView tvPhoneNum;
        public TextView tvRemark;
        public CheckBox cbDelete;
    }
}
