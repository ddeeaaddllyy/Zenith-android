package com.ddeeaaddllyy.zenith.ui.profile

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseInOutSine
import androidx.compose.animation.core.EaseOutSine
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.ddeeaaddllyy.zenith.domain.model.LoginResult
import com.ddeeaaddllyy.zenith.ui.theme.SuccessGreen
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val EXIT_DURATION_MS = 460
private const val PANEL_HEIGHT_DP = 440
private val PanelShape = RoundedCornerShape(28.dp)

// The glow halo is drawn with a much larger corner radius than the panel so that, once blurred,
// its outline has no perceptible corners — it reads as a soft rounded aura, not a rectangle.
private val GlowShape = RoundedCornerShape(52.dp)

enum class NedovolenAuthMode(
    val title: String,
    val sideText: String,
    val submitLabel: String
) {
    LOGIN("Вход", "С возвращением в Nedovolen", "Войти"),
    REGISTER("Регистрация", "Один аккаунт - все приложения", "Зарегистрироваться")
}

/**
 * A single big, showy window shared by login and registration. Pops in with a spring,
 * bathes its edges in a soft green light on success, then dissolves away.
 * [mode] == null means hidden (an exit animation plays first).
 *
 * ### How the success glow works
 * The goal was a *welcoming, cautious* green light — one that swells gently, then recedes and
 * leaves a fading green trace, never blinking or snapping. It is assembled from two independent
 * pieces:
 *
 * **1. The driver (timeline).** Two [Animatable]s, `coreGlow` and `trailGlow`, both run once when
 * `success` flips true (see the `LaunchedEffect` below):
 * ```
 * rise  : 0 → 1 over 650ms with EaseInOutSine   // slow start = "cautious/welcoming"
 * hold  : brief pause at full brightness
 * decay : 1 → 0 with EaseOutSine (decelerating)  // never a hard cut
 * ```
 * `trailGlow` decays over a *longer* window (2000ms vs the core's 1400ms), so after the sharp
 * inner light is gone the wide outer halo is still dimming — that lag is the "afterglow trail".
 * The overlay only dismisses itself after both channels finish (`trailJob.join()`), so the whole
 * arc is always seen.
 *
 * **2. The look (rendering).** Four empty, equally-sized [Box]es stacked behind the panel, each
 * drawing nothing but a rounded [border] that is then run through [Modifier.blur]. Because blur is
 * applied *before* the border in the modifier chain, the crisp stroke gets feathered into a band
 * of light; wider blur on the outer layers spreads it further. The halos use [GlowShape] (a 52dp
 * corner radius, far rounder than the panel's 28dp) so that, once blurred, the aura has no visible
 * corners at all. Opacity of each layer is tied to `coreGlow`/`trailGlow`, and the two outer
 * layers also scale up slightly (`bloom`) as the trail fades, so the green visibly expands outward
 * while it dims. (Blur is a RenderEffect available on API 31+; on 29–30 it gracefully degrades to
 * a plain green border.)
 */
@Composable
fun NedovolenAuthOverlay(
    mode: NedovolenAuthMode?,
    onRegister: (login: String, password: String, onDone: () -> Unit) -> Unit,
    onLogin: (login: String, password: String, onResult: (LoginResult) -> Unit) -> Unit,
    onDismissRequest: () -> Unit
) {
    var shouldCompose by remember { mutableStateOf(false) }
    var closing by remember { mutableStateOf(false) }
    var displayMode by remember { mutableStateOf(NedovolenAuthMode.LOGIN) }

    LaunchedEffect(mode) {
        if (mode != null) {
            displayMode = mode
            closing = false
            shouldCompose = true
        } else if (shouldCompose) {
            closing = true
            delay(EXIT_DURATION_MS.toLong())
            shouldCompose = false
        }
    }

    if (!shouldCompose) return

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        val animTarget = mode != null && !closing

        val scale by animateFloatAsState(
            targetValue = if (animTarget) 1f else 1.18f,
            animationSpec = if (animTarget) spring(dampingRatio = 0.62f, stiffness = 280f) else tween(EXIT_DURATION_MS),
            label = "authScale"
        )
        val contentAlpha by animateFloatAsState(
            targetValue = if (animTarget) 1f else 0f,
            animationSpec = tween(if (animTarget) 260 else EXIT_DURATION_MS),
            label = "authAlpha"
        )
        val dissolveBlur by animateFloatAsState(
            targetValue = if (animTarget) 0f else 26f,
            animationSpec = tween(EXIT_DURATION_MS),
            label = "authBlur"
        )

        var login by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var confirmPassword by remember { mutableStateOf("") }
        var passwordVisible by remember { mutableStateOf(false) }
        var confirmVisible by remember { mutableStateOf(false) }
        var errorText by remember { mutableStateOf<String?>(null) }
        var isSubmitting by remember { mutableStateOf(false) }
        var success by remember { mutableStateOf(false) }

        // Success glow: a soft green welcome that rises gently, then decays slowly. The outer
        // "trail" halo lingers a beat longer than the crisp "core" ring, so the green leaves an
        // afterglow behind it rather than snapping off.
        val coreGlow = remember { Animatable(0f) }
        val trailGlow = remember { Animatable(0f) }

        val isRegister = displayMode == NedovolenAuthMode.REGISTER
        val isValid = if (isRegister) {
            login.isNotBlank() && password.length >= 4 && password == confirmPassword
        } else {
            login.isNotBlank() && password.isNotBlank()
        }

        fun submit() {
            if (!isValid || isSubmitting || success) return
            errorText = null
            isSubmitting = true
            if (isRegister) {
                onRegister(login.trim(), password) {
                    isSubmitting = false
                    success = true
                }
            } else {
                onLogin(login.trim(), password) { result ->
                    isSubmitting = false
                    when (result) {
                        LoginResult.Success -> success = true
                        LoginResult.InvalidCredentials -> errorText = "Неверный логин или пароль"
                        LoginResult.NoAccount -> errorText = "Аккаунт не найден — сначала зарегистрируйтесь"
                    }
                }
            }
        }

        // Success timeline. Runs both glow channels once, then dismisses when the last one
        // (the slow trail) has fully faded — see the KDoc on this function for the full rationale.
        LaunchedEffect(success) {
            if (!success) return@LaunchedEffect
            // Outer trail: rises with the core, but fades on a longer, gentler tail so a green
            // aura is still receding after the inner ring is already gone (the afterglow).
            val trailJob = launch {
                trailGlow.animateTo(1f, tween(650, easing = EaseInOutSine))
                trailGlow.animateTo(0f, tween(2000, easing = EaseOutSine))
            }
            // Inner core: gentle welcoming rise, a short hold at full brightness, then a soft decay.
            coreGlow.animateTo(1f, tween(650, easing = EaseInOutSine))
            delay(360)
            coreGlow.animateTo(0f, tween(1400, easing = EaseOutSine))
            trailJob.join()
            onDismissRequest()
        }

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            // Success glow
            // The green aura is built from FOUR stacked halo layers, each an identically-sized,
            // empty Box that draws only a rounded BORDER and then blurs it. Two ideas make it
            // read as a soft, cornerless, "breathing" light rather than a hard rectangle:
            //
            //  1. Blur-then-border ordering. `Modifier.blur(r).border(w, …, GlowShape)` means the
            //     blur graphics-layer wraps the border draw, so the crisp stroke is smeared into a
            //     feathered band. Larger blur radius on the outer layers = softer, wider spread.
            //     (Modifier.blur is a RenderEffect, active on API 31+; on 29–30 it degrades to a
            //     plain border, still on-brand, just less feathery.)
            //  2. GlowShape uses a far larger corner radius (52dp) than the panel (28dp). After
            //     blurring, the halo's corners have no visible angle at all — the request to
            //     "remove the sharp corners".
            //
            // Two animation channels drive opacity so the light has a life cycle instead of a
            // blink:
            //   • coreGlow  — the crisp inner ring. Rises, holds, then fades on a medium curve.
            //   • trailGlow — the wide outer halo. Fades on a *longer* curve and simultaneously
            //     blooms outward (via `bloom` scale), so as the core dies the outer green expands
            //     and lingers a beat — the "afterglow / trace left behind".
            // Every fade uses EaseOutSine (decelerating), so the light never cuts off abruptly.
            val coreV = coreGlow.value
            val trailV = trailGlow.value
            val bloom = 1f + (1f - trailV) * 0.08f
            // Outermost, widest bloom — the lingering afterglow.
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.95f)
                    .height(PANEL_HEIGHT_DP.dp)
                    .graphicsLayer { scaleX = scale * bloom; scaleY = scale * bloom }
                    .blur(72.dp)
                    .border(22.dp, SuccessGreen.copy(alpha = trailV * 0.5f), GlowShape)
            )
            // Mid halo — the main body of the green light.
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.95f)
                    .height(PANEL_HEIGHT_DP.dp)
                    .graphicsLayer { scaleX = scale * bloom; scaleY = scale * bloom }
                    .blur(40.dp)
                    .border(14.dp, SuccessGreen.copy(alpha = maxOf(coreV, trailV) * 0.75f), GlowShape)
            )
            // Inner halo — gives the glow saturation near the panel edge.
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.95f)
                    .height(PANEL_HEIGHT_DP.dp)
                    .graphicsLayer { scaleX = scale; scaleY = scale }
                    .blur(20.dp)
                    .border(7.dp, SuccessGreen.copy(alpha = coreV * 0.9f), GlowShape)
            )
            // Crisp rim — a bright, tight line that traces the panel outline.
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.95f)
                    .height(PANEL_HEIGHT_DP.dp)
                    .graphicsLayer { scaleX = scale; scaleY = scale }
                    .blur(6.dp)
                    .border(3.dp, SuccessGreen.copy(alpha = coreV), GlowShape)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth(0.95f)
                    .height(PANEL_HEIGHT_DP.dp)
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                        alpha = contentAlpha
                    }
                    .blur(dissolveBlur.dp)
                    .clip(PanelShape)
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(120.dp)
                        .background(
                            Brush.verticalGradient(
                                listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.tertiary)
                            )
                        )
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "N",
                        style = MaterialTheme.typography.displayMedium,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = displayMode.sideText,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimary,
                        textAlign = TextAlign.Center
                    )
                }

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .verticalScroll(rememberScrollState())
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = displayMode.title,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        IconButton(onClick = onDismissRequest) {
                            Icon(
                                imageVector = Icons.Filled.Close,
                                contentDescription = "Закрыть",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    OutlinedTextField(
                        value = login,
                        onValueChange = { login = it; errorText = null },
                        label = { Text("Логин") },
                        singleLine = true,
                        enabled = !success,
                        colors = authFieldColors(),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it; errorText = null },
                        label = { Text("Пароль") },
                        singleLine = true,
                        enabled = !success,
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                    contentDescription = if (passwordVisible) "Скрыть пароль" else "Показать пароль",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        },
                        colors = authFieldColors(),
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (isRegister) {
                        OutlinedTextField(
                            value = confirmPassword,
                            onValueChange = { confirmPassword = it; errorText = null },
                            label = { Text("Повторите пароль") },
                            singleLine = true,
                            enabled = !success,
                            visualTransformation = if (confirmVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            trailingIcon = {
                                IconButton(onClick = { confirmVisible = !confirmVisible }) {
                                    Icon(
                                        imageVector = if (confirmVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                        contentDescription = if (confirmVisible) "Скрыть пароль" else "Показать пароль",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            },
                            colors = authFieldColors(),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    val validationHint = when {
                        !isRegister -> null
                        password.isNotEmpty() && password.length < 4 -> "Пароль должен быть не короче 4 символов"
                        confirmPassword.isNotEmpty() && password != confirmPassword -> "Пароли не совпадают"
                        else -> null
                    }
                    (errorText ?: validationHint)?.let {
                        Text(it, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)
                    }

                    Button(
                        onClick = { submit() },
                        enabled = isValid && !isSubmitting && !success,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary,
                            disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = if (success) 1f else 0.5f),
                            disabledContentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        when {
                            isSubmitting -> CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp
                            )
                            success -> {
                                Icon(Icons.Filled.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Готово!")
                            }
                            else -> Text(displayMode.submitLabel)
                        }
                    }

                    if (!success) {
                        TextButton(onClick = onDismissRequest, modifier = Modifier.fillMaxWidth()) {
                            Text("Отмена", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun authFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = MaterialTheme.colorScheme.primary,
    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
    focusedLabelColor = MaterialTheme.colorScheme.primary,
    unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
    focusedTextColor = MaterialTheme.colorScheme.onSurface,
    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
    cursorColor = MaterialTheme.colorScheme.primary
)
