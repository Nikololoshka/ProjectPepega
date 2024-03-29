package com.vereshchagin.nikolay.stankinschedule.journal.login.ui

import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.vereshchagin.nikolay.stankinschedule.core.ui.components.TrackCurrentScreen
import com.vereshchagin.nikolay.stankinschedule.core.ui.ext.Zero
import com.vereshchagin.nikolay.stankinschedule.core.ui.theme.Dimen
import com.vereshchagin.nikolay.stankinschedule.core.ui.utils.BrowserUtils
import com.vereshchagin.nikolay.stankinschedule.journal.core.domain.utils.Constants
import com.vereshchagin.nikolay.stankinschedule.journal.login.ui.components.LoginError
import com.vereshchagin.nikolay.stankinschedule.journal.login.ui.components.LoginToolBar


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JournalLoginScreen(
    viewModel: JournalLoginViewModel,
    navigateToJournal: () -> Unit,
    modifier: Modifier = Modifier,
) {
    TrackCurrentScreen(screen = "JournalLoginScreen")

    val focusManager = LocalFocusManager.current
    val context = LocalContext.current

    val loginState by viewModel.loginState.collectAsState()
    LaunchedEffect(loginState) {
        if (loginState) {
            navigateToJournal()
        }
    }

    val isLogging by viewModel.isLogging.collectAsState()
    val loginError by viewModel.loginError.collectAsState()

    var login by rememberSaveable { mutableStateOf("") }
    var isLoginError by remember { mutableStateOf(false) }

    var password by rememberSaveable { mutableStateOf("") }
    var isPasswordError by remember { mutableStateOf(false) }

    val submitForm: () -> Unit = {
        isLoginError = login.isEmpty()
        isPasswordError = password.isEmpty()

        if (!isLoginError && !isPasswordError) {
            viewModel.login(login, password)
        }

        focusManager.clearFocus()
    }

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(
        state = rememberTopAppBarState()
    )

    Scaffold(
        topBar = {
            LoginToolBar(
                scrollBehavior = scrollBehavior
            )
        },
        contentWindowInsets = WindowInsets.Zero,
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            AnimatedVisibility(
                visible = isLogging,
                enter = slideInVertically(),
                exit = slideOutVertically()
            ) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp)
                    .verticalScroll(rememberScrollState())
                    .padding(Dimen.ContentPadding)
            ) {

                loginError?.let { error ->
                    LoginError(
                        error = error,
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.error,
                                shape = RoundedCornerShape(size = 4.dp)
                            )
                            .padding(16.dp)
                    )
                }

                LoginField(
                    login = login,
                    onLoginChanged = {
                        login = it
                        isLoginError = login.isEmpty()
                    },
                    isLogging = isLogging,
                    isLoginError = isLoginError,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = Dimen.ContentPadding)
                )

                PasswordField(
                    password = password,
                    onPasswordChanged = {
                        password = it
                        isPasswordError = password.isEmpty()
                    },
                    isLogging = isLogging,
                    isPasswordError = isPasswordError,
                    onFormSubmit = { submitForm() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = Dimen.ContentPadding)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextButton(
                        onClick = { BrowserUtils.openLink(context, Constants.MODULE_JOURNAL_URL) },
                        enabled = !isLogging
                    ) {
                        Text(text = stringResource(R.string.forget_password))
                    }

                    Button(
                        onClick = { submitForm() },
                        enabled = !isLogging && !(isLoginError || isPasswordError)
                    ) {
                        Text(text = stringResource(R.string.sign_in))
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginField(
    login: String,
    onLoginChanged: (login: String) -> Unit,
    isLogging: Boolean,
    isLoginError: Boolean,
    modifier: Modifier = Modifier,
) {
    OutlinedTextField(
        value = login,
        onValueChange = onLoginChanged,
        enabled = !isLogging,
        singleLine = true,
        isError = isLoginError,
        supportingText = {
            AnimatedVisibility(
                visible = isLoginError,
                enter = slideInVertically(),
                exit = slideOutVertically()
            ) {
                Text(
                    text = stringResource(R.string.field_is_empty),
                    color = MaterialTheme.colorScheme.error,
                )
            }
        },
        label = { Text(text = stringResource(R.string.login)) },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Next
        ),
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasswordField(
    password: String,
    onPasswordChanged: (password: String) -> Unit,
    isLogging: Boolean,
    isPasswordError: Boolean,
    onFormSubmit: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var passwordVisible by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = password,
        onValueChange = onPasswordChanged,
        enabled = !isLogging,
        singleLine = true,
        isError = isPasswordError,
        label = { Text(text = stringResource(R.string.password)) },
        supportingText = {
            AnimatedVisibility(
                visible = isPasswordError,
                enter = slideInVertically(),
                exit = slideOutVertically(),
            ) {
                Text(
                    text = stringResource(R.string.field_is_empty),
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(
            onDone = { onFormSubmit() }
        ),
        visualTransformation = if (passwordVisible) {
            VisualTransformation.None
        } else {
            PasswordVisualTransformation()
        },
        trailingIcon = {
            @DrawableRes
            val image = if (passwordVisible) {
                R.drawable.ic_password_visibility
            } else {
                R.drawable.ic_password_visibility_off
            }

            val description = if (passwordVisible) {
                stringResource(id = R.string.password_hide)
            } else {
                stringResource(id = R.string.password_show)
            }

            IconButton(
                onClick = {
                    passwordVisible = !passwordVisible
                },
                enabled = !isLogging,
            ) {
                Icon(
                    painter = painterResource(image),
                    contentDescription = description
                )
            }
        },
        modifier = modifier
    )
}