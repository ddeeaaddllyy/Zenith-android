package com.ddeeaaddllyy.zenith.ui.common

import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.ddeeaaddllyy.zenith.ui.theme.BlackRaspberry
import com.ddeeaaddllyy.zenith.ui.theme.CalendarSurfaceVariant
import com.ddeeaaddllyy.zenith.ui.theme.CalendarWhite
import com.ddeeaaddllyy.zenith.ui.theme.MoonRock
import com.ddeeaaddllyy.zenith.ui.theme.SoftDove
import com.ddeeaaddllyy.zenith.ui.theme.SpicedHotChocolate
import java.time.LocalDate
import java.time.ZoneOffset

private val ZenithCalendarColors = lightColorScheme(
    primary = SpicedHotChocolate,
    onPrimary = CalendarWhite,
    primaryContainer = SoftDove,
    onPrimaryContainer = BlackRaspberry,
    background = CalendarWhite,
    surface = CalendarWhite,
    onSurface = BlackRaspberry,
    surfaceVariant = CalendarSurfaceVariant,
    onSurfaceVariant = MoonRock,
    outline = MoonRock
)

/**
 * Deliberately light/white — a bright pop against the rest of the dark app.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ZenithDatePickerDialog(
    initialDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    onDismiss: () -> Unit
) {
    val state = rememberDatePickerStateNotAfterToday(initialDate)

    MaterialTheme(colorScheme = ZenithCalendarColors) {
        DatePickerDialog(
            onDismissRequest = onDismiss,
            confirmButton = {
                TextButton(onClick = {
                    val millis = state.selectedDateMillis
                    if (millis != null) {
                        onDateSelected(
                            java.time.Instant.ofEpochMilli(millis).atZone(ZoneOffset.UTC).toLocalDate()
                        )
                    }
                    onDismiss()
                }) {
                    Text("Выбрать")
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text("Отмена")
                }
            }
        ) {
            DatePicker(state = state)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun rememberDatePickerStateNotAfterToday(initialDate: LocalDate) =
    androidx.compose.material3.rememberDatePickerState(
        initialSelectedDateMillis = initialDate.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli(),
        selectableDates = remember {
            object : SelectableDates {
                override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                    val today = LocalDate.now().atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
                    return utcTimeMillis <= today
                }
            }
        }
    )
