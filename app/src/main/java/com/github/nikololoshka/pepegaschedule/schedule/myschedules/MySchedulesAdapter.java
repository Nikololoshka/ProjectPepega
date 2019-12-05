package com.github.nikololoshka.pepegaschedule.schedule.myschedules;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.nikololoshka.pepegaschedule.R;
import com.github.nikololoshka.pepegaschedule.utils.FavoriteButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class MySchedulesAdapter
        extends RecyclerView.Adapter<MySchedulesAdapter.MySchedulesViewHolder>
        implements ItemTouchHelperAdapter{

    private static final String TAG = "MySchedulesAdapterLog";
    private static final boolean DEBUG = false;

    public interface OnItemClickListener {
        void onScheduleItemClicked(int pos);
        void onScheduleItemMove(int fromPosition, int toPosition);
        void onScheduleFavoriteSelected(String favorite);
    }

    /**
     * Callback для нажатия по расписанию
     */
    private final OnItemClickListener mClickListener;
    private final OnStartDragListener mDragStartListener;

    private List<String> mSchedules;
    private String mFavoriteSchedule;
    private boolean mIsAnimate;

    MySchedulesAdapter(OnItemClickListener clickListener, OnStartDragListener dragStartListener) {
        mClickListener = clickListener;
        mDragStartListener = dragStartListener;

        mSchedules = new ArrayList<>();
        mFavoriteSchedule = "";
    }

    public void update(List<String> schedules, String favoriteSchedule) {
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
        if (DEBUG) {
            Log.d(TAG, "onBindViewHolder: " + mSchedules.get(position)
                    + "; " + mFavoriteSchedule + " and "+ mSchedules.get(position)
                    + ";" + mFavoriteSchedule.equals(mSchedules.get(position)));
        }

        holder.setTitle(mSchedules.get(position));
        holder.setFavorite(mFavoriteSchedule.equals(mSchedules.get(position)));
        holder.setOnTouchListener(holder);
    }

    @Override
    public int getItemCount() {
        return mSchedules.size();
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        Collections.swap(mSchedules, fromPosition, toPosition);

        mClickListener.onScheduleItemMove(fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
    }

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
        }

        private void setTitle(String title) {
            mTitle.setText(title);
        }

        private void setFavorite(boolean check) {
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
                case R.id.schedule_item:
                    mClickListener.onScheduleItemClicked(getAdapterPosition());
                    break;
                case R.id.favorite_schedule:
                    mFavoriteSchedule = mTitle.getText().toString();
                    mClickListener.onScheduleFavoriteSelected(mFavoriteSchedule);

                    mIsAnimate = true;

                    notifyDataSetChanged();
                    break;
            }
        }
    }
}
