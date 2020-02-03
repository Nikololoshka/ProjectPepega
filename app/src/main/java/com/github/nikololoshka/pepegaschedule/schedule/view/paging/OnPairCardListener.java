package com.github.nikololoshka.pepegaschedule.schedule.view.paging;

import androidx.annotation.Nullable;

import com.github.nikololoshka.pepegaschedule.schedule.model.pair.Pair;

/**
 * Интерфейс callback'а для обработки нажатия на пару.
 */
public interface OnPairCardListener {
    /**
     * Вызывается, если была нажата пара.
     * @param pair нажатая пара.
     */
    void onPairClicked(@Nullable Pair pair);
}
