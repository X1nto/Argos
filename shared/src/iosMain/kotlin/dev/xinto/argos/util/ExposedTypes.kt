package dev.xinto.argos.util

import androidx.paging.ItemSnapshotList
import androidx.paging.PagingDataEvent
import androidx.paging.PagingDataPresenter

@Suppress("unused")
fun exposed(
    a: PagingDataPresenter<*>,
    b: PagingDataEvent<*>,
    c: ItemSnapshotList<*>
) {
    throw NotImplementedError()
}