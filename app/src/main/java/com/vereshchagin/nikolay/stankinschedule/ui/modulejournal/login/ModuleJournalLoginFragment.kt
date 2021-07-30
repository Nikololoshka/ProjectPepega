package com.vereshchagin.nikolay.stankinschedule.ui.modulejournal.login

import android.animation.ValueAnimator
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.vereshchagin.nikolay.stankinschedule.R
import com.vereshchagin.nikolay.stankinschedule.databinding.FragmentModuleJournalLoginBinding
import com.vereshchagin.nikolay.stankinschedule.repository.ModuleJournalRepository
import com.vereshchagin.nikolay.stankinschedule.ui.BaseFragment
import com.vereshchagin.nikolay.stankinschedule.utils.CommonUtils
import com.vereshchagin.nikolay.stankinschedule.utils.ExceptionUtils
import com.vereshchagin.nikolay.stankinschedule.utils.State

/**
 * Фрагмент входа в модульный журнал.
 */
class ModuleJournalLoginFragment :
    BaseFragment<FragmentModuleJournalLoginBinding>(FragmentModuleJournalLoginBinding::inflate),
    View.OnClickListener {

    /**
     * ViewModel фрагмента.
     */
    private val viewModel by viewModels<ModuleJournalLoginViewModel> {
        ModuleJournalLoginViewModel.Factory(requireActivity().application)
    }

    /**
     * Аниматор прогресса авторизации в модульном журнале.
     */
    private val loadAnimator = ValueAnimator().apply {
        duration = 300
        addUpdateListener(this@ModuleJournalLoginFragment::loadAnimatorUpdate)
    }

    private var loadingHeight = 50

    override fun onPostCreateView(savedInstanceState: Bundle?) {

        loadingHeight = resources.getDimensionPixelOffset(R.dimen.horizontal_loading_height)

        binding.mjLogin.addTextChangedListener(onTextChanged = { text: CharSequence?, _: Int, _: Int, _: Int ->
            if (text != null) {
                val isError = text.isEmpty()
                binding.mjLoginLayout.error = if (isError) {
                    getString(R.string.mj_empty_filed)
                } else {
                    null
                }
            }
        })

        binding.mjPassword.addTextChangedListener(onTextChanged = { text: CharSequence?, _: Int, _: Int, _: Int ->
            if (text != null) {
                val isError = text.isEmpty()
                binding.mjPasswordLayout.error = if (isError) {
                    getString(R.string.mj_empty_filed)
                } else {
                    null
                }
            }
        })

        binding.mjPassword.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                signIn()
                true
            } else {
                false
            }
        }

        binding.mjSignIn.setOnClickListener(this)
        binding.mjForgotPassword.setOnClickListener(this)

        // статус авторизации
        viewModel.authorizedState.observe(viewLifecycleOwner) {
            val state = it ?: return@observe

            when (state) {
                is State.Loading -> {
                    setLoadState(true)
                }
                is State.Success -> {
                    // успешно авторизован
                    if (state.data) {
                        loadAnimator.end()
                        navigateToModuleJournal()
                    }
                    setLoadState(false)
                }
                is State.Failed -> {
                    val description = ExceptionUtils.errorDescription(state.error, requireContext())

                    MaterialAlertDialogBuilder(requireContext())
                        .setTitle(R.string.error)
                        .setMessage(description)
                        .setNeutralButton(R.string.ok) { dialog, _ ->
                            dialog.dismiss()
                        }
                        .show()

                    setLoadState(false)
                }
            }
        }
    }

    override fun onDestroyView() {
        loadAnimator.end()
        super.onDestroyView()
        // скрыть клавиатуру перед выходом
        hideKeyboard()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            // авторизация
            R.id.mj_sign_in -> {
                signIn()
            }
            // забыт пароль
            R.id.mj_forgot_password -> {
                CommonUtils.openBrowser(requireContext(), ModuleJournalRepository.BASE_URL)
            }
        }
    }

    /**
     * Выполнение входа в модульный журнал.
     */
    private fun signIn() {
        if (!fieldsIsCorrect()) {
            return
        }

        setLoadState(true)
        binding.mjLogin.clearFocus()
        binding.mjPassword.clearFocus()

        val login = binding.mjLogin.text.toString()
        val password = binding.mjPassword.text.toString()

        viewModel.signIn(login, password)
    }

    private fun fieldsIsCorrect(): Boolean {
        val login = binding.mjLogin.text.toString()
        val password = binding.mjPassword.text.toString()

        if (login.isEmpty()) {
            binding.mjLoginLayout.isErrorEnabled = true
            binding.mjLoginLayout.error = getString(R.string.mj_empty_filed)
            return false
        }

        if (password.isEmpty()) {
            binding.mjPasswordLayout.isErrorEnabled = true
            binding.mjPasswordLayout.error = getString(R.string.mj_empty_filed)
            return false
        }

        return true
    }

    /**
     * Устанавливает состоянии загрузки в фрагмент.
     */
    private fun setLoadState(load: Boolean) {
        binding.mjLogin.isEnabled = !load
        binding.mjPassword.isEnabled = !load

        binding.mjSignIn.isEnabled = !load
        binding.mjForgotPassword.isEnabled = !load

        startLoadAnimator(load)
    }

    /**
     * Начинает анимацию загрузки при входе в модульный журнал.
     */
    private fun startLoadAnimator(load: Boolean) {
        loadAnimator.apply {
            setIntValues(binding.mjLoginLoading.measuredHeight, if (load) loadingHeight else 0)
            start()
        }
    }

    /**
     * Вызывается, при обновлении значения аниматора загрузки.
     */
    private fun loadAnimatorUpdate(animator: ValueAnimator) {
        // возможен случай, что анимация может завершиться после уничтожения binding
        rawBinding?.let {
            it.mjLoginLoading.layoutParams.height = animator.animatedValue as Int
            it.mjLoginLoading.requestLayout()
        }
    }

    /**
     * Переходит к фрагменту модульного журнала.
     */
    private fun navigateToModuleJournal() {
        val options = NavOptions.Builder()
            .setPopUpTo(R.id.nav_module_journal_login_fragment, true)
            .setEnterAnim(R.anim.nav_default_enter_anim)
            .setExitAnim(R.anim.nav_default_exit_anim)
            .setPopEnterAnim(R.anim.nav_default_pop_enter_anim)
            .setPopExitAnim(R.anim.nav_default_pop_exit_anim)
            .build()

        navigateTo(R.id.nav_module_journal_fragment, options = options)
    }
}