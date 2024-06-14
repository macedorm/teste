package br.com.jjconsulting.mobile.dansales.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import br.com.jjconsulting.mobile.dansales.R;
import br.com.jjconsulting.mobile.dansales.data.SyncPesquisaPilar;
import br.com.jjconsulting.mobile.dansales.util.Config;
import br.com.jjconsulting.mobile.dansales.util.PicassoCustom;
import br.com.jjconsulting.mobile.jjlib.util.FormatUtils;
import br.com.jjconsulting.mobile.jjlib.util.LogUser;
import br.com.jjconsulting.mobile.jjlib.util.TextUtils;

public class PesquisaNotasAdapter extends RecyclerView.Adapter<PesquisaNotasAdapter.ViewHolder> {

    private static final int ITEM = 0;
    private static final int LOADING = 1;

    private Context mContext;
    private List<SyncPesquisaPilar> mPesquisaPilar;

    private boolean finishPagination;
    private boolean isLoadingAdded = false;

    private final int STATUS_GREEN = 1;
    private final int STATUS_YELLOW = 2;
    private final int STATUS_RED = 3;
    private final int STATUS_GRAY = 4;
    private final int STATUS_RELATORIO = 5;

    public PesquisaNotasAdapter(Context context, List<SyncPesquisaPilar> pesquisaPilar) {
        mContext = context;
        mPesquisaPilar = pesquisaPilar;

        int status = -1;

        if(pesquisaPilar == null){
            mPesquisaPilar = new ArrayList<>();
        }

        for(SyncPesquisaPilar item: pesquisaPilar){
            if(item.getStatus() != status){
                status = item.getStatus();
                item.setShowHeader(true);

                switch (item.getStatus()){
                    case STATUS_GREEN:
                        item.setTextHeader(mContext.getString(R.string.checklist_status_nota_pontou_header));
                        break;
                    case STATUS_YELLOW:
                        item.setTextHeader(mContext.getString(R.string.checklist_status_oportunidade_header));
                        break;
                    case STATUS_RED:
                        item.setTextHeader(mContext.getString(R.string.checklist_status_nota_n_pontou_header));
                        break;
                    case STATUS_GRAY:
                        item.setTextHeader(mContext.getString(R.string.checklist_status_nota_n_respondeu_header));
                        break;
                    case STATUS_RELATORIO:
                        item.setTextHeader("");
                        break;
                }

            }
        }

        if (mPesquisaPilar.size() < Config.SIZE_PAGE) {
            finishPagination = true;
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View perguntaView;

        switch (viewType) {
            case ITEM:
                perguntaView = inflater.inflate(R.layout.item_pesquisa_nota, parent, false);
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
                SyncPesquisaPilar syncPesquisaPilar = mPesquisaPilar.get(position);
                PicassoCustom.setImage(mContext, syncPesquisaPilar.getUrlImg(), holder.mIconPilarImageView);

                holder.mTitlePilarTextView.setText(syncPesquisaPilar.getPilar());
                holder.mNotePilarTextView.setText(syncPesquisaPilar.getNota()  + " pts / " + syncPesquisaPilar.getPeso() + " pts");

                switch (syncPesquisaPilar.getStatus()){
                    case STATUS_GREEN:
                         holder.mContainerPilarCardView.setCardBackgroundColor(mContext.getResources().getColor(R.color.statusNotaCheckListPontou));
                         holder.mObsPilarTextView.setVisibility(View.GONE);
                         break;
                    case STATUS_YELLOW:
                        holder.mContainerPilarCardView.setCardBackgroundColor(mContext.getResources().getColor(R.color.statusNotaCheckListOportunidade));
                        holder.mObsPilarTextView.setVisibility(View.VISIBLE);
                        break;
                    case STATUS_RED:
                        holder.mContainerPilarCardView.setCardBackgroundColor(mContext.getResources().getColor(R.color.statusNotaCheckListNPontou));

                        if(TextUtils.isNullOrEmpty(syncPesquisaPilar.getInforme())){
                            holder.mObsPilarTextView.setVisibility(View.GONE);
                        } else {
                            holder.mObsPilarTextView.setVisibility(View.VISIBLE);
                            holder.mObsPilarTextView.setText(TextUtils.firstLetterUpperCase(syncPesquisaPilar.getInforme()));
                        }

                        break;
                    case STATUS_GRAY:
                        holder.mContainerPilarCardView.setCardBackgroundColor(mContext.getResources().getColor(R.color.statusNotaCheckListNRespondeu));
                        holder.mObsPilarTextView.setVisibility(View.GONE);
                        break;
                    case STATUS_RELATORIO:
                        holder.mContainerPilarCardView.setCardBackgroundColor(mContext.getResources().getColor(R.color.colorPrimary));
                        holder.mObsPilarTextView.setVisibility(View.GONE);
                        break;
                }

                holder.mHeaderContainerViewGroup.setVisibility(syncPesquisaPilar.isShowHeader() ? View.VISIBLE:View.GONE);
                holder.mHeaderTextView.setText(TextUtils.isNullOrEmpty(syncPesquisaPilar.getTextHeader()) ? "":syncPesquisaPilar.getTextHeader());
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        return (position == mPesquisaPilar.size() - 1 && isLoadingAdded) ? LOADING : ITEM;
    }

    @Override
    public int getItemCount() {
        return mPesquisaPilar.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView mTitlePilarTextView;
        private TextView mNotePilarTextView;
        private TextView mObsPilarTextView;
        private TextView mHeaderTextView;
        private ImageView mIconPilarImageView;
        private CardView mContainerPilarCardView;
        private ViewGroup mHeaderContainerViewGroup;

        public ViewHolder(View itemView) {
            super(itemView);

            switch (itemView.getId()) {
                case ITEM:
                    mTitlePilarTextView = itemView.findViewById(R.id.title_pilar_text_view);
                    mNotePilarTextView = itemView.findViewById(R.id.note_pilar_text_view);
                    mObsPilarTextView = itemView.findViewById(R.id.obs_pilar_text_view);
                    mIconPilarImageView = itemView.findViewById(R.id.icon_pilar_imageView);
                    mContainerPilarCardView = itemView.findViewById(R.id.container_pilar_card_view);
                    mHeaderTextView = itemView.findViewById(R.id.header_pilar_text_view);
                    mHeaderContainerViewGroup = itemView.findViewById(R.id.header_linear_layout);
                    break;
            }
        }
    }

    public boolean isFinishPagination() {
        return finishPagination;
    }

    public void setFinishPagination(boolean finishPagination) {
        this.finishPagination = finishPagination;
    }


}
