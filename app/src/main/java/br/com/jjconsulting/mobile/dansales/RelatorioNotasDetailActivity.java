package br.com.jjconsulting.mobile.dansales;

import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.ViewGroup;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.jjconsulting.mobile.dansales.asyncTask.AsyncTaskRelatorioNotasItens;
import br.com.jjconsulting.mobile.dansales.database.RelatorioNotasItensDao;
import br.com.jjconsulting.mobile.dansales.model.RelatorioNotas;
import br.com.jjconsulting.mobile.dansales.model.RelatorioNotasItem;
import br.com.jjconsulting.mobile.jjlib.OnPageSelected;

public class RelatorioNotasDetailActivity extends AppCompatActivity implements AsyncTaskRelatorioNotasItens.OnAsyncResponse {

    private static final String KEY_NOTAS_STATE = "filter_notas_state";
    private RelatorioNotasFragmentPagerAdapter mRelatorioNotasFragmentPagerAdapter;
    private ViewGroup mRootView;
    private TabLayout mRelatorioNotasTabLayout;
    private ViewPager mRelatorioNotasViewPager;
    private RelatorioNotas mRelatorioNotas;
    private AsyncTaskRelatorioNotasItens asyncTaskRelatorioNotasItens;
    private RelatorioNotasDetailCabecalhoFragment relatorioNotasDetailCabecalhoFragment;
    private RelatorioNotasDetailItensFragment relatorioNotasDetailItensFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_relatorio_notas_detail);

        getSupportActionBar().setSubtitle(getString(R.string.title_relatorios));

        Intent intent = getIntent();

        mRelatorioNotas = (RelatorioNotas) intent.getSerializableExtra(KEY_NOTAS_STATE);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.title_relatorio_notas));
        // It must be done for good visual style using TabLayout + ViewPager
        getSupportActionBar().setElevation(0);


        mRootView = findViewById(R.id.relatorio_notas_linear_layout);
        mRelatorioNotasTabLayout = findViewById(R.id.relatorio_notas_tab_layout);
        mRelatorioNotasViewPager = findViewById(R.id.relatorio_notas_view_pager);

        mRelatorioNotasViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset,
                                       int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                OnPageSelected listener = (OnPageSelected) mRelatorioNotasFragmentPagerAdapter
                        .getFragment(position);
                if (listener != null) {
                    listener.onPageSelected(position);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        loadNotasItens();

    }

    private class RelatorioNotasFragmentPagerAdapter extends FragmentPagerAdapter {

        private static final int FRAGMENT_COUNT = 2;
        private static final int CABECALHO_FRAGMENT = 0;
        private static final int SKU_FRAGMENT = 1;

        private Map<Integer, String> mFragmentTags;
        private FragmentManager mFragmentManager;

        public RelatorioNotasFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
            mFragmentTags = new HashMap<>();
            mFragmentManager = fm;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case CABECALHO_FRAGMENT:
                    relatorioNotasDetailCabecalhoFragment = RelatorioNotasDetailCabecalhoFragment.newInstance(mRelatorioNotas);
                    return relatorioNotasDetailCabecalhoFragment;
                case SKU_FRAGMENT:
                    relatorioNotasDetailItensFragment = RelatorioNotasDetailItensFragment.newInstance(mRelatorioNotas);
                    return relatorioNotasDetailItensFragment;
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return FRAGMENT_COUNT;
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
            switch (position) {
                case CABECALHO_FRAGMENT:
                    return getString(R.string.tab_relatorio_notas_cabecalho);
                case SKU_FRAGMENT:
                    return getString(R.string.tab_relatorio_notas_sku);
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
    public void processFinish(List<RelatorioNotasItem> sku) {
        mRelatorioNotas.setSku(sku);
        somaValorICMS(sku);
        mRelatorioNotasFragmentPagerAdapter = new RelatorioNotasFragmentPagerAdapter(getSupportFragmentManager());
        mRelatorioNotasViewPager.setAdapter(mRelatorioNotasFragmentPagerAdapter);
        mRelatorioNotasTabLayout.setupWithViewPager(mRelatorioNotasViewPager);
    }


    private void loadNotasItens() {

        if (asyncTaskRelatorioNotasItens != null) {
            asyncTaskRelatorioNotasItens.cancel(true);
        }

        RelatorioNotasItensDao mSKUDao = new RelatorioNotasItensDao(this);
        asyncTaskRelatorioNotasItens = new AsyncTaskRelatorioNotasItens(this, -1, mRelatorioNotas.getNumero(), mRelatorioNotas.getSerie(), mSKUDao, this);
        asyncTaskRelatorioNotasItens.execute();
    }

    private void somaValorICMS(List<RelatorioNotasItem> sku) {

        double icmsTotal = 0;
        double valorTotal = 0;

        for (RelatorioNotasItem item : sku) {
            double icms = 0;
            double icmst = 0;

            icms = item.getVlrIcms();
            icmst = item.getIcmsSt();
            valorTotal += item.getValorTotal();

            icmsTotal += (icms + icmst);

        }

        mRelatorioNotas.setIcmsTotal(icmsTotal);
        mRelatorioNotas.setValor(valorTotal);
    }
}
