package otus.homework.reactivecats

import retrofit2.http.GET
import rx.Observable

interface CatsService {

    @GET("random?animal_type=cat")
    fun getCatFact(): Observable<Fact>
}