package br.com.jjconsulting.mobile.jjlib.util;

import android.app.DialogFragment;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import br.com.jjconsulting.mobile.jjlib.R;
import br.com.jjconsulting.mobile.jjlib.adapter.JJDialogValidationAdapter;
import br.com.jjconsulting.mobile.jjlib.data.ValidationInfo;

public class JJValidationDialogFragment extends DialogFragment {

    private static final String ARG_ERROR_ARRAY_STRING = "array_string_error";
    private static final String ARG_SUCESS_BOOLEAN = "boolean_sucess";

    private ValidationInfo validationInfo;
    private JJDialogValidationAdapter mPedidoDialogValidationAdapter;
    private RecyclerView mDialogItemRecyclerView;
    private Button mDialogFragPedOkButton;
    private ImageView iconImageView;
    private TextView titleTextView;

    private OnFinishValidation onFinishValidation;

    private boolean isSucess;

    public JJValidationDialogFragment() { }

    public static JJValidationDialogFragment newInstance(ValidationInfo validationInfo, boolean isSucess) {
        JJValidationDialogFragment fragment = new JJValidationDialogFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_ERROR_ARRAY_STRING, validationInfo);
        fragment.setArguments(args);
        args.putBoolean(ARG_SUCESS_BOOLEAN, isSucess);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogValidationStyle);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.jj_dialog_fragment_validation, container);

        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        validationInfo = (ValidationInfo) getArguments().getSerializable(ARG_ERROR_ARRAY_STRING);
        isSucess = getArguments().getBoolean(ARG_SUCESS_BOOLEAN);

        mDialogItemRecyclerView = rootView.findViewById(R.id.dialog_frag_ped_valid_recycler_view);
        mDialogFragPedOkButton = rootView.findViewById(R.id.dialog_frag_ped_ok_button);
        iconImageView = rootView.findViewById(R.id.icon_image_view);
        titleTextView = rootView.findViewById(R.id.title_text_view);

        if (isSucess) {
            titleTextView.setText(getString(R.string.dialog_validation_sucess));
            iconImageView.setImageResource(R.drawable.ic_check_circle_black_24dp);
            iconImageView.setColorFilter(getResources().getColor(R.color.sucessCollor));
        } else {
            titleTextView.setText(getString(R.string.dialog_validation_error));
            iconImageView.setImageResource(R.drawable.ic_error_black_24dp);
            iconImageView.setColorFilter(getResources().getColor(R.color.alertCollor));
        }

        mDialogFragPedOkButton.setOnClickListener(view -> {
            dismiss();

            if(onFinishValidation != null){
                onFinishValidation.onFinish(isSucess);
            }
        });

        if (validationInfo != null) {
            mPedidoDialogValidationAdapter = new JJDialogValidationAdapter(this.getActivity(),
                    validationInfo);

            mDialogItemRecyclerView.setAdapter(mPedidoDialogValidationAdapter);
            mDialogItemRecyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));
            mDialogItemRecyclerView.setHasFixedSize(true);
            DividerItemDecoration divider = new DividerItemDecoration(
                    mDialogItemRecyclerView.getContext(), DividerItemDecoration.VERTICAL);
            divider.setDrawable(getResources().getDrawable(R.drawable.custom_divider));
            mDialogItemRecyclerView.addItemDecoration(divider);
        }

        return rootView;
    }

    public OnFinishValidation getOnFinishValidation() {
        return onFinishValidation;
    }

    public void setOnFinishValidation(OnFinishValidation onFinishValidation) {
        this.onFinishValidation = onFinishValidation;
    }

    public interface OnFinishValidation{
        void onFinish(boolean success);
    }
}
