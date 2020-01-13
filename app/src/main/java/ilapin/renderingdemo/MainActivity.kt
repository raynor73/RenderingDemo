package ilapin.renderingdemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import ilapin.common.Optional

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val a = Optional.empty<String>()
        if (a.isEmpty()) {
            Log.d("!@#", "Empty")
        }
    }
}
