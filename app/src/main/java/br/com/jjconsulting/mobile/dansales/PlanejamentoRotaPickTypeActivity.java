package br.com.jjconsulting.mobile.dansales;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import br.com.jjconsulting.mobile.dansales.base.BaseActivity;

public class PlanejamentoRotaPickTypeActivity extends BaseActivity implements View.OnClickListener {

    public static Intent newIntent(Context packageContext) {
        return new Intent(packageContext, PlanejamentoRotaPickTypeActivity.class);
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        setContentView(R.layout.activity_planejamento_rota_pick_type);

        TextView visitaClienteTextView = findViewById(R.id.add_visita_text_view);
        visitaClienteTextView.setOnClickListener(this);

        TextView visitaClientePromotorTextView = findViewById(R.id.add_visita_promotor_text_view);
        visitaClientePromotorTextView.setOnClickListener(this);

        TextView atividadeInternaTextView = findViewById(R.id.add_atividade_text_view);
        atividadeInternaTextView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        startActivity(PlanejamentoRotaAddActivity.newIntent(this));
    }
}

