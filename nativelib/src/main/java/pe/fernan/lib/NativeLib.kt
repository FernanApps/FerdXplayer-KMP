package pe.fernan.lib

import android.annotation.SuppressLint
import java.io.File

class NativeLib {

    /**
     * A native method that is implemented by the 'lib' native library,
     * which is packaged with this application.
     */
    external fun stringFromJNI(): String

    companion object {
        // Used to load the 'lib' library on application startup.
        init {
            System.loadLibrary("lib")
        }
    }
}

@SuppressLint("UnsafeDynamicallyLoadedCode")
fun main() {
    val currentDirectory = File("")
    println("currentDirectory ${currentDirectory.absolutePath}")

    System.setProperty("java.library.path", currentDirectory.absolutePath)
    System.load(File(currentDirectory.absolutePath, "reseting.dll").absolutePath)


}