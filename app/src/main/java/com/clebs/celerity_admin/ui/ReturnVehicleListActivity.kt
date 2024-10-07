package com.clebs.celerity_admin.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Checkbox
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.RadioButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.clebs.celerity_admin.R
import com.clebs.celerity_admin.dialogs.LoadingDialog
import com.clebs.celerity_admin.factory.MyViewModelFactory
import com.clebs.celerity_admin.network.ApiService
import com.clebs.celerity_admin.network.RetrofitService
import com.clebs.celerity_admin.repo.MainRepo
import com.clebs.celerity_admin.ui.ui.theme.CLSOSMTheme
import com.clebs.celerity_admin.utils.LoadingDialogComposable
import com.clebs.celerity_admin.utils.Prefs
import com.clebs.celerity_admin.utils.ReturnCollectionListItem
import com.clebs.celerity_admin.viewModels.MainViewModel

class ReturnVehicleListActivity : ComponentActivity() {
    private lateinit var mainViewModel: MainViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = ContextCompat.getColor(this, R.color.orange)
        val prefs = Prefs.getInstance(this)
        val apiService = RetrofitService.getInstance().create(ApiService::class.java)
        val mainRepo = MainRepo(apiService)

        mainViewModel =
            ViewModelProvider(this, MyViewModelFactory(mainRepo))[MainViewModel::class.java]
        setContent {
            var showDialog by remember {
                mutableStateOf(false)
            }
            com.clebs.celerity_admin.ui.theme.CLSOSMTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column {
                        VehicleCollectionList(dialogOpen = { showDialog = true })
                        FilterDialog(
                            showDialog = showDialog,
                            onDismissRequest = { showDialog = false })
                        VehicleReturnList(mainViewModel,prefs)
                    }
                }
            }
        }
    }

    @Composable
    fun VehicleReturnList(viewModel: MainViewModel, prefs: Prefs) {
        var showLoadingDialog by remember { mutableStateOf(true) }
        val vehReturnHistory by viewModel.GetVehicleReturnHistory(
            prefs.clebUserId.toInt(),
            true
        ).observeAsState(initial = null)
        if (showLoadingDialog) {
            LoadingDialogComposable(showDialog = true)
        }
        vehReturnHistory?.let {history->
            showLoadingDialog = false
            LazyColumn(Modifier.fillMaxSize()) {
                items(history!!.size) {it->
                    ReturnCollectionListItem(Modifier.fillMaxWidth(),history[it])
                }
            }
        }?:run{
            LoadingDialogComposable(false)
            Text("No Data Available")
        }
    }

    @Composable
    fun FilterDialog(showDialog: Boolean, onDismissRequest: () -> Unit) {
        if (showDialog) {
            Dialog(onDismissRequest = onDismissRequest) {
                Column(
                    verticalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier
                        .background(Color.White, RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_filter_list_24),
                            contentDescription = "Filter Icon",
                            Modifier.size(width = 20.dp, height = 20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("Filters", color = colorResource(R.color.orange))
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(checked = false,
                            onCheckedChange = {})
                        Text("Show Completed Collections")
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    var selectedItem by remember { mutableStateOf("Option 1") }
                    var expanded by remember { mutableStateOf(false) }
                    val options = listOf("Option 1", "Option 2", "Option 3")
                    var mTextFieldSize by remember { mutableStateOf(Size.Zero) }
                    val icon = if (expanded)
                        Icons.Filled.KeyboardArrowUp
                    else
                        Icons.Filled.KeyboardArrowDown
                    Column(modifier = Modifier.padding(10.dp)) {
                        OutlinedTextField(
                            value = selectedItem,
                            onValueChange = { selectedItem = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .onGloballyPositioned { coordinates ->
                                    // This value is used to assign to
                                    // the DropDown the same width
                                    mTextFieldSize = coordinates.size.toSize()
                                },
                            label = { Text("Select DA Location") },
                            trailingIcon = {
                                Icon(icon, "contentDescription",
                                    Modifier.clickable { expanded = !expanded })
                            }
                        )
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                            modifier = Modifier.width(with(LocalDensity.current) { mTextFieldSize.width.toDp() })
                        ) {
                            options.forEach { label ->
                                DropdownMenuItem(onClick = {
                                    selectedItem = label
                                    expanded = false
                                }) {
                                    Text(text = label)
                                }
                            }
                        }
                    }
                    /*                    Row(
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            RadioButton(selected = false, onClick = { *//*TODO*//* })
                        Text("Collect Vehicle")

                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(selected = true, onClick = { *//*TODO*//* })
                        Text("Return Vehicle")
                    }*/

                    Row(
                        horizontalArrangement = Arrangement.End,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        TextButton(onClick = onDismissRequest) {
                            Text(
                                text = "Cancel",
                                color = Color.Blue
                            )
                        }
                        TextButton(onClick = onDismissRequest) {
                            Text(
                                text = "OK",
                                color = Color.Blue
                            )
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun VehicleCollectionList(dialogOpen: () -> Unit) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(0.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = colorResource(id = R.color.orange))
                    .padding(vertical = 12.dp, horizontal = 12.dp),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { finish() }, modifier = Modifier.size(16.dp)) {
                    Icon(
                        painter = painterResource(id = R.drawable.back1),
                        tint = Color.White,
                        contentDescription = "Back Button"
                    )
                }
                Text(
                    text = "Return Vehicle List",
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(color = colorResource(id = R.color.orange)),
                    textAlign = TextAlign.Center,
                    color = colorResource(id = R.color.white),
                    fontSize = 14.sp,
                )
            }
            //hiding search options
/*            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = colorResource(id = R.color.orange))
                    .padding(vertical = 12.dp, horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(vertical = 5.dp, horizontal = 12.dp),
                    elevation = CardDefaults.cardElevation(8.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextField(value = "",
                            onValueChange = {

                            },
                            placeholder = {
                                Text(
                                    text = "Search",
                                    fontSize = 12.sp
                                )
                            })
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            painter = painterResource(id = R.drawable.search2),
                            contentDescription = "Search",
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(onClick = dialogOpen) {
                    Icon(
                        painter = painterResource(id = R.drawable.sort),
                        contentDescription = "Search",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }*/
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun Preview() {
        com.clebs.celerity_admin.ui.theme.CLSOSMTheme {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {

                //VehicleCollectionList()
            }
        }
    }

    @Preview
    @Composable
    fun FilterPreview() {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            FilterDialog(true) {}
        }
    }
}