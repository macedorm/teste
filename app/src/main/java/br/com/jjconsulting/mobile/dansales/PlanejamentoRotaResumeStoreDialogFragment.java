package br.com.jjconsulting.mobile.dansales;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import br.com.jjconsulting.mobile.dansales.adapter.ResumeStoreAdapter;
import br.com.jjconsulting.mobile.dansales.model.ResumeStore;
import br.com.jjconsulting.mobile.jjlib.util.DialogsCustom;
import br.com.jjconsulting.mobile.jjlib.util.FormatUtils;

public class PlanejamentoRotaResumeStoreDialogFragment extends DialogFragment {

    private static final String ARG_RESUME = "resume_string";

    private DialogsCustom dialogsCustom;

    private RecyclerView mPilaresRecyclerView;

    private OnDissmisDialog onDissmisDialog;

    public PlanejamentoRotaResumeStoreDialogFragment() { }

    public static PlanejamentoRotaResumeStoreDialogFragment newInstance(ResumeStore resumeStore) {
        PlanejamentoRotaResumeStoreDialogFragment fragment = new PlanejamentoRotaResumeStoreDialogFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_RESUME, resumeStore);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogProductStyle);

        dialogsCustom =  new DialogsCustom(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dialog_fragment_resume_store, container);

        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        ResumeStore resumeStore = (ResumeStore) getArguments().getSerializable(ARG_RESUME);

        TextView lastNoteTextView = rootView.findViewById(R.id.last_note_text_view);
        lastNoteTextView.setText(FormatUtils.toDoubleFormat(resumeStore.getUltimaNota()) + "");

        TextView averageTimeTextView = rootView.findViewById(R.id.average_time_text_view);
        averageTimeTextView.setText(resumeStore.getTempoMedio() + "");

        TextView sumNoteTextView = rootView.findViewById(R.id.sum_note_client_text_view);
        sumNoteTextView.setText(FormatUtils.toDoubleFormat(resumeStore.getVisitaMedia()) + "");

        ImageView closeImageView = rootView.findViewById(R.id.icon_close_image_view);
        closeImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });


        ImageView infoImageView = rootView.findViewById(R.id.icon_info_image_view);
        infoImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogsCustom.showDialogLeftMessage(getString(R.string.planejamento_rota_store_legenda),  dialogsCustom.DIALOG_TYPE_SUCESS, null);
            }
        });



        if(resumeStore.getPilares() != null && resumeStore.getPilares().size() > 0) {
            mPilaresRecyclerView = rootView.findViewById(R.id.resume_recycler_view);
            ResumeStoreAdapter resumeStoreAdapter = new ResumeStoreAdapter(getContext(), resumeStore.getPilares());
            mPilaresRecyclerView.setAdapter(resumeStoreAdapter);
            mPilaresRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
            DividerItemDecoration divider = new DividerItemDecoration(
                    mPilaresRecyclerView.getContext(), DividerItemDecoration.VERTICAL);
            divider.setDrawable(getContext().getResources().getDrawable(R.drawable.custom_divider));
            mPilaresRecyclerView.addItemDecoration(divider);

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
