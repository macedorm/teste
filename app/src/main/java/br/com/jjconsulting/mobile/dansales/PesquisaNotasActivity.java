package br.com.jjconsulting.mobile.dansales;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Date;

import br.com.jjconsulting.mobile.dansales.adapter.PesquisaNotasAdapter;
import br.com.jjconsulting.mobile.dansales.base.BaseActivity;
import br.com.jjconsulting.mobile.dansales.data.SyncPesquisa;
import br.com.jjconsulting.mobile.dansales.data.SyncPesquisaPilar;
import br.com.jjconsulting.mobile.dansales.service.Current;
import br.com.jjconsulting.mobile.jjlib.util.FormatUtils;

public class PesquisaNotasActivity extends BaseActivity {

    private static final String KEY_PILAR_STATE = "key_pilar_state";

    private static final String ARG_CODIGO_CLIENTE = "codigo_cliente";
    private static final String ARG_PESQUISA_DATE = "objeto_pesquisa_date";
    private static final String ARG_PESQUISA = "objeto_pesquisa_id";

    private static ArrayList<SyncPesquisaPilar> mPesquisaPilar;

    private static SyncPesquisa mSyncPesquisa;

    private RecyclerView mPerguntasRecyclerView;
    private PesquisaNotasAdapter mPerguntasAdapter;

    private LinearLayout mListEmptyLinearLayout;
    private ProgressDialog progressDialog;

    private TextView mPesquisaNotaTextView;

    private Button mCloseButton;

    private String codigoCliente;

    private Date currentDate;

    private int idPesquisa;

    public static Intent newIntent(Context context, String codigoCliente, int idPesquisa, Date date, SyncPesquisa syncPesquisa) {
        Intent intent = new Intent(context, PesquisaNotasActivity.class);
        intent.putExtra(ARG_CODIGO_CLIENTE, codigoCliente);
        intent.putExtra(ARG_PESQUISA, idPesquisa);
        intent.putExtra(ARG_PESQUISA_DATE, FormatUtils.toTextToCompareDateInSQlite(date));

        mPesquisaPilar = syncPesquisa.getListPilar();
        mSyncPesquisa = syncPesquisa;

        return intent;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if(mPesquisaPilar != null){
            //outState.putSerializable(KEY_PILAR_STATE, mPesquisaPilar);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nota_pesquisa);
        getSupportActionBar().setTitle(getString(R.string.title_check_list));

        codigoCliente = getIntent().getStringExtra(ARG_CODIGO_CLIENTE);
        try {
            currentDate = FormatUtils.toDate(getIntent().getStringExtra(ARG_PESQUISA_DATE));
        } catch (Exception ex){
            currentDate = new Date();
        }

        idPesquisa = getIntent().getIntExtra(ARG_PESQUISA, 0);

        createDialogProgress();

        mPerguntasRecyclerView = findViewById(R.id.pesquisa_detail_recycler_view);
        mListEmptyLinearLayout = findViewById(R.id.list_empty_text_view);

        mPesquisaNotaTextView = findViewById(R.id.pesquisa_nota_text_view);


        mPesquisaNotaTextView.setText(String.valueOf(mSyncPesquisa != null ? mSyncPesquisa.getNotaFiscal():""));

        mCloseButton = findViewById(R.id.pesquisa_note_close_button);
        mCloseButton.setOnClickListener((v)-> { finish();

        });


        if(mPesquisaPilar == null){
            finish();
        } else {
            mPerguntasAdapter = new PesquisaNotasAdapter(PesquisaNotasActivity.this, mPesquisaPilar);
            mPerguntasRecyclerView.setAdapter(mPerguntasAdapter);
            mPerguntasRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

            if (mPesquisaPilar.size() == 0) {
                mListEmptyLinearLayout.setVisibility(View.VISIBLE);
                mPerguntasRecyclerView.setVisibility(View.GONE);
            } else {
                mListEmptyLinearLayout.setVisibility(View.GONE);
                mPerguntasRecyclerView.setVisibility(View.VISIBLE);
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.pesquisa_nota_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_legenda:
                showLegenda();
                return true;
            case R.id.action_resume:
                showDialogResume();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void showDialogResume() {
        String unidadeNegocio = Current.getInstance(this).getUnidadeNegocio().getCodigo();
        String codigoUsuario = Current.getInstance(this).getUsuario().getCodigo();

        PesquisaResumoDialog pesquisaResumoDialog = new PesquisaResumoDialog(this, false,  codigoCliente, codigoUsuario, idPesquisa, currentDate, unidadeNegocio);
        pesquisaResumoDialog.show();
    }

    private void showLegenda() {
        Dialog mDialogSubtitles = new Dialog(this,
                android.R.style.Theme_Translucent_NoTitleBar);
        mDialogSubtitles.setCancelable(true);
        mDialogSubtitles.setContentView(R.layout.dialog_legenda_notas_checklist);

        TextView tvOkSubTitles = mDialogSubtitles.findViewById(R.id.ok_button);
        tvOkSubTitles.setOnClickListener(view -> mDialogSubtitles.dismiss());

        mDialogSubtitles.show();
    }

    private void createDialogProgress() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getString(R.string.aguarde));
    }

}
