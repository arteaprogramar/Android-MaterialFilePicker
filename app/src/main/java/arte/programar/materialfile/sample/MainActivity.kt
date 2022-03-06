package arte.programar.materialfile.sample

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import arte.programar.materialfile.MaterialFilePicker
import arte.programar.materialfile.sample.utils.Constants
import arte.programar.materialfile.sample.utils.PermissionActivity
import arte.programar.materialfile.ui.FilePickerActivity
import arte.programar.materialfile.utils.FileUtils
import java.util.regex.Pattern

class MainActivity : PermissionActivity() {

    private val TAG: String = MainActivity::class.java.simpleName

    private val pickButton: Button by lazy { findViewById(R.id.pick_from_activity) }

    private val startForResultFiles = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        onActivityResult(result.resultCode, result.data)
    }

    private val startForResultDocumentTree = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        Log.d(TAG, result.data.toString())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        pickButton.setOnClickListener {
            requestAppPermissions(Constants.PERMISSION_REQUEST)
        }
    }

    private fun openFilePicker() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            //startForResultDocumentTree.launch(intent)
        }

        val externalStorage = FileUtils.getFile(applicationContext, null)

        MaterialFilePicker()
            // Pass a source of context. Can be:
            //    .withActivity(Activity activity)
            //    .withFragment(Fragment fragment)
            //    .withSupportFragment(androidx.fragment.app.Fragment fragment)
            .withActivity(this)
            // With cross icon on the right side of toolbar for closing picker straight away
            .withCloseMenu(true)
            // Entry point path (user will start from it)
            //.withPath(alarmsFolder.absolutePath)
            // Root path (user won't be able to come higher than it)
            .withRootPath(externalStorage.absolutePath)
            // Showing hidden files
            .withHiddenFiles(true)
            // Want to choose only jpg images
            .withFilter(Pattern.compile(".*\\.(jpg|jpeg)$"))
            // Don't apply filter to directories names
            .withFilterDirectories(false)
            .withTitle("Sample title")
            .withActivityResultApi(startForResultFiles)
            .start()
    }

    override fun onPermissionsGranted() {
        openFilePicker()
    }

    override fun onPermissionsDenied(permissions: Array<String>) {
        Log.d(TAG, "onPermissionDenied")
    }

    private fun onActivityResult(resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            val path: String? = data?.getStringExtra(FilePickerActivity.RESULT_FILE_PATH)

            if (path != null) {
                Log.d("Path: ", path)
                Toast.makeText(this, "Picked file: $path", Toast.LENGTH_LONG).show()
            }
        }
    }
}