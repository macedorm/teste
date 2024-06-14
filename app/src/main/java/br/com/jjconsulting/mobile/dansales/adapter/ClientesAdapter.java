package br.com.jjconsulting.mobile.dansales.adapter;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import br.com.jjconsulting.mobile.dansales.R;
import br.com.jjconsulting.mobile.dansales.model.Cliente;
import br.com.jjconsulting.mobile.dansales.util.ClienteUtils;
import br.com.jjconsulting.mobile.dansales.util.Config;
import br.com.jjconsulting.mobile.dansales.util.PlanoCampoUtils;
import br.com.jjconsulting.mobile.jjlib.util.LogUser;

public class ClientesAdapter extends RecyclerView.Adapter<ClientesAdapter.ViewHolder> {

    private static final int ITEM = 0;
    private static final int LOADING = 1;
    private List<Cliente> mClientes;
    private boolean finishPagination;
    private boolean isLoadingAdded = false;
    private boolean isMultiSelection;

    private Context mContext;

    public ClientesAdapter(List<Cliente> clientes, boolean isMultiSelection, Context context) {
        this.isMultiSelection = isMultiSelection;

        mClientes = clientes;

        if (mClientes.size() < Config.SIZE_PAGE) {
            finishPagination = true;
        }

        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View clienteView;

        switch (viewType) {
            case ITEM:
                clienteView = inflater.inflate(R.layout.item_cliente, parent, false);
                clienteView.setId(viewType);
                break;
            case LOADING:
                clienteView = inflater.inflate(R.layout.item_progress, parent, false);
                clienteView.setId(viewType);
                break;
            default:
                clienteView = null;
                break;
        }

        return new ViewHolder(clienteView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        switch (getItemViewType(position)) {

            case ITEM:
                Cliente cliente = mClientes.get(position);
                holder.mStatusCreditoImageView.setColorFilter(ContextCompat.getColor(mContext, ClienteUtils.getStatusCreditoColorResourceId(cliente.getStatusCredito())));

                holder.mNomeTextView.setText(cliente.getNome());
                holder.mCodigoTextView.setText(cliente.getCodigo());
                holder.mEndereco1TextView.setText(cliente.getEndereco());
                holder.mEndereco2TextView.setText(String.format("%1$s, %2$s", cliente.getMunicipio(),
                        cliente.getUf()));

                if(cliente.getPlanoCampo() != null){
                    holder.mPlanoCampoTextView.setVisibility(View.VISIBLE);
                    holder.mPlanoCampoTextView.setText(Html.fromHtml(PlanoCampoUtils.createPlanoCampo(cliente.getPlanoCampo())));
                } else {
                    holder.mPlanoCampoTextView.setVisibility(View.GONE);
                }

                holder.mContainerMultiRelativeLayout.setVisibility(isMultiSelection ? View.VISIBLE:View.GONE);

                holder.mSelectedCheckBox.setTag(position);
                holder.mSelectedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        int index = Integer.parseInt(compoundButton.getTag().toString());
                        mClientes.get(index).setSelected(b);
                    }
                });


                break;
            case LOADING:

                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        return (position == mClientes.size() - 1 && isLoadingAdded) ? LOADING : ITEM;
    }

    @Override
    public int getItemCount() {
        return mClientes.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private RelativeLayout mContainerMultiRelativeLayout;

        private ImageView mStatusCreditoImageView;

        private CheckBox mSelectedCheckBox;

        private TextView mNomeTextView;
        private TextView mCodigoTextView;
        private TextView mEndereco1TextView;
        private TextView mEndereco2TextView;
        private TextView mPlanoCampoTextView;

        public ViewHolder(View itemView) {
            super(itemView);

            switch (itemView.getId()) {
                case ITEM:
                    mStatusCreditoImageView = itemView.findViewById(R.id.cliente_status_credito_image_view);
                    mNomeTextView = itemView.findViewById(R.id.cliente_nome_text_view);
                    mCodigoTextView = itemView.findViewById(R.id.cliente_codigo_text_view);
                    mEndereco1TextView = itemView.findViewById(R.id.cliente_endereco1_text_view);
                    mEndereco2TextView = itemView.findViewById(R.id.cliente_endereco2_text_view);
                    mPlanoCampoTextView = itemView.findViewById(R.id.plano_campo_text_view);
                    mContainerMultiRelativeLayout = itemView.findViewById(R.id.container_multiselection_relative_layout);
                    mSelectedCheckBox = itemView.findViewById(R.id.selected_check_box);
                    break;
            }
        }
    }

    public void updateData(List<Cliente> listClientes) {
        if (listClientes.size() == 0 || listClientes.size() < Config.SIZE_PAGE) {
            finishPagination = true;
        } else {
            finishPagination = false;

        }
        mClientes.addAll(listClientes);

        notifyDataSetChanged();
        LogUser.log(Config.TAG, "Pagination - listClientes size: " + mClientes.size() + " - page size: " + Config.SIZE_PAGE + " - finishPagination: " + finishPagination);
    }

    public void resetData() {
        mClientes.clear();
        finishPagination = false;
    }

    public boolean isFinishPagination() {
        return finishPagination;
    }

    public void setFinishPagination(boolean finishPagination) {
        this.finishPagination = finishPagination;
    }

    public List<Cliente> getClientes() {
        return mClientes;
    }

    public void add(Cliente cliente) {
        mClientes.add(cliente);
        notifyItemInserted(mClientes.size() - 1);
    }

    public void addLoadingFooter() {
        isLoadingAdded = true;
        add(new Cliente());
    }

    public void removeLoadingFooter() {
        isLoadingAdded = false;
        int position = mClientes.size() - 1;
        Cliente item = getItem(position);
        if (item != null) {
            mClientes.remove(position);
            notifyItemRemoved(position);
        }
    }

    public Cliente getItem(int position) {
        return mClientes.get(position);
    }


}
