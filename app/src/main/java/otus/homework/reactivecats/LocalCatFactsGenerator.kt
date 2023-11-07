package otus.homework.reactivecats

import android.content.Context
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import kotlin.random.Random

class LocalCatFactsGenerator(
    private val context: Context
) {
    private val facts by lazy {
        context.resources.getStringArray(R.array.local_cat_facts)
    }


    /**
     * Реализуйте функцию otus.homework.reactivecats.LocalCatFactsGenerator#generateCatFact так,
     * чтобы она возвращала Fact со случайной строкой  из массива строк R.array.local_cat_facts
     * обернутую в подходящий стрим(Flowable/Single/Observable и т.п)
     */
    fun generateCatFact(): Single<Fact> {
        val fact = Fact(facts[Random.nextInt(facts.size)])
        return Single.create { emitter ->
            emitter.onSuccess(fact)
        }
    }

    /**
     * Реализуйте функцию otus.homework.reactivecats.LocalCatFactsGenerator#generateCatFactPeriodically так,
     * чтобы она эмитила Fact со случайной строкой из массива строк R.array.local_cat_facts каждые 2000 миллисекунд.
     * Если вновь заэмиченный Fact совпадает с предыдущим - пропускаем элемент.
     */

    fun generateCatFactPeriodically(): Observable<Fact> {
        var oldFact = Fact(facts[Random.nextInt(facts.size)])
        return Observable.interval(DELAY_TIMEOUT, TimeUnit.MILLISECONDS, Schedulers.io())
            .map {
                var randomFact = oldFact
                while (randomFact == oldFact) {
                    randomFact = Fact(facts[Random.nextInt(facts.size)])
                }
                oldFact = randomFact
                randomFact
            }
    }

    companion object{
        private const val DELAY_TIMEOUT = 2000L
    }
}