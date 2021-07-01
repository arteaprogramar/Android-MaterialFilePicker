package arte.programar.materialfile.sample.utils

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

abstract class PermissionActivity : AppCompatActivity() {

    abstract fun onPermissionsGranted()

    abstract fun onPermissionsDenied(permissions: Array<String>)

    private val startActivityForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {}

    private val requestPermissionsLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        if (permissions.size == permissions.filterValues { it }.size) {
            onPermissionsGranted()
        } else {
            onPermissionsDenied(permissions.filterValues { !it }.keys.toTypedArray())
        }
    }

    fun checkAllPermissionGranted(requestedPermissions: Array<String>): Boolean {
        var granted = requestedPermissions.filter { it != Manifest.permission.MANAGE_EXTERNAL_STORAGE }
            .takeWhile {
                ContextCompat.checkSelfPermission(applicationContext, it) == PackageManager.PERMISSION_GRANTED
            }.size

        if (isPresent(requestedPermissions, Manifest.permission.MANAGE_EXTERNAL_STORAGE).isNotEmpty()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {
                    granted += 1
                }
            }
        }

        return granted == requestedPermissions.size
    }

    fun requestAppPermissions(requestedPermissions: Array<String>) {
        val permissionDenied = ArrayList<String>()
        var permissionAll = true

        requestedPermissions.map {
            if (ContextCompat.checkSelfPermission(applicationContext, it) != PackageManager.PERMISSION_GRANTED) {
                permissionDenied.add(it)
                permissionAll = false
            }
        }

        // Check if exists ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (isPresent(requestedPermissions, Manifest.permission.MANAGE_EXTERNAL_STORAGE).isNotEmpty()) {
                if (Environment.isExternalStorageManager()) {
                    permissionAll = true
                } else {
                    requestForAllFilesPermission()
                    permissionAll = Environment.isExternalStorageManager()

                    if (!permissionAll) {
                        permissionDenied.add(Manifest.permission.MANAGE_EXTERNAL_STORAGE)
                    }
                }
            }
        }

        if (permissionAll) {
            onPermissionsGranted()
        } else {
            requestPermissionsLauncher.launch(permissionDenied.toTypedArray())
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun showRequestPermissionRationale(permission: String) {
        if (super.shouldShowRequestPermissionRationale(permission)) {
            requestPermissionsLauncher.launch(arrayOf(permission))
        } else {
            Intent().apply {
                action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                data = Uri.parse(String.format("package:%s", applicationContext.packageName))
                startActivityForResult.launch(this)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun requestForAllFilesPermission() {
        try {
            Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION).apply {
                addCategory(Intent.CATEGORY_DEFAULT)
                data = Uri.parse(String.format("package:%s", applicationContext.packageName))
                startActivityForResult.launch(this)
            }
        } catch (ex: Exception) {
            Intent().apply {
                action = Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
                startActivityForResult.launch(this)
            }
        }
    }

    /**
     * Search item on array
     */
    private fun <T> isPresent(arr: Array<T>, target: T): List<T> {
        return arr.filter { it == target }
    }

}