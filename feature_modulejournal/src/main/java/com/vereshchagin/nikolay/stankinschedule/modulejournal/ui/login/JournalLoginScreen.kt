package com.vereshchagin.nikolay.stankinschedule.modulejournal.ui.login

import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.vereshchagin.nikolay.stankinschedule.core.ui.BrowserUtils
import com.vereshchagin.nikolay.stankinschedule.core.ui.theme.Dimen
import com.vereshchagin.nikolay.stankinschedule.modulejournal.R
import com.vereshchagin.nikolay.stankinschedule.modulejournal.ui.login.components.LoginError
import com.vereshchagin.nikolay.stankinschedule.modulejournal.util.Constants


@Composable
fun JournalLoginScreen(
    viewModel: JournalLoginViewModel,
    navigateToJournal: () -> Unit,
    modifier: Modifier = Modifier,
) {
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

    Box(
        modifier = modifier
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
                .padding(top = 4.dp)
                .padding(Dimen.ContentPadding)
        ) {

            if (loginError != null) {
                LoginError(
                    error = loginError,
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colors.error,
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

            AnimatedVisibility(visible = isLoginError) {
                Text(
                    text = stringResource(R.string.field_is_empty),
                    style = MaterialTheme.typography.caption,
                    color = MaterialTheme.colors.error,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }

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

            AnimatedVisibility(visible = isPasswordError) {
                Text(
                    text = stringResource(R.string.field_is_empty),
                    style = MaterialTheme.typography.caption,
                    color = MaterialTheme.colors.error,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }

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
        label = { Text(text = stringResource(R.string.login)) },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Next
        ),
        modifier = modifier
    )
}

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