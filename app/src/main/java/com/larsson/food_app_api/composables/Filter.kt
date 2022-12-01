package com.larsson.food_app_api

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberAsyncImagePainter
import com.larsson.food_app_api.model.Filter
import com.larsson.food_app_api.model.Restaurant
import com.larsson.food_app_api.ui.theme.PoppinsFontFamily
import com.larsson.food_app_api.viewModel.RestaurantsViewModel






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
                restaurantsViewModel.getFilteredRestaurants(item, false)
            }

            if (!selected) {
                restaurantsViewModel.getFilteredRestaurants(item, true)
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
                        color = Color(0xFF999999),
                        fontWeight = FontWeight.Bold,
                    )

                    if (index < restaurant.filterIds.size) {
                        Text(
                            text = "  •  ",
                            fontSize = 12.sp,
                            color = Color(0xFF999999),
                            fontWeight = FontWeight.Bold
                        )
                    }
                    index += 1
                }
            }

        }
    }
}