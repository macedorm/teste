package br.com.jjconsulting.mobile.dansales;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.jjconsulting.mobile.dansales.data.MessageFilter;
import br.com.jjconsulting.mobile.dansales.database.MessageDao;
import br.com.jjconsulting.mobile.dansales.kotlin.PedidoTrackingDetailActivity;
import br.com.jjconsulting.mobile.dansales.model.Layout;
import br.com.jjconsulting.mobile.dansales.model.Message;
import br.com.jjconsulting.mobile.dansales.model.Perfil;
import br.com.jjconsulting.mobile.dansales.service.Current;
import br.com.jjconsulting.mobile.dansales.service.MyFirebaseMessagingService;
import br.com.jjconsulting.mobile.dansales.util.Config;
import br.com.jjconsulting.mobile.dansales.util.JJSyncMessage;
import br.com.jjconsulting.mobile.dansales.util.TMessageType;
import br.com.jjconsulting.mobile.jjlib.util.LogUser;

public class HomeFragment extends Fragment {

    private static Map<Integer, NavOption> sOptions;
    private Current mCurrent;

    private ArrayAdapter<NavOption> mOptionsAdapter;

    private OnNavigationListener mListener;

    private GridView mOptionsGridView;
    private TextView mtitle;

    private int bagdeNotification;

    private boolean hasMessage;

    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance(int bagdeNotification, boolean hasMessage) {
        HomeFragment homeFragment = new HomeFragment();
        homeFragment.bagdeNotification = bagdeNotification;
        homeFragment.setHasMessage(hasMessage);
        return homeFragment;
    }

    public void setBagdeNotification(int value){
        bagdeNotification = value;
        mOptionsAdapter.notifyDataSetChanged();;
    }

    public boolean isHasMessage() {
        return hasMessage;
    }

    public void setHasMessage(boolean hasMessage) {
        this.hasMessage = hasMessage;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(MyFirebaseMessagingService.pushChat != null){
            startActivity(PedidoTrackingDetailActivity.Companion.newIntentPush(getContext(),
                    MyFirebaseMessagingService.pushChat.get("NumNF"),
                    MyFirebaseMessagingService.pushChat.get("Serie"),
                    ""
            ));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        try {

            JJSyncMessage jjSyncMessage = new JJSyncMessage();
            jjSyncMessage.syncMessage(getActivity(), null);

            mOptionsGridView = view.findViewById(R.id.home_options_grid_view);
            mOptionsGridView.setNumColumns(2);
            mtitle = view.findViewById(R.id.home_title);

            mOptionsAdapter = new OptionsAdapter(this.getActivity(), getAvailableOptions());

            if (mOptionsAdapter != null && mOptionsGridView != null) {
                mOptionsGridView.setAdapter(mOptionsAdapter);
            }

            mOptionsGridView.setOnItemClickListener((adapterView, view1, i, l) -> {
                onNavigation(getAvailableOptions().get(i).getId());
            });


            String hom = "";
            if (getContext().getPackageName().contains("hml") || getContext().getPackageName().contains("dev")) {
                hom = getString(R.string.title_hom);
            }


            mCurrent = Current.getInstance(getActivity());
            mtitle.setText(String.format(getString(R.string.title_page_home),
                    mCurrent.getUsuario().getNomeReduzido(), hom)
            );

            AppCompatActivity activity = ((AppCompatActivity) getActivity());
            if (activity.getSupportActionBar() != null) {
                activity.getSupportActionBar().setTitle(getString(R.string.app_name));
            }

            //Verifica se existem mensagens não lidas de rota guiada
            MessageFilter messageFilter = new MessageFilter();
            messageFilter.settMessageType(TMessageType.MESSAGEM);
            Current current = Current.getInstance(getActivity());
            MessageDao messageDao = new MessageDao(getContext());

            List<Message>  messages = messageDao.getMessages(current.getUsuario(), current.getUnidadeNegocio().getCodigo(), new Date(), messageFilter, true);

            if(messages != null && messages.size() > 0) {
                MessageDetailDialogFragment messageDetailDialogFragment = MessageDetailDialogFragment.newInstance(messages);
                messageDetailDialogFragment.show(getActivity().getSupportFragmentManager(), "");
            }

        }catch (Exception ex){
            LogUser.log(Config.TAG, ex.toString());
        }

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnNavigationListener) {
            mListener = (OnNavigationListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnNavigationListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private ArrayList<NavOption> getAvailableOptions() {
        try {

            if (sOptions == null) {
                setupOptions();
            }

            Current current = Current.getInstance(getContext());
            Perfil perfil = current.getUsuario().getPerfil();
            String codUnNeg = current.getUnidadeNegocio().getCodigo();
            ArrayList<NavOption> availableOptions = new ArrayList<>();

            if (perfil.isPermitePlanejamentoRota()) {
                availableOptions.add(sOptions.get(R.id.nav_planejamento_rota));
            }

            if (perfil.permiteVenda(codUnNeg)) {
                if(perfil.isPermiteRotaGuiada() && !perfil.isPermitePlanejamentoRota()){
                    availableOptions.add(sOptions.get(R.id.nav_rota_guiada));
                } else {
                    availableOptions.add(sOptions.get(R.id.nav_add_pedido));
                }
                availableOptions.add(sOptions.get(R.id.nav_pedidos));

                if (perfil.permiteRastreioPedidos()) {
                    availableOptions.add(sOptions.get(R.id.nav_rastreio));
                }
            }

            if (perfil.permiteVisualizarClientes()) {
                availableOptions.add(sOptions.get(R.id.nav_clientes));
            }

            if (perfil.permiteAprovacao(codUnNeg)) {
                availableOptions.add(sOptions.get(R.id.nav_aprovacao));
            }

            if (perfil.permiteLiberacao(codUnNeg)) {
                availableOptions.add(sOptions.get(R.id.nav_liberacao));
            }

            if (perfil.permiteVisualizarRotas()) {
                availableOptions.add(sOptions.get(R.id.nav_rota));
            }

            if (perfil.permiteVisualizarRelatorios()) {
                availableOptions.add(sOptions.get(R.id.nav_relatorios));
            }

            if (perfil.permitePesquisas()) {
                availableOptions.add(sOptions.get(R.id.nav_pesquisa));
            }

            if (perfil.permiteETap()) {
                availableOptions.add(sOptions.get(R.id.nav_etap));
            }

            if (perfil.isPermiteIsaac()){
                availableOptions.add(sOptions.get(R.id.nav_chat));
            }

            availableOptions.add(sOptions.get(R.id.nav_sync_data));

            if (perfil.isPermiteRequisicao()){
                availableOptions.add(sOptions.get(R.id.nav_requisicao));
            }

            availableOptions.add(sOptions.get(R.id.nav_message));

            if (perfil.isPermiteCR()){
                availableOptions.add(sOptions.get(R.id.nav_cr));
            }

            return availableOptions;

        } catch (Exception ex) {
            return null;
        }

    }

    private void setupOptions() {
        sOptions = new HashMap<>();
        sOptions.put(R.id.nav_add_pedido, new NavOption(R.id.nav_add_pedido,
                R.drawable.ic_add_pedido_white, R.string.title_add_pedido));
        sOptions.put(R.id.nav_planejamento_rota, new NavOption(R.id.nav_planejamento_rota,
                R.drawable.ic_rota_guiada, R.string.title_planejamento_rota));
        sOptions.put(R.id.nav_rota_guiada, new NavOption(R.id.nav_rota_guiada,
                R.drawable.ic_rota_guiada, R.string.title_rota));
        sOptions.put(R.id.nav_pedidos, new NavOption(R.id.nav_pedidos,
                R.drawable.ic_pedidos_white, R.string.title_hist_pedidos));
        sOptions.put(R.id.nav_clientes, new NavOption(R.id.nav_clientes,
                R.drawable.ic_clientes_white, R.string.title_clientes));
        sOptions.put(R.id.nav_rastreio, new NavOption(R.id.nav_rastreio,
                R.drawable.ic_home_white, R.string.title_rastreio));
        sOptions.put(R.id.nav_aprovacao, new NavOption(R.id.nav_aprovacao,
                R.drawable.ic_aprovacao_white, R.string.title_aprovacao));
        sOptions.put(R.id.nav_liberacao, new NavOption(R.id.nav_liberacao,
                R.drawable.ic_liberacao_white, R.string.title_liberacao));
        sOptions.put(R.id.nav_rota, new NavOption(R.id.nav_rota, R.drawable.ic_rota_white,
                R.string.title_rota));
        sOptions.put(R.id.nav_relatorios, new NavOption(R.id.nav_relatorios,
                R.drawable.ic_relatorios_white, R.string.title_relatorios));
        sOptions.put(R.id.nav_pesquisa, new NavOption(R.id.nav_pesquisa,
                R.drawable.ic_pesquisa_white, R.string.title_pesquisa));
        sOptions.put(R.id.nav_cr, new NavOption(R.id.nav_cr,
                R.drawable.icon_cr_menu, R.string.title_CR));
        sOptions.put(R.id.nav_etap, new NavOption(R.id.nav_etap,
                R.drawable.ic_tap_white, R.string.title_tap));
        sOptions.put(R.id.nav_sync_data, new NavOption(R.id.nav_sync_data,
                R.drawable.ic_sync_data_white, R.string.title_sync_data));
        sOptions.put(R.id.nav_requisicao, new NavOption(R.id.nav_requisicao,
                R.drawable.ic_relatorios_white, R.string.title_requisicao));
        sOptions.put(R.id.nav_chat, new NavOption(R.id.nav_chat,
                R.drawable.icon_issac, R.string.title_chat));
         sOptions.put(R.id.nav_message, new NavOption(R.id.nav_message,
                    R.drawable.ic_message_white_40dp, R.string.title_screen_message));

    }

    public void onNavigation(int targetNavId) {
        if (mListener != null) {
            mListener.onNavigation(targetNavId);
        }
    }

    public interface OnNavigationListener {
        void onNavigation(int navId);
    }

    private class OptionsAdapter extends ArrayAdapter<NavOption> {

        public OptionsAdapter(Context context, ArrayList<NavOption> options) {
            super(context, 0, options);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            NavOption item = getItem(position);

            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(
                        R.layout.home_option_item, parent, false);
            }

            convertView.setTag(position);

            ImageView icon = convertView.findViewById(R.id.option_icon_image_view);
            TextView title = convertView.findViewById(R.id.option_title_text_view);

            title.setText(item.getTitleResourceId());
            icon.setImageResource(item.getImageResourceId());

            LinearLayout badgeLinearLayout = convertView.findViewById(R.id.badge_notification_linear_layout);

            if(item.getId() == R.id.nav_cr){
                ViewGroup.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        (int)getContext().getResources().getDimension(R.dimen.home_options_cr_width),
                        (int)getContext().getResources().getDimension(R.dimen.home_options_cr_height));
                icon.setLayoutParams(layoutParams);
            }

            if(item.getId() == R.id.nav_message){
                if(bagdeNotification > 0){
                    badgeLinearLayout.setVisibility(View.VISIBLE);
                    TextView baTextView = convertView.findViewById(R.id.badge_notification_text_view);
                    baTextView.setText(String.valueOf(bagdeNotification));
                } else {
                    badgeLinearLayout.setVisibility(View.GONE);
                }
            } else {
                badgeLinearLayout.setVisibility(View.GONE);
            }

            return convertView;
        }
    }

    private class NavOption {

        private int mId;
        private int mImageResourceId;
        private int mTitleResourceId;

        public NavOption(int id, int imageResourceId, int titleResourceId) {
            this.mId = id;
            this.mImageResourceId = imageResourceId;
            this.mTitleResourceId = titleResourceId;
        }

        public int getId() {
            return mId;
        }

        public int getImageResourceId() {
            return mImageResourceId;
        }

        public int getTitleResourceId() {
            return mTitleResourceId;
        }
    }
}
