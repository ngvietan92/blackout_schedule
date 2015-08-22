package com.ngvietan.blackoutschedule.adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ngvietan.blackoutschedule.R;
import com.ngvietan.blackoutschedule.common.CommonVariables;
import com.ngvietan.blackoutschedule.models.BlackoutItem;
import com.ngvietan.blackoutschedule.models.Province;
import com.tonicartos.superslim.GridSLM;
import com.tonicartos.superslim.LayoutManager;
import com.tonicartos.superslim.LinearSLM;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

/**
 * Created by NgVietAn on 28/07/2015.
 */
public class BlackoutAdapter extends RecyclerView.Adapter<BlackoutAdapter.ViewHolder> {

    private static final int VIEW_TYPE_HEADER = 0x01;

    private static final int VIEW_TYPE_CONTENT = 0x00;

    private List<LineItem> items;

    private Context context;

    public BlackoutAdapter(Context context, List<BlackoutItem> items) {
        super();
        this.context = context;
        this.items = new ArrayList<>();
        addItems(items);
    }

    private static String formatDetail(String eDetail) {
        // remove same place
        String[] array = null;
        switch (CommonVariables.selectedProvince.id) {
            case Province.ID_DANANG:
                array = eDetail.split(";");
                break;
            case Province.ID_QUANG_NAM:
                array = eDetail.split(",");
                break;
        }
        if (array != null) {
            array = new HashSet<String>(Arrays.asList(array)).toArray(new String[0]);
            String result = "";
            int count = 0;
            for (String s : array) {
                count++;
                s = s.trim();
                if (!s.isEmpty()) {
                    result += s;
                    if (count < array.length) {
                        result += "\n";
                    }
                }
            }
            return result;
        } else {
            return eDetail;
        }
    }

    public void addItems(List<BlackoutItem> items) {
        if (items != null && items.size() != 0) {
            this.items.clear();

            String lastStart = "";
            String lastEnd = "";
            int headerId = 0;
            int headerCount = 0;
            int sectionFirstPosition = 0;
            for (int i = 0; i < items.size(); i++) {
                BlackoutItem item = items.get(i);
                // add sub header time (start and end)
                if (!item.start.equals(lastStart) || !item.end.equals(lastEnd)) {
                    // save time
                    lastStart = item.start;
                    lastEnd = item.end;

                    // calculate new section first
                    sectionFirstPosition = i + headerCount++;
                    // add sub header time
                    this.items.add(new LineItem(item, true, sectionFirstPosition, headerId));
                }

                this.items.add(new LineItem(item, false, sectionFirstPosition, i % 2));
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position).isHeader ? VIEW_TYPE_HEADER : VIEW_TYPE_CONTENT;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view;
        if (viewType == VIEW_TYPE_HEADER) {
            view = inflater.inflate(R.layout.row_blackout_header, parent, false);
        } else {
            view = inflater.inflate(R.layout.row_blackout, parent, false);
        }
        return new ViewHolder(context, view, viewType);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        LineItem item = items.get(position);
        holder.bindItem(item);
        GridSLM.LayoutParams layoutParams = GridSLM.LayoutParams.from(
                holder.itemView.getLayoutParams());
        if (item.isHeader) {
            layoutParams.headerDisplay = LayoutManager.LayoutParams.HEADER_ALIGN_START + LayoutManager.LayoutParams.HEADER_STICKY;
            layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;
            layoutParams.headerEndMarginIsAuto = false;
            layoutParams.headerStartMarginIsAuto = false;
        }
        layoutParams.setSlm(LinearSLM.ID);
        layoutParams.setFirstPosition(items.get(position).sectionFirstPosition);
        holder.itemView.setLayoutParams(layoutParams);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        Context context;
        LinearLayout cardBlackOut;
        TextView txtIndex;
        TextView txtDetail;
        TextView txtStart;
        TextView txtEnd;

        public ViewHolder(Context context, View itemView, int viewType) {
            super(itemView);
            this.context = context;
            if (viewType == VIEW_TYPE_HEADER) {
                txtStart = (TextView) itemView.findViewById(R.id.txtStart);
                txtEnd = (TextView) itemView.findViewById(R.id.txtEnd);
            } else {
                cardBlackOut = (LinearLayout) itemView.findViewById(R.id.cardBlackOut);
                txtIndex = (TextView) itemView.findViewById(R.id.txtIndex);
                txtDetail = (TextView) itemView.findViewById(R.id.txtDetail);
            }
        }

        public void bindItem(LineItem item) {
            if (item.isHeader) {
                txtStart.setText(item.blackoutItem.start.replace(" ", "\n"));
                txtEnd.setText(item.blackoutItem.end.replace(" ", "\n"));
            } else {
                if (item.sectionId == 0) {
                    cardBlackOut.setBackgroundColor(
                            context.getResources().getColor(R.color.material_500));
                } else if (item.sectionId == 1) {
                    cardBlackOut.setBackgroundColor(
                            context.getResources().getColor(R.color.material_accent));
                }

                if (item.blackoutItem.eIndex != null && !item.blackoutItem.eIndex.isEmpty()) {
                    txtIndex.setVisibility(View.VISIBLE);
                    txtIndex.setText(item.blackoutItem.eIndex);
                    txtDetail.setText(formatDetail(item.blackoutItem.eDetail));
                } else {
                    txtIndex.setVisibility(View.GONE);
                    txtDetail.setText(formatDetail(item.blackoutItem.eDetail));
                }
            }
        }

    }

    private static class LineItem {

        public int sectionFirstPosition;

        public int sectionId;

        public boolean isHeader;

        public BlackoutItem blackoutItem;

        public LineItem(BlackoutItem blackoutItem, boolean isHeader,
                int sectionFirstPosition, int sectionId) {
            this.isHeader = isHeader;
            this.blackoutItem = blackoutItem;
            this.sectionFirstPosition = sectionFirstPosition;
            this.sectionId = sectionId;
        }
    }
}
