package com.step.krm.findfalcone

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.step.krm.findfalcone.ui.theme.FindFalconeTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

data class Planet(val name: String, val distance: Int)
data class Vehicle(val name: String, val total_no: Int, val max_distance: Int, val speed: Int)

object RetrofitInstance {
    private const val BASE_URL = "https://findfalcone.geektrust.com/"

    val service: FalconeService by lazy {
        Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create())
            .build().create(FalconeService::class.java)
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            FindFalconeTheme {
                LandingPage(viewModel = FalconeViewModel())
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun DropDownSelection(label: String, viewModel: FalconeViewModel) {
    val planets: List<Planet>? by viewModel.planets.observeAsState(null)
    val vehicles: List<Vehicle>? by viewModel.vehicles.observeAsState(null)

    var isExpanded by remember { mutableStateOf(false) }
    var selectedPlanet: Planet? by remember { mutableStateOf(null) }

    var selectedVehicle: Vehicle? by remember { mutableStateOf(null) }

    LocalSoftwareKeyboardController.current?.hide()

    LaunchedEffect(Unit) {
        viewModel.fetchPlanets()
        viewModel.fetchVehicles()
    }

    Column {
        ExposedDropdownMenuBox(
            expanded = isExpanded,
            onExpandedChange = { isExpanded = !isExpanded },
        ) {
            OutlinedTextField(
                readOnly = true,
                value = selectedPlanet?.name ?: "",
                onValueChange = {},
                label = { Text(text = label) },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded)
                },
                colors = OutlinedTextFieldDefaults.colors(),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.None),
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = isExpanded,
                onDismissRequest = { isExpanded = false }) {
                planets?.forEach { planet: Planet ->
                    DropdownMenuItem(text = { Text(text = planet.name) }, onClick = {
                        isExpanded = false
                        selectedPlanet = planet
                    })
                }
            }
        }
        if (selectedPlanet != null) {
            vehicles?.forEach { vehicle ->
                Row(verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .clickable { selectedVehicle = vehicle }) {
                    RadioButton(selected = (vehicle == selectedVehicle),
                        onClick = { selectedVehicle = vehicle })
                    Text(
                        text = "${vehicle.name} (${vehicle.total_no})",
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun LandingPage(viewModel: FalconeViewModel) {
    val scrollState = rememberScrollState()
    FindFalconeTheme {
        Column(
            modifier = Modifier
                .padding(all = 8.dp)
                .fillMaxHeight()
                .fillMaxWidth()
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            Text(
                text = "Finding Falcone !",
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(20.dp)
            )
            Text(
                text = "Select panels you want to search in",
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(20.dp)
            )
            DropDownSelection("Destination 1", viewModel)
            DropDownSelection("Destination 2", viewModel)
            DropDownSelection("Destination 3", viewModel)
            DropDownSelection("Destination 4", viewModel)

        }
    }
}
