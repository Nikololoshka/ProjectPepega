package com.vereshchagin.nikolay.stankinschedule.modulejournal.login;

import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.vereshchagin.nikolay.stankinschedule.R;
import com.vereshchagin.nikolay.stankinschedule.modulejournal.network.ModuleJournalError;
import com.vereshchagin.nikolay.stankinschedule.modulejournal.network.ModuleJournalService;
import com.vereshchagin.nikolay.stankinschedule.utils.CommonUtils;
import com.vereshchagin.nikolay.stankinschedule.utils.TextWatcherWrapper;

import java.lang.ref.WeakReference;

/**
 * Фрагмент авторизации в модульный журнал.
 */
public class ModuleJournalLoginFragment extends Fragment
        implements View.OnClickListener {

    private TextInputLayout mLoginFieldLayout;
    private TextInputEditText mLoginFieldEditText;

    private TextInputLayout mPasswordFieldLayout;
    private TextInputEditText mPasswordFieldEditText;

    private Button mSignInButton;
    private Button mForgotPasswordButton;

    private ProgressBar mLoginLoading;
    private ValueAnimator mLoginLoadingAnimator;
    private int mLoginLoadingHeight;

    /**
     * ViewModel для авторизации.
     */
    private ModuleJournalLoginModel mModuleJournalLoginModel;

    public ModuleJournalLoginFragment() {
        super();

        mLoginLoadingAnimator = new ValueAnimator();
        mLoginLoadingAnimator.setDuration(300);
        mLoginLoadingAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mLoginLoading.getLayoutParams().height = (int) animation.getAnimatedValue();
                mLoginLoading.requestLayout();
            }
        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_module_journal_login, container, false);

        mLoginFieldLayout = view.findViewById(R.id.mj_login_layout);
        mLoginFieldEditText = view.findViewById(R.id.mj_login);

        mPasswordFieldLayout = view.findViewById(R.id.mj_password_layout);
        mPasswordFieldEditText = view.findViewById(R.id.mj_password);

        DataTextWatcher loginWatcher = new DataTextWatcher(mLoginFieldLayout);
        mLoginFieldEditText.addTextChangedListener(loginWatcher);

        DataTextWatcher passwordWatcher = new DataTextWatcher(mPasswordFieldLayout);
        mPasswordFieldEditText.addTextChangedListener(passwordWatcher);

        mSignInButton = view.findViewById(R.id.mj_sign_in);
        mSignInButton.setOnClickListener(this);

        mForgotPasswordButton = view.findViewById(R.id.mj_forgot_password);
        mForgotPasswordButton.setOnClickListener(this);

        mLoginLoading = view.findViewById(R.id.mj_login_loading);
        mLoginLoadingHeight = getResources().getDimensionPixelOffset(R.dimen.horizontal_loading_height);

        mModuleJournalLoginModel = new ViewModelProvider(this,
                new ModuleJournalLoginModel.Factory(getActivity().getApplication()))
                .get(ModuleJournalLoginModel.class);

        mModuleJournalLoginModel.stateData().observe(getViewLifecycleOwner(), new Observer<ModuleJournalLoginModel.State>() {
            @Override
            public void onChanged(ModuleJournalLoginModel.State state) {
                stateChanged(state);
            }
        });

        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        // скрыть клавиатуру перед выходом
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
                if (!fieldsIsCorrect()) {
                    return;
                }

                setLoading(true);

                mLoginFieldEditText.clearFocus();
                mPasswordFieldEditText.clearFocus();

                Editable login = mLoginFieldEditText.getText();
                Editable password = mPasswordFieldEditText.getText();

                mModuleJournalLoginModel.singIn(login == null ? "" : login.toString(),
                        password == null ? "" : password.toString());

                break;
            }
            // забыт пароль
            case R.id.mj_forgot_password: {
                Context context = getContext();
                if (context != null) {
                    CommonUtils.openBrowser(context, ModuleJournalService.URL);
                }
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
     * Устанавливает загрузку в фрагмент, пока происходит авторизация.
     * @param isLoading true - включить, false - выключить.
     */
    private void setLoading(boolean isLoading) {
        mLoginFieldEditText.setEnabled(!isLoading);
        mPasswordFieldEditText.setEnabled(!isLoading);

        mSignInButton.setEnabled(!isLoading);
        mForgotPasswordButton.setEnabled(!isLoading);

        int target = isLoading ? mLoginLoadingHeight : 0;
        mLoginLoadingAnimator.setIntValues(mLoginLoading.getMeasuredHeight(), target);
        mLoginLoadingAnimator.start();
    }

    /**
     * Проверяет поля для ввода данных на корректность.
     * @return true - поля заполнены правильно, иначе false.
     */
    private boolean fieldsIsCorrect() {
        Editable login = mLoginFieldEditText.getText();
        Editable password = mPasswordFieldEditText.getText();

        String loginText = login != null ? login.toString() : null;
        String passwordText = password != null ? password.toString() : null;

        if (loginText == null || passwordText == null) {
            return false;
        }

        if (loginText.isEmpty()) {
            mLoginFieldLayout.setErrorEnabled(true);
            mLoginFieldLayout.setError(getString(R.string.mj_empty_filed));
            return false;
        }

        if (passwordText.isEmpty()) {
            mPasswordFieldLayout.setEnabled(true);
            mPasswordFieldLayout.setError(getString(R.string.mj_empty_filed));
            return false;
        }

        return true;
    }

    /**
     * Вызывается, когда поменялось состояние авторизации.
     * @param state состояние.
     */
    private void stateChanged(@NonNull ModuleJournalLoginModel.State state) {
        switch (state) {
            case AUTHORIZED: {
                popToModuleJournal();
                break;
            }
            case LOADING: {
                setLoading(true);
                break;
            }
            case ERROR: {
                setLoading(false);

                ModuleJournalError error = mModuleJournalLoginModel.error();

                if (error == null) {
                    return;
                }

                String title = error.errorTitle();
                if (title == null) {
                    title = getString(error.errorTitleRes());
                }

                String description = error.errorDescription();
                if (description == null) {
                    description = getString(error.errorDescriptionRes());
                }

                new AlertDialog.Builder(getContext())
                        .setTitle(title)
                        .setMessage(description)
                        .setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .create()
                        .show();
                break;
            }
            case WAIT: {
                setLoading(false);
                break;
            }
        }
    }


    /**
     * Watcher для проверки полей ввода данных.
     */
    private class DataTextWatcher extends TextWatcherWrapper {

        private WeakReference<TextInputLayout> mFieldLayout;

        DataTextWatcher(@NonNull TextInputLayout fieldLayout) {
            mFieldLayout = new WeakReference<>(fieldLayout);
        }

        @Override
        public void onTextChanged(@NonNull String s) {
            TextInputLayout fieldLayout = mFieldLayout.get();
            if (fieldLayout  == null) {
                return;
            }

            if (s.length() == 0) {
                fieldLayout.setErrorEnabled(true);
                fieldLayout.setError(getString(R.string.mj_empty_filed));
            } else {
                fieldLayout.setErrorEnabled(false);
                fieldLayout .setError(null);
            }
        }
    }
}
