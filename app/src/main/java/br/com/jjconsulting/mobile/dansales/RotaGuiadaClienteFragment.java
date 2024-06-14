package br.com.jjconsulting.mobile.dansales;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;

import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.snackbar.Snackbar;

import org.w3c.dom.Text;

import java.io.InputStreamReader;
import java.util.ArrayList;

import br.com.jjconsulting.mobile.dansales.base.BaseFragment;
import br.com.jjconsulting.mobile.dansales.connectionController.BaseConnection;
import br.com.jjconsulting.mobile.dansales.connectionController.SyncPlanejamentoRotaGuiadaConnection;
import br.com.jjconsulting.mobile.dansales.model.Perfil;
import br.com.jjconsulting.mobile.dansales.model.ResumeStore;
import br.com.jjconsulting.mobile.dansales.model.RotaGuiadaTabType;
import br.com.jjconsulting.mobile.dansales.model.RotaOrigem;
import br.com.jjconsulting.mobile.dansales.model.Rotas;
import br.com.jjconsulting.mobile.dansales.model.TActionRotaGuiada;
import br.com.jjconsulting.mobile.dansales.service.Current;
import br.com.jjconsulting.mobile.dansales.util.JJSyncRotaGuiada;
import br.com.jjconsulting.mobile.dansales.util.RotaGuiadaUtils;
import br.com.jjconsulting.mobile.dansales.viewModel.RotaViewModel;
import br.com.jjconsulting.mobile.jjlib.util.Config;
import br.com.jjconsulting.mobile.jjlib.util.DialogsCustom;
import br.com.jjconsulting.mobile.jjlib.util.FormatUtils;
import br.com.jjconsulting.mobile.jjlib.util.Geocoding;
import br.com.jjconsulting.mobile.jjlib.util.LogUser;
import br.com.jjconsulting.mobile.jjlib.util.TextUtils;

public class RotaGuiadaClienteFragment extends BaseFragment implements OnMapReadyCallback, View.OnClickListener {

    private static int LOCATION_REQ_CODE = 1;

    private final int INTERVAL_LOCATION = 20 * 10000;
    private final int ZOOM_MAP = 15;

    private int RADIUS = 300;

    private GoogleMap googleMap;

    private TextView mClienteCodigoTextView;
    private TextView mClienteNomeTextView;
    private TextView mClienteAdressTextView;
    private TextView mDistanceTextView;
    private TextView mResumeStoreTextView;

    private ViewGroup mClienteFormWrapper;
    private ViewGroup mClienteAddressFormWrapper;

    private ViewPager viewPager;

    private Button mCheckinButton;
    private Button mContinueButton;


    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;

    private RotaViewModel mRotaViewModelProviders;

    private DialogsCustom dialogsDefault;

    private String address;

    private float distance = -1;

    private boolean isEdit;

    private Perfil perfil;

    public RotaGuiadaClienteFragment() {
        // Required empty public constructor
    }

    public static RotaGuiadaClienteFragment newInstance(ViewPager viewPager, boolean isEdit) {
        RotaGuiadaClienteFragment rotaGuiadaClienteFragment = new RotaGuiadaClienteFragment();
        rotaGuiadaClienteFragment.setViewPager(viewPager);
        rotaGuiadaClienteFragment.setEdit(isEdit);

        return rotaGuiadaClienteFragment;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        removeLocation();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRotaViewModelProviders = ViewModelProviders.of(getActivity()).get(RotaViewModel.class);
        RotaGuiadaUtils.checkValidRota(getActivity(), mRotaViewModelProviders.getRotas().getValue(), isEdit);

        setNewLocation(null);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_rota_guiada_cliente, container, false);

        mClienteCodigoTextView = view.findViewById(R.id.rg_cliente_codigo_text_view);
        mClienteNomeTextView = view.findViewById(R.id.rg_cliente_nome_text_view);
        mClienteFormWrapper = view.findViewById(R.id.rg_cliente_form_wrapper);
        mClienteAddressFormWrapper = view.findViewById(R.id.rg_cliente_address_form_wrapper);
        mClienteAdressTextView = view.findViewById(R.id.rg_cliente_adress_text_view);
        mDistanceTextView = view.findViewById(R.id.rg_distance_text_view);
        mResumeStoreTextView = view.findViewById(R.id.resume_store_tex_view);
        mResumeStoreTextView.setPaintFlags(mResumeStoreTextView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        mResumeStoreTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Rotas rota = mRotaViewModelProviders.getRotas().getValue();
                showDialogResumeStore(true, mRotaViewModelProviders.getPromotor().getValue(),
                        rota.getCliente().getCodigo(), rota.getCodUnidNeg());
            }
        });


        mCheckinButton = view.findViewById(R.id.checkin_button);
        mContinueButton = view.findViewById(R.id.continue_button);

        dialogsDefault = new DialogsCustom(getActivity());

        mClienteAddressFormWrapper.setOnClickListener(this);
        mClienteFormWrapper.setOnClickListener(this);
        mCheckinButton.setOnClickListener(this);
        mContinueButton.setOnClickListener(this);

        perfil = Current.getInstance(getContext()).getUsuario().getPerfil();

        RADIUS = perfil.getRotaRaioAderencia();
        visibleFields();
        bind();

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.frg);  //use SuppoprtMapFragment for using in fragment instead of activity  MapFragment = activity   SupportMapFragment = fragment
        mapFragment.getMapAsync(this);

        return view;
    }

    @Override
    public void onClick(View v){

        Rotas rota = mRotaViewModelProviders.getRotas().getValue();

        if(!RotaGuiadaUtils.checkValidRota(getActivity(), mRotaViewModelProviders.getRotas().getValue(), isEdit))
            return;

        switch (v.getId()){
            case R.id.rg_cliente_form_wrapper:
                startActivity(ClienteDetailActivity.newIntent(getActivity(),
                        rota.getCliente().getCodigo(), true));

                break;
            case R.id.rg_cliente_address_form_wrapper:
                Intent geoLocation = new Intent(Intent.ACTION_VIEW);
                geoLocation.setData(Uri.parse("geo:0,0?q=" +
                        rota.getCliente().getEndereco() + "+" +
                        rota.getCliente().getMunicipio() + "+" +
                        rota.getCliente().getUf() + "+"));

                PackageManager packageManager = getActivity().getPackageManager();
                if (packageManager.resolveActivity(geoLocation,
                        PackageManager.MATCH_DEFAULT_ONLY) == null) {
                    Snackbar.make(v, R.string.cant_call, Snackbar.LENGTH_LONG).show();
                } else {
                    startActivity(geoLocation);
                }
                break;
             case R.id.checkin_button:
                 if (!TextUtils.isNullOrEmpty(rota.getCheckin()) && TextUtils.isNullOrEmpty(rota.getCheckout())) {
                     dialogsDefault.showDialogMessage(getString(R.string.checkin_accomplished), dialogsDefault.DIALOG_TYPE_WARNING, null);
                     return;
                 }

                 if (!perfil.isRotaCheckInForaArea()) {
                     checkin(rota);
                 } else {

                     if(checkRadiusCliente()){
                         checkin(rota);
                         return;
                     }

                     dialogsDefault.showDialogQuestion(getString(R.string.not_area_client), dialogsDefault.DIALOG_TYPE_WARNING, new DialogsCustom.OnClickDialogQuestion() {
                         @Override
                         public void onClickPositive() {
                             checkin(rota);
                         }

                         @Override
                         public void onClickNegative() {
                         }
                     });
                 }

                 break;
            case R.id.continue_button:
                resumeRota(rota);
                break;
        }
    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;

        try {
            if (ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) ==
                            PackageManager.PERMISSION_GRANTED) {
                configMap();
            } else {
                requestPermissions(
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION},
                        LOCATION_REQ_CODE);
            }
        }catch (Exception ex){
            LogUser.log(Config.TAG, ex.toString());
        }
    }

    private void visibleFields(){
        mDistanceTextView.setVisibility(View.GONE);

        if(mRotaViewModelProviders.getIsVisitaPromotor().getValue() &&
                !TextUtils.isNullOrEmpty(mRotaViewModelProviders.getPromotor().getValue())){
            mResumeStoreTextView.setVisibility(View.VISIBLE);
        } else {
            mResumeStoreTextView.setVisibility(View.GONE);
        }

        if(isEdit){
            Rotas rota = mRotaViewModelProviders.getRotas().getValue();

            if(rota.getStatus() == RotaGuiadaUtils.STATUS_RG_EM_ANDAMENTO
                    || rota.getStatus() == RotaGuiadaUtils.STATUS_RG_PAUSADO){
                mCheckinButton.setVisibility(View.GONE);
            }

            if(rota.getStatus() != RotaGuiadaUtils.STATUS_RG_PAUSADO){
                mContinueButton.setVisibility(rota.getStatus() == RotaGuiadaUtils.STATUS_RG_EM_ANDAMENTO ? View.INVISIBLE:View.GONE);
            }
        } else {
            mCheckinButton.setVisibility(View.INVISIBLE);
            mContinueButton.setVisibility(View.INVISIBLE);
        }
    }

    private void bind() {
        Rotas rota = mRotaViewModelProviders.getRotas().getValue();

        address = rota.getCliente().getEndereco() + " - " + rota.getCliente().getMunicipio() +
                "-" +  rota.getCliente().getUf();
        mClienteCodigoTextView.setText(rota.getCliente().getCodigo());
        mClienteNomeTextView.setText(rota.getCliente().getNome());

        mClienteAdressTextView.setText(address);

        //Abre o popup de notas apenas a primeira, caso parametro isplanejamento = true e tenha informação do promotor
        if(!mRotaViewModelProviders.getIsFirstOpenPopup().getValue() &&
                mRotaViewModelProviders.getIsVisitaPromotor().getValue() &&
                !TextUtils.isNullOrEmpty(mRotaViewModelProviders.getPromotor().getValue())){
            showDialogResumeStore(false, mRotaViewModelProviders.getPromotor().getValue(),
                     rota.getCliente().getCodigo(), rota.getCodUnidNeg());
        }
    }

    private void resumeRota(Rotas rotas){
        RotaOrigem rotaOrigem = mRotaViewModelProviders.getRotaOrigem().getValue();

        RotaGuiadaUtils.actionRota(getActivity(), rotaOrigem,  rotas, TActionRotaGuiada.RESUME,
            new RotaGuiadaUtils.OnSyncRota() {
                @Override
                public void sync(Rotas currentRota) {
                    RotaGuiadaDetailActivity.isUpdate = true;
                    mRotaViewModelProviders.getRotas().setValue(currentRota);

                    JJSyncRotaGuiada jjSyncRotaGuiada = new JJSyncRotaGuiada();
                    jjSyncRotaGuiada.syncRotaGuiada(getActivity(), ()-> {
                        updateAfterAction();
                        viewPager.setCurrentItem(RotaGuiadaTabType.RESUMO_FRAGMENT.getValue());
                    });
                }
            });
    }

    private void checkin(Rotas rotas){
        RotaOrigem rotaOrigem = mRotaViewModelProviders.getRotaOrigem().getValue();

        RotaGuiadaUtils.actionRota(getActivity(), rotaOrigem,  rotas, TActionRotaGuiada.CHECKIN,
            new RotaGuiadaUtils.OnSyncRota() {
                @Override
                public void sync(Rotas currentRota) {
                    RotaGuiadaDetailActivity.isUpdate = true;
                    mRotaViewModelProviders.getRotas().setValue(currentRota);

                    JJSyncRotaGuiada jjSyncRotaGuiada = new JJSyncRotaGuiada();
                    jjSyncRotaGuiada.syncRotaGuiada(getActivity(), ()-> {
                        updateAfterAction();
                        viewPager.setCurrentItem(RotaGuiadaTabType.RESUMO_FRAGMENT.getValue());
                        RotaGuiadaResumoFragment rotaGuiadaResumoFragment = ((RotaGuiadaResumoFragment)
                                viewPager.getAdapter().instantiateItem(viewPager, RotaGuiadaTabType.RESUMO_FRAGMENT.getValue()));
                        rotaGuiadaResumoFragment.findItens();
                    });
                }
            });
    }

    private void updateAfterAction(){
        bind();
        visibleFields();
    }


    private void showDialogResumeStore(boolean isShowError, String promotor, String codCli, String unNeg){

        mRotaViewModelProviders.getIsFirstOpenPopup().setValue(true);

        dialogsDefault.showDialogProgress(getContext(), getString(R.string.aguarde));

        SyncPlanejamentoRotaGuiadaConnection planejamentoRotaGuiadaConnection = new SyncPlanejamentoRotaGuiadaConnection(getContext(), new BaseConnection.ConnectionListener() {
            @Override
            public void onSucess(String response, int typeConnection, InputStreamReader reader, ArrayList<String[]> list) {
                dialogsDefault.dissmissDialogProgress();

                try{
                    ResumeStore resumeStore = gson.fromJson(response, ResumeStore.class);

                    if(resumeStore.isSuccess()){
                        createDialogResumeSore(resumeStore);
                    } else {
                        if(isShowError){
                            showMessageError(resumeStore.getMessage());
                        }
                    }
                } catch (Exception ex){
                    if(isShowError){
                        showMessageError(getString(R.string.planejamneto_rota_resume_store_erro));
                    }
                }
            }

            @Override
            public void onError(VolleyError volleyError, int code, int typeConnection, String response) {
                dialogsDefault.dissmissDialogProgress();
                if(isShowError){
                    showMessageError(getString(R.string.planejamneto_rota_resume_store_erro));
                }
            }
        });

        planejamentoRotaGuiadaConnection.getResumeStore(promotor, codCli, unNeg);
    }

    private void createDialogResumeSore(ResumeStore resumeStore){
        Dialog messageDialog = new Dialog(getContext(), android.R.style.Theme_Translucent_NoTitleBar);
        messageDialog.setCancelable(false);
        messageDialog.setContentView(R.layout.dialog_resume_store);

        TextView lastNoteTextView = messageDialog.findViewById(R.id.last_note_text_view);
        lastNoteTextView.setText(FormatUtils.toDoubleFormat(resumeStore.getUltimaNota()) + "");

        TextView averageTimeTextView = messageDialog.findViewById(R.id.average_time_text_view);
        averageTimeTextView.setText(resumeStore.getTempoMedio() + "");

        TextView sumNoteTextView = messageDialog.findViewById(R.id.sum_note_client_text_view);
        sumNoteTextView.setText(FormatUtils.toDoubleFormat(resumeStore.getVisitaMedia()) + "");

        Button okButton = messageDialog.findViewById(R.id.ok_button);
        okButton.setOnClickListener(view -> {
            messageDialog.dismiss();
        });

        getActivity().runOnUiThread(()-> {
            messageDialog.show();
        });

    }

    private void configMap(){

        if (ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED) {

            googleMap.setMyLocationEnabled(true);
            googleMap.setMyLocationEnabled(true);

            configLocation();
            mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);

            Rotas rotas = mRotaViewModelProviders.getRotas().getValue();

            if (rotas.getCliente().getLatitude() == 0.0 || rotas.getCliente().getLongitude() == 0.0) {
                Geocoding locationAddress = new Geocoding();
                locationAddress.getAddressFromLocation(address,
                        getActivity(), new Handler() {
                            @Override
                            public void handleMessage(Message message) {
                            switch (message.what) {
                                case 1:
                                        Bundle bundle = message.getData();
                                        rotas.getCliente().setLatitude(bundle.getDouble("latitude"));
                                        rotas.getCliente().setLongitude(bundle.getDouble("longitude"));
                                        mRotaViewModelProviders.getRotas().setValue(rotas);

                                addMarker();
                                checkLastLocation();
                                break;
                            }
                            }
                        });
            } else {
                addMarker();
                checkLastLocation();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    configMap();
                } else {
                   getActivity().finish();
                }
                return;
            }
        }
    }

    private void addMarker(){
        Rotas rota = mRotaViewModelProviders.getRotas().getValue();

        googleMap.addCircle(new CircleOptions()
                .center(new LatLng(rota.getCliente().getLatitude(), rota.getCliente().getLongitude()))
                .radius(RADIUS)
                .strokeWidth(1f)
                .fillColor(0x880000FF));

        googleMap.addMarker(new MarkerOptions().position(new LatLng(rota.getCliente().getLatitude(), rota.getCliente().getLongitude())).title(rota.getCliente().getNome()));
    }

    public void configLocation(){

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(INTERVAL_LOCATION);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    calcDistance(location);
                }
            }
        };
    }

    public void setNewLocation( Location location){
        double logitude = 0;
        double latitude = 0;

        if (location != null) {
            logitude = location.getLongitude();
            latitude = location.getLatitude();
        }

        RotaOrigem origem = new RotaOrigem();
        origem.setLatCheckin(latitude);
        origem.setLongCheckin(logitude);
        origem.setInRadius(checkRadiusCliente());

        mRotaViewModelProviders.getRotaOrigem().setValue    (origem);
    }

    public void calcDistance(Location location ){
        Rotas rota = mRotaViewModelProviders.getRotas().getValue();

        if(getContext() == null){
            return;
        }

        if(location != null) {

            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            googleMap.animateCamera(CameraUpdateFactory.zoomTo(ZOOM_MAP));

            if (rota.getCliente().getLatitude() == 0.0) {
                mDistanceTextView.setVisibility(View.GONE);
            } else {
                Location locationA = new Location("A");
                locationA.setLatitude(latLng.latitude);
                locationA.setLatitude(latLng.longitude);

                Location locationB = new Location("B");
                locationB.setLatitude(rota.getCliente().getLatitude());
                locationB.setLatitude(rota.getCliente().getLongitude());

                mDistanceTextView.setVisibility(View.VISIBLE);

                String distanceString = (Geocoding.getDistance(locationA, locationB));
                mDistanceTextView.setText(getString(R.string.distance_marker, rota.getCliente().getCodigo(), distanceString));

                distanceString = distanceString.replace(".", "");

                try{
                    distance = Float.parseFloat(distanceString.replace(",", ".").trim());
                } catch (Exception ex){
                    //Caso não conseguir realizar cáculo de distancia retorna 0
                    distance = 0;
                }

                LogUser.log(Config.TAG, Geocoding.getDistance(locationA, locationB) + "");
            }
        }

        setNewLocation(location);

    }

    public void checkLastLocation(){
        if(getActivity() != null){
            if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) ==
                            PackageManager.PERMISSION_GRANTED) {
                mFusedLocationClient.getLastLocation().addOnSuccessListener((location)->{
                    if(getActivity() != null && !getActivity().isFinishing()) {
                        calcDistance(location);
                    }
                });
            }
        }
    }

    public void removeLocation(){
        if (mFusedLocationClient != null) {
            mFusedLocationClient.removeLocationUpdates(locationCallback);
        }
    }

    private boolean checkRadiusCliente(){
       if(RADIUS == 0)
           return false;

        boolean isCheck = true;
            if(distance == -1 || (distance * 1000) > RADIUS){
            isCheck = false;
        }

        return isCheck;
    }

    public boolean isEdit() {
        return isEdit;
    }

    public void setEdit(boolean edit) {
        isEdit = edit;
    }

    public void setViewPager(ViewPager viewPager) {
        this.viewPager = viewPager;
    }
}
