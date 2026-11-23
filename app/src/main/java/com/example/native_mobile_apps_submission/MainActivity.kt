package com.example.native_mobile_apps_submission

import android.content.res.Configuration
import android.media.MediaPlayer
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin

class MainActivity : ComponentActivity() {
    private val studyViewModel: StudyViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Surface(
                color = Color(0xFF101010),
                modifier = Modifier.fillMaxSize()
            ) {
                StudyAppRoot(viewModel = studyViewModel)
            }
        }
    }
}

@Composable
fun StudyAppRoot(viewModel: StudyViewModel) {
    val navController = rememberNavController()
    val uiState by viewModel.uiState.collectAsState()
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        AutoScrollingNatureBackground()

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.55f))
        )

        Text(
            text = "STUDY TIMER 101",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White.copy(alpha = 0.9f),
            letterSpacing = 2.sp,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = if (isLandscape) 16.dp else 105.dp)
        )

        NavHost(
            navController = navController,
            startDestination = "time_input",
            modifier = Modifier.fillMaxSize()
        ) {
            composable("time_input") {
                TimeInputScreen(
                    uiState = uiState,
                    onInputChanged = { viewModel.onMinutesInputChanged(it) },
                    onConfirm = {
                        viewModel.confirmAndStartTimer()
                        if (viewModel.uiState.value.selectedTimeMillis != null) {
                            navController.navigate("timer") {
                                launchSingleTop = true
                            }
                        }
                    },
                    onShowStats = { viewModel.setShowStatsDialog(true) },
                    onDismissStats = { viewModel.setShowStatsDialog(false) },
                    onClearHistory = { viewModel.clearHistory() }
                )
            }

            composable("timer") {
                TimerScreen(
                    uiState = uiState,
                    onToggleTimer = { viewModel.toggleTimer() },
                    onToggleMute = { viewModel.toggleMute() },
                    onBackToSetup = {
                        viewModel.backToSetup()
                        val popped = navController.popBackStack("time_input", inclusive = false)
                        if (!popped) {
                            navController.navigate("time_input") {
                                popUpTo("timer") { inclusive = true }
                                launchSingleTop = true
                            }
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun AutoScrollingNatureBackground() {
    val listState = rememberLazyListState()

    LaunchedEffect(Unit) {
        while (true) {
            delay(60L)
            listState.scrollBy(1f)
            if (listState.firstVisibleItemIndex >= 45) {
                listState.scrollToItem(0)
            }
        }
    }

    LazyColumn(
        state = listState,
        modifier = Modifier
            .fillMaxSize()
            .alpha(0.35f),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        userScrollEnabled = false
    ) {
        items(50) { index ->
            val imageUrl = when (index % 2) {
                0 -> "https://images.pexels.com/photos/35589577/pexels-photo-35589577/free-photo-of-tranquil-forest-scene-by-a-sunlit-lake.jpeg?auto=compress&w=1260&h=750&dpr=1"
                else -> "https://static.vecteezy.com/system/resources/previews/074/572/897/non_2x/nature-wallpaper-nature-landscape-serene-mountain-lake-at-sunset-with-calm-water-reflecting-golden-sky-and-lush-green-forests-photo.jpg"
            }
            AsyncImage(
                model = imageUrl,
                contentDescription = "Calm Nature Background",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp),
                contentScale = ContentScale.Crop
            )
        }
    }
}

@Composable
fun TimeInputScreen(
    uiState: StudyUiState,
    onInputChanged: (String) -> Unit,
    onConfirm: () -> Unit,
    onShowStats: () -> Unit,
    onDismissStats: () -> Unit,
    onClearHistory: () -> Unit
) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    if (isLandscape) {
        Row(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0x22FFFFFF)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "💡 Daily Study Inspiration",
                            fontSize = 12.sp,
                            color = Color(0xFF37B900),
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "\"${uiState.motivationalQuote}\"",
                            fontSize = 13.sp,
                            color = Color.White,
                            fontStyle = FontStyle.Italic
                        )
                        if (uiState.quoteAuthor.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "- ${uiState.quoteAuthor}",
                                fontSize = 11.sp,
                                color = Color.LightGray,
                                modifier = Modifier.align(Alignment.End)
                            )
                        }
                    }
                }
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Set Timer Duration",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = uiState.minutesInput,
                    onValueChange = onInputChanged,
                    label = { Text("Minutes (1 - 60)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF37B900),
                        unfocusedBorderColor = Color.DarkGray,
                        focusedLabelColor = Color(0xFF37B900),
                        unfocusedLabelColor = Color.LightGray,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )

                if (uiState.errorMessage != null) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = uiState.errorMessage, color = Color.Red, fontSize = 13.sp)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = onConfirm,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF37B900))
                    ) {
                        Text("Start", color = Color.White)
                    }
                    OutlinedButton(
                        onClick = onShowStats,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                    ) {
                        Text("Stats")
                    }
                }
            }
        }
    } else {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .padding(24.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0x22FFFFFF)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "💡 Daily Study Inspiration (Loaded from API)",
                        fontSize = 12.sp,
                        color = Color(0xFF37B900),
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "\"${uiState.motivationalQuote}\"",
                        fontSize = 14.sp,
                        color = Color.White,
                        fontStyle = FontStyle.Italic
                    )
                    if (uiState.quoteAuthor.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "- ${uiState.quoteAuthor}",
                            fontSize = 12.sp,
                            color = Color.LightGray,
                            modifier = Modifier.align(Alignment.End)
                        )
                    }
                }
            }

            Text(
                text = "Set Timer Duration",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Maximum limit is 60 minutes (1 hour)",
                fontSize = 14.sp,
                color = Color.LightGray
            )
            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = uiState.minutesInput,
                onValueChange = onInputChanged,
                label = { Text("Minutes (1 - 60)") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF37B900),
                    unfocusedBorderColor = Color.DarkGray,
                    focusedLabelColor = Color(0xFF37B900),
                    unfocusedLabelColor = Color.LightGray,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                )
            )

            if (uiState.errorMessage != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = uiState.errorMessage, color = Color.Red, fontSize = 14.sp)
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF37B900))
            ) {
                Text("Confirm & Start", color = Color.White, fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(
                onClick = onShowStats,
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
            ) {
                Text("View Total Study Time", fontSize = 14.sp)
            }
        }
    }

    if (uiState.showStatsDialog) {
        val totalMinutes = uiState.totalStudiedMillis / (1000 * 60)
        val totalHours = totalMinutes / 60
        val remainingMins = totalMinutes % 60

        AlertDialog(
            onDismissRequest = onDismissStats,
            title = { Text("Total Study Time") },
            text = {
                Text(
                    text = if (totalHours > 0) {
                        "You have studied for $totalHours hours and $remainingMins minutes in total!"
                    } else {
                        "You have studied for $totalMinutes minutes in total!"
                    },
                    fontSize = 16.sp
                )
            },
            confirmButton = {
                TextButton(onClick = onDismissStats) {
                    Text("Close")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = onClearHistory,
                    colors = ButtonDefaults.textButtonColors(contentColor = Color.Red)
                ) {
                    Text("Clear History")
                }
            }
        )
    }
}

@Composable
fun TimerScreen(
    uiState: StudyUiState,
    onToggleTimer: () -> Unit,
    onToggleMute: () -> Unit,
    onBackToSetup: () -> Unit
) {
    val context = LocalContext.current
    var size by remember { mutableStateOf(IntSize.Zero) }

    var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }
    var shuffledPlaylist by remember { mutableStateOf<List<Int>>(emptyList()) }
    var currentTrackIndex by remember { mutableStateOf(0) }

    fun playNextTrackInShuffle() {
        if (shuffledPlaylist.isEmpty()) {
            val rawFields = R.raw::class.java.fields
            shuffledPlaylist = rawFields.map { it.getInt(null) }.shuffled()
            currentTrackIndex = 0
        }

        if (shuffledPlaylist.isNotEmpty()) {
            val trackId = shuffledPlaylist[currentTrackIndex]
            mediaPlayer?.release()
            mediaPlayer = MediaPlayer.create(context, trackId)?.apply {
                val volume = if (uiState.isMuted) 0f else 1f
                setVolume(volume, volume)
                setOnCompletionListener {
                    currentTrackIndex++
                    if (currentTrackIndex >= shuffledPlaylist.size) {
                        shuffledPlaylist = shuffledPlaylist.shuffled()
                        currentTrackIndex = 0
                    }
                    if (uiState.isTimerRunning) {
                        playNextTrackInShuffle()
                    }
                }
                start()
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            mediaPlayer?.release()
        }
    }

    LaunchedEffect(uiState.isTimerRunning) {
        if (uiState.isTimerRunning) {
            if (mediaPlayer == null || shuffledPlaylist.isEmpty()) {
                playNextTrackInShuffle()
            } else {
                mediaPlayer?.start()
            }
        } else {
            mediaPlayer?.pause()
        }
    }

    LaunchedEffect(uiState.isMuted) {
        val vol = if (uiState.isMuted) 0f else 1f
        mediaPlayer?.setVolume(vol, vol)
    }

    val totalTime = uiState.selectedTimeMillis ?: 1L
    val progressValue = if (totalTime > 0L) uiState.currentTime / totalTime.toFloat() else 0f
    val strokeWidth: Dp = 5.dp

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(200.dp).onSizeChanged { size = it }
        ) {
            Canvas(modifier = Modifier.size(200.dp)) {
                drawArc(
                    color = Color.DarkGray,
                    startAngle = -215f,
                    sweepAngle = 250f,
                    useCenter = false,
                    size = Size(size.width.toFloat(), size.height.toFloat()),
                    style = Stroke(strokeWidth.toPx(), cap = StrokeCap.Round)
                )

                val dynamicActiveColor = lerp(Color.Red, Color.Green, progressValue.coerceIn(0f, 1f))

                drawArc(
                    color = dynamicActiveColor,
                    startAngle = -215f,
                    sweepAngle = 250f * progressValue.coerceIn(0f, 1f),
                    useCenter = false,
                    size = Size(size.width.toFloat(), size.height.toFloat()),
                    style = Stroke(strokeWidth.toPx(), cap = StrokeCap.Round)
                )

                val center = Offset(size.width / 2f, size.height / 2f)
                val beta = (250f * progressValue.coerceIn(0f, 1f) + 145f) * (Math.PI / 180f).toFloat()
                val r = size.width / 2f
                val a = cos(beta) * r
                val b = sin(beta) * r

                drawCircle(
                    color = Color.White,
                    radius = strokeWidth.toPx() * 1.2f,
                    center = Offset(center.x + a, center.y + b)
                )
            }

            Text(
                text = (uiState.currentTime / 1000L).toString(),
                fontSize = 44.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset(y = (-110).dp)
        ) {
            Button(
                onClick = onToggleTimer,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (!uiState.isTimerRunning || uiState.currentTime <= 0L) Color.Green else Color.Red
                )
            ) {
                Text(
                    text = if (uiState.isTimerRunning && uiState.currentTime > 0L) "Pause"
                    else if (!uiState.isTimerRunning && uiState.currentTime > 0L && uiState.currentTime < totalTime) "Start"
                    else if (uiState.currentTime <= 0L) "Restart"
                    else "Start",
                    color = Color.White
                )
            }
        }

        IconButton(
            onClick = onToggleMute,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(24.dp)
        ) {
            Text(
                text = if (uiState.isMuted) "🔇" else "🔊",
                fontSize = 28.sp
            )
        }

        IconButton(
            onClick = {
                mediaPlayer?.stop()
                mediaPlayer?.release()
                mediaPlayer = null
                onBackToSetup()
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp)
        ) {
            Text(
                text = "🚪",
                fontSize = 28.sp
            )
        }
    }
}
