package br.com.jjconsulting.mobile.dansales;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import br.com.jjconsulting.mobile.dansales.adapter.PedidoDialogValidationAdapter;
import br.com.jjconsulting.mobile.dansales.data.ValidationDan;

public class PedidoValidationDialogFragment extends DialogFragment {

    private static final String ARG_ERROR_ARRAY_STRING = "array_string_error";
    private static final String ARG_SUCESS_BOOLEAN = "boolean_sucess";

    private ValidationDan validationDan;
    private boolean isSucess;

    private PedidoDialogValidationAdapter mPedidoDialogValidationAdapter;
    private RecyclerView mDialogItemRecyclerView;
    private Button mDialogFragPedOkButton;
    private ImageView iconImageView;
    private TextView titleTextView;

    private OnDissmisDialog onDissmisDialog;

    public PedidoValidationDialogFragment() { }

    public static PedidoValidationDialogFragment newInstance(ValidationDan validationDan, boolean isSucess) {
        PedidoValidationDialogFragment fragment = new PedidoValidationDialogFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_ERROR_ARRAY_STRING, validationDan);
        fragment.setArguments(args);
        args.putBoolean(ARG_SUCESS_BOOLEAN, isSucess);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogProductStyle);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dialog_fragment_pedido_validation, container);

        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        validationDan = (ValidationDan) getArguments().getSerializable(ARG_ERROR_ARRAY_STRING);
        isSucess = getArguments().getBoolean(ARG_SUCESS_BOOLEAN);

        mDialogItemRecyclerView = rootView.findViewById(R.id.dialog_frag_ped_valid_recycler_view);
        mDialogFragPedOkButton = rootView.findViewById(R.id.dialog_frag_ped_ok_button);
        iconImageView = rootView.findViewById(R.id.icon_image_view);
        titleTextView = rootView.findViewById(R.id.title_text_view);

        if (isSucess) {
            titleTextView.setText(getString(R.string.detail_pedido_sync_sucess));
            iconImageView.setImageResource(R.drawable.ic_check_circle_black_24dp);
            iconImageView.setColorFilter(getContext().getResources().getColor(R.color.sucessCollor));
        } else {
            titleTextView.setText(getString(R.string.message_title_validation));
            iconImageView.setImageResource(R.drawable.ic_error_black_24dp);
            iconImageView.setColorFilter(getContext().getResources().getColor(R.color.alertCollor));
        }

        mDialogFragPedOkButton.setOnClickListener(view -> {
            dismiss();

            if(onDissmisDialog != null){
                onDissmisDialog.onFinish();
            }
        });

        if (validationDan != null) {
            mPedidoDialogValidationAdapter = new PedidoDialogValidationAdapter(this.getActivity(),
                    validationDan);

            mDialogItemRecyclerView.setAdapter(mPedidoDialogValidationAdapter);
            mDialogItemRecyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));
            DividerItemDecoration divider = new DividerItemDecoration(
                    mDialogItemRecyclerView.getContext(), DividerItemDecoration.VERTICAL);
            divider.setDrawable(getContext().getResources().getDrawable(R.drawable.custom_divider));
            mDialogItemRecyclerView.addItemDecoration(divider);
        }

        return rootView;
    }

    public OnDissmisDialog getOnDissmisDialog() {
        return onDissmisDialog;
    }

    public void setOnDissmisDialog(OnDissmisDialog onDissmisDialog) {
        this.onDissmisDialog = onDissmisDialog;
    }

    public interface OnDissmisDialog{
        void onFinish();
    }
}
