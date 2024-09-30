package com.clebs.celerity_admin.utils

import android.graphics.fonts.Font
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.clebs.celerity_admin.R

@Composable
fun ListItemX(modifier: Modifier = Modifier) {
    var expanded by remember {
        mutableStateOf(false)
    }
    var showDialog by remember {
        mutableStateOf(false)
    }
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(4.dp),
        border = BorderStroke(1.dp, colorResource(id = R.color.cardbg)),
        elevation = CardDefaults.cardElevation(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorResource(id = R.color.white)
        )
    ) {
        Column(
            modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Collection No : ",
                    fontSize = 10.sp,
                    color = colorResource(id = R.color.orange),
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(5.dp))
                Text(
                    text = "197250202482316575969",
                    fontSize = 12.sp,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(5.dp))
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(
                        painter = painterResource(id = R.drawable.mobile),
                        tint = Color.Black,
                        contentDescription = "Back Button",
                        modifier = Modifier.size(25.dp)
                    )
                }
                Spacer(modifier = Modifier.width(5.dp))
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(
                        painter = painterResource(id = R.drawable.contract),
                        tint = Color.Black,
                        contentDescription = "Back Button",
                        modifier = Modifier.size(25.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Row {
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Hire Company",
                        fontSize = 10.sp, color = colorResource(id = R.color.orange),
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(5.dp))
                    Text(text = "Avis", fontSize = 12.sp)
                }
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Collection Request Date",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = colorResource(id = R.color.orange)
                    )
                    Spacer(modifier = Modifier.height(5.dp))
                    Text(text = "23-Aug-2024", fontSize = 12.sp)
                }
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "No of Vehicles",
                        fontSize = 10.sp,
                        color = colorResource(id = R.color.orange),
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(5.dp))
                    Text(text = "2", fontSize = 12.sp)
                }
            }
            if (expanded) {
                Spacer(modifier = Modifier.height(10.dp))
                Column {
                    Row(
                    ) {
                        Text(
                            text = "Instructed Location \t",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(5.dp))
                        Text(text = "Bristol - DBS2", fontSize = 12.sp)
                    }
                    Spacer(modifier = Modifier.height(5.dp))
                    Row(
                    ) {
                        Text(
                            text = "Hire Company Address \t",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(5.dp))
                        Text(text = "Carr wood Road, Castleford, WF10 4SB", fontSize = 12.sp)
                    }

                }
                Spacer(modifier = Modifier.height(10.dp))
                Row(modifier = Modifier.padding(horizontal = 15.dp)) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Start Date : \t",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(5.dp))
                        Text(text = "24-Aug-2024\t", fontSize = 11.sp)
                    }
                    Spacer(
                        modifier = Modifier
                            .weight(1f)
                            .height(5.dp)
                    )
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "End Date : \t",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(5.dp))
                        Text(text = "31-Aug-2024\t", fontSize = 11.sp)
                    }
                    Spacer(
                        modifier = Modifier
                            .weight(1f)
                            .height(5.dp)
                    )
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            "Collected Count",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(5.dp))
                        Text("0")
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(verticalArrangement = Arrangement.Center) {

                    Spacer(modifier = Modifier.height(5.dp))
                    Row(
                    ) {
                        Text(
                            text = "Collection Location \t",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(5.dp))
                        Text(text = "Carr wood Road, Castleford, WF10 4SB", fontSize = 11.sp)
                    }
                }
                Spacer(
                    modifier = Modifier
                        .height(10.dp)
                        .weight(1f)
                )
                IconButton(onClick = { expanded = !expanded }) {
                    if (expanded)
                        Icon(
                            painter = painterResource(id = R.drawable.direction),
                            tint = Color.Black,
                            contentDescription = "Back Button",
                            modifier = Modifier.size(25.dp)
                        )
                    else
                        Icon(
                            painter = painterResource(id = R.drawable.down),
                            tint = Color.Black,
                            contentDescription = "Back Button",
                            modifier = Modifier.size(25.dp)
                        )
                }
            }
        }
    }
}

@Composable
fun ReturnCollectionListItem(modifier: Modifier = Modifier) {
    var expanded by remember {
        mutableStateOf(false)
    }
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(4.dp),
        border = BorderStroke(1.dp, colorResource(id = R.color.cardbg)),
        elevation = CardDefaults.cardElevation(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorResource(id = R.color.white)
        )
    ) {
        Column(
            modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Hire Company : ",
                    fontSize = 10.sp,
                    color = colorResource(id = R.color.orange),
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(5.dp))
                Text(
                    text = "CLS Test Supplier",
                    fontSize = 12.sp,
                    modifier = Modifier.weight(1f)
                )
/*                Spacer(modifier = Modifier.width(5.dp))
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(
                        painter = painterResource(id = R.drawable.eye),
                        tint = Color.Black,
                        contentDescription = "View Button",
                        modifier = Modifier.size(25.dp)
                    )
                }
                Spacer(modifier = Modifier.width(5.dp))
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(
                        painter = painterResource(id = R.drawable.cancel),
                        tint = Color.Black,
                        contentDescription = "Cross Button",
                        modifier = Modifier.size(25.dp)
                    )
                }*/
            }
            Spacer(modifier = Modifier.height(10.dp))
            Row {
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "No of Vehicles",
                        fontSize = 10.sp, color = colorResource(id = R.color.orange),
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(5.dp))
                    Text(text = "2", fontSize = 12.sp)
                }
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Return Request Date",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = colorResource(id = R.color.orange)
                    )
                    Spacer(modifier = Modifier.height(5.dp))
                    Text(text = "23-Aug-2024", fontSize = 12.sp)
                }
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Returned Count",
                        fontSize = 10.sp,
                        color = colorResource(id = R.color.orange),
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(5.dp))
                    Text(text = "1", fontSize = 12.sp)
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Column {
                Spacer(modifier = Modifier.height(5.dp))
                Row(
                ) {
                    Text(
                        text = "Hire Company Address \t",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(5.dp))
                    Text(text = "Carr wood Road, Castleford, WF10 4SB", fontSize = 12.sp)
                }

            }
            Spacer(modifier = Modifier.height(5.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(verticalArrangement = Arrangement.Center) {
                    Row(
                    ) {
                        Text(
                            text = "Return Location \t",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(5.dp))
                        Text(text = "Carr wood Road, Castleford, WF10 4SB", fontSize = 11.sp)
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End) {
                Button(
                    onClick = { /* Not Ready button click handler */ },
                    colors = ButtonDefaults.buttonColors(colorResource(id = R.color.red_light)),
                    contentPadding = PaddingValues(vertical = 8.dp, horizontal = 12.dp)
                ) {
                    Text(
                        text = "NOT READY",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Button(
                    onClick = { /* Return button click handler */ },
                    colors = ButtonDefaults.buttonColors(colorResource(id = R.color.greenBtn)),
                    contentPadding = PaddingValues(vertical = 8.dp, horizontal = 12.dp)
                ) {
                    Text(
                        text = "RETURN",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun ComDialogVehicleCollection(showDialog: Boolean, onDismissRequest: () -> Unit) {
    if (showDialog) {
        Dialog(onDismissRequest = onDismissRequest) {
            Column(
                verticalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.background(Color.White, RoundedCornerShape(16.dp))
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ){
                    Row {
                        Text(text = "View")
                        Spacer(modifier = Modifier.width(10.dp))
                        IconButton(onClick = { /*TODO*/ }) {
                            Icon(
                                painter = painterResource(id = R.drawable.eye),
                                contentDescription = "View Button",
                                tint = Color.Black,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LoadingDialogComposable(
    showDialog: Boolean
) {
    if (showDialog) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f))
        ) {
            Card(
                modifier = Modifier
                    .size(150.dp)
                    .align(Alignment.Center),
                elevation = CardDefaults.cardElevation(10.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    //CircularProgressIndicator(modifier = Modifier.size(40.dp))
                    Text("Loading...")
                }
            }
        }
    }
}

@Preview
@Composable
fun Preview(){
    ReturnCollectionListItem()
}
