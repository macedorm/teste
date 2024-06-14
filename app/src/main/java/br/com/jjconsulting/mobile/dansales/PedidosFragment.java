package br.com.jjconsulting.mobile.dansales;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import br.com.jjconsulting.mobile.dansales.adapter.LegendaPedidoAdapter;
import br.com.jjconsulting.mobile.dansales.adapter.PedidosAdapter;
import br.com.jjconsulting.mobile.dansales.asyncTask.AsyncTaskPedidos;
import br.com.jjconsulting.mobile.dansales.base.BaseFragment;
import br.com.jjconsulting.mobile.dansales.business.PedidoBusiness;
import br.com.jjconsulting.mobile.dansales.data.PedidoFilter;
import br.com.jjconsulting.mobile.dansales.database.PedidoDao;
import br.com.jjconsulting.mobile.dansales.database.StatusPedidoDao;
import br.com.jjconsulting.mobile.dansales.model.Pedido;
import br.com.jjconsulting.mobile.dansales.model.PedidoViewType;
import br.com.jjconsulting.mobile.dansales.model.StatusPedido;
import br.com.jjconsulting.mobile.dansales.model.Usuario;
import br.com.jjconsulting.mobile.dansales.service.Current;
import br.com.jjconsulting.mobile.dansales.service.CurrentActionPedido;
import br.com.jjconsulting.mobile.dansales.util.Config;
import br.com.jjconsulting.mobile.dansales.util.UsuarioUtils;
import br.com.jjconsulting.mobile.jjlib.base.ItemClickSupport;
import br.com.jjconsulting.mobile.jjlib.util.DialogsCustom;
import br.com.jjconsulting.mobile.jjlib.util.LogUser;

public class PedidosFragment extends BaseFragment implements AsyncTaskPedidos.OnAsyncResponse {

    private static final String ARG_TYPE = "arg_type";
    private static final String KEY_FILTER_RESULT_STATE = "filter_result_state";
    private static final int FILTER_REQUEST_CODE = 1;

    private PedidoViewType mType;

    private PedidoDao mPedidoDao;
    private PedidoFilter mPedidoFilter;
    private AsyncTaskPedidos mLoadPedidosTask;

    private OnPedidoClickListener mOnPedidoClickListener;

    private FloatingActionButton mAddPedidoFloatingActionButton;
    private PedidosAdapter mPedidosAdapter;
    private RecyclerView mPedidosRecyclerView;

    private LinearLayout mListEmptyLinearLayout;
    private LinearLayout linearLayout;

    private ImageButton mListEmptyImageButton;
    private Menu mMenu;

    private List<Pedido> mPedidos;

    private String mRazaoSocialPedido;

    private boolean mIsStartLoading;


    public PedidosFragment() {

    }

    public static PedidosFragment newInstance(PedidoViewType type) {
        PedidosFragment fragment = new PedidosFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_TYPE, type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (CurrentActionPedido.getInstance().isUpdateListPedido()) {
            CurrentActionPedido.getInstance().setUpdateListPedido(false);
            findPedidos();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mType = (PedidoViewType) getArguments().get(ARG_TYPE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        View view = inflater.inflate(R.layout.fragment_pedidos, container, false);

        // if is there any data sent, get the filter
        if (savedInstanceState != null && savedInstanceState.containsKey(
                KEY_FILTER_RESULT_STATE)) {
            mPedidoFilter = (PedidoFilter) savedInstanceState.getSerializable(
                    KEY_FILTER_RESULT_STATE);
        } else {
            createPedidoFilterStatusDefault();
        }

        mListEmptyLinearLayout = view.findViewById(R.id.list_empty_text_view);
        mListEmptyImageButton = view.findViewById(R.id.list_empty_image_button);
        mListEmptyImageButton.setOnClickListener(viewClick -> {
            openFilter();
        });

        mPedidosRecyclerView = view.findViewById(R.id.pedidos_recycler_view);
        mAddPedidoFloatingActionButton = view.findViewById(R.id.add_pedido_floating_action_button);

        mPedidoDao = new PedidoDao(getActivity());

        findPedidos();

        mPedidosRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        DividerItemDecoration divider = new DividerItemDecoration(mPedidosRecyclerView.getContext(), DividerItemDecoration.VERTICAL);
        divider.setDrawable(getContext().getResources().getDrawable(R.drawable.custom_divider));
        mPedidosRecyclerView.addItemDecoration(divider);

        mPedidosRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = LinearLayoutManager.class.cast(
                        recyclerView.getLayoutManager());
                int totalItemCount = layoutManager.getItemCount() - 1;
                int lastVisible = layoutManager.findLastCompletelyVisibleItemPosition();
                boolean endHasBeenReached = lastVisible >= totalItemCount;
                if (totalItemCount > 0 && endHasBeenReached
                        && !mPedidosAdapter.isFinishPagination()) {
                    mPedidosRecyclerView.post(() -> {
                        mPedidosAdapter.setFinishPagination(true);
                        mPedidoDao.addIndexOffSet();
                        loadPedidosPaginacao(false);
                    });
                }
            }
        });

        linearLayout = view.findViewById(R.id.pedidos_linear_layout);

        if (mType == PedidoViewType.PEDIDO) {
            ItemClickSupport.addTo(mPedidosRecyclerView).setOnItemLongClickListener((recyclerView, position, v) -> {
                createPopupMenu(v, position);
                return false;
            });
        }

        ItemClickSupport.addTo(mPedidosRecyclerView).setOnItemClickListener(
                (recyclerView, position, v) -> {
                    try {
                        Pedido pedido = mPedidosAdapter.getPedidos().get(position);
                        if (pedido.getCodigo() != null) {
                            if (pedido.getCodigoStatus() != StatusPedido.ENVIADO_ADM && mType == PedidoViewType.LIBERACAO) {
                                mOnPedidoClickListener.onPedidoClick(PedidoViewType.PEDIDO, pedido, false);
                            } else if (pedido.getCodigoStatus() != StatusPedido.ENVIADO_APROVACAO && mType == PedidoViewType.APROVACAO) {
                                mOnPedidoClickListener.onPedidoClick(PedidoViewType.APROVACAO, pedido, false);
                            } else {
                                mOnPedidoClickListener.onPedidoClick(mType, pedido, false);
                            }
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                });

        mAddPedidoFloatingActionButton.setOnClickListener(v -> {
            createNewPedido();
        });

       checkAddbutton();

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mPedidoFilter != null) {
            outState.putSerializable(KEY_FILTER_RESULT_STATE, mPedidoFilter);
        }
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, MenuInflater inflater) {
        mMenu = menu;

        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.search_menu, menu);
        inflater.inflate(R.menu.filter_menu, menu);

        if (mType == PedidoViewType.PEDIDO) {
            inflater.inflate(R.menu.pedidos_menu, menu);
        }

        inflater.inflate(R.menu.cancel_filter_menu, menu);
        inflater.inflate(R.menu.cliente_label_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint(getString(R.string.action_search_hint));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mRazaoSocialPedido = newText;
                if (mPedidoFilter == null) {
                    mPedidoFilter = new PedidoFilter();
                }
                findPedidos();
                return true;
            }
        });

        setFilterMenuIcon();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_filter:
                openFilter();
                return true;
            case R.id.action_add_new_pedido:
                createNewPedido();
                return true;
            case R.id.menu_cancel_filter:
                mPedidoFilter = new PedidoFilter();
                findPedidos();
                return true;
            case R.id.action_legendas:
                showLegendPedido();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case FILTER_REQUEST_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    findPedidos(data);
                }
                break;
            case Config.REQUEST_APRO_LIB:
                if (resultCode == Activity.RESULT_OK) {
                    mPedidosAdapter.resetData();
                    findPedidos();
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }

    @Override
    public void processFinish(List<Pedido> pedidos) {
        mPedidos = pedidos;
        if (mIsStartLoading) {
            mPedidosAdapter = new PedidosAdapter(getActivity(), mPedidos);
            mPedidosRecyclerView.setAdapter(mPedidosAdapter);
        } else {
            mPedidosAdapter.removeLoadingFooter();
            mPedidosAdapter.updateData(mPedidos);
        }

        if (!mPedidosAdapter.isFinishPagination()) {
            mPedidosAdapter.addLoadingFooter();
        }

        if (mPedidosAdapter.getPedidos().size() == 0) {
            mListEmptyLinearLayout.setVisibility(View.VISIBLE);
            mPedidosRecyclerView.setVisibility(View.GONE);
        } else {
            mListEmptyLinearLayout.setVisibility(View.GONE);
            mPedidosRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnPedidoClickListener) {
            mOnPedidoClickListener = (OnPedidoClickListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnPedidoClickListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mOnPedidoClickListener = null;
    }

    private void showLegendPedido() {
       try {
           Dialog mDialogSubtitles = new Dialog(getContext(),
                   android.R.style.Theme_Translucent_NoTitleBar);
           mDialogSubtitles.setCancelable(true);
           mDialogSubtitles.setContentView(R.layout.dialog_legenda_pedido_dynamic);

           StatusPedidoDao statusDao = new StatusPedidoDao(getContext());
           ArrayList<StatusPedido> listStatus = statusDao.getAll();

           RecyclerView mLegendaRecyclerView = mDialogSubtitles.findViewById(R.id.list_legenda_recycler_view);

           mLegendaRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));


           LegendaPedidoAdapter legendaPedidoAdapter = new LegendaPedidoAdapter(getContext(), listStatus);
           mLegendaRecyclerView.setAdapter(legendaPedidoAdapter);
           mLegendaRecyclerView.setNestedScrollingEnabled(false);
           mLegendaRecyclerView.setHasFixedSize(true);

           if (mLegendaRecyclerView != null) {
               int size = (int) getResources().getDimension(R.dimen.size_status_pedido) * listStatus.size();
               mLegendaRecyclerView.getLayoutParams().height = size;
           }


          /* for (StatusPedido st : listStatus) {
               String textViewID = "pedido_status" + st.getCodigo();
               int resID = getResources().getIdentifier(textViewID, "id",
                       getActivity().getPackageName());
               TextView lbl = mDialogSubtitles.findViewById(resID);
               lbl.setText(st.getNome());
           }*/

           TextView tvOkSubTitles = mDialogSubtitles.findViewById(R.id.ok_button);
           tvOkSubTitles.setOnClickListener(view -> mDialogSubtitles.dismiss());
           mDialogSubtitles.show();
       }catch (Exception ex){
           LogUser.log(Config.TAG, ex.toString());
       }
    }

    private void findPedidos() {
        findPedidos(null);
    }

    private void findPedidos(Intent data) {
        try {
            mPedidosAdapter = new PedidosAdapter(getActivity(), new ArrayList<>());
            mPedidosRecyclerView.setAdapter(mPedidosAdapter);

            // if is there any data sent, get the filter
            if (data != null && data.hasExtra(PedidoFilterActivity.FILTER_RESULT_DATA_KEY)) {
                mPedidoFilter = (PedidoFilter) data.getSerializableExtra(
                        PedidoFilterActivity.FILTER_RESULT_DATA_KEY);

                mListEmptyLinearLayout.setVisibility(View.GONE);
                mPedidosRecyclerView.setVisibility(View.VISIBLE);
            }

            reloadPedidos();
            mPedidosAdapter.addLoadingFooter();
            setFilterMenuIcon();
        } catch (Exception ex) {
            LogUser.log(Config.TAG, "applyFilterOnData: " + ex.toString());
        }
    }

    private void reloadPedidos() {
        mPedidoDao.resetIndexOffSet();
        mPedidosAdapter.resetData();
        loadPedidosPaginacao(true);
    }

    private void loadPedidosPaginacao(boolean isStartLoading) {
        this.mIsStartLoading = isStartLoading;
        if (mLoadPedidosTask != null) {
            mLoadPedidosTask.cancel(true);
        }
        mLoadPedidosTask = new AsyncTaskPedidos(getContext(), mType, mPedidoFilter, mRazaoSocialPedido,
                mPedidoDao, this);
        mLoadPedidosTask.execute();
    }

    private void setFilterMenuIcon() {
        // setup filter's icon
        if (mMenu != null) {
            MenuItem filterItem = mMenu.findItem(R.id.menu_filter);
            filterItem.setIcon((mPedidoFilter == null || mPedidoFilter.isEmpty()) ?
                    R.drawable.ic_filter : R.drawable.ic_filter_applied);

            mListEmptyImageButton.setColorFilter(
                    (mPedidoFilter == null || mPedidoFilter.isEmpty()) ?
                            getResources().getColor(R.color.statusNoFilter) :
                            getResources().getColor(R.color.statusFilter));
        }
    }

    private void createPopupMenu(View view, int position) {
        PopupMenu popup = new PopupMenu(getContext(), view, Gravity.END);
        popup.getMenuInflater().inflate(R.menu.long_click_pedido, popup.getMenu());

        PedidoBusiness pedidoBusiness = new PedidoBusiness();
        boolean isModeEdit = pedidoBusiness.vizualizationMode(mPedidosAdapter.getPedidos().get(position), mType);

        if (!isModeEdit) {
            popup.getMenu().getItem(0).setTitle(getString(R.string.action_title_visualization));
            popup.getMenu().getItem(1).setVisible(false);
            popup.getMenu().getItem(3).setVisible(false);
        }

        popup.setOnMenuItemClickListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.action_sync:
                    mOnPedidoClickListener.onPedidoClick(PedidoViewType.PEDIDO, mPedidosAdapter.getPedidos().get(position), true);
                    break;
                case R.id.action_open:
                    mOnPedidoClickListener.onPedidoClick(PedidoViewType.PEDIDO, mPedidosAdapter.getPedidos().get(position), false);
                    break;
                case R.id.action_log:
                    startActivity(LogPedidoActivity.newIntent(getContext(),
                            mPedidosAdapter.getPedidos().get(position).getCodigo()));
                    break;
                case R.id.action_delete:

                    dialogsDefault.showDialogQuestion(getString(R.string.detail_pedido_delete), dialogsDefault.DIALOG_TYPE_QUESTION, new DialogsCustom.OnClickDialogQuestion() {
                        @Override
                        public void onClickPositive() {
                            mAddPedidoFloatingActionButton.show();
                            checkAddbutton();

                            try {
                                if (position < mPedidosAdapter.getPedidos().size()) {
                                    pedidoBusiness.deletePedido(getContext(), mPedidosAdapter.getPedidos().get(position));
                                }
                            } catch (Exception ex) {
                                LogUser.log(Config.TAG, ex.toString());
                            }

                            findPedidos();
                        }

                        @Override
                        public void onClickNegative() {
                            mAddPedidoFloatingActionButton.show();
                            checkAddbutton();
                        }
                    });
                    break;
            }
            return false;
        });
        popup.show();
    }

    private void openFilter() {
        Intent filterIntent = new Intent(getActivity(), PedidoFilterActivity.class);
        if (mPedidoFilter != null) {
            filterIntent.putExtra(PedidoFilterActivity.FILTER_RESULT_DATA_KEY,
                    mPedidoFilter);
        }
        startActivityForResult(filterIntent, FILTER_REQUEST_CODE);
    }

    private void createNewPedido() {
        startActivity(PickClienteActivity.newIntent(getActivity()));
    }

    /**
     * Para telas de Liberação/Aprovação insere filtro padrão correspondente
     */
    private void createPedidoFilterStatusDefault() {
        if (PedidoViewType.PEDIDO != mType) {
            StatusPedidoDao statusPedidoDao = new StatusPedidoDao(getContext());

            int codigoStatus;
            if (PedidoViewType.LIBERACAO == mType) {
                codigoStatus = StatusPedido.ENVIADO_ADM;
            } else {
                codigoStatus = StatusPedido.ENVIADO_APROVACAO;
            }

            StatusPedido statusPedido = statusPedidoDao.get(codigoStatus);
            mPedidoFilter = new PedidoFilter();
            mPedidoFilter.setStatus(statusPedido);
        }
    }

    public void checkAddbutton(){
        Usuario usuario = Current.getInstance(getContext()).getUsuario();
        if ((usuario.getPerfil().isPermiteRotaGuiada() && UsuarioUtils.isPromotor(usuario.getCodigoFuncao()))
                || mType == PedidoViewType.APROVACAO || mType == PedidoViewType.LIBERACAO) {
            mAddPedidoFloatingActionButton.hide();
        }
    }

    public interface OnPedidoClickListener {
        void onPedidoClick(PedidoViewType type, Pedido pedido, boolean forceSync);
    }
}
