package com.clebs.celerity_admin.utils

import android.content.Intent
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.clebs.celerity_admin.R
import com.clebs.celerity_admin.VehicleCollectionListActivity
import com.clebs.celerity_admin.models.GetVehicleCollectionHistoryResponseItem
import com.clebs.celerity_admin.models.GetVehicleReturnHistoryResponseItem
import com.clebs.celerity_admin.ui.SubmitReturnActivity
import com.clebs.celerity_admin.ui.ReturnVehicleListActivity
import com.clebs.celerity_admin.ui.CollectVehicleFromSupplier

@Composable
fun CollectionListItem(
    modifier: Modifier = Modifier,
    item: GetVehicleCollectionHistoryResponseItem,
    context: VehicleCollectionListActivity
) {
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
                    text = item.VehUnqueNo,
                    fontSize = 12.sp,
                    modifier = Modifier.weight(1f)
                )
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
                    Text(text = item.CompanyName, fontSize = 12.sp)
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
                    Text(text = convertDateFormat(item.MasterHireStartDate), fontSize = 12.sp)
                }
                Row(
                    horizontalArrangement = Arrangement.End
                ) {
                    IconButton(onClick = { expanded = !expanded }) {
                        if (expanded)
                            Icon(
                                painter = painterResource(id = R.drawable.dropup),
                                tint = Color.DarkGray,
                                contentDescription = "Expand Button",
                                modifier = Modifier
                                    .size(20.dp)
                            ) else
                            Icon(
                                painter = painterResource(id = R.drawable.dropdown),
                                tint = Color.DarkGray,
                                contentDescription = "Collapse Button",
                                modifier = Modifier
                                    .size(20.dp)
                            )
                    }
                }
            }
            Spacer(modifier = Modifier.width(5.dp))

            if (expanded) {
                Spacer(modifier = Modifier.height(10.dp))
                Column {
                    Row(
                    ) {
                        Text(
                            text = "Collection Location \t",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(5.dp))
                        Text(text = item.CompanyAddress, fontSize = 12.sp)
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                    ) {
                        Text(
                            text = "Hire Company Address \t",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(5.dp))
                        Text(text = "${item.CompanyAddress}", fontSize = 12.sp)
                    }

                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
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
                    onClick = {
                        context.startActivity(
                            Intent(
                                context,
                                CollectVehicleFromSupplier::class.java
                            )
                        )
                    },
                    colors = ButtonDefaults.buttonColors(colorResource(id = R.color.greenBtn)),
                    contentPadding = PaddingValues(vertical = 8.dp, horizontal = 12.dp)
                ) {
                    Text(
                        text = "COLLECT",
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
fun ReturnCollectionListItem(
    modifier: Modifier = Modifier,
    item: GetVehicleReturnHistoryResponseItem,
    context: ReturnVehicleListActivity
) {
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
                    text = "Company Name\t",
                    fontSize = 10.sp,
                    color = colorResource(id = R.color.orange),
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(5.dp))
                Text(
                    text = item.CompanyName,
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
                        text = "Hire Company",
                        fontSize = 12.sp, color = colorResource(id = R.color.orange),
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(5.dp))
                    Text(text = item.CompanyName, fontSize = 12.sp)
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
                    Text(text = convertDateFormat(item.VehRetReqDate), fontSize = 12.sp)
                }
                Row(
                    horizontalArrangement = Arrangement.End
                ) {
                    IconButton(onClick = { expanded = !expanded }) {
                        if (expanded)
                            Icon(
                                painter = painterResource(id = R.drawable.dropup),
                                tint = Color.DarkGray,
                                contentDescription = "Expand Button",
                                modifier = Modifier
                                    .size(20.dp)
                            ) else
                            Icon(
                                painter = painterResource(id = R.drawable.dropdown),
                                tint = Color.DarkGray,
                                contentDescription = "Collapse Button",
                                modifier = Modifier
                                    .size(20.dp)
                            )
                    }
                }
                /*                Column(
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
                                }*/
            }
            Spacer(modifier = Modifier.height(10.dp))




            if (expanded) {
                Column {
                    Spacer(modifier = Modifier.height(5.dp))
                    Row(
                    ) {
                        Text(
                            text = "Hire Company Address \t",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = item.CompanyAddress, fontSize = 12.sp)
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                Column(verticalArrangement = Arrangement.Center) {
                    Row(
                    ) {
                        Text(
                            text = "Return Location \t",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = item.CompanyAddress, fontSize = 11.sp)
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
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
                    onClick = {
                        context.startActivity(
                            Intent(
                                context,
                                SubmitReturnActivity::class.java
                            )
                        )
                    },
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
                ) {
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CollectionNotReadyPopUp() {
    var isChecked by remember { mutableStateOf(false) }
    var textReason by remember {
        mutableStateOf("")
    }
    Dialog(onDismissRequest = {}) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = colorResource(id = R.color.white),
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                IconButton(onClick = { /*TODO*/ }) {
                    Icon(
                        painter = painterResource(id = R.drawable.carcross),
                        contentDescription = "Car Image",
                        modifier = Modifier.size(45.dp),
                        tint = colorResource(id = R.color.medium_orange)
                    )
                }
                Text(
                    "Vehicle Not Ready",
                    color = colorResource(id = R.color.black),
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(
                value = textReason, onValueChange = { textReason = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(top = 16.dp),
                singleLine = false,
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done
                ),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    unfocusedBorderColor = colorResource(id = R.color.medium_orange),
                    focusedBorderColor = colorResource(id = R.color.medium_orange)
                ),
                placeholder = {
                    Text(
                        text = "Vehicle Not Ready Reason",
                        color = colorResource(id = R.color.grey_main),
                        fontSize = 12.sp
                    )
                }
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = isChecked,
                    onCheckedChange = { isChecked = it },
                    colors = CheckboxDefaults.colors(
                        checkedColor = colorResource(id = R.color.medium_orange)
                    )
                )
                Text(
                    "Are you sure you want to set vehicle as not ready.",
                    modifier = Modifier.padding(start = 8.dp),
                    color = colorResource(id = R.color.text_color),
                    fontSize = 12.sp
                )
            }
            Spacer(modifier = Modifier.height(15.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {

            }
        }
    }
}

@Preview
@Composable
fun Preview() {
    CollectionNotReadyPopUp()
}

