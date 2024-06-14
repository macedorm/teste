package br.com.jjconsulting.mobile.dansales.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import br.com.jjconsulting.mobile.dansales.R;
import br.com.jjconsulting.mobile.dansales.model.PesquisaPergunta;
import br.com.jjconsulting.mobile.dansales.util.Config;
import br.com.jjconsulting.mobile.jjlib.util.FormatUtils;
import br.com.jjconsulting.mobile.jjlib.util.ImageCameraGallery;
import br.com.jjconsulting.mobile.jjlib.util.LogUser;
import br.com.jjconsulting.mobile.jjlib.util.TextUtils;

public class PerguntasPesquisaAdapter extends RecyclerView.Adapter<PerguntasPesquisaAdapter.ViewHolder> {

    private static final int ITEM = 0;
    private static final int LOADING = 1;

    private Context mContext;
    private List<PesquisaPergunta> mPerguntas;

    private boolean finishPagination;
    private boolean isLoadingAdded = false;

    public PerguntasPesquisaAdapter(Context context, List<PesquisaPergunta> perguntas) {
        mContext = context;
        mPerguntas = perguntas;

        if (mPerguntas.size() < Config.SIZE_PAGE) {
            finishPagination = true;
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View perguntaView;

        switch (viewType) {
            case ITEM:
                perguntaView = inflater.inflate(R.layout.item_perguntas, parent, false);
                perguntaView.setId(viewType);
                break;
            case LOADING:
                perguntaView = inflater.inflate(R.layout.item_progress, parent, false);
                perguntaView.setId(viewType);
                break;
            default:
                perguntaView = null;
                break;
        }
        return new ViewHolder(perguntaView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case ITEM:
                PesquisaPergunta pesquisaPergunta = mPerguntas.get(position);

                String pergunta = TextUtils.firstLetterUpperCase(pesquisaPergunta.getDescPergunta());
                if (pesquisaPergunta.isObrigatoria() && !pesquisaPergunta.isDisable()) {
                    pergunta += "*";
                }


                holder.mPerguntaCardView.setVisibility(mPerguntas.get(position).isDisable() ? View.GONE:View.VISIBLE);

                holder.mPerguntaTextView.setText(pergunta);
                holder.mRespotaTextView.setEnabled(!mPerguntas.get(position).isDisable());
                holder.mPerguntaTextView.setEnabled(!mPerguntas.get(position).isDisable());
                holder.mRespotaTextView.setText(mContext.getString(R.string.unanswered_question));
                holder.mRespotaTextView.setVisibility(View.VISIBLE);
                holder.mRespostaImageView.setVisibility(View.GONE);

                if (!mPerguntas.get(position).isDisable()) {
                    if (mPerguntas.get(position).getRespostaPesquisa() != null &&
                            mPerguntas.get(position).getRespostaPesquisa().size() > 0) {
                        switch (mPerguntas.get(position).getTipo()) {
                            case MULTI_SELECAO:
                                String textMult = mPerguntas.get(position).getRespostaPesquisa().get(0).getResposta();
                                if (!TextUtils.isNullOrEmpty(textMult)) {
                                    textMult = textMult.replace(";", ",");
                                    holder.mRespotaTextView.setText(TextUtils.firstLetterUpperCase(textMult));
                                }
                                break;
                            case SORTIMENTO_OBRIGATORIO:
                            case SORTIMENTO_RECOMENDADO:
                            case SORTIMENTO_INOVACAO:
                            case SORTIMENTO_OBRIGATORIO_PRECO:

                                String selected[] = null;

                                try{
                                    selected = mPerguntas.get(position).getRespostaPesquisa().get(0).getResposta().split("\\|");
                                } catch (Exception ex){
                                    LogUser.log(ex.toString());
                                }

                                if(selected != null && (selected.length > 1 || (selected.length == 1 && !TextUtils.isNullOrEmpty(selected[0])))){
                                    holder.mRespotaTextView.setText(mContext.getString(R.string.selected) + " " + String.valueOf(selected.length - 1));
                                }
                                break;
                            case UPLOAD_IMAGEM:
                                String image = mPerguntas.get(position).getRespostaPesquisa().get(0).getResposta();
                                if (!TextUtils.isNullOrEmpty(image)) {
                                    holder.mRespotaTextView.setVisibility(View.GONE);
                                    holder.mRespostaImageView.setVisibility(View.VISIBLE);
                                    holder.mRespostaImageView.setImageBitmap(ImageCameraGallery.decodeBase64Bitmap(image));
                                }
                                break;
                            case NUMERO_DECIMAL:
                                String numberDecimal = mPerguntas.get(position).getRespostaPesquisa().get(0).getResposta();
                                if (!TextUtils.isNullOrEmpty(numberDecimal)) {
                                    holder.mRespotaTextView.setText(numberDecimal.replace(".", ","));
                                }
                                break;
                            case MOEDA:
                                String money = mPerguntas.get(position).
                                        getRespostaPesquisa().get(0).getResposta();
                                if (!TextUtils.isNullOrEmpty(money)) {
                                    money = FormatUtils.toBrazilianRealCurrency(Double.parseDouble(
                                            money.replace(",", ".")));
                                    holder.mRespotaTextView.setText(money);
                                }
                                break;
                            case DATA:
                                try {
                                    holder.mRespotaTextView.setText(FormatUtils.toDateTimeText(mPerguntas.get(position).
                                            getRespostaPesquisa().get(0).getResposta()));
                                } catch (Exception ex) {
                                    LogUser.log(Config.TAG, ex.toString());
                                }
                                break;
                            default:
                                String text = mPerguntas.get(position).getRespostaPesquisa().get(0).getResposta();
                                if (!TextUtils.isNullOrEmpty(text)) {
                                    holder.mRespotaTextView.setText(TextUtils.firstLetterUpperCase(text));
                                }
                                break;
                        }
                    }
                }
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        return (position == mPerguntas.size() - 1 && isLoadingAdded) ? LOADING : ITEM;
    }

    @Override
    public int getItemCount() {
        return mPerguntas.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private CardView mPerguntaCardView;
        private TextView mPerguntaTextView;
        private TextView mRespotaTextView;
        private ImageView mRespostaImageView;

        public ViewHolder(View itemView) {
            super(itemView);

            switch (itemView.getId()) {
                case ITEM:
                    mPerguntaCardView = itemView.findViewById(R.id.pergunta_pesquisa_card_view);
                    mPerguntaTextView = itemView.findViewById(R.id.pergunta_pesquisa_text_view);
                    mRespotaTextView = itemView.findViewById(R.id.pergunta_pesquisa_resp_text_view);
                    mRespostaImageView = itemView.findViewById(R.id.pergunta_pesquisa_resp_imageView);
                    break;
            }
        }
    }

    public void updateData(List<PesquisaPergunta> listPerguntas) {
        if (listPerguntas.size() == 0 || listPerguntas.size() < Config.SIZE_PAGE) {
            finishPagination = true;
        } else {
            finishPagination = false;

        }
        mPerguntas.addAll(listPerguntas);

        notifyDataSetChanged();
        LogUser.log(Config.TAG, "Pagination - listClientes size: " + mPerguntas.size() + " - page size: " + Config.SIZE_PAGE + " - finishPagination: " + finishPagination);
    }

    public void resetData() {
        mPerguntas.clear();
        finishPagination = false;
    }

    public boolean isFinishPagination() {
        return finishPagination;
    }

    public void setFinishPagination(boolean finishPagination) {
        this.finishPagination = finishPagination;
    }

    public List<PesquisaPergunta> getPerguntas() {
        return mPerguntas;
    }

    public void add(PesquisaPergunta pesquisaPergunta) {
        mPerguntas.add(pesquisaPergunta);
        notifyItemInserted(mPerguntas.size() - 1);
    }

    public void addLoadingFooter() {
        isLoadingAdded = true;
        add(new PesquisaPergunta());
    }

    public void removeLoadingFooter() {
        isLoadingAdded = false;
        int position = mPerguntas.size() - 1;
        PesquisaPergunta item = getItem(position);
        if (item != null) {
            mPerguntas.remove(position);
            notifyItemRemoved(position);
        }
    }

    public PesquisaPergunta getItem(int position) {
        return mPerguntas.get(position);
    }


    public void updateItem(PesquisaPergunta pesquisaPergunta, int index) {
        mPerguntas.set(index, pesquisaPergunta);
        notifyItemChanged(index);
    }

    public void updateRange(List<PesquisaPergunta> listPesquisaPergunta, int index) {
        mPerguntas = listPesquisaPergunta;
        //notifyItemRangeChanged(index, mPerguntas.size());
        notifyItemRangeChanged(0, mPerguntas.size());
    }

}
