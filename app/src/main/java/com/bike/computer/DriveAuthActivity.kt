package com.bike.computer

import android.app.Activity
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast

/** WebView-based Google OAuth consent flow; exchanges the returned code for Drive tokens. */
class DriveAuthActivity : Activity() {
    private val redirect = "http://127.0.0.1:8080"

    override fun onCreate(s: Bundle?) {
        super.onCreate(s)
        val cid = Prefs.driveClientId(this)
        if (cid.isEmpty()) {
            toast("Enter Client ID & Secret first")
            finish()
            return
        }
        val web = WebView(this)
        web.setBackgroundColor(Color.BLACK)
        web.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            userAgentString = "Mozilla/5.0 (X11; Linux x86_64; rv:128.0) Gecko/20100101 Firefox/128.0"
        }
        web.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(v: WebView, r: WebResourceRequest): Boolean =
                handle(r.url.toString())

            @Deprecated("older devices")
            override fun shouldOverrideUrlLoading(v: WebView, u: String): Boolean = handle(u)
        }
        setContentView(web)
        web.loadUrl(GoogleDriveClient.authorizeUrl(cid, redirect))
    }

    private fun handle(url: String): Boolean {
        if (!url.startsWith(redirect)) return false
        val uri = Uri.parse(url)
        val code = uri.getQueryParameter("code")
        val err = uri.getQueryParameter("error")
        if (code == null) {
            toast("Authorization ${err ?: "cancelled"}")
            finish()
            return true
        }
        Thread {
            val failure = try {
                GoogleDriveClient.exchangeCode(this, code, redirect)
                null
            } catch (t: Throwable) {
                t
            }
            runOnUiThread {
                if (failure == null) {
                    toast("Connected to Google Drive")
                    setResult(-1)
                    finish()
                } else {
                    toast("Connect failed: ${failure.message}")
                    finish()
                }
            }
        }.start()
        return true
    }

    private fun toast(m: String) {
        Toast.makeText(this, m, 1).show()
    }
}
