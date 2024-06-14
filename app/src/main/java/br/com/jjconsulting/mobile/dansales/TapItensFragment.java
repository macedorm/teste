package br.com.jjconsulting.mobile.dansales;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.VolleyError;

import java.io.InputStreamReader;
import java.util.ArrayList;

import br.com.jjconsulting.mobile.dansales.adapter.TapItensAdapter;
import br.com.jjconsulting.mobile.dansales.connectionController.BaseConnection;
import br.com.jjconsulting.mobile.dansales.connectionController.TapConnection;
import br.com.jjconsulting.mobile.dansales.data.ValidationDan;
import br.com.jjconsulting.mobile.dansales.model.RetProcess;
import br.com.jjconsulting.mobile.dansales.model.TapDetail;
import br.com.jjconsulting.mobile.dansales.model.TapItem;
import br.com.jjconsulting.mobile.dansales.service.Current;
import br.com.jjconsulting.mobile.dansales.util.Config;
import br.com.jjconsulting.mobile.dansales.util.ETapUtils;
import br.com.jjconsulting.mobile.dansales.util.ManagerSystemUpdate;
import br.com.jjconsulting.mobile.dansales.util.UnidadeNegocioUtils;
import br.com.jjconsulting.mobile.jjlib.Connection;
import br.com.jjconsulting.mobile.jjlib.OnPageSelected;
import br.com.jjconsulting.mobile.jjlib.base.ItemClickSupport;
import br.com.jjconsulting.mobile.jjlib.model.RetError;
import br.com.jjconsulting.mobile.jjlib.model.ValidationLetter;
import br.com.jjconsulting.mobile.jjlib.util.DialogsCustom;
import br.com.jjconsulting.mobile.jjlib.util.LogUser;

public class TapItensFragment extends TapBaseFragment implements OnPageSelected {

    private ArrayList<TapItem> mItens;

    private TapConnection tapConnection;

    private FloatingActionButton mAddItemFloattingActionButton;
    private TapItensAdapter mTapItensAdapter;
    private RecyclerView mTapItensRecyclerView;
    private ViewGroup mNoItemsViewGroup;
    private ProgressDialog progressDialog;

    private int positionRemove;

    public TapItensFragment() {
    }

    public static TapItensFragment newInstance() {
        return new TapItensFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tap_itens, container, false);

        mTapItensRecyclerView = view.findViewById(R.id.itens_tap_recycler_view);
        mAddItemFloattingActionButton = view.findViewById(R.id.add_item_floating_action_button);
        mNoItemsViewGroup = view.findViewById(R.id.no_items_view_group);

        mItens = new ArrayList<>();

        mTapItensAdapter = new TapItensAdapter(getActivity(), mItens);

        mTapItensRecyclerView.setAdapter(mTapItensAdapter);
        mTapItensRecyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        DividerItemDecoration divider = new DividerItemDecoration(
                mTapItensRecyclerView.getContext(), DividerItemDecoration.VERTICAL);
        divider.setDrawable(getContext().getResources().getDrawable(R.drawable.custom_divider));
        mTapItensRecyclerView.addItemDecoration(divider);

        mAddItemFloattingActionButton.setOnClickListener(v -> {
            TapDetail tapDetail = getCurrentTap();


            ValidationDan validationLetter = new ValidationDan();

            if (tapDetail.getCabec().getTapMasterContrato() == null ||
                    tapDetail.getCabec().getTapMasterContrato().getMasterDC150().length() == 0) {
                validationLetter.addError(getString(R.string.message_etap_send_validation_mc_error));
            }

            if (tapDetail.getCabec().getAnoMesAcao() == null ||
                    tapDetail.getCabec().getAnoMesAcao().length() == 0) {
                validationLetter.addError(getString(R.string.message_etap_send_validation_mes_acao_error));
            }

            if (tapDetail.getCabec().getTapRegiao() == null ||
                    tapDetail.getCabec().getTapRegiao().getIdRegiao() == 0) {
                validationLetter.addError(getString(R.string.message_etap_send_validation_regiao_error));
            }


            if (validationLetter.getMessage().size() > 0) {
                showDialogValidationError(validationLetter);
            } else {
                Intent addNewItem = TapItemDetailActivity.newIntent(getActivity(), getCurrentTap(), -1);
                startActivityForResult(addNewItem, Config.REQUEST_INCLUIR_ITEM);
            }
        });


        ItemClickSupport.addTo(mTapItensRecyclerView).setOnItemClickListener(
                (recyclerView, position, v) -> {
                    Intent addNewItem = TapItemDetailActivity.newIntent(getActivity(), getCurrentTap(), position);
                    startActivityForResult(addNewItem, Config.REQUEST_ALTERAR_ITEM);
                });


        tapConnection = new TapConnection(getContext(), new BaseConnection.ConnectionListener() {
            @Override
            public void onSucess(String response, int typeConnection, InputStreamReader reader, ArrayList<String[]> responseList) {
                showProgressDialog(false);

                try {
                    getActivity().runOnUiThread(()-> {
                        mItens.remove(positionRemove);

                        TapDetail tapDetail = getCurrentTap();
                        tapDetail.setItens(mItens);
                        setCurrentTap(tapDetail);
                        mTapItensAdapter.notifyItemRemoved(positionRemove);
                        setComponentsVisibility();

                        mAddItemFloattingActionButton.show();
                    });
                } catch (Exception ex) {
                    showMessageError(getString(R.string.message_etap_remove_item_error));
                }
            }


            @Override
            public void onError(VolleyError volleyError, int code,  int typeConnection, String response) {
                showProgressDialog(false);

                if(volleyError != null &&  volleyError.networkResponse != null){
                    if(code == Connection.AUTH_FAILURE || code == Connection.SERVER_ERROR){
                        ValidationLetter errorConnection = gson.fromJson(response, ValidationLetter.class);
                        if(ManagerSystemUpdate.isRequiredUpadate(getContext(), errorConnection.getMessage())){
                            return;
                        }

                        showMessageError(errorConnection.getMessage());
                        return;
                    }

                }

                showMessageError(getString(R.string.message_etap_remove_item_error));
            }
        });


        createDialogProgress();

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case Config.REQUEST_INCLUIR_ITEM:
                if (resultCode == Activity.RESULT_OK) {
                    TapItem insertedItem = TapItemDetailActivity.getTapItemFromResultIntent(data);
                    mItens.add(0, insertedItem);

                    TapDetail tapDetail = getCurrentTap();
                    tapDetail.setItens(mItens);
                    setCurrentTap(tapDetail);

                    mTapItensAdapter.notifyItemInserted(0);
                    mTapItensRecyclerView.scrollToPosition(0);
                    setComponentsVisibility();
                }
                break;
            case Config.REQUEST_ALTERAR_ITEM:
                if (resultCode == Activity.RESULT_OK) {
                    int position = TapItemDetailActivity.getPositionFromResultIntent(data);
                    TapItem savedItem = TapItemDetailActivity.getTapItemFromResultIntent(data);
                    mItens.set(position, savedItem);

                    TapDetail tapDetail = getCurrentTap();
                    tapDetail.setItens(mItens);
                    setCurrentTap(tapDetail);

                    mTapItensAdapter.notifyItemChanged(position);
                    mTapItensRecyclerView.scrollToPosition(position);
                    setComponentsVisibility();
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }

    @Override
    public void onPageSelected(int position) {
        bind();
    }

    private void bind() {
        ArrayList mItensTemp = new ArrayList();
        mItensTemp.addAll(getCurrentTap().getItens());
        mItens = mItensTemp;

        mTapItensAdapter = new TapItensAdapter(getActivity(), mItens);
        mTapItensRecyclerView.setAdapter(mTapItensAdapter);

        setComponentsVisibility();
    }

    private void showFields() {

    }

    @SuppressLint("RestrictedApi")
    private void setComponentsVisibility() {
        boolean hasItems = mItens.size() > 0;

        if (isEditMode()) {
            mNoItemsViewGroup.setVisibility(hasItems ? View.GONE : View.VISIBLE);
        } else {
            mNoItemsViewGroup.setVisibility(View.GONE);
        }

        mTapItensRecyclerView.setVisibility(hasItems ? View.VISIBLE : View.INVISIBLE);

        boolean isAddDelete = ETapUtils.isAddDeleteItem(mItens);

        mAddItemFloattingActionButton.setVisibility(isAddDelete ? View.VISIBLE : View.GONE);

        if (isAddDelete) {
            ItemClickSupport.addTo(mTapItensRecyclerView).setOnItemLongClickListener(
                    (recyclerView, position, v) -> {
                        dialogsDefault.showDialogQuestion(
                                getString(R.string.mensagem_exluir_produto),
                                dialogsDefault.DIALOG_TYPE_QUESTION,
                                new DialogsCustom.OnClickDialogQuestion() {
                                    @Override
                                    public void onClickPositive() {
                                        showProgressDialog(true);

                                        positionRemove = position;

                                        TapDetail tapDetail = getCurrentTap();
                                        tapConnection.deleteItemETap(tapDetail.getItens().get(position), tapDetail.getCabec());
                                    }

                                    @Override
                                    public void onClickNegative() {
                                    }
                                });

                        return true;
                    });
        }

    }

    private void showProgressDialog(boolean isShow) {
        if (getActivity().getWindow().getDecorView().isShown()) {
            if (!isShow && progressDialog.isShowing()) {
                progressDialog.dismiss();
            } else {
                progressDialog.show();
            }
        }
    }

    private void createDialogProgress() {
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setCancelable(false);
        String msg = getString(R.string.aguarde);
        progressDialog.setMessage(msg);
    }

    private void showDialogValidationError(ValidationDan validation) {
        try {
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

            TapValidationDialogFragment tapValidationDialogFragment =
                    TapValidationDialogFragment.newInstance(validation);
            tapValidationDialogFragment.show(fragmentManager, "");
            fragmentManager.executePendingTransactions();

            tapValidationDialogFragment.getDialog().setOnDismissListener(dialogInterface -> {

            });
        } catch (Exception ex) {
            LogUser.log(Config.TAG, ex.toString());
        }
    }


}
