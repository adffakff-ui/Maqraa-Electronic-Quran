package com.example.ui.security

import android.content.Context
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity

object BiometricHelper {

    fun canAuthenticate(context: Context): Int {
        val biometricManager = BiometricManager.from(context)
        return biometricManager.canAuthenticate(
            BiometricManager.Authenticators.BIOMETRIC_STRONG or
                    BiometricManager.Authenticators.DEVICE_CREDENTIAL
        )
    }

    fun showBiometricPrompt(
        context: Context,
        title: String = "تسجيل الدخول بالبصمة",
        subtitle: String = "استخدم بصمة الإصبع أو قفل الجهاز للمتابعة",
        negativeButtonText: String = "إلغاء",
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val activity = context as? FragmentActivity
        if (activity == null) {
            onError("سياق النشاط غير مدعوم للبصمة")
            return
        }

        val executor = ContextCompat.getMainExecutor(context)
        val biometricPrompt = BiometricPrompt(
            activity,
            executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    onSuccess()
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    onError(errString.toString())
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    onError("فشلت المطابقة، يجدر إعادة المحاولة")
                }
            }
        )

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(title)
            .setSubtitle(subtitle)
            .setAllowedAuthenticators(
                BiometricManager.Authenticators.BIOMETRIC_STRONG or
                        BiometricManager.Authenticators.DEVICE_CREDENTIAL
            )
            .build()

        try {
            biometricPrompt.authenticate(promptInfo)
        } catch (e: Exception) {
            onError(e.localizedMessage ?: "خطأ في تشغيل مستشعر البصمة")
        }
    }
}
