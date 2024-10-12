package com.foodilogg

import android.content.Context
import android.content.res.Configuration

object DeviceUtil {

    const val STANDARD_WIDTH_PIXEL = 1080F
    const val STANDARD_HEIGHT_PIXEL = 2042F
    const val STANDARD_DENSITY_DPI = 450F

    const val STANDARD_WIDTH_MULTIPLE = 2F
    const val STANDARD_HEIGHT_MULTIPLE = 1.3F

    var widthMultiple = 1F
    var heightMultiple = 1F
    var dpiMultiple = 1F

    enum class DensityDpiType{
        MDPI, HDPI, XHDPI, XXHDPI, XXXHDPI
    }

    /**
     * 기준 해상도 계산
     * @param context 컨텍스트
     */
    fun create(context: Context)
    {
        val screenSizeType = context.resources.configuration.screenLayout.and(Configuration.SCREENLAYOUT_SIZE_MASK)
        when(screenSizeType)
        {
            Configuration.SCREENLAYOUT_SIZE_NORMAL, Configuration.SCREENLAYOUT_SIZE_SMALL -> {
                widthMultiple *= 1
                heightMultiple *= 1
            }
            Configuration.SCREENLAYOUT_SIZE_LARGE, Configuration.SCREENLAYOUT_SIZE_XLARGE -> {
                widthMultiple *= STANDARD_WIDTH_MULTIPLE
                heightMultiple *= STANDARD_HEIGHT_MULTIPLE
            }
        }

        dpiMultiple = STANDARD_DENSITY_DPI / getDensityDpiNum(context).toFloat()

    }

    /**
     * 디바이스 화면 넓이 구하기
     * @param context 컨텍스트
     * @return 화면 넓이
     */
    fun getDeviceWidth(context: Context): Int
    {
        val dm = context.applicationContext.resources.displayMetrics
        return dm.widthPixels
    }

    /**
     * 디바이스 화면 높이 구하기
     * @param context 컨텍스트
     * @return 화면 높이
     */
    fun getDeviceHeight(context: Context): Int
    {
        val dm = context.applicationContext.resources.displayMetrics
        return dm.heightPixels
    }

    /**
     * 디바이스 DPI구하기
     * @param context 컨텍스트
     * @return DPI 타입
     */
    fun getDensityDpi(context: Context): DensityDpiType
    {
        val dm = context.applicationContext.resources.displayMetrics
        val densityDpi = dm.densityDpi
        when(densityDpi)
        {
            in 0.. 160 -> return DensityDpiType.MDPI
            in 161.. 240 -> return DensityDpiType.HDPI
            in 241.. 320 -> return DensityDpiType.XHDPI
            in 321.. 480 -> return DensityDpiType.XXHDPI
            else -> return DensityDpiType.XXXHDPI
        }
        return DensityDpiType.MDPI
    }


    /**
     * 디바이스 DPI 구하기
     * @param context 컨텍스트
     * @return DPI
     */
    fun getDensityDpiNum(context: Context): Int
    {
        val dm = context.applicationContext.resources.displayMetrics
        val densityDpi = dm.densityDpi
        return densityDpi
    }

    /**
     * 기준 디바이스에서 해당 디바이스 넓이 배수 구하기
     * @param context 컨텍스트
     * @return 디바이스 넓이 배수
     */
    fun getDeviceWidthMultiple(context: Context): Float
    {
        val width = getDeviceWidth(context)
        return width / STANDARD_WIDTH_PIXEL * dpiMultiple * widthMultiple
    }

    /**
     * 기준 디바이스에서 해당 디바이스 높이 배수 구하기
     * @param context 컨텍스트
     * @return 디바이스 높이 배수
     */
    fun getDeviceHeightMultiple(context: Context): Float
    {
        val height = getDeviceHeight(context)
        return height / STANDARD_HEIGHT_PIXEL * dpiMultiple * heightMultiple
    }

    /**
     * PX => SP 단위변환
     * @param context 컨텍스트
     * @param px PX Value
     * @return SP Value
     */
    fun pxToSp(context: Context, px: Float): Float
    {
        val dm = context.applicationContext.resources.displayMetrics
        return px / dm.scaledDensity
    }

    /**
     * DP => PX 단위변환
     * @param context 컨텍스트
     * @param dp DP Value
     * @return PX Value
     */
    fun dpToPx(context: Context, dp: Float): Float
    {
        val density = context.applicationContext.resources.displayMetrics.density
        return dp * density
    }

    /**
     * PX => DP 단위변환
     * @param context 컨텍스트
     * @param px PX Value
     * @return DP Value
     */
    fun pxToDp(context: Context, px: Float): Float
    {
        val density = context.applicationContext.resources.displayMetrics.density
        return px / density
    }
}