package io.github.brendonmendicino.aformvalidator.sampleapp

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun UserForm(state: UserFormState = UserFormState()) {
    var userState by remember { mutableStateOf(state.toValidator()) }

    LaunchedEffect(userState) {
        println(userState.toString())
    }

    Column {
        OutlinedTextField(
            userState.name.value ?: "",
            label = { Text("Name") },
            onValueChange = { userState = userState.copy(name = userState.name.update(it)) },
            isError = userState.name.isError,
            supportingText = {
                if (userState.name.isError)
                    Text(stringResource(userState.name.error!!.asRes()))
            }
        )

        OutlinedTextField(
            userState.surname.value ?: "",
            label = { Text("Email") },
            onValueChange = { userState = userState.copy(surname = userState.surname.update(it)) },
            isError = userState.surname.isError,
            supportingText = {
                if (userState.surname.isError)
                    Text(stringResource(userState.surname.error!!.asRes()))
            }
        )

        OutlinedTextField(
            userState.email.value ?: "",
            label = { Text("Surname") },
            onValueChange = { userState = userState.copy(email = userState.email.update(it)) },
            isError = userState.email.isError,
            supportingText = {
                if (userState.email.isError) {
                    Text(stringResource(userState.email.error!!.asRes()))
                }
            }
        )

        OutlinedButton(
            onClick = {
                println(userState.toData())
            },
            enabled = userState.isError.not(),
        ) {
            Text("Submit")
        }

        if (userState.allUsed) {
            userState.errors.firstOrNull()?.let {
                Text(stringResource(it.asRes()))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun UserFormPreview() {
    UserForm(
        state = UserFormState(
            name = "test",
            surname = "world",
            email = "bre@bre.it",
            test = "hello",
            list = listOf(1)
        )
    )
}

@Preview(showBackground = true)
@Composable
fun UserFormErrorsPreview() {
    UserForm(state = UserFormState(name = "  ", surname = " "))
}