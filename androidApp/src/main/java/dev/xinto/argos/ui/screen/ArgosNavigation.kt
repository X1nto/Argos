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

    @Parcelize
    data object User : ArgosNavigation

    @Parcelize
    data class Message(val id: String, val semesterId: String) : ArgosNavigation

    @Parcelize
    data class Course(val id: String) : ArgosNavigation

}