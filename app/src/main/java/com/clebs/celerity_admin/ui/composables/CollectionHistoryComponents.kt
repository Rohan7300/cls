package com.clebs.celerity_admin.ui.composables

import android.content.Intent
import android.service.autofill.SaveCallback
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.clebs.celerity_admin.R
import com.clebs.celerity_admin.VehicleCollectionListActivity
import com.clebs.celerity_admin.models.GetVehicleCollectionHistoryResponseItem
import com.clebs.celerity_admin.ui.CollectVehicleFromSupplier
import com.clebs.celerity_admin.utils.Prefs
import com.clebs.celerity_admin.utils.convertDateFormat
import com.clebs.celerity_admin.utils.showToast
import com.clebs.celerity_admin.viewModels.MainViewModel

@Composable
fun CollectionListItem(
    modifier: Modifier = Modifier,
    item: GetVehicleCollectionHistoryResponseItem,
    context: VehicleCollectionListActivity,
    viewModel: MainViewModel,
    callback: () -> Unit
) {
    var expanded by remember {
        mutableStateOf(false)
    }
    var showDialog by remember {
        mutableStateOf(false)
    }
    val prefs = Prefs.getInstance(context)
    val supervisorId = prefs.osmUserId.toInt()
    val vehCollectionId = item.VehColId
    CollectionNotReadyPopUp(
        showCollectionNotReadyDialog = showDialog,
        dismissRequest = { showDialog = false },
        supervisorId, vehCollectionId,
        context,
        viewModel,
        callback
    )
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
                    onClick = { showDialog = true },
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CollectionNotReadyPopUp(
    showCollectionNotReadyDialog: Boolean,
    dismissRequest: () -> Unit,
    supervisorId: Int,
    vehCollectionId: Int,
    context: VehicleCollectionListActivity,
    viewModel: MainViewModel,
    saveCallback: ()->Unit
) {
    var showLoadingDialog by remember {
        mutableStateOf(false)
    }
    var isChecked by remember { mutableStateOf(false) }
    var textReason by remember {
        mutableStateOf("")
    }
    var isSaveBtnClicked by remember {
        mutableStateOf(false)
    }
    if (isSaveBtnClicked) {
        val SaveVehicleCollectionComment by viewModel.SaveVehicleCollectionComment(
            supervisorId,
            vehCollectionId,
            textReason
        ).observeAsState(initial = null)
        SaveVehicleCollectionComment?.let {
            dismissRequest()
            showLoadingDialog = false
            saveCallback()
            showToast("Saved Successfully!!", context)
        } ?: run {
            showLoadingDialog = false
        }
    }

    if (showLoadingDialog)
        LoadingDialogComposable(showDialog = true)
    else
        LoadingDialogComposable(showDialog = false)

    if (showCollectionNotReadyDialog) {
        Dialog(onDismissRequest = dismissRequest) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = colorResource(id = R.color.white),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.Center
                ) {
                    IconButton(
                        onClick = { /*TODO*/ },
                        modifier = Modifier.background(
                            color = colorResource(id = R.color.grey_main),
                        )
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.carcross),
                            contentDescription = "Car Image",
                            modifier = Modifier.size(40.dp),
                            tint = colorResource(id = R.color.white)
                        )
                    }
                    Spacer(modifier = Modifier.width(5.dp))
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
                        .height(300.dp)
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
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(onClick = dismissRequest) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.padding(start = 5.dp))
                    Button(onClick = {
                        if (textReason.isNotBlank()&&isChecked) {
                            showLoadingDialog = true
                            isSaveBtnClicked = true
                        }
                        else if(textReason.isBlank()){
                            showToast("Please add Reason in comment  before saving!!", context)
                        }
                        else {
                            showToast("Please tick the check box before saving!!", context)
                        }
                    }) {
                        Text("Save")

                    }
                }
            }
        }
    }

}