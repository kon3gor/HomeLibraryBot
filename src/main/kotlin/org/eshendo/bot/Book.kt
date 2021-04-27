package org.eshendo.bot

class Book(
    val title: String,
    val author: String,
    val index: String
){
    override fun toString(): String {
        return "$title\n$index"
    }
}