package zaragoza.mateo.ferpantallas

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.messaging.FirebaseMessaging
import zaragoza.mateo.ferpantallas.modelos.AppTheme
import zaragoza.mateo.ferpantallas.notificaciones.NotifService
import zaragoza.mateo.ferpantallas.ui.theme.FERPantallasTheme
import zaragoza.mateo.ferpantallas.viewmodels.SettingsViewModel
import zaragoza.mateo.ferpantallas.vistas.AppFrame

class MainActivity : ComponentActivity() {
    private var openPostDocumentId by mutableStateOf<String?>(null)

    @android.annotation.SuppressLint("InvalidFragmentVersionForActivityResult")
    private val notifPermissions =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        android.util.Log.d("FCM", "2. onCreate postId=${intent?.getStringExtra("postId")} restored=${savedInstanceState != null}")
        openPostDocumentId = intent?.getStringExtra("postId")
        intent?.removeExtra("postId")

        NotifService.createChannel(this)
        if (Build.VERSION.SDK_INT >= 33) {
            notifPermissions.launch(android.Manifest.permission.POST_NOTIFICATIONS)
        }
        FirebaseMessaging.getInstance().subscribeToTopic("posts")

        enableEdgeToEdge()
        setContent {
            val settingsViewModel: SettingsViewModel = viewModel()
            val dark = when (settingsViewModel.theme) {
                AppTheme.CLARO -> false
                AppTheme.OSCURO -> true
                AppTheme.SISTEMA -> isSystemInDarkTheme()
            }
            FERPantallasTheme(
                darkTheme = dark,
                textScale = settingsViewModel.textSize.scale,
            ) {
                AppFrame(
                    openPostDocumentId = openPostDocumentId,
                    onOpenPostHandled = { openPostDocumentId = null },
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        intent.getStringExtra("postId")?.let { openPostDocumentId = it }
        intent.removeExtra("postId")
        setIntent(intent)
    }
}