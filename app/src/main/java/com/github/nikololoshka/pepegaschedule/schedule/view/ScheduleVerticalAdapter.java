package com.github.nikololoshka.pepegaschedule.schedule.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.nikololoshka.pepegaschedule.R;
import com.github.nikololoshka.pepegaschedule.schedule.pair.Pair;
import com.github.nikololoshka.pepegaschedule.utils.StickHeaderItemDecoration;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.TreeSet;

/**
 * Adapter для RecyclerView с вертикальным отображением расписания расписанием.
 */
public class ScheduleVerticalAdapter
        extends ScheduleViewAdapter<RecyclerView.ViewHolder>
        implements StickHeaderItemDecoration.StickyHeaderInterface {

    private static final int TITLE_TYPE = 0;
    private static final int PAIRS_TYPE = 1;

    private ArrayList<TreeSet<Pair>> mDaysPairs;
    private ArrayList<String> mDaysTitles;
    private WeakReference<Context> mContext;
    private OnPairCardCallback mListener;

    ScheduleVerticalAdapter(OnPairCardCallback listener) {
        mDaysPairs = new ArrayList<>();
        mDaysTitles = new ArrayList<>();

        mListener = listener;
    }

    @Override
    public void update(ArrayList<TreeSet<Pair>> daysPair, ArrayList<String> daysTitles) {
        mDaysPairs = daysPair;
        mDaysTitles = daysTitles;

        notifyDataSetChanged();
    }

    @Override
    public int translateIndex(int position) {
        return position * 2 + 1;
    }

    @Override
    public int unTranslateIndex(int position) {
        if (position % 2 == 0) {
            return position / 2;
        }
        return (position - 1) / 2;
    }

    @Override
    public boolean scrolledNext(int firstPosition, int lastPosition, int todayPosition) {
        int today = translateIndex(todayPosition);

        int firstOffset = firstPosition - today;
        int lastOffset = lastPosition - today;

        return firstOffset > 0 && lastOffset > 0;
    }

    @Override
    public boolean scrolledPrev(int firstPosition, int lastPosition, int todayPosition) {
        int today = translateIndex(todayPosition);

        int firstOffset = firstPosition - today;
        int lastOffset = lastPosition - today;

        return firstOffset < -1 && lastOffset < -1;
    }

    @Override
    public void scrollTo(RecyclerView attachedRecyclerView, int position, boolean smooth) {
        LinearLayoutManager manager = (LinearLayoutManager) attachedRecyclerView.getLayoutManager();

        if (manager == null) {
            return;
        }

        int firstPos = manager.findFirstVisibleItemPosition();
        int lastPos = manager.findLastVisibleItemPosition();

        if (scrolledNext(firstPos, lastPos, position)) {
            if (smooth) {
                attachedRecyclerView.smoothScrollToPosition(position * 2);
            } else {
                attachedRecyclerView.scrollToPosition(position * 2);
            }
        } else if (scrolledPrev(firstPos, lastPos, position)) {
            if (smooth) {
                attachedRecyclerView.smoothScrollToPosition(position * 2 + 1);
            } else {
                attachedRecyclerView.scrollToPosition(position * 2 + 1);
            }
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = new WeakReference<>(parent.getContext());
        }

        // создаем
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        if (viewType == TITLE_TYPE) {
            return new ScheduleTitleHolder(inflater.inflate(R.layout.item_schedule_day_title,
                    parent, false));
        }

        // PAIRS_TYPE:
        return new SchedulePairsHolder(inflater.inflate(R.layout.item_schedule_day_pairs,
                parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof SchedulePairsHolder) {
            ((SchedulePairsHolder) holder).bind(mDaysPairs.get((position - 1) / 2));
        } else if (holder instanceof ScheduleTitleHolder) {
            ((ScheduleTitleHolder) holder).bind(mDaysTitles.get(position / 2));
        }
    }

    @Override
    public int getItemCount() {
        return mDaysPairs.size() + mDaysTitles.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position % 2 == 0 ? TITLE_TYPE : PAIRS_TYPE;
    }

    @Override
    public int getHeaderPositionForItem(int itemPosition) {
        int headerPosition = 0;

        do {
            if (this.isHeader(itemPosition)) {
                headerPosition = itemPosition;
                break;
            }
            // заголовок следует до пар
            itemPosition -= 1;
        } while (itemPosition >= 0);

        return headerPosition;
    }

    @Override
    public int getHeaderLayout(int headerPosition) {
        return R.layout.item_schedule_day_title;
    }

    @Override
    public void bindHeaderData(View header, int headerPosition) {
        if (header instanceof LinearLayout) {
            TextView view = ((LinearLayout) header).findViewById(R.id.schedule_day_title);
            view.setText(mDaysTitles.get(headerPosition / 2));
        }
    }

    @Override
    public boolean isHeader(int itemPosition) {
        return itemPosition % 2 == 0;
    }

    /**
     * Заголовок дня с парами раписания.
     */
    class ScheduleTitleHolder extends RecyclerView.ViewHolder {

        TextView mTitle;

        ScheduleTitleHolder(@NonNull View itemView) {
            super(itemView);
            mTitle = itemView.findViewById(R.id.schedule_day_title);
        }

        void bind(String title) {
            mTitle.setText(title);
        }
    }

    /**
     * День с парами в расписании.
     */
    class SchedulePairsHolder extends RecyclerView.ViewHolder {

        private ArrayList<PairCardView>  mPairCardViews;
        private LinearLayout mPairsLayout;
        private View mEmptyDay;

        SchedulePairsHolder(@NonNull View itemView) {
            super(itemView);

            mPairCardViews = new ArrayList<>();
            mPairsLayout = itemView.findViewById(R.id.schedule_day_pairs);
            mEmptyDay = itemView.findViewById(R.id.no_pairs);
        }

        /**
         * Соединяем данные с view.
         * @param pairs - пары.
         */
        void bind(TreeSet<Pair> pairs) {
            mPairsLayout.removeAllViews();

            int i = 0;
            for (Pair pair : pairs) {
                PairCardView cardView;

                // до создаем view пары, если не хватает
                if (i < mPairCardViews.size()) {
                    cardView = mPairCardViews.get(i);
                    cardView.updatePair(pair);
                } else {
                    cardView = new PairCardView(mContext.get(), pair);
                    cardView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mListener.onPairCardClicked(((PairCardView) v).pair());
                        }
                    });
                    mPairCardViews.add(cardView);
                }

                mPairsLayout.addView(cardView);
                i++;
            }

            // если нет пар
            if (pairs.isEmpty()) {
                mEmptyDay.setVisibility(View.VISIBLE);
            } else {
                mEmptyDay.setVisibility(View.GONE);
            }
        }
    }
}
