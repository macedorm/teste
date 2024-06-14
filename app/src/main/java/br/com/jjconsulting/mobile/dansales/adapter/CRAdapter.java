package br.com.jjconsulting.mobile.dansales.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import br.com.jjconsulting.mobile.dansales.R;
import br.com.jjconsulting.mobile.dansales.model.Layout;

public class CRAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<String> expandableListTitle;
    private HashMap<String, List<Layout>> expandableListDetail;

    public CRAdapter(Context context, List<Layout> listLayout) {
        this.context = context;
        this.expandableListDetail = expandableListDetail;

        expandableListDetail = new HashMap<String, List<Layout>>();
        expandableListTitle = new ArrayList<>();

        int index = 0;
        String group = "";
        List<Layout> listLayoutTemp = new ArrayList<>();

        for(Layout item: listLayout){
            if(!item.getGrupo().equals(group)){
                if(listLayoutTemp.size() > 0){
                    expandableListDetail.put(group, listLayoutTemp);
                    listLayoutTemp = new ArrayList<>();
                }
                group = item.getGrupo();
                expandableListTitle.add(group);
            }

            listLayoutTemp.add(item);
            index++;
        }

        if(listLayoutTemp.size() > 0){
            expandableListDetail.put(group, listLayoutTemp);
        }
    }

    @Override
    public Object getChild(int listPosition, int expandedListPosition) {
        return this.expandableListDetail.get(this.expandableListTitle.get(listPosition))
                .get(expandedListPosition);
    }

    @Override
    public long getChildId(int listPosition, int expandedListPosition) {
        return expandedListPosition;
    }

    @Override
    public View getChildView(int listPosition, final int expandedListPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        final Layout layout = (Layout) getChild(listPosition, expandedListPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.item_cr, null);
        }
        TextView expandedListTextView = convertView
                .findViewById(R.id.title_cr_text_view);
        expandedListTextView.setText(layout.getCodigo() + " - " + layout.getNome());
        return convertView;
    }

    @Override
    public int getChildrenCount(int listPosition) {
        return this.expandableListDetail.get(this.expandableListTitle.get(listPosition))
                .size();
    }

    @Override
    public Object getGroup(int listPosition) {
        return this.expandableListTitle.get(listPosition);
    }

    @Override
    public int getGroupCount() {
        return this.expandableListTitle.size();
    }

    @Override
    public long getGroupId(int listPosition) {
        return listPosition;
    }

    @Override
    public View getGroupView(int listPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        String listTitle = (String) getGroup(listPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.item_group_cr, null);
        }
        TextView listTitleTextView = (TextView) convertView
                .findViewById(R.id.title_group_cr_text_view);
        listTitleTextView.setTypeface(null, Typeface.BOLD);
        listTitleTextView.setText(listTitle);
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int listPosition, int expandedListPosition) {
        return true;
    }
}
