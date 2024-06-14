package br.com.jjconsulting.mobile.dansales.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import br.com.jjconsulting.mobile.dansales.R;
import br.com.jjconsulting.mobile.dansales.model.WebUser;
import br.com.jjconsulting.mobile.jjlib.util.Config;
import br.com.jjconsulting.mobile.jjlib.util.LogUser;

public class TapWebUserListAdapter extends RecyclerView.Adapter<TapWebUserListAdapter.ViewHolder> {

    private static final int ITEM = 0;

    private Context mContext;
    private ArrayList<WebUser> mListEtapList;


    public TapWebUserListAdapter(Context context, ArrayList<WebUser> tapList) {
        mContext = context;
        mListEtapList = tapList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view;

        view = inflater.inflate(R.layout.item_list_web_user_etap, parent, false);
        view.setId(viewType);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case ITEM:
                WebUser webUser = mListEtapList.get(position);
                try {
                    holder.mCod.setText(mContext.getString(R.string.tap_web_user_cod_label,
                            webUser.getCod()));
                    holder.mName.setText(mContext.getString(R.string.tap_web_user_name_label,
                            webUser.getName()));
                    holder.mEmail.setText(mContext.getString(R.string.tap_web_user_email_label,
                            webUser.getEmail()));
                } catch (Exception ex) {
                    LogUser.log(Config.TAG, ex.toString());
                }

                break;

        }
    }

    @Override
    public int getItemViewType(int position) {
        return ITEM;
    }

    @Override
    public int getItemCount() {
        return mListEtapList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mName;
        private TextView mCod;
        private TextView mEmail;

        public ViewHolder(View itemView) {
            super(itemView);
            switch (itemView.getId()) {
                case ITEM:
                    mName = itemView.findViewById(R.id.etap_cod_text_view);
                    mCod = itemView.findViewById(R.id.etap_name_tap_text_view);
                    mEmail = itemView.findViewById(R.id.etap_email_text_view);
                    break;
            }
        }
    }

    public ArrayList<WebUser> getListWebUser() {
        return mListEtapList;
    }


    public WebUser getItem(int position) {
        return mListEtapList.get(position);
    }
}
