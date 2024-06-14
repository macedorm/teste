package br.com.jjconsulting.mobile.jjlib.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

import br.com.jjconsulting.mobile.jjlib.R;
import br.com.jjconsulting.mobile.jjlib.dao.entity.TIcon;
import br.com.jjconsulting.mobile.jjlib.masterdata.JJIcon;
import br.com.jjconsulting.mobile.jjlib.model.SpinnerDataItem;

public class JJDialogSearchViewAdapter extends BaseAdapter {

    private Context mContext;

    private LayoutInflater inflater;

    private ArrayList<SpinnerDataItem> mSearchInfo;
    private ArrayList<SpinnerDataItem> arraylist;

    private boolean isShowImageLegend;

    public JJDialogSearchViewAdapter(Context context, boolean isShowImageLegend, ArrayList<SpinnerDataItem> searchInfo) {
        mContext = context;
        this.mSearchInfo = searchInfo;
        inflater = LayoutInflater.from(mContext);
        this.arraylist = new ArrayList<>();

        if(searchInfo.size() > 0){
            //searchInfo.remove(0);
        }

        this.arraylist.addAll(searchInfo);


        this.isShowImageLegend = isShowImageLegend;
    }

    public class ViewHolder {
        TextView name;
        LinearLayout linearLayoutIcon;

    }

    @Override
    public int getCount() {
        return mSearchInfo.size();
    }

    @Override
    public SpinnerDataItem getItem(int position) {
        return mSearchInfo.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View view, ViewGroup parent) {
        final ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.jj_item_list_search_view, null);
            holder.name = view.findViewById(R.id.name_text_view);
            holder.linearLayoutIcon = view.findViewById(R.id.container_icon_linear_layout);
            view.setTag(holder);

        } else {
            holder = (ViewHolder) view.getTag();
        }

        holder.name.setText(mSearchInfo.get(position).getName());

        if(!isShowImageLegend){
            holder.linearLayoutIcon.setVisibility(View.GONE);
        } else {
            holder.linearLayoutIcon.setVisibility(View.VISIBLE);

            TIcon icon = (TIcon.values()[mSearchInfo.get(position).getIcon()]);
            JJIcon jjIcon = new JJIcon(mContext, icon, mSearchInfo.get(position).getColorIcon());
            holder.linearLayoutIcon.removeAllViews();
            holder.linearLayoutIcon.addView(jjIcon.renderView());
        }

        return view;
    }

    public void filter(String charText) {

        charText = charText.toLowerCase(Locale.getDefault());
        String searchString[] = charText.split(" ");


        mSearchInfo.clear();
        if (charText.length() == 0) {
            mSearchInfo.addAll(arraylist);
        } else {
            for (SpinnerDataItem search : arraylist) {

                int contains = 0;

                for(String filter: searchString){
                    if (search.getName().toLowerCase(Locale.getDefault()).contains(filter)) {
                        contains++;
                    }
                }

                if(contains > 0 && contains == searchString.length){
                    mSearchInfo.add(search);
                }


            }
        }
        notifyDataSetChanged();
    }

}
