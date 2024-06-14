package br.com.jjconsulting.mobile.dansales;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentTransaction;

import java.util.List;

import br.com.jjconsulting.mobile.dansales.database.ChatDao;
import br.com.jjconsulting.mobile.dansales.database.MessageDao;
import br.com.jjconsulting.mobile.dansales.kotlin.PedidoTrackingDetailActivity;
import br.com.jjconsulting.mobile.dansales.kotlin.model.PedidoTrackingMessage;
import br.com.jjconsulting.mobile.dansales.model.Message;
import br.com.jjconsulting.mobile.dansales.util.JJSyncMessage;
import br.com.jjconsulting.mobile.jjlib.util.DialogsCustom;
import br.com.jjconsulting.mobile.jjlib.util.LogUser;

public class MessageDetailDialogFragment extends DialogFragment {

    private static final String ARG_CODIGO_PEDIDO = "codigo_pedido";
    private static final String FILTER_PRODUTO = "filter_produto";

    private MessageDao messageDao;

    private List<Message> mMessage;

    private TextView mTitleBarTypeImageView;
    private ImageButton mInfoMessageImageButton;
    private Button mOKButton;

    private int index;

    public MessageDetailDialogFragment() { }

    public static MessageDetailDialogFragment newInstance(List<Message> message) {
        MessageDetailDialogFragment fragment = new MessageDetailDialogFragment();
        fragment.setMessage(message);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogMessageStyle);
        messageDao = new MessageDao(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.message_detail_dialog_fragment, container, false);
        getDialog().setCanceledOnTouchOutside(false);

        mInfoMessageImageButton = rootView.findViewById(R.id.message_info_button);
        mInfoMessageImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogsCustom dialogsDefault = new DialogsCustom(getActivity());
                dialogsDefault.showDialogMessage(getString(R.string.message_info_dialog), dialogsDefault.DIALOG_TYPE_WARNING, null);
            }
        });

        mOKButton = rootView.findViewById(R.id.message_read_button);
        mOKButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMessage.get(index).setRead(true);
                messageDao.setMessagemLida(mMessage.get(index));

                try{
                    if(mMessage.get(index).getIdMessage() < 0){
                        ChatDao chatDao = new ChatDao(getActivity());
                        PedidoTrackingMessage pedidoTrackingMessage = chatDao.getMessage(mMessage.get(index).getIdMessage());

                        startActivity(PedidoTrackingDetailActivity.Companion.newIntentPush(getActivity(),
                                pedidoTrackingMessage.getNf(), pedidoTrackingMessage.getSerial(), pedidoTrackingMessage.getCnpj()));

                        dismiss();
                        return;
                    }
                } catch (Exception ex){
                    LogUser.log(ex.getMessage());
                }

                index++;

                if(mMessage != null &&  index < mMessage.size()){
                    init();
                } else {
                    JJSyncMessage jjSyncMessage = new JJSyncMessage();
                    jjSyncMessage.syncMessage(getActivity(), null);
                    dismiss();
                }

            }
        });

        mTitleBarTypeImageView = rootView.findViewById(R.id.title_bar_type_image_view);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }

    private void init() {

        if (mMessage == null || mMessage.size() == 0){
            dismiss();
        } else {
            String title = getString(R.string.title_screen_message) + " (";
            title += (index + 1) + "/" +  mMessage.size() + ")";
            mTitleBarTypeImageView.setText(title);

            MessageDetailFragment fragment = new MessageDetailFragment();
            Message message = mMessage.get(index);
            fragment.setMessage(message);

            FragmentTransaction fragmentTransaction =
                    this.getChildFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.container_message_detail, fragment);
            fragmentTransaction.commit();
        }
    }

    public List<Message> getMessage() {
        return mMessage;
    }

    public void setMessage(List<Message> mMessage) {
        this.mMessage = mMessage;
    }
}
