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
  Лабораторная работа №5<br>
  «Счетчик нажатий, поле ввода и отображение текста. Реализация ToDo-списка».<br>
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

Научиться обрабатывать пользовательский ввод, работать с состоянием (счетчик, список задач), динамически обновлять интерфейс приложения на Kotlin.

---

## Итоговый листинг файла `activity_main.xml`

```xml
<?xml version="1.0" encoding="utf-8"?>
<ScrollView
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:fillViewport="true">

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="vertical"
        android:padding="16dp">

        <!-- Блок 1: Счётчик -->
        <TextView
            android:id="@+id/textCounter"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="@string/counter_text"
            android:textSize="24sp"
            android:layout_marginBottom="8dp"/>

        <Button
            android:id="@+id/buttonIncrement"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="@string/button_increment"
            android:layout_marginBottom="4dp"/>

        <Button
            android:id="@+id/buttonResetCounter"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="@string/button_reset_counter"
            android:layout_marginBottom="24dp"/>

        <!-- Блок 2: Поле ввода и отображение текста -->
        <EditText
            android:id="@+id/editTextInput"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:hint="@string/hint_input"
            android:inputType="text"
            android:layout_marginBottom="8dp"
            android:importantForAutofill="no"/>

        <Button
            android:id="@+id/buttonShow"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="@string/button_show"
            android:layout_marginBottom="8dp"/>

        <TextView
            android:id="@+id/textEntered"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="@string/label_entered"
            android:textSize="18sp"
            android:layout_marginBottom="24dp"/>

        <!-- Блок 3: ToDO список -->
        <TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="@string/label_add_tasks"
            android:textStyle="bold"
            android:layout_marginBottom="4dp"/>

        <EditText
            android:id="@+id/editTextTask"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:hint="@string/hint_input"
            android:inputType="text"
            android:layout_marginBottom="8dp"
            android:importantForAutofill="no"/>

        <LinearLayout
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:orientation="horizontal"
            android:layout_marginBottom="8dp"
            android:gravity="start">

            <Button
                android:id="@+id/buttonAddTask"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:text="@string/button_add_task"
                android:layout_marginEnd="8dp"
                android:minWidth="120dp"/>

            <Button
                android:id="@+id/buttonDeleteLast"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:text="@string/button_delete_last"
                android:layout_marginStart="8dp"
                android:minWidth="120dp"/>
        </LinearLayout>

        <!-- Блок 4: Счётчик задач -->
        <TextView
            android:id="@+id/textTaskCount"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="@string/label_task_count"
            android:textSize="16sp"
            android:textStyle="italic"
            android:layout_marginBottom="8dp"/>

        <TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="@string/label_tasks"
            android:textStyle="bold"
            android:layout_marginBottom="4dp"/>

        <TextView
            android:id="@+id/textTasks"
            android:layout_width="match_parent"
            android:layout_height="200dp"
            android:text="@string/label_tasks"
            android:textSize="16sp"
            android:background="#F0F0F0"
            android:padding="8dp"
            android:scrollbars="vertical"/>
    </LinearLayout>
</ScrollView>
```

---

## Итоговый листинг файла `MainActivity.kt` (включая дополнительные и индивидуальное задание №2)

```kotlin
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
```

---

## Скриншот работающего приложения с демонстрацией всех функций

![Результат в эмуляторе](<Снимок экрана 2026-04-02 222540.png>)

---

## Ответы на контрольные вопросы

**1. Как получить текст из `EditText?`**

Чтобы получить текст из `EditText`, необходимо вызвать `.text.toString()` у данного объекта:

```kotlin
val text = editText.text.toString()
```

У `EditText` есть свойство `text` типа `Editable`, которое нужно преобразовать в `String` методом `toString()`.

<br>

**2. Почему при повороте экрана данные (счётчик, список задач) сбрасываются? Как это можно исправить?**

Данные сбрасываются при повороте экрана, так как в этот момент Android уничтожает и пересоздает активность. Поэтому все переменные в `onCreate()` инициализируются заново.

Чтобы это исправить, необходимо использовать метод `onSaveInstanceState()` для сохранения данных в `Bundle` перед уничтожением и `onRestoreInstanceState()` для восстановления:

```kotlin
override fun onSaveInstanceState(outState: Bundle) {
    super.onSaveInstanceState(outState)
    outState.putInt("counter", counter)
}

override fun onRestoreInstanceState(savedInstanceState: Bundle) {
    super.onRestoreInstanceState(savedInstanceState)
    counter = savedInstanceState.getInt("counter")
}
```

<br>

**3. Для чего используется `joinToString`? Как изменить разделитель?**

`joinToString` преобразует коллекцию в одну строку, соединяя элементы с заданным разделителем.

Для изменения разделителя необходимо поменять первый параметр метода (`separator`):

```kotlin
tasks.joinToString("\n") // каждый элемент с новой строки
tasks.joinToString(", ") // через запятую и пробел
tasks.joinToString(" | ") // через вертикальную черту
```

<br>

**4. В чём разница между `List` и `MutableList`?**

Разница между `List` и `MutableList` заключается в следующем:

|List                                                   |MutableList                                |
|-------------------------------------------------------|-------------------------------------------|
|Неизменяемый список                                    |Изменяемый список                          |
|Нельзя изменять размер или элементы (только для чтения)|Можно добавлять, удалять, изменять элементы|
|Методы: `get() `                                         |Методы: `add()`, `remove()`, `set()`     |

<br>

**5. Как очистить поле ввода после добавления задачи?**

Чтобы очистить поля ввода, необходимо вызвать метод `clear()`, который очищает текст в `EditText`:

```kotlin
editText.text.clear()
```

Также, в качестве альтернативного варианта, можно просто установить пустую строку:

```kotlin
editText.setText("")
```

---

## Вывод

В ходе выполнения лабораторной работы №5 изучил основы обработки пользовательского ввода в Android Studio, работы с состоянием приложения (счётчик, список задач), а также научился динамически обновлять интерфейс на Kotlin.

Выполняя задание, в файле `activity_main.xml` создал интерфейс приложения с использованием `LinearLayout`, `TextView`, `EditText` и `Button`. Разделил экран на три функциональных блока: счётчик нажатий, ввод и отображение текста, ToDo-список. В файле `MainActivity.kt` реализовал обработчики нажатия на кнопки с обновлением соответствующих элементов интерфейса.

Для интерфейса вынес все текстовые строки в `res/values/strings.xml`, настроил отступы и размеры элементов.

Помимо этого, реализовал дополнительные улучшения: добавил кнопку «Сбросить счётчик» для обнуления счётчика нажатий и кнопку «Удалить последнюю» для удаления последней задачи из списка, а также реализовал сохранение состояния с помощью `onSaveInstanceState` и `onRestoreInstanceState`, что предотвращает потерю данных при повороте экрана.

Также выполнил индивидуальное задание №2 — добавил `TextView` для отображения общего количества задач в списке, который обновляется при каждом добавлении или удалении.

Результат работы **успешно** протестирован на эмуляторе.