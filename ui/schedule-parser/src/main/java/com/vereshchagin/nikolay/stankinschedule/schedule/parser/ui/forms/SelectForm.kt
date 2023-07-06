package com.vereshchagin.nikolay.stankinschedule.schedule.parser.ui.forms

import android.Manifest
import android.content.res.Configuration
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.vereshchagin.nikolay.stankinschedule.core.ui.theme.Dimen
import com.vereshchagin.nikolay.stankinschedule.schedule.parser.ui.R
import com.vereshchagin.nikolay.stankinschedule.schedule.parser.ui.components.ReadPermissionDeniedDialog
import com.vereshchagin.nikolay.stankinschedule.schedule.parser.ui.model.ParserState


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun SelectForm(
    state: ParserState.SelectFile,
    selectFile: (uri: Uri) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
    ) {
        var readDeniedDialog by remember { mutableStateOf(false) }
        if (readDeniedDialog) {
            ReadPermissionDeniedDialog(
                onDismiss = { readDeniedDialog = false }
            )
        }

        val openScheduleLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.OpenDocument(),
            onResult = { if (it != null) selectFile(it) }
        )

        val readStoragePermission = rememberPermissionState(
            permission = Manifest.permission.READ_EXTERNAL_STORAGE,
            onPermissionResult = { isGranted ->
                if (isGranted) {
                    openScheduleLauncher.launch(arrayOf("application/pdf"))
                } else {
                    readDeniedDialog = true
                }
            }
        )

        val onSelectFile: () -> Unit = {
            if (readStoragePermission.isGrantedCompat) {
                openScheduleLauncher.launch(arrayOf("application/pdf"))
            } else {
                readStoragePermission.launchPermissionRequest()
            }
        }

        val configuration = LocalConfiguration.current
        val isLandscape by remember {
            derivedStateOf { configuration.orientation == Configuration.ORIENTATION_LANDSCAPE }
        }

        if (state.preview != null) {
            val preview by remember(state.preview) {
                derivedStateOf { state.preview.asImageBitmap() }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth(fraction = if (isLandscape) 0.5f else 1f)
                    .aspectRatio(ratio = 1.41f, matchHeightConstraintsFirst = !isLandscape)
                    .border(width = 1.dp, color = MaterialTheme.colorScheme.onBackground)
                    .clickable(onClick = onSelectFile)
            ) {
                Image(
                    bitmap = preview,
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxSize()
                )

                Icon(
                    painter = painterResource(R.drawable.ic_upload_file),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier
                        .size(36.dp)
                        .align(Alignment.TopEnd)
                        .background(
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(bottomStartPercent = 50)
                        )
                        .padding(8.dp)
                )
            }

        } else {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth(fraction = if (isLandscape) 0.5f else 1f)
                    .aspectRatio(ratio = 1.41f, matchHeightConstraintsFirst = !isLandscape)
                    .border(width = 1.dp, color = MaterialTheme.colorScheme.onBackground)
                    .clickable(onClick = onSelectFile)
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_upload_file),
                    contentDescription = null,
                    modifier = Modifier.size(48.dp)
                )

                Text(
                    text = stringResource(R.string.select_pdf_file),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = Dimen.ContentPadding)
                )
            }
        }

        Text(
            text = if (state.file != null) {
                stringResource(R.string.selected_filename, state.file.filename)
            } else {
                stringResource(R.string.file_not_selected)
            },
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = Dimen.ContentPadding)
        )
    }
}

@OptIn(ExperimentalPermissionsApi::class)
val PermissionState.isGrantedCompat: Boolean
    get() =
        this.status.isGranted || Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU