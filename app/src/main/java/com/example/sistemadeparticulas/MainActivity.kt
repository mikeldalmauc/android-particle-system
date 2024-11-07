package com.example.sistemadeparticulas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.sistemadeparticulas.ui.theme.SistemaDeParticulasTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SistemaDeParticulasTheme {
                NavigationDrawer()
            }
        }
    }
}

@Composable
fun NavigationDrawer(){
    var item = remember { mutableStateOf("Character") }

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Text("Ejemplos", modifier = Modifier.padding(16.dp))
                HorizontalDivider()
                NavigationDrawerItem(
                    label = { Text(text = "Particles") },
                    selected = false,
                    onClick = {
                        item.value = "Particles"
                    }
                )
                NavigationDrawerItem(
                    label = { Text(text = "Character") },
                    selected = false,
                    onClick = {
                        item.value = "Character"
                    }
                )
                NavigationDrawerItem(
                    label = { Text(text = "Zombi") },
                    selected = false,
                    onClick = {
                        item.value = "Zombi"
                    }
                )
                NavigationDrawerItem(
                    label = { Text(text = "Zombis") },
                    selected = false,
                    onClick = {
                        item.value = "Zombis"
                    }
                )
            }
        },
    ) {
        Scaffold(
            floatingActionButton = {
                ExtendedFloatingActionButton(
                    text = { Text("Browse Examples") },
                    icon = { Icon(Icons.Filled.Search, contentDescription = "") },
                    onClick = {
                        scope.launch {
                            drawerState.apply {
                                if (isClosed) open() else close()
                            }
                        }
                    }
                )
            }
        ) { contentPadding ->

            when(item.value){
                "Particles" -> ParticlesBox(PaddingValues(16.dp))
                "Character" -> {
                    val char = createCharacter()
                    AnimationBox(PaddingValues(16.dp), char) { CharacterAnimation(char) }
                }
                "Zombi" -> {
                    val char = createZombi()
                    AnimationBox(PaddingValues(16.dp), char) { ZombiAnimation(char) }
                }
                "Zombis" -> {
                    Box(contentAlignment = Alignment.Center,
                        modifier = Modifier.padding(contentPadding)
                            .fillMaxSize()
                    ) {
                        ZombiAnimation(createZombiRandom(), modifier = Modifier.size(96.dp, 96.dp).offset(0.dp, 0.dp))
                        ZombiAnimation(createZombiRandom(), modifier = Modifier.size(96.dp, 96.dp).offset(45.dp, 10.dp))
                        ZombiAnimation(createZombiRandom(), modifier = Modifier.size(96.dp, 96.dp).offset(59.dp, 5.dp))
                        ZombiAnimation(createZombiRandom(), modifier = Modifier.size(96.dp, 96.dp).offset(23.dp, 20.dp))
                        ZombiAnimation(createZombiRandom(), modifier = Modifier.size(96.dp, 96.dp).offset(-34.dp, 0.dp))
                        ZombiAnimation(createZombiRandom(), modifier = Modifier.size(96.dp, 96.dp).offset(-100.dp, -5.dp))
                        ZombiAnimation(createZombiRandom(), modifier = Modifier.size(96.dp, 96.dp).offset(-125.dp, 0.dp))

                    }
                }
                else -> ParticlesBox(PaddingValues(16.dp))
            }
        }
    }
}

@Composable
fun ParticlesBox(innerPadding: PaddingValues){
    Box(modifier = Modifier.padding(innerPadding)){
        Particles()
    }
}


@Composable
@Preview
fun AnimationPreview() {
    SistemaDeParticulasTheme {
        val char = createCharacter()
        AnimationBox(PaddingValues(16.dp), char) { CharacterAnimation(char) }
    }
}


@Composable
fun AnimationBox(innerPadding: PaddingValues, viewModel: CharacterViewModel,  view: @Composable () -> Unit){

    Box(modifier = Modifier
        .padding(innerPadding)
        .fillMaxSize(),
        contentAlignment = Alignment.Center
    ){
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ){

            // Invoca la funcón de vista correspondiente, hay varias
            view.invoke()

            Column(verticalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier
                    .padding(16.dp)
                    .border(1.dp, color = Color.Black)
            )
            {
                viewModel.animationNames().map { name ->
                    Text(name, modifier = Modifier
                        .padding(horizontal = 3.dp, vertical = 0.dp)
                        .clickable(onClick = {
                            viewModel.changeAnimation(name)
                        }))
                }
            }
        }

    }
}