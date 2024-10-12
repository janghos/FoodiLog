package com.foodilogg.fragment

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.text.style.ForegroundColorSpan
import android.view.View
import android.view.Window
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.foodilogg.DTO.DateDTO
import com.foodilogg.DTO.HolidayDataItem
import com.foodilogg.FoodilogApplication
import com.foodilogg.R
import com.foodilogg.databinding.FragmentNewSellCalendarDialogBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import org.threeten.bp.DayOfWeek
import java.io.IOException
import java.nio.charset.Charset
import java.util.Collections
import java.util.Locale

class NewSellCalendarDialogFragment : DialogFragment() {

    private lateinit var binding: FragmentNewSellCalendarDialogBinding
    private var startDate : DateDTO?= null
    var holidayList: List<HolidayDataItem> = Collections.emptyList()


    companion object {
        fun newInstance(): NewSellCalendarDialogFragment {
            return NewSellCalendarDialogFragment()
        }
    }

    interface OnDateSelectListener {
        fun onSingleDateSelect(selectDate : DateDTO)
    }

    fun setOnDateSelectListener(listener: OnDateSelectListener){
        onDateSelectListener = listener
    }

    private lateinit var onDateSelectListener : OnDateSelectListener

    @SuppressLint("NewApi")
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        holidayList = readJsonFromAssets(requireContext(), "holiday_korea.json")

        val builder = AlertDialog.Builder(requireContext())
        binding = FragmentNewSellCalendarDialogBinding.inflate(layoutInflater)

        binding.ibClose.setOnClickListener {
            dismissAllowingStateLoss()
        }

        binding.btnApplyDate.setOnClickListener {
            onDateSelectListener.onSingleDateSelect(startDate!!)
            dismiss()
        }

        configureCalendarView(binding.calendarView)
        builder.setView(binding.root)

        dialog?.let{safeDialog->
            safeDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            safeDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        }

        return builder.create()

    }


    @SuppressLint("NewApi")
    @RequiresApi(Build.VERSION_CODES.N)
    private fun configureCalendarView(calendarView: MaterialCalendarView) {
        val dayDecorator = DayDecorator(requireContext())
        val todayDecorator = TodayDecorator(requireContext())
        val sundayDecorator = SundayDecorator()
        val saturdayDecorator = SaturdayDecorator()
        var selectedMonthDecorator = SelectedMonthDecorator(CalendarDay.today().month)

        calendarView.addDecorators(dayDecorator, todayDecorator, sundayDecorator, saturdayDecorator, selectedMonthDecorator, holidayDecorator(holidayList))

        // 좌우 화살표 가운데의 연/월이 보이는 방식 지정
        calendarView.setHeaderTextAppearance(R.style.CalendarWidgetHeader)
        calendarView.setTitleFormatter { day ->
            val inputText = day.date
            val calendarHeaderElements = inputText.toString().split("-")
            val calendarHeaderBuilder = StringBuilder()

            calendarHeaderBuilder.append(calendarHeaderElements[0]).append("년 ")
                .append(calendarHeaderElements[1]).append("월")

            calendarHeaderBuilder.toString()
        }

        // 캘린더에 보여지는 Month가 변경된 경우
        calendarView.setOnMonthChangedListener { widget, date ->
            // 기존에 설정되어 있던 Decorators 초기화
            calendarView.removeDecorators()
            calendarView.invalidateDecorators()

            // Decorators 추가
            selectedMonthDecorator = SelectedMonthDecorator(date.month)
            calendarView.addDecorators(dayDecorator, todayDecorator, sundayDecorator, saturdayDecorator, selectedMonthDecorator, holidayDecorator(holidayList))
        }

        calendarView.setOnDateChangedListener { widget, date, selected ->

            binding.tvTotalDay.visibility = View.GONE

            startDate = DateDTO()
            startDate!!.year = date.year
            startDate!!.month = date.month
            startDate!!.day = date.day

            MaterialCalendarView.SELECTION_MODE_SINGLE
        }
    }

    private fun readJsonFromAssets(context: Context, fileName: String): List<HolidayDataItem> {
        var items: List<HolidayDataItem> = Collections.emptyList()
        try {
            val inputStream = context.assets.open(fileName)
            val size = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            inputStream.close()
            val json = String(buffer, Charset.defaultCharset())

            // Gson을 사용하여 JSON을 List<HolidayDataItem>으로 파싱
            items = Gson().fromJson(json, object : TypeToken<List<HolidayDataItem>>() {}.type)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return items
    }

    /* 선택된 날짜의 background를 설정하는 클래스 */
    private inner class DayDecorator(context: Context) : DayViewDecorator {
        private val drawable = ContextCompat.getDrawable(context,R.drawable.calendar_selector)
        // true를 리턴 시 모든 요일에 내가 설정한 드로어블이 적용된다
        override fun shouldDecorate(day: CalendarDay): Boolean {
            return true
        }

        // 일자 선택 시 내가 정의한 드로어블이 적용되도록 한다
        override fun decorate(view: DayViewFacade) {
            view.setSelectionDrawable(drawable!!)
            view.addSpan(ForegroundColorSpan(ContextCompat.getColor(context!!, R.color.black)))
        }
    }

    /* 오늘 날짜의 background를 설정하는 클래스 */
    private class TodayDecorator(context: Context): DayViewDecorator {
        private var date = CalendarDay.today()
        override fun shouldDecorate(day: CalendarDay?): Boolean {
            return day?.equals(date)!!
        }
        override fun decorate(view: DayViewFacade) {
            view.addSpan(ForegroundColorSpan(ContextCompat.getColor(FoodilogApplication.context, R.color.color_ff0000)))
        }
    }

    /* 이번달에 속하지 않지만 캘린더에 보여지는 이전달/다음달의 일부 날짜를 설정하는 클래스 */
    private inner class SelectedMonthDecorator(val selectedMonth : Int) : DayViewDecorator {
        override fun shouldDecorate(day: CalendarDay): Boolean {
            return day.month != selectedMonth
        }
        override fun decorate(view: DayViewFacade) {
            view.addSpan(ForegroundColorSpan(ContextCompat.getColor(FoodilogApplication.context, R.color.color_e3e3e3)))
        }
    }

    /* 일요일 날짜의 색상을 설정하는 클래스 */
    private class SundayDecorator : DayViewDecorator {
        override fun shouldDecorate(day: CalendarDay): Boolean {
            val sunday = day.date.with(DayOfWeek.SUNDAY).dayOfMonth
            return sunday == day.day
        }

        override fun decorate(view: DayViewFacade) {
//            view.addSpan(object:ForegroundColorSpan(Color.RED){})
        }
    }

    private class holidayDecorator(private var holidays: List<HolidayDataItem>) : DayViewDecorator {
        override fun shouldDecorate(day: CalendarDay): Boolean {
            val formattedDate = "${day.year}${formatTwoDigits(day.month)}${formatTwoDigits(day.day)}"
            return holidays.any { it.locdate == formattedDate.toInt() }
        }

        override fun decorate(view: DayViewFacade) {
            // Decorate된 날짜에 대한 작업 수행 (여기서는 텍스트 색상 변경)
//            view.addSpan(object:ForegroundColorSpan(Color.RED){})
        }

        private fun formatTwoDigits(value: Int): String {
            return String.format(Locale.getDefault(), "%02d", value)
        }
    }

    /* 토요일 날짜의 색상을 설정하는 클래스 */
    private class SaturdayDecorator : DayViewDecorator {
        override fun shouldDecorate(day: CalendarDay): Boolean {
            val saturday = day.date.with(DayOfWeek.SATURDAY).dayOfMonth
            return saturday == day.day
        }

        override fun decorate(view: DayViewFacade) {
//            view.addSpan(object:ForegroundColorSpan(Color.BLUE){})
        }
    }

}
