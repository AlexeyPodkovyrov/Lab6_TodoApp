package com.example.todoapp

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private var counter = 0
    private val tasks = mutableListOf<String>()
    private var enteredText = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Блок 1: Счётчик
        val textCounter = findViewById<TextView>(R.id.textCounter)
        val buttonIncrement = findViewById<Button>(R.id.buttonIncrement)
        val buttonResetCounter = findViewById<Button>(R.id.buttonResetCounter)

        updateCounterDisplay(textCounter)

        buttonIncrement.setOnClickListener {
            counter++
            updateCounterDisplay(textCounter)
        }

        buttonResetCounter.setOnClickListener {
            counter = 0
            updateCounterDisplay(textCounter)
        }

        // Блок 2: Полее ввода и отображение текста
        val editTextInput = findViewById<EditText>(R.id.editTextInput)
        val buttonShow = findViewById<Button>(R.id.buttonShow)
        val textEntered = findViewById<TextView>(R.id.textEntered)

        buttonShow.setOnClickListener {
            val inputText = editTextInput.text.toString()
            enteredText = inputText
            textEntered.text = "${getString(R.string.label_entered)} $inputText"
        }

        // Блок 3: ToDo список
        val editTextTask = findViewById<EditText>(R.id.editTextTask)
        val buttonAddTask = findViewById<Button>(R.id.buttonAddTask)
        val buttonDeleteLast = findViewById<Button>(R.id.buttonDeleteLast)
        val textTasks = findViewById<TextView>(R.id.textTasks)
        val textTaskCount = findViewById<TextView>(R.id.textTaskCount)

        savedInstanceState?.let {
            counter = it.getInt("counter", 0)
            enteredText = it.getString("enteredText", "")
            it.getStringArrayList("tasks")?.let { list ->
                tasks.clear()
                tasks.addAll(list)
            }
        }

        updateCounterDisplay(textCounter)

        if (enteredText.isNotEmpty()) {
            textEntered.text = "${getString(R.string.label_entered)} $enteredText"
        }

        updateTasksDisplay(textTasks, textTaskCount)

        buttonAddTask.setOnClickListener {
            val task = editTextTask.text.toString().trim()
            if (task.isNotBlank()) {
                tasks.add(task)
                updateTasksDisplay(textTasks, textTaskCount)
                editTextTask.text.clear()
            } else {
                Toast.makeText(this, R.string.toast_empty_task, Toast.LENGTH_SHORT).show()
            }
        }

        buttonDeleteLast.setOnClickListener {
            if (tasks.isNotEmpty()) {
                tasks.removeAt(tasks.lastIndex)
                updateTasksDisplay(textTasks, textTaskCount)
                Toast.makeText(this, "Последняя задача удалена", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Список задач пуст", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateCounterDisplay(textView: TextView) {
        textView.text = getString(R.string.counter_text, counter)
    }

    private fun updateTasksDisplay(textViewTasks: TextView, textViewCount: TextView) {
        textViewCount.text = getString(R.string.label_task_count, tasks.size)

        if (tasks.isEmpty()) {
            textViewTasks.text = getString(R.string.label_tasks)
        } else {
            textViewTasks.text = tasks.joinToString("\n") { "• $it" }
        }
    }

    // Сохранение состояния
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("counter", counter)
        outState.putString("enteredText", enteredText)
        outState.putStringArrayList("tasks", ArrayList(tasks))
    }

    // Восстановление состояния
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        counter = savedInstanceState.getInt("counter")
        enteredText = savedInstanceState.getString("enteredText", "")
        tasks.clear()
        tasks.addAll(savedInstanceState.getStringArrayList("tasks") ?: emptyList())

        updateCounterDisplay(findViewById(R.id.textCounter))

        if (enteredText.isNotEmpty()) {
            findViewById<TextView>(R.id.textEntered).text =
                "${getString(R.string.label_entered)} $enteredText"
        }

        updateTasksDisplay(
            findViewById(R.id.textTasks),
            findViewById(R.id.textTaskCount)
        )
    }
}