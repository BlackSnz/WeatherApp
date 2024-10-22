package com.example.weatherapp.ui.screens

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import androidx.appcompat.content.res.AppCompatResources
import androidx.cardview.widget.CardView
import com.example.weatherapp.R
import com.example.weatherapp.databinding.WindCardBinding

class WindCardView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = com.google.android.material.R.attr.cardViewStyle) :
    CardView(context, attrs, defStyleAttr) {

    private var textSpeedValue: String
    private var textSpeedUnit: String
    private var currentDirection: String
    private val binding: WindCardBinding

    init {
        binding = WindCardBinding.inflate(LayoutInflater.from(context), this, true)

        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.WindCardView,
            0, 0
        ).apply {
            try {
                textSpeedValue = getString(R.styleable.WindCardView_speedValue) ?: "0"
                textSpeedUnit = getString(R.styleable.WindCardView_speedUnit) ?: "m/s"
                currentDirection = getString(R.styleable.WindCardView_direction) ?: "n"


            } finally {
                recycle()
            }
        }

        binding.tvWindCardSpeedValue.text = textSpeedValue
        binding.tvWindCardSpeedUnit.text = textSpeedUnit
        binding.ivWindCardIcon.setImageDrawable(setDirectionIcon())

    }

    fun updateWindCard(speed: String, unit: String, direction: String) {
        textSpeedValue = speed
        textSpeedUnit = unit
        currentDirection = direction
        binding.tvWindCardSpeedValue.text = textSpeedValue
        binding.tvWindCardSpeedUnit.text = textSpeedUnit
        binding.ivWindCardIcon.setImageDrawable(setDirectionIcon())
        invalidate()
        requestLayout()
    }

    private fun setDirectionIcon(): Drawable? {
        when (currentDirection) {
            "n" -> return AppCompatResources.getDrawable(context, R.drawable.n_direction)
            "s" -> return AppCompatResources.getDrawable(context, R.drawable.s_direction)
            "e" -> return AppCompatResources.getDrawable(context, R.drawable.e_direction)
            "w" -> return AppCompatResources.getDrawable(context, R.drawable.w_direction)
            "ne" -> return AppCompatResources.getDrawable(context, R.drawable.ne_direction)
            "nw" -> return AppCompatResources.getDrawable(context, R.drawable.nw_direction)
            "se" -> return AppCompatResources.getDrawable(context, R.drawable.se_direction)
            "sw" -> return AppCompatResources.getDrawable(context, R.drawable.sw_direction)
            else -> {
                Log.e("WeatherCardView", "Unknown direction: $currentDirection")
                return AppCompatResources.getDrawable(context, R.drawable.n_direction)
            }
        }
    }

}