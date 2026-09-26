package com.kampplus.hava

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                AppNavigation()
            }
        }
    }
}

// -------------------------------------------------------------------------
// ADIM 2: NavHost Yapılandırması (Hedefler ve Parametre Tanımı)
// -------------------------------------------------------------------------
@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "main"
    ) {
        // 1. Ekran: Ana Ekran
        composable(route = "main") {
            MainScreen(
                onOpenScreen = { cityId ->
                    navController.navigate("detail/$cityId")
                }
            )
        }

        // 2. Ekran: Detay Ekranı (Parametreli Rota)
        composable(
            route = "detail/{cityId}",
            arguments = listOf(
                navArgument("cityId") {
                    type = NavType.StringType
                    nullable = true
                }
            )
        ) { backStackEntry ->
            val cityId = backStackEntry.arguments?.getString("cityId")
            DetailScreen(
                cityId = cityId,
                onBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}

// -------------------------------------------------------------------------
// ADIM 1 & 3: Ana Ekran (Bileşenleri ayırma ve Callback Sözleşmesi)
// -------------------------------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    onOpenScreen: (String) -> Unit, // Föydeki Geçiş Sözleşmesi
    modifier: Modifier = Modifier
) {
    val sehirler = listOf("İstanbul", "Ankara", "İzmir", "Bursa", "GecersizParametreTesti")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Şehirler (CP2 Ana Ekran)") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(sehirler) { sehir ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onOpenScreen(sehir) },
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (sehir == "GecersizParametreTesti") "Geçersiz Veri Senaryosu (Hata Testi)" else sehir,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "Detay >",
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------------------
// ADIM 1 & 4: Detay Ekranı (Parametre Kullanımı, Geri Dönüş ve Hata Ekranı)
// -------------------------------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    cityId: String?,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    // 4. Ölçüt için geçersiz parametre kontrolü
    val isInvalid = cityId.isNullOrBlank() || cityId == "GecersizParametreTesti"

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isInvalid) "Uyarı / Hata" else "Detay Ekranı") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Geri Dön"
                        )
                    }
                }
            )
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            if (isInvalid) {
                // Geçersiz parametre durumunda gösterilen açıklayıcı görünüm
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Geçersiz Parametre!",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "İletilen şehir bilgisi doğrulanamadı veya geçersiz bir parametre gönderildi.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = onBack,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Text("Geri Dön")
                        }
                    }
                }
            } else {
                // Aktarılan parametrenin doğru kullanıldığı görünüm
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Gelen Parametre:",
                            style = MaterialTheme.typography.labelMedium
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = cityId,
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "NavHost üzerinden başarıyla aktarıldı.",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}
