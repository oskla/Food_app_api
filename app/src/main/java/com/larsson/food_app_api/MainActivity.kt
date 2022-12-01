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
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.annotation.ExperimentalCoilApi
import com.larsson.food_app_api.model.Restaurant
import com.larsson.food_app_api.model.Restaurants
import com.larsson.food_app_api.ui.theme.Food_app_apiTheme
import com.larsson.food_app_api.viewModel.RestaurantsViewModel
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.ExperimentalTextApi
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.larsson.food_app_api.model.Filter
import com.larsson.food_app_api.ui.theme.PoppinsFontFamily
import kotlinx.coroutines.launch


// TODO - Don't scroll to top when pressing restaurant item
// TODO - Remove ripple effect when clicking filters
// TODO - How to show multiple filters at once?

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
                    color = MaterialTheme.colors.background
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
            .padding(0.dp, 22.dp, 0.dp, 16.dp)
            .onGloballyPositioned {
                sizeOfScreen = it.size.height
                println("size of screen $sizeOfScreen")
            },

        ) {
        Header()
        RestaurantList(restaurantList = restaurantsViewModel.restaurantsResponse, restaurantsViewModel = restaurantsViewModel)
    }
    AnimatedVisibility(
        visible = restaurantsViewModel.detailsVisable,
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

@Composable
fun HorizontalRow(filters: SnapshotStateList<Filter?>, restaurantsViewModel: RestaurantsViewModel) {

    LazyRow(
        // horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.padding(0.dp,22.dp,0.dp, 6.dp)
        ,content = {
            itemsIndexed(items = filters) { _, filter ->
                if (filter != null) {
                    FilterItem(item = filter, restaurantsViewModel = restaurantsViewModel)
                }

            }
        }
    )
}



@OptIn(ExperimentalCoilApi::class, ExperimentalMaterialApi::class)
@Composable
fun FilterItem(item: Filter, restaurantsViewModel: RestaurantsViewModel) {

    var selected by rememberSaveable { mutableStateOf(false) }

    var filterButtonBgColor = 0xFFFFFFFF
    var filterButtonTextColor = 0xFF1F2B2E

    if (selected) {
        filterButtonBgColor = 0xFFE2A364
        filterButtonTextColor = 0xFFFFFFFF
    }

    Surface (
        onClick = {
            selected = !selected
            if (selected) {
                //restaurantsViewModel.activeFilter = true
                restaurantsViewModel.getFiltredRestaurants(item, false)
            }

            if (!selected) {
                restaurantsViewModel.getFiltredRestaurants(item, true)
            }
                  },
       color = Color(filterButtonBgColor),
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier
            .padding(16.dp, 0.dp, 0.dp, 0.dp)
            .wrapContentWidth()
            .height(48.dp),

        elevation = 6.dp
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,

            ) {

            Image(
                painter = rememberAsyncImagePainter(item.imageUrl),
                contentDescription = null,
                modifier = Modifier.size(48.dp)
            )
            Row(modifier = Modifier
                .padding(8.dp, 0.dp, 16.dp, 0.dp))
            {
                Spacer(modifier = Modifier.weight(1.0f))
                Text(text = item.name, fontSize = 14.sp, fontFamily = PoppinsFontFamily, color = Color(filterButtonTextColor))
                Spacer(modifier = Modifier.weight(1.0f))
            }

        }

    }
}

@Composable
fun RestaurantList(
    restaurantList: Restaurants?,
    restaurantsViewModel: RestaurantsViewModel

) {
    val lazyListState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    LazyColumn (
        verticalArrangement = Arrangement.spacedBy(16.dp),
        state = lazyListState
    ) {

        scope.launch {
            lazyListState.animateScrollToItem(0)
        }

        if (restaurantList != null) {

            item {  HorizontalRow(filters = restaurantsViewModel.filters, restaurantsViewModel = restaurantsViewModel) }
            itemsIndexed(items = restaurantsViewModel.visibleRestaurants) { _, item ->
                RestaurantItem(restaurant = item, restaurantsViewModel = restaurantsViewModel)

            }
        }
    }
}

// Example Top-rated - Eat in etc...
@Composable
fun FiltersTextRow(
    restaurantsViewModel: RestaurantsViewModel,
    restaurant: Restaurant,
    fontSize: Int
) {
    LazyRow(

    ) {

        var index = 1 // for controlling when to show dot after text

        items(items = restaurantsViewModel.filters) { filter ->
            if (filter != null) {
                if (restaurant.filterIds.contains(filter.id)) {
                    Text(
                        text = filter.name,
                        fontSize = fontSize.sp,
                        color = Color.Gray,
                        fontWeight = Bold,
                    )

                    if (index < restaurant.filterIds.size) {
                        Text(
                            text = "  •  ",
                            fontSize = 12.sp,
                            color = Color.Gray,
                            fontWeight = Bold
                        )
                    }
                    index += 1
                }
            }

        }
    }
}

@OptIn(ExperimentalMaterialApi::class, ExperimentalTextApi::class)
@Composable
fun InfoBox(restaurant: Restaurant, restaurantsViewModel: RestaurantsViewModel){
    Card(
        modifier = Modifier
            .wrapContentHeight()
            .height(70.dp)
            .background(Color.White)
            .fillMaxWidth(),
    ) {


        Row(horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically ,modifier = Modifier.padding(8.dp)) {

            Column(
                modifier = Modifier.height(65.dp), verticalArrangement = Arrangement.Center
            ) {
                Text(text = AnnotatedString(restaurant.name), style = TextStyle(fontSize = 18.sp))

                FiltersTextRow(
                    restaurantsViewModel = restaurantsViewModel,
                    restaurant = restaurant,
                    fontSize = 12
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Schedule, "schedule",Modifier.size(10.dp), tint = Color.Red )
                    Text(text = restaurant.deliveryTimeMinutes.toString() + " min", fontSize = 10.sp, modifier = Modifier.padding(3.dp, 0.dp, 0.dp, 0.dp))
                }

            }
            Column(verticalArrangement = Arrangement.Top, modifier = Modifier.fillMaxHeight()) {


                Row(
                    modifier = Modifier
                ) {
                    Icon(Icons.Filled.Star, "star", Modifier.size(14.dp), tint = Color(0xFFF9CA24))
                    Text(text = restaurant.rating.toString(), fontSize = 10.sp, modifier = Modifier.padding(3.dp, 0.dp, 0.dp, 0.dp), fontWeight = Bold, color = Color(0xFF50555C))
                }
            }

        }
    }
}

@OptIn(ExperimentalCoilApi::class)
@Composable
fun RestaurantItem(
    restaurant: Restaurant,
    restaurantsViewModel: RestaurantsViewModel,

    ) {

    val stateChanged by rememberSaveable { mutableStateOf(false) }
    val detailsVisable by rememberSaveable { mutableStateOf(stateChanged) }

    println("detailsvisable from item $detailsVisable")
    Card(modifier = Modifier
        .fillMaxWidth()
        .height(196.dp)
        .padding(horizontal = 16.dp)
        .clickable {
            restaurantsViewModel.detailsVisable = true
            restaurantsViewModel.currentRestaurant = restaurant
        },

        shape = RoundedCornerShape(12.dp, 12.dp, 0.dp, 0.dp),
        elevation = 4.dp
    ) {
        Image(
            painter =
            rememberAsyncImagePainter(
                ImageRequest.Builder(LocalContext.current).data(data = restaurant.imageUrl)
                    .apply(block = fun ImageRequest.Builder.() {

                    }).build()
            ), contentDescription = restaurant.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp, 8.dp, 0.dp, 0.dp))
        )
        Box(contentAlignment = Alignment.BottomStart) {

            InfoBox(restaurant = restaurant, restaurantsViewModel = restaurantsViewModel)

        }
    }
}


@Composable
fun RestaurantDetail(restaurantsViewModel: RestaurantsViewModel) {

    Box() {
 
        var imgSize by remember { mutableStateOf(0) }
        Box(modifier = Modifier
            .fillMaxSize()
            .background(Color.White))


        Column() {

            Image(
                painter = rememberAsyncImagePainter(restaurantsViewModel.currentRestaurant?.imageUrl),
               // painterResource(id = detailsImage),
                contentDescription = null,
                modifier = Modifier
                    .wrapContentHeight()
                    .fillMaxWidth()
                    .onGloballyPositioned {
                        imgSize = it.size.height
                    },
                contentScale = ContentScale.FillWidth
            )
            Box(
                modifier = Modifier
                    .offset(y = -(imgSize / 18).dp)
                    .padding(horizontal = 16.dp)

            ) {
                RestaurantDetailsBox(restaurantsViewModel)

            }
        }

        Box(
            modifier = Modifier
                .padding(8.dp, 22.dp, 0.dp, 0.dp)
                .clickable {
                    restaurantsViewModel.detailsVisable = false
                },
            contentAlignment = Alignment.TopStart,

            ) {
            Icon(Icons.Filled.ExpandMore, "expand_more",Modifier.size(30.dp), tint = Color.Black )
        }
    }

}


@Composable
fun RestaurantDetailsBox(restaurantsViewModel: RestaurantsViewModel) {

    val currentRestaurant = restaurantsViewModel.currentRestaurant

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(144.dp),
        elevation = 6.dp,
        shape = RoundedCornerShape(8.dp)

    ) {
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.padding(16.dp)

        ) {
            restaurantsViewModel.currentRestaurant?.name?.let { Text(text = it, fontSize = 24.sp) }
            if (currentRestaurant != null) {
                FiltersTextRow(restaurantsViewModel = restaurantsViewModel, restaurant = currentRestaurant, fontSize = 14)
            }
            Text(text = "Open",  fontSize = 18.sp, color = Color(0xFF2ECC71))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    Food_app_apiTheme {
        // FilterItem(item = Filter("safas", "https://elgfors.se/code-test/filter/filter_top_rated.png","toprated"))
        // Home()
        //  InfoBox(restaurant = Restaurant(4, listOf("fasf"), "r3f3", "sad", "wayne", 5.4),)
        // RestaurantItem(restaurant = Restaurant(5,listOf("345", "534"), "afaw","https://elgfors.se/code-test/restaurant/burgers.png","hello", 5.6))
        //  RestaurantList(restaurantList = Restaurants(listOf((Restaurant(25, listOf("323"), "45", "https://elgfors.se/code-test/restaurant/burgers.png", "Burgers", 3.4)))))
    }
}