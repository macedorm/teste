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

public class TapValidationDialogFragment extends DialogFragment {

    private static final String ARG_ERROR_ARRAY_STRING = "array_string_error";

    private ValidationDan validationDan;

    private PedidoDialogValidationAdapter mPedidoDialogValidationAdapter;
    private RecyclerView mDialogItemRecyclerView;
    private Button mDialogFragTapOkButton;
    private ImageView iconImageView;
    private TextView titleTextView;

    public TapValidationDialogFragment() {
    }

    public static TapValidationDialogFragment newInstance(ValidationDan validationDan) {
        TapValidationDialogFragment fragment = new TapValidationDialogFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_ERROR_ARRAY_STRING, validationDan);
        fragment.setArguments(args);
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
        View rootView = inflater.inflate(R.layout.dialog_fragment_tap_validation, container);

        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        validationDan = (ValidationDan) getArguments().getSerializable(ARG_ERROR_ARRAY_STRING);

        mDialogItemRecyclerView = rootView.findViewById(R.id.dialog_frag_tap_valid_recycler_view);
        mDialogFragTapOkButton = rootView.findViewById(R.id.dialog_frag_tap_ok_button);
        iconImageView = rootView.findViewById(R.id.icon_image_view);
        titleTextView = rootView.findViewById(R.id.title_text_view);

        iconImageView.setImageResource(R.drawable.ic_error_black_24dp);
        iconImageView.setColorFilter(getContext().getResources().getColor(R.color.errorCollor));

        mDialogFragTapOkButton.setOnClickListener(view -> {
            dismiss();
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
}
