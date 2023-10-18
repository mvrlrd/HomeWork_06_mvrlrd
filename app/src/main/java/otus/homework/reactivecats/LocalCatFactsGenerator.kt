package otus.homework.reactivecats

import android.content.Context
import io.reactivex.Flowable
import io.reactivex.Single
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
    fun generateCatFactPeriodically(): Flowable<Fact> {
        val success = Fact(facts[Random.nextInt(facts.size)])
        return Flowable.generate() { emitter ->
            while (true){
                emitter.onNext(success)
                Thread.sleep(DELAY_TIMEOUT)
            }
        }
    }

    companion object{
        private const val DELAY_TIMEOUT = 2000L
    }
}