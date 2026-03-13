package com.example.webviewapp

import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.*
import androidx.activity.ComponentActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen

class MainActivity : ComponentActivity() {

    private lateinit var webView: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        
        setupWebView()
        setContentView(webView)
        
        // Load the live site; the Service Worker on the site will handle the "Save for Offline"
        webView.loadUrl("https://chats88.pythonanywhere.com")
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setupWebView() {
        webView = WebView(this).apply {
            webViewClient = object : WebViewClient() {
                // Keep everything inside the app
                override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                    return false
                }
            }
            
            settings.apply {
                javaScriptEnabled = true
                domStorageEnabled = true // Required for local database (IndexedDB)
                databaseEnabled = true
                allowFileAccess = true
                cacheMode = WebSettings.LOAD_DEFAULT // Uses cache when offline automatically
            }

            // Enable Service Worker support
            val swController = ServiceWorkerController.getInstance()
            swController.setServiceWorkerClient(object : ServiceWorkerClient() {
                override fun shouldInterceptRequest(request: WebResourceRequest): WebResourceResponse? {
                    return null // Let the Service Worker handle it
                }
            })
        }
    }

    override fun onBackPressed() {
        if (webView.canGoBack()) webView.goBack() else super.onBackPressed()
    }
}
