package br.com.jjconsulting.mobile.dansales.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.graphics.Typeface;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import br.com.jjconsulting.mobile.dansales.R;
import br.com.jjconsulting.mobile.dansales.database.SortimentoDao;
import br.com.jjconsulting.mobile.dansales.model.ItensListSortimento;
import br.com.jjconsulting.mobile.dansales.model.Produto;
import br.com.jjconsulting.mobile.jjlib.util.FormatUtils;
import br.com.jjconsulting.mobile.jjlib.util.JJExpandableCustomGridView;
import br.com.jjconsulting.mobile.jjlib.util.TextUtils;

public class JJProductGridView{

    private JJExpandableCustomGridView expandableListView;

    private ExpandableListAdapter expandableListAdapter;
    private List<String> expandableListTitle;
    private HashMap<String, ArrayList<ItensListSortimento>> expandableListDetail;

    private Context context;

    private int size;
    private int columnwidth;

    private boolean isPreco;

    private OnClickGrid onClickGrid;

    public JJProductGridView(Context context, boolean isPreco){
        this.context = context;
        expandableListView = new JJExpandableCustomGridView(context);
        expandableListView.setFooterDividersEnabled(false);
        expandableListView.setHeaderDividersEnabled(false);
        this.isPreco = isPreco;
    }

    public View renderView(HashMap<String, ArrayList<ItensListSortimento>> objects) {

        size = 0;

        expandableListDetail = objects;

        expandableListTitle = new ArrayList<>(expandableListDetail.keySet());
        expandableListDetail = sortByValues(objects);

        expandableListView.setOnGroupClickListener((parent, v, groupPosition, id)-> {
                expandableListView.post(()-> {
                    setListViewHeight(expandableListView);
                });
                return false;
        });

        expandableListAdapter = new JJProductGridViewListAdapter(context, expandableListTitle, expandableListDetail);
        expandableListView.setAdapter(expandableListAdapter);
        expandableListView.setChildDivider(context.getResources().getDrawable(R.color.white));

        expandableListView.post(()->{
            setListViewHeight(expandableListView);
        });

        return expandableListView;
    }

    private void setListViewHeight(ExpandableListView listView) {
        ExpandableListAdapter listAdapter = listView.getExpandableListAdapter();

        int desWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(),
                View.MeasureSpec.EXACTLY);

        for (int i = 0; i < listAdapter.getGroupCount(); i++) {
            View groupItem = listAdapter.getGroupView(i, false, null, listView);
            groupItem.measure(desWidth, View.MeasureSpec.UNSPECIFIED);

            if (listView.isGroupExpanded(i)) {
                for (int j = 0; j < listAdapter.getChildrenCount(i); j++) {
                    View listItem = listAdapter.getChildView(i, j, false, null,
                            listView);
                    listItem.measure(desWidth, View.MeasureSpec.UNSPECIFIED);
                }
           }
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();

        Display display = ((Activity)context).getWindowManager(). getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        params.height = (size.y / 2);
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    private HashMap sortByValues(HashMap<String, ArrayList<ItensListSortimento>>  map) {

        Collections.sort(expandableListTitle);

        HashMap<String, ArrayList<ItensListSortimento>> sortedHashMap = new LinkedHashMap();

        for(String item:expandableListTitle){

            Iterator it = map.entrySet().iterator();

            while(it.hasNext()){
                Map.Entry pairs = (Map.Entry) it.next();

                if(pairs.getKey().equals(item)){

                    ArrayList<ItensListSortimento> itensListSortimentos = ((ArrayList<ItensListSortimento>) pairs.getValue());
                    int count = 0;

                    if(isPreco){
                        count = itensListSortimentos.size();
                    } else {
                        for(ItensListSortimento itemSortimento: itensListSortimentos){
                            if(itemSortimento.getStatus() != Produto.SORTIMENTO_TIPO_SUBSTITUTO){
                                count++;
                            }
                        }
                    }

                    size += count;
                    sortedHashMap.put(pairs.getKey().toString(), itensListSortimentos);
                }
            }
        }

        return sortedHashMap;
    }

    public class JJProductGridViewListAdapter extends  BaseExpandableListAdapter {

        private Context context;
        private List<String> expandableListTitle;
        private HashMap<String, ArrayList<ItensListSortimento>> expandableListDetail;


        public JJProductGridViewListAdapter(Context context, List<String> expandableListTitle,
                                           HashMap<String, ArrayList<ItensListSortimento>> expandableListDetail) {
            this.context = context;
            this.expandableListTitle = expandableListTitle;
            this.expandableListDetail = expandableListDetail;
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
            ItensListSortimento itensListSortimento = (ItensListSortimento) getChild(listPosition, expandedListPosition);

            if (convertView == null) {
                LayoutInflater layoutInflater = (LayoutInflater) this.context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = layoutInflater.inflate(R.layout.jj_item_product_grid_view, null);

            }

            ViewGroup viewGroup = convertView.findViewById(R.id.product_linear_layout);
            viewGroup.setTag(listPosition + "-" + expandedListPosition);

            if(columnwidth > 0){
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(columnwidth, LinearLayout.LayoutParams.WRAP_CONTENT);

                int valueMargin = (int) context.getResources().getDimension(R.dimen.margin_bottom_dan_button);

                params.setMargins(valueMargin, valueMargin, valueMargin, valueMargin);
                viewGroup.setLayoutParams(params);
            }

            TextView expandedListTextView = convertView.findViewById(R.id.product_expanded_list_item);

            if(isPreco){
                expandedListTextView.setText(TextUtils.firstLetterUpperCase(itensListSortimento.getDescVariante()));
            } else {
                expandedListTextView.setText(TextUtils.firstLetterUpperCase(itensListSortimento.getName()));
            }

            ImageView imageView = convertView.findViewById(R.id.product_image_view);
            PicassoCustom.setImage(context, itensListSortimento.getUrl(), imageView);

            changeBackground(viewGroup, itensListSortimento);

            viewGroup.setOnClickListener((v)->{

                String tag[] = v.getTag().toString().split("-");
                ItensListSortimento item = (ItensListSortimento) getChild(Integer.parseInt(tag[0]), Integer.parseInt(tag[1]));

                if(isPreco){

                    DialogsProductPrice dialogsProductPrice = new DialogsProductPrice((Activity)context);
                    dialogsProductPrice.showDialog(isPreco ? itensListSortimento.getDescVariante():itensListSortimento.getName(), context.getString(R.string.info_price),item.getPrecoUser(),  new DialogsProductPrice.OnClickDialogMessage() {
                        @Override
                        public void onClick(String values) {
                            String preco = values.replace("R$", "");
                            preco = preco.replace(",", ".");
                            preco = preco.trim();
                            preco = preco.replace(" ", "");

                            item.setPrecoUser(preco);
                            itensListSortimento.setSelected(SortimentoUtils.isItemPrecoSelected(item.getPrecoEDV(), item.getPrecoPROMO(), item.getPrecoUser()));

                            expandableListDetail.get(expandableListTitle.get(Integer.parseInt(tag[0]))).set( Integer.parseInt(tag[1]), item);

                            LinearLayout precoLinearLayout = v.findViewById(R.id.preco_linear_layout);

                            if(!TextUtils.isNullOrEmpty(itensListSortimento.getPrecoUser())){
                                precoLinearLayout.setVisibility(View.VISIBLE);
                            }

                            if(onClickGrid != null){
                                onClickGrid.onClick();
                            }

                            expandableListDetail.get(expandableListTitle.get(Integer.parseInt(tag[0]))).set( Integer.parseInt(tag[1]), item);
                            changeBackground(viewGroup, item);

                        }
                    });

                    expandableListView.post(()->{
                        setListViewHeight(expandableListView);
                    });

                } else {
                    itensListSortimento.setSelected(!item.isSelected());
                    expandableListDetail.get(expandableListTitle.get(Integer.parseInt(tag[0]))).set( Integer.parseInt(tag[1]), item);
                    changeBackground(viewGroup, item);
                }

            });

            if(isPreco){
                LinearLayout precoLinearLayout = convertView.findViewById(R.id.preco_linear_layout);

                if(!TextUtils.isNullOrEmpty(itensListSortimento.getPrecoUser())){
                    precoLinearLayout.setVisibility(View.VISIBLE);
                } else {
                    precoLinearLayout.setVisibility(View.GONE);
                }

                float precoMin;
                float precoMax;

                if(itensListSortimento.getPrecoEDV() > itensListSortimento.getPrecoPROMO()){
                    precoMax = itensListSortimento.getPrecoEDV();
                    precoMin = itensListSortimento.getPrecoPROMO();
                } else {
                    precoMin = itensListSortimento.getPrecoEDV();
                    precoMax = itensListSortimento.getPrecoPROMO();
                }


                TextView precoMinTextView = convertView.findViewById(R.id.preco_min_text_view);
                precoMinTextView.setText(FormatUtils.toBrazilianRealCurrency(precoMin).replace("R$", ""));

                TextView precoManTextView = convertView.findViewById(R.id.preco_man_text_view);
                precoManTextView.setText(FormatUtils.toBrazilianRealCurrency(precoMax).replace("R$", ""));
            }

            return convertView;
        }

        private void changeBackground(View view, ItensListSortimento itensListSortimento){
            CardView minCardView = view.findViewById(R.id.container_min_card_view);
            CardView maxCardView = view.findViewById(R.id.container_max_card_view);

            if(itensListSortimento.isSelected()){
                view.setBackground(context.getResources().getDrawable(R.drawable.background_green_product_grid));
                minCardView.setCardBackgroundColor(context.getResources().getColor(R.color.sucessCollor));
                maxCardView.setCardBackgroundColor(context.getResources().getColor(R.color.sucessCollor));
            } else {
                view.setBackground(context.getResources().getDrawable(R.drawable.background_red_product_grid));
                minCardView.setCardBackgroundColor(context.getResources().getColor(R.color.errorCollor));
                maxCardView.setCardBackgroundColor(context.getResources().getColor(R.color.errorCollor));
            }
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
        public boolean areAllItemsEnabled() {
            return true;
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
                convertView = layoutInflater.inflate(R.layout.jj_group_product_grid_view, null);
            }

            TextView listTitleTextView = convertView
                    .findViewById(R.id.listTitle);
            listTitleTextView.setTypeface(null, Typeface.BOLD);

            listTitle = listTitle != null ? listTitle.toUpperCase().replace( "BASE BUSINESS", ""):"";
            listTitleTextView.setText(listTitle);

            return convertView;
        }


        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public boolean isChildSelectable(int listPosition, int expandedListPosition) {
            return false;
        }
    }

    public boolean isAllItensSelected(){
        boolean isAllItensSelected = false;

        if(isPreco) {
            String answer = getItensSelected();

            if(!TextUtils.isNullOrEmpty(answer)){
                String listAnswer[] = answer.split("\\|");

                if(listAnswer.length - 1 == size){
                    isAllItensSelected = true;
                }
            } else {
                isAllItensSelected = false;
            }
        } else {
            isAllItensSelected = true;
        }

        return isAllItensSelected;
    }


    public String getItensSelected(){

        String answer = "";

        for (String key : expandableListDetail.keySet()) {

            for(ItensListSortimento itens:expandableListDetail.get(key)){
                if(isPreco) {
                    if(!TextUtils.isNullOrEmpty(itens.getPrecoUser())){
                        if (TextUtils.isNullOrEmpty(answer)) {
                            answer += itens.getSKU() + "#" + itens.getPrecoEDV() + "#" + itens.getPrecoPROMO() + "#" + itens.getPrecoUser() + "#" + (itens.isSelected() ? "1":"0");
                        } else {
                            answer  += "|" + itens.getSKU() + "#" + itens.getPrecoEDV() + "#" + itens.getPrecoPROMO() + "#" + itens.getPrecoUser() + "#" +  (itens.isSelected() ? "1":"0");
                        }
                    }

                } else {
                    if(itens.isSelected()){
                        if (TextUtils.isNullOrEmpty(answer)) {
                            answer += itens.getSKU();
                        } else {
                            answer += "|" + itens.getSKU();
                        }
                    }
                }
            }
        }

        if(!TextUtils.isNullOrEmpty(answer)){
            if(isPreco) {
                answer += "|" + size;
            } else {

                String listAnswer[] = answer.split("\\|");

                int count = 0;

                for (String key : expandableListDetail.keySet()) {
                    for (ItensListSortimento itens : expandableListDetail.get(key)) {
                        if (itens.getStatus() != Produto.SORTIMENTO_TIPO_SUBSTITUTO) {
                            if (itens.isSelected()) {
                                count++;
                            } else if (!TextUtils.isNullOrEmpty(itens.getSUBSTITUTE())) {
                                for (int ind = 0; ind < listAnswer.length; ind++) {
                                    if (listAnswer[ind].equals(itens.getSUBSTITUTE())) {
                                        count++;
                                        ind = listAnswer.length;
                                    }
                                }
                            }
                        }
                    }
                }

                answer += "|" + size + "#" + count;
            }
        }
        return answer;
    }

    public int getSize(){
        return expandableListDetail == null ? 0:expandableListDetail.size();
    }

    public void setVisible(int visible){
        expandableListView.setVisibility(visible);
    }

    public HashMap<String, ArrayList<ItensListSortimento>> getExpandableList(){
        return expandableListDetail;
    }

    public void setColumnWidth(int width){
        columnwidth = width;
    }

    public void setNumColumn(int num){
        expandableListView.setNumColumns(num);
    }

    public OnClickGrid getOnClickGrid() {
        return onClickGrid;
    }

    public void setOnClickGrid(OnClickGrid onClickGrid) {
        this.onClickGrid = onClickGrid;
    }

    public interface OnClickGrid{
        void onClick();
    }

    public void addHeader(View view){
        if(expandableListView == null)
            return;
        expandableListView.addHeaderView(view);
    }

    public void addFooter(View view){
        if(expandableListView == null)
            return;
        expandableListView.addFooterView(view);
    }

}
