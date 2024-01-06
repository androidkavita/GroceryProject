package com.easym.vegie.activities

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.*
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.*
import androidx.core.content.FileProvider
import com.easym.vegie.R
import com.easym.vegie.Utils.Utility
import com.easym.vegie.model.UserModel
import com.easym.vegie.retrofit.CommonDataResponse
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.mikhaellopez.circularimageview.CircularImageView
import com.squareup.picasso.Picasso
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import rx.android.BuildConfig
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.io.*
import java.net.ConnectException
import java.util.*

/**
 * Created by Arti on 29th Sept 2020.
 */
class EditProfileActivity : BaseActivity(), View.OnClickListener {

    lateinit var backLayout: LinearLayout
    private var imageProfile: CircularImageView? = null
    lateinit var edtName: EditText
    lateinit var edtEmailId: EditText
    lateinit var edtMobileNo: EditText
    lateinit var btnSaveChanges: Button
    private val userId = ""
    lateinit var showProfile: CircularImageView
    lateinit var defaultProfile: CircularImageView
    lateinit var profileLayout: LinearLayout
    private var context: Context? = null
    private val GALLERY = 1
    private val CAMERA = 2
    private var userName: String? = ""
    private var userEmailId: String? = ""
    private var userProfilePic: String? = ""
    private var userMobileNo: String? = ""
    private var Imagepath: String? = ""
    lateinit var toolbarTxt: TextView
    private var requestFile: RequestBody? = null
    private var body: MultipartBody.Part? = null
    var pic_id = 100
    var profilePicUri: Uri? = null
    var REQUEST_IMAGE_CAPTURE = 251
    var REQUEST_GALLERY_IMAGE = 252
    var imageName = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        context = this
        backLayout = findViewById(R.id.back_layout)
        imageProfile = findViewById(R.id.circular_image)
        edtName = findViewById(R.id.edt_name)
        edtEmailId = findViewById(R.id.edt_email_id)
        edtMobileNo = findViewById(R.id.edt_phone_no)
        btnSaveChanges = findViewById(R.id.btn_save_changes)
        backLayout = findViewById(R.id.back_layout)
        showProfile = findViewById(R.id.show_profile)
        defaultProfile = findViewById(R.id.default_profile)
        profileLayout = findViewById(R.id.profile_layout)
        toolbarTxt = findViewById(R.id.txt_back)
        toolbarTxt.setText("Back")
        userName = intent.getStringExtra("userName")
        userEmailId = intent.getStringExtra("userEmail")
        userMobileNo = intent.getStringExtra("userMobileNo")
        edtName.setText(userName)
        edtEmailId.setText(userEmailId)
        edtMobileNo.setText(userMobileNo)
        if (!TextUtils.isEmpty(intent.getStringExtra("userProfilePic"))) {
            userProfilePic = intent.getStringExtra("userProfilePic")
            showProfile.setVisibility(View.VISIBLE)
            defaultProfile.setVisibility(View.GONE)
            Picasso.get().load(userProfilePic).into(showProfile)
        } else {
            showProfile.setVisibility(View.GONE)
            defaultProfile.setVisibility(View.VISIBLE)
            defaultProfile.setImageResource(R.drawable.upload_profile)
        }
        backLayout.setOnClickListener(this)
        btnSaveChanges.setOnClickListener(this)
        profileLayout.setOnClickListener(this)
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.back_layout -> finish()
            R.id.btn_save_changes -> userUpdateProfileApi()
            R.id.profile_layout -> requestMultiplePermissions()
        }
    }

    //update profile api parse
    private fun userUpdateProfileApi() {

        if (!Imagepath!!.isEmpty()) {

            val file = File(Imagepath)
            Log.e("FileName", "" + file.name)
            Log.e("ImagePath", Imagepath!!)

            requestFile = RequestBody.create("image/jpg".toMediaTypeOrNull(), file)
            body = MultipartBody.Part.createFormData("userfile", file.name, requestFile!!)

        }


        val emailRequest = RequestBody.create("text/plain".toMediaTypeOrNull(), edtEmailId!!.text.toString().trim())
        val lnameRequest = RequestBody.create("text/plain".toMediaTypeOrNull(), edtName!!.text.toString().trim())
        val fMobileRequest = RequestBody.create("text/plain".toMediaTypeOrNull(), edtMobileNo!!.text.toString().trim())
        val userIdRequest = RequestBody.create("text/plain".toMediaTypeOrNull(), userPref.user.id!!)
        val userAuth = RequestBody.create("text/plain".toMediaTypeOrNull(), userPref.user.token!!)


        if (Utility.getInstance().checkInternetConnection(context)) {
            apiService.updateUserProfile(
                    userIdRequest,
                    lnameRequest,
                    emailRequest,
                    fMobileRequest,
                    userAuth,
                    body)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe { showProgressDialog() }
                    .doOnCompleted { hideProgressDialog() }
                    .subscribe({ response: CommonDataResponse<UserModel> ->

                        if (response.responseStatus == "success") {
                            Toast.makeText(context, response.responseMessage, Toast.LENGTH_SHORT).show()
                            userPref.user = response.data!!
                        } else if (response.responseCode == "403") {
                            utils.openLogoutDialog(this, userPref)
                        } else {
                            hideProgressDialog()
                            Utility.simpleAlert(this, getString(R.string.info_dialog_title), response.responseMessage)
                        }
                    }, { e: Throwable ->

                        try {

                            if (e is ConnectException) {
                                hideProgressDialog()
                                Utility.simpleAlert(this, getString(R.string.error), getString(R.string.check_network_connection))
                            } else {
                                hideProgressDialog()
                                e.printStackTrace()
                                Utility.simpleAlert(context, "", getString(R.string.something_went_wrong))
                            }

                        } catch (ex: Exception) {
                            Log.e("MyProfilePage Activity", "Within Throwable Exception::" + e.message)
                        }

                    })
        } else {
            hideProgressDialog()
            Utility.simpleAlert(this, getString(R.string.error), getString(R.string.check_network_connection))
        }
    }

    private fun requestMultiplePermissions() {
        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {
                            showPictureDialog()
                        }

                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied) {
                            // show alert dialog navigating to Settings
                            //openSettingsDialog();
                        }
                    }

                    override fun onPermissionRationaleShouldBeShown(permissions: List<PermissionRequest>, token: PermissionToken) {
                        token.continuePermissionRequest()
                    }
                }).withErrorListener { Toast.makeText(applicationContext, "Some Error! ", Toast.LENGTH_SHORT).show() }
                .onSameThread()
                .check()
    }

    private fun showPictureDialog() {
        val pictureDialog = AlertDialog.Builder(this)
        pictureDialog.setTitle("Select Image")
        val pictureDialogItems = arrayOf(
                "From gallery",
                "From camera"
        )
        pictureDialog.setItems(pictureDialogItems
        ) { dialog, which ->
            when (which) {
                0 -> {
                    Imagepath = ""
                    choosePhotoFromGallary()
                }
                1 -> {
                    Imagepath = ""
                    takePhotoFromCamera()
                }
            }
        }
        pictureDialog.show()
    }

    private fun takePhotoFromCamera() {
        val intent1 = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent1.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        startActivityForResult(intent1, REQUEST_IMAGE_CAPTURE)
    }

    fun choosePhotoFromGallary() {
        val i = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(i, GALLERY)
    }

    private fun takeCameraImage() {
        Dexter.withActivity(this).withPermissions(
                Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE
        ).withListener(object : MultiplePermissionsListener {
            override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                if (report.areAllPermissionsGranted()) {
                    imageName = "" + System.currentTimeMillis() + ".jpg"
                    val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, getCacheImagePath(imageName))
                    if (takePictureIntent.resolveActivity(packageManager) != null) {
                        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                    }
                }
            }

            override fun onPermissionRationaleShouldBeShown(permissions: List<PermissionRequest>, token: PermissionToken) {
                token.continuePermissionRequest()
            }
        }).check()
    }

    private fun chooseImageFromGallery() {
        Dexter.withActivity(this).withPermissions(
                Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE
        ).withListener(object : MultiplePermissionsListener {
            override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                if (report.areAllPermissionsGranted()) {
                    val i = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    startActivityForResult(i, REQUEST_GALLERY_IMAGE)
                }
            }

            override fun onPermissionRationaleShouldBeShown(permissions: List<PermissionRequest>, token: PermissionToken) {
                token.continuePermissionRequest()
            }
        }).check()
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_CANCELED) {
            return
        }
        if (requestCode == GALLERY) {

            try {

                val galleryImageBitmap = MediaStore.Images.Media.getBitmap(baseContext.contentResolver, data!!.data)
                val stream = ByteArrayOutputStream()
                galleryImageBitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream)
                Imagepath = saveImage(galleryImageBitmap)
                defaultProfile!!.visibility = View.GONE
                showProfile!!.visibility = View.VISIBLE
                showProfile!!.setImageBitmap(galleryImageBitmap)

            } catch (e: IOException) {
                e.printStackTrace()
            }

        } else if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_IMAGE_CAPTURE) {
            var bitmap: Bitmap? = null
            bitmap = data!!.extras!!["data"] as Bitmap?
            val uri = getImageUri(bitmap!!)
            Imagepath = getPath(uri!!)
            Log.e("ImagePath", Imagepath!!)
            showProfile!!.setImageBitmap(bitmap)
            defaultProfile!!.visibility = View.GONE
            showProfile!!.visibility = View.VISIBLE
            showProfile!!.setImageBitmap(bitmap)
        }
    }

    @Throws(IOException::class)
    private fun getBitmapFromUri(uri: Uri): Bitmap {
        val parcelFileDescriptor = contentResolver.openFileDescriptor(uri, "r")
        val fileDescriptor = parcelFileDescriptor!!.fileDescriptor
        val image = BitmapFactory.decodeFileDescriptor(fileDescriptor)
        parcelFileDescriptor.close()
        return image
    }

    private fun cropImage(sourceUri: Uri) {
        val destinationUri = Uri.fromFile(File(cacheDir, queryName(contentResolver, sourceUri)))
        Imagepath = getRealPathFromURI(destinationUri)
        Log.e("ImagePath", "" + Imagepath)
    }


   /* private fun getImageUri(inImage: Bitmap): Uri? {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(contentResolver, inImage, "content", null)
        return Uri.parse(path)
    }*/

    private fun getImageUri(inImage: Bitmap): Uri? {

        try {
            val bytes = ByteArrayOutputStream()
            inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
            val path = MediaStore.Images.Media.insertImage(this.contentResolver, inImage, "pic_${System.currentTimeMillis()}", null)
            MediaScannerConnection.scanFile(
                    applicationContext, arrayOf<String>(path), null
            ) { path1, uri ->
                Log.e("ExternalStorage", "Scanned $path1:")
                Log.e("ExternalStorage", "-> uri=$uri")
            }
            Log.e("path", path)
            return Uri.parse(path)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            Log.e("finally", "finally")
        }
        return null
    }

    private fun queryName(resolver: ContentResolver, uri: Uri): String {
        val returnCursor = resolver.query(uri, null, null, null, null)
        val nameIndex = returnCursor!!.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        returnCursor.moveToFirst()
        val name = returnCursor.getString(nameIndex)
        returnCursor.close()
        return name
    }

    fun saveImage(myBitmap: Bitmap): String {
        val bytes = ByteArrayOutputStream()
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes)

        //val wallpaperDirectory = File(Environment.getExternalStorageDirectory().toString() + IMAGE_DIRECTORY)
        //val folder = File(Environment.getExternalStorageDirectory(), getString(R.string.app_name))
        val wallpaperDirectory = commonDocumentDirPath(getString(R.string.app_name))

        // have the object build the directory structure, if needed.
        if (!wallpaperDirectory!!.exists()) {
            wallpaperDirectory!!.mkdirs()
        }
        try {
            val f = File(wallpaperDirectory, Calendar.getInstance()
                    .timeInMillis.toString() + ".jpg")
            f.createNewFile()
            val fo = FileOutputStream(f)
            fo.write(bytes.toByteArray())
            MediaScannerConnection.scanFile(this, arrayOf(f.path), arrayOf("image/jpeg"), null)
            fo.close()
            Log.d("TAG", "File Saved::--->" + f.absolutePath)
            return f.absolutePath
        } catch (e1: IOException) {
            e1.printStackTrace()
        }
        return ""
    }

    private fun getRealPathFromURI(contentURI: Uri): String? {
        val result: String?
        val cursor = contentResolver.query(contentURI, null, null, null, null)
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.path
        } else {
            cursor.moveToFirst()
            val idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
            result = cursor.getString(idx)
            cursor.close()
        }
        return result
    }

    private fun getPath(uri: Uri): String {
        val data = arrayOf(MediaStore.Images.Media.DATA)
        val loader = CursorLoader(this, uri, data, null, null, null)
        val cursor = loader.loadInBackground()
        val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor.moveToFirst()
        Log.d("image path", cursor.getString(columnIndex))
        return cursor.getString(columnIndex)
    }

    private fun getCacheImagePath(fileName: String): Uri {
        val path = File(externalCacheDir, "camera")
        if (!path.exists()) path.mkdirs()
        val image = File(path, fileName)
        //return FileProvider.getUriForFile(this, "$packageName.provider", image);
        return FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID.toString() + ".provider", image)
    }

    companion object {
        private const val IMAGE_DIRECTORY = "/GroceryImage"
    }

    fun saveMediaToStorage(bitmap: Bitmap) {

        //Generating a file name
        val filename = "${System.currentTimeMillis()}.jpg"

        //Output stream
        var fos: OutputStream? = null

        //For devices running android >= Q
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            //getting the contentResolver
            context?.contentResolver?.also { resolver ->

                //Content resolver will process the contentvalues
                val contentValues = ContentValues().apply {

                    //putting file information in content values
                    put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                }

                //Inserting the contentValues to contentResolver and getting the Uri
                val imageUri: Uri? =
                        resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

                //Opening an outputstream with the Uri that we got
                fos = imageUri?.let { resolver.openOutputStream(it) }
            }
        } else {
            //These for devices running on android < Q
            //So I don't think an explanation is needed here
            val imagesDir =
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            val image = File(imagesDir, filename)
            fos = FileOutputStream(image)
        }

        fos?.use {
            //Finally writing the bitmap to the output stream that we opened
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
            Toast.makeText(context, "Saved to Photos", Toast.LENGTH_SHORT).show()
        }
    }

    fun commonDocumentDirPath(FolderName: String): File? {

        var dir: File? = null
        dir = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).toString() + "/" + FolderName)
        } else {
            File(Environment.getExternalStorageDirectory().toString() + "/" + FolderName)
        }
        return dir

    }
}