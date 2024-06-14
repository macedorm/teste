package br.com.jjconsulting.mobile.jjlib.masterdata;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import br.com.jjconsulting.mobile.jjlib.R;
import br.com.jjconsulting.mobile.jjlib.dao.entity.DataItemValue;
import br.com.jjconsulting.mobile.jjlib.dao.entity.TIcon;


public class JJLegendView extends DialogFragment {

    private static final String ARG_ERROR_ARRAY_STRING = "array_string_error";

    private ArrayList<DataItemValue> dataItemValues;

    private JJFormLegendViewAdapter mJJFormLegendViewAdapter;
    private RecyclerView mDialogItemRecyclerView;
    private Button mDialogFragPedOkButton;

    public JJLegendView() {
    }

    public static JJLegendView newInstance(ArrayList<DataItemValue> dataItemValues) {
        JJLegendView fragment = new JJLegendView();
        Bundle args = new Bundle();
        args.putSerializable(ARG_ERROR_ARRAY_STRING, dataItemValues);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogStyle);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.jj_dialog_fragment_legend, container);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dataItemValues = (ArrayList<DataItemValue>) getArguments().getSerializable(ARG_ERROR_ARRAY_STRING);

        mDialogItemRecyclerView = rootView.findViewById(R.id.dialog_frag_ped_valid_recycler_view);
        mDialogFragPedOkButton = rootView.findViewById(R.id.dialog_frag_ped_ok_button);

        mDialogFragPedOkButton.setOnClickListener(view -> {
            dismiss();
        });


        if (dataItemValues != null) {
            mJJFormLegendViewAdapter = new JJFormLegendViewAdapter(this.getActivity(), dataItemValues);

            mDialogItemRecyclerView.setAdapter(mJJFormLegendViewAdapter);
            mDialogItemRecyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));
            DividerItemDecoration divider = new DividerItemDecoration(
                    mDialogItemRecyclerView.getContext(), DividerItemDecoration.VERTICAL);
            divider.setDrawable(getContext().getResources().getDrawable(R.drawable.custom_divider));
            mDialogItemRecyclerView.addItemDecoration(divider);
        }

        return rootView;
    }

    public class JJFormLegendViewAdapter extends RecyclerView.Adapter<JJFormLegendViewAdapter.ViewHolder> {

        private static final int ITEM = 0;
        private static final int LOADING = 1;

        private ArrayList<DataItemValue> mDataItemValues;
        private boolean isLoadingAdded = false;

        public JJFormLegendViewAdapter(Context context, ArrayList<DataItemValue> dataItemValues) {
            mDataItemValues = dataItemValues;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View view;
            switch (viewType) {
                case ITEM:
                    view = inflater.inflate(R.layout.jj_item_dialog_legend, parent,
                            false);
                    view.setId(viewType);
                    break;
                case LOADING:
                    view = inflater.inflate(R.layout.jj_item_progress, parent,
                            false);
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
                    holder.mDescricaoErroTextView.setText(mDataItemValues.get(position).getDescription());

                    TIcon icon = (TIcon.values()[mDataItemValues.get(position).icon]);
                    JJIcon jjIcon = new JJIcon(getContext(), icon,mDataItemValues.get(position).imageColor);
                    holder.mIconLinearLayout.removeAllViews();
                    holder.mIconLinearLayout.addView(jjIcon.renderView());

                    break;
            }
        }

        @Override
        public int getItemCount() {
            return mDataItemValues.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            private TextView mDescricaoErroTextView;
            private LinearLayout mIconLinearLayout;

            public ViewHolder(View itemView) {
                super(itemView);
                mDescricaoErroTextView = itemView.findViewById(R.id.jj_item_dialog_desc_text_view);
                mIconLinearLayout = itemView.findViewById(R.id.jj_item_dialog_icon_image_view);
            }
        }

        @Override
        public int getItemViewType(int position) {
            return (position == mDataItemValues.size() - 1 && isLoadingAdded) ? LOADING : ITEM;
        }

    }
}