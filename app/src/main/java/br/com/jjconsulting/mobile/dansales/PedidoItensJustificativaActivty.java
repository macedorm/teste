package br.com.jjconsulting.mobile.dansales;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import br.com.jjconsulting.mobile.dansales.adapter.ItensPedidoJustAdapter;
import br.com.jjconsulting.mobile.dansales.base.BaseActivity;
import br.com.jjconsulting.mobile.dansales.database.RotaGuiadaDao;
import br.com.jjconsulting.mobile.dansales.model.ItemPedido;
import br.com.jjconsulting.mobile.dansales.model.MultiValues;
import br.com.jjconsulting.mobile.dansales.model.Pesquisa;
import br.com.jjconsulting.mobile.dansales.model.TMultiValuesType;
import br.com.jjconsulting.mobile.dansales.service.CurrentActionPedido;
import br.com.jjconsulting.mobile.dansales.util.DialogsMultiValue;
import br.com.jjconsulting.mobile.jjlib.base.ItemClickSupport;
import br.com.jjconsulting.mobile.jjlib.util.DialogsCustom;

public class PedidoItensJustificativaActivty extends BaseActivity {

    public static final String ARG_JUST = "justificativa";
    private static final String ARG_ITENS = "itens_justificativa";


    private ArrayList<ItemPedido> mItens;

    private ItensPedidoJustAdapter mItensPedidoAdapter;
    private RecyclerView mItensPedidoRecyclerView;
    private ViewGroup mNoItemsViewGroup;

    public PedidoItensJustificativaActivty() {
    }

    /**
     * Use it to create/insert a new item into the pedido.
     */
    public static Intent newIntent(Context context, ArrayList<ItemPedido> itens) {
        Intent intent = new Intent(context, PedidoItensJustificativaActivty.class);

        Gson gson = new Gson();
        String itensJson = gson.toJson(itens);
        intent.putExtra(ARG_ITENS, itensJson);
        return intent;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.edit_pedido_menu, menu);
        MenuItem menuSave = menu.findItem(R.id.action_save);
        menuSave.setVisible(true);
        MenuItem menuDelete = menu.findItem(R.id.action_delete);
        menuDelete.setVisible(false);
        MenuItem menuLog = menu.findItem(R.id.action_log);
        menuLog.setVisible(false);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                checkJustificativa(true);
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedido_itens_justicativa);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.title_pedido_justificativa));

        Gson gson = new Gson();
        String json = getIntent().getStringExtra(ARG_ITENS);
        mItens = gson.fromJson(json, new TypeToken<List<ItemPedido>>(){}.getType());

        mItensPedidoRecyclerView = findViewById(R.id.itens_pedido_recycler_view);
        mNoItemsViewGroup = findViewById(R.id.no_items_view_group);

        mItensPedidoAdapter = new ItensPedidoJustAdapter(this, mItens);

        mItensPedidoRecyclerView.setAdapter(mItensPedidoAdapter);
        mItensPedidoRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        DividerItemDecoration divider = new DividerItemDecoration(
                mItensPedidoRecyclerView.getContext(), DividerItemDecoration.VERTICAL);
        divider.setDrawable(getResources().getDrawable(R.drawable.custom_divider));
        mItensPedidoRecyclerView.addItemDecoration(divider);

        ItemClickSupport.addTo(mItensPedidoRecyclerView).setOnItemClickListener(
                (recyclerView, position, v) -> {
                    mItens.get(position);

                    DialogsMultiValue dialogsMultiValue = new DialogsMultiValue(this);
                    dialogsMultiValue.showDialogSpinner(TMultiValuesType.RG_ITEM_PEDIDO_JUST, getString(R.string.valid_checkout_success), dialogsMultiValue.DIALOG_TYPE_WARNING, new DialogsMultiValue.OnClickDialogMessage() {
                        @Override
                        public void onClick(MultiValues multiValues) {
                            mItens.get(position).setMultiValues(multiValues);
                            mItensPedidoAdapter.notifyItemChanged(position);
                        }
                    });
                });



        setComponentsVisibility();
    }


    @Override
    public void onBackPressed()
    {
        checkJustificativa(false);
    }

    private void checkJustificativa(boolean isSend){
        boolean isOK = true;
        Hashtable justificativa = new Hashtable();

        for(ItemPedido itemPedido: mItens){
            if(itemPedido.getMultiValues() == null){
                isOK = false;

                break;
            } else {
                justificativa.put(itemPedido.getProduto().getCodigo(), itemPedido.getMultiValues().getValCod());
            }
        }

        if(!isOK){
            dialogsDefault.showDialogQuestion(getString(R.string.message_n_justificado), dialogsDefault.DIALOG_TYPE_WARNING, new DialogsCustom.OnClickDialogQuestion() {
                @Override
                public void onClickPositive() {

                }

                @Override
                public void onClickNegative() {
                    finish();
                }
            });
        } else {
            Intent intent=new Intent();
            intent.putExtra(ARG_JUST, justificativa);
            setResult(isSend ? RESULT_OK:RESULT_CANCELED, intent);
            finish();
        }
    }

    private void setComponentsVisibility() {
        boolean hasItems = mItens.size() > 0;
        mNoItemsViewGroup.setVisibility(hasItems ? View.GONE : View.VISIBLE);
        mItensPedidoRecyclerView.setVisibility(hasItems ? View.VISIBLE : View.INVISIBLE);
    }
}
