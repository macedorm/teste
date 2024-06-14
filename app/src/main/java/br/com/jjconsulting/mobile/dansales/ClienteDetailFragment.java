package br.com.jjconsulting.mobile.dansales;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import com.google.android.material.snackbar.Snackbar;
import androidx.fragment.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import br.com.jjconsulting.mobile.dansales.business.PedidoBusiness;
import br.com.jjconsulting.mobile.dansales.database.ClienteDao;
import br.com.jjconsulting.mobile.dansales.database.PedidoDao;
import br.com.jjconsulting.mobile.dansales.model.Cliente;
import br.com.jjconsulting.mobile.dansales.model.Layout;
import br.com.jjconsulting.mobile.dansales.model.Pedido;
import br.com.jjconsulting.mobile.dansales.model.PedidoViewType;
import br.com.jjconsulting.mobile.dansales.model.Perfil;
import br.com.jjconsulting.mobile.dansales.model.Usuario;
import br.com.jjconsulting.mobile.dansales.service.Current;
import br.com.jjconsulting.mobile.dansales.service.CurrentActionPedido;
import br.com.jjconsulting.mobile.dansales.util.ClienteUtils;
import br.com.jjconsulting.mobile.dansales.util.Config;
import br.com.jjconsulting.mobile.dansales.util.PlanoCampoUtils;
import br.com.jjconsulting.mobile.dansales.util.UsuarioUtils;
import br.com.jjconsulting.mobile.jjlib.util.DialogsCustom;
import br.com.jjconsulting.mobile.jjlib.util.FormatUtils;
import br.com.jjconsulting.mobile.jjlib.util.LogUser;

public class ClienteDetailFragment extends Fragment {

    private static final String ARG_CODIGO = "codigo";
    private static final String ARG_IS_ROUTE = "is_route";

    private Cliente mCliente;

    private LinearLayout mClienteDetailEndereco;

    private TextView mClienteCondicaoPagamento;
    private TextView mClienteDataUltimoPedido;
    private TextView mClienteStatusCredito;
    private TextView mClienteCodigoSAP;
    private TextView mClienteEndereco1;
    private TextView mClienteEndereco2;
    private TextView mClienteEndereco3;
    private TextView mClienteBandeira;
    private TextView mClienteNome;
    private TextView mClienteCnpj;
    private TextView mPlanoCampo;
    private TextView mClienteLayout;

    private ViewGroup mPlanoCampoViewGroup;
    private ViewGroup mLayoutViewGroup;

    private boolean isRoute;

    public ClienteDetailFragment() { }

    public static ClienteDetailFragment newInstance(String codigo, boolean isRoute) {
        ClienteDetailFragment fragment = new ClienteDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_CODIGO, codigo);
        args.putBoolean(ARG_IS_ROUTE, isRoute);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Current current = Current.getInstance(getActivity());
        String codigoUnidadeNegocio = current.getUnidadeNegocio().getCodigo();
        String codigo = getArguments().getString(ARG_CODIGO);
        isRoute = getArguments().getBoolean(ARG_IS_ROUTE);

        mCliente = new ClienteDao(getActivity()).get(codigoUnidadeNegocio, codigo);

        if(mCliente == null){
            DialogsCustom dialogsDefault = new DialogsCustom(getActivity());
            dialogsDefault.showDialogMessage(getString(R.string.message_etap_client), dialogsDefault.DIALOG_TYPE_ERROR, new DialogsCustom.OnClickDialogMessage() {
                @Override
                public void onClick() {
                    getActivity().finish();
                }
            });
        }
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cliente_detail, container,
                false);

        if(mCliente != null) {

            getActivity().setTitle(mCliente.getNomeReduzido());

            mClienteNome = view.findViewById(R.id.cliente_nome_text_view);
            mClienteCnpj = view.findViewById(R.id.cliente_cnpj_text_view);
            mClienteBandeira = view.findViewById(R.id.cliente_bandeira_text_view);
            mClienteCodigoSAP = view.findViewById(R.id.cliente_codigo_sap_text_view);
            mClienteEndereco1 = view.findViewById(R.id.cliente_endereco1_text_view);
            mClienteEndereco2 = view.findViewById(R.id.cliente_endereco2_text_view);
            mClienteEndereco3 = view.findViewById(R.id.cliente_endereco3_text_view);
            mClienteCondicaoPagamento = view.findViewById(R.id.cliente_condicao_pagamento_text_view);
            mClienteStatusCredito = view.findViewById(R.id.cliente_status_credito_text_view);
            mClienteDataUltimoPedido = view.findViewById(R.id.cliente_data_ultimo_pedido_text_view);
            mClienteDetailEndereco = view.findViewById(R.id.cliente_detail_endereco);
            mPlanoCampo = view.findViewById(R.id.plano_campo_text_view);
            mPlanoCampoViewGroup = view.findViewById(R.id.plano_campo_form_wrapper);

            mClienteLayout  = view.findViewById(R.id.cliente_layout_text_view);
            mLayoutViewGroup = view.findViewById(R.id.layout_detail_linear_layout);

            if(mCliente.getPlanoCampo() == null){
                mPlanoCampoViewGroup.setVisibility(View.GONE);
            } else {
                mPlanoCampo.setText(Html.fromHtml(PlanoCampoUtils.createPlanoCampo(mCliente.getPlanoCampo())));
            }

            Current current = Current.getInstance(getContext());

            if(mCliente.getLayout() == null || !current.getUsuario().getPerfil().isPermiteCR()){
                mLayoutViewGroup.setVisibility(View.GONE);
            } else {
                mClienteLayout.setText(mCliente.getLayout().getNome());
                mLayoutViewGroup.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Layout layout = mCliente.getLayout();
                        layout.setCodCliente(mCliente.getCodigo());
                        startActivity(new CRDetailActivity().newIntent(getContext(), layout , true));
                    }
                });
            }

            mClienteNome.setText(mCliente.getNome());
            mClienteCodigoSAP.setText(mCliente.getCodigo());
            mClienteEndereco1.setText(mCliente.getEndereco());
            mClienteEndereco2.setText(String.format("%1$s %2$s", mCliente.getCep(),
                    mCliente.getBairro()));
            mClienteEndereco3.setText(String.format("%1$s, %2$s", mCliente.getMunicipio(),
                    mCliente.getUf()));

            mClienteBandeira.setText(mCliente.getBandeira() != null ?
                    mCliente.getBandeira().getNomeBandeira() : "-");

            mClienteCondicaoPagamento.setText(mCliente.getCondicaoPagamento() != null ?
                    mCliente.getCondicaoPagamento().getNome() : mCliente.getCodigoCondicaoPagamento());


            mClienteStatusCredito.setText(
                    ClienteUtils.getStatusCreditoStringResourceId(mCliente.getStatusCredito()));


            mClienteDataUltimoPedido.setText(new ClienteDao(this.getContext()).getDataUltimoPedido(mCliente));

            mClienteDetailEndereco.setOnClickListener(viewEndereco -> {
                Intent geoLocation = new Intent(Intent.ACTION_VIEW);
                geoLocation.setData(Uri.parse("geo:0,0?q=" +
                        mCliente.getEndereco() + "+" +
                        mCliente.getCep() + "+" +
                        mCliente.getMunicipio() + "+" +
                        mCliente.getUf() + "+"));

                PackageManager packageManager = getActivity().getPackageManager();
                if (packageManager.resolveActivity(geoLocation,
                        PackageManager.MATCH_DEFAULT_ONLY) == null) {
                    Snackbar.make(view, R.string.cant_call, Snackbar.LENGTH_LONG).show();
                } else {
                    startActivity(geoLocation);
                }
            });

            try {
                mClienteCnpj.setText(FormatUtils.toCnpjFormat(mCliente.getCnpj()));
            } catch (Exception ex) {
                LogUser.log(Config.TAG, "onCreateView: " + ex.toString());
                mClienteCnpj.setText("-");
            }

            setHasOptionsMenu(true);
        }

        return view;
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        if(!isRoute){
            Usuario usuario = Current.getInstance(getActivity()).getUsuario();
            if (!(usuario.getPerfil().isPermiteRotaGuiada() && UsuarioUtils.isPromotor(usuario.getCodigoFuncao()))) {
                inflater.inflate(R.menu.pedidos_menu, menu);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_new_pedido:
                CurrentActionPedido.getInstance().setUpdateListPedido(true);
                Current current = Current.getInstance(getContext());
                PedidoDao pedidoDao = new PedidoDao(getContext());
                PedidoBusiness pedidoBusiness = new PedidoBusiness();
                Pedido pedido = pedidoBusiness.createNewPedido(pedidoDao, current.getUnidadeNegocio(),
                        current.getUsuario(), mCliente);

                startActivity(PedidoDetailActivity.newIntent(getContext(), pedido.getCodigo(),
                        PedidoViewType.PEDIDO, false, true));

                Intent returnIntent = new Intent();
                getActivity().setResult(Activity.RESULT_OK, returnIntent);
                getActivity().finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
