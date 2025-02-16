package com.example.mathgame

object QuestionsRepository {

    private val basicMathQuestions = listOf(
        Question("What is 5 + 3?", "8", listOf("6", "7", "8", "9")),
        Question("What is 10 - 4?", "6", listOf("5", "6", "7", "8")),
        Question("What is 7 x 2?", "14", listOf("12", "13", "14", "15")),
        Question("What is 12 ÷ 4?", "3", listOf("2", "3", "4", "5")),
        Question("What is 13 - 6?", "7", listOf("7", "6", "19", "8")),
        Question("What is 20 ÷ 5?", "4", listOf("2", "3", "4", "5")),
        Question("What is 15 + 9?", "24", listOf("22", "23", "24", "25")),
        Question("What is 30 - 18?", "12", listOf("10", "11", "12", "13")),
        Question("What is 9 x 3?", "27", listOf("24", "25", "26", "27")),
        Question("What is 36 ÷ 6?", "6", listOf("4", "5", "6", "7"))
    ) + (1..40).map {
        val num1 = (1..50).random()
        val num2 = (1..50).random()
        val operators = listOf("+", "-", "x", "÷")
        val operator = operators.random()
        val answer = when (operator) {
            "+" -> num1 + num2
            "-" -> num1 - num2
            "x" -> num1 * num2
            "÷" -> if (num2 != 0) num1 / num2 else 1
            else -> 0
        }
        Question("What is $num1 $operator $num2?", answer.toString(), listOf((answer - 1).toString(), (answer + 1).toString(), answer.toString(), (answer + 2).toString()))
    }

    private val fractionsQuestions = listOf(
        Question("What is 1/2 + 1/4?", "3/4", listOf("1/2", "3/4", "2/3", "5/4"))
    ) + (1..40).map {
        val num1 = (1..10).random()
        val denom1 = (2..10).random()
        val num2 = (1..10).random()
        val denom2 = (2..10).random()
        val answer = "${num1 + num2}/${denom1 + denom2}"
        Question("What is $num1/$denom1 + $num2/$denom2?", answer, listOf("$num1/$denom1", answer, "$num2/$denom2", "${num1 - num2}/${denom1 - denom2}"))
    }

    private val geometryQuestions = listOf(
        Question("What is the area of a square with side 4?", "16", listOf("8", "12", "16", "20"))
    ) + (1..40).map {
        val side = (2..15).random()
        val area = side * side
        Question("What is the area of a square with side $side?", area.toString(), listOf((area - 2).toString(), (area + 2).toString(), area.toString(), (area + 4).toString()))
    }

    private val complexDivisionQuestions = listOf(
        Question("What is 128 ÷ 4?", "32", listOf("30", "31", "32", "33"))
    ) + (1..40).map {
        val num1 = (100..1000).random()
        val num2 = (2..20).random()
        val answer = num1 / num2
        Question("What is $num1 ÷ $num2?", answer.toString(), listOf((answer - 1).toString(), answer.toString(), (answer + 1).toString(), (answer + 2).toString()))
    }

    private val complexMultiplicationQuestions = listOf(
        Question("What is 12 x 13?", "156", listOf("144", "150", "156", "160"))
    ) + (1..40).map {
        val num1 = (10..50).random()
        val num2 = (10..50).random()
        val answer = num1 * num2
        Question("What is $num1 x $num2?", answer.toString(), listOf((answer - 10).toString(), (answer + 10).toString(), answer.toString(), (answer + 20).toString()))
    }

    private val preAlgebraQuestions = listOf(
        Question("Solve for x: 2x + 3 = 7", "2", listOf("1", "2", "3", "4"))
    ) + (1..40).map {
        val x = (1..20).random()
        val multiplier = (2..5).random()
        val constant = (1..10).random()
        val equation = "$multiplier x + $constant = ${multiplier * x + constant}"
        Question("Solve for x: $equation", x.toString(), listOf((x - 1).toString(), x.toString(), (x + 1).toString(), (x + 2).toString()))
    }

    fun getQuestionsForTopic(topic: String): List<Question> {
        return when (topic) {
            "Basic Math" -> basicMathQuestions
            "Fractions" -> fractionsQuestions
            "Geometry" -> geometryQuestions
            "Complex Division" -> complexDivisionQuestions
            "Complex Multiplication" -> complexMultiplicationQuestions
            "Pre Algebra" -> preAlgebraQuestions
            else -> emptyList()
        }
    }
}
