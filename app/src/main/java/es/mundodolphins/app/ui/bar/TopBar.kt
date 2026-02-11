package es.mundodolphins.app.ui.bar

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import es.mundodolphins.app.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar(
    onMenuClick: () -> Unit = {},
) {
    TopAppBar(
        colors =
            TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                titleContentColor = MaterialTheme.colorScheme.secondary,
            ),
        navigationIcon = {
            IconButton(onClick = onMenuClick) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = stringResource(R.string.open_navigation_menu),
                )
            }
        },
        title = {
            Row {
                Image(
                    painter = painterResource(id = R.drawable.mundo_dolphins_small),
                    contentDescription = stringResource(id = R.string.logo_description),
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxWidth(0.1f),
                )
                Text(
                    text = stringResource(id = R.string.app_name),
                    modifier =
                        Modifier
                            .fillMaxWidth(0.9f)
                            .padding(start = 5.dp),
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    textAlign = TextAlign.Center,
                )
            }
        },
    )
}
