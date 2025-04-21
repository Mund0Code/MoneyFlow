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

data class OnboardingPage(val icon: String, val title: String, val description: String)

@Composable
fun OnboardingScreen(
    navController: NavHostController,
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    val pages = listOf(
        OnboardingPage("📱", "Bienvenido", "Tu app financiera para organizar tus ingresos y gastos."),
        OnboardingPage("💰", "Controla tus gastos", "Categoriza, analiza y predice tus finanzas."),
        OnboardingPage("📊", "Toma decisiones", "Basado en tus datos reales.")
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
