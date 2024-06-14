package br.com.jjconsulting.mobile.dansales;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.Date;

import br.com.jjconsulting.mobile.dansales.database.ClienteDao;
import br.com.jjconsulting.mobile.dansales.database.LayoutDao;
import br.com.jjconsulting.mobile.dansales.database.PesquisaDao;
import br.com.jjconsulting.mobile.dansales.database.PesquisaRespostaDao;
import br.com.jjconsulting.mobile.dansales.database.SortimentoDao;
import br.com.jjconsulting.mobile.dansales.model.Cliente;
import br.com.jjconsulting.mobile.dansales.model.Layout;
import br.com.jjconsulting.mobile.dansales.model.Pesquisa;
import br.com.jjconsulting.mobile.dansales.model.TFreq;
import br.com.jjconsulting.mobile.dansales.service.Current;
import br.com.jjconsulting.mobile.dansales.util.PesquisaUtils;
import br.com.jjconsulting.mobile.jjlib.util.DialogsCustom;
import br.com.jjconsulting.mobile.jjlib.util.LogUser;

public class PesquisaResumoDialog extends Dialog {

    public final static int SELECT_LAYOUT = 965;
    public final static String LAYOUT_KEY = "layout_key";

    private PesquisaRespostaDao mPesquisaRespostaDao;

    private TextView mNomeClienteTextView;
    private TextView mAddresClienteTextView;
    private TextView mCheckListNotaTextView;
    private TextView mCheckListEspacoTextView;
    private TextView mInspetoriaNotaTextView;
    private TextView mInspetoriaEspacoTextView;
    private TextView mTitleTextView;

    private Button mRespostaCancelButton;
    private Button mRespostaOKButton;

    private TextView mLayoutTextView;

    private DialogsCustom dialogsDefault;

    private Activity activity;

    private Cliente cliente;

    private Pesquisa mPesquisa;

    private Layout mLayout;

    private LinearLayout container;

    private boolean isShowTitle;
    private boolean isChangedLayout;
    private boolean isBlockChange;

    private int idPesquisa;

    private String mFreq;



    public PesquisaResumoDialog(Activity activity, boolean isShowTitle, String codigoCliente, String codigoUsuario, int idPesquisa, Date currentDate, String unidadeNegocio) {
        super(activity);
        this.activity = activity;
        this.isChangedLayout = false;
        this.isShowTitle = isShowTitle;
        this.idPesquisa = idPesquisa;
        mPesquisaRespostaDao = new PesquisaRespostaDao(getContext(), currentDate);

        loadingCliente(unidadeNegocio, codigoCliente);
        loadingLayout(unidadeNegocio, codigoUsuario);

    }

    public PesquisaResumoDialog(Activity activity, boolean isShowTitle, String codigoCliente, Pesquisa pesquisa, String unidadeNegocio, String codigoUsuario) {
        super(activity);
        this.activity = activity;
        this.mPesquisa = pesquisa;
        this.isChangedLayout = false;
        this.isShowTitle = isShowTitle;
        this.idPesquisa = mPesquisa.getCodigo();
        mPesquisaRespostaDao = new PesquisaRespostaDao(getContext(), pesquisa.getCurrentDate());
        mFreq = TFreq.getFreq(mPesquisa.getFreq(), mPesquisa.getCurrentDate());

        loadingCliente(unidadeNegocio, codigoCliente);
        loadingLayout(unidadeNegocio, codigoUsuario);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        this.setCancelable(false);

        setContentView(R.layout.dialog_fragment_cliente_info);

        mNomeClienteTextView = findViewById(R.id.cliente_nome_text_view);
        mAddresClienteTextView = findViewById(R.id.cliente_address_text_view);
        mLayoutTextView = findViewById(R.id.layout_text_view);
        mCheckListNotaTextView = findViewById(R.id.check_list_nota_text_view);
        mCheckListEspacoTextView = findViewById(R.id.check_list_espaco_text_view);

        mInspetoriaNotaTextView = findViewById(R.id.inspetoria_nota_text_view);
        mInspetoriaEspacoTextView = findViewById(R.id.inspetoria_espaco_text_view);

        mTitleTextView = findViewById(R.id.title_text_view);

        container = findViewById(R.id.container_answers);

        mRespostaCancelButton = findViewById(R.id.cancel_button);
        mRespostaCancelButton.setOnClickListener(view -> {
            activity.getWindow().setSoftInputMode(
                    WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            activity.finish();
        });

        mRespostaOKButton = findViewById(R.id.ok_button);
        mRespostaOKButton.setOnClickListener(view -> {
            if (mLayout == null) {
                dialogsDefault.showDialogMessage(activity.getString(R.string.select_layout_error), dialogsDefault.DIALOG_TYPE_WARNING, null);
                return;
            }

            dismiss();
            if (isChangedLayout) {
                activity.finish();
                activity.startActivity(PesquisaPerguntasActivity.newIntent(activity, cliente.getCodigo(), true, mPesquisa));
            }
        });

        if (!isShowTitle) {
            mRespostaCancelButton.setVisibility(View.GONE);
        }

        bind();
        visible();

        mLayoutTextView.setOnClickListener((v) -> {
            if (!isBlockChange) {
                dialogsDefault.showDialogQuestion(activity.getString(R.string.checklist_change_layout_warning), dialogsDefault.DIALOG_TYPE_WARNING,
                        new DialogsCustom.OnClickDialogQuestion() {
                            @Override
                            public void onClickPositive() {
                                activity.startActivityForResult(PickLayoutActivity.newIntent(activity), SELECT_LAYOUT);
                            }

                            @Override
                            public void onClickNegative() {

                            }
                        });
            } else {
                Toast.makeText(activity, activity.getString(R.string.checklist_block_change_layout), Toast.LENGTH_LONG).show();
            }
        });

        dialogsDefault = new DialogsCustom(activity);

    }

    private void visible() {
        mTitleTextView.setVisibility(isShowTitle ? View.VISIBLE : View.GONE);
    }

    private void bind() {
        try {
            mNomeClienteTextView.setText(cliente.getNome());
            mAddresClienteTextView.setText(cliente.getEndereco());
            mLayoutTextView.setText(mLayout == null ? activity.getString(R.string.select_layout) : mLayout.getNome());

            mCheckListNotaTextView.setText(String.valueOf(cliente.getUltCheckListNota()));
            mInspetoriaNotaTextView.setText(String.valueOf(cliente.getUltInspetoriaNota()));

            mCheckListEspacoTextView.setText(String.format("%.0f", (cliente.getUltCheckListEspaco() * 100)).replace(",", ".") + "%");
            mInspetoriaEspacoTextView.setText(String.format("%.0f", (cliente.getUltInspetoriaEspaco() * 100)).replace(",", ".") + "%");


        } catch (Exception ex) {
            LogUser.log(ex.getMessage());
        }
    }

    private void loadingLayout(String unidadeNegocio, String codigoUsuario) {
        ClienteDao clienteDao = new ClienteDao(activity);
        cliente = clienteDao.get(unidadeNegocio, cliente.getCodigo());

        Layout layout;
        LayoutDao layoutDao = new LayoutDao(activity);

        if (mPesquisa != null) {
            if (mPesquisa.getLayout() == null) {
                layout = insertLayoutDefault(unidadeNegocio, codigoUsuario, layoutDao);
                mPesquisa.setLayout(layout);
            } else {
                layout = mPesquisa.getLayout();
            }

            if (layout == null)
                return;

            //Verifica se existe resposta enviada
            isBlockChange = mPesquisaRespostaDao.isPesquisaEnviada(codigoUsuario, cliente.getCodigo(), mPesquisa.getFreq(), mPesquisa.getCodigo());

            if (!isBlockChange) {
                //Verifica se layout ainda está disponivel
                boolean isExistLayout = layoutDao.isExistsLayout(unidadeNegocio, layout.getCodigo());
                if (!isExistLayout) {
                    isChangedLayout = true;
                    mPesquisa.setLayout(insertLayoutDefault(unidadeNegocio, codigoUsuario, layoutDao));

                    mPesquisaRespostaDao.deleteAllRespostas(idPesquisa, Current.getInstance(activity).getUsuario().getCodigo(),
                            cliente.getCodigo(), mFreq);

                    dialogsDefault.showDialogMessage(activity.getString(R.string.checklist_not_exists_layout), dialogsDefault.DIALOG_TYPE_WARNING, null);
                }
            }
        } else {
            PesquisaDao pesquisaDao = new PesquisaDao(activity);
            Date date = mPesquisaRespostaDao.getCurrentDate();

            mFreq = TFreq.getFreq(pesquisaDao.get(idPesquisa, codigoUsuario, cliente.getCodigo(), date).getFreq(), date);

            layout = layoutDao.getLayoutUser(idPesquisa, codigoUsuario, cliente.getCodigo(), mFreq);
            if (layout == null) {
                layout = new Layout();
                layout.setNome(layoutDao.getLayoutName(unidadeNegocio, cliente.getCodigo()));
            }

            isBlockChange = true;
        }

        mLayout = layout;
    }

    private void loadingCliente(String unidadeNegocio, String codigoCliente) {
        ClienteDao clienteDao = new ClienteDao(activity);
        cliente = clienteDao.get(unidadeNegocio, codigoCliente);
    }

    private Layout insertLayoutDefault(String unidadeNegocio, String codigoUsuario, LayoutDao layoutDao) {
        Layout layout = layoutDao.getLayout(unidadeNegocio, cliente.getCodigo(), mPesquisa.getCurrentDate());

        layoutDao.deleteLayout(mPesquisa.getCodigo(),
                cliente.getCodigo(), codigoUsuario, mFreq);

        if (layout != null)
            layoutDao.insertLayout(layout.getCodigo(), mPesquisa.getCodigo(),
                    cliente.getCodigo(), codigoUsuario, mFreq);

        return layout;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        String userID = Current.getInstance(activity).getUsuario().getCodigo();
        LayoutDao layoutDao = new LayoutDao(activity);

        mPesquisaRespostaDao.deleteAllRespostas(idPesquisa, userID,
                cliente.getCodigo(), mFreq);

        layoutDao.deleteLayout(mPesquisa.getCodigo(),
                cliente.getCodigo(), userID, mFreq);

        Gson gson = new Gson();
        Layout layout = gson.fromJson(data.getStringExtra(LAYOUT_KEY), Layout.class);
        mLayoutTextView.setText(layout.getNome());
        mPesquisa.setLayout(layout);
        mLayout = layout;

        isChangedLayout = true;

        layoutDao.insertLayout(mPesquisa.getLayout().getCodigo(), mPesquisa.getCodigo(),
                cliente.getCodigo(), userID, mFreq);
    }
}


