package com.larsson.food_app_api

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.larsson.food_app_api.model.Restaurant
import com.larsson.food_app_api.model.Restaurants
import com.larsson.food_app_api.viewModel.RestaurantsViewModel
import kotlinx.coroutines.launch

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
                Text(text = AnnotatedString(restaurant.name), style = TextStyle(fontSize = 18.sp), color = Color(0xFF1F2B2E))

                FiltersTextRow(
                    restaurantsViewModel = restaurantsViewModel,
                    restaurant = restaurant,
                    fontSize = 12
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Schedule, "schedule", Modifier.size(10.dp), tint = Color.Red )
                    Text(text = restaurant.deliveryTimeMinutes.toString() + " min", fontSize = 10.sp, color = Color(0xFF1F2B2E), modifier = Modifier.padding(3.dp, 0.dp, 0.dp, 0.dp))
                }

            }
            Column(verticalArrangement = Arrangement.Top, modifier = Modifier.fillMaxHeight()) {


                Row(
                    modifier = Modifier
                ) {
                    Icon(Icons.Filled.Star, "star", Modifier.size(14.dp), tint = Color(0xFFF9CA24))
                    Text(text = restaurant.rating.toString(), fontSize = 10.sp, modifier = Modifier.padding(3.dp, 0.dp, 0.dp, 0.dp), fontWeight = FontWeight.Bold, color = Color(0xFF50555C))
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
            restaurantsViewModel.detailsVisible = true
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
            .background(Color(0xFFF8F8F8)))


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
                    restaurantsViewModel.detailsVisible = false
                },
            contentAlignment = Alignment.TopStart,

            ) {
            Icon(Icons.Filled.ExpandMore, "expand_more", Modifier.size(30.dp), tint = Color.Black )
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
            restaurantsViewModel.currentRestaurant?.name?.let { Text(text = it, fontSize = 24.sp, color = Color(0xFF1F2B2E)) }
            if (currentRestaurant != null) {
                FiltersTextRow(restaurantsViewModel = restaurantsViewModel, restaurant = currentRestaurant, fontSize = 14)
            }
            Text(text = "Open",  fontSize = 18.sp, color = Color(0xFF2ECC71))
        }
    }
}