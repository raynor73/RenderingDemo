package ilapin.renderingdemo

import android.os.StrictMode
import androidx.multidex.MultiDexApplication
import ilapin.common.android.log.L
import io.reactivex.plugins.RxJavaPlugins
import timber.log.Timber

/**
 * @author Игорь on 15.01.2020.
 */
class App : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()

        @Suppress("ConstantConditionIf")
        if (BuildConfig.DEVELOPER_MODE) {
            Timber.plant(Timber.DebugTree())

            StrictMode.setThreadPolicy(
                StrictMode.ThreadPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .build()
            )
            StrictMode.setVmPolicy(
                StrictMode.VmPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .build()
            )
        }

        RxJavaPlugins.setErrorHandler { t -> L.e(LOG_TAG, t, "Unhandled error detected") }
    }

    companion object {

        const val LOG_TAG = "RenderingDemo"
    }
}