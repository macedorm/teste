package br.com.jjconsulting.mobile.jjlib.masterdata;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListPopupWindow;
import android.widget.TextView;

import java.util.List;

import br.com.jjconsulting.mobile.jjlib.R;
import br.com.jjconsulting.mobile.jjlib.dao.entity.TIcon;

public class JJPopMenuView {

    private ListPopupWindow listPopupWindow;

    private OnClickItem onClickItem;
    private Context mContext;

    public JJPopMenuView(Context context, OnClickItem onClickItem) {
        this.mContext = context;
        this.onClickItem = onClickItem;
    }

    public void dismiss(){
        listPopupWindow.dismiss();
    }

    public void showListPopupWindow(View anchor, List<ListPopupItem> listPopupItems) {
        listPopupWindow =
                createListPopupWindow(anchor, listPopupItems);
        listPopupWindow.setOnItemClickListener((parent, view, idItem, id)-> {
            if(onClickItem != null){
                onClickItem.onClick(listPopupItems.get(idItem).id);
            }
        });

        listPopupWindow.show();
    }

    public ListPopupWindow createListPopupWindow(View anchor,
                                                 List<ListPopupItem> items) {
        final ListPopupWindow popup = new ListPopupWindow(mContext);
        ListAdapter adapter = new ListPopupWindowAdapter(items);
        popup.setAnchorView(anchor);
        popup.setAdapter(adapter);
        popup.setDropDownGravity(Gravity.END);
        popup.setModal(true);

        DisplayMetrics displaymetrics = mContext.getResources().getDisplayMetrics();
        popup.setWidth((int)(displaymetrics.widthPixels / 1.5));

        return popup;
    }

    public class ListPopupWindowAdapter extends BaseAdapter {
        private List<ListPopupItem> items;

        public ListPopupWindowAdapter(List<ListPopupItem> items) {
            this.items = items;
        }

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public ListPopupItem getItem(int i) {
            return items.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.jj_item_pop_up_window, null);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.mContainerLinearLayout.removeAllViews();

            holder.mTitleTextView.setText(getItem(position).getTitle());

            if(items.get(position).imageRes != -1){
                TIcon icon = (TIcon.values()[items.get(position).imageRes]);
                JJIcon jjIcon = new JJIcon(mContext, icon,  String.format("#%06x", mContext.getResources().getColor(R.color.action_icon_menu) & 0xffffff));
                holder.mContainerLinearLayout.addView(jjIcon.renderView());
                holder.mContainerLinearLayout.setVisibility(View.VISIBLE);
            } else {
                holder.mContainerLinearLayout.setVisibility(View.GONE);
            }


            return convertView;
        }

          public class ViewHolder {
            TextView mTitleTextView;
            LinearLayout mContainerLinearLayout;

            ViewHolder(View view) {
                mTitleTextView = view.findViewById(R.id.text_popup_window);
                mContainerLinearLayout = view.findViewById(R.id.container_popup_window_linear_layout);
            }
        }
    }

    public static class ListPopupItem {
        private String title;
        private int imageRes;
        private int id;

        public ListPopupItem(String title, int imageRes, int id) {
            this.id = id;
            this.title = title;
            this.imageRes = imageRes;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public int getImageRes() {
            return imageRes;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }
    }


    public interface OnClickItem{
        void onClick(int position);
    }

    public static ListPopupItem getInstance(String title, int imageRes, int id){
       ListPopupItem item = new ListPopupItem(title, imageRes, id);
        return  item;
    }


}

