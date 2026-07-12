package org.neteinstein.pickaname.presentation.navigation

import org.neteinstein.pickaname.presentation.sync.SyncOrigin

/** Central catalogue of nav-graph routes. Plain String routes — no kotlinx-serialization needed. */
object Routes {
    const val SPLASH = "splash"
    const val SYNC_PATTERN = "sync/{origin}"
    const val NAME_LIST = "name_list"
    const val SETTINGS = "settings"

    fun sync(origin: SyncOrigin): String = "sync/${origin.name}"
}
