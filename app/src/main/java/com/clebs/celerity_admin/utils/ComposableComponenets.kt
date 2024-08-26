package com.clebs.celerity_admin.utils

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.clebs.celerity_admin.R

@Preview
@Composable
fun ListItemX(modifier: Modifier = Modifier) {
    var expanded by remember {
        mutableStateOf(false)
    }
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(4.dp),
        border = BorderStroke(2.dp, colorResource(id = R.color.cardbg)),
        colors = CardDefaults.cardColors(
            containerColor = colorResource(id = R.color.cardHeader)
        )
    ) {
        Column(
            modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "Collection No : ", fontSize = 10.sp)
                Spacer(modifier = Modifier.width(5.dp))
                Text(text = "197250202482316575969", fontSize = 12.sp)
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
                    Text(text = "Hire Company", fontSize = 10.sp)
                    Spacer(modifier = Modifier.height(5.dp))
                    Text(text = "Avis", fontSize = 12.sp)
                }
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "Collection Request Date", fontSize = 10.sp)
                    Spacer(modifier = Modifier.height(5.dp))
                    Text(text = "23-Aug-2024", fontSize = 12.sp)
                }
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "No of Vehicles", fontSize = 10.sp)
                    Spacer(modifier = Modifier.height(5.dp))
                    Text(text = "2", fontSize = 12.sp)
                }
            }
            if (true) {
                Spacer(modifier = Modifier.height(10.dp))
                Column {
                    Row(
                    ) {
                        Text(text = "Instructed Location \t", fontSize = 10.sp)
                        Spacer(modifier = Modifier.height(5.dp))
                        Text(text = "Bristol - DBS2", fontSize = 12.sp)
                    }
                    Row(
                    ) {
                        Text(text = "Hire Company Address \t", fontSize = 10.sp)
                        Spacer(modifier = Modifier.height(5.dp))
                        Text(text = "Carr wood Road, Castleford, WF10 4SB", fontSize = 12.sp)
                    }

                }
                Spacer(modifier = Modifier.height(10.dp))
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(verticalArrangement = Arrangement.Center) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Collected Count : ", fontSize = 10.sp)
                        Text("0")
                    }
                    Spacer(modifier = Modifier.height(5.dp))
                    Row(
                    ) {
                        Text(text = "Collection Location \t", fontSize = 10.sp)
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
