package com.ikbalghozali.storyapp.ui.story

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.ikbalghozali.storyapp.R
import com.ikbalghozali.storyapp.data.local.User
import com.ikbalghozali.storyapp.databinding.ActivityPostStoryBinding
import com.ikbalghozali.storyapp.utils.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class PostStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPostStoryBinding
    private lateinit var currentPhotoPath: String
    private lateinit var user: User
    private var getFile: File? = null
    private val viewModel by viewModels<PostStoryViewModel>()

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                Toast.makeText(
                    this,
                    getString(R.string.permission),
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.apply {
            title = getString(R.string.post_story_activity)
            elevation = 0f
            setDisplayHomeAsUpEnabled(true)
        }
        user = intent.getParcelableExtra(EXTRA_USER)!!
        getPermission()
        binding.btnCamera.setOnClickListener { startTakePhoto() }
        binding.btnGallery.setOnClickListener { startGallery() }
        binding.btnUpload.setOnClickListener { uploadImage() }
        showLoading()
    }

    private fun getPermission() {
        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        finish()
        return true
    }

    private fun startTakePhoto() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(packageManager)
        createTempFile(application).also {
            val photoURI: Uri = FileProvider.getUriForFile(
                this@PostStoryActivity,
                "com.ikbalghozali.storyapp",
                it
            )
            currentPhotoPath = it.absolutePath
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            launcherIntentCamera.launch(intent)
        }
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            val myFile = File(currentPhotoPath)
            getFile = myFile
            val result = BitmapFactory.decodeFile(getFile?.path)
            binding.previewPhoto.setImageBitmap(result)
        }
    }


    private fun startGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Choose a Picture")
        launcherIntentGallery.launch(chooser)
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            val selectedImg: Uri = it.data?.data as Uri
            val myFile = uriToFile(selectedImg, this@PostStoryActivity)
            getFile = myFile
            binding.previewPhoto.setImageURI(selectedImg)
        }
    }

    private fun uploadImage() {
        when {
            getFile == null -> {
                Toast.makeText(this@PostStoryActivity,
                    getString(R.string.no_attached),
                    Toast.LENGTH_SHORT).show()
            }
            binding.etDescription.text.toString().isEmpty() -> {
                Toast.makeText(this@PostStoryActivity, "Please fill desc", Toast.LENGTH_SHORT)
                    .show()
            }

            else -> {
                val file = reduceFileImage(getFile as File)
                val descriptionText = binding.etDescription.text.toString()
                val description = descriptionText.toRequestBody("text/plain".toMediaType())
                val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                val imageMultipart = MultipartBody.Part.createFormData(
                    "photo",
                    file.name,
                    requestImageFile
                )

                viewModel.uploadImage(user,
                    description,
                    imageMultipart,
                    object : ApiCallbackString {
                        override fun onResponse(success: Boolean, message: String) {
                            if (success) {
                                Toast.makeText(this@PostStoryActivity,
                                    getString(R.string.upload_success),
                                    Toast.LENGTH_SHORT)
                                    .show()
                                finish()

                            } else {
                                Toast.makeText(this@PostStoryActivity,
                                    getString(R.string.upload_failed) + message,
                                    Toast.LENGTH_SHORT)
                                    .show()
                                binding.progressBar.visibility = View.GONE
                            }
                        }
                    })
            }
        }
    }

    private fun showLoading() {
        viewModel.isLoading.observe(this) {
            binding.apply {
                if (it) progressBar.visibility = View.VISIBLE
                else progressBar.visibility = View.GONE
            }
        }
    }


    companion object {
        const val EXTRA_USER = "user"
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }

}