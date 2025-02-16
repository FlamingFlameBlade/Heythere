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
    )

    private val fractionsQuestions = listOf(
        Question("What is 1/2 + 1/4?", "3/4", listOf("1/2", "3/4", "2/3", "5/4")),
        Question("What is 3/5 - 1/5?", "2/5", listOf("1/5", "2/5", "3/5", "4/5")),
        Question("What is 1/3 + 2/3?", "1", listOf("1/2", "1", "3/2", "2")),
        Question("What is 5/8 - 3/8?", "2/8", listOf("1/8", "2/8", "3/8", "4/8")),
        Question("What is 7/10 + 2/10?", "9/10", listOf("8/10", "9/10", "10/10", "7/10")),
        Question("What is 1/2 - 1/3?", "1/6", listOf("1/3", "1/6", "1/4", "1/2")),
        Question("What is 2/3 + 1/3?", "1", listOf("2/3", "1", "3/3", "4/3")),
        Question("What is 3/4 - 1/4?", "1/2", listOf("1/4", "1/2", "3/4", "1")),
        Question("What is 4/5 + 1/5?", "1", listOf("4/5", "1", "5/5", "6/5")),
        Question("What is 7/9 - 2/9?", "5/9", listOf("4/9", "5/9", "6/9", "7/9"))
    )

    private val geometryQuestions = listOf(
        Question("What is the area of a square with side 4?", "16", listOf("8", "12", "16", "20")),
        Question("What is the perimeter of a triangle with sides 3, 4, 5?", "12", listOf("10", "11", "12", "13")),
        Question("What is the area of a circle with radius 3?", "28.27", listOf("15.7", "28.27", "30.5", "18")),
        Question("What is the volume of a cube with side 2?", "8", listOf("6", "8", "10", "12")),
        Question("What is the area of a rectangle with length 5 and width 3?", "15", listOf("8", "15", "12", "18")),
        Question("What is the circumference of a circle with radius 7?", "43.96", listOf("42", "43.96", "44", "50")),
        Question("What is the area of a triangle with base 6 and height 4?", "12", listOf("10", "12", "14", "16")),
        Question("What is the surface area of a cube with side 3?", "54", listOf("27", "54", "81", "36")),
        Question("What is the volume of a cylinder with radius 2 and height 5?", "62.83", listOf("60", "62.83", "63", "70")),
        Question("What is the area of a parallelogram with base 8 and height 3?", "24", listOf("20", "22", "24", "26"))
    )

    private val complexDivisionQuestions = listOf(
        Question("What is 128 ÷ 4?", "32", listOf("30", "31", "32", "33")),
        Question("What is 252 ÷ 6?", "42", listOf("40", "41", "42", "43")),
        Question("What is 144 ÷ 12?", "12", listOf("10", "11", "12", "13")),
        Question("What is 98 ÷ 7?", "14", listOf("12", "13", "14", "15")),
        Question("What is 221 ÷ 13?", "17", listOf("15", "16", "17", "18")),
        Question("What is 345 ÷ 5?", "69", listOf("67", "68", "69", "70")),
        Question("What is 420 ÷ 14?", "30", listOf("28", "29", "30", "31")),
        Question("What is 196 ÷ 14?", "14", listOf("12", "13", "14", "15")),
        Question("What is 289 ÷ 17?", "17", listOf("15", "16", "17", "18")),
        Question("What is 1024 ÷ 8?", "128", listOf("120", "124", "128", "132"))
    )

    private val complexMultiplicationQuestions = listOf(
        Question("What is 12 x 13?", "156", listOf("144", "150", "156", "160")),
        Question("What is 23 x 15?", "345", listOf("340", "345", "350", "355")),
        Question("What is 19 x 14?", "266", listOf("260", "264", "266", "270")),
        Question("What is 16 x 17?", "272", listOf("270", "272", "274", "276")),
        Question("What is 21 x 18?", "378", listOf("370", "374", "378", "382")),
        Question("What is 32 x 25?", "800", listOf("790", "800", "810", "820")),
        Question("What is 29 x 19?", "551", listOf("540", "550", "551", "560")),
        Question("What is 35 x 22?", "770", listOf("760", "770", "780", "790")),
        Question("What is 28 x 24?", "672", listOf("660", "670", "672", "680")),
        Question("What is 45 x 36?", "1620", listOf("1600", "1610", "1620", "1630"))
    )

    private val preAlgebraQuestions = listOf(
        Question("Solve for x: 2x + 3 = 7", "2", listOf("1", "2", "3", "4")),
        Question("Solve for x: 3x - 5 = 10", "5", listOf("4", "5", "6", "7")),
        Question("Solve for x: 5x + 2 = 17", "3", listOf("2", "3", "4", "5")),
        Question("Solve for x: 4x - 8 = 16", "6", listOf("5", "6", "7", "8")),
        Question("Solve for x: 6x + 9 = 21", "2", listOf("1", "2", "3", "4")),
        Question("Solve for x: 7x - 14 = 35", "7", listOf("6", "7", "8", "9")),
        Question("Solve for x: 8x + 16 = 48", "4", listOf("3", "4", "5", "6")),
        Question("Solve for x: 9x - 27 = 54", "9", listOf("8", "9", "10", "11")),
        Question("Solve for x: 10x + 5 = 55", "5", listOf("4", "5", "6", "7")),
        Question("Solve for x: 11x - 22 = 44", "6", listOf("5", "6", "7", "8"))
    )

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