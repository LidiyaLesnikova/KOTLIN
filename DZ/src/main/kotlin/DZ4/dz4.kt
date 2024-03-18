package DZ4

import java.io.File
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper

/*
— Добавьте новую команду export, которая экспортирует добавленные значения в текстовый файл в формате JSON.
Команда принимает путь к новому файлу. Например export /Users/user/myfile.json
— Реализуйте DSL на Kotlin, который позволит конструировать JSON и преобразовывать его в строку.
— Используйте этот DSL для экспорта данных в файл.
— Выходной JSON необязательно должен быть отформатирован, поля объектов могут идти в любом порядке.
Главное, чтобы он имел корректный синтаксис.
*/
sealed interface Command {
    fun isValid(): Boolean
}

class Help : Command {
    override fun isValid(): Boolean = true
}

class Show(val args: List<String>) : Command {
    override fun isValid(): Boolean = (args.size == 2)
}

class Exit : Command {
    override fun isValid(): Boolean = true
}

class Add(val args: List<String>) : Command {
    override fun isValid(): Boolean {
        return ((args.size == 4) &&
                ((args[2] == "PHONE" && args[3].matches(Regex("""\+[0-9]+"""))) ||
                (args[2] == "EMAIL" && args[3].matches(Regex("""\w+@[a-zA-Z_]+?\.[a-zA-Z]{2,6}""")))))
    }
}

class Find(val args: List<String>) : Command {
    override fun isValid(): Boolean = (args.size == 2)
}

class Export(val args: List<String>) : Command {
    override fun isValid(): Boolean = (args.size == 2 && !args[1].isEmpty())
}

data class Person(var name: String? = null, var phone: String? = null, var email: String? = null) {
    var listPerson = mutableMapOf(name to arrayOf(mutableListOf(phone), mutableListOf(email)))

    init {
        listPerson.remove(key = null)
    }

    fun getContactInfomation(key: String): Array<MutableList<String?>>? {
        return listPerson[key]
    }

    fun setListPerson(namePerson: String, contInf: Array<MutableList<String?>>) {
        listPerson.put(namePerson, contInf)
    }

    override fun toString(): String {
        return "${listPerson.forEach { t, u -> println("name: "+t.toString()+", phone: "+ u[0].toString()+", email: "+ u[1].toString())}}"
    }
}

fun readCommand(args: List<String>): Command {
    return when (args[0]) {
        "EXIT" -> Exit()
        "HELP" -> Help()
        "SHOW" -> if (Show(args).isValid()) Show(args)
        else {
            println("Command input error, try again")
            Help()
        }
        "ADD" -> if (Add(args).isValid()) Add(args)
        else {
            println("Command input error, try again")
            Help()
        }
        "FIND" -> if (Find(args).isValid()) Find(args)
        else {
            println("Command input error, try again")
            Help()
        }
        "EXPORT" -> if (Export(args).isValid()) Export(args)
        else {
            println("Command input error, try again")
            Help()
        }
        else -> {
            println("Command input error, try again")
            Help()
        }
    }
}

var person = Person()
fun main() {
    var flag = true

    while (flag) {
        println(
            "Enter one of the commands:\n" +
                    " help\n" +
                    " add <Имя> phone <Номер телефона>\n" +
                    " add <Имя> email <Адрес электронной почты>\n" +
                    " show <Имя> (or <ALL>)\n" +
                    " find <строка поиска>\n" +
                    " export <путь к новому файлу> (or './' текущий каталог)\n" +
                    " exit\n"
        )
        val answer = readlnOrNull().toString().uppercase().split(" ");
        val command = readCommand(answer)
        when (command) {
            is Exit -> {
                println("Goodbye")
                flag = false
            }
            is Help -> println("Help output\n")
            is Show -> showContactInformation(answer)
            is Add  -> addContact(answer)
            is Find -> findContact(answer)
            is Export -> exportListContact(answer)
        }
    }
}

fun showContactInformation(answer: List<String>) {
    if (answer[1] == "ALL") person.toString()
    else if (person.getContactInfomation(answer[1]) == null) println("Not initialized\n")
    else println("Contact information ${answer[1]}:\n" +
            "PHONE - ${person.getContactInfomation(answer[1])!![0]}\n" +
            "EMAIL - ${person.getContactInfomation(answer[1])!![1]}\n")
}

fun addContact(answer: List<String>) {
    if (person.getContactInfomation(answer[1])!=null) {
        var phone = person.getContactInfomation(answer[1])!!.get(0)
        var email = person.getContactInfomation(answer[1])!!.get(1)
        if (answer[2] == "PHONE") {
            if (phone.get(0) == null) phone.set(0, answer[3])
            else phone.add(answer[3])

        } else if (answer[2] == "EMAIL") {
            if (email.get(0) == null) email.set(0, answer[3])
            else email.add(answer[3])
        }
        val contactInfomation = arrayOf(phone,email)
        person.setListPerson(answer[1], contactInfomation)
    } else {
        var phone: String? = null
        var email: String? = null
        if (answer[2] == "PHONE") {
            phone = answer[3]
        } else if (answer[2] == "EMAIL") {
            email = answer[3]
        }
        person.setListPerson(answer[1], arrayOf(mutableListOf(phone), mutableListOf(email)))
    }
    println("Contact information added\n")
}

fun findContact(answer: List<String>) {
    val filtrPerson = person.listPerson.filter {person -> person.value[0].filterNotNull().filter { contact -> contact.contains(answer[1]) }.count()>0 ||
            person.value[1].filterNotNull().filter { contact -> contact.contains(answer[1]) }.count()>0}
    if (filtrPerson.isEmpty()) println("No records found\n")
    else filtrPerson.forEach { t, u -> println("name: "+t.toString()+", phone: "+ u[0].toString()+", email: "+ u[1].toString())}
}

fun exportListContact(answer: List<String>) {
    val mapper = jacksonObjectMapper()
    val jsonArray = mapper.writeValueAsString(person.listPerson)
    File(answer[1], "ContactInformation.json").writeText(jsonArray)
    println("The data is saved to a file ${answer[1]}ContactInformation.json:\n$jsonArray\n")
}