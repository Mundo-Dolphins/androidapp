package es.mundodolphins.app.ui.views.seasons

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import es.mundodolphins.app.R
import es.mundodolphins.app.ui.Routes
import es.mundodolphins.app.ui.theme.MundoDolphinsTheme
import es.mundodolphins.app.ui.theme.TealBorder
import es.mundodolphins.app.ui.theme.TealDeep
import es.mundodolphins.app.ui.theme.TealPale
import es.mundodolphins.app.ui.theme.TextMid
import es.mundodolphins.app.ui.theme.orange
import es.mundodolphins.app.viewmodel.EpisodesViewModel

@Composable
fun SeasonsListScreen(
    modifier: Modifier = Modifier,
    model: EpisodesViewModel = viewModel(),
    navController: NavController,
) {
    SeasonsList(
        seasons = model.seasons.collectAsState(initial = emptyList()).value,
        modifier = modifier,
        navController = navController,
    )
}

@Composable
fun SeasonsList(
    seasons: List<Int>,
    modifier: Modifier = Modifier,
    navController: NavController,
) {
    val listState = rememberLazyListState()

    LazyColumn(
        state = listState,
        modifier =
            modifier
                .fillMaxWidth()
                .background(color = colorScheme.background),
        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        items(seasons) {
            SeasonRow(it, navController)
        }
    }
}

@Composable
fun SeasonRow(
    seasonId: Int,
    navController: NavController,
) {
    if (seasonId > 0) {
        Card(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .heightIn(min = 78.dp)
                    .clickable { navController.navigate(Routes.SeasonView.route + "/$seasonId") },
            shape = RoundedCornerShape(8.dp),
            border = BorderStroke(1.dp, TealBorder),
            colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        ) {
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .background(TealPale.copy(alpha = 0.42f))
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Column {
                    Text(
                        text = stringResource(R.string.season, seasonId),
                        color = TealDeep,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 22.sp,
                        letterSpacing = 0.sp,
                    )
                    Text(
                        text = stringResource(R.string.episodios),
                        color = TextMid,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(top = 4.dp),
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier =
                            Modifier
                                .padding(end = 8.dp)
                                .size(width = 18.dp, height = 3.dp)
                                .background(orange, RoundedCornerShape(8.dp)),
                    )
                    Text(
                        text = "Ver →",
                        color = TealDeep,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun SeasonsListPreview() {
    MundoDolphinsTheme {
        SeasonsList(
            listOf(8, 7, 6, 5, 4, 3, 2, 1),
            navController = NavController(LocalContext.current),
        )
    }
}
