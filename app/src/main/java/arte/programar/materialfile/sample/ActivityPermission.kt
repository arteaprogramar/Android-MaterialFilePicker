package arte.programar.materialfile.sample

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

abstract class ActivityPermission : AppCompatActivity() {

    abstract fun onPermissionsGranted(requestCode: Int)

    abstract fun onPermissionDenied(requestCode: Int)

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        var permissionCheck: Int = PackageManager.PERMISSION_GRANTED

        grantResults.map {
            permissionCheck += it
        }

        if ((grantResults.isNotEmpty()) && permissionCheck == PackageManager.PERMISSION_GRANTED) {
            onPermissionsGranted(requestCode)
        } else {
            onPermissionDenied(PackageManager.PERMISSION_DENIED)
        }
    }

    fun requestAppPermissions(requestedPermissions: Array<String>, stringId: Int, requestCode: Int) {
        // Variables for permissions
        var permissionAll: Boolean = true
        val permissionDenied = ArrayList<String>()

        requestedPermissions.map {
            if (ContextCompat.checkSelfPermission(applicationContext, it) != PackageManager.PERMISSION_GRANTED) {
                permissionDenied.add(it)
                permissionAll = false
            }
        }

        // Check if exists ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            if (isPresent(requestedPermissions, Manifest.permission.MANAGE_EXTERNAL_STORAGE)){
                if (Environment.isExternalStorageManager()){
                    permissionAll = true
                } else {
                    permissionAll = false
                    requestForAllFilesPermission(requestCode)
                }
            }
        }

        if (permissionAll) {
            onPermissionsGranted(requestCode)
        } else {
            //requestPermissions(permissionDenied.toTypedArray(), requestCode)
            ActivityCompat.requestPermissions(this, permissionDenied.toTypedArray(), requestCode)
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun requestForAllFilesPermission(requestCode: Int){
        try {
            Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION).apply {
                addCategory("android.intent.category.DEFAULT")
                data = Uri.parse(String.format("package:%s", arrayOf<Any>(applicationContext.packageName)))
                startActivityForResult(this, requestCode)
            }
        } catch (ex: Exception){
            Intent().apply {
                action = Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
                startActivityForResult(this, requestCode)
            }
        }
    }

    private fun <T> isPresent(arr: Array<T>, target: T): Boolean {
        return arr.contains(target)
    }

}