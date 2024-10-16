package com.foodilogg.fragment

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.icu.util.Calendar
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.compose.ui.semantics.text
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.foodilogg.DTO.DateDTO
import com.foodilogg.DTO.ShopInfoData
import com.foodilogg.HeightProvider
import com.foodilogg.activity.BaseActivity
import com.foodilogg.activity.MainActivity
import com.foodilogg.databinding.FragmentAddReviewBinding
import com.foodilogg.local.FoodiDataBase
import com.foodilogg.local.ReviewEntity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.text.format
import kotlin.text.replace
import kotlin.text.toLong

class AddReviewFragment : Fragment() {


    companion object {
        private const val REQUEST_PERMISSION = 100
        private const val REQUEST_IMAGE_PICK = 200
        private const val REQUEST_IMAGE_CAPTURE = 300
    }

    private var _binding: FragmentAddReviewBinding? = null // binding을 nullable로 선언
    private val binding get() = _binding!! // binding에 접근할 때 non-null assertion 사용
    private var heightProvider : HeightProvider ?= null
    private var mShopInfoData : ShopInfoData ?=null
    private var mSelectedImageUri: Uri? = null
    private var mSelectedImageUris: MutableList<Uri> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddReviewBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialization()
        binding.map.onCreate(savedInstanceState)
        binding.map.onResume()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        heightProvider?.let {
            it.dismiss()
        }
    }

    fun onShopInfoSelected(shopInfoData: ShopInfoData) {

        mShopInfoData = shopInfoData

        binding.llShopReview.visibility = View.VISIBLE
        binding.map.visibility = View.VISIBLE
        binding.tvShopTitle.text = shopInfoData.name
        binding.tvShopAddress.text = shopInfoData.address
        binding.map.getMapAsync { googleMap ->
            val storeLocation = LatLng(shopInfoData.latitude, shopInfoData.longitude)
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(storeLocation, 15f))
            googleMap.addMarker(MarkerOptions().position(storeLocation).title(shopInfoData.name))
        }

        binding.reSearch.visibility = View.VISIBLE

        binding.btnSearch.visibility = View.GONE
    }

    fun initialization() {

        binding.btnSearch.setOnClickListener {
            SearchDialogFragment().show(childFragmentManager, "search")
        }

        binding.reSearch.setOnClickListener{
            SearchDialogFragment().show(childFragmentManager, "search")
        }

        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyy년 MM월 dd일", Locale.getDefault())
        val todayDate = dateFormat.format(calendar.time)
        binding.tvDate.text = todayDate

        binding.tvDate.setOnClickListener {
            val calendarDialog = NewSellCalendarDialogFragment.newInstance()
            calendarDialog.setOnDateSelectListener(object : NewSellCalendarDialogFragment.OnDateSelectListener{
                override fun onSingleDateSelect(selectDate: DateDTO) {
                    binding.tvDate.text = String.format("%04d년 %02d월 %02d일", selectDate.year, selectDate.month, selectDate.day)
                }
            })
            calendarDialog.show(childFragmentManager, "calendar")
        }

        binding.btnSubmit.setOnClickListener {
            saveReviewData()
        }


        //키보드 bottom navigation 숨김처리
        heightProvider = HeightProvider(requireActivity()).init().setHeightListener { height ->
            val layoutParams = binding.rlLayout.layoutParams as FrameLayout.LayoutParams
            if (height == 0) {
                // 키보드가 내려갔을 때
                (requireActivity() as MainActivity).visibleNav()
                layoutParams.height = FrameLayout.LayoutParams.MATCH_PARENT // 원래 높이로 복원
            } else {
                // 키보드가 올라왔을 때
                (requireActivity() as MainActivity).goneNav()
                layoutParams.height = binding.rlLayout.height - height // 높이를 줄임
            }
            binding.rlLayout.layoutParams = layoutParams
        }

        binding.btnAddImage.setOnClickListener {
            // 권한 확인
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                // 권한 요청
                ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE), REQUEST_PERMISSION)
            } else {
                // 권한이 이미 있는 경우
                showImagePicker()
            }
        }

    }

    private fun saveReviewData() {
        if (binding.etReviewTitle.text.toString().isNotEmpty() && binding.etReviewContent.text.toString().isNotEmpty()) {
            try {

                if (mSelectedImageUri != null) { // null 체크 추가
                    // 이미지 로컬 저장
                    val bitmap = MediaStore.Images.Media.getBitmap(requireContext().contentResolver, mSelectedImageUri)
                    val imageFile = createImageFile()
                    val outputStream = FileOutputStream(imageFile)
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                    outputStream.flush()
                    outputStream.close()
                }
                // 데이터 저장
                val reviewTitle = binding.etReviewTitle.text.toString()
                val reviewContent = binding.etReviewContent.text.toString()
                val rating = binding.ratingBar.rating
                val dateString = binding.tvDate.text.toString() // "2024년 00월 00일" 형식
                val dateLong = dateString.replace("[^0-9]".toRegex(), "").toLong()

                val reviewEntity = ReviewEntity(
                    shopName = mShopInfoData?.name,
                    shopAddress = mShopInfoData?.address,
                    reviewTitle = reviewTitle,
                    reviewContent = reviewContent,
                    rating = rating,
                    date = dateLong,
                    imagePaths = mSelectedImageUris.map { it.toString() },
                    latitude = mShopInfoData?.latitude ?: 0.0,
                    longitude = mShopInfoData?.longitude ?: 0.0

                )

                // 데이터베이스에 저장
                lifecycleScope.launch {
                    FoodiDataBase.getDatabase(requireContext()).reviewDao().insertReview(reviewEntity)
                }

                (requireActivity() as BaseActivity).commonDialogAlert("저장이 완료되었습니다!") { replaceHistoryFragment() }
            } catch (e: Exception) {
                Log.d("errorrrrrr", e.toString())
                e.printStackTrace()
            }
        } else {
            Toast.makeText(requireContext(),"제목과 내용을 입력해주세요!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun replaceHistoryFragment(){
        (requireActivity() as MainActivity).replaceFragment(HistoryFragment())
    }

    // 이미지 파일 생성
    private fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss")
        val imageFileName = "JPEG_" + timeStamp + "_"
        val storageDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(imageFileName, ".jpg", storageDir)
    }

    private fun showImagePicker() {
        val options = arrayOf<CharSequence>("사진 촬영", "갤러리에서 선택")
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("사진 선택")
        builder.setItems(options) { dialog, item ->
            when (item) {
                0 -> {
                    // 사진 촬영
                    val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    if (takePictureIntent.resolveActivity(requireContext().packageManager) != null) {
                        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                    }
                }
                1 -> {
                    // 갤러리에서 선택
                    val pickPhotoIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    pickPhotoIntent.type = "image/*"
                    startActivityForResult(pickPhotoIntent, REQUEST_IMAGE_PICK)
                }
            }
        }
        builder.show()
    }

    private fun getImageUriFromBitmap(bitmap: Bitmap): Uri? {
        val bytes = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(requireContext().contentResolver, bitmap, "Title", null)
        return Uri.parse(path)
    }


    private fun onMoveSetting() {
        val intent = Intent()
        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        val uri = Uri.fromParts("package", requireActivity().packageName, null)
        intent.data = uri
        startActivity(intent)
    }

    private fun addImageView(imageUri: Uri) {
        if (mSelectedImageUris.size < 3) {
            mSelectedImageUris.add(imageUri)

            val imageView = ImageView(requireContext())
            imageView.setImageURI(imageUri)
            imageView.layoutParams = LinearLayout.LayoutParams(300, 300)
            imageView.scaleType = ImageView.ScaleType.CENTER_CROP
            binding.imageContainer.addView(imageView)

            // 추가 버튼 숨기기/표시
            if (mSelectedImageUris.size == 3) {
                binding.btnAddImage.visibility = View.GONE
            } else {
                binding.btnAddImage.visibility = View.VISIBLE
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == REQUEST_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                // 권한 허용
                showImagePicker()
            } else {
                (requireActivity() as BaseActivity).commonDialogConfirm("사진 정보를 가져올 수 없습니다.", "설정", "취소", {onMoveSetting()}, {})
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_IMAGE_PICK, REQUEST_IMAGE_CAPTURE -> {
                    val uri = if (requestCode == REQUEST_IMAGE_PICK) {
                        data?.data // 갤러리에서 선택한 이미지 Uri
                    } else {
                        val imageBitmap = data?.extras?.get("data") as Bitmap?
                        imageBitmap?.let { getImageUriFromBitmap(it) } // 카메라로 촬영한 이미지 Uri
                    }

                    uri?.let {
                        addImageView(it) // 이미지뷰 추가
                    }
                }
            }
        }
    }
}