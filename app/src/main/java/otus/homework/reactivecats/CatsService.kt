package otus.homework.reactivecats


import io.reactivex.rxjava3.core.Observable
import retrofit2.http.GET


interface CatsService {
    @GET(" ")
    fun getCatFact(): Observable<Fact>
}