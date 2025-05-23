package com.rudderstack.android.sample.kotlin

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.common.GooglePlayServicesRepairableException
import com.google.android.gms.security.ProviderInstaller
import com.rudderstack.android.sdk.core.RudderClient
import com.rudderstack.android.sdk.core.RudderOption
import com.rudderstack.android.sdk.core.RudderProperty
import com.rudderstack.android.sdk.core.RudderTraits
import javax.net.ssl.SSLContext


class MainActivity : AppCompatActivity() {
    private var count = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT)
            tlsBackport()
        val option = RudderOption()
            .putExternalId("brazeExternalId", "some_external_id_1")
            .putExternalId("braze_id", "some_braze_id_2")
            .putIntegration("GA", true).putIntegration("Amplitude", true)
            .putCustomContext(
                "customContext", mapOf(
                    "version" to "1.0.0",
                    "language" to "kotlin"
                )
            )
        MainApplication.rudderClient!!.identify(
            "userId",
            RudderTraits().putFirstName("Test First Name"),
            option
        )
        MainApplication.rudderClient!!.reset()
        val props = RudderProperty()
        props.put("Name", "John")
        props.put("city", "NYC")
        MainApplication.rudderClient!!.track("test event john", props, option)

        MainApplication.rudderClient!!.track("Test Event")

        MainApplication.rudderClient!!.onIntegrationReady(
            "App Center",
            NativeCallBack("App Center")
        );

        MainApplication.rudderClient!!.onIntegrationReady(
            "Custom Factory",
            NativeCallBack("Custom Factory")
        );
    }

    private fun tlsBackport() {
        try {
            ProviderInstaller.installIfNeeded(this)
            Log.e("Rudder", "Play present")
            val sslContext: SSLContext = SSLContext.getInstance("TLSv1.2")
            sslContext.init(null, null, null)
            sslContext.createSSLEngine()
        } catch (e: GooglePlayServicesRepairableException) {
            // Prompt the user to install/update/enable Google Play services.
            GoogleApiAvailability.getInstance()
                .showErrorNotification(this, e.connectionStatusCode)
            Log.e("Rudder", "Play install")
        } catch (e: GooglePlayServicesNotAvailableException) {
            // Indicates a non-recoverable error: let the user know.
            Log.e("SecurityException", "Google Play Services not available.");
            e.printStackTrace()
        }
    }
}

internal class NativeCallBack(private val integrationName: String) : RudderClient.Callback {
    override fun onReady(instance: Any) {
        println("Call back of integration : " + integrationName + " is called");
    }
}