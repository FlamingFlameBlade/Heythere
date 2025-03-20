package com.example.mathgame
import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mathgame.ui.theme.Bronze
import com.example.mathgame.ui.theme.Gold
import com.example.mathgame.ui.theme.Iron
import com.example.mathgame.ui.theme.Ivory
import com.example.mathgame.ui.theme.LightBlue
import com.example.mathgame.ui.theme.LightGray
import com.example.mathgame.ui.theme.LightYellow
import com.example.mathgame.ui.theme.MathGameTheme
import com.example.mathgame.ui.theme.Ruby
import com.example.mathgame.ui.theme.Silver
import com.example.mathgame.ui.theme.Steel
import com.example.mathgame.ui.theme.Yellow
import kotlin.random.Random

//FlamingFlameBlade

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

val topics = listOf("Addition", "Subtraction","Multiplication","Division", "Fraction Addition","Fraction Subtraction","Fraction Multiplication",
    "Complex Division", "Complex Multiplication", "Pre Algebra")

@Composable
fun MathLearningApp(sharedPreferences: SharedPreferences) {
    var currentScreen by remember { mutableStateOf("menu") }
    var currentTopic by remember { mutableStateOf("Basic Math") }

    // Initialize states for all topics
    val progressState = remember { mutableStateMapOf<String, Float>() }
    val levelState = remember { mutableStateMapOf<String, Int>() }

    // Track total correct answers
    var totalCorrectAnswers by rememberSaveable { mutableStateOf(getTotalCorrectAnswers(sharedPreferences)) }

    // Load initial values for all topics
    LaunchedEffect(Unit) {
        topics.forEach { topic ->
            progressState[topic] = getProgress(sharedPreferences, topic)
            levelState[topic] = getLevel(sharedPreferences, topic)
        }
    }
//FlamingFlameBlade
    when (currentScreen) {
        "menu" -> MainMenuScreen(
            onNavigate = { topic ->
                currentTopic = topic
                currentScreen = "question"
            },
            progressState = progressState,
            levelState = levelState,
            onAchievements = { currentScreen = "achievements" }
        )
        "question" -> MathQuestionScreen(
            topic = currentTopic,
            progress = progressState[currentTopic] ?: 0f,
            level = levelState[currentTopic] ?: 1,
            onCorrectAnswer = {
                totalCorrectAnswers += 1
                saveTotalCorrectAnswers(sharedPreferences, totalCorrectAnswers) // Save the updated count

                val level = levelState[currentTopic] ?: 1
                val multiplier = 1f / level
                val newProgress = (progressState[currentTopic] ?: 0f) + (multiplier * 0.5f)

                if (newProgress >= 1f) {
                    // Level up
                    val newLevel = level + 1
                    saveLevel(sharedPreferences, currentTopic, newLevel)
                    levelState[currentTopic] = newLevel
                    progressState[currentTopic] = 0.9999f
                    progressState[currentTopic] = 0f
                    saveProgress(sharedPreferences, currentTopic, 0f) // Reset progress
                } else {
                    // Update progress without leveling up
                    progressState[currentTopic] = newProgress
                    saveProgress(sharedPreferences, currentTopic, newProgress)
                }
            },

                    onBack = { currentScreen = "menu" },
            onIncorrectAnswer = {
                val newProgress = (progressState[currentTopic] ?: 0f) - 0.2f
                saveProgress(sharedPreferences, currentTopic, if (newProgress <= -0.2f) 0f else newProgress)
                progressState[currentTopic] =  if (newProgress <= -0.2f) 0f else newProgress
            }
        )
        "achievements" -> AchievementsScreen(totalCorrectAnswers, {currentScreen = "menu"})
    }
}



@Composable
fun MainMenuScreen(
    onNavigate: (String) -> Unit,
    onAchievements: () -> Unit, // New parameter for Achievements navigation
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
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 16.dp, top = 48.dp)
            )
        }

        // Achievements Button
        item {
            Button(
                onClick = onAchievements,
                modifier = Modifier.padding(10.dp)
            ) {
                Text(text = "Achievements", fontSize = 18.sp)
            }
        }

        items(topics.chunked(2)) { rowTopics ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
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
    var tileColor = Iron
    var progressForeground = Steel
    var progressBackground = Ivory
    if (level != null && level > 7){
        tileColor = Gold
    }
    else if (level != null && level > 5) {
        tileColor = Silver
        progressForeground = LightBlue
    }
    else if (level != null && level > 3) {
        tileColor = Bronze
        progressBackground = LightYellow
        progressForeground = Yellow
    }

    Card(
        modifier = Modifier
            .height(172.dp) // Rectangular shape
            .width(200.dp)
            .padding(10.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = tileColor),
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
                        color = progressForeground,
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
                            color = progressBackground, // Background color
                            strokeWidth = 8.dp
                        )

                        // Foreground progress bar
                        CircularProgressIndicator(
                            progress = progress ?: 0f,
                            modifier = Modifier.fillMaxSize(),
                            color = progressForeground, // Foreground color
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

//FlamingFlameBlade
@Composable
fun MathQuestionScreen(
    topic: String,
    progress: Float,
    level: Int,
    onCorrectAnswer: () -> Unit,
    onBack: () -> Unit,
    onIncorrectAnswer: () -> Unit
) {
    var showFeedback by remember { mutableStateOf<String?>(null) } // Feedback message
    var isAnsweringEnabled by remember { mutableStateOf(true) } // Enable/disable answering during delay
//FlamingFlameBlade
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
//FlamingFlameBlade
    // Handle answer selection
    fun handleAnswer(answer: String) {
        if (!isAnsweringEnabled) return // Disable answering during delay

        if (answer == currentQuestion.correctAnswer) {
            showFeedback = "Correct!"
            onCorrectAnswer()
        } else {
            showFeedback = "Incorrect."
            onIncorrectAnswer()
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
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextButton(onClick = onBack) {
                Text(text = "Back", color = Color.Gray)
            }
            LevelIndicator(level)
        }
//FlamingFlameBlade
        val animatedProgress by animateFloatAsState(targetValue = progress)

        LinearProgressIndicator(
            progress = animatedProgress,
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
//FlamingFlameBlade
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
@Composable
fun LevelIndicator(level: Int) {
    var isLevelUp by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isLevelUp) 1.2f else 1f, // Scale up slightly on level up
        animationSpec = tween(
            durationMillis = 1000, // Animation duration
            easing = FastOutSlowInEasing
        ),
        finishedListener = { isLevelUp = false } // Reset scale after animation
    )

    val color by animateColorAsState(
        targetValue = if (isLevelUp) Color(0xFFFFD700) else MaterialTheme.colorScheme.primary, // Gold color on level-up
        animationSpec = tween(durationMillis = 800) // Same duration as scale animation
    )

    LaunchedEffect(level) {
        isLevelUp = true // Trigger animations when the level changes
    }

    Text(
        text = "Level $level",
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        color = color, // Apply animated color
        modifier = Modifier.scale(scale) // Apply scaling animation
    )
}
@Composable
fun AchievementsScreen(totalCorrectAnswers: Int, onBack: () -> Unit) {
    val achievements = listOf(
        Achievement(10, "Beginner", "Answer 10 questions correctly"),
        Achievement(50, "Apprentice", "Answer 50 questions correctly"),
        Achievement(100, "Scholar", "Answer 100 questions correctly"),
        Achievement(250, "Expert", "Answer 250 questions correctly"),
        Achievement(500, "Master", "Answer 500 questions correctly")
    )
    TextButton(onClick = onBack) {
        Text(text = "Back", color = Color.Gray, modifier = Modifier.padding(top = 32.dp))
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(30.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Title
        Text(
            text = "Achievements",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(top = 24.dp)
        )

        // Total correct answers
        Text(
            text = "Total Correct Answers: $totalCorrectAnswers",
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.secondary
        )
//FlamingFlameBlade
        // Achievements Grid
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(achievements.chunked(3)) { rowAchievements ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    rowAchievements.forEach { achievement ->
                        AchievementTile(achievement, totalCorrectAnswers)
                    }
                }
            }
        }
    }
}

// Achievement data class
@Composable
fun AchievementTile(achievement: Achievement, totalCorrectAnswers: Int) {
    val unlocked = totalCorrectAnswers >= achievement.milestone
    val backgroundColor =
        if (unlocked)
            if (achievement.title == "Beginner")
                Bronze
            else if (achievement.title == "Apprentice")
                Silver
            else if (achievement.title == "Scholar")
                Gold
            else if (achievement.title == "Expert")
                Ruby
            else
                LightGray
        else
            Color.Gray.copy(alpha = 0.4f)
//FlamingFlameBlade
    Column(
        modifier = Modifier.width(110.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Achievement Box
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(backgroundColor),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (unlocked) "🏆" else "🔒",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = if (unlocked) Color.Black else Color.DarkGray
            )
        }

        // Title
        Text(
            text = achievement.title,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.tertiary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp)
        )

        // Description
        Text(
            text = achievement.description,
            fontSize = 12.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            lineHeight = 15.sp
        )
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
fun saveTotalCorrectAnswers(sharedPreferences: SharedPreferences, count: Int) {
    sharedPreferences.edit().putInt("total_correct_answers", count).apply()
}

fun getTotalCorrectAnswers(sharedPreferences: SharedPreferences): Int {
    return sharedPreferences.getInt("total_correct_answers", 0)
}
data class Question(
    val questionText: String,
    val correctAnswer: String,
    val options: List<String>
)
data class Achievement(
    val milestone: Int,
    val title: String,
    val description: String)


