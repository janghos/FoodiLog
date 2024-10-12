package com.foodilog.fragment

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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.foodilog.DTO.DateDTO
import com.foodilog.DTO.ReviewData
import com.foodilog.DTO.ShopInfoData
import com.foodilog.HeightProvider
import com.foodilog.activity.BaseActivity
import com.foodilog.activity.MainActivity
import com.foodilog.databinding.FragmentAddReviewBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat

class AddReviewFragment : Fragment() {


    companion object {
        private const val REQUEST_PERMISSION = 100
        private const val REQUEST_IMAGE_PICK = 200
        private const val REQUEST_IMAGE_CAPTURE = 300
    }

    lateinit var binding : FragmentAddReviewBinding
    private var heightProvider : HeightProvider ?= null
    private var mShopInfoData : ShopInfoData ?=null
    private var mSelectedImageUri: Uri? = null
    private var mSelectedImageUris: MutableList<Uri> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentAddReviewBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        initialization()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.map.onCreate(savedInstanceState)
        binding.map.onResume()
    }

    override fun onDestroyView() {
        super.onDestroyView()

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
        val dateFormat = SimpleDateFormat("yyyy년 MM월 dd일")
        val todayDate = dateFormat.format(calendar.time)
        binding.tvDate.text = todayDate

        binding.tvDate.setOnClickListener {
            val calendarDialog = NewSellCalendarDialogFragment.newInstance()
            calendarDialog.setOnDateSelectListener(object : NewSellCalendarDialogFragment.OnDateSelectListener{
                override fun onSingleDateSelect(selectDate: DateDTO) {
                    binding.tvDate.text = "${selectDate.year}년 ${selectDate.month}월 ${selectDate.day}일"
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
        // 이미지 Uri가 있는 경우에만 데이터 저장
        if (mSelectedImageUri != null) {
            try {
                // 이미지 로컬 저장
                val bitmap = MediaStore.Images.Media.getBitmap(requireContext().contentResolver, mSelectedImageUri)
                val imageFile = createImageFile()
                val outputStream = FileOutputStream(imageFile)
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                outputStream.flush()
                outputStream.close()

                // 데이터 저장
                val reviewTitle = binding.etReviewTitle.text.toString()
                val reviewContent = binding.etReviewContent.text.toString()
                val rating = binding.ratingBar.rating
                val date = binding.tvDate.text.toString() // "2024년 00월 00일" 형식

                val reviewData = ReviewData(
                    reviewShopInfo = mShopInfoData,
                    reviewTitle = reviewTitle,
                    reviewContent = reviewContent,
                    rating = rating,
                    date = date,
                    imagePath = mSelectedImageUris.map { it.toString() } // Uri 리스트를 문자열 리스트
                )

                // TODO: reviewData를 로컬 데이터베이스 또는 파일에 저장

            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            // 이미지가 선택되지 않은 경우 처리 (예: 토스트 메시지 표시)
        }
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
            imageView.layoutParams = LinearLayout.LayoutParams(300, 300) // 이미지뷰 크기 설정
            imageView.scaleType = ImageView.ScaleType.CENTER_CROP // 이미지뷰 scaleType 설정

            // 이미지뷰 클릭 리스너 추가 (수정 기능)
            imageView.setOnClickListener {
                // TODO: 이미지 수정 기능 구현
            }

            binding.imageContainer.addView(imageView) // imageContainer는 LinearLayout

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