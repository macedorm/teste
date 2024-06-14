package br.com.jjconsulting.mobile.dansales.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.text.InputFilter;
import android.text.InputType;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.graphics.BitmapCompat;

import com.google.android.material.textfield.TextInputEditText;
import java.util.ArrayList;
import java.util.Date;

import br.com.jjconsulting.mobile.dansales.R;
import br.com.jjconsulting.mobile.dansales.asyncTask.AsyncTaskItensPesquisaSortimento;
import br.com.jjconsulting.mobile.dansales.database.PesquisaPerguntaDao;
import br.com.jjconsulting.mobile.dansales.model.PesquisaPergunta;
import br.com.jjconsulting.mobile.dansales.model.PesquisaPerguntaOpcoes;
import br.com.jjconsulting.mobile.dansales.model.PesquisaPerguntaType;
import br.com.jjconsulting.mobile.dansales.model.PesquisaResposta;
import br.com.jjconsulting.mobile.jjlib.base.SpinnerArrayAdapter;
import br.com.jjconsulting.mobile.jjlib.dao.entity.TRegSync;
import br.com.jjconsulting.mobile.jjlib.util.Config;
import br.com.jjconsulting.mobile.jjlib.util.DateTextWatcher;
import br.com.jjconsulting.mobile.jjlib.util.DecimalTextWatcher;
import br.com.jjconsulting.mobile.jjlib.util.FormatUtils;
import br.com.jjconsulting.mobile.jjlib.util.ImageCameraGallery;
import br.com.jjconsulting.mobile.jjlib.util.LogUser;
import br.com.jjconsulting.mobile.jjlib.util.MoneyTextWatcher;
import br.com.jjconsulting.mobile.jjlib.util.TextUtils;

public class JJFormAnswersView {

    private OnActions onActions;

    private PesquisaPerguntaDao mPesquisaPerguntaDao;
    private PesquisaPergunta mPergunta;
    private ImageCameraGallery imageCameraGallery;

    private LinearLayout mViewDefaultLinearLayout;
    private TextInputEditText mRespostaTextInputEditText;
    private RadioGroup mRespostaRadioGroup;

    private Spinner mRespostaSpinner;
    private ImageView mRespostaImageView;
    private ImageView mAnswersImageView;

    private TextView mAnswersTitleTextView;
    private TextView mAnswersSubTitleTextView;
    private TextView mAnswersDescTitleTextView;
    private TextView mRespostaMsgErrorTextView;
    private TextView mRespostaPergutaTextView;

    private JJProductGridView jjProductGridView;

    private Bitmap bitmap;
    private String extension;
    private String codigoCliente;

    private ArrayList<CheckBox> arrayCheckBox;

    private Activity activity;

    private ViewGroup layout;

    private int index;
    private int total;
    private int now;

    private PesquisaResposta mPesquisaRespostaObrig;
    private ArrayList<PesquisaResposta> mPesquisaRespostaDep;

    public JJFormAnswersView(Activity activity, PesquisaPergunta pergunta,
                             String codigoCliente,
                             PesquisaResposta pesquisaRespostaObrig, ArrayList<PesquisaResposta> pesquisaRespostaDep,
                             Date currentDate, int index, int total, int now) {
        this.activity = activity;
        mPergunta = pergunta;
        mPesquisaPerguntaDao = new PesquisaPerguntaDao(activity, currentDate);
        this.codigoCliente = codigoCliente;
        this.index = index;
        this.total = total;
        this.now = now;
        this.mPesquisaRespostaObrig = pesquisaRespostaObrig;
        this.mPesquisaRespostaDep = pesquisaRespostaDep;
    }

    public View renderView() {

        imageCameraGallery = new ImageCameraGallery();

        switch (mPergunta.getTipo()) {
            case MULTI_SELECAO:
                initView();
                createRadioGroupMultiSelect();
                bindView();
                break;
            case SELECAO_UNICA:
                initView();
                createRadioGroup();
                bindView();
                break;
            case LISTA:
                initView();
                createSpinner();
                bindView();
                break;
            case UPLOAD_IMAGEM:
                initView();
                createImageUpload();
                bindView();
                break;
            case SORTIMENTO_OBRIGATORIO:
            case SORTIMENTO_RECOMENDADO:
            case SORTIMENTO_INOVACAO:
                createExpandablelistview(false);
                break;
            case SORTIMENTO_OBRIGATORIO_PRECO:
                createExpandablelistview(true);
                break;
            default:
                initView();
                createEditText();
                bindView();
                break;
        }




        return layout;
    }

    private void bindView(){

        String pergunta = TextUtils.firstLetterUpperCase(mPergunta.getDescPergunta());

        if (mPergunta.isObrigatoria()) {
            pergunta += "*";
        }

        mRespostaPergutaTextView.setText(pergunta);

        mAnswersTitleTextView.setVisibility(TextUtils.isNullOrEmpty(mPergunta.getTitlePergunta()) ? View.GONE:View.VISIBLE);
        mAnswersTitleTextView.setText(mPergunta.getTitlePergunta());
        mAnswersSubTitleTextView.setVisibility(TextUtils.isNullOrEmpty(mPergunta.getSubTitlePergunta()) ? View.GONE:View.VISIBLE);
        mAnswersSubTitleTextView.setText(mPergunta.getSubTitlePergunta());
        mAnswersDescTitleTextView.setVisibility(TextUtils.isNullOrEmpty(mPergunta.getDescTitlePergunta()) ? View.GONE:View.VISIBLE);
        mAnswersDescTitleTextView.setText(mPergunta.getDescTitlePergunta());
        mAnswersImageView.setVisibility(TextUtils.isNullOrEmpty(mPergunta.getImage()) ? View.GONE:View.VISIBLE);

        PicassoCustom.setImage(activity, mPergunta.getImage(), mAnswersImageView);

    }

    private void initHeader(View headers){
        mRespostaPergutaTextView = headers.findViewById(R.id.respostas_pergunta_text_view);
        mRespostaMsgErrorTextView = headers.findViewById(R.id.respostas_msg_error_text_view);

        mAnswersTitleTextView = headers.findViewById(R.id.answers_title_text_view);
        mAnswersSubTitleTextView = headers.findViewById(R.id.answers_sub_title_text_view);
        mAnswersDescTitleTextView = headers.findViewById(R.id.answers_title_description_text_view);

        mAnswersImageView = headers.findViewById(R.id.answers_image_view);

        if(mPergunta.getTitlePergunta() != null){
            mRespostaPergutaTextView.setVisibility(View.GONE);
        }
    }

    private void initView(){
        layout = (LinearLayout) activity.getLayoutInflater().inflate(R.layout.item_answers, null);

        mViewDefaultLinearLayout = layout.findViewById(R.id.view_dialog_fragment_perguntas_linear_layout);
        mRespostaPergutaTextView = layout.findViewById(R.id.respostas_pergunta_text_view);
        mRespostaMsgErrorTextView = layout.findViewById(R.id.respostas_msg_error_text_view);

        mAnswersTitleTextView = layout.findViewById(R.id.answers_title_text_view);
        mAnswersSubTitleTextView = layout.findViewById(R.id.answers_sub_title_text_view);
        mAnswersDescTitleTextView = layout.findViewById(R.id.answers_title_description_text_view);

        mAnswersImageView = layout.findViewById(R.id.answers_image_view);

        if(mPergunta.getTitlePergunta() != null){
            mRespostaPergutaTextView.setVisibility(View.GONE);
        }

        layout.findViewById(R.id.respostas_pergunta_linear_layout).setBackgroundColor(Color.WHITE);
    }

    private void createSpinner() {
        LinearLayout child = (LinearLayout) activity.getLayoutInflater().inflate(R.layout.perguntas_tipo_spinner, null);
        mViewDefaultLinearLayout.addView(child);
        mRespostaSpinner = child.findViewById(R.id.perguntas_spinner);

        if (mPergunta.getPesquisaPerguntaOpcoes() == null) {
            mPergunta.setPesquisaPerguntaOpcoes(mPesquisaPerguntaDao
                    .getOpcoes(mPergunta));
        }
        Object[] objects = SpinnerArrayAdapter.makeObjectsWithHint(mPergunta.getPesquisaPerguntaOpcoes().toArray(),
                activity.getString(R.string.select_answer));

        SpinnerArrayAdapter mPerguntasSpinnerAdapter = new SpinnerArrayAdapter<PesquisaPerguntaOpcoes>(
                activity, objects, true) {
            @Override
            public String getItemDescription(PesquisaPerguntaOpcoes item) {
                return item.getDesc();
            }
        };

        mRespostaSpinner.setAdapter(mPerguntasSpinnerAdapter);

        for (int i = 0; i < mPergunta.getPesquisaPerguntaOpcoes().size(); i++) {
            if(mPergunta.getRespostaPesquisa() != null && mPergunta.getRespostaPesquisa().size() > 0
                    && mPergunta.getRespostaPesquisa().get(0).getOpcao() != null){

                if(mPergunta.getRespostaPesquisa().get(0).getOpcao().trim().equals(
                        mPergunta.getPesquisaPerguntaOpcoes().get(i).getValor().trim())){
                    mRespostaSpinner.setSelection(i + 1);
                }
            }

        }
    }

    private void createRadioGroupMultiSelect() {

        arrayCheckBox = new ArrayList<>();

        //Caso as opções seja dependente das respostas de outra questão
        if(mPesquisaRespostaDep != null && mPesquisaRespostaDep.size() > 0){
            ArrayList<PesquisaPerguntaOpcoes> listPesquisaPerguntaOpcoesDep = new ArrayList<>();

            String opcoes[] = mPesquisaRespostaDep.get(0).getOpcao().split(";");
            String respostas[] = mPesquisaRespostaDep.get(0).getResposta().split(";");

            for(int ind = 0; ind < opcoes.length; ind++){
                PesquisaPerguntaOpcoes pesquisaPerguntaOpcoesDep = new PesquisaPerguntaOpcoes();

                String opcao = opcoes[ind].trim();
                String reposta = respostas[ind];

                pesquisaPerguntaOpcoesDep.setNumOpcao(Integer.parseInt(opcao.trim()));
                pesquisaPerguntaOpcoesDep.setDesc(reposta);

                listPesquisaPerguntaOpcoesDep.add(pesquisaPerguntaOpcoesDep);
            }

            mPergunta.setPesquisaPerguntaOpcoes(listPesquisaPerguntaOpcoesDep);

        } else {
            if (mPergunta.getPesquisaPerguntaOpcoes() == null) {
                mPergunta.setPesquisaPerguntaOpcoes(mPesquisaPerguntaDao.getOpcoes(mPergunta));
            }
        }

        String[] selected = null;

        if(mPergunta.getRespostaPesquisa() != null && mPergunta.getRespostaPesquisa().size() > 0
                && mPergunta.getRespostaPesquisa().get(0).getOpcao() != null){
            selected = mPergunta.getRespostaPesquisa().get(0).getOpcao().trim().split(";");
        }

        for (int i = 0; i < mPergunta.getPesquisaPerguntaOpcoes().size(); i++) {
            CheckBox checkBox = new CheckBox(activity);
            checkBox.setText(mPergunta.getPesquisaPerguntaOpcoes().get(i).getDesc());
            checkBox.setId(i);
            checkBox.setOnCheckedChangeListener((compoundButton, b) -> {
                compoundButton.setChecked(b);
            });

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, (int) activity.getResources().getDimension(R.dimen.pesquisa_margin_radio_box),
                    0, (int) activity.getResources().getDimension(R.dimen.pesquisa_margin_radio_box));
            checkBox.setLayoutParams(params);

            if(selected != null){
                for(int ind = 0; ind < selected.length; ind++){
                    if(selected[ind].trim().equals(mPergunta.getPesquisaPerguntaOpcoes().get(i).getNumOpcao() + "")){
                        ind = selected.length;
                        checkBox.setChecked(true);
                    }
                }
            }

            View line = new View(activity, null, R.style.LineDefault);
            LinearLayout.LayoutParams paramsLine = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    1);
            line.setLayoutParams(paramsLine);
            line.setBackgroundColor(activity.getResources().getColor(R.color.formLineColor));

            arrayCheckBox.add(checkBox);
            mViewDefaultLinearLayout.addView(checkBox);

            if ((i + 1) < mPergunta.getPesquisaPerguntaOpcoes().size()) {
                mViewDefaultLinearLayout.addView(line);
            }
        }
    }

    private void createRadioGroup() {
        LinearLayout child = (LinearLayout) activity.getLayoutInflater().inflate(R.layout.perguntas_tipo_group, null);
        mViewDefaultLinearLayout.addView(child);
        mRespostaRadioGroup = child.findViewById(R.id.pergunta_radio_group);

        if (mPergunta.getPesquisaPerguntaOpcoes() == null) {
            mPergunta.setPesquisaPerguntaOpcoes(mPesquisaPerguntaDao.getOpcoes(mPergunta));
        }

        for (int i = 0; i < mPergunta.getPesquisaPerguntaOpcoes().size(); i++) {
            RadioButton radioButton = new RadioButton(activity);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);

            params.setMargins(0, (int) activity.getResources().getDimension(R.dimen.pesquisa_margin_radio_box), 0,
                    (int) activity.getResources().getDimension(R.dimen.pesquisa_margin_radio_box));

            radioButton.setLayoutParams(params);
            radioButton.setText(mPergunta.getPesquisaPerguntaOpcoes().get(i).getDesc());
            radioButton.setId(i);

            if(mPergunta.getRespostaPesquisa() != null && mPergunta.getRespostaPesquisa().size() > 0
                    && mPergunta.getRespostaPesquisa().get(0).getOpcao() != null){
                if(mPergunta.getRespostaPesquisa().get(0).getOpcao().trim().equals(
                        mPergunta.getPesquisaPerguntaOpcoes().get(i).getNumOpcao() + "")){
                    radioButton.setChecked(true);
                }
            }

            mRespostaRadioGroup.addView(radioButton);
        }
    }

    private void createImageUpload() {
        LinearLayout child = (LinearLayout) activity.getLayoutInflater().inflate(R.layout.perguntas_tipo_image, null);
        mViewDefaultLinearLayout.addView(child);

        if (mPergunta.getNameImage() == null) {
            String nameImage = String.format("%d%d_img_per.png", mPergunta.getIdPequisa(), mPergunta.getNumPergunta());
            mPergunta.setNameImage(nameImage);
        }

        mRespostaImageView = child.findViewById(R.id.pergunta_image_view);
        mRespostaImageView.setOnClickListener(view -> {
            openCamera();
        });

        if(mPergunta.getRespostaPesquisa() != null && mPergunta.getRespostaPesquisa().size() > 0){
            bitmap = ImageCameraGallery.decodeBase64Bitmap(mPergunta.getRespostaPesquisa().get(0).getResposta());
            mRespostaImageView.setImageBitmap(bitmap);
        }

    }

    private void openCamera() {
        activity.startActivityForResult(imageCameraGallery.getPhotoIntent(activity.getString(R.string.title_intent_photo), mPergunta.getNameImage(), mPergunta.isOnlyCamera(),  activity), ImageCameraGallery.OPEN_CAMERA);

        if(onActions != null){
            onActions.onOpenImage(index);
        }
    }

    private void createExpandablelistview(boolean isPreco){
        layout = (LinearLayout) activity.getLayoutInflater().inflate(R.layout.item_answers_sortimento, null);
        mViewDefaultLinearLayout = layout.findViewById(R.id.view_dialog_fragment_perguntas_linear_layout);

        String resposta =  mPergunta.getRespostaPesquisa().size() == 0 ? "":mPergunta.getRespostaPesquisa() .get(0).getResposta();

        AsyncTaskItensPesquisaSortimento asyncTaskItensPesquisaSortimento = new AsyncTaskItensPesquisaSortimento(activity,
               resposta, mPesquisaRespostaObrig, mPergunta.getItensListSortimento(), isPreco ,(objects) -> {

            activity.runOnUiThread(()-> {
                jjProductGridView = new JJProductGridView(activity, isPreco);
                jjProductGridView.setNumColumn(2);

                if(isPreco){
                    jjProductGridView.setOnClickGrid(() -> {
                            mRespostaMsgErrorTextView.setVisibility(View.GONE);
                    });
                }

                mViewDefaultLinearLayout.addView(jjProductGridView.renderView(objects));

                View headers =  activity.getLayoutInflater().inflate(R.layout.item_answers_sortimento_header, null);
                jjProductGridView.addHeader(headers);
                initHeader(headers);

                bindView();

            });
        });

        asyncTaskItensPesquisaSortimento.execute();

    }

    private void createEditText() {
        LinearLayout child = (LinearLayout) activity.getLayoutInflater().inflate(R.layout.perguntas_tipo_texto, null);
        mRespostaTextInputEditText = child.findViewById(R.id.pergunta_edit_text);

        if (mPergunta.getTamMax() > 0) {
            mRespostaTextInputEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(mPergunta.getTamMax())});
        }

        switch (mPergunta.getTipo()) {
            case MOEDA:
                mRespostaTextInputEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
                mRespostaTextInputEditText.addTextChangedListener(new MoneyTextWatcher(mRespostaTextInputEditText));
                if (mPergunta.getTamMax() == 0) {
                    mRespostaTextInputEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(11)});
                }
                break;
            case NUMERO:
                mRespostaTextInputEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
                if (mPergunta.getTamMax() == 0) {
                    mRespostaTextInputEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(9)});
                }
                break;
            case NUMERO_DECIMAL:
                mRespostaTextInputEditText.setInputType(InputType.TYPE_CLASS_NUMBER);

                if(mPergunta.getCasasDecimais() > 0){
                    mRespostaTextInputEditText.addTextChangedListener(new DecimalTextWatcher(mRespostaTextInputEditText, mPergunta.getCasasDecimais()));
                }

                if (mPergunta.getTamMax() == 0) {
                    mRespostaTextInputEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(9)});
                }
                break;
            case DATA:
                mRespostaTextInputEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
                mRespostaTextInputEditText.addTextChangedListener(new DateTextWatcher(mRespostaTextInputEditText));
                break;
        }

        if(mPergunta.getRespostaPesquisa() != null && mPergunta.getRespostaPesquisa().size() > 0){
            if(mPergunta.getTipo() == PesquisaPerguntaType.DATA){
                try {
                    mRespostaTextInputEditText.setText(FormatUtils.toDateTimeText(mPergunta          .
                            getRespostaPesquisa().get(0).getResposta()));
                } catch (Exception ex) {
                    LogUser.log(Config.TAG, ex.toString());
                }
            } else {
                mRespostaTextInputEditText.setText(mPergunta.getRespostaPesquisa().get(0).getResposta());
            }
        }

        mViewDefaultLinearLayout.addView(child);
    }

    public boolean createAnswer(){
        if (checkAnswerValid()) {

            ArrayList<PesquisaResposta> respostaItemPesquisas = new ArrayList<>();
            PesquisaResposta item = new PesquisaResposta();
            item.setData(FormatUtils.getDateTimeNow(0, 0, 0));

            switch (mPergunta.getTipo()) {
                case MULTI_SELECAO:
                    item.setCodigoPergunta(mPergunta.getNumPergunta());

                    boolean isChecked = false;

                    for (CheckBox checkBox : arrayCheckBox) {
                        if (checkBox.isChecked()) {
                            isChecked = true;
                            if (item.getResposta() == null) {
                                item.setResposta(mPergunta.getPesquisaPerguntaOpcoes().get(checkBox.getId()).getDesc());
                                item.setOpcao(mPergunta.getPesquisaPerguntaOpcoes().get(checkBox.getId()).getNumOpcao() + "");
                                item.setRegSync(TRegSync.INSERT);
                            } else {
                                item.setResposta(item.getResposta() + "; " + mPergunta.getPesquisaPerguntaOpcoes().get(checkBox.getId()).getDesc());
                                item.setOpcao(item.getOpcao() + "; " + mPergunta.getPesquisaPerguntaOpcoes().get(checkBox.getId()).getNumOpcao());
                                item.setRegSync(TRegSync.INSERT);
                            }
                        }
                    }

                    if (isChecked) {
                        respostaItemPesquisas.add(item);
                    }

                    break;
                case SELECAO_UNICA:
                    int radioID = mRespostaRadioGroup.getCheckedRadioButtonId();
                    if (radioID >= 0) {
                        item.setCodigoPergunta(mPergunta.getNumPergunta());
                        item.setResposta(mPergunta.getPesquisaPerguntaOpcoes().get(mRespostaRadioGroup.getCheckedRadioButtonId()).getDesc());
                        item.setOpcao(mPergunta.getPesquisaPerguntaOpcoes().get(mRespostaRadioGroup.getCheckedRadioButtonId()).getNumOpcao() + "");
                        item.setRegSync(TRegSync.INSERT);
                        respostaItemPesquisas.add(item);
                    }
                    break;
                case LISTA:
                    if (mRespostaSpinner.getSelectedItemPosition() > 0) {
                        item.setCodigoPergunta(mPergunta.getNumPergunta());
                        item.setResposta(mPergunta.getPesquisaPerguntaOpcoes().get(mRespostaSpinner.getSelectedItemPosition() - 1).getDesc());
                        item.setOpcao(mPergunta.getPesquisaPerguntaOpcoes().get(mRespostaSpinner.getSelectedItemPosition() - 1).getNumOpcao() + "");
                        item.setRegSync(TRegSync.INSERT);
                        respostaItemPesquisas.add(item);
                    }
                    break;
                case SORTIMENTO_OBRIGATORIO:
                case SORTIMENTO_RECOMENDADO:
                case SORTIMENTO_INOVACAO:
                case SORTIMENTO_OBRIGATORIO_PRECO:
                    if(jjProductGridView != null){
                        item.setCodigoPergunta(mPergunta.getNumPergunta());
                        item.setResposta(jjProductGridView.getItensSelected());
                        item.setRegSync(TRegSync.INSERT);
                        respostaItemPesquisas.add(item);
                    }
                    break;
                case UPLOAD_IMAGEM:
                    if (bitmap != null) {
                        item.setCodigoPergunta(mPergunta.getNumPergunta());
                        item.setResposta(ImageCameraGallery.convertBase64(bitmap));
                        item.setExtensaoArquivo(extension);
                        item.setRegSync(TRegSync.INSERT);
                        respostaItemPesquisas.add(item);
                    } else {
                        respostaItemPesquisas = mPergunta.getRespostaPesquisa();
                    }
                    break;
                case MOEDA:
                case NUMERO_DECIMAL:
                    if (mRespostaPergutaTextView.length() > 0) {
                        item.setCodigoPergunta(mPergunta.getNumPergunta());
                        item.setResposta(toCleanNumber(mRespostaTextInputEditText.getText().toString()));
                        item.setRegSync(TRegSync.INSERT);
                        respostaItemPesquisas.add(item);
                    }
                    break;
                case DATA:
                    if (mRespostaPergutaTextView.length() > 0) {
                        item.setCodigoPergunta(mPergunta.getNumPergunta());

                        String date = "";
                        try {

                            date = FormatUtils.toDefaultDateFormat(FormatUtils.toDate(FormatUtils.toConvertDate(
                                    mRespostaTextInputEditText.getText().toString(), "dd/MM/yyyy", "yyyy-MM-dd HH:mm")));
                            item.setResposta(date);

                        } catch (Exception ex) {
                            LogUser.log(Config.TAG, ex.toString());
                        }
                        item.setRegSync(TRegSync.INSERT);

                        respostaItemPesquisas.add(item);
                    }
                    break;
                default:
                    if (mRespostaPergutaTextView.length() > 0) {
                        item.setCodigoPergunta(mPergunta.getNumPergunta());
                        item.setResposta(mRespostaTextInputEditText.getText().toString());
                        item.setRegSync(TRegSync.INSERT);
                        respostaItemPesquisas.add(item);
                    }
                    break;
            }


            for (int ind = 0; ind < mPergunta.getRespostaPesquisa().size(); ind++) {
                if (mPergunta.getRespostaPesquisa().get(ind).getRegSync() == TRegSync.SEND) {
                    respostaItemPesquisas.get(ind).setRegSync(TRegSync.EDIT);
                } else {
                    if(respostaItemPesquisas.size() > 0){
                        respostaItemPesquisas.get(ind).setRegSync(mPergunta.getRespostaPesquisa().get(ind).getRegSync());
                    }
                }
            }


            mPergunta.setRespostaPesquisa(respostaItemPesquisas);
            return true;
        } else{
            return false;
        }
    }

    private boolean checkAnswerValid() {
        String messageError = "";

        switch (mPergunta.getTipo()) {
            case MULTI_SELECAO:
                boolean isChecked = false;
                for (CheckBox item : arrayCheckBox) {
                    if (item.isChecked()) {
                        isChecked = true;
                    }
                }
                if (!isChecked && mPergunta.isObrigatoria()) {
                    messageError = activity.getString(R.string.message_error_validation_res_option);
                }
                break;
            case SELECAO_UNICA:
                if (mRespostaRadioGroup.getCheckedRadioButtonId() == -1 && mPergunta.isObrigatoria()) {
                    messageError = activity.getString(R.string.message_error_validation_res_option);
                }
                break;
            case LISTA:
                if (mRespostaSpinner.getSelectedItemPosition() == 0 && mPergunta.isObrigatoria()) {
                    messageError = activity.getString(R.string.message_error_validation_res_option);
                }
                break;
            case SORTIMENTO_OBRIGATORIO:
            case SORTIMENTO_RECOMENDADO:
            case SORTIMENTO_INOVACAO:

                if(mPergunta.isObrigatoria()){
                    if(TextUtils.isNullOrEmpty(jjProductGridView.getItensSelected()) && jjProductGridView.getExpandableList().size() > 0){
                        messageError = activity.getString(R.string.message_error_validation_res_option);
                    }
                }

                break;
            case SORTIMENTO_OBRIGATORIO_PRECO:
                if(mPergunta.isObrigatoria()){
                    if(!jjProductGridView.isAllItensSelected() && jjProductGridView.getExpandableList().size() > 0){
                        messageError = activity.getString(R.string.message_error_validation_res_option_preco);
                    }
                }
                break;
            case UPLOAD_IMAGEM:
                if (bitmap == null && mPergunta.isObrigatoria()) {
                    messageError = activity.getString(R.string.message_error_validation_res_text);
                }
                break;
            case MOEDA:
            case NUMERO:
            case NUMERO_DECIMAL:
                if (mRespostaTextInputEditText.getText().toString().length() > 0) {
                    messageError = checkAnswerNumberRange(mRespostaTextInputEditText.getText().toString());
                } else {
                    if (mPergunta.isObrigatoria()) {
                        messageError = activity.getString(R.string.message_error_validation_res_text);
                    }
                }
                break;
            case DATA:
                if (mRespostaTextInputEditText.getText().toString().length() > 0) {
                    if (!FormatUtils.isDateValid(mRespostaTextInputEditText.getText().toString())) {
                        messageError = activity.getString(R.string.message_error_validation_res_date);
                    }
                } else {
                    if (mPergunta.isObrigatoria()) {
                        messageError = activity.getString(R.string.message_error_validation_res_date);
                    }
                }
                break;
            case CAMPO_MEMO:
            case TEXTO_LIVRE:
            case OUTROS:
                if (mRespostaTextInputEditText.getText().toString().length() == 0 && mPergunta.isObrigatoria()) {
                    messageError = activity.getString(R.string.message_error_validation_res_text);
                }
                break;
        }

        if (messageError.length() > 0) {
            mRespostaMsgErrorTextView.setText(messageError);
            mRespostaMsgErrorTextView.setVisibility(View.VISIBLE);
            return false;
        } else {
            mRespostaMsgErrorTextView.setText("");
            mRespostaMsgErrorTextView.setVisibility(View.GONE);
            return true;
        }
    }

    private String checkAnswerNumberRange(String text) {
        String message = "";

        double value = Double.parseDouble(toCleanNumber(text));
        if (value < mPergunta.getValorMin() && mPergunta.getValorMin() != 0) {
            message = activity.getString(R.string.message_error_validation_res_number,
                    String.valueOf(mPergunta.getValorMin()).replace(".", ","),
                    String.valueOf(mPergunta.getValorMax()).replace(".", ","));
        }

        if (value > mPergunta.getValorMax() && mPergunta.getValorMax() != 0) {
            message = activity.getString(R.string.message_error_validation_res_number,
                    String.valueOf(mPergunta.getValorMin()).replace(".", ","),
                    String.valueOf(mPergunta.getValorMax()).replace(".", ","));
        }

        return message;
    }

    private String toCleanNumber(String text) {
        if (mPergunta.getTipo() == PesquisaPerguntaType.MOEDA) {
            text = text.replace(".", "");
            text = text.replace("R$", "").trim();
        }
        text = text.replace(",", ".");
        text = text.replace(" ","");
        return text;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data, Context context) {

            String path = null;

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 2;

        if (data != null) {
                Uri uriImage = data.getData();
                if (uriImage == null) {
                    try {
                        uriImage = imageCameraGallery.getOutPutfileUri();
                        path = imageCameraGallery.getRealPathFromURI(uriImage, activity);
                        bitmap = BitmapFactory.decodeFile(path, options);
                    } catch (Exception e) {
                        LogUser.log(Config.TAG, e.toString());
                    }

                } else {
                    try {
                        path = imageCameraGallery.getRealPathFromURI(uriImage, activity);
                        bitmap = BitmapFactory.decodeFile(path, options);

                    } catch (Exception e) {
                        LogUser.log(Config.TAG, e.toString());
                    }
                }
            } else {
                Uri uriImage = imageCameraGallery.getOutPutfileUri();
                try {
                    path = imageCameraGallery.getRealPathFromURI(uriImage, activity);
                    bitmap = BitmapFactory.decodeFile(path);
                } catch (Exception e) {
                    LogUser.log(Config.TAG, e.toString());
                }

            }

            if (bitmap != null) {
                int bitmapByteCount= BitmapCompat.getAllocationByteCount(bitmap);
                LogUser.log("Tamanho imagem original: " + bitmapByteCount);

                if(bitmapByteCount > 5100000){
                    bitmap = getResizedBitmap(bitmap);
                    bitmapByteCount= BitmapCompat.getAllocationByteCount(bitmap);
                    LogUser.log("Tamanho imagem reduzida: " + bitmapByteCount);
                }

                Bitmap bitmapTemp = imageCameraGallery.imageOrientationValidator(bitmap, path);

                if(bitmapTemp != null){
                    bitmap = bitmapTemp;
                }

                bitmapByteCount= BitmapCompat.getAllocationByteCount(bitmap);
                LogUser.log("Tamanho imagem reduzida bitmapTemp: " + bitmapByteCount);

                mRespostaImageView.setImageBitmap(bitmap);
            }


            extension = path.substring(path.lastIndexOf(".") + 1);
    }

    public void removeResposta() {
         mPergunta.setRespostaPesquisa(new ArrayList<>());
    }

    public PesquisaPergunta getPergunta() {
        return mPergunta;
    }

    public void setPergunta(PesquisaPergunta mPergunta) {
        this.mPergunta = mPergunta;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public OnActions getOnActions() {
        return onActions;
    }

    public void setOnActions(OnActions onActions) {
        this.onActions = onActions;
    }

    public interface OnActions{
        void onOpenImage(int index);
    }

    public Bitmap getResizedBitmap(Bitmap image) {
        int width = image.getWidth();
        int height = image.getHeight();

        return Bitmap.createScaledBitmap(image, width / 3, height / 3, true);
    }

}

