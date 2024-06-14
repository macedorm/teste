package br.com.jjconsulting.mobile.dansales;

import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import br.com.jjconsulting.mobile.dansales.model.Pedido;
import br.com.jjconsulting.mobile.dansales.model.PerfilVenda;
import br.com.jjconsulting.mobile.dansales.util.CustomTextInputLayout;
import br.com.jjconsulting.mobile.jjlib.OnPageSelected;
import br.com.jjconsulting.mobile.jjlib.util.LogUser;
import br.com.jjconsulting.mobile.jjlib.util.TextWatcherUtils;

public class PedidoObservacaoFragment extends PedidoBaseFragment implements OnPageSelected {

    private TextWatcher mObsTextWatcher;
    private TextWatcher mObsNfTextWatcher;

    private RelativeLayout mObsRelativeLayout;
    private RelativeLayout mObsNfRelativeLayout;

    private EditText mObsEditText;
    private EditText mObsNfEditText;

    private ImageView mObsInfoImageView;
    private ImageView mObsNfImageView;

    private CustomTextInputLayout mObsTextInputLayout;
    private CustomTextInputLayout mObsNfTextInputLayout;

    private LinearLayout mNoObsLinearLayout;

    public PedidoObservacaoFragment() {
    }

    public static PedidoObservacaoFragment newInstance() {
        return new PedidoObservacaoFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pedido_observacao, container,
                false);

        mObsRelativeLayout = view.findViewById(R.id.pedido_obs_relative_layout);
        mObsNfRelativeLayout = view.findViewById(R.id.pedido_obs_nf_relative_layout);
        mObsEditText = view.findViewById(R.id.pedido_obs_text_view);
        mObsNfEditText = view.findViewById(R.id.pedido_obs_nf_text_view);
        mObsTextInputLayout = view.findViewById(R.id.pedido_obs_input_layout);
        mObsNfTextInputLayout = view.findViewById(R.id.pedido_obs_nf_input_layout);
        mNoObsLinearLayout = view.findViewById(R.id.pedido_obs_error_layout);
        mObsInfoImageView = view.findViewById(R.id.pedido_obs_info_image_view);
        mObsNfImageView = view.findViewById(R.id.pedido_obs_nf_info_image_view);

        mObsNfImageView.setOnClickListener(viewObsNfInfo -> {
            dialogsDefault.showDialogMessage(getCurrentPerfilVenda().getObsNfInfo(), dialogsDefault.DIALOG_TYPE_QUESTION, null);
        });

        mObsInfoImageView.setOnClickListener(viewObsNfInfo -> {
            dialogsDefault.showDialogMessage(getCurrentPerfilVenda().getObsInternaInfo(), dialogsDefault.DIALOG_TYPE_QUESTION, null);
        });

        //Bind Values
        bindPedido(getCurrentPedido());

        //Enable Disable and Hide Fields
        enableFields(isEditMode());
        showHideFields();

        //Events
        mObsNfEditText.post(() -> {
            addListeners();
        });

        return view;
    }

    /**
     * Show and hide fields
     */
    private void showHideFields() {

        if (isEditMode()) {

            if (getCurrentPerfilVenda().getTipoObsInterna() == PerfilVenda.TIPO_OBS_INTERNA_NAO_EXIBIR) {
                mObsRelativeLayout.setVisibility(View.GONE);
            } else {
                mObsTextInputLayout.setVisibility(View.VISIBLE);

                if (getCurrentPerfilVenda().getObsInternaInfo() != null &&
                        getCurrentPerfilVenda().getObsInternaInfo().trim().length() > 0) {
                    mObsInfoImageView.setVisibility(View.VISIBLE);
                } else {
                    mObsInfoImageView.setVisibility(View.GONE);
                }

            }

            if (getCurrentPerfilVenda().getTipoObsNf() == PerfilVenda.TIPO_OBS_NF_NAO_EXIBIR) {
                mObsNfRelativeLayout.setVisibility(View.GONE);
            } else {
                mObsNfRelativeLayout.setVisibility(View.VISIBLE);
                if (getCurrentPerfilVenda().getObsNfInfo() != null
                        && getCurrentPerfilVenda().getObsNfInfo().trim().length() > 0) {
                    mObsNfImageView.setVisibility(View.VISIBLE);
                } else {
                    mObsNfImageView.setVisibility(View.GONE);
                }
            }

        } else {

            mObsInfoImageView.setVisibility(View.GONE);
            mObsNfImageView.setVisibility(View.GONE);

            Pedido pedido = getCurrentPedido();
            if(pedido != null) {
                if (pedido.getObservacao() == null || pedido.getObservacao().isEmpty()) {
                    mObsRelativeLayout.setVisibility(View.GONE);
                } else {
                    mObsRelativeLayout.setVisibility(View.VISIBLE);
                }

                if (pedido.getObservacaoNotaFiscal() == null || pedido.getObservacaoNotaFiscal().isEmpty()) {
                    mObsNfTextInputLayout.setVisibility(View.GONE);
                } else {
                    mObsNfTextInputLayout.setVisibility(View.VISIBLE);
                }
            }
        }

        if (mObsRelativeLayout.getVisibility() == View.GONE &&
                mObsNfRelativeLayout.getVisibility() == View.GONE) {
            mNoObsLinearLayout.setVisibility(View.VISIBLE);
        } else {
            mNoObsLinearLayout.setVisibility(View.GONE);

        }
    }

    /**
     * Enable and disable fields
     *
     * @param value
     */

    private void enableFields(boolean value) {
        mObsEditText.setEnabled(value);
        mObsNfEditText.setEnabled(value);
    }


    @Override
    public void onPageSelected(int position) {

        //Enable Disable and Hide Fields
        enableFields(isEditMode());
        showHideFields();
    }

    /**
     * Bind values
     *
     * @param pedido
     */
    private void bindPedido(Pedido pedido) {

        if (pedido != null) {

            if (pedido.getObservacaoNotaFiscal() == null) {
                pedido.setObservacaoNotaFiscal("");
            }

            if (pedido.getObservacao() == null) {
                pedido.setObservacao("");
            }

            mObsEditText.setText(pedido.getObservacao());
            mObsNfEditText.setText(pedido.getObservacaoNotaFiscal());

        }
    }

    private void addListeners() {

        mObsTextWatcher = TextWatcherUtils.buildWithAfterTextChanged(editable -> {
            try {
                Pedido pedido = getCurrentPedido();

                if (pedido.getObservacao() == null || !editable.toString().equals(pedido.getObservacao())) {
                    pedido.setObservacao(editable.toString());
                    setCurrentPedido(pedido);
                }
            }catch (Exception ex){
                LogUser.log(ex.toString());
            }


        });

        mObsNfEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Antes da mudança no texto
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Durante a mudança no texto
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String text = editable.toString();
                String sanitizedText = removeSpecialCharacters(text);

                if (!text.equals(sanitizedText)) {
                    editable.replace(0, editable.length(), sanitizedText);
                }
            }
        });


        mObsEditText.addTextChangedListener(mObsTextWatcher);
        mObsTextInputLayout.loadStyle();

        mObsNfTextWatcher = TextWatcherUtils.buildWithAfterTextChanged(editable -> {
            try {
                Pedido pedido = getCurrentPedido();
                if (pedido.getObservacaoNotaFiscal() == null || !editable.toString().equals(pedido.getObservacaoNotaFiscal())) {
                    pedido.setObservacaoNotaFiscal(editable.toString());
                    setCurrentPedido(pedido);
                    }
            }catch (Exception ex){
                LogUser.log(ex.toString());
            }
        });

        mObsNfEditText.addTextChangedListener(mObsNfTextWatcher);
        mObsNfTextInputLayout.loadStyle();
    }


    private String removeSpecialCharacters(String text) {
        // Use uma expressão regular para remover caracteres especiais
        return text.replaceAll("[^a-zA-Z0-9 ]", "");
    }

}
