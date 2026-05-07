package es.mundodolphins.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import es.mundodolphins.app.di.IoDispatcher
import es.mundodolphins.app.models.historical.HistoricalGame
import es.mundodolphins.app.models.historical.HistoricalSeasonDetail
import es.mundodolphins.app.models.historical.HistoricalSeasonSummary
import es.mundodolphins.app.repository.HistoricalRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

sealed interface HistoricalUiState<out T> {
    data object Loading : HistoricalUiState<Nothing>

    data class Success<T>(
        val data: T,
    ) : HistoricalUiState<T>

    data class Error(
        val message: String? = null,
    ) : HistoricalUiState<Nothing>
}

@HiltViewModel
class HistoricalSeasonsViewModel
    @Inject
    constructor(
        private val historicalRepository: HistoricalRepository,
        @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    ) : ViewModel() {
        private val _uiState = MutableStateFlow<HistoricalUiState<List<HistoricalSeasonSummary>>>(HistoricalUiState.Loading)
        val uiState: StateFlow<HistoricalUiState<List<HistoricalSeasonSummary>>> = _uiState.asStateFlow()

        private var hasLoadedOnce = false

        fun loadSeasons(force: Boolean = false) {
            if (!force && hasLoadedOnce && _uiState.value is HistoricalUiState.Success) return

            viewModelScope.launch {
                _uiState.value = HistoricalUiState.Loading
                runCatching {
                    withContext(ioDispatcher) { historicalRepository.getSeasons(force) }
                }.onSuccess { seasons ->
                    hasLoadedOnce = true
                    _uiState.value = HistoricalUiState.Success(seasons)
                }.onFailure { throwable ->
                    _uiState.value = HistoricalUiState.Error(throwable.message)
                }
            }
        }
    }

@HiltViewModel
class SeasonDetailViewModel
    @Inject
    constructor(
        private val historicalRepository: HistoricalRepository,
        @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    ) : ViewModel() {
        private val _uiState = MutableStateFlow<HistoricalUiState<HistoricalSeasonDetail>>(HistoricalUiState.Loading)
        val uiState: StateFlow<HistoricalUiState<HistoricalSeasonDetail>> = _uiState.asStateFlow()

        private var lastYear: Int? = null

        fun loadSeason(
            year: Int,
            force: Boolean = false,
        ) {
            if (!force && lastYear == year && _uiState.value is HistoricalUiState.Success) return

            viewModelScope.launch {
                _uiState.value = HistoricalUiState.Loading
                runCatching {
                    withContext(ioDispatcher) { historicalRepository.getSeasonDetail(year, force) }
                }.onSuccess { season ->
                    lastYear = year
                    _uiState.value = HistoricalUiState.Success(season)
                }.onFailure { throwable ->
                    _uiState.value = HistoricalUiState.Error(throwable.message)
                }
            }
        }
    }

@HiltViewModel
class GameDetailViewModel
    @Inject
    constructor(
        private val historicalRepository: HistoricalRepository,
        @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    ) : ViewModel() {
        private val _uiState = MutableStateFlow<HistoricalUiState<HistoricalGame>>(HistoricalUiState.Loading)
        val uiState: StateFlow<HistoricalUiState<HistoricalGame>> = _uiState.asStateFlow()

        private var lastKey: Pair<Int, String>? = null

        fun loadGame(
            year: Int,
            gameId: String,
            force: Boolean = false,
        ) {
            if (!force && lastKey == year to gameId && _uiState.value is HistoricalUiState.Success) return

            viewModelScope.launch {
                _uiState.value = HistoricalUiState.Loading
                runCatching {
                    withContext(ioDispatcher) { historicalRepository.getGame(year, gameId, force) }
                }.onSuccess { game ->
                    if (game == null) {
                        _uiState.value = HistoricalUiState.Error()
                    } else {
                        lastKey = year to gameId
                        _uiState.value = HistoricalUiState.Success(game)
                    }
                }.onFailure { throwable ->
                    _uiState.value = HistoricalUiState.Error(throwable.message)
                }
            }
        }
    }
