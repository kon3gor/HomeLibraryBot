package org.eshendo.bot

import java.lang.Exception
import java.util.concurrent.atomic.AtomicBoolean

class BooksBot {

    private val backend = BotBackend()

    private val _waitingForBookForm = AtomicBoolean(false)
    var waitingForBookForm: Boolean
        get() = _waitingForBookForm.get()
        set(value) = _waitingForBookForm.set(value)

    fun findByAuthor(
        author: String,
        success: (result: String) -> Unit,
        error: (error: String) -> Unit
    ){
        val callback = { result: List<Book>?, error: String?  ->
            if (result != null){
                val response = "Вот что я нашел:\n\n${result.joinToString("\n\n")}"
                success(response)
            }else{
                error(error!!)
            }
            Unit
        }

        val request = Request(RequestType.AUTHOR, author)
        backend.find(request, callback)
    }

    fun findByTitle(
        title: String,
        success: (result: String) -> Unit,
        error: (error: String) -> Unit
    ){
        val callback = { result: List<Book>?, error: String?  ->
            if (result != null){
                val response = "Вот что я нашел:\n\n${result.joinToString("\n\n")}"
                success(response)
            }else{
                error(error!!)
            }
            Unit
        }

        val request = Request(RequestType.TITLE, title)
        backend.find(request, callback)
    }

    fun addBook(
        form: String,
        success: (result: String) -> Unit,
        error: (error: String) -> Unit
    ){
        _waitingForBookForm.set(false)
        val splited: List<String>
        try {
            splited = form.split("-").subList(1, 5).map { it.trim() }
        }catch (e: Exception){
            error("Неправильные данные :(")
            return
        }
        if (splited.isEmpty()){
            error("Неправильные данные :(")
            return
        }
        if (splited[3] != KEY){
            error("Неправильный ключ :(")
            return
        }
        val book = Book(splited[0], splited[1], splited[2])
        backend.addBook(book)
        success("Книга добавлена!")
    }

    fun findAllBooks(
        success: (result: String) -> Unit,
        error: (error: String) -> Unit
    ){
        val callback = { result: List<Book>?, error: String?  ->
            if (result != null){
                val response = "Вот что я нашел:\n\n${result.joinToString("\n\n")}"
                success(response)
            }else{
                error(error!!)
            }
            Unit
        }

        val request = Request(RequestType.ALL, "")
        backend.find(request, callback)
    }
}