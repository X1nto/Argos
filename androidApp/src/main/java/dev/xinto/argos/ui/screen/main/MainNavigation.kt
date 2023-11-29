package dev.xinto.argos.ui.screen.main

import android.os.Parcelable
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import dev.xinto.argos.R
import kotlinx.parcelize.Parcelize

sealed class MainNavigation(
    @StringRes
    val title: Int,

    @DrawableRes
    val icon: Int,

    @DrawableRes
    val iconSelected: Int = icon,
) : Parcelable {

    @Parcelize
    data object Home : MainNavigation(
        title = R.string.home_title,
        icon = R.drawable.ic_home,
        iconSelected = R.drawable.ic_home_filled
    )

    @Parcelize
    data object Messages : MainNavigation(
        title = R.string.messages_title,
        icon = R.drawable.ic_mail,
        iconSelected = R.drawable.ic_mail_filled,
    )

    @Parcelize
    data object News : MainNavigation(
        title = R.string.news_title,
        icon = R.drawable.ic_newspaper
    )

    companion object {
        val bottomBarItems by lazy {
            setOf(Home, Messages, News)
        }
    }
}