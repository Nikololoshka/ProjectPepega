package com.github.nikololoshka.pepegaschedule.schedule.view;

import androidx.recyclerview.widget.RecyclerView;

import com.github.nikololoshka.pepegaschedule.schedule.model.pair.Pair;

import java.util.ArrayList;
import java.util.TreeSet;

/**
 * Adapter для RecyclerView с расписанием
 */
abstract public class ScheduleViewAdapter<T extends RecyclerView.ViewHolder>
        extends RecyclerView.Adapter<T> {
    /**
     * Обновляет содержимое adapter.
     * @param daysPair пары расписания.
     * @param daysTitles подписи к дням с парами.
     */
    abstract public void update(ArrayList<TreeSet<Pair>> daysPair, ArrayList<String> daysTitles);

    /**
     * Переделывает индекс, отображаемой на самом деле элемента, исходя из собственной реализации.
     * @param position текущая позиция элемента, который отображается.
     * @return позиция отображаемого элемента в RecyclerView.
     */
    abstract public int translateIndex(int position);

    /**
     * Работает противоположо translateIndex.
     */
    abstract public int unTranslateIndex(int position);

    /**
     * Проверяет, было ли прокрученно расписание к последующим дням.
     * @param firstPosition первая показываема позиция.
     * @param lastPosition последняя показыаемая позиция.
     * @param todayPosition позиция текущего дня.
     * @return True если прокрученно, иначе False
     */
    abstract public boolean scrolledNext(int firstPosition, int lastPosition, int todayPosition);

    /**
     * Проверяет, было ли прокрученно расписание к предыдущим дням.
     * @param firstPosition первая показываема позиция.
     * @param lastPosition последняя показыаемая позиция.
     * @param todayPosition позиция текущего дня.
     * @return True если прокрученно, иначе False
     */
    abstract public boolean scrolledPrev(int firstPosition, int lastPosition, int todayPosition);

    /**
     * Пролистовывает RecyclerView до необхоимой позиции.
     * @param attachedRecyclerView присоединеный RecyclerView.
     * @param position позиция.
     */
    abstract public void scrollTo(RecyclerView attachedRecyclerView, int position, boolean smooth);
}
