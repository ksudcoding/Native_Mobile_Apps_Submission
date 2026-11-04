package com.example.native_mobile_apps_submission

data class StudyUiState(
    val selectedTimeMillis: Long? = null,
    val minutesInput: String = "",
    val errorMessage: String? = null,
    val currentTime: Long = 0L,
    val isTimerRunning: Boolean = false,
    val isMuted: Boolean = false,
    val totalStudiedMillis: Long = 0L,
    val showStatsDialog: Boolean = false,
    val motivationalQuote: String = "Fetching study inspiration from the web...",
    val quoteAuthor: String = ""
)