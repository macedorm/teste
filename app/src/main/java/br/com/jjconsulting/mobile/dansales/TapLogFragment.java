package br.com.jjconsulting.mobile.dansales;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import br.com.jjconsulting.mobile.dansales.adapter.LogTapAdapter;
import br.com.jjconsulting.mobile.jjlib.OnPageSelected;

public class TapLogFragment extends TapBaseFragment implements OnPageSelected {


    private LinearLayout mListEmptyLinearLayout;
    private RecyclerView mLogTapRecyclerView;

    public TapLogFragment() {
    }

    public static TapLogFragment newInstance() {
        TapLogFragment fragment = new TapLogFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_log_tap, container,
                false);

        getActivity().setTitle(getString(R.string.title_log_tap));


        ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle(getCurrentTap().getTapCod());

        mListEmptyLinearLayout = view.findViewById(R.id.list_empty_text_view);

        mLogTapRecyclerView = view.findViewById(R.id.log_tap_recycler_view);
        mLogTapRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        DividerItemDecoration divider = new DividerItemDecoration(mLogTapRecyclerView.getContext(), DividerItemDecoration.VERTICAL);
        divider.setDrawable(getContext().getResources().getDrawable(R.drawable.custom_divider));
        mLogTapRecyclerView.addItemDecoration(divider);




        return view;
    }

    @Override
    public void onPageSelected(int position) {

        LogTapAdapter mLogPedidoAdapter = new LogTapAdapter(getContext(), getCurrentTap().getListStep());
        mLogTapRecyclerView.setAdapter(mLogPedidoAdapter);


        mListEmptyLinearLayout.setVisibility(getCurrentTap().getListStep().size() > 0 ? View.GONE : View.VISIBLE);
        mLogTapRecyclerView.setVisibility(getCurrentTap().getListStep().size() > 0 ? View.VISIBLE : View.GONE);
    }
}
