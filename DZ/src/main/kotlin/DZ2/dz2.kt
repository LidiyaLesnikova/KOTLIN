package DZ2

/*
1. Создайте иерархию sealed классов, которые представляют собой команды. В корне иерархии интерфейс Command.
2. В каждом классе иерархии должна быть функция isValid(): Boolean, которая возвращает true, если
команда введена с корректными аргументами. Проверку телефона и email нужно перенести в эту функцию.
3. Напишите функцию readCommand(): Command, которая читает команду из текстового ввода, распознаёт её и
возвращает один из классов наследников Command, соответствующий введённой команде.
4. Создайте data класс Person, который представляет собой запись о человеке. Этот класс должен содержать поля:
● name – имя человека
● phone – номер телефона
● email – адрес электронной почты
5. Добавьте новую команду show, которая выводит последнее значение, введённой с помощью команды add. Для этого
значение должно быть сохранено в переменную типа Person. Если на момент выполнения команды show не было
ничего введено, нужно вывести на экран сообщение “Not initialized”.
6. Функция main должна выглядеть следующем образом. Для каждой команды от пользователя:
a. Читаем команду с помощью функции readCommand
b. Выводим на экран получившийся экземпляр Command
c. Если isValid для команды возвращает false, выводим help. Если true, обрабатываем команду внутри when.
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

data class Person(var name: String? = null, var phone: String? = null, var email: String? = null) {
    var listPerson = mutableMapOf(name to arrayOf(phone, email))

    fun getContactInfomation(key: String): Array<String?>? {
        return listPerson.get(key)
    }

    fun setListPerson(namePerson: String, contInf: Array<String?>) {
        listPerson.put(namePerson, contInf)
    }

    override fun toString(): String {
        return "${listPerson.forEach { t, u -> println("name: "+t.toString()+", phone: "+u.get(0).toString()+", email: "+u.get(1).toString())}}"
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
                    " show <Имя> (or <ALL>) \n" +
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
                            "PHONE - ${person.getContactInfomation(answer[1])?.get(0)}\n" +
                            "EMAIL - ${person.getContactInfomation(answer[1])?.get(1)}\n")
            }
            is Add -> {
                if (answer[2] == "PHONE") {
                    val email = person.getContactInfomation(answer[1])?.get(1).toString()
                    person.setListPerson(answer[1], arrayOf(answer[3], email))
                    println("Contact information added\n")
                } else if (answer[2] == "EMAIL") {
                    val phone = person.getContactInfomation(answer[1])?.get(0).toString()
                    person.setListPerson(answer[1], arrayOf(phone, answer[3]))
                    println("Contact information added\n")
                } else println("Command input error, try again")
            }
        }
    }
}