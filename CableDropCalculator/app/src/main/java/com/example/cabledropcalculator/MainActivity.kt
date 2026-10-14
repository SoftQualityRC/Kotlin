package com.example.cabledropcalculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Если у вас падает ошибка на тему, можно использовать стандартный MaterialTheme напрямую:
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    CableCalculatorScreen()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CableCalculatorScreen() {
    var lengthInput by remember { mutableStateOf("10") }
    var currentInput by remember { mutableStateOf("16") }
    var gaugeInput by remember { mutableStateOf("2.5") }
    var voltageInput by remember { mutableStateOf("230") }
    var isCopper by remember { mutableStateOf(true) }
    var isSinglePhase by remember { mutableStateOf(true) }

    // Вычисления в реальном времени
    val length = lengthInput.toDoubleOrNull() ?: 0.0
    val current = currentInput.toDoubleOrNull() ?: 0.0
    val gauge = gaugeInput.toDoubleOrNull() ?: 0.0
    val voltage = voltageInput.toDoubleOrNull() ?: 230.0
    val rho = if (isCopper) ConductorMathEngine.RHO_COPPER else ConductorMathEngine.RHO_ALUMINIUM
    val phaseCoeff = if (isSinglePhase) ConductorMathEngine.PHASE_SINGLE else ConductorMathEngine.PHASE_TRIPLE_SYMMETRIC

    val dropVolts = remember(length, current, gauge, isCopper, isSinglePhase) {
        if (length > 0 && current > 0 && gauge > 0) {
            ConductorMathEngine.calculateVoltageDropVolts(length, phaseCoeff, rho, gauge, null, null, current)
        } else 0.0
    }

    val dropPercent = remember(dropVolts, voltage) {
        if (voltage > 0) (dropVolts * 100.0) / voltage else 0.0
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Калькулятор падения напряжения") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Карточка с результатами
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Результат расчета",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Падение напряжения: %.2f В".format(dropVolts),
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = "Потери: %.2f %%".format(dropPercent),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }

            // Выбор материала проводника
            Text("Материал провода", style = MaterialTheme.typography.labelLarge)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { isCopper = true },
                    modifier = Modifier.weight(1f), // Исправлено с 1.dp на 1f
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isCopper) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = if (isCopper) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                ) {
                    Text("Медь")
                }
                Button(
                    onClick = { isCopper = false },
                    modifier = Modifier.weight(1f), // Исправлено с 1.dp на 1f
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (!isCopper) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = if (!isCopper) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                ) {
                    Text("Алюминий")
                }
            }

            // Выбор типа сети
            Text("Тип сети", style = MaterialTheme.typography.labelLarge)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { isSinglePhase = true },
                    modifier = Modifier.weight(1f), // Исправлено с 1.dp на 1f
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isSinglePhase) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = if (isSinglePhase) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                ) {
                    Text("Однофазная (220В)")
                }
                Button(
                    onClick = { isSinglePhase = false },
                    modifier = Modifier.weight(1f), // Исправлено с 1.dp на 1f
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (!isSinglePhase) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = if (!isSinglePhase) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                ) {
                    Text("Трехфазная (380В)")
                }
            }

            // Поля ввода
            OutlinedTextField(
                value = lengthInput,
                onValueChange = { lengthInput = it },
                label = { Text("Длина провода (м)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = currentInput,
                onValueChange = { currentInput = it },
                label = { Text("Ток (А)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = gaugeInput,
                onValueChange = { gaugeInput = it },
                label = { Text("Сечение провода (мм²)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = voltageInput,
                onValueChange = { voltageInput = it },
                label = { Text("Номинальное напряжение (В)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}