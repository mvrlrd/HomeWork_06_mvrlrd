package otus.homework.reactivecats

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import rx.Observable
import rx.Subscriber
import rx.Subscription
import rx.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class CatsViewModel(
    private val catsService: CatsService,
    private val localCatFactsGenerator: LocalCatFactsGenerator,
) : ViewModel() {

    private val _catsLiveData = MutableLiveData<Result>()
    val catsLiveData: LiveData<Result> = _catsLiveData

    private var subscription: Subscription? = null

    init {
        getFacts()
    }

    private fun getFacts() {
        Observable.interval(DELAY_TIME_IN_SEC, TimeUnit.SECONDS, Schedulers.io())
            .map { catsService.getCatFact()
                .distinctUntilChanged()
                .observeOn(Schedulers.io())}
            .subscribe(){
                it.subscribe(object : Subscriber<Fact>(){
                    override fun onCompleted() {
                        log("onCompleted")
                    }
                    override fun onError(e: Throwable?) {
                        log("onError $e")
                        _catsLiveData.postValue(Error(ERROR_MESSAGE_TIMEOUT))
                        generateRandomFact()
                    }
                    override fun onNext(t: Fact?) {
                        log("onNext ${t?.text}")
                        t?.let {fact ->
                            log(fact.text)
                            _catsLiveData.postValue(Success(fact))
                        }
                    }
                })
            }
    }

    private fun generateRandomFact() {
        localCatFactsGenerator.generateCatFact()
            .subscribeOn(io.reactivex.schedulers.Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : SingleObserver<Fact> {
                override fun onSubscribe(d: Disposable) {
                    log("onSubscribe")
                }

                override fun onSuccess(t: Fact) {
                    log("onSuccess")
                    _catsLiveData.value = Success(t)
                }

                override fun onError(e: Throwable) {
                    log("onError")
                    _catsLiveData.value = Error("Ошибка")
                }
            })
    }

    override fun onCleared() {
        super.onCleared()
        subscription?.unsubscribe()
    }

    private fun log(text: String) {
        Log.d(TAG, text)
    }


    companion object {
        private const val ERROR_MESSAGE_TIMEOUT = "Не удалось получить ответ от сервером"
        private const val DELAY_TIME_IN_SEC = 2L
        private const val TAG = "CatsPresenter"
    }

}



class CatsViewModelFactory(
    private val catsRepository: CatsService,
    private val localCatFactsGenerator: LocalCatFactsGenerator,
) :
    ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T =
        CatsViewModel(catsRepository, localCatFactsGenerator) as T
}

sealed class Result
data class Success(val fact: Fact) : Result()
data class Error(val message: String) : Result()
object ServerError : Result()