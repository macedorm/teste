package br.com.jjconsulting.mobile.dansales;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import br.com.jjconsulting.mobile.dansales.adapter.MessagesAdapter;
import br.com.jjconsulting.mobile.dansales.asyncTask.AsyncTaskMessage;
import br.com.jjconsulting.mobile.dansales.base.BaseFragment;
import br.com.jjconsulting.mobile.dansales.data.MessageFilter;
import br.com.jjconsulting.mobile.dansales.database.ChatDao;
import br.com.jjconsulting.mobile.dansales.database.MessageDao;
import br.com.jjconsulting.mobile.dansales.kotlin.PedidoTrackingDetailActivity;
import br.com.jjconsulting.mobile.dansales.kotlin.model.PedidoTrackingMessage;
import br.com.jjconsulting.mobile.dansales.model.Message;
import br.com.jjconsulting.mobile.dansales.util.Config;
import br.com.jjconsulting.mobile.jjlib.base.ItemClickSupport;
import br.com.jjconsulting.mobile.jjlib.util.LogUser;

public class MessageFragment extends BaseFragment implements AsyncTaskMessage.OnAsyncResponse {

    private static final String KEY_FILTER_RESULT_STATE = "filter_result_state";
    private static final int FILTER_REQUEST_CODE = 1;

    private static final int MESSAGE_DETAIL_REQUEST_CODE = 99;


    private AsyncTaskMessage mAsyncTaskMessage;
    private List<Message> mMessages;
    private MessageDao messageDao;
    private MessageFilter mMessageFilter;
    private MessagesAdapter mMessagesAdapter;

    private RecyclerView mMessageRecyclerView;
    private LinearLayout mListEmptyLinearLayout;
    private ImageButton mListEmptyImageButton;
    private Menu mMenu;

    private String mNome;

    private boolean mIsStartLoading;

    private int indexOffSet;
    private int indexSelected;

    public MessageFragment() {
    }

    public static MessageFragment newInstance() {
        return new MessageFragment();
    }

    @Override
    public void onResume() {

        if(indexSelected != -1){
            Message message = mMessagesAdapter.getMessages().get(indexSelected);
            if(message != null && !message.isRead()){
                message.setRead(true);
                messageDao.setMessagemLida(message);
                mMessagesAdapter.updateItem(message, indexSelected);
            }
            indexSelected = -1;
        }

        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        View view = inflater.inflate(R.layout.fragment_message, container, false);

        indexSelected  = -1;

        // if is there any data sent, get the filter
        if (savedInstanceState != null && savedInstanceState.containsKey(
                KEY_FILTER_RESULT_STATE)) {
            mMessageFilter = (MessageFilter) savedInstanceState.getSerializable(
                    KEY_FILTER_RESULT_STATE);
        }

        mMessageRecyclerView = view.findViewById(R.id.message_recycler_view);

        mListEmptyLinearLayout = view.findViewById(R.id.list_empty_text_view);
        mListEmptyImageButton = view.findViewById(R.id.list_empty_image_button);
        mListEmptyImageButton.setOnClickListener(viewListEmpty -> {
            openFilter();
        });

        messageDao = new MessageDao(getActivity());
        findMessage();

        mMessageRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        DividerItemDecoration divider = new DividerItemDecoration(mMessageRecyclerView.getContext(), DividerItemDecoration.VERTICAL);
        divider.setDrawable(getContext().getResources().getDrawable(R.drawable.custom_divider));
        mMessageRecyclerView.addItemDecoration(divider);

        mMessageRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = LinearLayoutManager.class.cast(
                        recyclerView.getLayoutManager());
                int totalItemCount = layoutManager.getItemCount() - 1;
                int lastVisible = layoutManager.findLastCompletelyVisibleItemPosition();
                boolean endHasBeenReached = lastVisible >= totalItemCount;

                if (totalItemCount > 0 && endHasBeenReached && !mMessagesAdapter.isFinishPagination()) {
                    mMessageRecyclerView.post(() -> {
                        mMessagesAdapter.setFinishPagination(true);
                        addIndexOffSet();
                        loadMessagesPaginacao(false);
                    });
                }
            }
        });

        ItemClickSupport.addTo(mMessageRecyclerView).setOnItemClickListener(
                (recyclerView, position, v) -> {
                    try {
                        indexSelected = position;
                        Message message = mMessagesAdapter.getMessages().get(position);

                        if(message.getIdMessage() < 0 && !message.getTitle().contains("Pedido EDI")){
                            ChatDao chatDao = new ChatDao(getActivity());
                            PedidoTrackingMessage pedidoTrackingMessage = chatDao.getMessage(message.getIdMessage());

                            startActivity(PedidoTrackingDetailActivity.Companion.newIntentPush(getActivity(),
                                    pedidoTrackingMessage.getNf(), pedidoTrackingMessage.getSerial(), pedidoTrackingMessage.getCnpj()));

                            return;
                        }

                        startActivityForResult(MessageDetailActivity.newIntent(getActivity(), message), MESSAGE_DETAIL_REQUEST_CODE);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                });

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mMessageFilter != null) {
            outState.putSerializable(KEY_FILTER_RESULT_STATE, mMessageFilter);
        }
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, MenuInflater inflater) {
        mMenu = menu;

        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.search_menu, menu);
        inflater.inflate(R.menu.filter_menu, menu);
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
                mNome = newText;
                if (mMessageFilter == null) {
                    mMessageFilter = new MessageFilter();
                }
                findMessage();
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
            case R.id.menu_cancel_filter:
                mMessageFilter = new MessageFilter();
                findMessage();
                return true;
            case R.id.action_legendas:
                showLegend();
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
                    findMessage(data);
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }

    @Override
    public void processFinish(List<Message> messages) {

        mMessages = messages;
        if (mIsStartLoading) {
            mMessagesAdapter = new MessagesAdapter(mMessages, getContext());
            mMessageRecyclerView.setAdapter(mMessagesAdapter);
        } else {
            mMessagesAdapter.removeLoadingFooter();
            mMessagesAdapter.updateData(mMessages);
        }

        if (!mMessagesAdapter.isFinishPagination()) {
            mMessagesAdapter.addLoadingFooter();
        }

        if (mMessagesAdapter.getMessages().size() == 0) {
            mListEmptyLinearLayout.setVisibility(View.VISIBLE);
            mMessageRecyclerView.setVisibility(View.GONE);
        } else {
            mListEmptyLinearLayout.setVisibility(View.GONE);
            mMessageRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void findMessage() {
        findMessage(null);
    }

    private void findMessage(Intent data) {
        try {
            mMessagesAdapter = new MessagesAdapter(new ArrayList<>(), getContext());
            mMessageRecyclerView.setAdapter(mMessagesAdapter);

            if (data != null && data.hasExtra(ClienteFilterActivity.FILTER_RESULT_DATA_KEY)) {
                mMessageFilter = (MessageFilter) data.getSerializableExtra(
                        MessageFilterActivity.FILTER_RESULT_DATA_KEY);

                mListEmptyLinearLayout.setVisibility(View.GONE);
                mMessageRecyclerView.setVisibility(View.VISIBLE);
            }

            reloadMessages();
            mMessagesAdapter.addLoadingFooter();

            setFilterMenuIcon();

        } catch (Exception ex) {
            LogUser.log(Config.TAG, "applyFilterOnData: " + ex.toString());
        }
    }

    private void reloadMessages() {
        resetIndexOffSet();
        mMessagesAdapter.resetData();
        loadMessagesPaginacao(true);
    }

    private void loadMessagesPaginacao(boolean isStartLoading) {
        this.mIsStartLoading = isStartLoading;
        if (mAsyncTaskMessage != null) {
            mAsyncTaskMessage.cancel(true);
        }

        mAsyncTaskMessage = new AsyncTaskMessage(getActivity(), indexOffSet, mMessageFilter, mNome, messageDao,  this);
        mAsyncTaskMessage.execute();
    }

    private void setFilterMenuIcon() {
        // setup filter's icon
        if (mMenu != null) {
            MenuItem filterItem = mMenu.findItem(R.id.menu_filter);
            filterItem.setIcon((mMessageFilter == null || mMessageFilter.isEmpty()) ?
                    R.drawable.ic_filter : R.drawable.ic_filter_applied);

            mListEmptyImageButton.setColorFilter(
                    (mMessageFilter == null || mMessageFilter.isEmpty()) ?
                            getResources().getColor(R.color.statusNoFilter) :
                            getResources().getColor(R.color.statusFilter));
        }
    }


    private void openFilter() {
        Intent filterIntent = new Intent(getActivity(), MessageFilterActivity.class);
        if (mMessageFilter != null) {
            filterIntent.putExtra(MessageFilterActivity.FILTER_RESULT_DATA_KEY,
                    mMessageFilter);
        }
        startActivityForResult(filterIntent, FILTER_REQUEST_CODE);
    }

    private void showLegend() {
        Dialog mDialogSubtitles = new Dialog(getContext(), android.R.style.Theme_Translucent_NoTitleBar);
        mDialogSubtitles.setCancelable(true);
        mDialogSubtitles.setContentView(R.layout.dialog_legenda_mensagem);
        TextView tvOkSubTitles = mDialogSubtitles.findViewById(R.id.ok_button);
        tvOkSubTitles.setOnClickListener(v -> mDialogSubtitles.dismiss());
        mDialogSubtitles.show();
    }

    public void resetIndexOffSet() {
        indexOffSet = 0;
    }

    public void addIndexOffSet() {
        this.indexOffSet += Config.SIZE_PAGE;
    }

    public interface OnMessageClickListener {
        void onMessageClick(Message message);
    }

}
