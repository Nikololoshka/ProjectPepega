package com.vereshchagin.nikolay.stankinschedule.modulejournal.ui.screen

import androidx.compose.animation.*
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.vereshchagin.nikolay.stankinschedule.core.ui.BrowserUtils
import com.vereshchagin.nikolay.stankinschedule.core.ui.theme.Dimen
import com.vereshchagin.nikolay.stankinschedule.modulejournal.R
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
                Row(
                    horizontalArrangement = Arrangement.spacedBy(Dimen.ContentPadding),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colors.error,
                            shape = RoundedCornerShape(size = 4.dp)
                        )
                        .padding(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Error,
                        contentDescription = null,
                        tint = MaterialTheme.colors.error,
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )
                    Text(
                        text = loginError ?: "Unknown exception",
                        color = MaterialTheme.colors.error,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .weight(1f)
                    )
                }
            }

            OutlinedTextField(
                value = login,
                onValueChange = {
                    login = it
                    isLoginError = login.isEmpty()
                },
                enabled = !isLogging,
                singleLine = true,
                isError = isLoginError,
                label = { Text(text = stringResource(R.string.login)) },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
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

            var passwordVisible by remember { mutableStateOf(false) }

            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    isPasswordError = password.isEmpty()
                },
                enabled = !isLogging,
                singleLine = true,
                isError = isPasswordError,
                label = { Text(text = stringResource(R.string.password)) },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = { submitForm() }
                ),
                visualTransformation = if (passwordVisible) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation()
                },
                trailingIcon = {
                    val image = if (passwordVisible) {
                        Icons.Filled.Visibility
                    } else {
                        Icons.Filled.VisibilityOff
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
                        Icon(imageVector = image, description)
                    }
                },
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