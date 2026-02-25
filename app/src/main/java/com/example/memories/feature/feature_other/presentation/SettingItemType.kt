package com.example.memories.feature.feature_other.presentation

import android.Manifest.permission_group.STORAGE
import androidx.annotation.DrawableRes
import com.example.memories.R

interface SettingItemType {
    val title: String
    val description: String

    @get:DrawableRes
    val icon: Int
    val onClickEvent: SettingClickEvent
}


enum class GeneralSettingType(
    override val title: String,
    override val description: String,
    override val icon: Int,
    override val onClickEvent: SettingClickEvent
) : SettingItemType {
    NOTIFICATION(
        "Notifications", "Manage Your Notifications", R.drawable.ic_notification,
        SettingClickEvent.NOTIFICATION_ITEM_CLICK
    ),
    STORAGE(
        "Storage", "View app's storage information", R.drawable.ic_storage,
        SettingClickEvent.STORAGE_ITEM_CLICK
    ),
    DATABASE_BACKUP(
        "Database backup", "Take database backup", R.drawable.ic_database_backup,
        SettingClickEvent.DATABASE_BACKUP_ITEM_CLICK
    ),
    THEME(
        "Change Theme", "Toggle between light and dark theme", R.drawable.ic_theme,
        SettingClickEvent.THEME_ITEM_CLICK
    ),
    TAG_INFO(
        "Tags Info", "Check and edit your created tags", R.drawable.ic_tag,
        SettingClickEvent.TAG_ITEM_CLICK
    ),
    HISTORY(
        "View Past Memories", "Relive your cherished moments", R.drawable.ic_history,
        SettingClickEvent.HISTORY_ITEM_CLICK
    ),
    DELETE_ALL_DATA(
        "Delete All Data", "Delete the entire data you have created", R.drawable.ic_delete,
        SettingClickEvent.DELETE_ALL_DATA_ITEM_CLICK
    ),
    HIDDEN_MEMORIES(
        "Hidden Memories", "View your hidden memories", R.drawable.ic_hidden,
        SettingClickEvent.HIDDEN_MEMORY_ITEM_CLICK
    )

}

enum class PrivacySettingType(
    override val title: String,
    override val description: String,
    override val icon: Int,
    override val onClickEvent: SettingClickEvent
) : SettingItemType {
    HIDDEN_MEMORIES_SETTING(
        title = "Hidden Memories Security",
        description = "Choose how to protect access to your hidden memories",
        icon = R.drawable.ic_lock,
        onClickEvent = SettingClickEvent.HIDDEN_ITEM_CLICK
    ),

}

enum class AppInfoSettingType(
    override val title: String,
    override val description: String,
    override val icon: Int,
    override val onClickEvent: SettingClickEvent
) : SettingItemType {
    APP_VERSION(
        "App Version", "View app's version", R.drawable.ic_app_version,
        SettingClickEvent.ABOUT_ITEM_CLICK
    ),
    DEVELOPER_INFO(
        "Developer Info", "View developer info", R.drawable.ic_developer,
        SettingClickEvent.DEVELOPER_INFO_ITEM_CLICK
    )
}


enum class SettingClickEvent {
    NOTIFICATION_ITEM_CLICK,
    STORAGE_ITEM_CLICK,
    DATABASE_BACKUP_ITEM_CLICK,
    THEME_ITEM_CLICK,
    TAG_ITEM_CLICK,
    HISTORY_ITEM_CLICK,
    DELETE_ALL_DATA_ITEM_CLICK,
    HIDDEN_MEMORY_ITEM_CLICK,
    HIDDEN_ITEM_CLICK,
    ABOUT_ITEM_CLICK,
    DEVELOPER_INFO_ITEM_CLICK
}





