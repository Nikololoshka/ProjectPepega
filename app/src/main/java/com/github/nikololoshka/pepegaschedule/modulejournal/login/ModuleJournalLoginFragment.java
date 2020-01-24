package com.github.nikololoshka.pepegaschedule.modulejournal.login;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;

import com.github.nikololoshka.pepegaschedule.R;
import com.github.nikololoshka.pepegaschedule.modulejournal.network.ModuleJournalService;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Objects;

/**
 * Фрагмент авторизации в модульный журнал.
 */
public class ModuleJournalLoginFragment extends Fragment
        implements View.OnClickListener,
        LoaderManager.LoaderCallbacks<ModuleJournalLoginLoader.LoadData> {

    private static final int MODULE_JOURNAL_LOGIN_LOADER = 0;

    private TextInputEditText mLoginFieldEditText;
    private TextInputEditText mPasswordFieldEditText;
    private Button mSignInButton;
    private Button mForgotPasswordButton;

    private ModuleJournalLoginLoader mLoginLoader;

    public ModuleJournalLoginFragment() {
        super();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_module_journal_login, container, false);

        mLoginFieldEditText = view.findViewById(R.id.mj_login);
        mPasswordFieldEditText = view.findViewById(R.id.mj_password);

        mSignInButton = view.findViewById(R.id.mj_sign_in);
        mSignInButton.setOnClickListener(this);

        mForgotPasswordButton = view.findViewById(R.id.mj_forgot_password);
        mForgotPasswordButton.setOnClickListener(this);

        mLoginLoader = (ModuleJournalLoginLoader) LoaderManager.getInstance(this)
                .initLoader(MODULE_JOURNAL_LOGIN_LOADER, null, this);

        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        // скрыть клавиатуру если выходим
        if (getActivity() != null) {
            InputMethodManager manager = ContextCompat.getSystemService(getActivity(),
                    InputMethodManager.class);

            View currentFocusedView = getActivity().getCurrentFocus();
            if (currentFocusedView != null && manager != null) {
                manager.hideSoftInputFromWindow(currentFocusedView.getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            // авторизация
            case R.id.mj_sign_in: {
                setEnabledView(false);

                mLoginFieldEditText.clearFocus();
                mPasswordFieldEditText.clearFocus();

                Editable login = mLoginFieldEditText.getText();
                Editable password = mPasswordFieldEditText.getText();

                mLoginLoader.signIn(login != null ? login.toString() : null,
                        password != null ? password.toString() : null);

                break;
            }
            // забыт пароль
            case R.id.mj_forgot_password: {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(ModuleJournalService.URL));
                startActivity(intent);
                break;
            }
        }
    }

    /**
     * Спускает навигацию до фрагмента с модульным журналом.
     */
    private void popToModuleJournal() {
        if (getActivity() != null) {
            NavController controller = Navigation.findNavController(getActivity(), R.id.nav_host);

            NavOptions options = new NavOptions.Builder()
                    .setPopUpTo(R.id.nav_module_journal_login_fragment, true)
                    .setEnterAnim(R.anim.nav_default_enter_anim)
                    .setExitAnim(R.anim.nav_default_exit_anim)
                    .setPopEnterAnim(R.anim.nav_default_pop_enter_anim)
                    .setPopExitAnim(R.anim.nav_default_pop_exit_anim)
                    .build();

            controller.navigate(R.id.nav_module_journal_fragment, null, options);
        }
    }

    /**
     * Устанавливает доступность по нажатию view элементом.
     * @param enabledView true - включить, false - выключить view.
     */
    private void setEnabledView(boolean enabledView) {
        mLoginFieldEditText.setEnabled(enabledView);
        mPasswordFieldEditText.setEnabled(enabledView);

        mSignInButton.setEnabled(enabledView);
        mForgotPasswordButton.setEnabled(enabledView);
    }

    @NonNull
    @Override
    public Loader<ModuleJournalLoginLoader.LoadData> onCreateLoader(int id, @Nullable Bundle args) {
        return new ModuleJournalLoginLoader(Objects.requireNonNull(getContext()));
    }

    @Override
    public void onLoadFinished(@NonNull Loader<ModuleJournalLoginLoader.LoadData> loader,
                               ModuleJournalLoginLoader.LoadData data) {
        if (data.signIn) {
            popToModuleJournal();

        } else {
            new AlertDialog.Builder(getContext())
                    .setTitle(data.errorTitle)
                    .setMessage(data.errorDescription)
                    .setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create()
                    .show();

            setEnabledView(true);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<ModuleJournalLoginLoader.LoadData> loader) {

    }
}
