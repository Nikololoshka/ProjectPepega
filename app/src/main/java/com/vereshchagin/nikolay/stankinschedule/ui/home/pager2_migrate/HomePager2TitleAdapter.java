package com.vereshchagin.nikolay.stankinschedule.ui.home.pager2_migrate;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vereshchagin.nikolay.stankinschedule.R;

import java.util.ArrayList;

/**
 * Адаптер для pager'а с заголовками дня.
 */
public class HomePager2TitleAdapter
        extends RecyclerView.Adapter<HomePager2TitleAdapter.HomePagerTitleHolder> {

    /**
     * Массив с заголовками.
     */
    private ArrayList<String> mTitleData;

    HomePager2TitleAdapter() {
        super();

        mTitleData = new ArrayList<>();
    }

    /**
     * Обновляет данные в адапторе.
     * @param titleData массив с загаловками.
     */
    public void update(@NonNull ArrayList<String> titleData) {
        mTitleData = titleData;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public HomePagerTitleHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_pager_day_title, parent, false);
        return new HomePagerTitleHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HomePagerTitleHolder holder, int position) {
        holder.bind(mTitleData.get(position));
    }

    @Override
    public int getItemCount() {
        return mTitleData.size();
    }

    /**
     * Holder для адаптера.
     */
    class HomePagerTitleHolder extends RecyclerView.ViewHolder {

        /**
         * View заголовка.
         */
        private TextView mTitleTextView;

        HomePagerTitleHolder(@NonNull View itemView) {
            super(itemView);

            mTitleTextView = itemView.findViewById(R.id.pager_day_title);
        }

        /**
         * Присоединяет данные к holder'у.
         * @param title заголовок.
         */
        void bind(@NonNull String title) {
            mTitleTextView.setText(title);
        }
    }
}
