package com.lightricks.feedexercise.ui.feed


import androidx.lifecycle.*
import com.lightricks.feedexercise.data.FeedItem
import com.lightricks.feedexercise.network.Constant.BASE_THUMBNAIL_URL
import com.lightricks.feedexercise.network.FeedApiResponseGenerator
import com.lightricks.feedexercise.network.FeedApiService
import com.lightricks.feedexercise.network.TemplatesMetadata
import com.lightricks.feedexercise.network.TemplatesMetadataItem
import com.lightricks.feedexercise.util.Event
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

/**
 * This view model manages the data for [FeedFragment].
 */
open class FeedViewModel () : ViewModel() {


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

    private fun fetchFeedItems(){
        val feedApiService : FeedApiService = FeedApiResponseGenerator.createFeedApiService()
        val subscribe  = feedApiService.getFeedData()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ feedResponse ->
                handleResponse(feedResponse)
            }, { error ->
                handleNetworkError(error)
            })
        compositeDisposable.add(subscribe)
    }

    fun handleNetworkError(error: Throwable){
        val msg : String = error.message ?: "Unexpected error"
        networkErrorEvent.postValue(Event(msg))
    }

    fun handleResponse(feedResponse: TemplatesMetadata) {
        val rawResponseList = feedResponse.templatesMetadata
        val feedItemLst : MutableList<FeedItem> = mutableListOf()
        for(item in rawResponseList){
            val currItem = FeedItem(item.id, responseUrlToThumbnailUrl(item) ,item.isPremium)
            feedItemLst.add(currItem)
        }
        updateState{ State(feedItemLst, false) }
    }


    fun getIsEmpty(): LiveData<Boolean> {
        return stateInternal.map { state -> state.feedItems?.isEmpty() ?: true }
    }

    fun getFeedItems(): LiveData<List<FeedItem>> {
        return stateInternal.map { state -> state.feedItems ?: emptyList() }
    }

    fun getNetworkErrorEvent(): LiveData<Event<String>> = networkErrorEvent

    init {
        refresh()
    }

    fun refresh() {
        fetchFeedItems()
    }

    private fun updateState(transform: State.() -> State) {
        stateInternal.value = transform(getState())
    }

    private fun getState(): State {
        return stateInternal.value!!
    }

    data class State(
        val feedItems: List<FeedItem>?,
        val isLoading: Boolean)

    companion object {
        private val DEFAULT_STATE = State(
            feedItems = null,
            isLoading = false)
    }
}

/**
 * This class creates instances of [FeedViewModel].
 * It's not necessary to use this factory at this stage. But if we will need to inject
 * dependencies into [FeedViewModel] in the future, then this is the place to do it.
 */
class FeedViewModelFactory (): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (!modelClass.isAssignableFrom(FeedViewModel::class.java)) {
            throw IllegalArgumentException("factory used with a wrong class")
        }
        @Suppress("UNCHECKED_CAST")
        return FeedViewModel() as T
    }
}

fun responseUrlToThumbnailUrl(item : TemplatesMetadataItem) : String {
    return BASE_THUMBNAIL_URL + item.templateThumbnailURI
}