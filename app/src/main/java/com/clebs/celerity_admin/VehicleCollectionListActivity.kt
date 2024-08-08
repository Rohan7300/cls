package com.clebs.celerity_admin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonColors
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Checkbox
import androidx.compose.material.RadioButton
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import com.clebs.celerity_admin.ui.theme.CLSOSMTheme

class VehicleCollectionListActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = ContextCompat.getColor(this, R.color.orange)
        setContent {
            var showDialog by remember {
                mutableStateOf(false)
            }
            CLSOSMTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    VehicleCollectionList(dialogOpen = { showDialog = true })
                    FilterDialog(showDialog = showDialog, onDismissRequest = { showDialog = false })
                }
            }
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
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.TopEnd
                    ) {
                        IconButton(onClick = onDismissRequest) {
                            Icon(
                                painter = painterResource(id = R.drawable.xmark),
                                contentDescription = "Cross"
                            )
                        }
                    }
                    Text("Filters")
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(checked = false,
                            onCheckedChange = {})
                        Text("Show Completed Collections")
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(selected = true, onClick = { /*TODO*/ })
                        Text("Collect Vehicle")

                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(selected = true, onClick = { /*TODO*/ })
                        Text("Return Vehicle")
                    }
                    Button(
                        onClick = onDismissRequest,
                        colors = ButtonDefaults.buttonColors(
                            contentColor = Color.Red
                        )
                    ) {
                        Text(
                            text = "Dismiss",
                            color = Color.White
                        )
                    }
                }
            }
        }
    }

    @Composable
    fun VehicleCollectionList(dialogOpen: () -> Unit) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(1.dp),
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
                    text = "Vehicle Collection List",
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(color = colorResource(id = R.color.orange)),
                    textAlign = TextAlign.Center,
                    color = colorResource(id = R.color.white),
                    fontSize = 14.sp,
                )
            }
            Row(
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
                            modifier = Modifier.size(20.dp)
                        )
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
            }
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun Preview() {
        CLSOSMTheme {
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
        setContent {
            CLSOSMTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    FilterDialog(true) {}
                }
            }
        }
    }
}




