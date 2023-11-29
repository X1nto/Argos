package dev.xinto.argos.ui.screen

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

sealed interface ArgosNavigation : Parcelable {

    @Parcelize
    data object Login : ArgosNavigation

    @Parcelize
    data object Main : ArgosNavigation

    @Parcelize
    data object Notifications : ArgosNavigation

}