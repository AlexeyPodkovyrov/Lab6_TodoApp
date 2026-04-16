<p align="center">
  МИНИСТЕРСТВО НАУКИ И ВЫСШЕГО ОБРАЗОВАНИЯ РОССИЙСКОЙ ФЕДЕРАЦИИ ФЕДЕРАЛЬНОЕ<br>
  ГОСУДАРСТВЕННОЕ БЮДЖЕТНОЕ ОБРАЗОВАТЕЛЬНОЕ УЧРЕЖДЕНИЕ ВЫСШЕГО ОБРАЗОВАНИЯ<br>
  «САХАЛИНСКИЙ ГОСУДАРСТВЕННЫЙ УНИВЕРСИТЕТ»
</p>

<br><br><br>

<p align="center">
  Институт естественных наук и техносферной безопасности<br>
  Кафедра информатики<br>
  Подковыров Алексей Игоревич
</p>

<br><br><br>

<p align="center">
  Лабораторная работа №6<br>
  «Отображение списка задач из предыдущей лабораторной в красивых карточках».<br>
  01.03.02 Прикладная математика и информатика
</p>

<br><br><br><br><br><br><br><br><br><br>

<p align="right">
  Научный руководитель<br>
  Соболев Евгений Игоревич
</p>

<br><br><br><br>

<p align="center">
  г. Южно-Сахалинск<br>
  2026 г.
</p>

---

## Цель работы

Научиться использовать RecyclerView для отображения списка данных, освоить создание адаптера и ViewHolder, применить CardView для оформления элементов списка.

---

## Итоговый листинг файла `item_task.xml`

```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.cardview.widget.CardView
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:id="@+id/cardView"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:layout_margin="4dp"
    app:cardCornerRadius="8dp"
    app:cardElevation="2dp"
    app:cardBackgroundColor="#FFFFFF">

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="horizontal"
        android:padding="12dp"
        android:gravity="center_vertical">

        <TextView
            android:id="@+id/textTask"
            android:layout_width="0dp"
            android:layout_height="wrap_content"
            android:layout_weight="1"
            android:textSize="16sp"
            android:textColor="#333333"
            android:paddingEnd="8dp"/>

        <CheckBox
            android:id="@+id/checkTask"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:clickable="true"
            android:focusable="false"/>

    </LinearLayout>

</androidx.cardview.widget.CardView>
```

---

## Итоговый листинг класса `TaskAdapter.kt`

```kotlin
package com.example.todoapp

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class TaskAdapter(
    private val tasks: MutableList<String>,
    private val onTaskCheckedChange: (Int, Boolean) -> Unit,
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    // Состояния чекбоксов
    private val checkedStates = mutableMapOf<Int, Boolean>()

    init {
        // По умолчанию состояние false
        tasks.indices.forEach { index ->
            checkedStates[index] = false
        }
    }

    class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardView: CardView = itemView.findViewById(R.id.cardView)
        val textTask: TextView = itemView.findViewById(R.id.textTask)
        val checkTask: CheckBox = itemView.findViewById(R.id.checkTask)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = tasks[position]
        val isChecked = checkedStates[position] ?: false

        holder.textTask.text = task

        // Цвет карточек в зависимости от чётности позиции
        if (position % 2 == 0) {
            // Чётные
            holder.cardView.setCardBackgroundColor(
                ContextCompat.getColor(holder.itemView.context, android.R.color.white)
            )
        } else {
            // Нечётные
            holder.cardView.setCardBackgroundColor(
                ContextCompat.getColor(holder.itemView.context, R.color.light_gray)
            )
        }

        // Зачеркивание задач
        if (isChecked) {
            holder.textTask.paintFlags = holder.textTask.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            holder.textTask.alpha = 0.6f
        } else {
            holder.textTask.paintFlags = holder.textTask.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            holder.textTask.alpha = 1.0f
        }

        holder.checkTask.setOnCheckedChangeListener(null)
        holder.checkTask.isChecked = isChecked

        // Обработка изменения состояния чекбокса
        holder.checkTask.setOnCheckedChangeListener { _, isCheckedNow ->
            checkedStates[position] = isCheckedNow
            onTaskCheckedChange(position, isCheckedNow)
            notifyItemChanged(position)
        }
    }

    override fun getItemCount(): Int = tasks.size

    // Удаление задачи
    fun deleteTask(position: Int): String {
        val deletedTask = tasks[position]
        tasks.removeAt(position)

        // Обновление состояния задачи
        val newStates = mutableMapOf<Int, Boolean>()
        checkedStates.forEach { (index, state) ->
            if (index < position) {
                newStates[index] = state
            } else if (index > position) {
                newStates[index - 1] = state
            }
        }
        checkedStates.clear()
        checkedStates.putAll(newStates)

        notifyItemRemoved(position)
        return deletedTask
    }

    // Восстановление удаленной задачи
    fun restoreTask(position: Int, task: String, wasChecked: Boolean) {
        tasks.add(position, task)

        val newStates = mutableMapOf<Int, Boolean>()
        checkedStates.forEach { (index, state) ->
            if (index < position) {
                newStates[index] = state
            } else {
                newStates[index + 1] = state
            }
        }
        checkedStates.clear()
        checkedStates.putAll(newStates)
        checkedStates[position] = wasChecked

        notifyItemInserted(position)
    }

    fun getTaskCheckedState(position: Int): Boolean = checkedStates[position] ?: false
}
```

---

## Итоговый листинг класса `MainActivity.kt` (включая доп. улучшения и индивидуальное задание №1)

```kotlin
package com.example.todoapp

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.ItemTouchHelper
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

    private var counter = 0
    private val tasks = mutableListOf<String>()
    private var enteredText = ""

    private lateinit var adapter: TaskAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var textTaskCount: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Блок 1: Счётчик
        val textCounter = findViewById<TextView>(R.id.textCounter)
        val buttonIncrement = findViewById<Button>(R.id.buttonIncrement)
        val buttonResetCounter = findViewById<Button>(R.id.buttonResetCounter)

        // Блок 2: Поле ввода
        val editTextInput = findViewById<EditText>(R.id.editTextInput)
        val buttonShow = findViewById<Button>(R.id.buttonShow)
        val textEntered = findViewById<TextView>(R.id.textEntered)

        // Блок 3: ToDo-список с RecyclerView
        val editTextTask = findViewById<EditText>(R.id.editTextTask)
        val buttonAddTask = findViewById<Button>(R.id.buttonAddTask)
        val buttonDeleteLast = findViewById<Button>(R.id.buttonDeleteLast)
        recyclerView = findViewById(R.id.recyclerViewTasks)
        textTaskCount = findViewById(R.id.textTaskCount)

        // Настройка RecyclerView
        setupRecyclerView()

        // Восстановление состояния
        savedInstanceState?.let {
            counter = it.getInt("counter", 0)
            enteredText = it.getString("enteredText", "")
            it.getStringArrayList("tasks")?.let { list ->
                tasks.clear()
                tasks.addAll(list)
            }
        }

        // "Вы ввели"
        updateCounterDisplay(textCounter)

        if (enteredText.isNotEmpty()) {
            textEntered.text = "${getString(R.string.label_entered)} $enteredText"
        }

        updateTasksDisplay()

        // "+1" и "Сбросить счётчик"
        buttonIncrement.setOnClickListener {
            counter++
            updateCounterDisplay(textCounter)
        }

        buttonResetCounter.setOnClickListener {
            counter = 0
            updateCounterDisplay(textCounter)
        }

        // "Показать"
        buttonShow.setOnClickListener {
            val inputText = editTextInput.text.toString()
            enteredText = inputText
            textEntered.text = "${getString(R.string.label_entered)} $inputText"
        }

        // Обработчик добавления задачи
        buttonAddTask.setOnClickListener {
            val task = editTextTask.text.toString().trim()
            if (task.isNotBlank()) {
                tasks.add(task)
                adapter.notifyItemInserted(tasks.size - 1)
                updateTaskCount()
                editTextTask.text.clear()
                recyclerView.scrollToPosition(tasks.size - 1)

                Toast.makeText(this, "Задача добавлена", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, R.string.toast_empty_task, Toast.LENGTH_SHORT).show()
            }
        }

        // Обработчик удаления последней задачи
        buttonDeleteLast.setOnClickListener {
            if (tasks.isNotEmpty()) {
                val lastIndex = tasks.size - 1
                val wasChecked = adapter.getTaskCheckedState(lastIndex)
                val deletedTask = adapter.deleteTask(lastIndex)
                updateTaskCount()

                Snackbar.make(recyclerView, "Задача \"$deletedTask\" удалена", Snackbar.LENGTH_LONG)
                    .setAction("Отмена") {
                        adapter.restoreTask(lastIndex, deletedTask, wasChecked)
                        updateTaskCount()
                    }
                    .show()
            } else {
                Toast.makeText(this, "Список задач пуст", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = TaskAdapter(
            tasks = tasks,
            onTaskCheckedChange = { position, isChecked ->
                // Обновление счётчика задач
                updateTaskCount()
            }
        )

        recyclerView.adapter = adapter

        // Удаление свайпом
        val swipeHandler = object : ItemTouchHelper.SimpleCallback(
            0,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val wasChecked = adapter.getTaskCheckedState(position)
                    val deletedTask = adapter.deleteTask(position)
                    updateTaskCount()

                    Snackbar.make(recyclerView, "Задача \"$deletedTask\" удалена", Snackbar.LENGTH_LONG)
                        .setAction("Отмена") {
                            adapter.restoreTask(position, deletedTask, wasChecked)
                            updateTaskCount()
                        }
                        .show()
                }
            }
        }

        ItemTouchHelper(swipeHandler).attachToRecyclerView(recyclerView)
    }

    private fun updateCounterDisplay(textView: TextView) {
        textView.text = getString(R.string.counter_text, counter)
    }

    private fun updateTasksDisplay() {
        updateTaskCount()
        adapter.notifyDataSetChanged()
    }

    // Обновление количества задач
    private fun updateTaskCount() {
        textTaskCount.text = getString(R.string.label_task_count, tasks.size)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("counter", counter)
        outState.putString("enteredText", enteredText)
        outState.putStringArrayList("tasks", ArrayList(tasks))
    }
}
```

---

## Скриншот работающего приложения с несколькими карточками

![Результат в эмуляторе](<Снимок экрана 2026-04-16 225755.png>)

---

## Ответы на контрольные вопросы

**1. Для чего нужен `RecyclerView`? Чем он лучше `ListView`?**

`RecyclerView` - это современный компонент для эффективного отображения больших списков данных с возможностью прокрутки.

**Ключевым отличием** от `ListView` является то, что `RecyclerView` принудительное переиспользует паттерн `ViewHolder`, что исключает частые вызовы `findViewById()` и значительно повышает производительность при прокрутке больших списков.

<br>

**2. Какие компоненты необходимы для работы `RecyclerView`?**

Для работы `RecyclerView` необходимы следующие компоненты:

* **LayoutManager** - определяет расположение элементов.
* **Adapter** - связывает данные с View.
* **ViewHolder** - кэширует ссылки на View.


Также существуют **дополнительные опциональные** компоненты:

* **ItemDecoration** - отступы, разделители.
* **ItemAnimator** - анимации при изменениях.
* **ItemTouchHelper** - свайпы, перетаскивание.

<br>

**3. Что такое `ViewHolder` и для чего он используется?**

`ViewHolder` - это внутренний класс в адаптере, который хранит ссылки на View компоненты элемента списка.

Использование:

* `findViewById()` вызывается только при создании элемента, а не при каждой прокрутке.
* Кэширует View для быстрого доступа.
* Повышает производительность при прокрутке.

<br>

**4. Чем отличается `notifyDataSetChanged()` от `notifyItemInserted()`?**

Разница между `notifyDataSetChanged()` и `notifyItemInserted()` заключается в следующем:

|notifyDataSetChanged()          |notifyItemInserted()              |
|--------------------------------|----------------------------------|
|Обновляет весь список           |Обновляет только один элемент     |
|Низкая эффективность            |Высокая эффективность             |
|Нет анимации                    |Есть анимация                     |

Если известно, какой элемент изменился, следует использовать точечные методы (`notifyItemInserted()`, `notifyItemRemoved()`, `notifyItemChanged()`, `notifyItemMoved()`).

При массовых изменениях (очистка всего спика), следует использовать `notifyDataSetChanged()`.

<br>

**5. Как добавить обработку кликов на элементы `RecyclerView`?**

Обработка кликов на элементы `RecyclerView` реализуется через интерфейсы в адаптере:

```kotlin
// Создание интерфейса в адаптере
class TaskAdapter(
    private val tasks: MutableList<TaskItem>,
    private val onItemClick: (Int) -> Unit // колбэк на клик
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    // Установка слушателя в onBindViewHolder
    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.itemView.setOnClickListener {
            onItemClick(position) // передача позиции кликнутого элемента
        }
    }
}

// Передача лямбды при создании адаптера в MainActivity
adapter = TaskAdapter(tasks) { position ->
    Toast.makeText(this, "Клик по задаче №$position", Toast.LENGTH_SHORT).show()
}
```

---

## Вывод

В ходе выполнения **лабораторной работы №6** изучил современный компонент `RecyclerView` для эффективного отображения списков данных, освоил создание адаптера и `ViewHolder`, а также применил `CardView` для оформления элементов списка в виде красивых карточек.

Выполняя задание, в файле `activity_main.xml` заменил статический `TextView` на `RecyclerView` для отображения списка задач, создал отдельный файл разметки `item_task.xml` с использованием `CardView`, `LinearLayout`, `TextView` и `CheckBox`. В файле `TaskAdapter.kt` реализовал адаптер с `ViewHolder` для эффективного переиспользования элементов, добавил обработку изменения состояния чекбоксов с визуальным перечёркиванием текста выполненных задач.

В файле `MainActivity.kt` настроил `RecyclerView` с `LinearLayoutManager`, связал адаптер с данными, реализовал динамическое обновление списка при добавлении новых задач через `notifyItemInserted()` и удалении через `notifyItemRemoved()`, что обеспечило плавную анимацию и высокую производительность.

Помимо этого, реализовал дополнительные улучшения: добавил чередование цветов карточек в зависимости от чётности позиции (белые и светло-серые) и добавил анимацию появления/удаления карточек.

Также выполнил индивидуальное задание №1 - реализовал удаление задач свайпом влево/вправо с помощью `ItemTouchHelper` и добавил `Snackbar` с возможностью отмены удаления и восстановления задачи с сохранением её состояния (отмечена/не отмечена).

Результат работы **успешно** протестирован на эмуляторе.