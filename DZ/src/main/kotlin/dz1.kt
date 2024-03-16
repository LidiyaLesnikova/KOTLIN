/*Написать программу, которая обрабатывает введённые пользователем в консоль команды:
exit
help
add <Имя> phone <Номер телефона>
add <Имя> email <Адрес электронной почты>
После выполнения команды, кроме команды exit, программа ждёт следующую команду.*/

fun main() {
    println("Enter one of the commands:\n" +
            " exit\n" +
            " help\n" +
            " add <Имя> phone <Номер телефона>\n" +
            " add <Имя> email <Адрес электронной почты>\n")
    val answer = readlnOrNull().toString();
    when (answer) {
        "exit" -> println("Goodbye")
        "help" -> {
            println("Help output\n")
            DZ2.main()
        }
        else -> {
            answerSplit(answer.split(" "))
            DZ2.main()
        }
    }
}

fun answerSplit(args: List<String>) {
    if ((args.size == 4) && (args[0] == "add") && (args[2] == "phone" || args[2] == "email"))
        if ((args[2]=="phone" && args[3].matches(Regex("""\+[0-9]+"""))) ||
            (args[2]=="email" && args[3].matches(Regex("""\w+@[a-zA-Z_]+?\.[a-zA-Z]{2,6}"""))))
            println("Contact information ${args[1]}:  ${args[2]} ${args[3]}\n")
        else println("Incorrect contact information format")
    else println("Command input error, try again")
}
