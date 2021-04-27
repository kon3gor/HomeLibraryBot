package org.eshendo.bot

import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.firestore.Firestore
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.cloud.FirestoreClient
import me.xdrop.fuzzywuzzy.FuzzySearch
import java.io.FileInputStream
import java.util.concurrent.Executor
import java.util.concurrent.ExecutorService

enum class RequestType{
    AUTHOR, TITLE, ALL
}

data class Request(
    val type: RequestType,
    val text: String
)

typealias RequestCallBack = (result: List<Book>?, error: String? ) -> Unit

class BotBackend{

    private val db: Firestore

    init {
        val accountService = FileInputStream(BotBackend::class.java.getResource("/key.json").path)
        val credentials = GoogleCredentials.fromStream(accountService)
        val options = FirebaseOptions.builder()
            .setCredentials(credentials)
            .build()
        FirebaseApp.initializeApp(options)
        db = FirestoreClient.getFirestore()
    }

    fun addBook(book: Book){
        db.collection("books").document().set(book)
    }

    fun find(request: Request, callback: RequestCallBack){
        val result = when(request.type){
            RequestType.AUTHOR -> findByAuthor(request.text)
            RequestType.TITLE -> findByTitle(request.text)
            RequestType.ALL -> findAll()
        }

        result?.let { callback(it, null) } ?: callback(null, "Ничего не нашел :(")
    }

    private fun findAll() : List<Book>?{
        val result = arrayListOf<Book>()
        db.collection("books")
            .get()
            .get()
            .documents.apply {
                forEach { doc ->
                    val title = doc.getString("title") ?: ""
                    val author = doc.getString("author") ?: ""
                    val index = doc.getString("index") ?: ""
                    result.add(Book(title, author, index))
                }
            }

        return if (result.isEmpty()) null else result
    }

    private fun findByAuthor(text: String) : List<Book>?{
        val result = arrayListOf<Book>()
        db.collection("books")
                .get()
                .get()
                .documents.apply {
                    forEach { doc ->
                        val title = doc.getString("title") ?: ""
                        val author = doc.getString("author") ?: ""
                        val index = doc.getString("index") ?: ""

                        if (isSimilar(author, text)){
                            result.add(Book(title, author, index))
                        }
                    }
                }

        return if (result.isEmpty()) null else result
    }

    private fun isSimilar(text: String, target: String) : Boolean{
        val res = FuzzySearch.weightedRatio(target, text)
        println(res)
        println(text)
        println(target)
        return res >= 60
    }

    private fun findByTitle(text: String) : List<Book>?{
        val result = arrayListOf<Book>()
        db.collection("books")
                .get()
                .get()
                .documents.apply {
                    forEach { doc ->
                        val title = doc.getString("title") ?: ""
                        val author = doc.getString("author") ?: ""
                        val index = doc.getString("index") ?: ""

                        if (isSimilar(title, text)){
                            result.add(Book(title, author, index))
                        }
                    }
                }

        return if (result.isEmpty()) null else result
    }
}