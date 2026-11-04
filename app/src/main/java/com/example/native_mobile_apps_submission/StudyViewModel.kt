package com.example.native_mobile_apps_submission

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class StudyViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = application.getSharedPreferences("StudyPrefs", Context.MODE_PRIVATE)

    private val _uiState = MutableStateFlow(StudyUiState())
    val uiState: StateFlow<StudyUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null

    init {
        loadTotalStudiedTime()
        fetchMotivationalQuote()
    }

    private fun loadTotalStudiedTime() {
        val total = prefs.getLong("total_studied_millis", 0L)
        _uiState.update { it.copy(totalStudiedMillis = total) }
    }

    fun fetchMotivationalQuote() {
        viewModelScope.launch {
            try {
                val response = QuoteApiService.instance.getRandomQuote()
                _uiState.update {
                    it.copy(
                        motivationalQuote = response.quote,
                        quoteAuthor = response.author
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        motivationalQuote = "Keep pushing forward! Consistency is key.",
                        quoteAuthor = "Study Reminder"
                    )
                }
            }
        }
    }

    private fun saveStudiedTime(elapsedMillis: Long) {
        val currentTotal = prefs.getLong("total_studied_millis", 0L)
        val newTotal = currentTotal + elapsedMillis
        prefs.edit().putLong("total_studied_millis", newTotal).apply()
        _uiState.update { it.copy(totalStudiedMillis = newTotal) }
    }

    fun onMinutesInputChanged(input: String) {
        _uiState.update { it.copy(minutesInput = input, errorMessage = null) }
    }

    fun confirmAndStartTimer() {
        val mins = _uiState.value.minutesInput.toLongOrNull()
        if (mins == null || mins <= 0) {
            _uiState.update { it.copy(errorMessage = "Please enter a valid number greater than 0") }
        } else if (mins > 60) {
            _uiState.update { it.copy(errorMessage = "Timer limit cannot exceed 60 minutes (1 hour)") }
        } else {
            val millis = mins * 60 * 1000L
            _uiState.update {
                it.copy(
                    selectedTimeMillis = millis,
                    currentTime = millis,
                    errorMessage = null,
                    isTimerRunning = false
                )
            }
            startTimer()
        }
    }

    fun toggleTimer() {
        val currentState = _uiState.value
        if (currentState.isTimerRunning) {
            pauseTimer()
        } else {
            if (currentState.currentTime <= 0L) {
                currentState.selectedTimeMillis?.let { total ->
                    _uiState.update { it.copy(currentTime = total) }
                }
            }
            startTimer()
        }
    }

    private fun startTimer() {
        timerJob?.cancel()
        _uiState.update { it.copy(isTimerRunning = true) }
        timerJob = viewModelScope.launch {
            while (_uiState.value.isTimerRunning && _uiState.value.currentTime > 0L) {
                delay(100L)
                _uiState.update { state ->
                    val nextTime = state.currentTime - 100L
                    if (nextTime <= 0L) {
                        val total = state.selectedTimeMillis ?: 0L
                        saveStudiedTime(total)
                        state.copy(isTimerRunning = false, currentTime = 0L)
                    } else {
                        state.copy(currentTime = nextTime)
                    }
                }
            }
        }
    }

    fun pauseTimer() {
        timerJob?.cancel()
        _uiState.update { it.copy(isTimerRunning = false) }
    }

    fun toggleMute() {
        _uiState.update { it.copy(isMuted = !it.isMuted) }
    }

    fun setShowStatsDialog(show: Boolean) {
        if (show) {
            loadTotalStudiedTime()
        }
        _uiState.update { it.copy(showStatsDialog = show) }
    }

    fun clearHistory() {
        prefs.edit().putLong("total_studied_millis", 0L).apply()
        _uiState.update { it.copy(totalStudiedMillis = 0L, showStatsDialog = false) }
    }

    fun backToSetup() {
        timerJob?.cancel()
        val currentState = _uiState.value
        val total = currentState.selectedTimeMillis ?: 0L
        val elapsed = total - currentState.currentTime
        if (elapsed > 0) {
            saveStudiedTime(elapsed)
        }

        fetchMotivationalQuote()
        _uiState.update {
            it.copy(
                selectedTimeMillis = null,
                isTimerRunning = false,
                currentTime = 0L,
                minutesInput = ""
            )
        }
    }
}