package com.lightricks.feedexercise.ui.feed
import android.app.Application
import androidx.lifecycle.*
import androidx.room.Room
import com.lightricks.feedexercise.data.FeedItem
import com.lightricks.feedexercise.database.FeedDatabase
import com.lightricks.feedexercise.database.UserProject
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
open class FeedViewModel (application: Application) : AndroidViewModel(application ) {


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
        val feedApiService : FeedApiService = FeedApiResponseGenerator.getFeedApiService()
        val subscribe  = feedApiService.getFeedData()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ feedResponse ->
                handleResponse(feedResponse)
                saveToDb()
            }, { error ->
                handleNetworkError(error)
            })
        compositeDisposable.add(subscribe)
    }

    private fun handleNetworkError(error: Throwable){
        val msg : String = error.message ?: "Unexpected error"
        networkErrorEvent.postValue(Event(msg))
    }

    private fun handleResponse(feedResponse: TemplatesMetadata) {
        val rawResponseList = feedResponse.templatesMetadata
        val feedItemLst : List<FeedItem> = rawResponseList.map {
                item -> FeedItem(item.id, responseUrlToThumbnailUrl(item) ,item.isPremium) }
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

    private fun saveToDb(){
        val DB_NAME = "user_project_db.db" //TODO: verify
        val db = Room.databaseBuilder(getApplication(),FeedDatabase::class.java,DB_NAME).build()
        val userProjectDao = db.userProjectDao()
        val disposable = userProjectDao.insertAll(feedLstToDbLst(stateInternal.value?.feedItems!!))
            .subscribeOn(Schedulers.io()).subscribe({},{ error -> handleNetworkError(error)}) //TODO inline the function call or keep as is for readability?
        compositeDisposable.add(disposable)
    }
}

/**
 * This class creates instances of [FeedViewModel].
 * It's not necessary to use this factory at this stage. But if we will need to inject
 * dependencies into [FeedViewModel] in the future, then this is the place to do it.
 */
class FeedViewModelFactory (val application: Application): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (!modelClass.isAssignableFrom(FeedViewModel::class.java)) {
            throw IllegalArgumentException("factory used with a wrong class")
        }
        @Suppress("UNCHECKED_CAST")
        return FeedViewModel(application) as T
    }
}

fun responseUrlToThumbnailUrl(item : TemplatesMetadataItem) : String {
    return BASE_THUMBNAIL_URL + item.templateThumbnailURI
}

fun feedLstToDbLst(feedItemLst : List<FeedItem>) : List<UserProject>{ //TODO remove? add inline?
    return feedItemLst.map { feedItem ->
        UserProject(feedItem.id,feedItem.thumbnailUrl,feedItem.isPremium) }
}

