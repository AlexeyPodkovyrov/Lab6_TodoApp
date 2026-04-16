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