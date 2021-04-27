package org.eshendo.bot

import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.dispatch
import com.github.kotlintelegrambot.dispatcher.command
import com.github.kotlintelegrambot.dispatcher.text
import com.github.kotlintelegrambot.entities.ChatId
import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import java.io.FileInputStream
import java.io.InputStream

fun main(){

    println("started")

    val booksBot = BooksBot()

    val bot = bot {
        token = TG_TOKEN
        dispatch {
            text {
                if (booksBot.waitingForBookForm){
                    booksBot.addBook(
                        message.text ?: "",
                        { result -> bot.sendMessage(chatId = ChatId.fromId(message.chat.id), text = result) },
                        { error -> bot.sendMessage(chatId = ChatId.fromId(message.chat.id), text = error) })
                }
                else if (message.text?.startsWith("/") != true){
                    bot.sendMessage(ChatId.fromId(message.chat.id), text = "Не понимаю :(")
                }
            }
            command("start") {
                bot.sendMessage(chatId = ChatId.fromId(message.chat.id),
                    text = "Привет! Я бот-помощник для поиска книг в квартире одного крутого разработчика. Чем могу помочь?)")
            }
            command("findbyauthor"){
                val joinedArgs = args.joinToString(" ")
                booksBot.findByAuthor(
                    joinedArgs,
                    { result -> bot.sendMessage(chatId = ChatId.fromId(message.chat.id), text = result) },
                    { error -> bot.sendMessage(chatId = ChatId.fromId(message.chat.id), text = error) }
                )
            }

            command("findbytitle"){
                val joinedArgs = args.joinToString(" ")
                booksBot.findByTitle(
                    joinedArgs,
                    { result -> bot.sendMessage(chatId = ChatId.fromId(message.chat.id), text = result) },
                    { error -> bot.sendMessage(chatId = ChatId.fromId(message.chat.id), text = error) }
                )
            }
            command("add"){
                bot.sendMessage(chatId = ChatId.fromId(message.chat.id), text = """
                    Чтобы добавить книгу отправьте мне сообщение в следующем формате:
                    - Название книги
                    - Автор
                    - Индекс
                    - Ключ
                """.trimIndent())
                booksBot.waitingForBookForm = true
            }
            command("all"){
                booksBot.findAllBooks(
                    { result -> bot.sendMessage(chatId = ChatId.fromId(message.chat.id), text = result) },
                    { error -> bot.sendMessage(chatId = ChatId.fromId(message.chat.id), text = error) }
                )
            }
            command("help"){
                bot.sendMessage(chatId = ChatId.fromId(message.chat.id), text = HELP)
            }
        }
    }

    bot.startPolling()
}

