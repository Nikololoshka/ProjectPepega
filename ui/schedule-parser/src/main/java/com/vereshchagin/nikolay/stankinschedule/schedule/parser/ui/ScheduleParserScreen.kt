package com.vereshchagin.nikolay.stankinschedule.schedule.parser.ui

import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.vereshchagin.nikolay.stankinschedule.core.ui.components.AppScaffold
import com.vereshchagin.nikolay.stankinschedule.core.ui.theme.Dimen
import com.vereshchagin.nikolay.stankinschedule.schedule.parser.ui.components.ParserState
import com.vereshchagin.nikolay.stankinschedule.schedule.parser.ui.components.ProgressStepper
import com.vereshchagin.nikolay.stankinschedule.schedule.parser.ui.components.ReadPermissionDeniedDialog
import com.vereshchagin.nikolay.stankinschedule.schedule.parser.ui.components.ScheduleParserAppBar
import kotlinx.coroutines.flow.collectLatest


@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun ScheduleParserScreen(
    viewModel: ScheduleParserViewModel,
    onBackPressed: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    val parserState by viewModel.parserState.collectAsState()

    AppScaffold(
        topBar = {
            ScheduleParserAppBar(
                onBackPressed = onBackPressed,
                scrollBehavior = scrollBehavior
            )
        },
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(Dimen.ContentPadding)
                .fillMaxSize()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                ProgressStepper(
                    step = 1,
                    count = 4,
                    modifier = Modifier
                        .height(32.dp)
                        .aspectRatio(1f)
                )
                Column(
                    verticalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = "Select file",
                        style = MaterialTheme.typography.titleMedium,
                    )
                    Text(
                        text = "Next: parse schedule",
                        style = MaterialTheme.typography.labelSmall,
                    )
                }
            }

            Column(
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                var readDeniedDialog by remember { mutableStateOf(false) }
                if (readDeniedDialog) {
                    ReadPermissionDeniedDialog(
                        onDismiss = { readDeniedDialog = false }
                    )
                }

                val openScheduleLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.OpenDocument(),
                    onResult = {
                        if (it != null) {
                            viewModel.selectFile(it)
                        }
                    }
                )

                val readStoragePermission = rememberPermissionState(
                    permission = android.Manifest.permission.READ_EXTERNAL_STORAGE,
                    onPermissionResult = { isGranted ->
                        if (isGranted) {
                            openScheduleLauncher.launch(arrayOf("application/pdf"))
                        } else {
                            readDeniedDialog = true
                        }
                    }
                )

                Text(
                    text = "Select PDF file with schedule from device",
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = Dimen.ContentPadding)
                )

                val interactionSource = remember { MutableInteractionSource() }
                LaunchedEffect(interactionSource) {
                    interactionSource.interactions.collectLatest {
                        if (it is PressInteraction.Release) {
                            if (readStoragePermission.status.isGranted || Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                openScheduleLauncher.launch(arrayOf("application/pdf"))
                            } else {
                                readStoragePermission.launchPermissionRequest()
                            }
                        }
                    }
                }

                OutlinedTextField(
                    value = (parserState as ParserState.SelectFile?)?.data?.name ?: "",
                    onValueChange = {},
                    readOnly = true,
                    singleLine = true,
                    placeholder = { Text(text = "Filename") },
                    trailingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_upload_file),
                            contentDescription = null
                        )
                    },
                    interactionSource = interactionSource,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedButton(
                    onClick = viewModel::back,
                    enabled = parserState !is ParserState.SelectFile
                ) {
                    Text(text = stringResource(R.string.step_back))
                }

                Button(
                    enabled = (parserState as ParserState.SelectFile?)?.data?.uri != null,
                    onClick = viewModel::next,
                ) {
                    Text(text = stringResource(R.string.step_next))
                }
            }
        }
    }
}