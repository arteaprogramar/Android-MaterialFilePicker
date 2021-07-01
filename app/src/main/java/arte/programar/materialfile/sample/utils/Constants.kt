package arte.programar.materialfile.sample.utils

import android.Manifest
import android.os.Build

object Constants {

    val PERMISSION_REQUEST = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> {
            arrayOf(
                Manifest.permission.MANAGE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        }
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN -> {
            arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        }
        else -> {
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
    }

}