package br.com.jjconsulting.mobile.dansales;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import com.google.android.material.snackbar.Snackbar;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class HelpFragment extends Fragment {

    private Button mCallHelpDeskButton;

    public HelpFragment() {
        // Required empty public constructor
    }

    public static HelpFragment newInstance() {
        return new HelpFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_help, container, false);

        mCallHelpDeskButton = view.findViewById(R.id.call_help_desk_button);

        final Intent callHelpDesk = new Intent(Intent.ACTION_DIAL);
        String helpDeskNumber = getString(R.string.help_desk_phone_number);

        PackageManager packageManager = getActivity().getPackageManager();
        if (packageManager.resolveActivity(callHelpDesk,
                PackageManager.MATCH_DEFAULT_ONLY) == null) {
            mCallHelpDeskButton.setOnClickListener(v -> {
                Snackbar.make(view.findViewById(R.id.help_main_content_view_group),
                        R.string.cant_call, Snackbar.LENGTH_LONG).show();
            });
        } else {
            mCallHelpDeskButton.setOnClickListener(v -> {
                callHelpDesk.setData(Uri.parse("tel:" + helpDeskNumber));
                startActivity(callHelpDesk);
            });
        }

        return view;
    }
}
