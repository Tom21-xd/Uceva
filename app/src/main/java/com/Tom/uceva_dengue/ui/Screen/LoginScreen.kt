package com.Tom.uceva_dengue.ui.Screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.Tom.uceva_dengue.R
import com.Tom.uceva_dengue.ui.Navigation.Rout
import com.Tom.uceva_dengue.ui.theme.fondo
import com.Tom.uceva_dengue.ui.viewModel.AuthViewModel


@Composable
fun LoginScreen (viewModel: AuthViewModel, navController: NavController){
    Box(Modifier.background(color=fondo)){
        Box(
            Modifier
                .fillMaxSize()
                .padding(20.dp)){
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.Center, // Esto centra los elementos en la fila
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally // Esto alinea el contenido dentro de la columna
                ) {
                    Text(
                        text = "Iniciar sesión",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF000000),
                        modifier = Modifier
                            .clickable { viewModel.log_regis.value = false }
                            .padding(10.dp)
                    )
                    HorizontalDivider(
                        modifier = Modifier.fillMaxWidth(0.34f),
                        thickness = 2.dp,
                        color = Color(0xFF000000)
                    )
                }

                Spacer(modifier = Modifier.width(25.dp)) // Añadir espacio entre los elementos

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally // Alineación centrada en esta columna
                ) {
                    Text(
                        text = "Crear cuenta",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF000000),
                        modifier = Modifier
                            .clickable { viewModel.log_regis.value = true }
                            .padding(10.dp)
                    )
                    HorizontalDivider(
                        modifier = Modifier.fillMaxWidth(0.66f),
                        thickness = 2.dp,
                        color = Color(0xFF000000)
                    )
                }
            }

            val logRegis : Boolean by viewModel.log_regis.observeAsState(initial = false)
            AnimatedVisibility(visible = !logRegis) {
                Login(Modifier.align(Alignment.Center), viewModel, navController)
            }
            AnimatedVisibility(visible = logRegis) {
                Registro(Modifier.align(Alignment.Center), viewModel, navController)
            }
        }
    }
}



@Composable
fun Login(modifier: Modifier, viewModel: AuthViewModel, navController: NavController) {

    val correo : String by viewModel.correo.observeAsState(initial = "")
    val contra : String by viewModel.contra.observeAsState(initial = "")
    val loginEneable : Boolean by viewModel.loginEnabled.observeAsState(initial = false)
    val contravisible : Boolean by viewModel.contravisible.observeAsState(initial = false)

    Box(modifier = modifier.fillMaxSize()){

        Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
            HeaderImage(Modifier.align(Alignment.CenterHorizontally))
            Spacer(modifier = Modifier.padding(10.dp))
            CampoCorreo(correo) { viewModel.onLoginChange(it, contra) }
            Spacer(modifier = Modifier.padding(5.dp))
            CampoContra(contra,viewModel,contravisible) { viewModel.onLoginChange(correo, it) }
            Spacer(modifier = Modifier.padding(15.dp))
            BotonInicio(Modifier.align(Alignment.CenterHorizontally),loginEneable,viewModel,correo,contra,navController)
        }
        OlvContra(Modifier.align(Alignment.BottomCenter))
    }

}

@Composable
fun BotonInicio(modifier: Modifier, loginEneable: Boolean, viewModel: AuthViewModel, correo:String, contra:String, navController: NavController) {
    Button(
        onClick = { viewModel.iniciosesioncorreo(correo,contra){navController.navigate(Rout.HomeScreen.name)}},
        modifier = modifier
            .width(200.dp)
            .height(48.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE1E3DA)),
        enabled = loginEneable
    ) {
        Text(text = "Iniciar sesión", color = Color(0xFF000000))

    }
}



@Composable
fun OlvContra(modifier: Modifier) {
    Text(

        text = "¿Olvidaste la contraseña?",
        modifier = modifier.clickable {},
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF666057)
    )
}

@Composable
fun CampoContra(contra:String,  viewModel: AuthViewModel,contravisible: Boolean , onTextFieldChanged: (String) -> Unit) {

    val transformacionvisual = if (viewModel.contravisible.value == true){
        VisualTransformation.None
    } else {
        PasswordVisualTransformation()
    }

    OutlinedTextField(value = contra,
        onValueChange = { onTextFieldChanged(it)},
        Modifier.fillMaxWidth(),
        placeholder = { Text(text = "Contraseña") } ,
        visualTransformation = transformacionvisual,
        keyboardOptions = KeyboardOptions(keyboardType =  KeyboardType.Password),
        singleLine = true,
        trailingIcon = { Contravisibleicono(viewModel,contravisible) },
        maxLines = 1)
}

@Composable
fun Contravisibleicono(viewModel: AuthViewModel, contravisible: Boolean) {
    val image =
        if (viewModel.contravisible.value == true){
            Icons.Default.VisibilityOff
        } else {
            Icons.Default.Visibility
        }
    IconButton(onClick = {viewModel.onContraVisibilityChange(!contravisible) }){
        Icon(imageVector = image, contentDescription ="a" )

    }
}

@Composable
fun CampoCorreo(correo:String , onTextFieldChanged: (String) -> Unit) {
    OutlinedTextField(value = correo,
        onValueChange = {onTextFieldChanged(it)},
        Modifier.fillMaxWidth(),
        placeholder = { Text(text = "Correo") } ,
        keyboardOptions = KeyboardOptions(keyboardType =  KeyboardType.Email),
        singleLine = true,
        maxLines = 1)
}

@Composable
fun HeaderImage(modifier: Modifier) {
    Image(painter = painterResource(id = R.drawable.salud ), contentDescription = "Header" ,modifier=modifier)
}
/*-----------------------------------------------Registro---------------------------------------------------------*/
@Composable
fun Registro(modifier: Modifier, viewModel:AuthViewModel, navController: NavController) {


    val nombres : String by viewModel.nombres.observeAsState(initial = "")
    val correo : String by viewModel.correo.observeAsState(initial = "")
    val contra : String by viewModel.contra.observeAsState(initial = "")
    val confirmacionContra : String by viewModel.confirmacionContra.observeAsState(initial = "")
    val apellidos : String by viewModel.apellidos.observeAsState(initial = "")
    val departamento : String by viewModel.departamento.observeAsState(initial = "Seleccione")
    val ciudad : String by viewModel.ciudad.observeAsState(initial = "Seleccione")
    val direccion : String by viewModel.direccion.observeAsState(initial = "")
    val personalMedico : Boolean by viewModel.personalMedico.observeAsState(initial = false)


    Box(modifier = modifier.fillMaxSize()){
        Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "¿Como te llamas?", fontSize = 25.sp, fontWeight = FontWeight.Bold, color = Color(0xFF000000), modifier = Modifier
                .clickable { viewModel.log_regis.value = true })

            Spacer(modifier = Modifier.padding(10.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.weight(1f)) {
                    Campo(nombres,"Nombres") { viewModel.OnRegisterChange(correo, contra, confirmacionContra, it, apellidos, departamento, ciudad, direccion, personalMedico) }
                }
                Spacer(modifier = Modifier.padding(5.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Campo(apellidos,"Apellidos") { viewModel.OnRegisterChange(correo, contra, confirmacionContra, nombres, it, departamento, ciudad, direccion, personalMedico) }
                }
            }
            Spacer(modifier = Modifier.padding(10.dp))
            Row (modifier){
                Column(modifier=Modifier.weight(1f)) {
                    DynamicSelectTextField(departamento,fruits,"Departamento"){viewModel.OnRegisterChange(correo, contra, confirmacionContra, nombres, apellidos, it, ciudad, direccion, personalMedico) }
                }
                Spacer(modifier = Modifier.padding(5.dp))
                Column(modifier=Modifier.weight(1f)) {
                    DynamicSelectTextField(ciudad,fruits,"Ciudad"){viewModel.OnRegisterChange(correo, contra, confirmacionContra, nombres, apellidos, departamento, it, direccion, personalMedico) }
                }

            }
        }
    }

}

@Composable
fun Campo(dato: String,nombre:String, onTextFieldChanged: (String) -> Unit) {
    OutlinedTextField(value = dato,
        onValueChange = { onTextFieldChanged(it)},
        Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions(keyboardType =  KeyboardType.Text),
        singleLine = true,
        label = { Text(nombre) },
        maxLines = 1)
}


val fruits: List<String> = listOf("Apple", "Banana", "Strawberry")


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DynamicSelectTextField(
    selectedValue: String,
    options: List<String>,
    label: String,
    onValueChangedEvent: (String) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
    ) {
        OutlinedTextField(
            readOnly = true,
            value = selectedValue,
            onValueChange = {},
            label = { Text(text = label) },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            colors = OutlinedTextFieldDefaults.colors(),
            modifier = Modifier
                .fillMaxWidth()
        )

        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { option: String ->
                DropdownMenuItem(
                    text = { Text(text = option) },
                    onClick = {
                        expanded = false
                        onValueChangedEvent(option)
                    }
                )
            }
        }
    }
}