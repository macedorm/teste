package br.com.jjconsulting.mobile.jjlib.masterdata;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.os.AsyncTask;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.beardedhen.androidbootstrap.AwesomeTextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

import br.com.jjconsulting.mobile.jjlib.R;
import br.com.jjconsulting.mobile.jjlib.base.ItemClickSupport;
import br.com.jjconsulting.mobile.jjlib.dao.entity.Factory;
import br.com.jjconsulting.mobile.jjlib.dao.entity.FormElement;
import br.com.jjconsulting.mobile.jjlib.dao.entity.FormElementField;
import br.com.jjconsulting.mobile.jjlib.dao.entity.TIcon;
import br.com.jjconsulting.mobile.jjlib.dao.entity.TMenuType;
import br.com.jjconsulting.mobile.jjlib.model.DataItem;
import br.com.jjconsulting.mobile.jjlib.model.DataTable;
import br.com.jjconsulting.mobile.jjlib.util.Config;
import br.com.jjconsulting.mobile.jjlib.util.DialogsCustom;
import br.com.jjconsulting.mobile.jjlib.util.JJSDK;
import br.com.jjconsulting.mobile.jjlib.util.LogUser;

public class JJMenuView extends JJBaseView {

    public static int fontSizeDefault = 20;
    public static int fontSizeButtonDefault = 23;

    private AsyncTaskDataLoading asyncTaskDataLoading;

    private OnClickList onClickList;

    private Factory factory;

    private FormElement mFormElement;

    private Context mContext;

    private DialogsCustom dialogsCustom;

    private ArrayList<DataTable> appMenu;
    private ArrayList<DataTable> appButtonMenu;


    private LinearLayout mListEmptyLinearLayout;
    private LinearLayout mContainerLinearLayout;
    private LinearLayout mListProgressLinearLayout;

    private JJRecyclerViewAdapter mJJRecyclerViewAdapter;
    private JJGridButtonAdapter mJJGridButtonAdapter;

    private RecyclerView mGridViewRecyclerView;
    private GridView mButtonGridView;

    private View viewSelected;

    private DrawerLayout mDrawerLayout;

    private Hashtable relationValues;
    private Hashtable mFilter;

    private FragmentManager fragmentManager;
    private Fragment fragment;

    private HashMap<Integer, Fragment> modules;

    private boolean styleButton;

    private int idFragment;

    private int selectedPosition;
    private int selectedPositionOld;

    private int primaryColor;

    private String titleHome;

    private String appName;

    private ArrayList<View> listJJIcon;
    private ArrayList<View> positionView;

    private RelativeLayout baseRelativeLayout;

    private boolean showButtonAdd;

    public JJMenuView(Context context, int idFragment, int primaryColor, FormElement formElement, ArrayList<DataTable> appMenu) {
        this.mContext = context;
        this.fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
        this.idFragment = idFragment;
        this.modules = new HashMap<>();
        this.primaryColor = primaryColor;
        this.dialogsCustom = new DialogsCustom(context);
        this.listJJIcon = new ArrayList<>();
        positionView = new ArrayList<>();
        this.mFormElement = formElement;
        this.appMenu = appMenu;
        this.showButtonAdd = true;
    }

    public JJMenuView(Context context, int idFragment, int primaryColor) {
        this.mContext = context;
        this.fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
        this.idFragment = idFragment;
        this.modules = new HashMap<>();
        this.primaryColor = primaryColor;
        dialogsCustom = new DialogsCustom(context);
        listJJIcon = new ArrayList<>();
        positionView = new ArrayList<>();
        this.showButtonAdd = true;


        try {
            if (!JJSDK.isInitialize()) {
                throw new Exception(context.getString(R.string.sdk_error), new Throwable(""));
            }

        } catch (Exception ex) {
            LogUser.log(Config.TAG, ex.toString());
        }
    }

    public View renderView() {
        LayoutInflater inflater = ((Activity)mContext).getLayoutInflater();
        return onCreateView(inflater);
    }

    private View onCreateView(LayoutInflater inflater) {
        baseRelativeLayout = (RelativeLayout) inflater.inflate(R.layout.menu_base_linear_layout, null);
        mButtonGridView = (GridView) inflater.inflate(R.layout.jj_grid_auto_fit_view, null);

        mContainerLinearLayout = baseRelativeLayout.findViewById(R.id.content_linear_layout);
        mListEmptyLinearLayout = baseRelativeLayout.findViewById(R.id.list_empty_text_view);
        mListProgressLinearLayout = baseRelativeLayout.findViewById(R.id.loading_linear_layout);
        mListProgressLinearLayout.setVisibility(View.VISIBLE);

        RelativeLayout containerRelativeLayout = new RelativeLayout(mContext);
        RelativeLayout.LayoutParams lpContainer = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        containerRelativeLayout.setLayoutParams(lpContainer);

        createList();

        if(appMenu == null){
            loadData();
        } else {
            setData();
        }

        configHeaderList(baseRelativeLayout.findViewById(R.id.container_footer_linear_layout));

        return baseRelativeLayout;
    }

    public void onBack(){
        LinearLayout header = baseRelativeLayout.findViewById(R.id.container_footer_linear_layout);
        clickItemList(header, appMenu,-1);
    }

    private View createList(){
        if(styleButton){
            mButtonGridView.setVisibility(View.VISIBLE);
            mButtonGridView.setOnItemClickListener((parent, view, position, id) ->  {
                clickItemList(view, appButtonMenu, position);
            });

            mContainerLinearLayout.addView(mButtonGridView);
        } else {
            mButtonGridView.setVisibility(View.GONE);
            mGridViewRecyclerView = new RecyclerView(mContext);
            final RecyclerView.LayoutParams lpRecyclerView = new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.MATCH_PARENT);
            mGridViewRecyclerView.setLayoutParams(lpRecyclerView);
            mGridViewRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
            mGridViewRecyclerView.setNestedScrollingEnabled(false);
            mGridViewRecyclerView.setHasFixedSize(true);
            DividerItemDecoration divider = new DividerItemDecoration(mGridViewRecyclerView.getContext(), DividerItemDecoration.VERTICAL);
            divider.setDrawable(mContext.getResources().getDrawable(R.drawable.null_divider));
            mGridViewRecyclerView.addItemDecoration(divider);
            mGridViewRecyclerView.setNestedScrollingEnabled(false);
            mGridViewRecyclerView.setLayoutFrozen(true);
            mGridViewRecyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);

            mContainerLinearLayout.addView(mGridViewRecyclerView);

            ItemClickSupport.addTo(mGridViewRecyclerView).setOnItemClickListener(
                    (recyclerView, position, v) -> {
                        clickItemList(v, appMenu, position);
            });
        }

        return null;
    }

    private void configHeaderList(View view){

        if(!styleButton){
            mGridViewRecyclerView.post(new Runnable() {
                @Override
                public void run() {
                    clickItemList(view, appMenu, - 1);
                }
            });

            LinearLayout containerIconLinearLayout = view.findViewById(R.id.container_icon_linear_layout);


            TIcon icon = TIcon.values()[321];
            JJIcon jjIcon = new JJIcon(mContext, icon, "#" + Integer.toHexString(primaryColor));
            View viewIcon = jjIcon.renderView();

            listJJIcon.add(viewIcon);
            positionView.add(containerIconLinearLayout);

            containerIconLinearLayout.addView(viewIcon);

            view.setOnClickListener((v)->{
                clickItemList(v, appMenu, - 1);
            });
        } else {
            view.setVisibility(View.GONE);
        }

    }

    private void changeColorViemCornerItem(View view, boolean isSelect){
        float[] outerRadii = new float[]{30,30,30,30,30,30,30,30};
        float[] innerRadii = new float[]{30,30,30,30,30,30,30,30};

        ShapeDrawable borderDrawable = new ShapeDrawable(new RoundRectShape(
                outerRadii,
                null,
                innerRadii
        ));

        ShapeDrawable backgroundShape = new ShapeDrawable(new RoundRectShape(
                outerRadii,
                null,
                innerRadii
        ));

        if(isSelect){
            String colorFull = Integer.toHexString(primaryColor);
            String color = "#E6" + colorFull.substring(2);
            backgroundShape.getPaint().setColor(Color.parseColor(color));
        } else {
            backgroundShape.getPaint().setColor(primaryColor);
        }
        backgroundShape.getPaint().setStyle(Paint.Style.FILL);
        backgroundShape.getPaint().setAntiAlias(true);

        Drawable[] drawables = new Drawable[]{
                borderDrawable,
                backgroundShape
        };

        backgroundShape.setPadding(70,70,70,70);
        LayerDrawable layerDrawable = new LayerDrawable(drawables);
        view.setBackground(layerDrawable);
    }

    public void changeColor(View v, int position){

        if(viewSelected != null){

            int[] attrs = new int[]{R.attr.selectableItemBackground};
            TypedArray typedArray = mContext.obtainStyledAttributes(attrs);
            int backgroundResource = typedArray.getResourceId(0, 0);
            viewSelected.setBackgroundResource(backgroundResource);

            ((TextView)viewSelected.findViewById(R.id.name_menu_text_View)).setTextColor(mContext.getResources().getColor(R.color.menu_text));
            ((AwesomeTextView)listJJIcon.get(selectedPositionOld + 1)).setTextColor(mContext.getResources().getColor(R.color.menu_icon));

        }

        v.setBackgroundColor(mContext.getResources().getColor(R.color.menu_selected_background));
        ((TextView)v.findViewById(R.id.name_menu_text_View)).setTextColor(primaryColor);
        ((AwesomeTextView)listJJIcon.get(position + 1)).setTextColor(primaryColor);

        viewSelected = v;
        selectedPositionOld = position;

    }

    private void clickItemList(View v, ArrayList<DataTable> dataTable, int position){
        if(selectedPosition == -1 && position == -1){
            if (mDrawerLayout != null) {
                mDrawerLayout.closeDrawer(GravityCompat.START);
            }
            return;
        } else {

            if (onClickList != null) {
                onClickList.onClick(v, position);
            }

            selectedPosition = position;

            if (!styleButton) {
                changeColor(v, position);
            }

            fragment = null;

            if (position != -1) {

                int type = Integer.parseInt(factory.getValueFieldName(dataTable.get(position), "MNU_TYPE", getElement()));

                switch (TMenuType.fromInteger(type)) {
                    case MASTERDATA:
                        JJFormView jjFormView = JJFormView.renderFragment(mContext, factory.getValueFieldName(dataTable.get(position), "MNU_ACTION", getElement()));
                        fragment = jjFormView;
                        break;
                    case URL:
                        fragment = JJWebView.renderFragment(getContext(), factory.getValueFieldName(dataTable.get(position), "MNU_ACTION", getElement()));
                        break;
                    case MODULE:
                        int moduleId = Integer.parseInt(factory.getValueFieldName(dataTable.get(position), "MNU_MODULE", getElement()));
                        fragment = modules.get(moduleId);
                        break;
                }
            } else {
                fragment = JJHomeFragment.newInstance(this);
            }

            if (mDrawerLayout != null) {
                mDrawerLayout.closeDrawer(GravityCompat.START);
            }

            fragmentManager.beginTransaction()
                    .replace(idFragment, fragment)
                    .commitAllowingStateLoss();
        }
    }

    private void loadData() {

        if (asyncTaskDataLoading != null) {
            asyncTaskDataLoading.cancel(true);
        }

        asyncTaskDataLoading = new AsyncTaskDataLoading((objects) ->  {
            appMenu = objects;
            setData();
            dialogsCustom.showDialogLoading(false);
        });
        asyncTaskDataLoading.execute();

    }

    private void setData(){
        if(styleButton){

            appButtonMenu = new ArrayList<>();

            for(DataTable dataTable: appMenu){
                for (int ind = 0; ind < dataTable.getDataItens().size(); ind++) {
                    FormElementField form = getElement().getFormFields().get(ind);
                    if(form.getFieldname().equals("MNU_SHOWHOME")){
                        if("1".equals(dataTable.getDataItens().get(ind).getValue().toString())){
                            appButtonMenu.add(dataTable);
                        }
                        ind = dataTable.getDataItens().size();
                    }
                }
            }

            mJJGridButtonAdapter = new JJGridButtonAdapter(mContext, appButtonMenu);
            mButtonGridView.setAdapter(mJJGridButtonAdapter);
        } else {

            mJJRecyclerViewAdapter = new JJRecyclerViewAdapter(mContext, appMenu);
            mGridViewRecyclerView.setAdapter(mJJRecyclerViewAdapter);

            mListProgressLinearLayout.setVisibility(View.GONE);
            if (mJJRecyclerViewAdapter.getItemCount() == 0) {
                mListEmptyLinearLayout.setVisibility(View.VISIBLE);
                mGridViewRecyclerView.setVisibility(View.GONE);
            } else {
                mListEmptyLinearLayout.setVisibility(View.GONE);
                mGridViewRecyclerView.setVisibility(View.VISIBLE);
            }
        }
    }

    public FormElement getElement() {

        if(mFormElement == null){
            if(factory == null)
                factory = new Factory(getContext());

            mFormElement = factory.getFormElement("AppMenu");
        }

        return mFormElement;
    }

    public void setElement(FormElement element) {
        this.mFormElement = element;
    }

    private String getOrderBy() {
        String orderby = "";

        orderby += "MNU_ORDER " + " ASC ";

        return orderby;
    }

    private Hashtable getFilter(){
        if(relationValues != null){
            if(mFilter == null){
                return relationValues;
            } else {
                Hashtable filterTemp = mFilter;

                for (Object key : relationValues.keySet()) {
                    if(relationValues.containsKey(key)){
                        filterTemp.remove(key);
                    }
                    filterTemp.put(key, relationValues.get(key));
                }

                return  filterTemp;
            }
        } else {
            return mFilter;
        }
    }

    public void setStyleButton(boolean styleButton) {
        this.styleButton = styleButton;
    }

    public void setDrawerLayout(DrawerLayout mDrawerLayout) {
        this.mDrawerLayout = mDrawerLayout;
    }

    public void setModule(int moduleId, Fragment moduleFragment) {
        if (this.modules.containsKey(moduleId)) {
            this.modules.remove(moduleId);
        }
        this.modules.put(moduleId,moduleFragment);
    }

    public void setHashModule(HashMap<Integer, Fragment> hashModule){
        this.modules.clear();
        this.modules.putAll(hashModule);
    }

    public int getSelectedPosition() {
        return selectedPosition;
    }

    public void setSelectedPosition(int selectedPosition) {
        this.selectedPosition = selectedPosition;
    }

    public Context getContext() {
        return mContext;
    }

    public int getIdFragment() {
        return idFragment;
    }

    public int getPrimaryColor() {
        return primaryColor;
    }

    public HashMap<Integer, Fragment> getModules() {
        return modules;
    }

    public String getTitleHome() {
        return titleHome;
    }

    public void setTitleHome(String titleHome) {
        this.titleHome = titleHome;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public void setOnClickList(OnClickList onClickList) {
        this.onClickList = onClickList;
    }

    public ArrayList<View> getPositionView() {
        return positionView;
    }

    public boolean isShowButtonAdd() {
        return showButtonAdd;
    }

    public void setShowButtonAdd(boolean showButtonAdd) {
        this.showButtonAdd = showButtonAdd;
    }

    private class JJRecyclerViewAdapter extends RecyclerView.Adapter<JJRecyclerViewAdapter.ViewHolder> {
        private static final int ITEM = 0;
        private static final int LOADING = 1;
        private Context mContext;
        private ArrayList<DataTable> appMenu;

        public JJRecyclerViewAdapter(Context context, ArrayList<DataTable> dataTable) {
            mContext = context;
            appMenu = dataTable;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View view = null;

            switch (viewType) {
                case ITEM:
                    view = inflater.inflate(R.layout.jj_item_menu_view, parent, false);
                    view.setId(viewType);
                    break;
                case LOADING:
                    view = inflater.inflate(R.layout.jj_item_progress, parent, false);
                    view.setId(viewType);
                    break;

            }

            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {


            DisplayMetrics displaymetrics = new DisplayMetrics();

            ((Activity)mContext).getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);


            switch (getItemViewType(position)) {
                case ITEM:
                    DataTable dataTable = appMenu.get(position);
                    if (dataTable != null && dataTable.getDataItens() != null) {

                        boolean separator = factory.getValueFieldName(dataTable, "MNU_SEPARATOR", getElement()).equals("1");

                        for (int ind = 0; ind < dataTable.getDataItens().size(); ind++) {
                            DataItem dataTableItem = dataTable.getDataItens().get(ind);
                            FormElementField form = getElement().getFormFields().get(ind);
                            if(form.getFieldname().equals("MNU_LABEL")){
                                holder.mMenuNameTextView.setText(dataTableItem.getValue().toString());
                            } else if(form.getFieldname().equals("MNU_ICON")){
                                TIcon icon = (TIcon.values()[Integer.parseInt(dataTableItem.getValue().toString())]);
                                JJIcon jjIcon = new JJIcon(mContext, icon, "#" + Integer.toHexString(mContext.getResources().getColor(R.color.menu_icon)));
                                View viewIcon = jjIcon.renderView(fontSizeDefault);

                                if(listJJIcon.size() > (position + 1)){
                                    listJJIcon.set(position + 1, viewIcon);
                                } else {
                                    listJJIcon.add(position + 1, viewIcon);
                                }
                                holder.mContainerIconRelativeLayout.addView(viewIcon);
                            }
                        }

                        if(positionView.size() > (position + 1)){
                            positionView.set(position + 1, holder.itemView);
                        } else {
                            positionView.add(holder.itemView);
                        }

                        if(separator){
                            holder.mLineLinearLayout.setVisibility(View.VISIBLE);
                        } else {
                            holder.mLineLinearLayout.setVisibility(View.GONE);
                        }
                    }
                    break;
            }

        }


        @Override
        public int getItemCount() {
            return appMenu.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            private LinearLayout mItemLinearLayout;
            private LinearLayout mContainerIconRelativeLayout;
            private LinearLayout mLineLinearLayout;
            private TextView mMenuNameTextView;


            public ViewHolder(View itemView) {
                super(itemView);

                switch (itemView.getId()) {
                    case ITEM:
                        mItemLinearLayout = itemView.findViewById(R.id.item_menu_linear_layout);
                        mMenuNameTextView = itemView.findViewById(R.id.name_menu_text_View);
                        mContainerIconRelativeLayout = itemView.findViewById(R.id.container_icon_linear_layout);
                        mLineLinearLayout = itemView.findViewById(R.id.line_item_linear_layout);
                        break;
                }
            }
        }

        @Override
        public int getItemViewType(int position) {
            return ITEM;
        }
    }



    private class JJGridButtonAdapter extends ArrayAdapter<DataTable> {

        public JJGridButtonAdapter(Context context, ArrayList<DataTable> options) {
            super(context, 0, options);
        }


        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            DataTable dataTable = getItem(position);

            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(
                        R.layout.jj_button_grid_item, parent, false);
            }

            TextView menuNameTextView = convertView.findViewById(R.id.name_menu_text_View);
            LinearLayout containerIconRelativeLayout = convertView.findViewById(R.id.container_icon_linear_layout);
            containerIconRelativeLayout.removeAllViews();

            if (dataTable != null && dataTable.getDataItens() != null) {

                for (int ind = 0; ind < dataTable.getDataItens().size(); ind++) {
                    DataItem dataTableItem = dataTable.getDataItens().get(ind);
                    FormElementField form = getElement().getFormFields().get(ind);
                    if(form.getFieldname().equals("MNU_LABEL")){
                        menuNameTextView.setText(dataTableItem.getValue().toString());
                    } else if(form.getFieldname().equals("MNU_ICON") ){
                        TIcon icon = (TIcon.values()[Integer.parseInt(dataTableItem.getValue().toString())]);

                        JJIcon jjIcon = new JJIcon(mContext, icon, String.format("#%06x", mContext.getResources().getColor(R.color.home_menu_text_color) & 0xffffff));
                        containerIconRelativeLayout.addView(jjIcon.renderView(fontSizeButtonDefault));
                    }
                }

            }


            return convertView;
        }
    }


    /**
     * Classe efetua o carregamento dos dados da lista (online/offline)
     */
    public class AsyncTaskDataLoading extends AsyncTask<Void, Void, ArrayList<DataTable>> {

        private OnAsyncResponse onAsyncResponse;

        public AsyncTaskDataLoading(OnAsyncResponse onAsyncResponse) {
            this.onAsyncResponse = onAsyncResponse;
        }

        @Override
        protected ArrayList<DataTable> doInBackground(Void... params) {
            ArrayList<DataTable> objects;
                 if(factory == null)
                     factory = new Factory(getContext());

                 objects  = factory.getDataTable(getElement(), getFilter(), getOrderBy(), false, Config.SIZE_PAGE, 1, 0);
            return objects;
        }

        @Override
        protected void onPostExecute(ArrayList<DataTable> objects) {
            onAsyncResponse.processFinish(objects);
        }
    }

    public interface OnAsyncResponse {
        void processFinish(ArrayList<DataTable> objects);
    }

    public interface OnClickList{
        void onClick(View view, int position);
    }

}

