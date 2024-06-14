package br.com.jjconsulting.mobile.dansales;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import java.util.ArrayList;

import br.com.jjconsulting.mobile.dansales.adapter.TapWebUserListAdapter;
import br.com.jjconsulting.mobile.dansales.base.BaseActivity;
import br.com.jjconsulting.mobile.dansales.model.WebUser;
import br.com.jjconsulting.mobile.jjlib.base.ItemClickSupport;

public class TapWebUserActivity extends BaseActivity {

    public static final String WEB_USER_RESULT = "web_user_result";
    private static final String ARG_WEB_USER_ACTION = "web_user";

    private ArrayList<WebUser> mTapListWebUser;
    private TapWebUserListAdapter mTapListAdapter;

    private RecyclerView mETapRecyclerView;
    private LinearLayout mListEmptyLinearLayout;

    /**
     * Use it to edit an item.
     */
    public static Intent newIntent(Context context, ArrayList<WebUser> weUsers) {
        Intent intent = new Intent(context, TapWebUserActivity.class);
        intent.putExtra(ARG_WEB_USER_ACTION, weUsers);
        return intent;
    }

    public static TapWebUserActivity newInstance() {
        return new TapWebUserActivity();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_web_user_etap);

        mTapListWebUser = (ArrayList<WebUser>) getIntent().getSerializableExtra(ARG_WEB_USER_ACTION);

        mETapRecyclerView = findViewById(R.id.list_web_user_etap_recycler_view);
        mListEmptyLinearLayout = findViewById(R.id.list_empty_text_view);

        mETapRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        DividerItemDecoration divider = new DividerItemDecoration(mETapRecyclerView.getContext(), DividerItemDecoration.VERTICAL);
        divider.setDrawable(getResources().getDrawable(R.drawable.custom_divider));
        mETapRecyclerView.addItemDecoration(divider);

        mTapListAdapter = new TapWebUserListAdapter(getBaseContext(), mTapListWebUser);
        mETapRecyclerView.setAdapter(mTapListAdapter);

        if (mTapListWebUser.size() == 0) {
            mListEmptyLinearLayout.setVisibility(View.VISIBLE);
            mETapRecyclerView.setVisibility(View.GONE);
        }

        ItemClickSupport.addTo(mETapRecyclerView).setOnItemClickListener(
                (recyclerView, position, v) -> {
                    try {
                        Intent result = new Intent();
                        result.putExtra(WEB_USER_RESULT, mTapListWebUser.get(position));
                        setResult(RESULT_OK, result);
                        finish();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        //getMenuInflater().inflate(R.menu.search_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
