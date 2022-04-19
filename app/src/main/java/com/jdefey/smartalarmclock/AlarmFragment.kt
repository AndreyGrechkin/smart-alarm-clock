package com.jdefey.smartalarmclock

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.InputFilter
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.work.WorkManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.jdefey.smartalarmclock.utils.InputFilterMinMax
import com.jdefey.smartalarmclock.woker.SunriseWorker
import com.jdefey.smartalarmclock.databinding.FragmentAlarmBinding
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*


@AndroidEntryPoint
class AlarmFragment : Fragment(R.layout.fragment_alarm) {

    private val binding by viewBinding(FragmentAlarmBinding::bind)
    private val viewModel: AlarmViewModel by viewModels()
    private var timeRise: Long = 0
    private lateinit var hourInput: EditText
    private lateinit var minuteInput: EditText
    private val sdf = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault())

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        inputText()
        setupUI(requireView())
        observe()

        binding.setAlarmButton.setOnClickListener {
            if (hourInput.text.toString() == "") hourInput.setText(resources.getText(R.string._00))
            if (minuteInput.text.toString() == "") minuteInput.setText(resources.getText(R.string._00))

            viewModel.setAlarm(
                hour = hourInput.text.toString().toLong(),
                minute = minuteInput.text.toString().toLong(),
                timeRise = timeRise
            )
        }

        binding.checkboxLocation.setOnClickListener {
            if (binding.checkboxLocation.isChecked) {
                if (!hasLocationPermission()) {
                    requestLocationPermission()
                }
                viewModel.getSunrise()
            }
        }

        binding.searchLocationButton.setOnClickListener {
            viewModel.getEditLocation(binding.editLocation.editText?.text.toString())
        }
    }

    private fun hideSoftKeyboard() {
        try {
            val inputMethodManager =
                requireActivity().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(
                requireActivity().currentFocus!!.windowToken,
                0
            )
        } catch (e: Exception) {
            return
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    fun setupUI(view: View) {
        if (view !is EditText) {
            view.setOnTouchListener { _, _ ->
                hideSoftKeyboard()
                false
            }
        }
        if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                val innerView = view.getChildAt(i)
                setupUI(innerView)
            }
        }
    }

    private fun inputText() {
        hourInput = binding.editHour.editText!!
        minuteInput = binding.editMinute.editText!!
        hourInput.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                hourInput.setText("")
                hourInput.text?.filters = arrayOf<InputFilter>(InputFilterMinMax(0, 24))
                binding.setAlarmButton.isEnabled = true
            } else {
                if (hourInput.text.toString() == "") hourInput.setText(resources.getText(R.string._00))
            }
        }

        minuteInput.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                minuteInput.setText("")
                minuteInput.text.filters = arrayOf<InputFilter>(InputFilterMinMax(0, 60))
                binding.setAlarmButton.isEnabled = true
            } else {
                if (minuteInput.text.toString() == "") minuteInput.setText(resources.getText(R.string._00))
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun observe() {
        WorkManager.getInstance(requireContext())
            .getWorkInfosForUniqueWorkLiveData(UNIQUE)
            .observe(viewLifecycleOwner) { workInfoList ->
                workInfoList.forEach { workInfo ->
                    val successOutputData = workInfo.outputData
                    timeRise = successOutputData.getLong(SunriseWorker.WORK_RESULT_KEY, 0)
                    binding.timeTextView3.text = "Расвет  " + sdf.format(timeRise)
                }
            }
    }

    private fun hasLocationPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(), arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
            REQUEST_CODE
        )
    }

    companion object {
        const val UNIQUE = "unique"
        const val REQUEST_CODE = 11
    }
}