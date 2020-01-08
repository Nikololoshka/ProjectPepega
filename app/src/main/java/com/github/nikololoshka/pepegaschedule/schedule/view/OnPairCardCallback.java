package com.github.nikololoshka.pepegaschedule.schedule.view;

import com.github.nikololoshka.pepegaschedule.schedule.pair.Pair;

/**
 * Интерфейс callback'а для обработки нажатия на пару.
 */
public interface OnPairCardCallback {
    void onPairCardClicked(Pair pair);
}