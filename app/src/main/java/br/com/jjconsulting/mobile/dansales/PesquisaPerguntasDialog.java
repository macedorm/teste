package br.com.jjconsulting.mobile.dansales;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.com.jjconsulting.mobile.dansales.database.PesquisaPerguntaDao;
import br.com.jjconsulting.mobile.dansales.model.PesquisaPergunta;
import br.com.jjconsulting.mobile.dansales.model.PesquisaResposta;
import br.com.jjconsulting.mobile.dansales.util.JJFormAnswersView;
import br.com.jjconsulting.mobile.jjlib.util.Config;
import br.com.jjconsulting.mobile.jjlib.util.ImageCameraGallery;
import br.com.jjconsulting.mobile.jjlib.util.LogUser;

public class PesquisaPerguntasDialog extends Dialog {

    private PesquisaPerguntaDao mPesquisaPerguntaDao;
    private PesquisaPergunta mPergunta;

    private OnPerguntasClickListener mOnPerguntasClickListener;

    private ImageButton mRespostaAntButton;
    private ImageButton mRespostaCancelButton;
    private ImageButton mRespostaOKButton;

    private Activity activity;

    private String codigoCliente;

    private LinearLayout container;

    private TextView answersTextView;

    private JJFormAnswersView mJJFormAnswersView;
    private List<JJFormAnswersView> parentJJFormAnswersViewList;

    private JJFormAnswersView.OnActions onActions;

    private int total;
    private int now;
    private int indexFormView;

    private PesquisaResposta pesquisaRespostaObrig;

    private ArrayList<PesquisaResposta> pesquisaRespostaDep;

    private Date currentDate;

    public PesquisaPerguntasDialog(Activity activity, OnPerguntasClickListener onPerguntasClickListener,
                                   PesquisaPergunta pergunta, Date currentDate, String codigoCliente, int total, int now) {
        super(activity);
        this.activity = activity;
        this.mOnPerguntasClickListener = onPerguntasClickListener;
        this.mPergunta = pergunta;
        this.codigoCliente = codigoCliente;
        this.indexFormView = -1;
        this.total = total;
        this.now = now;
        this.mPesquisaPerguntaDao = new PesquisaPerguntaDao(activity, currentDate);
        this.parentJJFormAnswersViewList = new ArrayList<>();
        this.currentDate = currentDate;

        onActions = (index)-> {
            indexFormView = index;
        };
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        this.setCancelable(true);

        switch (mPergunta.getTipo()){
            case SORTIMENTO_OBRIGATORIO:
            case SORTIMENTO_RECOMENDADO:
            case SORTIMENTO_INOVACAO:
            case SORTIMENTO_OBRIGATORIO_PRECO:
                setContentView(R.layout.dialog_fragment_perguntas_not_scroll);
                break;
            default:
                setContentView(R.layout.dialog_fragment_perguntas);
                break;
        }


        container = findViewById(R.id.container_answers);
        answersTextView = findViewById(R.id.answers_textView);

        createJJFormAnswers();

        mRespostaCancelButton = findViewById(R.id.cancel_image_view);
        mRespostaCancelButton.setOnClickListener(view -> {
            activity.getWindow().setSoftInputMode(
                    WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            dismiss();
        });

        mRespostaAntButton = findViewById(R.id.previous_image_view);
        mRespostaAntButton.setOnClickListener(view -> {
            activity.getWindow().setSoftInputMode(
                    WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            dismiss();

            mOnPerguntasClickListener.onPerguntaClickOK(null);
        });

        mRespostaOKButton = findViewById(R.id.next_image_view);
        mRespostaOKButton.setOnClickListener(view -> {
            boolean isCreate;
            isCreate = mJJFormAnswersView.createAnswer();
            mPergunta = mJJFormAnswersView.getPergunta();

            int index = 0;

            for(JJFormAnswersView jjFormAnswersView:parentJJFormAnswersViewList){
                if(isCreate){
                    isCreate = jjFormAnswersView.createAnswer();
                } else {
                    jjFormAnswersView.createAnswer();
                }

                mPergunta.getPesquisaPerguntaParent().set(index, jjFormAnswersView.getPergunta());
                index++;
            }

            if(isCreate){
                mOnPerguntasClickListener.onPerguntaClickOK(mPergunta);

                activity.getWindow().setSoftInputMode(
                        WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                dismiss();
            } else {
                Toast toast = Toast.makeText(getContext(), "Foram encontrados alguns erros na suas respostas", Toast.LENGTH_SHORT);
                toast.show();
            }

            setPositionAnswer();
        });

        setPositionAnswer();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ImageCameraGallery.OPEN_CAMERA && resultCode == Activity.RESULT_OK) {
            try {
                if (indexFormView == 0) {
                    mJJFormAnswersView.onActivityResult(requestCode, resultCode, data, getContext());
                } else if (indexFormView > 0) {
                    parentJJFormAnswersViewList.get(indexFormView - 1).onActivityResult(requestCode, resultCode, data, getContext());
                }
            }catch (Exception ex){
                LogUser.log(Config.TAG, ex.toString());
            }
        }
    }

    private void createJJFormAnswers(){
        mJJFormAnswersView = new JJFormAnswersView(activity, mPergunta, codigoCliente, pesquisaRespostaObrig, pesquisaRespostaDep, currentDate,0, total, now);
        mJJFormAnswersView.setOnActions(onActions);
        container.addView(mJJFormAnswersView.renderView());
        mJJFormAnswersView.getPergunta().getPesquisaPerguntaParent();

        if(mPergunta.getPesquisaPerguntaParent() != null){
            int index = 1;
            for(PesquisaPergunta pesquisaPerguntaParent: mPergunta.getPesquisaPerguntaParent()) {
                JJFormAnswersView mJJFormAnswersView = new JJFormAnswersView(activity, pesquisaPerguntaParent, codigoCliente, pesquisaRespostaObrig, pesquisaRespostaDep, currentDate, index, total, now);
                mJJFormAnswersView.setOnActions(onActions);
                parentJJFormAnswersViewList.add(mJJFormAnswersView);
                container.addView(mJJFormAnswersView.renderView());
                index++;
            }
        }
    }

    private void setPositionAnswer(){
        answersTextView.setText((now + 1)  + "/" +  total);
    }

    public PesquisaResposta getPesquisaResposta() {
        return pesquisaRespostaObrig;
    }

    public void setPesquisaResposta(PesquisaResposta pesquisaResposta) {
        this.pesquisaRespostaObrig = pesquisaResposta;
    }

    public ArrayList<PesquisaResposta> getPesquisaRespostaDep() {
        return pesquisaRespostaDep;
    }

    public void setPesquisaRespostaDep(ArrayList<PesquisaResposta> pesquisaRespostaDep) {
        this.pesquisaRespostaDep = pesquisaRespostaDep;
    }

    public interface OnPerguntasClickListener {
        void onPerguntaClickOK(PesquisaPergunta pesquisaPergunta);
    }
}


