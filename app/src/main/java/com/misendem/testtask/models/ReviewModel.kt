package com.misendem.testtask.models

import com.google.gson.annotations.SerializedName

class ReviewModel(
    @SerializedName("review")
    var review:S
)
class S(
@SerializedName("id")
    var id: Int,
@SerializedName("status_title")
    var status_title: String,
@SerializedName("days_to_publish")
    var days_to_publish: Int,
@SerializedName("properties")
    var properties: ArrayList<Properties>,
//@SerializedName("order")
    var order: Order
)

class Order(

    var id: Int,
    var date_begin: String,
    var date_end: String,
    var guests: Int,
    @SerializedName("object")
    var home: Home

)

class Home(
   var id: Int,
   var address: String,
   var city: String,
   var type: String
)


class Properties(
    var group_name: String,
    var grop_title: String,
    var items: ArrayList<Item>
)

class Item(
    var key: String,
    var title: String,
    var valueRating: Float?,
    var valueComment: String?
)

enum class GroupName{
    review_ratings_order,
    review_text_order
}