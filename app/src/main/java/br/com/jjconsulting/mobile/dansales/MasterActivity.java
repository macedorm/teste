package br.com.jjconsulting.mobile.dansales;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.navigation.NavigationView;
import com.mikepenz.actionitembadge.library.ActionItemBadge;

import java.util.Date;
import java.util.List;

import br.com.jjconsulting.mobile.dansales.database.MessageDao;
import br.com.jjconsulting.mobile.dansales.database.PesquisaDao;
import br.com.jjconsulting.mobile.dansales.database.UnidadeNegocioDao;
import br.com.jjconsulting.mobile.dansales.kotlin.PedidoTrackingFilterActivity;
import br.com.jjconsulting.mobile.dansales.model.Cliente;
import br.com.jjconsulting.mobile.dansales.model.ConsultaGenerica;
import br.com.jjconsulting.mobile.dansales.model.Pedido;
import br.com.jjconsulting.mobile.dansales.model.PedidoViewType;
import br.com.jjconsulting.mobile.dansales.model.Perfil;
import br.com.jjconsulting.mobile.dansales.model.UnidadeNegocio;
import br.com.jjconsulting.mobile.dansales.model.Usuario;
import br.com.jjconsulting.mobile.dansales.service.Current;
import br.com.jjconsulting.mobile.dansales.service.CurrentActionPedido;
import br.com.jjconsulting.mobile.dansales.util.Config;
import br.com.jjconsulting.mobile.dansales.util.ETapUtils;
import br.com.jjconsulting.mobile.dansales.util.FirebaseUtils;
import br.com.jjconsulting.mobile.dansales.util.UserInfo;
import br.com.jjconsulting.mobile.dansales.util.UsuarioUtils;
import br.com.jjconsulting.mobile.jjlib.SuperActivity;
import br.com.jjconsulting.mobile.jjlib.UnderDevelopment;
import br.com.jjconsulting.mobile.jjlib.UnderDevelopmentFragment;
import br.com.jjconsulting.mobile.jjlib.syncData.SyncDataDao;
import br.com.jjconsulting.mobile.jjlib.syncData.SyncDataManager;
import br.com.jjconsulting.mobile.jjlib.util.DialogsCustom;
import br.com.jjconsulting.mobile.jjlib.util.FormatUtils;
import br.com.jjconsulting.mobile.jjlib.util.JJSDK;
import br.com.jjconsulting.mobile.jjlib.util.LogUser;
import br.com.jjconsulting.mobile.jjlib.util.SavePref;

public class MasterActivity extends SuperActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        HomeFragment.OnNavigationListener, ClientesFragment.OnClienteClickListener,
        PedidosFragment.OnPedidoClickListener {

    private static final String SAVED_CURRENT_TITLE = "current_title";
    private static final String SAVED_INDEX_NAV_CURRENT = "current_index_nav_current";

    private static final int NAV_TYPE_INTENT = 1;
    private static final int NAV_TYPE_FRAGMENT = 2;

    private NavigationView mNavigationView;

    private Fragment fragment;

    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private Toolbar mToolbar;

    private MenuItem menuItemMessage;

    private MessageDao messageDao;

    private int mIndexNavCurrent;

    private int badgeOld;

    private boolean hasMessage;


    @Override
    public  void onResume(){
        super.onResume();
        getBagdeNotification();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_master);

        messageDao = new MessageDao(this);

        Current current = Current.getInstance(this);
        if(current == null || current.getUnidadeNegocio() == null){
            finish();
            return;

        }

        int messageSize = messageDao.getMessages(current.getUsuario(), current.getUnidadeNegocio().getCodigo(), new Date(), null,false).size();
        hasMessage = messageSize == 0 ? false:true;

        mDrawerLayout = findViewById(R.id.master_drawer_layout);
        mNavigationView = findViewById(R.id.master_navigation_view);
        mToolbar = findViewById(R.id.master_toolbar);

        setSupportActionBar(mToolbar);

        mNavigationView.setNavigationItemSelectedListener(this);

        // The ActionBarDrawerToggle helps in functionality and
        // design when using ActionBar and DrawerLayout
        mDrawerToggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, mToolbar, R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(mDrawerToggle);

        setupHeaderData();
        setupUnidadeNegocioComponent();

        try {
            setupNavigation();
        } catch (Exception ex) {
            LogUser.log(Config.TAG, ex.toString());
        }

        setlogUserOnCrashlytics();

        if (savedInstanceState == null) {
            // When savedInstanceState is null it's first activity
            // launch (not sure) so we select the default feature (home)
            onNavigationItemSelected(mNavigationView.getMenu().findItem(R.id.nav_home));
        } else {
            getSupportActionBar().setTitle(savedInstanceState.getString(SAVED_CURRENT_TITLE));
            mIndexNavCurrent = savedInstanceState.getInt(SAVED_INDEX_NAV_CURRENT);
        }
    }


    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Saving current title and selected unidade de negócio
        outState.putString(SAVED_CURRENT_TITLE, getSupportActionBar().getTitle().toString());
        outState.putInt(SAVED_INDEX_NAV_CURRENT, mIndexNavCurrent);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return mDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (fragment != null && fragment.getClass().equals(SyncDataFragment.class)
                && ((SyncDataFragment) fragment).getManagerSyncData()) {
            return;
        }

        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            if (mIndexNavCurrent != -1 && mIndexNavCurrent != R.id.nav_home) {
                Fragment fragment = createFragmentByNavId(R.id.nav_home);
                if (fragment != null) {
                    MenuItem item = mNavigationView.getMenu().findItem(R.id.nav_home);
                    setupNewFragment(fragment, item);
                    mIndexNavCurrent = -1;
                }
            } else {
                exit();
            }
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        navigate(item.getItemId(), item);
        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onNavigation(int navId) {
        navigate(navId, null);
    }

    private void navigate(int navId, MenuItem item) {
        Fragment fragment = null;
        Intent intent = null;

        setupHeaderData();

        int navType = getNavigationType(navId);
        if (navType == NAV_TYPE_INTENT) {
            intent = createIntentByNavId(navId);
        } else if (navType == NAV_TYPE_FRAGMENT) {
            fragment = createFragmentByNavId(navId);
        }

        if (item == null) {
            item = mNavigationView.getMenu().findItem(navId);
        }

        if (fragment != null && item != null) {
            setupNewFragment(fragment, item);
        } else if (intent != null) {
            if(item != null && item.getItemId() == R.id.nav_rota_guiada){
                if(!UsuarioUtils.isPromGa(Current.getInstance(this).getUsuario().getCodigoFuncao())){
                    DialogsCustom dialogsDefault = new DialogsCustom(this);
                    dialogsDefault.showDialogMessage(getString(R.string.user_not_allowed), dialogsDefault.DIALOG_TYPE_WARNING, null);
                    return;
                }
            }

            try {
                if (item != null && item.getItemId() == R.id.nav_settings) {
                    startActivityForResult(intent, Config.REQUEST_SETTINGS);
                } else {
                    startActivity(intent);
                }
            } catch (Exception ex) {
                finish();
            }


        }
    }

    public int getNavigationType(int navId) {
        if (navId == R.id.nav_rota_guiada
                || navId == R.id.nav_add_pedido
                || navId == R.id.nav_settings
                || navId == R.id.nav_help
                || navId == R.id.nav_about
                || navId == R.id.nav_logout
                || navId == R.id.nav_chat
                || navId == R.id.nav_requisicao
                || navId == R.id.nav_rastreio
                || (navId == R.id.nav_etap && ETapUtils.isOneItemTap(this))) {
            return NAV_TYPE_INTENT;
        } else {
            return NAV_TYPE_FRAGMENT;
        }
    }

    public Intent createIntentByNavId(int navId) {
        Intent intent = null;

        switch (navId) {
            case R.id.nav_add_pedido:
                intent = PickClienteActivity.newIntent(this);
                break;
            case R.id.nav_rota_guiada:
                intent = RotaGuiadaActivity.newIntent(this);
                break;
            case R.id.nav_etap:
                intent = ETapUtils.intentItemTap(this);
                break;
            case R.id.nav_settings:
                intent = new Intent(MasterActivity.this,
                        SettingsActivity.class);
                intent.putExtra("login", false);
                break;
            case R.id.nav_help:
                intent = new Intent(MasterActivity.this,
                        HelpActivity.class);
                break;
            case R.id.nav_about:
                intent = new Intent(MasterActivity.this,
                        AboutActivity.class);
                break;
            case R.id.nav_logout:
                logout();
                break;
            case R.id.nav_requisicao:
                intent = new Intent(this, ConsultaGenericaActiviy.class);
                break;
            case R.id.nav_chat:
                intent = new Intent(this, IssacActivity.class);
                break;
            case R.id.nav_rastreio:
                intent = new Intent(this, PedidoTrackingFilterActivity.class);
                break;
        }

        return intent;
    }

    public Fragment createFragmentByNavId(int navId) {
        fragment = null;

        int bagde = getBagdeNotification();
        switch (navId) {
            case R.id.nav_home:
                fragment = HomeFragment.newInstance(bagde, hasMessage);
                break;
            case R.id.nav_pedidos:
                fragment = PedidosFragment.newInstance(PedidoViewType.PEDIDO);
                break;
            case R.id.nav_clientes:
                fragment = ClientesFragment.newInstance();
                break;
            case R.id.nav_aprovacao:
                fragment = AprovacaoFragment.newInstance(PedidoViewType.APROVACAO);
                break;
            case R.id.nav_liberacao:
                fragment = LiberacaoFragment.newInstance(PedidoViewType.LIBERACAO);
                break;
            case R.id.nav_planejamento_rota:
                fragment = PlanejamentoRotaFragment.newInstance();
                break;
            case R.id.nav_rota:
                fragment = RotaFragment.newInstance();
                break;
            case R.id.nav_relatorios:
                fragment = RelatoriosFragment.newInstance();
                break;
            case R.id.nav_pesquisa:
                fragment = PesquisaFragment.newInstance(null, PesquisaDao.TTypePesquisa.PESQUISA);
                break;
            case R.id.nav_etap:
                fragment = TapFragment.newInstance();
                break;
            case R.id.nav_sync_data:
                fragment = SyncDataFragment.newInstance();
                break;
            case R.id.nav_message:
                fragment = MessageFragment.newInstance();
                break;
            case R.id.nav_cr:
                fragment = CRFragment.newInstance();
                break;
            case R.id.nav_rastreio:

                break;
            case R.id.nav_teste:
                fragment = TesteFragment.newInstance();
                break;
        }

        return fragment;
    }

    public void setupNewFragment(Fragment fragment, MenuItem item) {
        mIndexNavCurrent = item.getItemId();

        Fragment finalFragment = fragment;
        for (Class c : finalFragment.getClass().getInterfaces()) {
            if (!BuildConfig.DEBUG && UnderDevelopment.class.equals(c)) {
                finalFragment = UnderDevelopmentFragment.newInstance();
                break;
            }
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.master_fragment_container, finalFragment)
                .commitAllowingStateLoss();

        item.setChecked(true);

        if (getSupportActionBar() != null) {
            if(item.getItemId() == R.id.nav_cr){
                getSupportActionBar().setTitle(getString(R.string.title_layout));
            }  else {
                getSupportActionBar().setTitle(item.getTitle());
            }


        }
    }

    public void setupHeaderData() {
        try {
            View navigationHeader = mNavigationView.getHeaderView(0);
            if (navigationHeader != null) {
                TextView nomeUsuario = navigationHeader.findViewById(R.id.nav_user_name_text_view);
                TextView emailUsuario = navigationHeader.findViewById(R.id.nav_user_email_text_view);
                TextView codigoUsuario = navigationHeader.findViewById(R.id.nav_user_id_text_view);
                TextView ultimaSincronizacao = navigationHeader.findViewById(
                        R.id.nav_last_sync_date_text_view);


                Usuario usuario = Current.getInstance(this).getUsuario();
                nomeUsuario.setText(usuario.getNomeReduzido());
                emailUsuario.setText(usuario.getEmail());
                codigoUsuario.setText(getString(R.string.usuario_id, usuario.getCodigo()));

                if (TextUtils.isEmpty(usuario.getEmail()))
                    emailUsuario.setVisibility(View.GONE);

                SyncDataDao masterDataDao = new SyncDataDao(this);
                Date dataUltimaSincronizacao = masterDataDao.getLastDateSync(usuario.getCodigo());


                if (dataUltimaSincronizacao != null) {
                    ultimaSincronizacao.setText(getString(R.string.ultima_sincronizacao,
                            FormatUtils.toDefaultDateAndHourFormat(this, dataUltimaSincronizacao)));
                } else {
                    ultimaSincronizacao.setText(getString(R.string.ultima_sincronizacao, "-"));
                }

            }
        } catch (Exception ex) {
            LogUser.log(Config.TAG, ex.toString());
        }
    }

    private void setupUnidadeNegocioComponent() {
        try {
            final UnidadeNegocioDao unDao = new UnidadeNegocioDao(this);
            List<UnidadeNegocio> unidades = unDao.getAll(Current.getInstance(this).getUsuario().getCodigo());

            RadioGroup unRadioGroup = getUnidadeNegocioRadioGroup();
            RadioGroup.LayoutParams defaultLayoutParams = new RadioGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            for (UnidadeNegocio un : unidades) {
                RadioButton unRadioButton = new RadioButton(unRadioGroup.getContext());
                unRadioButton.setId(Integer.parseInt(un.getCodigo()));
                unRadioButton.setText(un.getNome());
                unRadioButton.setTag(R.id.unidade_negocio_radio_button_tag, un.getCodigo());

                if (un.getCodigo().equals(Current.getInstance(this).getUnidadeNegocio().getCodigo())) {
                    unRadioButton.setChecked(true);
                }

                unRadioGroup.addView(unRadioButton, defaultLayoutParams);
            }

            unRadioGroup.setOnCheckedChangeListener((radioGroup, i) -> {
                RadioButton unRadioButton = radioGroup.findViewById(i);
                String codigoUnidadeNegocio = (String) unRadioButton.getTag(R.id.unidade_negocio_radio_button_tag);
                if (codigoUnidadeNegocio != null) {
                    Usuario usuario = Current.getInstance(this).getUsuario();
                    UnidadeNegocio unSelecionada = unDao.get(usuario.getCodigo(), codigoUnidadeNegocio);

                    Current.setValues(usuario, unSelecionada);

                    UserInfo userInfo = new UserInfo();
                    userInfo.saveUserUnidadeNegocioSelected(unSelecionada.getCodigo(), getApplicationContext());

                    finish();
                    startActivity(getIntent());
                }
            });
        }catch (Exception ex){
            LogUser.log(Config.TAG, ex.getMessage());
        }
    }

    private void setupNavigation() {
        try {

            Current current = Current.getInstance(this);

            Perfil perfil = current.getUsuario().getPerfil();
            Menu menu = mNavigationView.getMenu();

            if (perfil == null) {
                Current.clear(this);
                Intent loginIntent = new Intent(MasterActivity.this, LoginActivity.class);
                startActivity(loginIntent);
                finish();

                Toast toast = Toast.makeText(getApplicationContext(), R.string.perfil_change, Toast.LENGTH_LONG);
                toast.show();

                return;
            }

            String codUnNeg = current.getUnidadeNegocio().getCodigo();
            if (!perfil.permiteVenda(codUnNeg)) {
                menu.removeItem(R.id.nav_rota_guiada);
                menu.removeItem(R.id.nav_add_pedido);
                menu.removeItem(R.id.nav_pedidos);
                menu.removeItem(R.id.nav_rastreio);
            } else {
                if(perfil.isPermiteRotaGuiada() && !perfil.isPermitePlanejamentoRota()){
                    menu.removeItem(R.id.nav_add_pedido);
                } else {
                    menu.removeItem(R.id.nav_rota_guiada);
                }
            }

            if (!perfil.permiteVisualizarClientes()) {
                menu.removeItem(R.id.nav_clientes);
            }

            if (!perfil.permiteAprovacao(codUnNeg)) {
                menu.removeItem(R.id.nav_aprovacao);
            }

            if (!perfil.permiteLiberacao(codUnNeg)) {
                menu.removeItem(R.id.nav_liberacao);
            }

            if (!perfil.permiteVisualizarRotas()) {
                menu.removeItem(R.id.nav_rota);
            }

            if (!perfil.permiteVisualizarRelatorios()) {
                menu.removeItem(R.id.nav_relatorios);
            }

            if (!perfil.permitePesquisas()) {
                menu.removeItem(R.id.nav_pesquisa);
            }

            if (!perfil.permiteETap()) {
                menu.removeItem(R.id.nav_etap);
            }

            if (!perfil.isPermiteIsaac()) {
                menu.removeItem(R.id.nav_chat);
            }

            if(!perfil.isPermiteCR()){
                menu.removeItem(R.id.nav_cr);
            }

            if (!perfil.permiteRastreioPedidos()) {
                menu.removeItem(R.id.nav_rastreio);
            }

            if(!perfil.isPermiteRequisicao()){
                menu.removeItem(R.id.nav_requisicao);
            }

            if(!perfil.isPermitePlanejamentoRota()){
                menu.removeItem(R.id.nav_planejamento_rota);
            }
            menuItemMessage = menu.findItem(R.id.nav_message);



        } catch (Exception ex) {
            LogUser.log(Config.TAG, ex.toString());
            finish();
        }
    }

    private RadioGroup getUnidadeNegocioRadioGroup() {
        View navigationHeader = mNavigationView.getHeaderView(0);
        RadioGroup unidadeNegocioRadioGroup = navigationHeader.findViewById(
                R.id.unidade_negocio_radio_group);
        return unidadeNegocioRadioGroup;
    }

    private void exit() {
        new AlertDialog.Builder(this)
                .setMessage(R.string.exit_message)
                .setPositiveButton(R.string.yes, (dialogInterface, i) -> System.exit(0))
                .setNegativeButton(R.string.no, null)
                .show();
    }

    private void logout() {
        new AlertDialog.Builder(this)
                .setMessage(R.string.logout_message)
                .setPositiveButton(R.string.yes, (dialogInterface, i) -> {
                    Current.clear(this);
                    Intent loginIntent = new Intent(MasterActivity.this, LoginActivity.class);
                    startActivity(loginIntent);
                    JJSDK.clear(this);
                    SavePref savePref = new SavePref();
                    savePref.saveBoolSharedPreferences(Config.TAG_CACHE, Config.TAG, true, this);

                    finish();
                })
                .setNegativeButton(R.string.no, null)
                .show();
    }

    public void setDrawerEnabled(boolean enabled) {
        int lockMode = enabled ? DrawerLayout.LOCK_MODE_UNLOCKED :
                DrawerLayout.LOCK_MODE_LOCKED_CLOSED;
        mDrawerLayout.setDrawerLockMode(lockMode);
        mDrawerToggle.setDrawerIndicatorEnabled(enabled);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case Config.REQUEST_SETTINGS:
                if (resultCode == RESULT_OK) {
                    Intent loginIntent = new Intent(MasterActivity.this,
                            LoginActivity.class);
                    startActivity(loginIntent);
                    finish();
                }
                break;
            case Config.REQUEST_DETAIL_CLIENTE:
                if (resultCode == RESULT_OK) {

                    if (mNavigationView == null || mNavigationView.getMenu() == null ||   mNavigationView.getMenu().findItem(R.id.nav_pedidos) == null) {
                        return;
                    }

                    mNavigationView.getMenu().findItem(R.id.nav_pedidos).setChecked(true);
                    navigate(R.id.nav_pedidos, mNavigationView.getMenu().findItem(R.id.nav_pedidos));
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }

    @Override
    public void onClienteClick(Cliente cliente) {
        // this will be called when a cliente is selected in the clientes' list
        startActivityForResult(ClienteDetailActivity.newIntent(this, cliente.getCodigo(), false), Config.REQUEST_DETAIL_CLIENTE);
    }

    @Override
    public void onPedidoClick(PedidoViewType type, Pedido pedido, boolean forceSync) {
        // this will be called when a pedido is selected in the pedidos' list
        if (type == PedidoViewType.PEDIDO) {
            startActivity(PedidoDetailActivity.newIntent(this, pedido.getCodigo(), type, forceSync, false));
        } else {
            startActivityForResult(PedidoDetailActivity.newIntent(this, pedido.getCodigo(), type, forceSync, false), Config.REQUEST_APRO_LIB);
        }
    }

    private void setlogUserOnCrashlytics() {
        Current current = Current.getInstance(this);
        if (current == null || current.getUsuario() == null) {
            return;
        }

        Usuario usuario = current.getUsuario();
        FirebaseUtils.setUser(usuario.getCodigo(), usuario.getEmail(), usuario.getNome());
    }

    public NavigationView getNavigationView() {
        return mNavigationView;
    }

    public void setNavigationView(NavigationView mNavigationView) {
        this.mNavigationView = mNavigationView;
    }

    public int getBagdeNotification(){
        if(menuItemMessage == null) {
            return 0;
        } else {
            FrameLayout menuBadge = menuItemMessage.getActionView().findViewById(R.id.menu_badge_frame_layout);

            if (menuItemMessage == null || !hasMessage) {
                menuBadge.setVisibility(View.INVISIBLE);
                return 0;
            }

            Current current = Current.getInstance(this);

            MessageDao messageDao = new MessageDao(this);
            int badge = messageDao.countMensagensNovas(current.getUsuario(), current.getUnidadeNegocio().getCodigo());

            if ((badge) > 0) {
                ActionItemBadge.update(menuItemMessage, badge);
                menuBadge.setVisibility(View.VISIBLE);
            } else {
                menuBadge.setVisibility(View.INVISIBLE);
            }

            if (badgeOld != badge && mIndexNavCurrent == R.id.nav_home) {
                if (fragment != null) {
                    ((HomeFragment) fragment).setBagdeNotification(badge);
                }
            }

            badgeOld = badge;
            return badge;
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        CurrentActionPedido.getInstance().clear();
        NotificationManager nMgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        nMgr.cancelAll();
    }
}
