package DZ3

/*
  — Измените класс Person так, чтобы он содержал список телефонов и список почтовых адресов, связанных с человеком.
— Теперь в телефонной книге могут храниться записи о нескольких людях. Используйте для этого наиболее подходящую структуру данных.
— Команда AddPhone теперь должна добавлять новый телефон к записи соответствующего человека.
— Команда AddEmail теперь должна добавлять новый email к записи соответствующего человека.
— Команда show должна принимать в качестве аргумента имя человека и выводить связанные с ним телефоны и адреса электронной почты.
— Добавьте команду find, которая принимает email или телефон и выводит список людей, для которых записано такое значение.
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

data class Person(var name: String? = null, var phone: String? = null, var email: String? = null) {
    var listPerson = mutableMapOf(name to arrayOf(mutableListOf(phone), mutableListOf(email)))

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
        else -> {
            println("Command input error, try again")
            Help()
        }
    }
}

fun main() {
    var flag = true
    var person: Person = Person()

    while (flag) {
        println(
            "Enter one of the commands:\n" +
                    " help\n" +
                    " add <Имя> phone <Номер телефона>\n" +
                    " add <Имя> email <Адрес электронной почты>\n" +
                    " show <Имя> (or <ALL>)\n" +
                    " find <строка поиска>\n" +
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
            is Show -> {
                if (answer[1] == "ALL") person.toString()
                else if (person.getContactInfomation(answer[1]) == null) println("Not initialized\n")
                else println("Contact information ${answer[1]}:\n" +
                            "PHONE - ${person.getContactInfomation(answer[1])!![0]}\n" +
                            "EMAIL - ${person.getContactInfomation(answer[1])!![1]}\n")
            }
            is Add -> {
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
            is Find -> {
                val filtrperson = person.listPerson.filter {person -> person.value[0].filterNotNull().filter { contact -> contact.contains(answer[1]) }.count()>0 ||
                        person.value[1].filterNotNull().filter { contact -> contact.contains(answer[1]) }.count()>0}
                if (filtrperson.isEmpty()) println("No records found\n")
                else filtrperson.forEach { t, u -> println("name: "+t.toString()+", phone: "+ u[0].toString()+", email: "+ u[1].toString())}
            }
        }
    }
}