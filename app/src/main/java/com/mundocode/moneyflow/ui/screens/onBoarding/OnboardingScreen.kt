package com.mundocode.moneyflow.ui.screens.onBoarding

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.foundation.pager.PagerState
import androidx.compose.ui.res.stringResource
import com.mundocode.moneyflow.R

data class OnboardingPage(val icon: String, val title: String, val description: String)

@Composable
fun OnboardingScreen(
    navController: NavHostController,
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    val pages = listOf(
        OnboardingPage(
            icon = "👋",
            title = stringResource(R.string.onboarding_title_1),
            description = stringResource(R.string.onboarding_description_1)
        ),
        OnboardingPage(
            icon = "🎯",
            title = stringResource(R.string.onboarding_title_2),
            description = stringResource(R.string.onboarding_description_2)
        ),
        OnboardingPage(
            icon = "💰",
            title = stringResource(R.string.onboarding_title_3),
            description = stringResource(R.string.onboarding_description_3)
        ),
        OnboardingPage(
            icon = "📊",
            title = stringResource(R.string.onboarding_title_4),
            description = stringResource(R.string.onboarding_description_4)
        ),
        OnboardingPage(
            icon = "🛡️",
            title = stringResource(R.string.onboarding_title_5),
            description = stringResource(R.string.onboarding_description_5)
        ),
        OnboardingPage(
            icon = "🏠",
            title = stringResource(R.string.onboarding_title_6),
            description = stringResource(R.string.onboarding_description_6)
        ),
        OnboardingPage(
            icon = "🔄",
            title = stringResource(R.string.onboarding_title_7),
            description = stringResource(R.string.onboarding_description_7)
        ),
        OnboardingPage(
            icon = "📊",
            title = stringResource(R.string.onboarding_title_8),
            description = stringResource(R.string.onboarding_description_8)
        ),
        OnboardingPage(
            icon = "📈",
            title = stringResource(R.string.onboarding_title_9),
            description = stringResource(R.string.onboarding_description_9)
        ),
        OnboardingPage(
            icon = "🎯",
            title = stringResource(R.string.onboarding_title_10),
            description = stringResource(R.string.onboarding_description_10)
        ),
        OnboardingPage(
            icon = "⚙️",
            title = stringResource(R.string.onboarding_title_11),
            description = stringResource(R.string.onboarding_description_11)
        ),
        OnboardingPage(
            icon = "🔑",
            title = stringResource(R.string.onboarding_title_12),
            description = stringResource(R.string.onboarding_description_12)
        ),
        OnboardingPage(
            icon = "📝",
            title = stringResource(R.string.onboarding_title_13),
            description = stringResource(R.string.onboarding_description_13)
        ),
        OnboardingPage(
            icon = "🚀",
            title = stringResource(R.string.onboarding_title_14),
            description = stringResource(R.string.onboarding_description_14)
        )
    )

    val pagerState = rememberPagerState(
        pageCount = { pages.size },
    )
    val scope = rememberCoroutineScope()

    Scaffold {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) { page ->
                val item = pages[page]
                Column(
                    modifier = Modifier
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(item.icon, fontSize = 60.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(item.title, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        item.description,
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            PagerState(
                currentPage = pagerState.currentPage,
                currentPageOffsetFraction = pagerState.currentPageOffsetFraction,
                pageCount = { pagerState.pageCount }
            )

//            HorizontalPagerIndicator(
//                pagerState = pagerState,
//                modifier = Modifier.padding(16.dp)
//            )

            val loading = remember { mutableStateOf(false) }

            if (loading.value) {
                CircularProgressIndicator()
            }

            Button(
                onClick = {
                    if (pagerState.currentPage == pages.size - 1) {
                        scope.launch {
                            viewModel.markCompletedAsync()
                            delay(300)
                            navController.navigate("home") {
                                popUpTo("onboarding") { inclusive = true }
                            }
                        }
                    } else {
                        scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text(text = if (pagerState.currentPage == pages.size - 1) "Comenzar" else "Siguiente")
            }
        }
    }

}
