package kr.ac.kumoh.ce.s20241071.s26w08navigation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import kotlinx.serialization.Serializable
import kr.ac.kumoh.ce.s20241071.s26w08navigation.ui.theme.S26W08NavigationTheme
import kr.ac.kumoh.ce.s20241071.s26w08navigation.viewmodel.PokemonDetailViewModel
import kr.ac.kumoh.ce.s20241071.s26w08navigation.viewmodel.PokemonListViewModel
import java.util.Map.entry

sealed interface PokemonRoute : NavKey {
    @Serializable
    data object PokemonList : PokemonRoute

    @Serializable
    data class PokemonDetail(val pokemonId: Int) : PokemonRoute
}



class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            S26W08NavigationTheme {
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen() {
    val backStack = rememberNavBackStack(PokemonRoute.PokemonList)

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        NavDisplay(
            modifier = Modifier.padding(innerPadding),
            backStack = backStack,

            entryDecorators = listOf(
                // Add the default decorators for managing scenes and saving state
                rememberSaveableStateHolderNavEntryDecorator(),
            ),
            entryProvider = entryProvider {
                entry<PokemonRoute.PokemonList> {
                    PokemonListScreen(

                        onNavigateToDetail = { id ->
                            backStack.add(PokemonRoute.PokemonDetail(pokemonId = id))
                        }
                    )
                }
                entry<PokemonRoute.PokemonDetail> { key ->
                    PokemonDetailScreen(key.pokemonId)
                    }

            })

    }
}


@Composable
fun PokemonListScreen(
//    pokemonList: List<Pokemon>,
    onNavigateToDetail: (Int) -> Unit,
    viewModel: PokemonListViewModel = viewModel()
) {
    val pokemonList by viewModel.pokemonList.collectAsStateWithLifecycle()

    LazyColumn {
        items(pokemonList) { pokemon ->
            Button(
                onClick = {
                    onNavigateToDetail(pokemon.id)
                }
            ) {
                Text(pokemon.name)
            }
        }
    }
}
@Composable
fun PokemonDetailScreen(
    pokemonId: Int,
    viewModel: PokemonDetailViewModel = viewModel(),
) {
    val pokemon = viewModel.getPokemonById(pokemonId)

    pokemon?.let {
        Text(it.info)
    }
}