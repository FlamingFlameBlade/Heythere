@file:Suppress("DEPRECATION")

package com.example.mathgame
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import androidx.core.content.FileProvider
import com.example.mathgame.ui.theme.BackgroundGaps
import com.example.mathgame.ui.theme.BrickBackground
import com.example.mathgame.ui.theme.Bronze
import com.example.mathgame.ui.theme.Emerald
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
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import kotlin.random.Random


//FlamingFlameBlade

class MainActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sharedPreferences = getSharedPreferences("math_progress", MODE_PRIVATE)

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

    val FirstLaunch = if (remember { sharedPreferences.getString("username", "") } == "") "name"
        else "menu"


    var currentScreen by remember { mutableStateOf(FirstLaunch) }
    var currentTopic by remember { mutableStateOf("Basic Math") }

    // Initialize states for all topics
    val progressState = remember { mutableStateMapOf<String, Float>() }
    val levelState = remember { mutableStateMapOf<String, Int>() }

    // Track total correct answers
    var totalCorrectAnswers by rememberSaveable { mutableIntStateOf(getTotalCorrectAnswers(sharedPreferences)) }
    var streak by rememberSaveable { mutableIntStateOf(getStreak(sharedPreferences))}


    // Load initial values for all topics
    LaunchedEffect(Unit) {
        topics.forEach { topic ->
            progressState[topic] = getProgress(sharedPreferences, topic)
            levelState[topic] = getLevel(sharedPreferences, topic)
        }
    }
//FlamingFlameBlade
    val username:String = remember { sharedPreferences.getString("username", "")!! }
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
                saveTotalCorrectAnswers(sharedPreferences, totalCorrectAnswers)
                streak += 1
                saveStreak(sharedPreferences,streak)// Save the updated count

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
                streak = 0
                saveStreak(sharedPreferences,streak)
            }
        )

        "achievements" -> AchievementsScreen(totalCorrectAnswers, {currentScreen = "menu"},username = username,streak = streak)
        "name" -> NameScreen(onContinue = {currentScreen = "menu"},sharedPreferences = sharedPreferences)
    }
}



@Composable
fun MainMenuScreen(
    onNavigate: (String) -> Unit,
    onAchievements: () -> Unit,
    progressState: Map<String, Float>,
    levelState: Map<String, Int>
)
{Box(modifier = Modifier
    .fillMaxSize()
    .background(BackgroundGaps)){}
    Canvas(modifier = Modifier.fillMaxSize()) {
        drawBrickPattern(
            brickColor = BrickBackground,
            brickWidth = 500f,
            brickHeight = 500f,
            gap = 20f
        )
    }
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Text(
                text = "Choose a Math Topic",
                fontSize = 50.sp,
                fontWeight = FontWeight.Bold,
                color = Gold,
                fontFamily = FontFamily(
                    Font(R.font.enchantedland)
                ),
                modifier = Modifier.padding(bottom = 16.dp, top = 60.dp)
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
    var currentQuestionIndex by remember { mutableIntStateOf(Random.nextInt(questions.size)) }
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
    Box(modifier = Modifier
        .fillMaxSize()
        .background(BackgroundGaps)){}
    Canvas(modifier = Modifier.fillMaxSize()) {
        drawBrickPattern(
            brickColor = BrickBackground,
            brickWidth = 500f,
            brickHeight = 500f,
            gap = 20f
        )
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
        targetValue = if (isLevelUp) Color(0xFFFFD700) else Ivory, // Gold color on level-up
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
fun AchievementsScreen(totalCorrectAnswers: Int, onBack: () -> Unit,username: String,streak:Int) {
    val context = LocalContext.current
    val view = LocalView.current

    val AnswerAchievements = listOf(
        Achievement(10, "Novice", "Answer 10 questions correctly",1),
        Achievement(50, "Apprentice", "Answer 50 questions correctly",2),
        Achievement(100, "Adventurer", "Answer 100 questions correctly",3),
        Achievement(250, "Expert", "Answer 250 questions correctly",4),
        Achievement(500, "Hero", "Answer 500 questions correctly",5),
        Achievement(1000,"King","Answer 1000 questions correctly",6)
    )

    val StreakAchievements = listOf(
        Achievement(5,"Committed","Answer 5 questions correctly in a row",1),
        Achievement(10,"Dedicated","Answer 10 questions correctly in a row",2),
        Achievement(25,"Perservering","Answer 25 questions correctly in a row",3),
        Achievement(20,"Unyielding","Answer 20 questions correctly in a row",4),
        Achievement(50,"Perfectionist","Answer 50 questions correctly in a row",5)
    )
    Box(modifier = Modifier
        .fillMaxSize()
        .background(BackgroundGaps)){}
    Canvas(modifier = Modifier.fillMaxSize()) {
        drawBrickPattern(
            brickColor = BrickBackground,
            brickWidth = 500f,
            brickHeight = 500f,
            gap = 20f
        )
    }
    TextButton(onClick = onBack) {
        Text(text = "Back", color = Color.Gray, modifier = Modifier.padding(top = 32.dp))
    }
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(30.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Title
        item {
            Text(
                text = "Achievements",
                fontSize = 50.sp,
                fontFamily = FontFamily(Font(R.font.enchantedland)),
                fontWeight = FontWeight.Bold,
                color = Gold,
                modifier = Modifier.padding(top = 24.dp)
            )
        }

        // Share button
        item {
            Button(onClick = {
                captureAndShareScreen(view, context)
            }) {
                Text(
                    text = "Show the world your deeds (Share)",
                    fontFamily = FontFamily(Font(R.font.enchantedland)),
                    fontSize = 24.sp
                )
            }
        }

        item {
            Text(
                text = "Total Correct Answers: $totalCorrectAnswers",
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )
        }
        // First Achievements Grid
        items(AnswerAchievements.chunked(3)) { rowAchievements ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                rowAchievements.forEach { achievement ->
                    AchievementTile(
                        achievement = achievement,
                        totalCorrectAnswers = totalCorrectAnswers,
                        username = username,
                        type = "Answer",
                        streak = streak
                    )
                }
            }
        }

        // Current Streak Text
        item {
            Text(
                text = "Current Streak: $streak",
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )
        }

        // Second Achievements Grid (duplicated content — check if this is intentional)
        items(StreakAchievements.chunked(3)) { rowAchievements ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                rowAchievements.forEach { achievement ->
                    AchievementTile(
                        achievement = achievement,
                        totalCorrectAnswers = totalCorrectAnswers,
                        username = username,
                        type = "Streak",
                        streak = streak
                    )
                }
            }
        }
    }

}


@Composable
fun AchievementTile(achievement: Achievement, totalCorrectAnswers: Int, username: String, type: String, streak: Int) {
    val unlocked = if (type == "Answer") {totalCorrectAnswers >= achievement.milestone}
        else streak >= achievement.milestone
    val English = FontFamily(
        Font(R.font.enchantedland)
    )
    val backgroundColor =
        if (unlocked)
            when (achievement.tier) {
                1 -> Bronze
                2 -> Silver
                3 -> Gold
                4 -> LightBlue
                5 -> Ruby
                6 -> Emerald
                else -> LightGray
            }
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
        ) {
            Row (
                horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .fillMaxWidth()
                        ){
                    Text(
                        text = if (unlocked) "🏆" else "🔒",
                        fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (unlocked) Color.Black else Color.DarkGray,

                )
            }
            Row (horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                .fillMaxWidth()
                    .padding(top = 40.dp))
            {
                Text(
                    text = if (unlocked) username else "",
                    fontSize = 160 / username.length * 1.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = English,
                    color = if (unlocked) Color.Black else Color.DarkGray
                )
            }
        }

        // Title
        Text(
            text = achievement.title,
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = backgroundColor,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp),
            fontFamily = FontFamily(
                Font(R.font.enchantedland))
        )

        // Description
        Text(
            text = achievement.description,
            fontSize = 16.sp,
            color = Color.Black,
            textAlign = TextAlign.Center,
            lineHeight = 15.sp
        )
    }
}

@Composable
fun NameScreen(onContinue: () -> Unit,sharedPreferences: SharedPreferences){
    var username by remember { mutableStateOf("") } // Holds user input
    var errorMessage by remember { mutableStateOf("Get Typing!") } // Holds validation message
    Box(modifier = Modifier
        .fillMaxSize()
        .background(BackgroundGaps)){}
    Canvas(modifier = Modifier.fillMaxSize()) {
        drawBrickPattern(
            brickColor = BrickBackground,
            brickWidth = 500f,
            brickHeight = 500f,
            gap = 20f
        )
    }
    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,

        ) {
        Text(text = "Welcome to YOUR Math Quest!",
            fontSize = 80.sp,
            lineHeight = 60.sp,
            fontFamily = FontFamily(
                Font(R.font.enchantedland) // Reference the font here
            ),
            textAlign = TextAlign.Center,
            color = Ruby,

            modifier = Modifier
                .padding(top = 80.dp)
                .padding(bottom = 200.dp)
                .fillMaxWidth()
        )
        Text(text = "Enter your name, adventurer:")

        TextField(
            value = username,
            onValueChange = {
                username = it

                // Validation: Ensure length is between 4 and 20
                errorMessage = when {
                    it.length < 4 -> "Username must be at least 4 characters"
                    it.length > 18 -> "Username cannot be longer than 18 characters"
                    it.contains (" ") -> "Username cannot contain spaces"
                    it.contains ("(") || it.contains(")") || it.contains("!")-> "Username cannot contain symbols"
                    else -> "" // No error
                }
            },
            isError = errorMessage.isNotEmpty(),
            singleLine = true,
            modifier = Modifier.padding(bottom = 50.dp)
        )

        if (errorMessage.isNotEmpty()) {
            Text(
                text = errorMessage,
                color = Color.Red,
                fontSize = 12.sp
            )
        }
        else {
            Button(
                onClick = {
                    sharedPreferences.edit().putString("username", username).apply()
                    onContinue()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Gold
                )


            ) { Text(text = "I'm Ready") }
        }



    }
}

fun androidx.compose.ui.graphics.drawscope.DrawScope.drawBrickPattern(
    brickColor: Color,
    brickWidth: Float,
    brickHeight: Float,
    gap: Float
) {
    // Calculate effective brick size with gap
    val totalBrickWidth = brickWidth + gap
    val totalBrickHeight = brickHeight + gap

    // Calculate the number of rows and columns required
    val rowCount = (size.height / totalBrickHeight).toInt() + 1
    val columnCount = (size.width / totalBrickWidth).toInt() + 2

    for (row in 0 until rowCount) {
        val yOffset = row * totalBrickHeight
        val isOffsetRow = row % 2 != 0 // Alternate row alignment

        for (column in 0 until columnCount) {
            val xOffset = column * totalBrickWidth

            // Add an offset for alternating rows
            val adjustedXOffset = if (isOffsetRow) xOffset - (brickWidth / 2) else xOffset

            drawRect(
                color = brickColor,
                topLeft = Offset(adjustedXOffset, yOffset),
                size = androidx.compose.ui.geometry.Size(brickWidth, brickHeight)
            )
        }
    }
}


fun captureAndShareScreen(view: View, context: Context) {
    view.isDrawingCacheEnabled = true
    val bitmap = Bitmap.createBitmap(view.drawingCache)
    view.isDrawingCacheEnabled = false

    val imageFile = saveBitmapToCache(context, bitmap)
    if (imageFile != null) {
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", imageFile)
        shareImage(context, uri)
    }
}

private fun saveBitmapToCache(context: Context, bitmap: Bitmap): File? {
    // Define the directory in the cache folder
    val cachePath = File(context.cacheDir, "shared_images")

    // Create the directory if it doesn't exist
    if (!cachePath.exists()) {
        cachePath.mkdirs()
    }

    // Define the file to save the image
    val file = File(cachePath, "shared_screen.png")

    return try {
        // Save the bitmap as a PNG file
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        }
        // Return the file if the save was successful
        file
    } catch (e: IOException) {
        e.printStackTrace()
        null
    }
}

private fun shareImage(context: Context, imageUri: Uri) {
    val shareIntent = Intent(Intent.ACTION_SEND).apply {
        type = "image/*"          // Specify that we are sending an image
        putExtra(Intent.EXTRA_STREAM, imageUri) // Attach the image URI

        // Optionally, add text to accompany the image
        putExtra(Intent.EXTRA_TEXT, "My Achievements on Math Quest!")
    }

    // Open the generic share menu where the user can choose an app
    val chooserIntent = Intent.createChooser(shareIntent, "Share via")
    context.startActivity(chooserIntent)
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

fun saveStreak(sharedPreferences: SharedPreferences, count: Int) {
    sharedPreferences.edit().putInt("streak",count).apply()
}

fun getStreak(sharedPreferences: SharedPreferences): Int {
    return sharedPreferences.getInt("streak", 0)
}

//Question data class
data class Question(
    val questionText: String,
    val correctAnswer: String,
    val options: List<String>
)

// Achievement data class
data class Achievement(
    val milestone: Int,
    val title: String,
    val description: String,
    val tier: Int)


