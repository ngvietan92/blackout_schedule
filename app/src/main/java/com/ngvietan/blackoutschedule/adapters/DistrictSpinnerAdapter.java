package com.ngvietan.blackoutschedule.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ngvietan.blackoutschedule.R;
import com.ngvietan.blackoutschedule.models.District;

import java.util.List;

/**
 * District spinner adapter
 * Created by NgVietAn on 16/08/2015.
 */
public class DistrictSpinnerAdapter extends BaseAdapter {

    final int TYPE_DROPDOWN = 1;
    final int TYPE_VIEW = 2;

    private Context context;
    private List<District> districts;

    public DistrictSpinnerAdapter(Context context, List<District> districts) {
        this.context = context;
        this.districts = districts;
    }

    @Override
    public int getCount() {
        return districts.size();
    }

    @Override
    public Object getItem(int position) {
        return districts.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getDropDownView(int position, View view, ViewGroup parent) {
        ViewHolder viewHolder;

        if (view == null || view.getTag() == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.toolbar_spinner_dropdown, parent,
                    false);
            viewHolder = new ViewHolder(view, TYPE_DROPDOWN);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.bindData((District) getItem(position));
        return view;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder viewHolder;

        if (view == null || view.getTag() == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.toolbar_spinner_actionbar, parent,
                    false);
            viewHolder = new ViewHolder(view, TYPE_VIEW);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.bindData((District) getItem(position));
        return view;
    }

    class ViewHolder {
        TextView txtContent;

        public ViewHolder(View itemView, int type) {
            switch (type) {
                case TYPE_DROPDOWN:
                    txtContent = (TextView) itemView.findViewById(R.id.txtDropDown);
                    break;
                case TYPE_VIEW:
                    txtContent = (TextView) itemView.findViewById(R.id.txtSpinner);
                    break;
            }
        }

        public void bindData(District district) {
            if (district != null) {
                txtContent.setText(district.districtName);
            } else {
                txtContent.setText("");
            }
        }
    }
}
