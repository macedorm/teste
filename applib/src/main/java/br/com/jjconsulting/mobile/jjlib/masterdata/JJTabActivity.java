package br.com.jjconsulting.mobile.jjlib.masterdata;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.VolleyError;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import br.com.jjconsulting.mobile.jjlib.Connection;
import br.com.jjconsulting.mobile.jjlib.OnPageSelected;
import br.com.jjconsulting.mobile.jjlib.R;
import br.com.jjconsulting.mobile.jjlib.connection.SyncFieldsConnection;
import br.com.jjconsulting.mobile.jjlib.dao.entity.ElementRelation;
import br.com.jjconsulting.mobile.jjlib.dao.entity.ElementRelationColumn;
import br.com.jjconsulting.mobile.jjlib.dao.entity.Factory;
import br.com.jjconsulting.mobile.jjlib.dao.entity.FormElement;
import br.com.jjconsulting.mobile.jjlib.dao.entity.TLoadingDataType;
import br.com.jjconsulting.mobile.jjlib.dao.entity.TPageState;
import br.com.jjconsulting.mobile.jjlib.dao.entity.TRelationView;
import br.com.jjconsulting.mobile.jjlib.model.DataTable;
import br.com.jjconsulting.mobile.jjlib.model.ValidationLetter;
import br.com.jjconsulting.mobile.jjlib.util.Config;
import br.com.jjconsulting.mobile.jjlib.util.DialogsCustom;
import br.com.jjconsulting.mobile.jjlib.util.LogUser;

public class JJTabActivity extends AppCompatActivity implements JJOnUpdate {

    private static final String ARG_DATA_TABLE = "data_table";
    private static final String ARG_FORM_ELEMENT = "form_element";
    private static final String ARG_PAGESTATE = "page_state";
    private static final String ARG_LOADING = "loading";


    private TabFragmentPagerAdapter mTabFragmentPagerAdapter;

    private TabLayout mTabLayout;

    private ViewPager mTabViewPager;

    private DialogsCustom dialogsCustom;

    private JJTabContentView jjTabContentView[];

    private OnPageSelected listener;

    private TPageState pageState;

    private DataTable rowSelected;

    private FormElement mRootElement;

    private String elementName;

    private int positionTabUnselected;
    private int positionTabSelected;

    private boolean isErrorUpdateTab;
    private boolean isAutoReloadFormView;
    private boolean isLoading;

    public static Intent newIntent(Context context, DataTable rowSelected, String element, TPageState pageState) {
        Intent intent = new Intent(context, JJTabActivity.class);
        intent.putExtra(ARG_DATA_TABLE, rowSelected);
        intent.putExtra(ARG_FORM_ELEMENT, element);
        intent.putExtra(ARG_PAGESTATE, pageState.getValue());
        intent.putExtra(ARG_LOADING, false);

        return intent;
    }

    public static Intent newIntent(Context context, DataTable rowSelected, String element, TPageState pageState, boolean isLoading) {
        Intent intent = new Intent(context, JJTabActivity.class);
        intent.putExtra(ARG_DATA_TABLE, rowSelected);
        intent.putExtra(ARG_FORM_ELEMENT, element);
        intent.putExtra(ARG_PAGESTATE, pageState.getValue());
        intent.putExtra(ARG_LOADING, isLoading);

        return intent;
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        isAutoReloadFormView = true;

        mTabFragmentPagerAdapter = new TabFragmentPagerAdapter(getSupportFragmentManager());
        mTabViewPager.setAdapter(mTabFragmentPagerAdapter);

        mTabLayout.setupWithViewPager(mTabViewPager);

        addListener();
        mTabLayout.post(() -> {
            listener = (OnPageSelected) mTabFragmentPagerAdapter
                    .getFragment(0);
            if (listener != null) {
                listener.onPageSelected(positionTabSelected);
            }
        });

        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                positionTabSelected = tab.getPosition();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                positionTabUnselected = tab.getPosition();
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setElevation(0);

        getSupportActionBar().setTitle(mRootElement.getTitle());

        if (rowSelected.getDataItens() != null) {
            for (int ind = 0; ind < mRootElement.getFormFields().size(); ind++) {
                if (mRootElement.getFormFields().get(ind).getIspk()) {
                    getSupportActionBar().setSubtitle(rowSelected.getDataItens().get(ind).getValue().toString());
                    ind = mRootElement.getFormFields().size();
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.jj_tab_activity);

        Toolbar mToolbar = findViewById(R.id.master_toolbar);
        setSupportActionBar(mToolbar);

        rowSelected = (DataTable) getIntent().getSerializableExtra(ARG_DATA_TABLE);
        elementName = getIntent().getStringExtra(ARG_FORM_ELEMENT);
        pageState = TPageState.fromInteger(getIntent().getIntExtra(ARG_PAGESTATE, TPageState.VIEW.getValue()));
        isLoading = getIntent().getBooleanExtra(ARG_LOADING, false);

        setElement(this, elementName);

        dialogsCustom = new DialogsCustom(this);
        mTabLayout = findViewById(R.id.tab_layout);
        mTabViewPager = findViewById(R.id.tab_view_pager);
        jjTabContentView = new JJTabContentView[mRootElement.getRelations().size() + 1];

        mTabViewPager.setOffscreenPageLimit(mRootElement.getRelations().size() + 1);
    }

    private void addListener() {
        mTabViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset,
                                       int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (isErrorUpdateTab) {
                    isErrorUpdateTab = false;
                } else {

                    listener = (OnPageSelected) mTabFragmentPagerAdapter
                            .getFragment(position);

                    //updateTab(positionTabUnselected);


                    if (jjTabContentView[positionTabUnselected] != null && jjTabContentView[positionTabUnselected].isUpdate()) {
                        updateTab(positionTabUnselected);
                    } else {
                        if (listener != null) {
                            listener.onPageSelected(positionTabSelected);
                        }
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (jjTabContentView[positionTabSelected] != null && jjTabContentView[positionTabSelected].isUpdate()) {
            updateTab(positionTabSelected);
        } else {
            if (isAutoReloadFormView) {
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
            }
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        //getMenuInflater().inflate(R.menu.action_menu_painel, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        jjTabContentView[positionTabSelected].onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onUpdate() {
        isAutoReloadFormView = true;
    }

    public void updateTab(int index) {
        SyncFieldsConnection syncFieldsConnection = new SyncFieldsConnection(this, new SyncFieldsConnection.ConnectionListener() {
            @Override
            public void onSucess(String response, int typeConnection, InputStreamReader reader) {
                try {

                    dialogsCustom.showDialogLoading(false);

                    if (index == 0) {
                        Factory factory = new Factory(JJTabActivity.this);
                        rowSelected = factory.convertHastableInDataTable(jjTabContentView[index].getFormValues(), jjTabContentView[index].getFormElement());
                        jjTabContentView[index].setDataTable(rowSelected);
                    }

                    jjTabContentView[index].setUpdate(false);


                    for (int ind = 0; ind < jjTabContentView.length; ind++) {


                        if (ind == index) {
                        } else {
                            jjTabContentView[ind].setReCreate(true);
                        }

                        if (index == 0 && ind > 0) {
                            ElementRelation elementRelation = mRootElement.getRelations().get(ind - 1);
                            Hashtable relation = getRelationValue(elementRelation);

                            if (relation.size() > 0) {
                                jjTabContentView[ind].setRelationValues(getRelationValue(elementRelation));
                            }
                        }
                    }

                    if (index == positionTabSelected) {
                        Intent intent = new Intent();
                        setResult(RESULT_OK, intent);
                        finish();
                    } else {
                        if (listener != null) {
                            listener.onPageSelected(positionTabSelected);
                        }
                    }

                    if (TLoadingDataType.fromInteger(jjTabContentView[index].getFormElement().getMode()) == TLoadingDataType.OFFLINE) {
                        Factory factory = new Factory(JJTabActivity.this);
                        factory.insert(jjTabContentView[index].getFormElement(), jjTabContentView[index].getFormValues(), null);
                    }

                    isAutoReloadFormView = true;
                } catch (Exception ex) {
                    LogUser.log(Config.TAG, ex.toString());
                    dialogsCustom.showDialogMessage(getString(br.com.jjconsulting.mobile.jjlib.R.string.sync_connection_error), DialogsCustom.DIALOG_TYPE_ERROR, null);
                    backTabPageErrorUpdate(index);
                }
            }

            @Override
            public void onError(int code, VolleyError volleyError, int typeConnection, String response) {
                dialogsCustom.showDialogLoading(false);

                if(code == Connection.AUTH_FAILURE){
                    dialogsCustom.showDialogMessage(getString(br.com.jjconsulting.mobile.jjlib.R.string.title_token_expired), dialogsCustom.DIALOG_TYPE_ERROR, null);
                    return;
                }

                if (code == Connection.NO_CONNECTION) {
                    dialogsCustom.showDialogMessage(getString(br.com.jjconsulting.mobile.jjlib.R.string.sync_connection_error), DialogsCustom.DIALOG_TYPE_ERROR, null);
                } else {
                    try {
                        Gson gson = new Gson();
                        ArrayList<ValidationLetter> retProcess = gson.fromJson(response, new TypeToken<List<ValidationLetter>>() {
                        }.getType());

                        if (retProcess != null) {
                            jjTabContentView[index].showErros(retProcess.get(0).getValidationList());
                        }
                    } catch (Exception ex) {
                        dialogsCustom.showDialogMessage(getString(br.com.jjconsulting.mobile.jjlib.R.string.sync_connection_error), DialogsCustom.DIALOG_TYPE_ERROR, null);
                    }

                    backTabPageErrorUpdate(index);
                }
            }
        });

        dialogsCustom.showDialogLoading(true);
        syncFieldsConnection.insertField(jjTabContentView[index].getFormElement().getName(), jjTabContentView[index].getFormValues());
    }

    private void backTabPageErrorUpdate(int index) {
        isErrorUpdateTab = true;
        mTabViewPager.setCurrentItem(index);
    }

    public void setElement(Context context, String elementName) {
        Factory factory = new Factory(context);
        this.mRootElement = factory.getFormElement(elementName);
    }

    public Hashtable getRelationValue(ElementRelation elementRelation) {
        Hashtable relationValues = new Hashtable();

        for (ElementRelationColumn elementRelationColumn : elementRelation.getColumns()) {
            int index = mRootElement.getFormFieldIndex(elementRelationColumn.getPkcolumn());
            relationValues.put(elementRelationColumn.getFkcolumn(), rowSelected.getDataItens().get(index).getValue());
        }

        return relationValues;
    }


    private class TabFragmentPagerAdapter extends FragmentPagerAdapter {

        private Map<Integer, String> mFragmentTags;
        private FragmentManager mFragmentManager;

        public TabFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
            mFragmentTags = new HashMap<>();
            mFragmentManager = fm;
        }

        @Override
        public Fragment getItem(int position) {

            switch (position) {
                case 0:
                    jjTabContentView[position] = JJTabContentView.newInstance(JJTabActivity.this, rowSelected, elementName, pageState, TRelationView.UPDATE, position, JJTabActivity.this);
                    jjTabContentView[0].isLoading = isLoading;

                    break;
                default:
                    ElementRelation elementRelation = mRootElement.getRelations().get(position - 1);
                    jjTabContentView[position] = JJTabContentView.newInstance(JJTabActivity.this, elementRelation.getChildElement(), getRelationValue(elementRelation), pageState, TRelationView.fromInteger(elementRelation.viewType), position, JJTabActivity.this);
                    break;
            }
            return jjTabContentView[position];
        }

        @Override
        public int getCount() {
            return mRootElement.getRelations().size() + 1;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Object object = super.instantiateItem(container, position);
            if (object instanceof Fragment) {
                Fragment fragment = (Fragment) object;
                String tag = fragment.getTag();
                mFragmentTags.put(position, tag);
            }
            return object;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if (position == 0) {
                return mRootElement.getTitle();
            } else {
                return mRootElement.getRelations().get(position - 1).getTitle();
            }
        }

        public Fragment getFragment(int position) {
            String tag = mFragmentTags.get(position);
            if (tag == null)
                return null;
            return mFragmentManager.findFragmentByTag(tag);
        }
    }

    public JJTabContentView[] getJjTabContentView() {
        return jjTabContentView;
    }

    public void setJjTabContentView(JJTabContentView[] jjTabContentView) {
        this.jjTabContentView = jjTabContentView;
    }
}
