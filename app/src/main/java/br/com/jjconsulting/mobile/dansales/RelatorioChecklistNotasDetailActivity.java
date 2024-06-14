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

import br.com.jjconsulting.mobile.dansales.adapter.PesquisaNotasAdapter;
import br.com.jjconsulting.mobile.dansales.base.BaseActivity;
import br.com.jjconsulting.mobile.dansales.data.SyncPesquisa;
import br.com.jjconsulting.mobile.dansales.data.SyncPesquisaPilar;
import br.com.jjconsulting.mobile.dansales.service.Current;

public class RelatorioChecklistNotasDetailActivity extends BaseActivity {

    private static final String ARG_CODIGO_CLIENTE = "codigo_cliente";
    private static final String ARG_PESQUISA_PILAR = "objeto_pesquisa_pilar";

    private static ArrayList<SyncPesquisaPilar> mPesquisaPilar;

    private static SyncPesquisa mSyncPesquisa;

    private RecyclerView mPerguntasRecyclerView;
    private PesquisaNotasAdapter mPerguntasAdapter;

    private LinearLayout mListEmptyLinearLayout;
    private ProgressDialog progressDialog;

    private TextView mPesquisaNotaTextView;

    private Button mCloseButton;

    private String codigoCliente;

    public static Intent newIntent(Context context, String codigoCliente, SyncPesquisa syncPesquisa) {
        Intent intent = new Intent(context, RelatorioChecklistNotasDetailActivity.class);
        intent.putExtra(ARG_CODIGO_CLIENTE, codigoCliente);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        mPesquisaPilar = syncPesquisa.getListPilar();
        mSyncPesquisa = syncPesquisa;
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checklist_detail_nota);
        getSupportActionBar().setTitle(getString(R.string.title_relatorio_check_list_nota));

        codigoCliente = getIntent().getStringExtra(ARG_CODIGO_CLIENTE);

        createDialogProgress();

        if(mPesquisaPilar == null || mSyncPesquisa == null){
            finish();
        } else {
            mPerguntasRecyclerView = findViewById(R.id.pesquisa_detail_recycler_view);
            mListEmptyLinearLayout = findViewById(R.id.list_empty_text_view);

            mPesquisaNotaTextView = findViewById(R.id.pesquisa_nota_text_view);
            mPesquisaNotaTextView.setText(String.valueOf(mSyncPesquisa.getNotaFiscal()));

            mCloseButton = findViewById(R.id.pesquisa_note_close_button);
            mCloseButton.setOnClickListener((v)-> { finish();

            });

            mPerguntasAdapter = new PesquisaNotasAdapter(RelatorioChecklistNotasDetailActivity.this, mPesquisaPilar);
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


    private void createDialogProgress() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getString(R.string.aguarde));
    }

}
