package com.vereshchagin.nikolay.stankinschedule.schedule.myschedules.list;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vereshchagin.nikolay.stankinschedule.R;
import com.vereshchagin.nikolay.stankinschedule.utils.FavoriteButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Адаптер для списка с расписаниями.
 */
public class MySchedulesAdapter extends RecyclerView.Adapter<MySchedulesAdapter.MySchedulesViewHolder> {

    private static final String TAG = "MySchedulesAdapterLog";

    /**
     * Listener для нажатия по расписанию
     */
    public interface OnItemClickListener {
        /**
         * Вызывается если расписание было нажато.
         * @param schedule название расписания.
         */
        void onScheduleItemClicked(@NonNull String schedule);

        /**
         * Избранное расписание изменено.
         * @param favorite избранное расписание.
         */
        void onScheduleFavoriteSelected(@NonNull String favorite);
    }

    private final OnItemClickListener mClickListener;
    private final DragToMoveCallback.OnStartDragListener mDragStartListener;

    private List<String> mSchedules;
    private String mFavoriteSchedule;

    private boolean mIsAnimate;

    public MySchedulesAdapter(@NonNull OnItemClickListener clickListener, @NonNull DragToMoveCallback.OnStartDragListener dragStartListener) {
        mClickListener = clickListener;
        mDragStartListener = dragStartListener;

        mSchedules = new ArrayList<>();
        mFavoriteSchedule = "";
    }

    /**
     * Обновляе данные в адапторе.
     * @param schedules расписания.
     * @param favoriteSchedule избранное расписание.
     */
    public void submitList(@NonNull List<String> schedules, @NonNull String favoriteSchedule) {
        mSchedules = schedules;
        mFavoriteSchedule = favoriteSchedule;

        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MySchedulesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_schedule, parent, false);
        return new MySchedulesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MySchedulesViewHolder holder, int position) {
//        if (BuildConfig.DEBUG) {
//            Log.d(TAG, "onBindViewHolder: " + mSchedules.get(position));
//            Log.d(TAG, "onBindViewHolder: " + mFavoriteSchedule);
//            Log.d(TAG, "onBindViewHolder: " + mSchedules.get(position));
//        }

        holder.bind(mSchedules.get(position), mFavoriteSchedule.equals(mSchedules.get(position)));
    }

    @Override
    public int getItemCount() {
        return mSchedules.size();
    }

    /**
     * Перемещает элемент в списке.
     * @param fromPosition начальная позиция.
     * @param toPosition конечная позиция.
     */
    public void moveItem(int fromPosition, int toPosition) {
        Collections.swap(mSchedules, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
    }

    /**
     * Holder элемента расписания в списке.
     */
    class MySchedulesViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        private final TextView mTitle;
        private final FavoriteButton mFavorite;
        private final ImageView mMovingHandler;

        private MySchedulesViewHolder(@NonNull final View itemView) {
            super(itemView);

            mTitle = itemView.findViewById(R.id.schedule_info);
            mFavorite = itemView.findViewById(R.id.favorite_schedule);
            mMovingHandler = itemView.findViewById(R.id.moving_handle);

            itemView.setOnClickListener(this);
            itemView.findViewById(R.id.favorite_schedule).setOnClickListener(this);

            setOnTouchListener(this);
            // itemView.setOnLongClickListener(this);
        }

        /**
         * Обновляет данные в holder.
         * @param title название расписания.
         * @param check избранное ли расписание.
         */
        void bind(@NonNull String title, boolean check) {
            mTitle.setText(title);

            boolean animate = check && mIsAnimate;
            mFavorite.setToggle(check, animate);

            if (animate) {
                mIsAnimate = false;
            }
        }

        @SuppressLint("ClickableViewAccessibility")
        private void setOnTouchListener(@NonNull final MySchedulesViewHolder holder) {
            mMovingHandler.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        mDragStartListener.onStartDrag(holder);
                    }
                    return false;
                }
            });
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                // нажато расписание
                case R.id.schedule_item: {
                    mClickListener.onScheduleItemClicked(mSchedules.get(getAdapterPosition()));
                    break;
                }
                // нажато на кнопку "избранное" расписание
                case R.id.favorite_schedule: {
                    mFavoriteSchedule = mTitle.getText().toString();
                    mClickListener.onScheduleFavoriteSelected(mFavoriteSchedule);

                    mIsAnimate = true;

                    notifyDataSetChanged();
                    break;
                }
            }
        }
    }
}
