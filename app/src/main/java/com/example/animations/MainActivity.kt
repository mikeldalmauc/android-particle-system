package com.example.animations

import BouncingBallAnimation
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.animations.ui.theme.AnimationsTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            AnimationsTheme {
                NavigationDrawer()
            }
        }
    }
}

@Preview
@Composable
fun Preview() {
    AnimationsTheme {
        NavigationDrawer()
    }
}

@Composable
fun NavigationDrawer() {
    val item = remember { mutableStateOf("Carrousel") }

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.fillMaxWidth(0.3f)
            ) {
                Text("Ejemplos", modifier = Modifier.padding(16.dp))
                HorizontalDivider()

                listOf(
                    "Particles",
                    "Character",
                    "Zombie",
                    "Zombies",
                    "lottie_lego",
                    "SinusoidalBallAnimation",
                    "BouncingBallAnimation",
                    "Watch",
                    "Confetti",
                    "Carrousel",
                    "ErrorPage",
                    "TextAnimation"

                ).map { name ->
                    NavigationDrawerItem(label = { Text(text = name) },
                        selected = item.value == name,
                        onClick = {
                            item.value = name
                            scope.launch {
                                drawerState.close()
                            }
                        })
                }
            }
        },
    ) {
        Scaffold(floatingActionButton = {
            ExtendedFloatingActionButton(text = { Text("Otros ejemplos") },
                icon = { Icon(Icons.Filled.Search, contentDescription = "") },
                onClick = {
                    scope.launch {
                        drawerState.apply {
                            if (isClosed) open() else close()
                        }
                    }
                })
        }) { contentPadding ->

            when (item.value) {
                "Particles" -> ParticlesBox(PaddingValues(16.dp))

                "Character" -> {
                    val char = createCharacter()
                    AnimationBox(PaddingValues(16.dp), char) { CharacterAnimation(char) }
                }

                "Zombie" -> {
                    val char = createZombi()
                    AnimationBox(PaddingValues(16.dp), char) { ZombiAnimation(char) }
                }

                "Zombies" -> Zombies(PaddingValues(16.dp))

                "lottie_lego" -> LottieLego(contentPadding)

                "SinusoidalBallAnimation" -> SinusoidalBallAnimation()

                "BouncingBallAnimation" -> BouncingBallAnimation()

                "Watch" -> Watch()

                "Confetti" -> Confeti()

                "Carrousel" ->
                    Carousel()

                "TextAnimation" ->
                    TextAnimation()

                else -> {
                    ErrorPage(contentPadding)
                }
            }
        }
    }
}

@Composable
fun ParticlesBox(innerPadding: PaddingValues) {
    Box(modifier = Modifier.padding(innerPadding)) {
        Particles()
    }
}

@Composable
@Preview
fun AnimationBoxPrev() {
    AnimationBox(PaddingValues(16.dp), createCharacter()) { CharacterAnimation(createCharacter()) }
}

@Composable
fun AnimationBox(
    innerPadding: PaddingValues, viewModel: CharacterViewModel, view: @Composable () -> Unit
) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    colors = listOf(Color(0x8D9DB3FF    ), Color(0xFFFFFFFF)),
                    start = Offset(0.5f, 0f),
                    end = Offset(0.5f, 2000f)
                )
            )
        , contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.bg),
            contentDescription = "bg",
            modifier = Modifier.fillMaxHeight(0.6f),
            contentScale = ContentScale.Crop
        )
        Row(
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .offset(0.dp, 210.dp)
        ) {

            // Invoca la funcón de vista correspondiente, hay varias
            view.invoke()

            Column(
                verticalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier
                    .padding(16.dp)
                    .border(1.dp, color = Color.Black)
                    .background(Color.White)
            ) {
                viewModel.animationNames().map { name ->
                    Text(
                        name,
                        modifier = Modifier
                            .padding(horizontal = 3.dp, vertical = 0.dp)
                            .clickable(onClick = {
                                viewModel.changeAnimation(name)
                            })
                    )
                }
            }
        }

    }
}

@Composable
fun Zombies(contentPadding: PaddingValues) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .padding(contentPadding)
            .fillMaxSize()
    ) {
        ZombiAnimation(
            createZombiRandom(), modifier = Modifier
                .size(96.dp, 96.dp)
                .offset(0.dp, 0.dp)
        )
        ZombiAnimation(
            createZombiRandom(), modifier = Modifier
                .size(96.dp, 96.dp)
                .offset(45.dp, 10.dp)
        )
        ZombiAnimation(
            createZombiRandom(), modifier = Modifier
                .size(96.dp, 96.dp)
                .offset(59.dp, 5.dp)
        )
        ZombiAnimation(
            createZombiRandom(), modifier = Modifier
                .size(96.dp, 96.dp)
                .offset(23.dp, 20.dp)
        )
        ZombiAnimation(
            createZombiRandom(), modifier = Modifier
                .size(96.dp, 96.dp)
                .offset(-34.dp, 0.dp)
        )
        ZombiAnimation(
            createZombiRandom(), modifier = Modifier
                .size(96.dp, 96.dp)
                .offset(-100.dp, -5.dp)
        )
        ZombiAnimation(
            createZombiRandom(), modifier = Modifier
                .size(96.dp, 96.dp)
                .offset(-125.dp, 0.dp)
        )
    }
}