package br.com.jjconsulting.mobile.dansales;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import br.com.jjconsulting.mobile.dansales.base.BaseActivity;
import br.com.jjconsulting.mobile.dansales.model.Perfil;
import br.com.jjconsulting.mobile.dansales.model.RotaGuiadaTabType;
import br.com.jjconsulting.mobile.dansales.model.Rotas;
import br.com.jjconsulting.mobile.dansales.service.Current;
import br.com.jjconsulting.mobile.dansales.util.RotaGuiadaUtils;
import br.com.jjconsulting.mobile.dansales.viewModel.RotaViewModel;
import br.com.jjconsulting.mobile.jjlib.OnPageSelected;
import br.com.jjconsulting.mobile.jjlib.util.Config;
import br.com.jjconsulting.mobile.jjlib.util.FormatUtils;
import br.com.jjconsulting.mobile.jjlib.util.LogUser;
import br.com.jjconsulting.mobile.jjlib.util.TextUtils;

public class RotaGuiadaDetailActivity extends BaseActivity {

    private static final String KEY_LIST_ROTA_STATE = "list_rota_state";
    private static final String ARG_ROTA_GUIADA = "rota_guiada";
    private static final String ARG_ROTA_GUIADA_BLOCKED = "rota_guiada_blocked";
    private static final String ARG_PLANEJAMENTO_ROTA_GUIADA_VISITA_PROMOTOR = "planejamento_rota_guiada";
    private static final String ARG_PLANEJAMENTO_ROTA_GUIADA_PROMOTOR = "planejamento_rota_guiada_promotor";

    public static boolean isUpdate;

    public boolean isEdit;
    public boolean isForceBlocked;

    private ArrayList<RotaGuiadaTabType> arrayRotaGuiadaTab;

    private RotaGuiadaFragmentPagerAdapter mRotaGuiadaFragmentPagerAdapter;
    private TabLayout mRotaGuiadaTabLayout;
    private ViewPager mRotaGuiadaViewPager;
    private ProgressDialog progressDialog;

    private RotaViewModel mRotaViewModelProviders;

    public static Intent newIntent(Context context, Rotas rota) {
        Intent intent = new Intent(context, RotaGuiadaDetailActivity.class);
        intent.putExtra(ARG_ROTA_GUIADA, rota);
        return intent;
    }

    public static Intent newIntent(Context context, Rotas rota, boolean isBlocked) {
        Intent intent = new Intent(context, RotaGuiadaDetailActivity.class);
        intent.putExtra(ARG_ROTA_GUIADA, rota);
        intent.putExtra(ARG_ROTA_GUIADA_BLOCKED, isBlocked);

        return intent;
    }

    public static Intent newIntent(Context context, Rotas rota, String promotor, boolean isVisitaPromotor, boolean isBlocked) {
        Intent intent = new Intent(context, RotaGuiadaDetailActivity.class);
        intent.putExtra(ARG_ROTA_GUIADA, rota);
        intent.putExtra(ARG_ROTA_GUIADA_BLOCKED, isBlocked);
        intent.putExtra(ARG_PLANEJAMENTO_ROTA_GUIADA_VISITA_PROMOTOR, isVisitaPromotor);
        intent.putExtra(ARG_PLANEJAMENTO_ROTA_GUIADA_PROMOTOR, promotor);

        return intent;
    }


    public RotaGuiadaDetailActivity() {

    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rota_guiada_detail);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.title_rota_guiada_detail));
        getSupportActionBar().setElevation(0);

        mRotaViewModelProviders = ViewModelProviders.of(this).get(RotaViewModel.class);
        mRotaViewModelProviders.getIsFirstOpenPopup().setValue(false);

        Rotas rotas;
        boolean isVisitaPromotor;
        String promotor = null;

        if (savedInstanceState != null && savedInstanceState.containsKey(KEY_LIST_ROTA_STATE)) {
            rotas = (Rotas) savedInstanceState.getSerializable(KEY_LIST_ROTA_STATE);
            isForceBlocked = savedInstanceState.getBoolean(ARG_ROTA_GUIADA);
            isVisitaPromotor =  savedInstanceState.getBoolean(ARG_PLANEJAMENTO_ROTA_GUIADA_VISITA_PROMOTOR);

            if(savedInstanceState.containsKey(ARG_PLANEJAMENTO_ROTA_GUIADA_PROMOTOR)){
                promotor =  savedInstanceState.getString(ARG_PLANEJAMENTO_ROTA_GUIADA_PROMOTOR);
            }

        } else {
            rotas = (Rotas) getIntent().getSerializableExtra(ARG_ROTA_GUIADA);
            isForceBlocked =  getIntent().getBooleanExtra(ARG_ROTA_GUIADA_BLOCKED, false);
            isVisitaPromotor =  getIntent().getBooleanExtra(ARG_PLANEJAMENTO_ROTA_GUIADA_VISITA_PROMOTOR, false);


            if(getIntent().hasExtra(ARG_PLANEJAMENTO_ROTA_GUIADA_PROMOTOR)){
                promotor =  getIntent().getStringExtra(ARG_PLANEJAMENTO_ROTA_GUIADA_PROMOTOR);
            }
        }

        if(!TextUtils.isNullOrEmpty(promotor)){
            mRotaViewModelProviders.getPromotor().setValue(promotor);
        }

        mRotaViewModelProviders.getRotas().setValue(rotas);
        mRotaViewModelProviders.getIsVisitaPromotor().setValue(isVisitaPromotor);


        mRotaGuiadaTabLayout = findViewById(R.id.rota_guiada_tab_layout);
        mRotaGuiadaViewPager = findViewById(R.id.rota_guiada_view_pager);

        setEdit();
        createDialogProgress();
        createArrayViewPager();

        if(RotaGuiadaUtils.checkValidRota(this, rotas, isEdit)) {
            mRotaGuiadaFragmentPagerAdapter = new RotaGuiadaFragmentPagerAdapter(getSupportFragmentManager());
            mRotaGuiadaViewPager.setAdapter(mRotaGuiadaFragmentPagerAdapter);
            mRotaGuiadaTabLayout.setupWithViewPager(mRotaGuiadaViewPager);
            addListener();
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mRotaViewModelProviders.getRotas().getValue() != null) {
            outState.putSerializable(KEY_LIST_ROTA_STATE, mRotaViewModelProviders.getRotas().getValue());
            outState.putSerializable(ARG_ROTA_GUIADA_BLOCKED, isForceBlocked);
            outState.putSerializable(ARG_PLANEJAMENTO_ROTA_GUIADA_VISITA_PROMOTOR, mRotaViewModelProviders.getIsVisitaPromotor().getValue());
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                return true;
            case R.id.action_save:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setEdit(){
        try {
            Perfil perfil = Current.getInstance(this).getUsuario().getPerfil();
            Rotas rotas = mRotaViewModelProviders.getRotas().getValue();

            if(perfil.isRotaPermiteCheckinChekout() && FormatUtils.toDate(rotas.getDate()).equals(FormatUtils.toDateTimeFixed())){
                isEdit = true;
            } else {
                isEdit = false;
            }
        } catch (Exception ex){
            LogUser.log(Config.TAG, ex.toString());
        }

        if(isForceBlocked){
            isEdit = !isForceBlocked;
        }
    }

    private void addListener() {
        mRotaGuiadaViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset,
                                       int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                switch (RotaGuiadaTabType.getRotaGuiadaTabType(position)){
                    case CLIENTE_FRAGMENT:
                        break;
                    case RESUMO_FRAGMENT:
                        Rotas rotas = mRotaViewModelProviders.getRotas().getValue();


                        RotaGuiadaResumoFragment rotaGuiadaResumoFragment = ((RotaGuiadaResumoFragment) mRotaGuiadaFragmentPagerAdapter
                                .getFragment(position));

                        if(rotas != null && rotaGuiadaResumoFragment != null){
                            rotaGuiadaResumoFragment.setEditPause(rotas.getStatus() == RotaGuiadaUtils.STATUS_RG_PAUSADO ? false:isEdit);
                        }
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    private void createArrayViewPager() {
        arrayRotaGuiadaTab = new ArrayList<>();
        arrayRotaGuiadaTab.add(RotaGuiadaTabType.CLIENTE_FRAGMENT);

        if(!isForceBlocked){
            arrayRotaGuiadaTab.add(RotaGuiadaTabType.RESUMO_FRAGMENT);
        }
    }

    private void createDialogProgress() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        String msg = getString(R.string.aguarde);
        progressDialog.setMessage(msg);
    }

    public class RotaGuiadaFragmentPagerAdapter extends FragmentPagerAdapter {

        private Map<Integer, String> mFragmentTags;
        private FragmentManager mFragmentManager;

        public RotaGuiadaFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
            mFragmentTags = new HashMap<>();
            mFragmentManager = fm;
        }

        @Override
        public Fragment getItem(int position) {
            switch (arrayRotaGuiadaTab.get(position)) {
                case CLIENTE_FRAGMENT:
                    return RotaGuiadaClienteFragment.newInstance(mRotaGuiadaViewPager, isEdit);
                case RESUMO_FRAGMENT:
                    return RotaGuiadaResumoFragment.newInstance(mRotaGuiadaViewPager, isEdit);
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return arrayRotaGuiadaTab.size();
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
            switch (arrayRotaGuiadaTab.get(position)) {
                case CLIENTE_FRAGMENT:
                    return getString(R.string.tab_rota_guiada_cliente);
                case RESUMO_FRAGMENT:
                    return getString(R.string.tab_rota_guiada_resumo);
                default:
                    return null;
            }
        }

        public Fragment getFragment(int position) {
            String tag = mFragmentTags.get(position);
            if (tag == null)
                return null;
            return mFragmentManager.findFragmentByTag(tag);
        }
    }

}
