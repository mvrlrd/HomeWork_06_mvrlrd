package otus.homework.reactivecats

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.SingleObserver
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import org.reactivestreams.Subscription

import java.util.concurrent.TimeUnit

class CatsViewModel(
    private val catsService: CatsService,
    private val localCatFactsGenerator: LocalCatFactsGenerator,
) : ViewModel() {

    private val _catsLiveData = MutableLiveData<Result>()
    val catsLiveData: LiveData<Result> = _catsLiveData
    private var subscription : Disposable? = null

    init {
        getFacts()
    }

    private fun getFacts() {
        subscription = Observable.interval(DELAY_TIME_IN_SEC, TimeUnit.SECONDS, Schedulers.io())
            .subscribe {
                catsService.getCatFact()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                        {
                            _catsLiveData.value = (Success(it))
                        },
                        {
                        _catsLiveData.postValue(Error(ERROR_MESSAGE_TIMEOUT))
                        generateRandomFact()
                        }
                    )
            }
    }

    private fun generateRandomFact() {
        localCatFactsGenerator.generateCatFact()
            .subscribeOn(Schedulers.io())
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
        subscription?.dispose()
    }

    private fun log(text: String) {
        Log.d(TAG, text)
    }


    companion object {
        private const val ERROR_MESSAGE_TIMEOUT = "Не удалось получить ответ от сервером"
        private const val DELAY_TIME_IN_SEC = 2L
        private const val TAG = "CatsPresenter"


                fun getViewModelFactory(
                    catsService: CatsService,
                    localCatFactsGenerator: LocalCatFactsGenerator,
                ): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {

                // 1
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return CatsViewModel(
                        catsService, localCatFactsGenerator
                    ) as T
                }
            }
    }

}

sealed class Result
data class Success(val fact: Fact) : Result()
data class Error(val message: String) : Result()
object ServerError : Result()