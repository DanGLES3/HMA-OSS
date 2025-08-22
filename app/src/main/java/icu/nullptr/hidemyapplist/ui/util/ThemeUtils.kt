package icu.nullptr.hidemyapplist.ui.util

import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.widget.ImageView
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.StyleRes
import androidx.fragment.app.Fragment
import com.google.android.material.color.DynamicColors
import icu.nullptr.hidemyapplist.service.PrefManager
import org.frknkrc44.hma_oss.R

object ThemeUtils {
    private const val THEME_DEFAULT = "DEFAULT"
    // private const val THEME_BLACK = "BLACK"

    val isSystemAccent get() = DynamicColors.isDynamicColorAvailable() && PrefManager.followSystemAccent

    fun isNightMode(context: Context) = (context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES

    fun getNightTheme(context: Context): String {
        return /*if (PrefManager.blackDarkTheme && isNightMode(context))
            THEME_BLACK else*/ THEME_DEFAULT
    }

    @StyleRes
    fun getNightThemeStyleRes(context: Context): Int {
        return /*if (PrefManager.blackDarkTheme && isNightMode(context))
            R.style.Base_AppTheme else*/ R.style.Base_AppTheme
    }

    fun ImageView.setCircleBackground(@ColorInt color: Int) {
        val gradientDrawable = GradientDrawable()
        gradientDrawable.setColor(color)
        gradientDrawable.cornerRadius = resources.displayMetrics.density * 96
        background = gradientDrawable
    }

    /**
     * Retrieve a color from the current [android.content.res.Resources.Theme].
     */
    @ColorInt
    fun Context.themeColor(
        @AttrRes themeAttrId: Int
    ): Int {
        val style = obtainStyledAttributes(intArrayOf(themeAttrId))
        val color = style.getColor(0, Color.MAGENTA)
        style.recycle()
        return color
    }

    @ColorInt
    fun Fragment.themeColor(
        @AttrRes themeAttrId: Int
    ) = requireContext().themeColor(themeAttrId)

    @ColorInt
    fun Fragment.getColor(
        @ColorRes colorId: Int
    ) = requireContext().getColor(colorId)
}
