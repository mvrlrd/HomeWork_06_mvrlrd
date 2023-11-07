package otus.homework.reactivecats

import com.google.gson.annotations.SerializedName

data class Fact(
    @field:SerializedName("data")
    val data: List<String>
)