package com.example.mathgame



object QuestionsRepository {

    // Generate addition questions (Numbers 1-50)
    private fun generateAdditionQuestions(count: Int): List<Question> {
        return (1..count).map {
            val num1 = (1..50).random()
            val num2 = (1..50).random()
            val answer = num1 + num2
            Question(
                "What is $num1 + $num2?",
                correctAnswer = answer.toString(), listOf(answer, answer - 1, answer + 1, answer + 2).map { it.toString() }.shuffled()
            )
        }
    }

    // Generate subtraction questions (Numbers 1-50, ensure non-negative results)
    private fun generateSubtractionQuestions(count: Int): List<Question> {
        return (1..count).map {
            val num1 = (1..50).random()
            val num2 = (1..num1).random() // Ensure num1 is larger
            val answer = num1 - num2
            Question(
                "What is $num1 - $num2?",
                correctAnswer = answer.toString(),
                listOf(answer, answer - 1, answer + 1, answer + 2).map { it.toString() }.shuffled()
            )
        }
    }

    // Generate multiplication questions (Smaller numbers 1-12)
    private fun generateMultiplicationQuestions(count: Int): List<Question> {
        return (1..count).map {
            val num1 = (1..12).random()
            val num2 = (1..12).random()
            val answer = num1 * num2
            Question("What is $num1 × $num2?",
                correctAnswer = answer.toString(), listOf(answer, answer - 1, answer + 1, answer + 2).map { it.toString() }.shuffled()
            )
        }
    }

    // Generate division questions (Dividends up to 100, divisors 1-10, clean division)
    private fun generateDivisionQuestions(count: Int): List<Question> {
        return (1..count).map {
            val divisor = (1..10).random()
            val quotient = (1..10).random()
            val dividend = divisor * quotient // Ensures a clean division
            Question("What is $dividend ÷ $divisor?",
                correctAnswer = quotient.toString(), listOf(quotient, quotient - 1, quotient + 1, quotient + 2).map { it.toString() }.shuffled()
            )
        }
    }

    // Store questions separately
    private val additionQuestions = generateAdditionQuestions(50)
    private val subtractionQuestions = generateSubtractionQuestions(50)
    private val multiplicationQuestions = generateMultiplicationQuestions(50)
    private val divisionQuestions = generateDivisionQuestions(50)

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
        val equation = ""+multiplier + "x + " + constant + " = " + (multiplier * x + constant)
        Question("Solve for x: $equation", x.toString(), listOf((x - 1).toString(), x.toString(), (x + 1).toString(), (x + 2).toString()))
    }

    fun getQuestionsForTopic(topic: String): List<Question> {
        return when (topic) {
            "Addition" -> additionQuestions
            "Subtraction" -> subtractionQuestions
            "Multiplication" -> multiplicationQuestions
            "Division" -> divisionQuestions
            "Fractions" -> fractionsQuestions
            "Geometry" -> geometryQuestions
            "Complex Division" -> complexDivisionQuestions
            "Complex Multiplication" -> complexMultiplicationQuestions
            "Pre Algebra" -> preAlgebraQuestions
            else -> emptyList()
        }
    }
}
