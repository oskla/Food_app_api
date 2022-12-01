package com.larsson.food_app_api

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.larsson.food_app_api.ui.theme.Food_app_apiTheme
import com.larsson.food_app_api.viewModel.RestaurantsViewModel
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource


// TODO - Don't scroll to top when pressing restaurant item


class MainActivity : ComponentActivity() {

    private val restaurantsViewModel by viewModels<RestaurantsViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        restaurantsViewModel.getRestaurants(id = null)

        super.onCreate(savedInstanceState)

        setContent {
            Food_app_apiTheme {

                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFFF8F8F8)
                ) {
                    Home(restaurantsViewModel = restaurantsViewModel)
                }
            }
        }
    }
}

@Composable
fun Home(restaurantsViewModel: RestaurantsViewModel){
    val density = LocalDensity.current

    var sizeOfScreen: Int by remember { mutableStateOf(0) }

    Column(
        modifier = Modifier
            .padding(0.dp, 22.dp, 0.dp, 0.dp)
            .onGloballyPositioned {
                sizeOfScreen = it.size.height
                println("size of screen $sizeOfScreen")
            },

        ) {
        Header()
        RestaurantList(restaurantList = restaurantsViewModel.restaurantsResponse, restaurantsViewModel = restaurantsViewModel)
    }
    AnimatedVisibility(
        visible = restaurantsViewModel.detailsVisible,
        enter = slideInVertically {
        with(density) {
            sizeOfScreen.dp.roundToPx()
        }
    },
        exit = slideOutVertically(animationSpec = tween(durationMillis = 500, easing = LinearEasing)) {
        with(density) {
            println("size screen $sizeOfScreen")
            sizeOfScreen.dp.roundToPx()
        }
    }


    ) {
        RestaurantDetail(restaurantsViewModel)
    }
}

@Composable
fun Header() {
    Box(modifier = Modifier.padding(start = 16.dp)) {
        Image(painterResource(R.drawable.logo), contentDescription = "",
            modifier = Modifier
                .height(54.dp)
                .width(54.dp))
    }

}





@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    Food_app_apiTheme {

        Header()
        // FilterItem(item = Filter("safas", "https://elgfors.se/code-test/filter/filter_top_rated.png","toprated"))
        // Home()
        //  InfoBox(restaurant = Restaurant(4, listOf("fasf"), "r3f3", "sad", "wayne", 5.4),)
        // RestaurantItem(restaurant = Restaurant(5,listOf("345", "534"), "afaw","https://elgfors.se/code-test/restaurant/burgers.png","hello", 5.6))
        //  RestaurantList(restaurantList = Restaurants(listOf((Restaurant(25, listOf("323"), "45", "https://elgfors.se/code-test/restaurant/burgers.png", "Burgers", 3.4)))))
    }
}