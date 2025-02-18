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

val topics = listOf("Addition", "Subtraction","Multiplication","Division", "Fractions", "Geometry",
    "Complex Division", "Complex Multiplication", "Pre Algebra")

@Composable
fun MathLearningApp(sharedPreferences: SharedPreferences) {
    var currentScreen by remember { mutableStateOf("menu") }
    var currentTopic by remember { mutableStateOf("Basic Math") }

    // Initialize states for all topics
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

    // Get questions from repository
    val questions = QuestionsRepository.getQuestionsForTopic(topic)

    // Ensure there are questions available
    if (questions.isEmpty()) {
        return // Avoid rendering anything if there are no questions
    }

    // Track the random index for question selection
    var currentQuestionIndex by remember { mutableStateOf(Random.nextInt(questions.size)) }
    val currentQuestion = questions[currentQuestionIndex]

    // Update the random index when the user progresses to the next question
    fun nextQuestion() {
        val handler = Handler(Looper.getMainLooper())

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
            showFeedback = "Incorrect."
        }

        // Disable answering and start delay for next question
        isAnsweringEnabled = false
        nextQuestion()
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


