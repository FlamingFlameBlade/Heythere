package com.example.mathgame
import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import kotlin.random.Random
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.text.style.TextAlign
import com.example.mathgame.ui.theme.MathGameTheme
import android.content.Context
import android.content.SharedPreferences
import android.os.Handler
import android.os.Looper
import androidx.compose.runtime.*
import kotlinx.coroutines.delay


class MainActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sharedPreferences = getSharedPreferences("math_progress", Context.MODE_PRIVATE)

        enableEdgeToEdge()
        setContent {
            MathGameTheme{
                Scaffold(modifier = Modifier.fillMaxSize()) {
                    MathLearningApp(sharedPreferences)
                }
            }
        }
    }
}



@Composable
fun MathLearningApp(sharedPreferences: SharedPreferences) {
    var currentScreen by remember { mutableStateOf("menu") }
    var currentTopic by remember { mutableStateOf("Basic Math") }

    // Initialize states for all topics
    val topics = listOf(
        "Basic Math", "Fractions", "Geometry",
        "Complex Division", "Complex Multiplication", "Pre Algebra"
    )
    val progressState = remember { mutableStateMapOf<String, Float>() }
    val levelState = remember { mutableStateMapOf<String, Int>() }

    // Load initial values for all topics
    LaunchedEffect(Unit) {
        topics.forEach { topic ->
            progressState[topic] = getProgress(sharedPreferences, topic)
            levelState[topic] = getLevel(sharedPreferences, topic)
        }
    }

    when (currentScreen) {
        "menu" -> MainMenuScreen(
            onNavigate = { topic ->
                currentTopic = topic
                currentScreen = "question"
            },
            progressState = progressState,
            levelState = levelState
        )
        "question" -> MathQuestionScreen(
            topic = currentTopic,
            progress = progressState[currentTopic] ?: 0f,
            level = levelState[currentTopic] ?: 1,
            onCorrectAnswer = {
                val newProgress = (progressState[currentTopic] ?: 0f) + 0.2f
                saveProgress(
                    sharedPreferences,
                    currentTopic,
                    if (newProgress >= 1f) 0f else newProgress
                )
                if (newProgress >= 1f) {
                    val newLevel = (levelState[currentTopic] ?: 1) + 1
                    saveLevel(sharedPreferences, currentTopic, newLevel)
                    levelState[currentTopic] = newLevel
                }
                progressState[currentTopic] = if (newProgress >= 1f) 0f else newProgress
            },
            onBack = { currentScreen = "menu" }
        )
    }
}



@Composable
fun MainMenuScreen(
    onNavigate: (String) -> Unit,
    progressState: Map<String, Float>,
    levelState: Map<String, Int>
) {
    val topics = listOf(
        "Basic Math",
        "Fractions",
        "Geometry",
        "Complex Division",
        "Complex Multiplication",
        "Pre Algebra"
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Text(
                text = "Choose a Math Topic",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier
                    .padding(bottom = 16.dp)
                    .padding(top = 48.dp)
            )
        }
        items(topics.chunked(2)) { rowTopics ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center // This centers the row's content
            ) {
                rowTopics.forEach { topic ->
                    TopicSquare(
                        topic = topic,
                        progress = progressState[topic],
                        level = levelState[topic],
                        onClick = { onNavigate(topic) }
                    )
                }
            }
        }
    }
}

@Composable
fun TopicSquare(topic: String, progress: Float?, level: Int?, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .height(172.dp) // Rectangular shape
            .width(200.dp)
            .padding(10.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD)),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            contentAlignment = Alignment.Center
        ) {
            if (level != null && level > 1 || (progress != null && progress > 0f)) {
                // Show progress bar and level if level > 1 or progress exists
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = topic,
                        fontSize = 20.sp, // Increased font size for better fit
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 8.dp),
                    )

                    Box(
                        modifier = Modifier.size(80.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        // Background for the progress bar
                        CircularProgressIndicator(
                            progress = 1f, // Full circle
                            modifier = Modifier.fillMaxSize(),
                            color = Color(0xFFDCEEEE), // Background color
                            strokeWidth = 8.dp
                        )

                        // Foreground progress bar
                        CircularProgressIndicator(
                            progress = progress ?: 0f,
                            modifier = Modifier.fillMaxSize(),
                            color = Color(0xFF57E8DD), // Foreground color
                            strokeWidth = 8.dp
                        )

                        // Display the level in the center
                        Text(
                            text = "Lv ${level ?: 1}",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }
                }
            } else {
                // Show topic name in the center if no progress and level <= 1
                Text(
                    text = topic,
                    fontSize = 20.sp, // Increased font size for better fit
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}


@Composable
fun MathQuestionScreen(
    topic: String,
    progress: Float,
    level: Int,
    onCorrectAnswer: () -> Unit,
    onBack: () -> Unit
) {
    var showFeedback by remember { mutableStateOf<String?>(null) } // Feedback message
    var isAnsweringEnabled by remember { mutableStateOf(true) } // Enable/disable answering during delay

    // Define the question pools for each topic
    val questions = when (topic) {
        "Basic Math" -> listOf(
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
        "Fractions" -> listOf(
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
        "Geometry" -> listOf(
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
        "Complex Division" -> listOf(
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
        "Complex Multiplication" -> listOf(
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
        "Pre Algebra" -> listOf(
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
        else -> emptyList()
    }

    // Ensure there are questions available
    if (questions.isEmpty()) {
        return // Avoid rendering anything if there are no questions
    }

    // Track the random index for question selection
    var currentQuestionIndex by remember { mutableStateOf(Random.nextInt(questions.size)) }
    val currentQuestion = questions[currentQuestionIndex]

    // Update the random index when the user progresses to the next question
    fun nextQuestion() {val handler = Handler(Looper.getMainLooper())

        // Delay the next question by 1 second (1000 ms)
        handler.postDelayed({
        currentQuestionIndex = Random.nextInt(questions.size)
            isAnsweringEnabled = true
            showFeedback = null
        }, 1000)
    }

    // Handle answer selection
    fun handleAnswer(answer: String) {
        if (!isAnsweringEnabled) return // Disable answering during delay

        if (answer == currentQuestion.correctAnswer) {
            showFeedback = "Correct!"
            onCorrectAnswer()
        } else {
            showFeedback = "Incorrect. Try again!"
        }

        // Disable answering and start delay for next question
        isAnsweringEnabled = false
        nextQuestion() // Move to the next question

    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Level and Progress Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextButton(onClick = onBack) {
                Text(text = "Back", color = Color.Gray)
            }
            Text(text = "Level $level", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }

        LinearProgressIndicator(
            progress = progress,
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = Color(0xFF80CBC4),
            trackColor = Color(0xFFE0F2F1)
        )

        // Question
        Text(
            text = currentQuestion.questionText,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 24.dp)
        )

        // Feedback Message
        if (showFeedback != null) {
            Text(
                text = showFeedback!!,
                color = if (showFeedback == "Correct!") Color.Green else Color.Red,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(8.dp)
            )
        }

        // Answer Options
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            currentQuestion.options.forEach { answer ->
                Button(
                    onClick = { handleAnswer(answer) },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE3F2FD)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    enabled = isAnsweringEnabled // Disable button during delay
                ) {
                    Text(text = answer, fontSize = 18.sp, color = Color.Black)
                }
            }
        }
    }
}
fun saveProgress(sharedPreferences: SharedPreferences, topic: String, progress: Float) {
    sharedPreferences.edit().putFloat("progress_$topic", progress).apply()
}

fun saveLevel(sharedPreferences: SharedPreferences, topic: String, level: Int) {
    sharedPreferences.edit().putInt("level_$topic", level).apply()
}

fun getProgress(sharedPreferences: SharedPreferences, topic: String): Float {
    return sharedPreferences.getFloat("progress_$topic", 0f)
}

fun getLevel(sharedPreferences: SharedPreferences, topic: String): Int {
    return sharedPreferences.getInt("level_$topic", 1)
}
data class Question(
    val questionText: String,
    val correctAnswer: String,
    val options: List<String>
)


