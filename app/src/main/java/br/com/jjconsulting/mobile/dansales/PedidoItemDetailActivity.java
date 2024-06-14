package br.com.jjconsulting.mobile.dansales;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.textfield.TextInputEditText;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Date;

import br.com.jjconsulting.mobile.dansales.asyncTask.AsyncTaskSearchProduto;
import br.com.jjconsulting.mobile.dansales.base.BaseActivity;
import br.com.jjconsulting.mobile.dansales.business.PedidoBusiness;
import br.com.jjconsulting.mobile.dansales.data.ValidationDan;
import br.com.jjconsulting.mobile.dansales.database.ItemPedidoDao;
import br.com.jjconsulting.mobile.dansales.database.ProdutoDao;
import br.com.jjconsulting.mobile.dansales.model.BatchDAT;
import br.com.jjconsulting.mobile.dansales.model.ItemPedido;
import br.com.jjconsulting.mobile.dansales.model.Pedido;
import br.com.jjconsulting.mobile.dansales.model.PerfilVenda;
import br.com.jjconsulting.mobile.dansales.model.PrecoVenda;
import br.com.jjconsulting.mobile.dansales.model.Produto;
import br.com.jjconsulting.mobile.dansales.model.TipoVenda;
import br.com.jjconsulting.mobile.dansales.model.Usuario;
import br.com.jjconsulting.mobile.dansales.service.Current;
import br.com.jjconsulting.mobile.dansales.util.AndroidUICompoentUtils;
import br.com.jjconsulting.mobile.dansales.util.Config;
import br.com.jjconsulting.mobile.dansales.util.CustomTextInputLayout;
import br.com.jjconsulting.mobile.jjlib.base.SpinnerArrayAdapter;
import br.com.jjconsulting.mobile.jjlib.util.DialogsCustom;
import br.com.jjconsulting.mobile.jjlib.util.FormatUtils;
import br.com.jjconsulting.mobile.jjlib.util.LocaleUtils;
import br.com.jjconsulting.mobile.jjlib.util.LogUser;
import br.com.jjconsulting.mobile.jjlib.util.MathUtils;
import br.com.jjconsulting.mobile.jjlib.util.TextUtils;
import br.com.jjconsulting.mobile.jjlib.util.TextWatcherUtils;

public class PedidoItemDetailActivity extends BaseActivity
        implements ProdutosDialogFragment.OnProdutoClickListener,
        ProdutosDATDialogFragment.OnProdutoDATClickListener {


    public static final String KEY_PRODUCT_EXIST = "key_product";

    private static final int BARCODE_REQUEST_CODE = 1;

    private static final String ARG_PEDIDO = "pedido";
    private static final String ARG_CODIGO_ITEM = "codigo_item";
    private static final String ARG_ULTIMO_PRODUTO_INCLUIDO = "ultimo_produto_incluido";

    private ItemPedido mItem;
    private Pedido mPedido;
    private ItemPedidoDao mItemPedidoDao;
    private ProdutoDao mProdutoDao;
    private PedidoBusiness mPedidoBusiness;
    private Object[] mBatchs;
    private int mSpinnerChangeCounter = 0;

    private TextWatcher mQuantidadeTextWatcher;
    private TextWatcher mDescontoTextWatcher;

    private SpinnerArrayAdapter<BatchDAT> mBatchSpinnerAdapter;
    private Spinner mBatchSpinner;
    private TextInputEditText mIdEditText;
    private TextInputEditText mProdutoEditText;
    private TextInputEditText mQuantidadeEditText;
    private TextInputEditText mDescontoEditText;
    private TextInputEditText mQuantidadeEstoqueEditText;
    private TextInputEditText mQtdPedidoAnteriorEditText;
    private TextInputEditText mQtdPedidoAnteriorLabel;
    private TextInputEditText mItensPorCaixaEditText;
    private TextInputEditText mValorUnitarioEditText;
    private TextInputEditText mQtdMediaPorPedidoEditText;
    private TextInputEditText mValorTotalEditText;
    private TextInputEditText mPesoTotalEditText;
    private TextInputEditText mSortimentoEditText;
    private TextInputEditText mUltimoItemEditText;
    private TextInputEditText mValorGondulaEditText;

    private CustomTextInputLayout mDescontoLabel;
    private CustomTextInputLayout mQuantidadeEstoqueLabel;
    private CustomTextInputLayout mValorUnitarioLabel;
    private CustomTextInputLayout mSortimentoLabel;
    private CustomTextInputLayout mItensPorCaixaLabel;
    private CustomTextInputLayout mQtdMediaPorPedidoLabel;

    private LinearLayout mDataGroup3;
    private LinearLayout mExpirationDateLinearLayout;

    private DatePickerDialog mExpirationDatePickerDialog;
    private DatePickerDialog.OnDateSetListener mExpirationDateSetListener;


    private TextView mBatchLabel;
    private TextView mExpirationTextView;

    private Button mIncluirOuSalvarButton;
    private ImageButton mPesquisaProdutoButton;
    private ViewGroup mGroupData1;
    private LinearLayout mProgressLinearLayout;
    private ScrollView mBaseScrollView;

    private Current current;

    /**
     * Use it to create/insert a new item into the pedido.
     */
    public static Intent newIntent(Context context, Pedido pedido, String ultimoProdutoIncluido) {
        Intent intent = new Intent(context, PedidoItemDetailActivity.class);
        intent.putExtra(ARG_PEDIDO, pedido);
        intent.putExtra(ARG_ULTIMO_PRODUTO_INCLUIDO, ultimoProdutoIncluido);
        return intent;
    }

    /**
     * Use it to edit an item.
     */
    public static Intent newIntent(Context context, Pedido pedido, int codigoItem,
                                   String ultimoProdutoIncluido) {
        Intent intent = new Intent(context, PedidoItemDetailActivity.class);
        intent.putExtra(ARG_PEDIDO, pedido);
        intent.putExtra(ARG_CODIGO_ITEM, codigoItem);
        intent.putExtra(ARG_ULTIMO_PRODUTO_INCLUIDO, ultimoProdutoIncluido);
        return intent;
    }

    public static Intent newResultIntent(int codigoItem) {
        Intent intent = new Intent();
        intent.putExtra(ARG_CODIGO_ITEM, codigoItem);
        return intent;
    }

    public static int getCodigoItemFromResultIntent(Intent intent) {
        return intent.getIntExtra(ARG_CODIGO_ITEM, -1);
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_pedido_item_detail);

        mPesquisaProdutoButton = findViewById(R.id.produto_search_linear_layout);
        mIdEditText = findViewById(R.id.id_produto_edit_text);
        mProdutoEditText = findViewById(R.id.produto_edit_text);
        mQuantidadeEditText = findViewById(R.id.quantidade_edit_text);
        mDescontoEditText = findViewById(R.id.desconto_edit_text);
        mDescontoLabel = findViewById(R.id.desconto_text_input_layout);
        mQuantidadeEstoqueEditText = findViewById(R.id.estoque_edit_text);
        mQuantidadeEstoqueLabel = findViewById(R.id.estoque_text_input_layout);
        mBatchSpinner = findViewById(R.id.batch_spinner);
        mBatchLabel = findViewById(R.id.batch_text_view);
        mQtdPedidoAnteriorEditText = findViewById(R.id.ped_ant_edit_text);
        mQtdPedidoAnteriorLabel = findViewById(R.id.ped_ant_edit_text);
        mItensPorCaixaEditText = findViewById(R.id.itens_caixa_edit_text);
        mItensPorCaixaLabel = findViewById(R.id.itens_caixa_text_input_layout);
        mValorUnitarioEditText = findViewById(R.id.valor_un_edit_text);
        mValorUnitarioLabel = findViewById(R.id.valor_un_text_input_layout);
        mValorTotalEditText = findViewById(R.id.valor_total_edit_text);
        mPesoTotalEditText = findViewById(R.id.peso_total_edit_text);
        mQtdMediaPorPedidoEditText = findViewById(R.id.media_edit_text);
        mQtdMediaPorPedidoLabel = findViewById(R.id.media_text_input_layout);
        mDataGroup3 = findViewById(R.id.dat_group3_view_group);
        mExpirationDateLinearLayout = findViewById(R.id.expiration_date_linearlayout);
        mExpirationTextView = findViewById(R.id.expiration_date_text_view);
        mValorGondulaEditText = findViewById(R.id.valor_gondula_edit_text);
        mValorGondulaEditText.setInputType(InputType.TYPE_CLASS_NUMBER);


        mSortimentoEditText = findViewById(R.id.sor_edit_text);
        mSortimentoLabel = findViewById(R.id.sort_text_input_layout);
        mUltimoItemEditText = findViewById(R.id.ultimo_item_edit_text);
        mIncluirOuSalvarButton = findViewById(R.id.incluir_button);
        mGroupData1 = findViewById(R.id.data_group1);
        mProgressLinearLayout = findViewById(R.id.loading_linear_layout);
        mBaseScrollView = findViewById(R.id.base_scroll_view);

        mItemPedidoDao = new ItemPedidoDao(this);
        mProdutoDao = new ProdutoDao(this);
        mPedidoBusiness = new PedidoBusiness();

        current = Current.getInstance(this);
        mPedido = (Pedido) getIntent().getSerializableExtra(ARG_PEDIDO);


        if (isNew()) {
            mItem = mPedidoBusiness.createNewItem(mItemPedidoDao, current.getUsuario(), mPedido);
        } else {
            mItem = mItemPedidoDao.get(current.getUsuario(), mPedido,
                    getIntent().getIntExtra(ARG_CODIGO_ITEM, -1), true);
        }

        mQuantidadeTextWatcher = TextWatcherUtils.buildWithAfterTextChanged(editable -> {
            if(isReb()){
                processAndShowValorTotalReb(mValorGondulaEditText.getText().toString());
            } else {
                processAndShowValorTotal(editable, mDescontoEditText.getText());
            }
        });

        mDescontoTextWatcher = TextWatcherUtils.buildWithAfterTextChanged(editable ->
                processAndShowValorTotal(mQuantidadeEditText.getText(), editable));

        mBatchSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (mSpinnerChangeCounter > 0 && mBatchs != null) {
                    BatchDAT batchDAT = (BatchDAT) mBatchs[i];
                    if (batchDAT != null) {
                        mQuantidadeEstoqueEditText.setText(
                                String.valueOf(batchDAT.getQuantidadeDisponivel()));
                        processAndShowValorTotal(mQuantidadeEditText.getText(),
                                mDescontoEditText.getText());
                    }
                }

                ++mSpinnerChangeCounter;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        mPesquisaProdutoButton.setOnClickListener(view -> searchProduto());
        mIncluirOuSalvarButton.setOnClickListener(view -> saveItem());

        getSupportActionBar().setTitle(getString(isNew() ?
                R.string.title_add_item : R.string.title_edit_item));

        mIncluirOuSalvarButton.setText(getString(isNew() ?
                R.string.insert_new_item : R.string.save_existing_item));

        boolean ehUnitario = Pedido.UNIDADE_MEDIDA_UNIDADE.equals(mPedido.getUnidadeMedida());
        int hintResourceId = ehUnitario ?
                R.string.produto_valor_uni_hint : R.string.produto_valor_p_caixa_hint;
        mValorUnitarioLabel.setHint(getString(hintResourceId));


        mQuantidadeEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                saveItem();
                return true;
            }
            return false;
        });

        mIdEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                searchProduto();
                return true;
            }
            return false;
        });

        createDataPicker(null);
        bindItem(false);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.edit_item_pedido_menu, menu);

        MenuItem menuPesquisa = menu.findItem(R.id.action_search);
        menuPesquisa.setVisible(isNew());

        MenuItem menuBarcode = menu.findItem(R.id.action_barcode);
        menuBarcode.setVisible(isNew());

        MenuItem menuSave = menu.findItem(R.id.action_save);
        menuSave.setTitle(getString(isNew() ?
                R.string.insert_new_item : R.string.save_existing_item));

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                cancel();
                return true;
            case R.id.action_save:
                saveItem();
                return true;
            case R.id.action_search:
                listSearchProduto();
                return true;
            case R.id.action_barcode:
                startActivityForResult(new Intent(this,
                        BarcodeScannerActivity.class), BARCODE_REQUEST_CODE);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == BARCODE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            String barcode = data.getExtras().getString(BarcodeScannerActivity.KEY_RESULT_BARCODE);

            if (TextUtils.isNullOrEmpty(barcode)) {
                barcode = "";
                dialogsDefault.showDialogMessage(getString(R.string.barcode_error, barcode),
                        dialogsDefault.DIALOG_TYPE_ERROR, null);
                return;
            }

            String codigoProduto = mProdutoDao.getCodigoProdByBarcode(barcode, current.getUnidadeNegocio().getCodigo());
            if (codigoProduto.length() > 0) {
                mIdEditText.setText(codigoProduto);
            } else {
                dialogsDefault.showDialogMessage(getString(R.string.barcode_error, barcode),
                        dialogsDefault.DIALOG_TYPE_ERROR, null);
            }
        }
    }

    @Override
    public void onProdutoClick(Produto produto) {
        mItem.setCodigoProduto(produto.getCodigo());
        mItem.setProduto(produto);
        bindItem(true);

        DialogsCustom.OnClickDialogMessage onClickDialogMessage = () -> {
            try {
                mQuantidadeEditText.requestFocus();
                AndroidUICompoentUtils.showKeyboard(
                        PedidoItemDetailActivity.this);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        };

        if(!checkItemExists()){
            if (!produto.isPrecoAvailable()) {
                dialogsDefault.showDialogMessage(getString(R.string.add_item_empty_price),
                        dialogsDefault.DIALOG_TYPE_WARNING, onClickDialogMessage);
            } else if (produto.getPrecoVenda().getBloq() != PrecoVenda.BLOQ_PRODUTO_DISPONIVEL &&
                    !mPedido.getTipoVenda().getCodigo().equals(TipoVenda.REB)) {
                dialogsDefault.showDialogMessage(getString(R.string.add_item_blocked),
                        dialogsDefault.DIALOG_TYPE_WARNING, onClickDialogMessage);
            } else {
                try {
                    mQuantidadeEditText.requestFocus();
                    AndroidUICompoentUtils.showKeyboard(PedidoItemDetailActivity.this);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }


    }

    @Override
    public void onProdutoDATClick(Produto produto) {
        mItem.setCodigoProduto(produto.getCodigo());
        mItem.setProduto(produto);


        if(!checkItemExists()) {
            bindItem(true);

            try {
                mQuantidadeEditText.requestFocus();
                AndroidUICompoentUtils.showKeyboard(PedidoItemDetailActivity.this);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

    }

    private void bindItem(boolean shouldFillIdTextEdit) {
        removeListeners();
        setData(shouldFillIdTextEdit);
        configInputs();
        addListeners();
    }

    private void setData(boolean shouldFillIdTextEdit) {
        if (mItem != null && mItem.getProduto() != null) {
            Usuario usuario = current.getUsuario();
            PerfilVenda perfilVenda = usuario.getPerfil().getPerfilVenda(mPedido);
            Produto produto = mItem.getProduto();

            String codigoProduto = TextUtils.removeAllLeftOccurrencies(produto.getCodigo(), '0');
            mProdutoEditText.setText(String.format("%s - %s", codigoProduto, produto.getNome()));
            mItensPorCaixaEditText.setText(String.valueOf(produto.getMultiplo()));

            if (shouldFillIdTextEdit) {
                mIdEditText.setText(codigoProduto);
            }

            if (isDAT()) {
                setupBatchSpinner(
                        mProdutoDao.getAllBatchDAT(mPedido, produto.getCodigo()).toArray());
            }

            Integer quantidadePedidoAnteriorTemp = mItemPedidoDao.getQuantidadePedidoAnterior(
                    usuario.getCodigo(), mPedido.getCodigo(), produto.getCodigo());
            int quantidadePedidoAnterior = quantidadePedidoAnteriorTemp == null ?
                    0 : quantidadePedidoAnteriorTemp;
            mQtdPedidoAnteriorEditText.setText(String.valueOf(quantidadePedidoAnterior));

            Integer quantidadeMediaPorPedidoTemp = mItemPedidoDao.getQuantidadeMediaPorPedido(
                    usuario.getCodigo(), produto.getCodigo());
            int quantidadeMediaPorPedido = quantidadeMediaPorPedidoTemp == null ?
                    0 : quantidadeMediaPorPedidoTemp;
            mQtdMediaPorPedidoEditText.setText(String.valueOf(quantidadeMediaPorPedido));

            if (perfilVenda.isSortimentoHabilitado() && !isReb()) {
                boolean sortimento = produto.getTipoSortimento() != null;

                mSortimentoEditText.setText(sortimento ?
                        produto.getDescricaoSortimento() : "-");
            }

            if (produto.isPrecoAvailable() && !isDAT()) {
                double preco = produto.getPrecoVenda().getPreco();
                if (!Pedido.UNIDADE_MEDIDA_UNIDADE.equals(mPedido.getUnidadeMedida())) {
                    preco *= produto.getMultiplo();
                }
                mValorUnitarioEditText.setText(FormatUtils.toBrazilianRealCurrency(preco));
            }

            if(isReb()){
                if(TextUtils.isNullOrEmpty(mItem.getLote())){
                    mExpirationTextView.setText(getText(R.string.produto_expiration_date));
                } else {
                    mExpirationTextView.setText(mItem.getLote());
                }

            }

        } else {
            mIdEditText.setText("");
            mProdutoEditText.setText("");
            mQuantidadeEstoqueEditText.setText("0");
            mQtdPedidoAnteriorEditText.setText("0");
            mItensPorCaixaEditText.setText("0");
            mValorUnitarioEditText.setText(FormatUtils.toBrazilianRealCurrency(0));
            mQtdMediaPorPedidoEditText.setText("0");
            mSortimentoEditText.setText("-");

            if (isDAT()) {
                setupBatchSpinner(new Object[0]);
            }
        }

        if(mItem == null){
           finish();
           return;
        }


        if (mItem.getQuantidade() > 0 || mItem.getPercentualDesconto() > 0) {
            mQuantidadeEditText.setText(String.valueOf(mItem.getQuantidade()));
            mDescontoEditText.setText(FormatUtils.toDoubleFormat(mItem.getPercentualDesconto()));

            if(isReb()){



                mValorGondulaEditText.setText(FormatUtils.toBrazilianRealCurrency(mItem.getPrecoVenda()));
            } else{
                processAndShowValorTotal(mQuantidadeEditText.getText(), mDescontoEditText.getText());
            }
        } else {
            mQuantidadeEditText.setText("");
            mDescontoEditText.setText("");
            mValorTotalEditText.setText(FormatUtils.toBrazilianRealCurrency(0));
            mPesoTotalEditText.setText(FormatUtils.toKilogram(0, 2));
        }

        mUltimoItemEditText.setText(getIntent().hasExtra(ARG_ULTIMO_PRODUTO_INCLUIDO) ?
                getIntent().getStringExtra(ARG_ULTIMO_PRODUTO_INCLUIDO) : "");
    }

    /**
     * E.g set visibility, enable/disable, etc.
     */
    private void configInputs() {
        PerfilVenda perfilVenda = current.getUsuario().getPerfil().getPerfilVenda(mPedido);

        if (!isNew()) {
            mIdEditText.setEnabled(false);
            mPesquisaProdutoButton.setEnabled(false);
            mGroupData1.setVisibility(View.GONE);

            mQuantidadeEditText.requestFocus();
        }

        if (perfilVenda == null || perfilVenda.getTipoDesconto() == PerfilVenda.TIPO_DESCONTO_SEM_PERMISSAO) {
            mDescontoEditText.setVisibility(View.GONE);
            mDescontoLabel.setVisibility(View.GONE);
        }

        if (isDAT()) {
            mValorUnitarioEditText.setVisibility(View.GONE);
            mValorUnitarioLabel.setVisibility(View.GONE);
        } else {

            if(isReb()){
                mQtdPedidoAnteriorLabel.setVisibility(View.GONE);
                mItensPorCaixaLabel.setVisibility(View.GONE);
                mQtdMediaPorPedidoLabel.setVisibility(View.GONE);
                mValorUnitarioLabel.setVisibility(View.GONE);
                mDataGroup3.setVisibility(View.VISIBLE);
            }

            mQuantidadeEstoqueEditText.setVisibility(View.GONE);
            mQuantidadeEstoqueLabel.setVisibility(View.GONE);
            mBatchSpinner.setVisibility(View.GONE);
            mBatchLabel.setVisibility(View.GONE);
        }

        if ((perfilVenda == null || !perfilVenda.isSortimentoHabilitado() || isReb())) {
            mSortimentoEditText.setVisibility(View.GONE);
            mSortimentoLabel.setVisibility(View.GONE);
        }

        boolean possuiUltimoItemInserido = !android.text.TextUtils.isEmpty(
                getIntent().getStringExtra(ARG_ULTIMO_PRODUTO_INCLUIDO));
        mUltimoItemEditText.setVisibility(possuiUltimoItemInserido ? View.VISIBLE : View.GONE);
    }

    private void setupBatchSpinner(Object[] objects) {
        Object[] source;
        boolean useHint = objects.length == 0;

        // if there's no batch, use unavailable hint
        if (useHint) {
            source = new String[1];
            source[0] = getString(R.string.batch_hint);
        } else {
            source = objects;
        }

        mBatchSpinner.setEnabled(!useHint);

        final boolean exibeValorCalculado = current
                        .getUsuario()
                        .getPerfil()
                        .getPerfilVenda(mPedido)
                        .isExibicaoValorCalculadoLoteHabilitada();

        mBatchs = useHint ? null : source;

        mBatchSpinnerAdapter = new SpinnerArrayAdapter<BatchDAT>(
                this, source, useHint) {
            @Override
            public String getItemDescription(BatchDAT item) {
                if (exibeValorCalculado) {
                    return String.format("%s (%s)",
                            FormatUtils.toDefaultDateFormat(PedidoItemDetailActivity.this,
                                    item.getData()),
                            FormatUtils.toBrazilianRealCurrency(item.getPrecoComDescontoAplicado()));
                } else {
                    return String.format("%s (%s - %s)",
                            FormatUtils.toDefaultDateFormat(PedidoItemDetailActivity.this,
                                    item.getData()),
                            FormatUtils.toBrazilianRealCurrency(item.getPrecoVenda().getPreco()),
                            FormatUtils.toPercent(item.getPercentualDesconto(), 2));
                }
            }
        };

        mBatchSpinner.setAdapter(mBatchSpinnerAdapter);
    }

    private void removeListeners() {
        mQuantidadeEditText.removeTextChangedListener(mQuantidadeTextWatcher);
        mDescontoEditText.removeTextChangedListener(mDescontoTextWatcher);

    }

    private void addListeners() {
        mQuantidadeEditText.addTextChangedListener(mQuantidadeTextWatcher);
        mDescontoEditText.addTextChangedListener(mDescontoTextWatcher);
        mValorGondulaEditText.addTextChangedListener(new TextWatcher() {
            private String currentText = "";
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void afterTextChanged(Editable s) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if( !TextUtils.isNullOrEmpty(s.toString()) && !s.toString().equals(currentText)){
                    mValorGondulaEditText.removeTextChangedListener(this);

                    String cleanString = removeMask(s.toString());

                    double parsed = Double.parseDouble(FormatUtils.toCleanNumber(cleanString.replace(".", "")));
                    String formatted = NumberFormat.getCurrencyInstance().format((parsed/100));

                    currentText = formatted.replaceAll("[R$]", "");
                    mValorGondulaEditText.setText(currentText);
                    mValorGondulaEditText.setSelection(currentText.length());

                    mValorGondulaEditText.addTextChangedListener(this);
                    processAndShowValorTotalReb(mValorGondulaEditText.getText().toString());
                }
            }
        });

        mExpirationDateLinearLayout.setOnClickListener((v)-> mExpirationDatePickerDialog.show());
    }

    private void cancel() {
        dialogsDefault.showDialogQuestion(
                getString(R.string.add_item_cancel),
                dialogsDefault.DIALOG_TYPE_QUESTION,
                new DialogsCustom.OnClickDialogQuestion() {
                    @Override
                    public void onClickPositive() {
                        setResult(Activity.RESULT_CANCELED);
                        finish();
                    }

                    @Override
                    public void onClickNegative() {
                    }
                });
    }

    public void showProgressBar(boolean value) {
        mBaseScrollView.setVisibility(value ? View.GONE : View.VISIBLE);
        mProgressLinearLayout.setVisibility(value ? View.VISIBLE : View.GONE);
    }

    private void searchProduto() {
        String codigo = mIdEditText.getText().toString();

        if (android.text.TextUtils.isEmpty(codigo)) {
            return;
        }

        try {
            AndroidUICompoentUtils.hideKeyboard(this);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        showProgressBar(true);

        boolean isSortimentoAvailable = current
                .getUsuario()
                .getPerfil()
                .getPerfilVenda(mPedido)
                .isSortimentoHabilitado();

        AsyncTaskSearchProduto asyncTaskSearchProduto = new AsyncTaskSearchProduto(this,
                codigo, mPedido, isSortimentoAvailable, mProdutoDao, produto -> {
            showProgressBar(false);

            if (produto == null) {
                dialogsDefault.showDialogMessage(
                        getString(R.string.search_item_product_error),
                        dialogsDefault.DIALOG_TYPE_ERROR, null);
                return;
            }

                try {
                    if (TipoVenda.DAT.equals(mPedido.getCodigoTipoVenda())) {
                        onProdutoDATClick(produto);
                    } else {
                        onProdutoClick(produto);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    dialogsDefault.showDialogMessage(ex.getMessage(),
                            dialogsDefault.DIALOG_TYPE_ERROR, null);
                }
        });

        asyncTaskSearchProduto.execute();
    }

    private void listSearchProduto() {
        if (isDAT()) {
            ProdutosDATDialogFragment produtosDATDialogFragment =
                    ProdutosDATDialogFragment.newInstance(mPedido.getCodigo());
            produtosDATDialogFragment.show(getSupportFragmentManager(), "");
        } else {
            ProdutosDialogFragment produtosDialogFragment =
                    ProdutosDialogFragment.newInstance(mPedido.getCodigo());
            produtosDialogFragment.show(getSupportFragmentManager(), "");
        }
    }

    private boolean checkItemExists(){
        if(!isNew()){
            return false;
        }

        if (mItem.getCodigoProduto() != null && mItemPedidoDao.isProdutoAlreadyInThePedido(mPedido.getCodigo(),
                mItem.getCodigoProduto())) {

            dialogsDefault.showDialogQuestion(getString(R.string.message_error_prod_alter), dialogsDefault.DIALOG_TYPE_QUESTION, new DialogsCustom.OnClickDialogQuestion() {
                @Override
                public void onClickPositive() {

                    Intent data = new Intent();
                    data.putExtra(KEY_PRODUCT_EXIST, mItem.getCodigoProduto() == null ? mIdEditText.getText().toString(): mItem.getCodigoProduto());
                    PedidoItemDetailActivity.this.setResult(Activity.RESULT_OK, data);

                    finish();
                }

                @Override
                public void onClickNegative() {

                }
            });

            return true;

        }else {
            return false;
        }

    }

    private void saveItem() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);

        processInputAndBuildModel();

        ItemPedidoDao itemPedidoDao = new ItemPedidoDao(this);
        ProdutoDao produtoDao = new ProdutoDao(this);

        if(!checkItemExists()){
            ValidationDan validationDan = mPedidoBusiness.validateItem(getApplicationContext(),
                    itemPedidoDao, produtoDao, current.getUsuario(),
                    mPedido, mItem, isNew());

            if (!validationDan.isValid()) {
                dialogsDefault.showDialogMessage(validationDan.GetAllErrorMessages(),
                        dialogsDefault.DIALOG_TYPE_WARNING, null);
                return;
            }

            if (isNew()) {
                mItemPedidoDao.insert(mItem);
            } else {
                mItemPedidoDao.updateSimple(mItem);
            }

            Intent resultIntent = newResultIntent(mItem.getId());
            setResult(Activity.RESULT_OK, resultIntent);
            finish();
        }
    }

    private void processInputAndBuildModel() {
        if (mItem == null || mItem.getProduto() == null) {
            mQuantidadeEditText.setText("");
            return;
        }

        int quantidade = 0;
        double percentualDesconto = 0;

        try {
            quantidade = Integer.parseInt(mQuantidadeEditText.getText().toString());
        } catch (Exception ex) {
            LogUser.log(Config.TAG, ex.toString());
        }

        if (!"".equals(mDescontoEditText.toString().trim())) {
            percentualDesconto = MathUtils.toDoubleOrDefault(mDescontoEditText.getText().toString(),
                    LocaleUtils.getCurrentLocale(this));
        }

        if (isDAT()) {
            BatchDAT batchDAT = getSelectedBatchDAT();
            if (batchDAT != null) {
                percentualDesconto += batchDAT.getPercentualDesconto();
                mItem.setLote(batchDAT.getLote());
                mItem.getProduto().setPrecoVenda(batchDAT.getPrecoVenda());
            }
        }

        mItem.setQuantidade(quantidade);
        mItem.setPercentualDesconto(percentualDesconto);
        mItem.setLote(mExpirationTextView.getText().toString().replace(getString(R.string.produto_expiration_date), ""));
        if(isReb()){
            mPedidoBusiness.buildNewItemReb(mPedido, mItem, isNew() ? 0:1);
        } else {
            mPedidoBusiness.buildNewItem(mPedido, mItem);
        }


    }

    private void processAndShowValorTotalReb(String valor) {
        if (valor.trim().equals("")) {
            return;
        }

        if(isReb()){
            if( mItem.getProduto() == null){
                mValorGondulaEditText.setText("");
                return;
            }


            int quantidade = 0;
            double peso = mItem.getProduto().getPesoLiquido();

            if(mQuantidadeEditText.getText().toString().length() > 0){
                quantidade = Integer.parseInt(mQuantidadeEditText.getText().toString());
            }

            double valorGondula = 0;
            double multipo = 1;

            if(valor.length() > 0){
                LogUser.log(valor);

                if(valor.contains(".") && valor.contains(",")){
                    valor = valor.replace(".", "");
                    valor = valor.replace(",", "");
                }

                valor = valor.replace("R$ ", "");
                valor = valor.replace("R$", "");
                valor = valor.replace(" ", "");
                valor = valor.replace(",", ".");

                valorGondula = Double.parseDouble(valor.trim());
            }

            double venda =  0;

            if (Pedido.UNIDADE_MEDIDA_CAIXA.equals(mPedido.getUnidadeMedida())) {

                multipo = mItem.getProduto().getMultiplo();
                peso *= multipo;
                if((multipo * mItem.getValorTotal()) / valorGondula == valorGondula){
                    valorGondula = valorGondula / multipo;
                }

                venda = quantidade * (valorGondula * multipo);
                mItem.setPrecoVenda(venda);

            } else {
                venda = valorGondula * quantidade;
                mItem.setPrecoVenda(valorGondula);

            }

            String total = FormatUtils.toBrazilianRealCurrency(venda);

            mPesoTotalEditText.setText(FormatUtils.toKilogram(peso*quantidade, 2));
            mValorTotalEditText.setText(total);
        }
    }


    public void processAndShowValorTotal(Editable inputQuantidade, Editable inputDesconto) {
        if (inputQuantidade.toString().trim().equals("")) {
            return;
        }

        if (mItem.getProduto() == null) {
            return;
        }

        if (!isDAT() && mItem.getProduto().getPrecoVenda() == null) {
            return;
        }


        BatchDAT batchDAT = isDAT() ? getSelectedBatchDAT() : null;

        if (isDAT() && (batchDAT == null || batchDAT.getPrecoVenda() == null)) {
            return;
        }

        boolean ehCaixa = Pedido.UNIDADE_MEDIDA_CAIXA.equals(mPedido.getUnidadeMedida());
        int quantidade = 0;
        double desconto = isDAT() ? batchDAT.getPercentualDesconto() : 0;
        double peso = mItem.getProduto().getPesoLiquido();
        double preco = isDAT() ? batchDAT.getPrecoVenda().getPreco() :
                mItem.getProduto().getPrecoVenda().getPreco();
        double valorTotal, pesoTotal;

        if (ehCaixa) {
            preco *= mItem.getProduto().getMultiplo();
            peso *= mItem.getProduto().getMultiplo();
        }

        try {
            quantidade = Integer.parseInt(inputQuantidade.toString());
        } catch (Exception ex) {
            LogUser.log(Config.TAG, ex.toString());
        }

        valorTotal = quantidade * preco;
        pesoTotal = quantidade * peso;

        if (!"".equals(inputDesconto.toString().trim())) {
            desconto += MathUtils.toDoubleOrDefault(inputDesconto.toString(),
                    LocaleUtils.getCurrentLocale(this));
        }

        valorTotal = valorTotal - (desconto / 100 * valorTotal);

        if(!isReb()){
            mValorTotalEditText.setText(FormatUtils.toBrazilianRealCurrency(valorTotal));
        } else {
            double valorGondula = 0;

            if(mValorGondulaEditText.getText().toString().length() > 0){
                valorGondula = Double.parseDouble(removeMask(mValorGondulaEditText.getText().toString()));
            }

            mValorTotalEditText.setText(FormatUtils.toBrazilianRealCurrency(quantidade * valorGondula));
        }

        mPesoTotalEditText.setText(FormatUtils.toKilogram(pesoTotal, 2));
    }

    private BatchDAT getSelectedBatchDAT() {
        BatchDAT batchDAT = null;
        boolean isThereAnyBatchDATSelected = mBatchSpinnerAdapter
                .isThereAnyItemSelected(mBatchSpinner);
        if (isThereAnyBatchDATSelected) {
            batchDAT = (BatchDAT) mBatchSpinner.getSelectedItem();
        }
        return batchDAT;
    }

    private void createDataPicker(Date date) {
        Calendar calendar = Calendar.getInstance();

        if (date != null) {
            try {
                calendar.setTime(date);
            } catch (Exception e) {
                LogUser.log(Config.TAG, e.toString());
            }
        }

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        mExpirationDateSetListener = (DatePicker datePicker, int yearSelected, int monthSelected, int daySelected) -> {
            try {
                Date convertDate = FormatUtils.toCreateDatePicker(yearSelected, monthSelected, daySelected);
                mExpirationTextView.setText(FormatUtils.toDefaultDateBrazilianFormat(convertDate));
                mItem.setLote(mExpirationTextView.getText().toString());
            } catch (Exception e) {
                LogUser.log(Config.TAG, e.toString());
            }
        };


        mExpirationDatePickerDialog = new DatePickerDialog(
                this, mExpirationDateSetListener, year, month, day);

    }


    /**
     * Indicates if the screen is being used to add
     * a new item or to edit an existing item.
     */
    private boolean isNew() {
        return !getIntent().hasExtra(ARG_CODIGO_ITEM);
    }

    /**
     * Indicates if the pedido is DAT or not.
     */
    private boolean isDAT() {
        return TipoVenda.DAT.equals(mPedido.getCodigoTipoVenda());
    }

    /**
     * Indicates if the pedido is Rebaixado.
     */
    private boolean isReb() {
        return TipoVenda.REB.equals(mPedido.getCodigoTipoVenda());
    }

    private String removeMask(String value){
        String valueTemp = value.replace(".", "");
        valueTemp = valueTemp.replace(",", ".");
        valueTemp = valueTemp.replace("R$", "");

        valueTemp = FormatUtils.toCleanNumber(valueTemp);

        return valueTemp;
    }
}
