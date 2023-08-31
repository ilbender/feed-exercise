package com.lightricks.feedexercise.ui.feed

import androidx.lifecycle.*
import com.lightricks.feedexercise.data.FeedItem
import com.lightricks.feedexercise.data.FeedRepository
import com.lightricks.feedexercise.util.Event
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable


/**
 * This view model manages the data for [FeedFragment].
 */
open class FeedViewModel(private val feedRepository: FeedRepository) : ViewModel() {


    private val stateInternal: MutableLiveData<State> = MutableLiveData<State>(DEFAULT_STATE)
    private val networkErrorEvent = MutableLiveData<Event<String>>()
    private val compositeDisposable = CompositeDisposable()


    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }

    fun getIsLoading(): LiveData<Boolean> {
        return stateInternal.map { state -> state.isLoading }
    }


    private fun handleError(error: Throwable) {
        val msg: String = error.message ?: "Unexpected error"
        networkErrorEvent.postValue(Event(msg))
        updateState { copy(isLoading = false) }
    }


    fun getIsEmpty(): LiveData<Boolean> {
        return stateInternal.map { state -> state.feedItems?.isEmpty() ?: true }
    }

    fun getFeedItems(): LiveData<List<FeedItem>> {
        return stateInternal.map { state -> state.feedItems ?: emptyList() }
    }

    fun getNetworkErrorEvent(): LiveData<Event<String>> = networkErrorEvent

    init {
        val disposableA =  feedRepository.getAllProjects()
            .observeOn(AndroidSchedulers.mainThread()).subscribe({ userProjects ->
            updateState {
                State(
                    userProjects.map { userProject ->
                        FeedItem(userProject.id, userProject.thumbnailUrl, userProject.isPremium)
                    },
                    false
                )
            }
        }, { error ->
            updateState { copy(isLoading = false) }
            handleError(error)
        })
        val disposableB = feedRepository.refresh().subscribe({}, { error -> handleError(error) })
        compositeDisposable.add(disposableA)
        compositeDisposable.add(disposableB)
    }

    fun refresh() {
        updateState { copy(isLoading = true) }
        val disposable = feedRepository.refresh().subscribe({},
            { error ->
                handleError(error)
            })
        compositeDisposable.add(disposable)
    }

    private fun updateState(transform: State.() -> State) {
        stateInternal.value = transform(getState())
    }

    private fun getState(): State {
        return stateInternal.value!!
    }

    data class State(
        val feedItems: List<FeedItem>?,
        val isLoading: Boolean
    )

    companion object {
        private val DEFAULT_STATE = State(
            feedItems = null,
            isLoading = false
        )
    }


}

/**
 * This class creates instances of [FeedViewModel].
 * It's not necessary to use this factory at this stage. But if we will need to inject
 * dependencies into [FeedViewModel] in the future, then this is the place to do it.
 */
class FeedViewModelFactory(val feedRepository: FeedRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (!modelClass.isAssignableFrom(FeedViewModel::class.java)) {
            throw IllegalArgumentException("factory used with a wrong class")
        }
        @Suppress("UNCHECKED_CAST")
        return FeedViewModel(feedRepository) as T
    }
}