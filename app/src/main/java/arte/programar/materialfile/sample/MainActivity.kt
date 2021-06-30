package arte.programar.materialfile.sample

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Button
import android.widget.Toast
import arte.programar.materialfile.MaterialFilePicker
import arte.programar.materialfile.ui.FilePickerActivity
import com.nbsp.materialfilepicker.sample.R
import java.io.File
import java.util.regex.Pattern

class MainActivity : ActivityPermission() {

    private val TAG: String = MainActivity::class.java.simpleName
    private val pickButton: Button by lazy { findViewById(R.id.pick_from_activity) }
    private val PERMISSION_REQUEST = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
        arrayOf(
                Manifest.permission.MANAGE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
        arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
    } else {
        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        pickButton.setOnClickListener {
            requestAppPermissions(PERMISSION_REQUEST,  R.string.app_name, PERMISSIONS_REQUEST_CODE)
        }
    }

    private fun openFilePicker() {
        val externalStorage = Environment.getExternalStorageDirectory()
        val alarmsFolder = File(externalStorage, ALARMS_EXTERNAL_STORAGE_FOLDER)

        MaterialFilePicker()
                // Pass a source of context. Can be:
                //    .withActivity(Activity activity)
                //    .withFragment(Fragment fragment)
                //    .withSupportFragment(androidx.fragment.app.Fragment fragment)
                .withActivity(this)
                // With cross icon on the right side of toolbar for closing picker straight away
                .withCloseMenu(true)
                // Entry point path (user will start from it)
                .withPath(alarmsFolder.absolutePath)
                // Root path (user won't be able to come higher than it)
                .withRootPath(externalStorage.absolutePath)
                // Showing hidden files
                .withHiddenFiles(true)
                // Want to choose only jpg images
                .withFilter(Pattern.compile(".*\\.(jpg|jpeg)$"))
                // Don't apply filter to directories names
                .withFilterDirectories(false)
                .withTitle("Sample title")
                .withRequestCode(FILE_PICKER_REQUEST_CODE)
                .start()
    }

    override fun onPermissionsGranted(requestCode: Int) {
        openFilePicker()
    }

    override fun onPermissionDenied(requestCode: Int) {
        Log.d(TAG, "onPermissionDenied")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == FILE_PICKER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            data ?: throw IllegalArgumentException("data must not be null")

            val path = data.getStringExtra(FilePickerActivity.RESULT_FILE_PATH)

            if (path != null) {
                Log.d("Path: ", path)
                Toast.makeText(this, "Picked file: $path", Toast.LENGTH_LONG).show()
            }
        }
    }

    companion object {
        private const val PERMISSIONS_REQUEST_CODE = 10000
        private const val FILE_PICKER_REQUEST_CODE = 9999

        private const val ALARMS_EXTERNAL_STORAGE_FOLDER = "Alarms"
    }
}