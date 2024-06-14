package br.com.jjconsulting.mobile.dansales.adapter;

import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import br.com.jjconsulting.mobile.dansales.R;
import br.com.jjconsulting.mobile.dansales.data.RotasFilter;
import br.com.jjconsulting.mobile.dansales.model.Rotas;
import br.com.jjconsulting.mobile.dansales.model.Usuario;
import br.com.jjconsulting.mobile.dansales.util.Config;
import br.com.jjconsulting.mobile.dansales.util.RotaGuiadaUtils;
import br.com.jjconsulting.mobile.jjlib.util.LogUser;
import br.com.jjconsulting.mobile.jjlib.util.TextUtils;

public class RotaGuiadaAdapter extends RecyclerView.Adapter<RotaGuiadaAdapter.ViewHolder> {

    private static final int ITEM = 0;
    private static final int LOADING = 1;

    private List<Rotas> mRotas;

    private Context context;

    private boolean isLoadingAdded = false;
    private boolean isUnrealized;
    private boolean isTitle;

    public RotaGuiadaAdapter(Context context, List<Rotas> rotas, RotasFilter rotasFilter, boolean isUnrealized) {
        this.mRotas = rotas;
        this.context = context;
        this.isUnrealized = isUnrealized;

        if(rotasFilter != null && rotasFilter.getHierarquiaComercial() != null){
            for(Usuario usuarios: rotasFilter.getHierarquiaComercial()){
                for(int ind = 0; ind < mRotas.size();ind++){
                    Rotas itemRota = mRotas.get(ind);

                    if(itemRota.getCodRegFunc().equals(usuarios.getCodigo())){
                        isTitle = true;
                        mRotas.get(ind).setNomeUser(usuarios.getNomeReduzido());
                        ind = mRotas.size();
                    }

                }
            }
        }

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view;

        switch (viewType) {
            case ITEM:
                view = inflater.inflate(R.layout.item_rota_guiada, parent, false);
                view.setId(viewType);
                break;
            case LOADING:
                view = inflater.inflate(R.layout.item_progress, parent, false);
                view.setId(viewType);
                break;
            default:
                view = null;
                break;
        }

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        switch (getItemViewType(position)) {

            case ITEM:
                Rotas rotas = mRotas.get(position);

                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                        isTitle ? (int)context.getResources().getDimension(R.dimen.rg_list_size): (int)context.getResources().getDimension(R.dimen.rg_small_list_size));
                holder.mContainerRelativeLayout.setLayoutParams(params);

                if(isTitle){
                    if(!TextUtils.isNullOrEmpty(rotas.getNomeUser())){
                        holder.mUserTextView.setVisibility(View.VISIBLE);
                        holder.mUserTextView.setText(rotas.getNomeUser());
                    } else {
                        holder.mUserTextView.setVisibility(View.GONE);
                    }
                } else {
                        holder.mUserTextView.setVisibility(View.GONE);
                }


                holder.mStatusImageView.setImageResource(
                        RotaGuiadaUtils.getStatusRGImageResourceId(rotas.isRota(), rotas.getStatus(), isUnrealized));

                try {
                    holder.mNomeTextView.setText(rotas.getCliente().getNome());
                    holder.mEndereco1TextView.setText(rotas.getCliente().getEndereco());
                    holder.mEndereco2TextView.setText(String.format("%1$s, %2$s", rotas.getCliente().getMunicipio(),
                            rotas.getCliente().getUf()));
                }catch (Exception ex){
                    LogUser.log(Config.TAG, ex.toString());
                }

                if(rotas.isRota() || rotas.getStatus() == RotaGuiadaUtils.STATUS_RG_FINALIZADO
                        || rotas.getStatus() == RotaGuiadaUtils.STATUS_RG_FORA_ROTA){
                    holder.mForaRotaLinearLayout.setVisibility(View.GONE);
                } else {
                    holder.mForaRotaLinearLayout.setVisibility(View.VISIBLE);
                }

                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        return (position == mRotas.size() - 1 && isLoadingAdded) ? LOADING : ITEM;
    }

    @Override
    public int getItemCount() {
        return mRotas.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView mStatusImageView;
        private TextView mUserTextView;
        private TextView mNomeTextView;
        private TextView mEndereco1TextView;
        private TextView mEndereco2TextView;
        private LinearLayout mForaRotaLinearLayout;
        private RelativeLayout mContainerRelativeLayout;

        public ViewHolder(View itemView) {
            super(itemView);

            switch (itemView.getId()) {
                case ITEM:
                    mContainerRelativeLayout = itemView.findViewById(R.id.rg_container_item_relative_layout);
                    mUserTextView = itemView.findViewById(R.id.rg_user_text_view);
                    mStatusImageView = itemView.findViewById(R.id.rg_status_image_view);
                    mNomeTextView = itemView.findViewById(R.id.rg_nome_text_view);
                    mEndereco1TextView = itemView.findViewById(R.id.rg_endereco1_text_view);
                    mEndereco2TextView = itemView.findViewById(R.id.rg_endereco2_text_view);
                    mForaRotaLinearLayout = itemView.findViewById(R.id.fora_rota_linear_layout);
                    break;
            }
        }
    }



    public void resetData() {
        mRotas.clear();
    }

    public List<Rotas> getRotas() {
        return mRotas;
    }

    public void add(Rotas rotas) {
        mRotas.add(rotas);
        notifyItemInserted(mRotas.size() - 1);
    }

    public void addLoadingFooter() {
        isLoadingAdded = true;
        add(new Rotas());
    }

    public void removeLoadingFooter() {
        isLoadingAdded = false;
        int position = mRotas.size() - 1;
        Rotas item = getItem(position);
        if (item != null) {
            mRotas.remove(position);
            notifyItemRemoved(position);
        }
    }

    public Rotas getItem(int position) {
        return mRotas.get(position);
    }

    public boolean isTitle() {
        return isTitle;
    }

    public void setTitle(boolean title) {
        isTitle = title;
    }

    public int getSizeItem(){
        if(context == null){
            return 0;
        } else {
            return isTitle ? (int)context.getResources().getDimension(R.dimen.rg_list_size): (int)context.getResources().getDimension(R.dimen.rg_small_list_size);
        }

    }

}
