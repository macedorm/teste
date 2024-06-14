package br.com.jjconsulting.mobile.dansales;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import br.com.jjconsulting.mobile.dansales.adapter.ItensPedidoAdapter;
import br.com.jjconsulting.mobile.dansales.business.PedidoBusiness;
import br.com.jjconsulting.mobile.dansales.database.ItemPedidoDao;
import br.com.jjconsulting.mobile.dansales.database.SortimentoDao;
import br.com.jjconsulting.mobile.dansales.model.ItemPedido;
import br.com.jjconsulting.mobile.dansales.model.ItensSortimento;
import br.com.jjconsulting.mobile.dansales.model.Pedido;
import br.com.jjconsulting.mobile.dansales.model.Produto;
import br.com.jjconsulting.mobile.dansales.model.TipoVenda;
import br.com.jjconsulting.mobile.dansales.service.CurrentActionPedido;
import br.com.jjconsulting.mobile.dansales.util.Config;
import br.com.jjconsulting.mobile.jjlib.OnPageSelected;
import br.com.jjconsulting.mobile.jjlib.base.ItemClickSupport;
import br.com.jjconsulting.mobile.jjlib.util.DialogsCustom;
import br.com.jjconsulting.mobile.jjlib.util.LogUser;
import br.com.jjconsulting.mobile.jjlib.util.TextUtils;

public class PedidoItensFragment extends PedidoBaseFragment implements OnPageSelected {

    private ArrayList<ItemPedido> mItens;
    private ItemPedidoDao mItemPedidoDao;

    private FloatingActionButton mAddItemFloattingActionButton;
    private ItensPedidoAdapter mItensPedidoAdapter;
    private RecyclerView mItensPedidoRecyclerView;
    private ViewGroup mNoItemsViewGroup;
    private ItensPedidoAdapter.OnAddRemove onAddRemove;

    public PedidoItensFragment() {
    }

    public static PedidoItensFragment newInstance() {
        return new PedidoItensFragment();
    }

    @Override
    public void onResume() {
        super.onResume();

        if(mAddItemFloattingActionButton != null){
            mAddItemFloattingActionButton.setEnabled(true);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        onAddRemove = new ItensPedidoAdapter.OnAddRemove() {
            @Override
            public void onClick(int id, boolean add) {
                ItemPedido itemPedido = null;
                int position = 0;

                for(int ind = 0; ind < mItens.size(); ind++){
                    if(mItens.get(ind).getId() == id){
                        itemPedido = mItens.get(ind);
                        position = ind;
                    }
                }

                if(itemPedido == null){
                    return;
                }

                boolean isNotUpdate = false;

                if(add){
                    if(itemPedido.getQuantidade() == -1){
                        itemPedido.setQuantidade(1);
                    } else {
                        itemPedido.setQuantidade(itemPedido.getQuantidade() + 1);
                    }
                } else {
                    if(itemPedido.getQuantidade() > 1){
                        itemPedido.setQuantidade(itemPedido.getQuantidade() - 1);
                    } else {
                        if(itemPedido.getProduto().getTipoSortimento() != null){
                            itemPedido.setQuantidade(-1);
                        } else {
                            isNotUpdate = true;
                        }
                    }
                }

                if(!isNotUpdate){
                    PedidoBusiness pedidoBusiness = new PedidoBusiness();
                    if(TipoVenda.REB.equals(getCurrentPedido().getCodigoTipoVenda())){
                        mItens.set(position,  pedidoBusiness.buildNewItemReb(getCurrentPedido(), itemPedido, 1));
                    } else {
                        mItens.set(position,  pedidoBusiness.buildNewItem(getCurrentPedido(), itemPedido));
                    }

                    setCurrentItens(mItens);
                    mItensPedidoAdapter.notifyItemChanged(position);

                    mItemPedidoDao.updateSimple(mItens.get(position));
                }

            }
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pedido_itens, container, false);

        mItensPedidoRecyclerView = view.findViewById(R.id.itens_pedido_recycler_view);
        mAddItemFloattingActionButton = view.findViewById(R.id.add_item_floating_action_button);
        mNoItemsViewGroup = view.findViewById(R.id.no_items_view_group);

        mItemPedidoDao = new ItemPedidoDao(getActivity());
        mItens = new ArrayList<>();

        final Pedido currentPedido = getCurrentPedido();

        String codigoUnNeg = "";
        String codigoTipoVenda = "";
        if(currentPedido != null){
            codigoUnNeg = currentPedido.getCodigoUnidadeNegocio();
            codigoTipoVenda = currentPedido.getCodigoTipoVenda() == null ?
                    TipoVenda.VENDA : currentPedido.getCodigoTipoVenda();
        }

        final boolean showSortimento = getCurrentUsuario()
                .getPerfil()
                .getPerfilVenda(codigoTipoVenda, codigoUnNeg)
                .isSortimentoHabilitado();

        mItensPedidoAdapter = new ItensPedidoAdapter(getActivity(), onAddRemove, mItens, codigoTipoVenda,
                showSortimento, isEditMode());

        mItensPedidoRecyclerView.setAdapter(mItensPedidoAdapter);
        mItensPedidoRecyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        DividerItemDecoration divider = new DividerItemDecoration(
                mItensPedidoRecyclerView.getContext(), DividerItemDecoration.VERTICAL);
        divider.setDrawable(getContext().getResources().getDrawable(R.drawable.custom_divider));
        mItensPedidoRecyclerView.addItemDecoration(divider);

        mAddItemFloattingActionButton.setOnClickListener(v -> {
            mAddItemFloattingActionButton.setEnabled(false);
            if (getCurrentPedido().getTipoVenda() == null) {
                dialogsDefault.showDialogMessage(
                        getString(R.string.add_no_item_detected),
                        dialogsDefault.DIALOG_TYPE_WARNING,
                        null);
                mAddItemFloattingActionButton.setEnabled(true);
            } else {
                Pedido pedido = getCurrentPedido();
                Intent addNewItem = PedidoItemDetailActivity.newIntent(getActivity(),
                        pedido, getLastInsertedProdutoDescription());
                startActivityForResult(addNewItem, Config.REQUEST_INCLUIR_ITEM);
            }
        });

        // using the same rules that webped, you can't edit a DAT item
        if (isEditMode() && !TipoVenda.DAT.equals(getCurrentPedido().getCodigoTipoVenda())) {
            ItemClickSupport.addTo(mItensPedidoRecyclerView).setOnItemClickListener(
                    (recyclerView, position, v) -> {
                        if (getCurrentPedido().getTipoVenda() == null) {
                            dialogsDefault.showDialogMessage(
                                    getString(R.string.add_no_item_detected),
                                    dialogsDefault.DIALOG_TYPE_WARNING,
                                    null);
                        } else {
                            try {
                                Pedido pedido = getCurrentPedido();

                                if (position < mItens.size()) {
                                    ItemPedido item = mItens.get(position);
                                    Intent editExistingItemIntent = PedidoItemDetailActivity.newIntent(
                                            getActivity(), pedido, item.getId(),
                                            getLastInsertedProdutoDescription());
                                    startActivityForResult(editExistingItemIntent,
                                            Config.REQUEST_ALTERAR_ITEM);
                                }

                            } catch (Exception ex) {
                                LogUser.log(Config.TAG, ex.toString());
                            }
                        }
                    });
        }

        if (isEditMode()) {
            ItemClickSupport.addTo(mItensPedidoRecyclerView).setOnItemLongClickListener(
                    (recyclerView, position, v) -> {
                        ItemPedido item = mItens.get(position);

                       /* if(mItens.get(position).getProduto().getTipoSortimento() != null && (mItens.get(position).getProduto().getTipoSortimento() == Produto.SORTIMENTO_TIPO_OBRIGATORIO
                        || mItens.get(position).getProduto().getTipoSortimento() == Produto.SORTIMENTO_TIPO_SUBSTITUTO)){
                            dialogsDefault.showDialogMessage(getString(R.string.block_delete_item), dialogsDefault.DIALOG_TYPE_WARNING, null);
                        } else {*/
                            dialogsDefault.showDialogQuestion(
                                    getString(R.string.mensagem_exluir_produto),
                                    dialogsDefault.DIALOG_TYPE_QUESTION,
                                    new DialogsCustom.OnClickDialogQuestion() {
                                        @Override
                                        public void onClickPositive() {
                                            mItemPedidoDao.delete(item.getId());
                                            mItens.remove(position);
                                            setCurrentItens(mItens);
                                            mItensPedidoAdapter.notifyItemRemoved(position);
                                            setComponentsVisibility();

                                            mAddItemFloattingActionButton.show();
                                        }

                                        @Override
                                        public void onClickNegative() {
                                            mAddItemFloattingActionButton.show();
                                        }
                                    });

                      //  }

                        return true;
                    });
        } else {
            mAddItemFloattingActionButton.hide();
        }


        bindPedido();
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        try {
            switch (requestCode) {
                case Config.REQUEST_INCLUIR_ITEM:
                    if (resultCode == Activity.RESULT_OK) {

                        String codigoProduto = data.getExtras().getString(PedidoItemDetailActivity.KEY_PRODUCT_EXIST);

                        if (TextUtils.isNullOrEmpty(codigoProduto)) {
                            int codigoItem = PedidoItemDetailActivity.getCodigoItemFromResultIntent(data);
                            ItemPedido insertedItem = mItemPedidoDao.get(getCurrentUsuario(),
                                    getCurrentPedido(), codigoItem, isEditMode());

                            Integer quantidadePedidoAnteriorTemp = mItemPedidoDao.getQuantidadePedidoAnterior(
                                    getCurrentUsuario().getCodigo(), getCurrentPedido().getCodigo(), insertedItem.getProduto().getCodigo());
                            int quantidadePedidoAnterior = quantidadePedidoAnteriorTemp == null ?
                                    0 : quantidadePedidoAnteriorTemp;
                            insertedItem.setQtdPedAnt(quantidadePedidoAnterior);

                            mItens.add(0, insertedItem);
                            mItensPedidoAdapter.setCodigoTipoVenda(getCurrentPedido().getCodigoTipoVenda());

                            setCurrentItens(mItens);
                            mItensPedidoAdapter.notifyItemInserted(0);
                            mItensPedidoRecyclerView.scrollToPosition(0);


                            setComponentsVisibility();
                        } else {

                            for (ItemPedido item : mItens) {
                                if (item.getProduto().getCodigo().equals(codigoProduto)) {
                                    Intent editExistingItemIntent = PedidoItemDetailActivity.newIntent(
                                            getActivity(), getCurrentPedido(), item.getId(),
                                            getLastInsertedProdutoDescription());
                                    startActivityForResult(editExistingItemIntent,
                                            Config.REQUEST_ALTERAR_ITEM);
                                }
                            }
                        }


                    } else if (resultCode == Activity.RESULT_OK) {

                    }

                    break;
                case Config.REQUEST_ALTERAR_ITEM:
                    if (resultCode == Activity.RESULT_OK) {
                        int codigoItem = PedidoItemDetailActivity.getCodigoItemFromResultIntent(data);
                        ItemPedido updatedItem = mItemPedidoDao.get(getCurrentUsuario(),
                                getCurrentPedido(), codigoItem, isEditMode());
                        if (updatedItem != null) {
                            int indexOfUpdatedItem = getIndexOf(mItens, updatedItem);
                            updatedItem.getProduto().setTipoSortimento(mItens.get(indexOfUpdatedItem).getProduto().getTipoSortimento());
                            updatedItem.setQtdPedAnt(mItens.get(indexOfUpdatedItem).getQtdPedAnt());
                            mItens.set(indexOfUpdatedItem, updatedItem);
                            setCurrentItens(mItens);


                            if (organizeItens()) {
                                mItens.clear();
                                mItens.addAll(getCurrentItens());
                                mItensPedidoAdapter.notifyDataSetChanged();
                                setComponentsVisibility();
                            } else {
                                mItensPedidoAdapter.notifyItemChanged(indexOfUpdatedItem);
                                setComponentsVisibility();
                            }

                            Pedido pedido = getCurrentPedido();

                            if (pedido.getJustificativa() != null) {
                                pedido.getJustificativa().remove(updatedItem.getProduto().getCodigo());
                                setCurrentPedido(pedido);
                            }
                        }
                    }
                    break;
                default:
                    super.onActivityResult(requestCode, resultCode, data);
                    break;
            }

        }catch (Exception ex){
            LogUser.log(ex.getMessage());
        }
    }

    @Override
    public void onPageSelected(int position) {
        CurrentActionPedido currentActionPedido = CurrentActionPedido.getInstance();
        if (currentActionPedido.isUpdateListItem()) {
            currentActionPedido.setUpdateListItem(false);
            mItens.clear();
            organizeItens();
            mItens.addAll(getCurrentItens());
            mItensPedidoAdapter.notifyDataSetChanged();
            setComponentsVisibility();
        }
    }

    private void bindPedido() {
        mItens.clear();
        organizeItens();

        if(mItens == null || getCurrentItens() == null){
            dialogsDefault.showDialogMessage(getString(R.string.loading_pedido_item), dialogsDefault.DIALOG_TYPE_WARNING, ()-> {
                getActivity().finish();
            });
        } else {
            mItens.addAll(getCurrentItens());
            mItensPedidoAdapter.notifyDataSetChanged();
            setComponentsVisibility();
        }
    }

    private boolean organizeItens() {
        boolean isOrganize = false;

        if(getCurrentPerfilVenda() != null && getCurrentPerfilVenda().isSortimentoHabilitado()){
            Pedido pedido = getCurrentPedido();
            SortimentoDao sortimentoDao = new SortimentoDao(getContext());
            ArrayList<ItensSortimento> itens = sortimentoDao.getItensSortimentoPedido(getCurrentUsuario(),  pedido, pedido.getCodigoSortimento(), pedido.getDataCadastro());

            if(itens != null && !itens.isEmpty() && !getCurrentItens().isEmpty()) {
                ArrayList<ItemPedido> itensPedidoObr = new ArrayList<>();
                ArrayList<ItemPedido> itensPedidoIno = new ArrayList<>();
                ArrayList<ItemPedido> itensPedidoRec = new ArrayList<>();
                ArrayList<ItemPedido> itensSubstitute = new ArrayList<>();
                ArrayList<ItemPedido> itensPedidoTemp = new ArrayList<>();

                //Separa os itens com e sem sortimento
                for (ItemPedido itemPedido : getCurrentItens()) {

                    if(itemPedido.getProduto().getTipoSortimento() != null && itemPedido.getProduto().getTipoSortimento() == Produto.SORTIMENTO_TIPO_SUBSTITUTO){
                        itemPedido.getProduto().setTipoSortimento(null);
                    }

                    if (itemPedido.getProduto().getTipoSortimento() == null) {
                        itensSubstitute.add(itemPedido);
                    } else {
                        switch (itemPedido.getProduto().getTipoSortimento()){
                            case Produto.SORTIMENTO_TIPO_OBRIGATORIO:
                                if(itemPedido.getQtdPedAnt() == -1){

                                    Integer quantidadePedidoAnteriorTemp = mItemPedidoDao.getQuantidadePedidoAnterior(
                                            getCurrentUsuario().getCodigo(), getCurrentPedido().getCodigo(), itemPedido.getProduto().getCodigo());
                                    int quantidadePedidoAnterior = quantidadePedidoAnteriorTemp == null ?
                                            0 : quantidadePedidoAnteriorTemp;
                                    itemPedido.setQtdPedAnt(quantidadePedidoAnterior);
                                }

                                itensPedidoObr.add(itemPedido);
                                break;
                            case Produto.SORTIMENTO_TIPO_INOVACAO:
                                itensPedidoIno.add(itemPedido);
                                break;
                            case Produto.SORTIMENTO_TIPO_RECOMENDADO:
                                itensPedidoRec.add(itemPedido);
                                break;
                        }
                    }
                }

                //Organiza por tipo de sortimento
                Collections.sort(itensPedidoObr, new ItemPedido());
                Collections.sort(itensPedidoRec, new ItemPedido());
                Collections.sort(itensPedidoIno, new ItemPedido());

                for (ItemPedido itemPedido : itensPedidoObr) {
                    ItemPedido itemPedidoSubstitute =  null;

                    if(itemPedido.getProduto().getTipoSortimento() == Produto.SORTIMENTO_TIPO_OBRIGATORIO){
                        itemPedidoSubstitute =  isItemSortimentoSubstitue(itemPedido, itensSubstitute, itens);
                    }

                    if(itemPedidoSubstitute != null){
                        itemPedido.setCodSubstituto(itemPedidoSubstitute.getId());
                        itensPedidoTemp.add(itemPedido);

                        itemPedidoSubstitute.getProduto().setTipoSortimento(Produto.SORTIMENTO_TIPO_SUBSTITUTO);
                        itensPedidoTemp.add(itemPedidoSubstitute);
                        itensSubstitute.remove(itemPedidoSubstitute);
                    } else {
                         itensPedidoTemp.add(itemPedido);
                    }
                }

                itensSubstitute.addAll(itensPedidoTemp);
                itensSubstitute.addAll(itensPedidoIno);
                itensSubstitute.addAll(itensPedidoRec);

                Collections.sort(itensSubstitute, (new Comparator<ItemPedido>() {
                    public int compare(ItemPedido i1, ItemPedido i2) {
                        int value1 = (i1.getQuantidade() == -1 ? 1:0);
                        int value2 = (i2.getQuantidade() == -1 ? 1:0);

                        return (value1 - value2);
                    }
                }));

                setCurrentItens(itensSubstitute);
                isOrganize = true;
            }
        }

        return isOrganize;
    }

    private ItemPedido isItemSortimentoSubstitue(ItemPedido item, ArrayList<ItemPedido> itensSubstitute, ArrayList<ItensSortimento> itensSortimento){

        String codSubstitute = null;

        for (ItensSortimento sortimento: itensSortimento){
            if(sortimento.getSKU().equals(item.getProduto().getCodigo())){
                codSubstitute = sortimento.getSUBSTITUTE();
                break;
            }
        }

        if(!TextUtils.isNullOrEmpty(codSubstitute)){
            for(ItemPedido itemPedidoSubstitue: itensSubstitute){
                if(itemPedidoSubstitue.getProduto().getCodigo().equals(codSubstitute)){
                    return itemPedidoSubstitue;
                }
            }

            return null;
        } else {
            return  null;
        }
    }

    private String getLastInsertedProdutoDescription() {
        if (mItens.size() == 0) {
            return null;
        }

        int biggestId = 0;
        int indexOfBiggestId = 0;

        for (int i = 0; i < mItens.size(); i++) {
            ItemPedido currentItem = mItens.get(i);
            if (currentItem.getId() > biggestId) {
                biggestId = currentItem.getId();
                indexOfBiggestId = i;
            }
        }

        return mItens.get(indexOfBiggestId).getProduto().getNome();
    }

    private int getIndexOf(List<ItemPedido> itens, ItemPedido itemPedido) {
        for (int i = 0; i < itens.size(); i++) {
            if (itens.get(i).getId() == itemPedido.getId()) {
                return i;
            }
        }

        return -1;
    }

    private void setComponentsVisibility() {
        boolean hasItems = mItens.size() > 0;
        mNoItemsViewGroup.setVisibility(hasItems ? View.GONE : View.VISIBLE);
        mItensPedidoRecyclerView.setVisibility(hasItems ? View.VISIBLE : View.INVISIBLE);
    }
}
