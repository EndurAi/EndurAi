package com.android.sample.ui.achievements

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.sample.R
import com.android.sample.model.achievements.Statistics
import com.android.sample.ui.theme.Black
import com.android.sample.ui.theme.BlueGradient
import com.android.sample.ui.theme.NeutralGrey
import com.android.sample.ui.theme.OpenSans
import com.android.sample.ui.theme.Transparent
import com.android.sample.ui.theme.White
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map

@Composable
fun InfiniteCalendar(statistics: Statistics, padding: PaddingValues) {

    val lazyListState = rememberLazyListState()
    var monthsToShow by remember { mutableStateOf(3) } // Start with 3 months

    val currentMonth = YearMonth.now()
    val months =
        remember(monthsToShow) {
            generateSequence(currentMonth) { it.minusMonths(1) }.take(monthsToShow).toList()
        }

    LaunchedEffect(lazyListState) {
        snapshotFlow { lazyListState.firstVisibleItemIndex }
            .map { it >= monthsToShow - 3 } // Detect when scrolled to the top
            .distinctUntilChanged()
            .filter { it }
            .collect {
                monthsToShow += 3 // Load one more months
            }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(padding).testTag("HistoryScreen"),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.weight(0.2f))

        Surface(modifier = Modifier.fillMaxWidth().fillMaxHeight(fraction = 0.88f)) {
            LazyColumn(
                modifier = Modifier.testTag("MonthList"),
                reverseLayout = true,
                state = lazyListState,
            ) {
                itemsIndexed(months) { _, yearMonth -> MonthView(yearMonth, statistics) }
            }
        }
    }
}

@Composable
fun MonthView(yearMonth: YearMonth, statistics: Statistics) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp).testTag("MonthView"),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(modifier = Modifier.fillMaxWidth(fraction = 0.95f)) {

            // Display the month at the top

            Text(
                text = yearMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault()),
                fontSize = 50.sp,
                fontFamily = OpenSans,
                fontWeight = FontWeight.Normal,
                modifier = Modifier.align(Alignment.Center))

            // Display the at the top

            Text(
                text = yearMonth.year.toString(),
                fontSize = 20.sp,
                fontFamily = OpenSans,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.BottomEnd))
        }

        // Divider with year at the bottom-right corner
        Box(modifier = Modifier.fillMaxWidth(fraction = 0.95f)) {
            HorizontalDivider(
                color = NeutralGrey, thickness = 2.dp, modifier = Modifier.align(Alignment.CenterStart))
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Weekdays row
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            listOf(
                stringResource(
                    R.string.mon,
                    stringResource(R.string.tue),
                    stringResource(R.string.wed),
                    stringResource(R.string.thu),
                    stringResource(R.string.fri),
                    stringResource(R.string.sat),
                    stringResource(R.string.son)))
                .forEach {
                    Text(
                        text = it,
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center,
                        fontFamily = OpenSans,
                        fontWeight = FontWeight.Light,
                        color = NeutralGrey,
                        modifier = Modifier.weight(1f))
                }
        }

        // Days grid
        val daysInMonth = yearMonth.lengthOfMonth()
        val firstDayOfWeek = yearMonth.atDay(1).dayOfWeek.value % 7 // Shift to start on Monday

        val days = (1..daysInMonth).map { it.toString() }
        val leadingEmptyDays = List(firstDayOfWeek) { "" }
        val trailingEmptyDays = List((7 - (leadingEmptyDays.size + days.size) % 7) % 7) { "" }
        val calendarGrid = leadingEmptyDays + days + trailingEmptyDays

        calendarGrid.chunked(7).forEach { week ->
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                week.forEach { day ->
                    DayView(
                        day, yearMonth, statisticsDates = statistics.getDates().map { d -> d.toLocalDate() })
                }
            }
        }
    }
}

@Composable
fun DayView(day: String, yearMonth: YearMonth, statisticsDates: List<LocalDate>) {
    var isWorkoutDay by remember { mutableStateOf(false) }
    if (day.isNotEmpty()) {
        val dayNumber = day.toInt()
        val date = yearMonth.atDay(dayNumber)
        isWorkoutDay = statisticsDates.contains(date)
    }

    Box(
        modifier =
        if (day.isEmpty()) {
            Modifier.size(50.dp)
                .testTag("DayView")
                .shadow(elevation = if (day.isNotEmpty()) 8.dp else 0.dp, shape = CircleShape)
                .background(Transparent, shape = CircleShape)
                .border(
                    width = 0.5.dp,
                    color = if (day.isNotEmpty()) Black else Transparent,
                    shape = CircleShape)
        } else {
            if (isWorkoutDay) {
                Modifier.size(50.dp)
                    .testTag("DayView")
                    .shadow(elevation = if (day.isNotEmpty()) 8.dp else 0.dp, shape = CircleShape)
                    .background(BlueGradient, shape = CircleShape)
                    .border(
                        width = 0.5.dp,
                        color = if (day.isNotEmpty()) Black else Transparent,
                        shape = CircleShape)
            } else {
                Modifier.size(50.dp)
                    .testTag("DayView")
                    .shadow(elevation = if (day.isNotEmpty()) 8.dp else 0.dp, shape = CircleShape)
                    .background(White, shape = CircleShape)
                    .border(
                        width = 0.5.dp,
                        color = if (day.isNotEmpty()) Black else Transparent,
                        shape = CircleShape)
            }
        },
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = day,
            fontSize = 16.sp,
            fontFamily = OpenSans,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
            color = if (isWorkoutDay) White else Black)
    }
}